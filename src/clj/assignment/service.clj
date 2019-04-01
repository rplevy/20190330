(ns assignment.service
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [cheshire.core :as json]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [hiccup.core :as hiccup]
            [ring.middleware.defaults :as ring-defaults]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer (get-sch-adapter)]))

(let [{:keys [ch-recv send-fn connected-uids
              ajax-post-fn ajax-get-or-ws-handshake-fn]}
      (sente/make-channel-socket! (get-sch-adapter) {})]
  (def ring-ajax-post ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk ch-recv)
  (def chsk-send! send-fn)
  (def connected-uids connected-uids))

(defn event-msg-handler [event-message]
  (log/infof "server received websocket event %s" event-message))

(defonce router
  (sente/start-server-chsk-router! ch-chsk event-msg-handler))

(defroutes site-routes
  (GET "/" request
       (hiccup/html [:html
                     [:head
                      [:meta {:charset "utf-8"}]
                      [:meta {:content "width=device-width, initial-scale=1"
                              :name "viewport"}]]
                     [:body
                      [:div {:id "app"}]
                      [:div
                       {:id "sente-csrf-token"
                        :data-csrf-token (:anti-forgery-token request)}]
                      [:script {:src "js/compiled/app.js"
                                :type "text/javascript"}]]]))
  ;; websocket routes
  (GET  "/chsk" request
        (ring-ajax-get-or-ws-handshake request))
  (POST "/chsk" request
        (ring-ajax-post request)))

(defroutes api-routes
  (POST "/sensor-event" request
        (let [sensor-event (json/parse-string (slurp (:body request)))]
          (log/infof "received sensor event %s" sensor-event)
          (chsk-send! :sente/all-users-without-uid
                      [:service/sensor-event sensor-event]))
        {:status 201}))

(def app
  (routes
   api-routes
   (ring-defaults/wrap-defaults site-routes ring-defaults/site-defaults)))

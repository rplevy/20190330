(ns assignment.service
  (:require [assignment.service.view :as view]
            [cljs.core.async :as async :refer (<! >! put! chan)]
            [reagent.core :as r]
            [taoensso.sente  :as sente]))

(let [csrf-token (when-let [el (.getElementById js/document "sente-csrf-token")]
                   (.getAttribute el "data-csrf-token"))
      {:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk"
                                  csrf-token
                                  {:type :auto})]
  (def chsk chsk)
  (def ch-chsk ch-recv)
  (def chsk-send! send-fn)
  (def chsk-state state))

(defmulti event-msg-handler :id)

(defmethod event-msg-handler :chsk/recv
  [{[event-type sensor-event] :?data}]
  (when (= event-type :service/sensor-event)
    (swap! view/db view/update-view sensor-event)))

(defmethod event-msg-handler :chsk/handshake
  [{:as ev-msg :keys [?data]}]
  (let [[?uid ?csrf-token ?handshake-data] ?data]
    (println "Handshake: %s" ?data)))

(defn mount-root []
  (r/render [view/home] (.getElementById js/document "app")))

(sente/start-client-chsk-router! ch-chsk event-msg-handler)

(mount-root)

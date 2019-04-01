(ns assignment.service
  (:require [cljs.core.async :as async :refer (<! >! put! chan)]
            [reagent.core :as r]
            [taoensso.sente  :as sente]))

(def db (r/atom {:events []}))

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

(defn simple-component []
  [:div
   [:div "sensor events"]
   (for [ev (:events @db)]
     [:p "id: " (get ev "id") " type: " (get ev "type")])])

(defmulti event-msg-handler :id)

(defmethod event-msg-handler :chsk/recv
  [{[event-type sensor-event] :?data}]
  (when (= event-type :service/sensor-event)
    (swap! db update :events conj sensor-event)))

(defmethod event-msg-handler :chsk/handshake
  [{:as ev-msg :keys [?data]}]
  (let [[?uid ?csrf-token ?handshake-data] ?data]
    (println "Handshake: %s" ?data)))

(defn mount-root []
  (r/render [simple-component] (.getElementById js/document "app")))

(sente/start-client-chsk-router! ch-chsk event-msg-handler)

(mount-root)

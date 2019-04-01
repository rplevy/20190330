(ns assignment.service.view
  (:require [clojure.set :as set]
            [cljs-time.coerce :as tc]
            [cljs-time.format :as tf]
            [reagent.core :as r]))

(def db (r/atom {:events []}))

(defn door-to-bathroom [sensor-event]
  (when (= "door" (:type sensor-event))
    (first (set/intersection #{"bathroom-1" "bathroom-2"}
                             (set (:location sensor-event))))))

(defn less-than-a-minute-ago? [millis]
  (when millis
    (let [ms-minutes-conv 60000
          current-time (. js/Date now)]
      (< (/ (- current-time millis)
            ms-minutes-conv)
         1))))

(defn update-bathroom-data [bathroom-status sensor-event]
  (let [door-status (if (= "door" (:type sensor-event))
                      (:status sensor-event)
                      (:door-status bathroom-status))
        last-light (if (= "light" (:type sensor-event))
                     (:last-emitted sensor-event)
                     (:last-light bathroom-status))
        last-motion (if (= "motion" (:type sensor-event))
                      (:last-emitted sensor-event)
                      (:last-motion bathroom-status))]
    {:door-status door-status
     :last-light last-light
     :last-motion last-motion
     :occupied? (and (= (:door-status "closed")
                        (or (less-than-a-minute-ago? last-light)
                            (less-than-a-minute-ago? last-motion))))}))

(defn update-bathrooms-status [bathrooms-status sensor-event]
  (let [location (or (door-to-bathroom sensor-event)
                     (:location sensor-event))]
    (merge bathrooms-status
           (when (#{"bathroom-1" "bathroom-2"} location)
             {location (update-bathroom-data bathrooms-status sensor-event)}))))

(defn update-rooms-status [rooms-status sensor-event]
  (merge rooms-status
         (when (and (#{"media-room"
                       "living-room"} (:location sensor-event))
                    (or (= "light" (:type sensor-event))
                        (= "motion" (:type sensor-event))))
           {(:location sensor-event) {:last-active
                                      (:last-emitted sensor-event)}})))

(defn update-doors-status [doors-status sensor-event]
  (merge doors-status
         (when (#{"front-door" "back-door"} (:id sensor-event))
           {(:id sensor-event) {:status (:status sensor-event)}})))

(defn update-view [db sensor-event]
  (let [sensor-event' (reduce-kv (fn [r k v] (assoc r (keyword k) v))
                                 {}
                                 sensor-event)]
    (assoc db
           :events (conj (:events db) sensor-event')
           :rooms-status (update-rooms-status (:rooms-status db)
                                              sensor-event')
           :bathrooms-status (update-bathrooms-status (:bathrooms-status db)
                                                      sensor-event')
           :doors-status (update-doors-status (:doors-status db)
                                              sensor-event'))))

(defn print-value [v]
  (or v "no data yet"))

(defn print-bathroom [occupied?]
  (if occupied? "occupied" "vacant"))

(defn home []
  (let [{events               :events
         {:strs [living-room
                 media-room]} :rooms-status
         {:strs [bathroom-1
                 bathroom-2]} :bathrooms-status
         {:strs [back-door
                 front-door]} :doors-status} @db
        lr-last-active (when (:last-active living-room)
                         (tf/unparse (tf/formatters :hour-minute-second)
                                     (tc/from-long (:last-active living-room))))
        mr-last-active (when (:last-active media-room)
                         (tf/unparse (tf/formatters :hour-minute-second)
                                     (tc/from-long (:last-active media-room))))]
    [:div
     [:div
      [:h1 "recent activity"]
      [:div "living room last active at " (print-value lr-last-active)]
      [:div "media room last active at " (print-value mr-last-active)]]

     [:div
      [:h1 "bathroom availability"]
      [:div "bathroom 1: " (print-bathroom (:occupied? bathroom-1))]
      [:div "bathroom 2: " (print-bathroom (:occupied? bathroom-2))]]

     [:div
      [:h1 "doors status"]
      [:div "front door: " (print-value (:status front-door))]
      [:div "back door: " (print-value (:status back-door))]]

     #_[:div
      [:h1 "all events"]
      (for [ev events]
        [:p "location: " (:location ev) " id: " (:id ev) " type: " (:type ev)])]]))

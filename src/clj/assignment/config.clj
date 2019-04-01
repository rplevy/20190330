(ns assignment.config)

(def sensor-defaults {:interval [3 10]
                      :emit-via :http})

(defn create-sensor [id type location & [options]]
  (merge sensor-defaults
         {:id id
          :type type
          :location location}
         options))

(def env
  {:channels {:http {:host "127.0.0.1"
                     :port "3000"
                     :endpoint "/sensor-event"}}
   :sensors [(create-sensor :front-door :door   [:outside :living-room])
             (create-sensor :lr-light   :light  :living-room)
             (create-sensor :lr-motion  :motion :living-room)
             (create-sensor :lr-mr-door :door   [:media-room :living-room])
             (create-sensor :mr-motion  :motion :media-room)
             (create-sensor :mr-light   :light  :media-room)
             (create-sensor :back-door  :door   [:outside :media-room])
             (create-sensor :br1-door   :door   [:media-room :bathroom-1]
                            {:interval [90 100]})
             (create-sensor :br1-motion :motion :bathroom-1)
             (create-sensor :br1-light  :light  :bathroom-1)
             (create-sensor :br2-door   :door   [:living-room :bathroom-2]
                            {:interval [60 80]})
             (create-sensor :br2-motion :motion :bathroom-2)
             (create-sensor :br2-light  :light  :bathroom-2
                            {:emit-via  :log})]})

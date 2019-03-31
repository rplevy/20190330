(ns assignment.simulator
  (:require [assignment.config :refer [env]]
            [assignment.simulator.emit :as emit]))

(defn ready-to-emit?
  "a sensor *must* emit no later than the term of interval,
   but *may* emit if the time since last emit is within its
   interval."
  [sensor last-emitted]
  (let [[start end] (:interval sensor)
        time-since-emit (- (System/currentTimeMillis)
                           (or (get-in last-emitted [(:id sensor) :last-emitted])
                               0))]
    (or (>= time-since-emit (* 1000 end))
        (and (>= time-since-emit (* 1000 start))
             (> (rand) 0.5)))))

(defn gather-events-to-emit
  "Accepts a map of {sensor-id sensor,...} with :last-emitted key on sensor.
   An empty/nil map is valid.
   Produces a map of new events ready to emit."
  [last-emitted]
  (reduce (fn [events sensor]
            (merge events
                   (when (ready-to-emit? sensor last-emitted)
                     {(:id sensor)
                      (assoc sensor
                        :last-emitted (System/currentTimeMillis))})))
          {}
          (env :sensors)))

(defn -main [& _]
  (loop [last-emitted nil]
    (let [to-emit (gather-events-to-emit last-emitted)]
      (Thread/sleep 1000)
      (doseq [sensor (vals to-emit)]
        (emit/emit-event! sensor))
      (recur (merge last-emitted to-emit)))))

(ns assignment.simulator.emit
  (:require [clojure.tools.logging :as log]
            [clj-http.client :as http]
            [cheshire.core :as json]
            [assignment.config :refer [env]]))

(defmulti emit-event! :emit-via)

(defn log [id loc via]
  (log/infof "%s is emitting in %s via %s" id loc via))

(defmethod emit-event! :http [{:keys [id location] :as sensor}]
  (log id location :http)
  (let [{:keys [host port endpoint]} (get-in env [:channels :http])]
    (http/post (format "http://%s:%s%s" host port endpoint)
               {:headers {"Content-Type" "application/json"}
                :accept :json
                :body (json/generate-string sensor)})))

(defmethod emit-event! :log [{:keys [id location] :as sensor}]
  (log id location :log))

(defmethod emit-event! :queue [{:keys [id location] :as sensor}]
  (log id location :queue))

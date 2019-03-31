(ns assignment.routes
  (:require [cheshire.core :as json]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [taoensso.sente :as sente]))

(defroutes app
  (POST "/sensor-event" request
        (let [sensor-event (json/parse-string (slurp (:body request)))]
          )))

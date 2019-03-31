(ns assignment.routes
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [cheshire.core :as json]
            [compojure.core :refer :all]
            [compojure.route :as route]
            #_[taoensso.sente :as sente]))

(defroutes routes
  (POST "/sensor-event" request
        (let [sensor-event (json/parse-string (slurp (:body request)))]
          (log/infof "received sensor event %s" sensor-event)))
  (route/not-found "Route not found"))

(def app
  (wrap-resource routes "public"))

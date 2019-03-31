(ns assignment.service
  (:require [reagent.core :as r]))

(defn simple-component []
  [:div
   [:p "placeholder"]])

(defn mount-root []
  (r/render [simple-component] (.getElementById js/document "app")))

(mount-root)

(ns todo-mvc.filter-bar
  (:require [re-frame.core :as rf]
            [reagent.core :as r]))

(def ReactNative (js/require "react-native"))

(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))

(defn- active-counter []
  (let [count @(rf/subscribe [:get-active-count])]
    [text (str count " item" (if (= count 1) "" "s") " left")]))

(defn filter-buttons [])

(defn filter-bar []
  [view
   {:style {:margin 10}}
   [active-counter]])


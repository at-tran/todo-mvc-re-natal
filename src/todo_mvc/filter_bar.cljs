(ns todo-mvc.filter-bar
  (:require [re-frame.core :as rf]
            [reagent.core :as r]))

(def ReactNative (js/require "react-native"))

(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def touchable (r/adapt-react-class (.-TouchableNativeFeedback ReactNative)))

(def common-style {:borderWidth  1
                   :borderColor  "transparent"
                   :padding      5
                   :borderRadius 5})

(defn- active-counter []
  (let [count @(rf/subscribe [:get-active-count])]
    [text
     {:style common-style}
     (str count " item" (if (= count 1) "" "s") " left")]))

(defn text-button [{:keys [title style on-press border?]}]
  [touchable
   {:on-press on-press}
   [view
    [text
     {:style [{:textAlign "center"}
              common-style
              (when border? {:borderColor "pink"})
              style]}
     title]]])

(defn filter-buttons []
  (let [showing @(rf/subscribe [:get-showing])]
    [view
     {:style {:justify-content "space-around"
              :flex            1
              :flex-direction  "row"}}
     [text-button {:title    "All"
                   :border?  (= :all showing)
                   :on-press #(rf/dispatch [:set-showing :all])}]
     [text-button {:title    "Active"
                   :border?  (= :active showing)
                   :on-press #(rf/dispatch [:set-showing :active])}]
     [text-button {:title    "Completed"
                   :border?  (= :completed showing)
                   :on-press #(rf/dispatch [:set-showing :completed])}]]))

(defn filter-bar []
  [view
   {:style {:margin         10
            :flex-direction "row"
            :align-items    "center"}}
   [active-counter]
   [filter-buttons]
   [text-button {:title    "Clear completed"
                 :style    {:flex 0}
                 :on-press #(rf/dispatch [:clear-completed])}]])


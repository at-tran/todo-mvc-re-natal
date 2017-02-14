(ns todo-mvc.list-todo
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [todo-mvc.editable-text :refer [editable-text]]))

(def ReactNative (js/require "react-native"))
(def FontAwesome (js/require "react-native-vector-icons/FontAwesome"))
(def MaterialIcons (js/require "react-native-vector-icons/MaterialIcons"))

(def text (r/adapt-react-class (.-Text ReactNative)))
(def text-input (r/adapt-react-class (.-TextInput ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def fontawesome-icon (r/adapt-react-class (.-default FontAwesome)))
(def material-icon (r/adapt-react-class (.-default MaterialIcons)))


(defn item-todo [[id {:keys [desc done?]}]]
  [view {:style      {:flex-direction "row"
                      :flex           1
                      :align-items    "center"
                      :padding        10}
         :key        id
         :margin-top 10}
   [material-icon {:name     "close"
                   :size     25
                   :on-press #(rf/dispatch [:remove-todo id])
                   :color    "red"
                   :style    {:opacity      0.5
                              :margin-right 5}}]
   [editable-text id desc done?]
   [fontawesome-icon {:name     (if done? "check-circle" "circle-o")
                      :size     40
                      :on-press #(rf/dispatch [:toggle-todo id])
                      :style    {:margin-left 5}}]])

(defn list-todo []
  [view {:style {:flex 1 :align-self "stretch"}}
   (map item-todo @(rf/subscribe [:get-todos]))])

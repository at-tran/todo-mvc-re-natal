(ns todo-mvc.list-todo
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))

(def ReactNative (js/require "react-native"))
(def FontAwesome (js/require "react-native-vector-icons/FontAwesome"))

(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def icon (r/adapt-react-class (.-default FontAwesome)))

(defn item-todo [[id {:keys [done? desc]}]]
  [view {:key   id
         :style {:flex-direction "row"
                 :flex           1
                 :align-items    "center"
                 :margin         10}}
   [text
    {:style [{:flex     1
              :fontSize 16}
             (if done? {:textDecorationLine "line-through"})]}
    desc]
   [icon {:name     (if done? "check-circle" "circle-o")
          :size     30
          :on-press #(rf/dispatch [:toggle-todo id])}]])

(defn list-todo []
  (let [todos (rf/subscribe [:get-todos])]
    (fn []
      [view {:style {:flex 1 :align-self "stretch"}}
       (map item-todo @todos)])))

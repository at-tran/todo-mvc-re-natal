(ns todo-mvc.list-todo
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))

(def ReactNative (js/require "react-native"))
(def FontAwesome (js/require "react-native-vector-icons/FontAwesome"))
(def MaterialIcons (js/require "react-native-vector-icons/MaterialIcons"))

(def text (r/adapt-react-class (.-Text ReactNative)))
(def text-input (r/adapt-react-class (.-TextInput ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def fontawesome-icon (r/adapt-react-class (.-default FontAwesome)))
(def material-icon (r/adapt-react-class (.-default MaterialIcons)))


(defn item-todo [[id {:keys [done? desc]}]]
  [view {:style      {:flex-direction "row"
                      :flex           1
                      :align-items    "center"
                      :padding        10}
         :key        id
         :margin-top 10}
   [material-icon {:name     "close"
                   :size     30
                   :on-press #(rf/dispatch [:remove-todo id])
                   :color    "red"
                   :style    {:opacity      0.7
                              :margin-right 5}}]
   [text
    {:style [{:flex     1
              :fontSize 18
              :color    "black"}
             (if done? {:textDecorationLine "line-through"
                        :color              "grey"
                        :opacity            0.7})]
     :multiline true}
    desc]

   [fontawesome-icon {:name     (if done? "check-circle" "circle-o")
                      :size     40
                      :on-press #(rf/dispatch [:toggle-todo id])
                      :style    {:margin-left 5}}]])

(defn list-todo []
  (let [todos (rf/subscribe [:get-todos])]
    (fn []
      [view {:style {:flex 1 :align-self "stretch"}}
       (map item-todo @todos)])))

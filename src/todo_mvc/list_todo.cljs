(ns todo-mvc.list-todo
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))

(def ReactNative (js/require "react-native"))

(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
;(def circle-checkbox (js/require "react-native-circle-checkbox"))

(defn item-todo [todo]
  [view {:key (random-uuid)}
   [text {:style [{:height 58} nil]}

    (:desc todo)]])

(defn list-todo []
  (let [todos (rf/subscribe [:get-todos])]
    (fn []
      [view {:style {:flex 1 :align-self "stretch"}}
       (map item-todo @todos)])))

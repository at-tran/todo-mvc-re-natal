(ns todo-mvc.root
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [todo-mvc.input-todo :refer [input-todo]]
            [todo-mvc.list-todo :refer [list-todo]]))

(def ReactNative (js/require "react-native"))

(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def scroll-view (r/adapt-react-class (.-ScrollView ReactNative)))

(defn root []
  [view {:style {:flex 1}}
   [scroll-view {:style                        {:margin-left  15
                                                :margin-right 15
                                                :flex         1}
                 :content-container-style      {:align-items "center"}
                 :showsVerticalScrollIndicator false
                 :keyboardShouldPersistTaps    "handled"}
    [text {:style {:font-size     70
                   :font-weight   "100"
                   :margin-bottom 10
                   :text-align    "center"
                   :color         "pink"}}
     "todos"]
    [input-todo]
    [view {:style {:height 10}}]
    [list-todo]]
   [view
    {:style {:margin 10}}
    [text (str @(rf/subscribe [:get-uncompleted-count]) " items left")]]])

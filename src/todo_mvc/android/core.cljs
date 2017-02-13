(ns todo-mvc.android.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [todo-mvc.input-todo :refer [input-todo]]
            [todo-mvc.list-todo :refer [list-todo]]
            [todo-mvc.events :refer [load-todos parse-todos]]
            [todo-mvc.subs]))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def text-input (r/adapt-react-class (.-TextInput ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def scroll-view (r/adapt-react-class (.-ScrollView ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))

(def logo-img (js/require "./images/cljs.png"))

(defn alert [title]
  (.alert (.-Alert ReactNative) title))

(defn app-root []
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
   [list-todo]])

(defn init []
  (dispatch-sync [:initialize-db])
  (load-todos
    (fn [todos]
      (dispatch [:load-todos todos])))
  (.registerComponent app-registry "TodoMvc" #(r/reactify-component app-root)))


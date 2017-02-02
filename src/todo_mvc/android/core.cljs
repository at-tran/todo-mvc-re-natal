(ns todo-mvc.android.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [todo-mvc.events]
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
  (let [todos (subscribe [:get-todos])]
    (fn []
      [scroll-view {:style                   {:flex-direction "column" :margin 40 :flex 1}
                    :content-container-style {:align-items "center"}}
       [text {:style      {:font-size     50
                           :font-weight   "100"
                           :margin-bottom 20
                           :text-align    "center"
                           :color         "pink"}
              :selectable true}
        "todos"]
       [view {:style {:flex 1 :align-self "stretch"}}
        (map (fn [todo]
               [text {:style {:height 58}
                      :key   (random-uuid)}
                (:desc todo)])
             @todos)]
       [text-input {:style             {:height 58 :align-self "stretch"}
                    :on-submit-editing #(dispatch [:add-todo (.. % -nativeEvent -text)])}]])))
(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "TodoMvc" #(r/reactify-component app-root)))

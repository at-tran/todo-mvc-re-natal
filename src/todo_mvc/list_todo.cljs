(ns todo-mvc.list-todo
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [todo-mvc.editable-text :refer [editable-text]]))

(def ReactNative (js/require "react-native"))
(def FontAwesome (js/require "react-native-vector-icons/FontAwesome"))
(def MaterialIcons (js/require "react-native-vector-icons/MaterialIcons"))
(def LayoutAnimation (.-LayoutAnimation ReactNative))
(def ListView (.-ListView ReactNative))

(def view (r/adapt-react-class (.-View ReactNative)))
(def list-view (r/adapt-react-class (.-ListView ReactNative)))
(def fontawesome-icon (r/adapt-react-class (.-default FontAwesome)))
(def material-icon (r/adapt-react-class (.-default MaterialIcons)))

(when (= "android" (.. ReactNative -Platform -OS))
  (.. ReactNative -UIManager (setLayoutAnimationEnabledExperimental true)))

(defn item-todo [id {:keys [desc done?]}]
  [view
   {:style {:flex-direction "row"
            :flex           1
            :align-items    "center"
            :padding        10
            :margin-top     10}}
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
  (let [ds (new (.-DataSource ListView) #js {:rowHasChanged not=})]
    (r/create-class
      {:reagent-render
       (fn []
         (let [dataSource (.cloneWithRows ds (clj->js @(rf/subscribe [:get-showing-todos])))]
           [list-view
            {:style               {:flex       1
                                   :align-self "stretch"}
             :dataSource          dataSource
             :renderRow           (fn [rowData _ rowID]
                                    (r/as-element
                                      [item-todo rowID (js->clj rowData
                                                                :keywordize-keys true)]))
             ; this flag will be deprecated in future releases of react-native
             :enableEmptySections true}]))
       :componentWillUpdate
       #(.easeInEaseOut LayoutAnimation)})))

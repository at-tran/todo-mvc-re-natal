(ns todo-mvc.editable-text
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))

(def ReactNative (js/require "react-native"))
(def Animated (.-Animated ReactNative))
(def Easing (.-Easing ReactNative))

(def text (r/adapt-react-class (.-Text ReactNative)))
(def animated-text (r/adapt-react-class (.-Text Animated)))
(def text-input (r/adapt-react-class (.-TextInput ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def touchable (r/adapt-react-class (.-TouchableNativeFeedback ReactNative)))

(defn- update-todo-and-finish-editing [id event editing]
  (rf/dispatch [:update-todo id (.. event -nativeEvent -text)])
  (swap! editing not))

(defn- animate [animated-value target]
  (-> Animated
      (.timing animated-value
               (clj->js {:toValue  target
                         :duration 400
                         :easing   (.-linear Easing)}))
      .start))

(defn- text-viewer [desc done?]
  (let [animated-value (new (.-Value Animated) (if done? 0.4 1))]
    (r/create-class
      {:reagent-render
       (fn [desc done?]
         [animated-text
          {:style [{:flex      1
                    :fontSize  18
                    :textAlign "center"
                    :opacity   animated-value
                    :color     "black"}
                   (if done? {:textDecorationLine "line-through"})]}
          desc])

       :component-did-update
       (fn [comp [constructor prev-desc prev-done?]]
         (animate animated-value (if prev-done? 1 0.4)))})))


(defn- text-editor [id desc editing]
  [text-input {:default-value     desc
               :id                (str "input-" (first desc))
               :auto-focus        true
               :on-end-editing    #(update-todo-and-finish-editing id % editing)
               :on-submit-editing #(update-todo-and-finish-editing id % editing)
               :style             {:flex      1
                                   :fontSize  18
                                   :textAlign "center"
                                   :margin    0}}])

(defn editable-text [id desc done?]
  (let [editing (r/atom false)]
    (fn [id desc done?]
      [touchable
       {:on-press #(swap! editing not)}
       [view
        {:style {:flex-direction "row"
                 :flex           1
                 :align-items    "center"}}
        (if @editing
          [text-editor id desc editing]
          [text-viewer desc done?])]])))


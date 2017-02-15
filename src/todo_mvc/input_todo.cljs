(ns todo-mvc.input-todo
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))

(def ReactNative (js/require "react-native"))
(def MaterialIcons (js/require "react-native-vector-icons/MaterialIcons"))

(def view (r/adapt-react-class (.-View ReactNative)))
(def text-input (r/adapt-react-class (.-TextInput ReactNative)))
(def material-icon (r/adapt-react-class (.-default MaterialIcons)))

(defn input-todo []
  (let [state (r/atom "")]
    (fn []
      [view {:align-self     "stretch"
             :flex-direction "row"}
       [text-input {:style             {:height     58
                                        :font-style (if (zero? (count @state)) "italic" "normal")
                                        :fontSize   16
                                        :flex       1}
                    :on-submit-editing #(do (rf/dispatch [:add-todo (.. % -nativeEvent -text)])
                                            (reset! state ""))
                    :on-change-text    #(reset! state (str %))
                    :placeholder       "What needs to be done?"
                    :default-value     @state
                    :auto-capitalize   "sentences"}]
       [material-icon {:name     "keyboard-arrow-down"
                       :size     50
                       :style    {:padding-top 10}
                       :on-press #(rf/dispatch [:toggle-all
                                                (not= 0
                                                      @(rf/subscribe [:get-active-count]))])}]])))
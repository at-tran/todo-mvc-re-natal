(ns todo-mvc.input-todo
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))

(def ReactNative (js/require "react-native"))

(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def text-input (r/adapt-react-class (.-TextInput ReactNative)))

(defn input-todo []
  (let [state (r/atom "")]
    (fn []
      [view {:align-self "stretch"}
       [text-input {:style             {:height     58
                                        :font-style (if (zero? (count @state)) "italic" "normal")
                                        :fontSize   16}
                    :on-submit-editing #(do (rf/dispatch [:add-todo (.. % -nativeEvent -text)])
                                            (reset! state ""))
                    :on-change-text    #(reset! state (str %))
                    :placeholder       "What needs to be done?"
                    :default-value     @state}]])))
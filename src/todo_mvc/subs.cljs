(ns todo-mvc.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :get-todos
  (fn [db _]
    (:todos db)))

(defn- completed? [todo]
  (true? (:done? todo)))

(reg-sub
  :get-uncompleted-count
  (fn [{:keys [todos]} _]
    (count (remove (fn [[_ todo]] (completed? todo)) todos))))
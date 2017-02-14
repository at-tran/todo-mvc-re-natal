(ns todo-mvc.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :get-todos
  (fn [db _]
    (:todos db)))

(defn filter-todos-by-status [todos done?]
  (let [pred (if done? true? false?)]
    (filter (fn [[_ todo]] (pred (:done? todo))) todos)))

(reg-sub
  :get-active-count
  (fn [{:keys [todos]} _]
    (count (filter (fn [[_ todo]] (false? (:done? todo))) todos))))

(defn filter-shown-todos [todos showing]
  (case showing
    :all todos
    :active (filter-todos-by-status todos false)
    :completed (filter-todos-by-status todos true)))

(reg-sub
  :get-showing-todos
  (fn [{:keys [todos showing]} _]
    (filter-shown-todos todos showing)))

(reg-sub
  :get-showing
  (fn [db _]
    (:showing db)))


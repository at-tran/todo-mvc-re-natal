(ns todo-mvc.subs
  (:require [re-frame.core :refer [reg-sub]]
            [todo-mvc.utils :refer [filter-map]]))

(reg-sub
  :get-todos
  (fn [db _]
    (:todos db)))

(reg-sub
  :get-active-count
  (fn [{:keys [todos]} _]
    (count (filter (fn [[_ todo]] (false? (:done? todo))) todos))))

(defn filter-shown-todos [todos showing]
  (case showing
    :all (filter-map (fn [_ _] true) todos)
    :active (filter-map #(not (:done? %2)) todos)
    :completed (filter-map #(:done? %2) todos)))

(reg-sub
  :get-showing-todos
  (fn [{:keys [todos showing]} _]
    (filter-shown-todos todos showing)))

(reg-sub
  :get-showing
  (fn [db _]
    (:showing db)))

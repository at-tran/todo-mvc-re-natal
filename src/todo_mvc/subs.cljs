(ns todo-mvc.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :get-todos
  (fn [db _]
    (:todos db)))
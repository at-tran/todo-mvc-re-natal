(ns todo-mvc.events
  (:require
    [re-frame.core :refer [reg-event-db after]]
    [clojure.spec :as s]
    [todo-mvc.db :as db :refer [app-db]]))

;; -- Interceptors ------------------------------------------------------------
;;
;; See https://github.com/Day8/re-frame/blob/master/docs/Interceptors.md
;;
(defn check-and-throw
  "Throw an exception if db doesn't have a valid spec."
  [spec db [event]]
  (when-not (s/valid? spec db)
    (let [explain-data (s/explain-data spec db)]
      (throw (ex-info (str "Spec check after " event " failed: " explain-data) explain-data)))))

(def validate-spec
  (if goog.DEBUG
    (after (partial check-and-throw ::db/app-db))
    []))

;; -- Handlers --------------------------------------------------------------

(reg-event-db
  :initialize-db
  validate-spec
  (fn [_ _]
    app-db))

(reg-event-db
  :add-todo
  validate-spec
  (fn [db [_ todo]]
    (assoc-in db [:todos (random-uuid)] {:desc  todo
                                         :done? false})))

(reg-event-db
  :toggle-todo
  validate-spec
  (fn [db [_ id]]
    (update-in db [:todos id :done?] not)))

(reg-event-db
  :remove-todo
  validate-spec
  (fn [db [_ id]]))


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

(defn index-of-id [todos id]
  (loop [index 0 [cur & more] todos]
    (when cur
      (if (= id (:id cur))
        index
        (recur (inc index) more)))))

(defn toggle-todo [todos id]
  (if-let [index (index-of-id todos id)]
    (update todos index
            (fn [todo] (update todo :done? not)))
    todos))

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
    (update db :todos
            #(conj % {:id    (random-uuid)
                      :desc  todo
                      :done? false}))))

(reg-event-db
  :toggle-todo
  validate-spec
  (fn [db [_ id]]
    (update db :todos
            #(toggle-todo % id))))

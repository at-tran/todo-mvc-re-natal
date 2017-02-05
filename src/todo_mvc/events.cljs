(ns todo-mvc.events
  (:require
    [re-frame.core :refer [dispatch-sync reg-event-db after]]
    [clojure.spec :as s]
    [todo-mvc.db :as db :refer [app-db]]
    [linked.core :as linked]))

(def ReactNative (js/require "react-native"))

(def AsyncStorage (.-AsyncStorage ReactNative))

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

(def update-storage
  (after (fn [db _]
           (.setItem AsyncStorage "todos" (str (vals (:todos db)))))))

(defn parse-todos [s]
  (if s
    (apply linked/map
           (interleave (repeatedly random-uuid) (cljs.reader/read-string s)))
    (linked/map)))

;; -- Handlers --------------------------------------------------------------

(reg-event-db
  :initialize-db
  validate-spec
  (fn [_ [_ data]]
    (println data)
    data))

(reg-event-db
  :add-todo
  [validate-spec update-storage]
  (fn [db [_ todo]]
    (assoc-in db [:todos (random-uuid)] {:desc  todo
                                         :done? false})))

(reg-event-db
  :toggle-todo
  [validate-spec update-storage]
  (fn [db [_ id]]
    (update-in db [:todos id :done?] not)))

(reg-event-db
  :remove-todo
  [validate-spec update-storage]
  (fn [db [_ id]]))

;; Functions

(defn load-todos [callback]
  (-> (.getItem AsyncStorage "todos")
      (.then
        (fn [s]
          (dispatch-sync [:initialize-db {:todos (parse-todos s)}])))
      (.then callback)))

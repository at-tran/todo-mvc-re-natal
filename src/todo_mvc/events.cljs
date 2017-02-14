(ns todo-mvc.events
  (:require
    [re-frame.core :refer [dispatch-sync reg-event-db after]]
    [clojure.spec :as s]
    [todo-mvc.db :as db :refer [app-db]]
    [linked.core :as linked]
    [todo-mvc.utils :refer [filter-map update-map]]))

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
           (.setItem AsyncStorage "data" (pr-str db)))))

; Shamelessly adapted from
; https://github.com/clojure/core.incubator/blob/master/src/main/clojure/clojure/core/incubator.clj
(defn dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
  nested structure. keys is a sequence of keys. Any empty maps that result
  WILL be present in the new structure."
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (assoc m k newmap))
      m)
    (dissoc m k)))

;; -- Handlers --------------------------------------------------------------

(reg-event-db
  :initialize-db
  validate-spec
  (fn [_ [_]]
    app-db))

(reg-event-db
  :load-todos
  [validate-spec update-storage]
  (fn [db [_ todos]]
    (update db :todos #(merge % todos))))

(reg-event-db
  :add-todo
  [validate-spec update-storage]
  (fn [db [_ desc]]
    (if (seq desc)
      (assoc-in db
                [:todos (str (random-uuid))]
                {:desc  desc
                 :done? false})
      db)))

(reg-event-db
  :toggle-todo
  [validate-spec update-storage]
  (fn [db [_ id]]
    (update-in db [:todos id :done?] not)))

(reg-event-db
  :remove-todo
  [validate-spec update-storage]
  (fn [db [_ id]]
    (dissoc-in db [:todos id])))

(reg-event-db
  :update-todo
  [validate-spec update-storage]
  (fn [db [_ id new-desc]]
    (if (seq new-desc)
      (assoc-in db [:todos id :desc] new-desc)
      (dissoc-in db [:todos id]))))

(reg-event-db
  :set-showing
  [validate-spec update-storage]
  (fn [db [_ showing]]
    (assoc db :showing showing)))

(reg-event-db
  :clear-completed
  [validate-spec update-storage]
  (fn [db [_]]
    (update db :todos #(into (linked/map)
                             (filter-map (fn [_ v] (not (:done? v))) %)))))

(reg-event-db
  :toggle-all
  [validate-spec update-storage]
  (fn [db [_ completed?]]
    (update db :todos #(into (linked/map)
                             (update-map (fn [k v]
                                           [k (if completed?
                                                (assoc v :done? true)
                                                (assoc v :done? false))])
                                         %)))))

;; Functions

(defn load-todos [callback]
  (-> (.getItem AsyncStorage "data")
      (.then #(if % (cljs.reader/read-string %) app-db))
      (.then callback)))
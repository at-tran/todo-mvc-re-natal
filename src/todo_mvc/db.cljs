(ns todo-mvc.db
  (:require [clojure.spec :as s]
            [linked.core :as linked]))

;; spec of app-db
(s/def ::id string?)
(s/def ::desc string?)
(s/def ::done? boolean?)

(s/def ::todo
  (s/keys :req-un [::desc ::done?]))

(s/def ::todos (s/map-of ::id ::todo :min-count 0))

(s/def ::showing #{:all :active :completed})

(s/def ::app-db
  (s/keys :req-un [::todos ::showing]))

;; initial state of app-db
(def app-db {:todos   (linked/map)
             :showing :all})
(ns todo-mvc.utils)

(defn filter-map [pred m]
  (filter (fn [[k v]] (pred k v)) m))

(defn update-map [f m]
  (map (fn [[k v]] (f k v)) m))


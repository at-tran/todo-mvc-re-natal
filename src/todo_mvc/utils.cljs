(ns todo-mvc.utils)

(defn filter-todos-by-status [todos done?]
  (let [pred (if done? true? false?)]
    (filter (fn [[_ todo]] (pred (:done? todo))) todos)))

(defn filter-map [pred m]
  (filter (fn [[k v]] (pred k v)) m))

(defn update-map [f m]
  (map (fn [[k v]] (f k v)) m))


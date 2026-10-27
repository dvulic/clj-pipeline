(ns clj-pipeline.core)

(defn setText
  [scene value]
  (assoc scene :text value)
  )

(defn toLessonMap
  [& slides]
  (assoc {} :slides (vec slides))
  )

(defn toSlide
  [& maps]
  (vec maps)
  )

(defn setBackground
  [name bg-path]
  "Returns a map with scene name and bg-path"
  (assoc {} :name name :bg bg-path))

(defn toFile [value]
  (spit "out.txt", value)
  )



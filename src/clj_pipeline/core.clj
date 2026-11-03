(ns clj-pipeline.core
  (:require [clojure.data.json :as json]
            [clojure.string :as str])
  (:import (java.io File)))

(defn to-kv-pair [[key value]] [(keyword key) value])

(defn parse-text
  [line]
  (let [start (str/index-of line "\"") end (str/index-of line "\"" (inc start)) value (subs line (inc start) end) wo-value (str (subs line 0 start) (subs line (inc end)))]
    (let [[_tag & attributes] (str/split (str/trim wo-value) #"\s+")] (merge {:value value :font "Roboto" :size 12 :alignment 1} (into {} (map to-kv-pair (partition 2 attributes)))))
  )
)

(defn parse-character
  [line]
  (let [[_tag name & attributes] (str/split (str/trim line) #"\s+")] (merge {:name name :scale 1 :position 0 :pose "idle" :expression "happy"} (into {} (map to-kv-pair (partition 2 attributes))))))

(defn parse-background
  [line] (assoc {} :path (subs line (count "@background ")))
)

(defn parse-slide [block]
  (let [lines (str/split-lines block) bg-line (some #(when (str/starts-with? % "@background ") %) lines) ch-lines (filterv #(when (str/starts-with? % "@character ") %) lines) text-lines (filterv #(when (str/starts-with? % "@text ") %) lines)]
    (merge {} (when bg-line {:background (parse-background bg-line)}) (if (seq text-lines) {:texts (mapv parse-text text-lines)}) (if (seq ch-lines) {:characters (mapv parse-character ch-lines)})))
  )

(defn parse-raw-lesson [path]
  (let [text (slurp path) split-blocks (str/split text #"\r?\n[ \t]*\r?\n+") blocks (remove str/blank? split-blocks)] (mapv parse-slide blocks))
)

(defn is-file [path] (.isFile (^[String] File/new path)))

(defn to-json [filename lesson-map]
  (spit (clojure.string/join ["lessons/" filename ".json"]) (json/write-str lesson-map))
)

(defn main []
  ;;example use
  (to-json "example-lesson" (parse-raw-lesson "lessons/rawLesson.txt"))
)


(ns clj-pipeline.core
  (:require [clojure.data.json :as json]
            [clojure.string :as str])
  (:import (java.io File)))

(defn parseText
  ;;current list of attributes: value & optional: font size alignment
  ;;example: @text "Hello and welcome!" size 12 alignment 1
  [line]
  ;;extract value first
  (let [
        start (str/index-of line "\"")
        end (str/index-of line "\"" (inc start))
        value (subs line (inc start) end)
        wo-value (str (subs line 0 start) (subs line (inc end)))
      ]
    ;;remove tag and apply attributes
    (let [[_tag & attributes]
          (str/split (str/trim wo-value) #"\s+")]
      (merge
        ;;default values for text
        {
         :value value
         :font "Roboto"
         :size 12
         :alignment 1
         }

        (into {}
          (map
            (fn
              [[key value]]
              [(keyword key) value]
              )
            (partition 2 attributes)
            )
          )
        )
      )
  )
)
(defn parseCharacter
  ;;current list of attributes: name & optional: pose expression scale position
  ;;example: @character Max scale 10 pose "A"
  [line]
  (let [[_tag name & attributes]
        (str/split (str/trim line) #"\s+")]
    (merge
      ;;default values for character
      {
        :name name
        :scale 1
        :position 0
        :pose "idle"
        :expression "happy"
      }

      (into {}
        (map
          (fn
            [[key value]]
            [(keyword key) value]
          )
          (partition 2 attributes)
        )
      )
    )
  )
)

(defn parseBackground
  [line]
  "Returns a map with background path"
  (assoc {} :path (subs line (count "@background ")))
)

(defn parseSlide
  [block]
  (let
    [
        lines (str/split-lines block)
        bgLine (some #(when (str/starts-with? % "@background ") %) lines)
        ;;TODO add support for multiple character and text maps
        characterLine (some #(when (str/starts-with? % "@character ") %) lines)
        textLine (some #(when (str/starts-with? % "@text ") %) lines)
    ]
    (merge {}
           (when bgLine {:background (parseBackground bgLine)})
           (when characterLine {:character (parseCharacter characterLine)})
           (when textLine {:text (parseText textLine)})
    )
  )
)

(defn parseRawLesson
  [path]
  (let [text (slurp path)
        split-blocks (str/split text #"\r?\n[ \t]*\r?\n+")
        blocks (remove str/blank? split-blocks)]
  (mapv parseSlide blocks))
)

(defn isFile [path]
  (.isFile (^[String] File/new path)))

(defn toJson [filename lessonMap]
  (spit (clojure.string/join ["lessons/" filename ".json"]) (json/write-str lessonMap))
)

(defn main []
  ;;example use
  (toJson "example-lesson" (parseRawLesson "lessons/rawLesson.txt"))
  (println "[\n  {\n    \"background\": {\n      \"path\": \"assets/sample-bg.png\"\n    },\n    \"character\": {\n      \"name\": \"Mimi\",\n      \"scale\": \"2\",\n      \"position\": 0,\n      \"pose\": \"A\",\n      \"expression\": \"happy\"\n    },\n    \"text\": {\n      \"value\": \"Welcome!\",\n      \"font\": \"Roboto\",\n      \"size\": 12,\n      \"alignment\": 1\n    }\n  },\n  {\n    \"background\": {\n      \"path\": \"assets/sample-bg.png\"\n    },\n    \"character\": {\n      \"name\": \"Mimi\",\n      \"scale\": \"2\",\n      \"position\": 0,\n      \"pose\": \"A\",\n      \"expression\": \"happy\"\n    },\n    \"text\": {\n      \"value\": \"This is a sample lesson!\",\n      \"font\": \"Roboto\",\n      \"size\": 12,\n      \"alignment\": 1\n    }\n  }\n]")
)


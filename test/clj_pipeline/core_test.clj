(ns clj-pipeline.core-test
  (:require [midje.sweet :refer :all]
            [clj-pipeline.core :refer :all]))

(facts "parse-text-test"
       (parse-text "@text \"This is a sample lesson!\"") => {:value "This is a sample lesson!" :font "Roboto" :size 12 :alignment 1}
       (parse-text "@text \"  This is    a sample    lesson!\" alignment 2 font TestFont size 12") => {:value "  This is    a sample    lesson!" :font "TestFont" :size "12" :alignment "2"})
(facts "character test"
       (parse-character "@character Max") => {:name "Max" :scale 1 :position 0 :pose "idle" :expression "happy"}
)

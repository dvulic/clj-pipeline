(defproject clj-pipeline "0.1.0-SNAPSHOT"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.12.2"]
                 [org.clojure/data.json "2.5.1"]]
  :profiles {:dev {:dependencies [[midje "1.10.10"
                                   :exclusions [org.clojure/clojure]]]
                   :plugins [[lein-midje "3.2.1"]]}}
  :repl-options {:init-ns clj-pipeline.core})

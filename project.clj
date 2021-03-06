(defproject big-replicate "0.1.0"
  :description "Copies data between BigQuery projects"
  :url "https://github.com/uswitch/big-replicate"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :uberjar-name "big-replicate-standalone.jar"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [gclouj/bigquery "0.2.5" :exclusions [commons-logging]]
                 [gclouj/storage "0.2.5"]
                 [org.clojure/tools.cli "0.3.1"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/core.async "0.2.385"]
                 [org.slf4j/slf4j-api "1.7.21"]
                 [org.slf4j/jcl-over-slf4j "1.7.21"]]
  :profiles {:dev {:dependencies [[org.slf4j/slf4j-simple "1.7.21"]]
                   :jvm-opts ["-Dorg.slf4j.simpleLogger.defaultLogLevel=debug"]}
             :uberjar {:dependencies [[ch.qos.logback/logback-classic "1.1.7"]]
                       :aot :all}})

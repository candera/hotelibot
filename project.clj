(defproject hotelibot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.3.0"]
                 [org.clojure/tools.namespace "0.2.4"]
                 [com.stuartsierra/component "0.2.1"]
                 [ring/ring-json "0.3.1"]
                 [org.slf4j/jcl-over-slf4j "1.7.6"]
                 [org.slf4j/slf4j-api "1.7.6"]
                 [org.slf4j/slf4j-log4j12 "1.7.6"]]
  :plugins [[lein-ring "0.8.10"]]
  :ring {:handler hotelibot.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-jetty-adapter "1.2.1"]
                        [ring-mock "0.1.5"]]
         :source-paths ["dev"]}})
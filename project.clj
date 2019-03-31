(defproject assignment "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.logging "0.4.1"]
                 [clj-http "3.9.1"]
                 [compojure "1.6.1"]
                 [cheshire "5.8.1"]
                 [com.taoensso/sente "1.11.0"]]
  :repl-options {:init-ns assignment.core}
  :profiles {:dev
             {:plugins [[lein-ring "0.12.5"]]
              :ring {:handler assignment.routes/app}
              :repl-options {:init-ns assignment.routes}}}
  :main assignment.simulator)

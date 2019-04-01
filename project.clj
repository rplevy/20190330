(defproject assignment "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.logging "0.4.1"]
                 [clj-http "3.9.1"]
                 [compojure "1.6.1"]
                 [cheshire "5.8.1"]
                 [cider/piggieback "0.3.8"]
                 [com.taoensso/sente "1.14.0-RC2"]
                 [hiccup "1.0.5"]
                 [http-kit "2.2.0"]
                 [reagent "0.8.1"]
                 [ring "1.5.1"]
                 [ring/ring-defaults "0.3.2"]]
  :plugins [[lein-ring "0.12.5"]
            [lein-cljsbuild "1.1.5"]
            [lein-figwheel "0.5.16"]]
  :source-paths ["src/clj" "src/cljs"]
  :figwheel {:nrepl-port 7002
             :server-port 3000
             :nrepl-middleware [cider.piggieback/wrap-cljs-repl]
             :ring-handler assignment.service/app}
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]
  :cljsbuild {:builds {:app
                       {:source-paths ["src/cljs"]
                        :compiler
                        {:main "assignment.service"
                         :output-to "resources/public/js/compiled/app.js"
                         :output-dir "resources/public/js/compiled/out"
                         :asset-path  "js/compiled/out"
                         :source-map true
                         :optimizations :none
                         :pretty-print  true}
                        :figwheel
                        {:on-jsload "assignment.service/mount-root"
                         :open-urls ["http://localhost:3000/"]}}}}
  :main assignment.simulator)

(defproject techwall "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [ring-simpleweb-adapter "0.2.0"]
                 [compojure "1.1.3"]
                 [cheshire "4.0.3"]
                 [com.h2database/h2 "1.3.168"]
                 [org.clojure/java.jdbc "0.2.3"]]
  :plugins [[lein-ring "0.7.5"]]
  :ring {:handler techwall.handler/app}
  :main techwall.app
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]]}})

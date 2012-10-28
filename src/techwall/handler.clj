(ns techwall.handler
  (:use compojure.core)
  (:require [cheshire.core :as json]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as resp]))

(def ^{:private true} walls [{:id 789 :name "Foo"} {:id 342 :name "Bar"}])
(def ^{:private true} technologies ["java" "scala" "ruby" "erlang"])
(def ^{:private true} wall {:id 789
                            :name "Project Zappa"
                            :columns [{:name "Radical"    :entries ["erlang"]}
                                      {:name "Tentative"  :entries ["clojure" "play"]}
                                      {:name "Adopted"    :entries ["java" "junit" "guava"]}
                                      {:name "Deprecated" :entries ["easymock" "test objects"]}
                                      {:name "Obsolete"   :entries ["lambdaj"]}]})

(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (GET "/walls.json" [] {:headers {"Content-Type" "application/json"} :body (json/generate-string walls) })
  (GET "/technologies.json" [] {:headers {"Content-Type" "application/json"} :body (json/generate-string technologies) })
  (GET "/walls/:id/wall.json" [] {:headers {"Content-Type" "application/json"} :body (json/generate-string wall)})
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

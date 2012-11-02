(ns techwall.handler
  (:use compojure.core)
  (:require [cheshire.core :as json]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as resp]))

(def ^{:private true} walls [{:id 789 :name "Foo"} {:id 342 :name "Bar"}])
(def ^{:private true} technologies [{:id 1 :name "java"} {:id 2 :name "scala"} {:id 3 :name "ruby"} {:id 4 :name "erlang"}])
(def ^{:private true} wall {:id 789
                            :name "Project Zappa"
                            :categories [{:id 1 :name "Radical"    :entries [{:id 4 :name "erlang"}]}
                                         {:id 2 :name "Tentative"  :entries [{:id 5 :name "clojure"} {:id 6 :name "play"}]}
                                         {:id 3 :name "Adopted"    :entries [{:id 1 :name "java"} {:id 7 :name "junit"} {:id 8 :name "guava"}]}
                                         {:id 4 :name "Deprecated" :entries [{:id 9 :name "easymock"} {:id 10 :name "test objects"}]}
                                         {:id 5 :name "Obsolete"   :entries [{:id 11 :name "lambdaj"}]}]})

(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (GET "/walls.json" [] {:headers {"Content-Type" "application/json"} :body (json/generate-string walls) })
  (GET "/technologies.json" [] {:headers {"Content-Type" "application/json"} :body (json/generate-string technologies) })
  (GET "/walls/:id/wall.json" [] {:headers {"Content-Type" "application/json"} :body (json/generate-string wall)})
  (POST "/walls/:wall-id/categories/:category-id/entries" [] "{}")
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

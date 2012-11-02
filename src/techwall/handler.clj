(ns techwall.handler
  (:use compojure.core)
  (:require [cheshire.core :as json]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [techwall.technologies :as tech]
            [techwall.walls :as walls]))

(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (GET "/technologies" [] (tech/all))
  (GET "/walls" [] (walls/all))
  (GET "/wall/:wallId" [wallId] (walls/wall wallId))
  (POST "/wall/:wallId/category/:categoryId/entry" [wallId categoryId techId techName] (walls/add-entity wallId categoryId techId techName))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

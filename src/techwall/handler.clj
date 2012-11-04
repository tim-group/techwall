(ns techwall.handler
  (:use compojure.core)
  (:require [cheshire.core :as json]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [techwall.technologies :as tech]
            [techwall.walls :as walls]))

(defn json-response-of [data] 
  {:headers {"Content-Type" "application/json"} :body (json/generate-string data)})

(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (GET "/technologies" [] (json-response-of (tech/all)))
  (GET "/walls" [] (json-response-of (walls/all)))
  (GET "/wall/:wallId" [wallId] (json-response-of (walls/wall wallId)))
  (POST "/wall/:wallId/category/:categoryId/entry" [wallId categoryId techId techName]
        (json-response-of (walls/add-entity wallId categoryId techId techName)))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

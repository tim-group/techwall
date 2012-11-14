(ns techwall.handler
  (:use compojure.core)
  (:require [cheshire.core :as json]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [ring.middleware.file-info :as file-info]
            [techwall.technologies :as tech]
            [techwall.walls :as walls]
            [techwall.notes :as notes]
            [techwall.db :as db]))

(defn- json-response-of [data] 
  {:headers {"Content-Type" "application/json"} :body (json/generate-string data)})

(defn- route-backup [path]
  (file-info/wrap-file-info (GET path [] (db/backup-to-file))))

(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (GET "/technologies" [] (json-response-of (tech/all)))
  (GET "/technology/:tech-id" [tech-id] (json-response-of (tech/find-by-id tech-id)))
  (PUT "/technology/:tech-id" [tech-id name techtypeid description notes]
       (json-response-of (tech/update-tech tech-id name techtypeid description)))
  (GET "/techtypes" [] (json-response-of (tech/all-types)))
  (GET "/walls" [] (json-response-of (walls/all)))
  (GET "/wall/:wallId" [wallId] (json-response-of (walls/wall wallId)))
  (POST "/wall" [name] (json-response-of (walls/add-wall name)))
  (POST "/wall/:wallId/category/:categoryId/entry" [wallId categoryId techId techName]
        (json-response-of (walls/add-entry wallId categoryId techId techName)))
  (GET "/wall/:wall-id/technology/:technology-id/note" [wall-id technology-id]
       (json-response-of (notes/find-note wall-id technology-id)))
  (PUT "/wall/:wall-id/technology/:technology-id/note" [wall-id technology-id note]
       (json-response-of (notes/write-note wall-id technology-id note)))
  (route-backup "/backup.zip")
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

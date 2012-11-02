(ns techwall.technologies
  (:require [cheshire.core :as json]))

(def ^{:private true} technologies [{:id 1 :name "java"} {:id 2 :name "scala"} {:id 3 :name "ruby"} {:id 4 :name "erlang"}])

(defn all [] 
  {:headers {"Content-Type" "application/json"} :body (json/generate-string technologies)})


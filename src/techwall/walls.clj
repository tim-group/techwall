(ns techwall.walls
  (:use techwall.db)
  (:require [cheshire.core :as json]
            [clojureql.core :as ql]))

(def ^{:private true} wall-data {:id 789
                                 :name "Project Zappa"
                                 :categories [{:id 1 :name "Radical"    :entries [{:id 4 :name "erlang"}]}
                                              {:id 2 :name "Tentative"  :entries [{:id 5 :name "clojure"} {:id 6 :name "play"}]}
                                              {:id 3 :name "Adopted"    :entries [{:id 1 :name "java"} {:id 7 :name "junit"} {:id 8 :name "guava"}]}
                                              {:id 4 :name "Deprecated" :entries [{:id 9 :name "easymock"} {:id 10 :name "test objects"}]}
                                              {:id 5 :name "Obsolete"   :entries [{:id 11 :name "lambdaj"}]}]})

(defn all [] 
  {:headers {"Content-Type" "application/json"} :body (json/generate-string @(ql/table :walls))})

(defn wall [id]
  {:headers {"Content-Type" "application/json"} :body (json/generate-string wall-data)})

(defn add-entity [wall-id category-id tech-id tech-name]
  {:headers {"Content-Type" "application/json"} :body (json/generate-string {:id tech-id :name tech-name})})
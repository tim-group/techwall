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

(defn wall [wall-id]
  (let [data @(->
                (ql/select (ql/table {:transitions :a}) (ql/where (= :a.wall_id wall-id)))
                (ql/join (ql/aggregate (ql/table {:transitions :b}) [[:max/ordering :as :latest]] [:technology_id :wall_id])
                         (ql/where (and (= :a.wall_id :b.wall_id)
                                        (= :a.technology_id :b.technology_id)
                                        (= :a.ordering :b.latest))))
                (ql/join (ql/table {:categories :c}) (ql/where (= :a.category_id :c.id)))
                (ql/join (ql/table {:technologies :d}) (ql/where (= :a.technology_id :d.id)))
                (ql/select (ql/where (= :a.added 1)))
                (ql/project [:a.category_id :a.technology_id [:c.name :as :category_name] [:d.name :as :technology_name]]))]
    {:headers {"Content-Type" "application/json"} :body (json/generate-string 
                                                          (reduce (fn [result [[category-id category-name] datum]] (conj result {:id category-id :name category-name :entries
                                                                                                                                 (map #(identity {:id (:technology_id %) :name (:technology_name %)}) datum)}))
                                                                  [] (group-by (fn [x] [(:category_id x) (:category_name x)]) data)))}
    ))

(defn add-entity [wall-id category-id tech-id tech-name]
  {:headers {"Content-Type" "application/json"} :body (json/generate-string {:id tech-id :name tech-name})})


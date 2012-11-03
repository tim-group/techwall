(ns techwall.walls
  (:use techwall.db)
  (:require [cheshire.core :as json]
            [clojureql.core :as ql]
            [techwall.technologies :as tech]))

(def ^{:private true} wall-data-stmt
  "SELECT c.id AS category_id, c.name AS category_name, t.technology_id, t.technology_name
     FROM categories c
     LEFT OUTER JOIN (SELECT t1.category_id, g.id AS technology_id, g.name AS technology_name
                        FROM transitions t1
                        JOIN technologies g ON (g.id = t1.technology_id)
                       WHERE t1.wall_id = ?
                         AND t1.added = 1
                         AND t1.ordering = (SELECT MAX(t2.ordering)
                                             FROM transitions t2
                                            WHERE t2.wall_id = t1.wall_id
                                              AND t2.technology_id = t1.technology_id)) t
                  ON (t.category_id = c.id)")

(defn all [] 
  {:headers {"Content-Type" "application/json"} :body (json/generate-string @(ql/table :walls))})

(defn wall [wall-id]
  (let [wall-name @(ql/pick (ql/select (ql/table :walls) (ql/where (= :id wall-id))) :name)
        data (db-select wall-data-stmt wall-id) 
        categories (reduce (fn [result [[category-id category-name] datum]]
                             (conj result {:id category-id :name category-name :entries
                                           (map #(identity {:id (:technology_id %) :name (:technology_name %)})
                                                (filter :technology_id datum))}))
                           [] (group-by (fn [x] [(:category_id x) (:category_name x)]) data))]
    {:headers {"Content-Type" "application/json"} :body (json/generate-string {:id wall-id :name wall-name :categories categories})}))

(defn add-entity [wall-id category-id tech-id tech-name]
  (let [technology (tech/find-or-make tech-id tech-name)]
    (ql/conj! (ql/table :transitions) {:wall_id wall-id :category_id category-id :technology_id (:id technology) :added 1})
    {:headers {"Content-Type" "application/json"} :body (json/generate-string technology)}))


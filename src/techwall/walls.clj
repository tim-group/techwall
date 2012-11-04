(ns techwall.walls
  (:require [techwall.db :as db]
            [techwall.technologies :as tech]))

(defn all [] (db/select "SELECT * FROM walls"))
(defn find-by-id [id] (db/select-one "SELECT id, name FROM walls WHERE id = ?" id))

(defn- insert [wall-id, category-id, technology-id]
  (db/do-insert "INSERT INTO transitions (wall_id, category_id, technology_id, added)
                 VALUES (?, ?, ?, 1)" wall-id, category-id, technology-id))

(defn- assignments-by-id [wall-id]
  (db/select "SELECT c.id AS category_id, c.name AS category_name, t.technology_id, t.technology_name
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
                             ON (t.category_id = c.id)" wall-id))

(defn wall [wall-id]
  (let [wall-name (:name (find-by-id wall-id))
        data (assignments-by-id wall-id)
        categories (reduce (fn [result [[category-id category-name] datum]]
                             (conj result {:id category-id :name category-name :entries
                                           (map #(identity {:id (:technology_id %) :name (:technology_name %)})
                                                (filter :technology_id datum))}))
                           [] (group-by (fn [x] [(:category_id x) (:category_name x)]) data))]
    {:id wall-id :name wall-name :categories categories}))

(defn add-entity [wall-id category-id tech-id tech-name]
  (let [technology (tech/find-or-make tech-id tech-name)]
    (insert wall-id category-id (:id technology))
    technology))


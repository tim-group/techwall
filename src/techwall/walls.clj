(ns techwall.walls
  (:require [techwall.db :as db]
            [techwall.technologies :as tech]))

(defn all [] (db/select "SELECT * FROM walls"))
(defn find-by-id [id] (db/select-one "SELECT id, name FROM walls WHERE id = ?" id))
(defn find-by-name [name] (db/select-one "SELECT id, name FROM walls WHERE name = ?" name))

(defn- insert-entry [wall-id, category-id, technology-id]
  (db/do-insert "INSERT INTO transitions (wall_id, category_id, technology_id, added)
                 VALUES (?, ?, ?, 1)" wall-id, category-id, technology-id))

(defn- insert-wall [name] (db/do-insert "INSERT INTO walls (name)
                                         SELECT ? FROM DUAL
                                          WHERE NOT EXISTS (SELECT 1
                                                              FROM walls
                                                              WHERE name = ?)" name name))

(defn- assignments-by-id [wall-id]
  (db/select "SELECT c.id AS category_id, c.name AS category_name, t.technology_id, t.technology_name, t.techtype_id
                FROM categories c
                LEFT OUTER JOIN (SELECT t1.category_id, g.id AS technology_id, g.name AS technology_name, g.techtype_id
                                   FROM transitions t1
                                   JOIN technologies g ON (g.id = t1.technology_id)
                                  WHERE t1.wall_id = ?
                                    AND t1.added = 1
                                    AND t1.ordering = (SELECT MAX(t2.ordering)
                                                        FROM transitions t2
                                                       WHERE t2.wall_id = t1.wall_id
                                                         AND t2.technology_id = t1.technology_id)) t
                             ON (t.category_id = c.id)
               ORDER BY c.ordering ASC" wall-id))

(defn wall [wall-id]
  (let [wall-name (:name (find-by-id wall-id))
        data (assignments-by-id wall-id)
        categories (reduce (fn [result [[category-id category-name] datum]]
                             (conj result {:id category-id :name category-name :entries
                                           (map #(identity {:id (:technology_id %) :name (:technology_name %) :techtypeid (:techtype_id %)})
                                                (filter :technology_id datum))}))
                           [] (group-by (fn [x] [(:category_id x) (:category_name x)]) data))]
    {:id wall-id :name wall-name :categories categories}))

(defn add-wall [name]
  (if-let [id (insert-wall name)]
    {:id id :name name}
    (find-by-name name)))

(defn add-entry [wall-id category-id tech-id tech-name]
  (let [technology (tech/find-or-make tech-id tech-name)]
    (insert-entry wall-id category-id (:id technology))
    technology))


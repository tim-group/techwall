(ns techwall.technologies
  (:require [techwall.db :as db]))

(defn all [] (db/select "SELECT * FROM technologies"))
(defn find-by-id [id] (db/select-one "SELECT id, name FROM technologies WHERE id = ?" id))
(defn find-by-name [name] (db/select-one "SELECT id, name FROM technologies WHERE UPPER(name) = UPPER(?)" name))

(defn- insert [name] (db/do-insert "INSERT INTO technologies (name)
                                    SELECT ? FROM DUAL
                                     WHERE NOT EXISTS (SELECT 1
                                                 FROM technologies
                                                WHERE UPPER(name) = UPPER(?))" name name))

(defn find-or-make
  ([id name]
    (if-let [existing (find-by-id id)]
      existing
      (find-or-make name)))
  ([name]
    (if-let [id (insert name)]
      {:id id :name name}
      (find-by-name name))))


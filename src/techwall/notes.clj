(ns techwall.notes
  (:require [techwall.db :as db]))

(defn find [wall-id tech-id]
  (db/select-one "SELECT a.id, a.note, a.wall_id as wallid, a.technology_id as technologyid
                    FROM walltechnotes a
                   WHERE a.wall_id = ?
                     AND a.technology_id = ?
                     AND a.ordering = SELECT MAX(b.ordering)
                                        FROM walltechnotes b
                                       WHERE b.wall_id = ?
                                         AND b.technology_id = ?"
                 wall-id tech-id wall-id tech-id))

(defn write [wall-id tech-id note]
  (db/do-insert "INSERT INTO walltechnotes (wall_id, technology_id, note) VALUES (?, ?, ?)" wall-id tech-id note))



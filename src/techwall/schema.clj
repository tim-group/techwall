(ns techwall.schema
  (:require [clojure.java.jdbc :as jdbc]))

(def db
  {:classname   "org.h2.Driver"
   :subprotocol "h2"
   :subname      "~/techwall6"})

(def ^{:private true} upgrades
  ["CREATE TABLE MIGRATIONS (ID INT PRIMARY KEY, EXECUTION_DATETIME TIMESTAMP)"
   "CREATE TABLE WALLS (ID INT PRIMARY KEY, NAME VARCHAR(255))"
   "CREATE TABLE CATEGORIES (ID INT PRIMARY KEY, NAME VARCHAR(255))"
   "CREATE TABLE TECHNOLOGIES (ID INT PRIMARY KEY, NAME VARCHAR(255))"
   "CREATE TABLE VERSIONS (ID INT PRIMARY KEY, CREATION_DATETIME TIMESTAMP)"
   "CREATE TABLE ALLOCATIONS (ID INT PRIMARY KEY, VERSION_ID INT, WALL_ID INT, CATEGORY_ID INT, TECHNOLOGY_ID INT)"
   ])

(defn- next-upgrade-number []
  (if (-> (jdbc/find-connection) .getMetaData (.getTables nil nil "MIGRATIONS" nil) .next)
    (jdbc/with-query-results rows ["SELECT MAX(id) + 1 as migration_no FROM MIGRATIONS"] (:migration_no (first rows)))
    0))

(defn create []
  (jdbc/with-connection db
    (loop [idx (next-upgrade-number)]
      (if (< idx (count upgrades))
        (do (jdbc/transaction
              (jdbc/do-commands
                (upgrades idx)
                (str "INSERT INTO MIGRATIONS VALUES (" idx ", NOW());")))
          (recur (inc idx)))))))
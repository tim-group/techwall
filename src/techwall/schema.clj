(ns techwall.schema
  (:require [clojure.java.jdbc :as jdbc]))

(def db
  {:classname   "org.h2.Driver"
   :subprotocol "h2"
   :subname      "~/techwall13"})

(def ^{:private true} migrations
  ["CREATE TABLE MIGRATIONS (ID INT PRIMARY KEY, EXECUTION_DATETIME TIMESTAMP)"
   "CREATE TABLE WALLS (ID INT PRIMARY KEY AUTO_INCREMENT, NAME VARCHAR(255))"
   "CREATE TABLE CATEGORIES (ID INT PRIMARY KEY AUTO_INCREMENT, NAME VARCHAR(255))"
   "CREATE TABLE TECHNOLOGIES (ID INT PRIMARY KEY AUTO_INCREMENT, NAME VARCHAR(255))"
   "CREATE TABLE TRANSITIONS (ID INT PRIMARY KEY AUTO_INCREMENT, ORDERING INT AUTO_INCREMENT, WALL_ID INT, CATEGORY_ID INT, TECHNOLOGY_ID INT, ADDED INT)"
   ])

(defn- next-upgrade-number []
  (if (-> (jdbc/find-connection) .getMetaData (.getTables nil nil "MIGRATIONS" nil) .next)
    (jdbc/with-query-results rows ["SELECT MAX(id) + 1 as migration_no FROM MIGRATIONS"] (:migration_no (first rows)))
    0))

(defn create []
  (jdbc/with-connection db
    (loop [idx (next-upgrade-number)]
      (if (< idx (count migrations))
        (do (jdbc/transaction
              (jdbc/do-commands
                (migrations idx)
                (str "INSERT INTO MIGRATIONS VALUES (" idx ", NOW());")))
          (recur (inc idx)))))))


(ns techwall.schema
  (:require [clojure.java.jdbc :as jdbc]))

(def db
  {:classname   "org.h2.Driver"
   :subprotocol "h2"
   :subname      "~/techwall"})

(def ^{:private true} migrations
  ["CREATE TABLE MIGRATIONS (ID INT PRIMARY KEY, EXECUTION_DATETIME TIMESTAMP AS NOW() NOT NULL)"

   "CREATE TABLE WALLS (ID INT PRIMARY KEY AUTO_INCREMENT, NAME VARCHAR NOT NULL)"
   "CREATE UNIQUE INDEX IDX_WALLS_NAME ON WALLS (NAME)"

   "CREATE TABLE CATEGORIES (ID INT PRIMARY KEY AUTO_INCREMENT, NAME VARCHAR NOT NULL, ORDERING INT NOT NULL)"
   " ALTER TABLE CATEGORIES ADD CONSTRAINT UQ_CATEGORIES_NAME UNIQUE (NAME)"
   " ALTER TABLE CATEGORIES ADD CONSTRAINT UQ_CATEGORIES_ORDERING UNIQUE (ORDERING)"
   "INSERT INTO  CATEGORIES (NAME, ORDERING) VALUES ('Radical', 1), ('Tentative', 2), ('Adopted', 3), ('Deprecated', 4), ('Obsolete', 5)"

   "CREATE TABLE TECHNOLOGIES (ID         INT PRIMARY KEY AUTO_INCREMENT,
                               NAME       VARCHAR NOT NULL,
                               UPPER_NAME VARCHAR AS UPPER(NAME))"
   "CREATE UNIQUE INDEX IDX_TECHNOLOGIES_U_NAME ON TECHNOLOGIES (UPPER_NAME)"

   "CREATE TABLE TRANSITIONS (ID                INT PRIMARY KEY AUTO_INCREMENT,
                              ORDERING          INT NOT NULL AUTO_INCREMENT,
                              WALL_ID           INT NOT NULL,
                              CATEGORY_ID       INT NOT NULL,
                              TECHNOLOGY_ID     INT NOT NULL,
                              ADDED             INT NOT NULL,
                              CREATION_DATETIME TIMESTAMP AS NOW() NOT NULL)"
   " ALTER TABLE TRANSITIONS ADD CONSTRAINT FK_TRANSITIONS_WALLS FOREIGN KEY (WALL_ID) REFERENCES WALLS (ID)"
   " ALTER TABLE TRANSITIONS ADD CONSTRAINT FK_TRANSITIONS_CATEGORIES FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORIES (ID)"
   " ALTER TABLE TRANSITIONS ADD CONSTRAINT FK_TRANSITIONS_TECHNOLOGIES FOREIGN KEY (TECHNOLOGY_ID) REFERENCES TECHNOLOGIES (ID)"
   " ALTER TABLE TRANSITIONS ADD CONSTRAINT CHK_TRANSITIONS_ADDED CHECK ADDED IN (1, 2)"
   " ALTER TABLE TRANSITIONS ADD CONSTRAINT UQ_TECHNOLOGIES UNIQUE (ORDERING, WALL_ID, TECHNOLOGY_ID)"
   "CREATE INDEX IDX_TRANSITIONS_TECH ON TRANSITIONS (WALL_ID, TECHNOLOGY_ID)"
   "CREATE INDEX IDX_TRANSITIONS_WALL ON TRANSITIONS (WALL_ID, ORDERING, ADDED)"
   ])

(defn- next-upgrade-number []
  (if (-> (jdbc/find-connection) .getMetaData (.getTables nil nil "MIGRATIONS" nil) .next)
    (jdbc/with-query-results rows ["SELECT MAX(id) + 1 AS migration_no FROM MIGRATIONS"] (:migration_no (first rows)))
    0))

(defn create []
  (jdbc/with-connection db
    (loop [idx (next-upgrade-number)]
      (if (< idx (count migrations))
        (do (jdbc/transaction
              (jdbc/do-commands
                (migrations idx)
                (str "INSERT INTO MIGRATIONS (id) VALUES (" idx ")")))
          (recur (inc idx)))))))


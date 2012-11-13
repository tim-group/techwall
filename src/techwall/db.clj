(ns techwall.db
  (:require [clojure.java.jdbc :as jdbc]
            [techwall.schema :as schema]))

(schema/create)

(defn prepare-select [sql]
  (jdbc/prepare-statement schema/db sql {:result-type :forward-only :concurrency :read-only}))

(defn prepare-insert [sql]
  (jdbc/with-connection schema/db
    (jdbc/prepare-statement (jdbc/connection) sql :return-keys true)))

(defn do-insert [stmt & params]
  ((keyword "scope_identity()")
    (jdbc/with-connection schema/db (jdbc/do-prepared-return-keys stmt params))))

(defn do-update [stmt & params]
  (jdbc/with-connection schema/db (jdbc/do-prepared stmt params)))

(defn select [query & params]
  (jdbc/with-connection schema/db
    (jdbc/with-query-results res
      (into (conj [] query) params) 
      (into [] res))))

(defn select-one [& queryandparams] (first (apply select queryandparams)))

(defn backup-to-file [] 
  (let [file (java.io.File/createTempFile "techwall-backup" ".zip")]
    (jdbc/with-connection schema/db (jdbc/do-commands (str "BACKUP TO '" (.getPath file) "'")))
    file))
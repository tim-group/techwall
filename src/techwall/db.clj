(ns techwall.db
  (:require [clojureql.core :as ql]
            [clojure.java.jdbc :as jdbc]
            [techwall.schema :as schema]))

(schema/create)
(ql/open-global schema/db)

(defn prepare-select [sql]
  (jdbc/prepare-statement schema/db sql {:result-type :forward-only :concurrency :read-only}))

(defn prepare-insert [sql]
  (jdbc/with-connection schema/db
    (jdbc/prepare-statement (jdbc/connection) sql :return-keys true)))

(defn do-insert [stmt & params]
  ((keyword "scope_identity()")
    (jdbc/with-connection schema/db (jdbc/do-prepared-return-keys stmt params))))

(defn select [query & params]
  (jdbc/with-connection schema/db
    (jdbc/with-query-results res
      (into (conj [] query) params) 
      (into [] res))))

(defn select-one [& queryandparams] (first (apply select queryandparams)))

(if (= 0 (count @(ql/table :walls)))
  (let [ins (ql/conj! (ql/table :walls) {:name "Example Wall"})]
    (println (:last-index (meta ins)))))

(if (= 0 (count @(ql/table :categories)))
  (ql/conj! (ql/table :categories) [{:name "Radical"} {:name "Tentative"} {:name "Adopted"} {:name "Deprecated"} {:name "Obsolete"}]))

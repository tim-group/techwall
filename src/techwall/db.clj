(ns techwall.db
  (:require [clojureql.core :as ql]
            [clojure.java.jdbc :as jdbc]
            [techwall.schema :as schema]))

(schema/create)
(ql/open-global schema/db)

(defn db-select [query & params]
  (jdbc/with-connection schema/db
    (jdbc/with-query-results res
      (into (conj []  query) params) 
      (into [] res))))

(if (= 0 (count @(ql/table :walls)))
  (let [ins (ql/conj! (ql/table :walls) {:name "Example Wall"})]
    (println (:last-index (meta ins)))))

(if (= 0 (count @(ql/table :categories)))
  (ql/conj! (ql/table :categories) [{:name "Radical"} {:name "Tentative"} {:name "Adopted"} {:name "Deprecated"} {:name "Obsolete"}]))

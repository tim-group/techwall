(ns techwall.db
  (:require [clojureql.core :as ql])
  (:require [techwall.schema :as schema]))

(schema/create)
(ql/open-global schema/db)

(if (= 0 (count @(ql/table :walls)))
  (let [ins (ql/conj! (ql/table :walls) {:name "Example Wall"})]
    (println (:last-index (meta ins)))))

(if (= 0 (count @(ql/table :categories)))
  (ql/conj! (ql/table :categories) [{:name "Radical"} {:name "Tentative"} {:name "Adopted"} {:name "Deprecated"} {:name "Obsolete"}]))

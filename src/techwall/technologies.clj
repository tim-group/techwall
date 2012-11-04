(ns techwall.technologies
  (:use techwall.db)
  (:require [clojureql.core :as ql]))

(defn all [] @(ql/table :technologies))

(defn find-or-make
  ([id name]
    (if-let [actual-name @(ql/pick (ql/select (ql/table :technologies) (ql/where (= :id id))) :name)]
      {:id id :name actual-name}
      (find-or-make name)))
  ([name]
    (if-let [[id actual-name] @(ql/pick (ql/select (ql/table :technologies) (ql/where (= :upper/name (.toUpperCase name)))) [:id :name])]
      {:id id :name actual-name}
      (let [insert (ql/conj! (ql/table :technologies) {:name name})
            id (:last-index (meta insert))]
        {:id id :name name}))))


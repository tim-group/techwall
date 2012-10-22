(ns techwall.schema
  (:require [clojure.java.jdbc :as jdbc]))

(def db
  {:classname   "org.h2.Driver"
   :subprotocol "h2"
   :subname      "~/techwall"})

(jdbc/with-connection db
  (jdbc/transaction
    (jdbc/do-commands
      "CREATE TABLE walls (id INT PRIMARY KEY, name VARCHAR(255))"
      "CREATE TABLE categories (id INT PRIMARY KEY, name VARCHAR(255))"
      "CREATE TABLE technologies (id INT PRIMARY KEY, name VARCHAR(255))"
      "CREATE TABLE transitions (id INT PRIMARY KEY, creation_datetime TIMESTAMP)"
      "CREATE TABLE links (id INT PRIMARY KEY, transition_id INT, wall_id INT, category_id INT, technology_id INT)")))

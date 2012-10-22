(ns techwall.db
  (:require [clojureql.core :as ql]))

(def db
 {:classname   "org.h2.Driver"
  :subprotocol "h2"
  :user         ""
  :password     ""
  :subname      "~/test"})

(ql/open-global db)
@(ql/table "INFORMATION_SCHEMA.SCHEMATA")
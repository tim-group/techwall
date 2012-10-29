(ns techwall.db
  (:require [clojureql.core :as ql])
  (:require [techwall.schema :as schema]))

(schema/create)

(ql/open-global schema/db)

@(ql/table :walls)
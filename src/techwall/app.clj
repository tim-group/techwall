(ns techwall.app
  (:use ring.adapter.simpleweb)
  (:require [techwall.handler :as handler])
  (:gen-class))

(defn -main [& args]
  (let [port (try (Integer/parseInt (first args)) (catch Exception e 3000))]
    (run-simpleweb handler/app {:port port})))

(ns techwall.test.handler
  (:use clojure.test
        ring.mock.request  
        techwall.handler))

(deftest test-app
  (testing "main route"
    (let [response (app (request :get "/"))]
      (is (= (:status response) 302))
      (is (= (get (:headers response) "Location") "/index.html"))))
  
  (testing "index page"
    (let [response (app (request :get "/index.html"))]
      (is (= (:status response) 200))))
  
  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= (:status response) 404)))))
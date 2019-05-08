(ns simple-statsd.ring-test
  (:require [clojure.test :refer :all]
            [simple-statsd.ring :as ring]
            [simple-statsd.core :as core]))

(defn- remove-index
  "Removes the nth element from a sequence."
  [sequence n]
  (let [v (vec sequence)]
    (concat (subvec v 0 n)
            (subvec v (inc n)))))

(deftest statsd-reporting-test
  (testing "when the handler is instrumented with named-handler"
    (testing "timing is called with the correct arguments"
      (let [timing-args (atom nil)
            wrapped-handler (-> (constantly {:status 200 :body "ok"})
                                (ring/named-handler "foo-api-name")
                                (ring/wrap-statsd-reporting "foo-app-name"))]
        (with-redefs [core/timing (fn [& args]
                                    (reset! timing-args (vec args)))]
          (wrapped-handler {}))
        (is (= ["foo-app-name.api.response-time.foo-api-name"
                {:api-name "foo-api-name"
                 :status   200
                 :app-name "foo-app-name"}]
               (remove-index @timing-args 1)))))

    (testing "increment is called with the correct arguments"
      (let [increment-args (atom nil)
            wrapped-handler (-> (constantly {:status 200 :body "ok"})
                                (ring/named-handler "foo-api-name")
                                (ring/wrap-statsd-reporting "foo-app-name"))]
        (with-redefs [core/increment (fn [& args]
                                       (reset! increment-args (vec args)))]
          (wrapped-handler {}))
        (is (= ["foo-app-name.api.count.foo-api-name"
                {:api-name "foo-api-name"
                 :status   200
                 :app-name "foo-app-name"}]
               @increment-args)))))

  (testing "when the handler is not instrumented with named-handler"
    (testing "timing is not called"
      (let [timing-called? (atom false)
            wrapped-handler (-> (constantly {:status 200 :body "ok"})
                                (ring/wrap-statsd-reporting "foo-app-name"))]
        (with-redefs [core/timing (fn [& _]
                                    (reset! timing-called? true))]
          (wrapped-handler {}))
        (is (not @timing-called?)))

      (testing "increment is not called"
        (let [increment-called? (atom false)
              wrapped-handler (-> (constantly {:status 200 :body "ok"})
                                  (ring/wrap-statsd-reporting "foo-app-name"))]
          (with-redefs [core/increment (fn [& _]
                                         (reset! increment-called? true))]
            (wrapped-handler {}))
          (is (not @increment-called?)))))))

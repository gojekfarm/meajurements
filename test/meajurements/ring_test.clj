(ns meajurements.ring-test
  (:require [clojure.test :refer :all]
            [meajurements.ring :as ring]
            [meajurements.core :as core]))

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
                                (ring/wrap-statsd-reporting "foo-app-name" "foo-api-name"))]
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
                                (ring/wrap-statsd-reporting "foo-app-name" "foo-api-name"))]
        (with-redefs [core/increment (fn [& args]
                                       (reset! increment-args (vec args)))]
          (wrapped-handler {}))
        (is (= ["foo-app-name.api.count.foo-api-name"
                {:api-name "foo-api-name"
                 :status   200
                 :app-name "foo-app-name"}]
               @increment-args))))))

(deftest statsd-reporting-custom-tags-test
  (testing "when the handler is instrumented with named-handler"
    (testing "timing is called with the correct arguments"
      (let [timing-args (atom nil)
            wrapped-handler (-> (constantly {:status 200 :body "ok"})
                                (ring/wrap-statsd-reporting "foo-app-name" "foo-api-name" (fn [req _]
                                                                                            (select-keys req [:country-id]))))]
        (with-redefs [core/timing (fn [& args]
                                    (reset! timing-args (vec args)))]
          (wrapped-handler {:country-id "SG"}))
        (is (= ["foo-app-name.api.response-time.foo-api-name"
                {:api-name "foo-api-name"
                 :status   200
                 :app-name "foo-app-name"
                 :country-id "SG"}]
               (remove-index @timing-args 1)))))

    (testing "increment is called with the correct arguments"
      (let [increment-args (atom nil)
            wrapped-handler (-> (constantly {:status 200 :body "ok"})
                                (ring/wrap-statsd-reporting "foo-app-name" "foo-api-name" (fn [req _]
                                                                                            (select-keys req [:country-id]))))]
        (with-redefs [core/increment (fn [& args]
                                       (reset! increment-args (vec args)))]
          (wrapped-handler {:country-id "SG"}))
        (is (= ["foo-app-name.api.count.foo-api-name"
                {:api-name "foo-api-name"
                 :status   200
                 :app-name "foo-app-name"
                 :country-id "SG"}]
               @increment-args))))))

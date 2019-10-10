(ns meajurements.ring
  (:require [meajurements.core :as core]))

(defn wrap-statsd-reporting
  "Instruments response times and API status codes. Wrap your handler function with this."
  [handler app-name api-name]
  (fn [request]
    (let [start-time (System/nanoTime)
          {:keys [status] :as response} (handler request)]
      (core/timing (str app-name ".api.response-time." api-name)
                   (quot (- (System/nanoTime) start-time) 1000000)
                   {:api-name api-name
                    :status   status
                    :app-name app-name})
      (core/increment (str app-name ".api.count." api-name)
                      {:api-name api-name
                       :status   status
                       :app-name app-name})
      response)))

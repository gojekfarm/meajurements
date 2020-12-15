(ns meajurements.ring
  (:require [meajurements.core :as core]))

(defn wrap-statsd-reporting
  "Instruments response times and API status codes. Wrap your handler function with this."
  ([handler app-name api-name]
   (wrap-statsd-reporting handler app-name api-name (fn [_ _] {})))
  ([handler app-name api-name tags-fn]
   (fn [request]
     (let [start-time (System/nanoTime)
           {:keys [status] :as response} (handler request)
           elapsed-time (quot (- (System/nanoTime) start-time) 1000000)
           tags (merge {:api-name api-name
                        :status   status
                        :app-name app-name}
                       (tags-fn request response))]
       (core/timing (str app-name ".api.response-time." api-name)
                    elapsed-time
                    tags)
       (core/increment (str app-name ".api.count." api-name)
                       tags)
       response))))

(ns simple-statsd.ring
  (:require [simple-statsd.core :as core]))

(defn named-handler
  "Adds an API name to the request/response for reporting purposes.
  Wrap handlers with this function in routes.
  Only handlers wrapped by this function will be instrumented."
  [handler api-name]
  (fn [request]
    (let [response (handler (assoc request :api-name api-name))]
      (assoc response :api-name api-name))))

(defn wrap-statsd-reporting
  "Instruments response times and API status codes.
  Requires the handler to be wrapped with named-handler."
  [handler app-name]
  (fn [request]
    (let [start-time (System/currentTimeMillis)
          {:keys [status api-name] :as response} (handler request)]
      (when api-name
        (core/timing (str app-name ".api.response-time." api-name)
                     (- (System/currentTimeMillis) start-time)
                     {:api-name api-name
                      :status   status
                      :app-name app-name})
        (core/increment (str app-name ".api.count." api-name)
                        {:api-name api-name
                         :status   status
                         :app-name app-name}))
      response)))

(ns meajurements.core
  (:require [clj-statsd :as statsd]))

(defn setup
  [host port]
  (statsd/setup host port))

(defn- coerce-value
  [v]
  (if (or (keyword? v)
          (symbol? v))
    (name v)
    v))

(defn- build-tags
  [tags]
  (map (fn [[key value]]
         (str (name key) ":" (coerce-value value)))
       tags))

(defn increment
  ([metric]
   (increment metric {}))
  ([metric tags]
   (statsd/increment metric 1 1.0 (build-tags tags))))

(defn decrement
  ([metric]
   (decrement metric {}))
  ([metric tags]
   (statsd/decrement metric 1 1.0 (build-tags tags))))

(defn timing
  ([metric-name time-ms]
   (timing metric-name time-ms {}))
  ([metric-name time-ms tags]
   (statsd/timing metric-name time-ms 1.0 (build-tags tags))))

(defn gauge
  ([metric value]
   (gauge metric value {}))
  ([metric value tags]
   (statsd/gauge metric value 1.0 (build-tags tags))))

(defn time-fn
  [f metric tags]
  (let [start-time (System/nanoTime)
        response (f)
        response-time (- (System/nanoTime) start-time)]
    (timing metric (quot response-time 1000000) tags)
    response))

(defmacro with-timing
  [metric-name tags & body]
  `(time-fn (fn []
              ~@body)
            ~metric-name
            ~tags))

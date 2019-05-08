(ns simple-statsd.core
  (:require [clj-statsd :as statsd]))

(defn setup
  [host port]
  (statsd/setup host port))

(defn- build-tags
  [tags]
  (map (fn [[key value]]
         (str (name key) ":" value))
       tags))

(defn increment
  ([metric]
   (increment metric {}))
  ([metric tags]
   (statsd/increment metric 1 1.0 (build-tags tags))))

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

# meajurements

<p align="center">
  <a href="https://travis-ci.com/gojek/ziggurat">
    <img src="https://travis-ci.com/gojekfarm/meajurements.svg?branch=master" alt="Build Status" />
  </a>
  <a href='https://clojars.org/tech.gojek/ziggurat'>
    <img src='https://img.shields.io/clojars/v/tech.gojek/meajurements' alt='Clojars Project' />
  </a>
</p>

A wrapper over https://github.com/pyr/clj-statsd that also supports pushing tags in the `dogstatsd` format. It also includes a Ring middleware which instruments response times, HTTP response codes and throughput for your HTTP APIs.

## Usage
Add `[tech.gojek/meajurements "3.0.0"]` to your dependencies.
Call `meajurements.core/setup` once to configure the `statsd` host and port.

### Basic reporting
Use the functions in `meajurements.core` to report metrics to statsd. Tags can optionally be passed as maps.

```clojure
(require '[meajurements.core :as statsd])
(statsd/timing "foo-service.some-time"
               250)
(statsd/increment "foo-service.some-event.count"
                  {:app-name "foo-service"})
(statsd/gauge "foo-service.memory-usage" 500 {:app-name "foo-service"})
```

### Ring middleware for instrumenting APIs
`meajurements.ring` has a middleware that will instrument response times and throughput per response code for your HTTP APIs.

Wrap your handler with `meajurements.ring/wrap-statsd-reporting` with a metric prefix and an API name to report metrics to `statsd`.

Example using `bidi`:
```clojure
(require '[meajurements.ring :as statsd-ring])

(def ^:private routes
  ;; Wrap your handler and give it a name
  ["/" {"ping" (statsd-ring/wrap-statsd-reporting (constantly {:status 200
                                                               :body   "pong"})
                                                  "my-app" "ping")}])

(def handler (-> routes
                 bidi.ring/make-handler
                 wrap-coerce-key-names
                 wrap-api-middleware
                 wrap-handle-exceptions))
```

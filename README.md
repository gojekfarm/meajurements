# simple-statsd

A wrapper over the statsd API that also supports pushing tags in the `dogstatsd` format (which will work with the GO-JEK telegraf instances).

Does not add any additional abstractions on top of what `statsd`/`telegraf` supports.

## Usage
Add `[tech.gojek/simple-statsd "3.0.0"]` to your dependencies.

Call `simple-statsd.core/setup` once to configure the `statsd` host and port.


### Basic reporting
Use the functions in `simple-statsd.core` to report metrics to statsd. Tags can optionally be passed as maps.

```clojure
(require '[simple-statsd.core :as statsd])
(statsd/timing "foo-service.some-time"
               250)
(statsd/increment "foo-service.some-event.count"
                  {:app-name "foo-service"})
(statsd/gauge "foo-service.memory-usage" 500 {:app-name "foo-service"})
```

### Ring middleware for instrumenting APIs
`simple-statsd.ring` has a middleware that will instrument response times and throughput per response code for your HTTP APIs.

Wrap your handler with `simple-statsd.ring/wrap-statsd-reporting` with a metric prefix and an API name to report metrics to `statsd`.

Example using `bidi`:
```clojure
(require '[simple-statsd.ring :as statsd-ring])

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

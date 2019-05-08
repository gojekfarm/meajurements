# simple-statsd

A wrapper over the statsd API that also supports pushing tags in the `dogstatsd` format (which will work with the GO-JEK telegraf instances).

Does not add any additional abstractions on top of what `statsd`/`telegraf` supports.

## Usage
Add `[tech.gojek/simple-statsd "1.0.0"]` to your dependencies.


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
`simple-statsd.ring` has two middlewares that will instrument response times and throughput per response code for your HTTP APIs.

Wrap your handler with `simple-statsd.ring/named-handler` and give it a name, to be used in the `statsd` metric names.

Further down the middleware chain (possibly with other middleware in between), wrap your handler with `simple-statsd.ring/wrap-statsd-reporting` with a metric prefix to report metrics to `statsd`. Only handlers wrapped with `named-handler` will be instrumented.

Example using `bidi`:
```clojure
(require '[simple-statsd.ring :as statsd-ring])

(def ^:private routes
  ;; Wrap your handler with named-handler and give it a name
  ["/" {"ping" (statsd-ring/named-handler (constantly {:status 200
                                           :body   "pong"})
                                          "ping")}])

(def handler (-> routes
                 bidi.ring/make-handler
                 wrap-coerce-key-names
                 wrap-api-middleware
                 wrap-handle-exceptions
                 ;; After applying other middleware, use the wrap-statsd-reporting middleware
                 statsd-ring/wrap-statsd-reporting))
```

(defproject tech.gojek/meajurements "3.2.0"
  :description "A statsd client that supports tagged metrics in the dogstatsd format"
  :url "https://github.com/gojekfarm/meajurements"
  :license {:name "Apache License, Version 2.0"
            :url  "https://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [clj-statsd "0.4.0"]]
  :deploy-repositories [["clojars" {:url           "https://clojars.org/repo"
                                    :username      :env/clojars_username
                                    :password      :env/clojars_password
                                    :sign-releases false}]]
  :profiles {:uberjar {:aot         :all
                       :global-vars {*warn-on-reflection* true}}})

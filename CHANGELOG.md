# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## 3.0.0
* Coerce tag values using `name` if they are keywords. This means that keyword tag values won't be prefixed with `:` anymore.

## 2.1.0
* Add `time-fn` and `with-timing` macros to time blocks of code.
* Use `System/nanoTime` in the ring middleware for more accurate timing.

## 2.0.0
* Change the `simple-statsd.ring` functionality. Move away from the `named-handler` way of doing things, because it could interfere with other middleware.

## 1.0.0
* Initial release

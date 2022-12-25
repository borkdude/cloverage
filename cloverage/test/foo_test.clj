(ns foo-test
  (:require [clojure.test :as t]
            [foo]))

(t/deftest foo-test
  (foo/foo 1))

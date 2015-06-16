(ns wiki-search-xml.t-parser
  (:require [clojure.core.async :refer [go >! >!!]]
            [wiki-search-xml.parser :refer :all]
            [wiki-search-xml.system :as sys]
            [wiki-search-xml.common :as common]
            [midje.sweet :refer :all]))

(facts "about `parser`"
  (let [config-map (sys/make-config)
        system (sys/new-system config-map)]

    (fact "key in system never nil"
      (:wsx-parser system) => some?)

    (fact "unstarted, should have nil dependencies and instance"
      (get-in system [:wsx-parser :sub-parse]) => nil
      (get-in system [:wsx-parser :bus]) => nil)
    
    (fact "when started should have non-nil dependencies and instance"
      (common/with-component-start system
        (get-in __started__ [:wsx-parser :sub-parse]) => some?
        (get-in __started__ [:wsx-parser :bus]) => some?))

    (fact "when started then stopped, should have nil dependencies and instance"
      (let [stopped (common/with-component-start system :test)]
        (get-in stopped [:wsx-parser :sub-parse]) => nil
        (get-in stopped [:wsx-parser :bus]) => nil))

    )
  )



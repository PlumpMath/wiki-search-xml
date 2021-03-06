(ns ^{:skip-aot true} user
  (:require[clojure.tools.namespace.repl :refer [refresh refresh-all]]
           [clojure.pprint :refer [pprint]]
           [com.stuartsierra.component :as component]
           [wiki-search-xml.system :refer [new-system make-config]]
           [wiki-search-xml.daemon :as daemon]
           [midje.repl :as mr]))

(defn ppsys
  "Pretty prints the system var"
  []
  (if-let [sys daemon/system]
    (pprint sys)
    (println "System not initialized yet")))

(defn init
  "Constructs the current development system."
  []
  (println "Initializing system...")
  (daemon/-init :fake-instance :fake-daemon-context))

(defn start
  "Starts the current development system."
  []
  (println "Starting system...")
  (daemon/-start :fake-instance))

(defn stop
  "Shuts down and destroys the current development system."
  []
  (println "Stopping system...")
  (daemon/-stop :fake-instance))

(defn go
  "Initializes the current development system and starts it running."
  []
  (println "Go!")
  (init)
  (start))

(defn reset []
  (println "Resetting system...")
  (stop)
  (refresh :after 'user/go))

(defn auto
  [& opts]
  (apply midje.repl/autotest opts))



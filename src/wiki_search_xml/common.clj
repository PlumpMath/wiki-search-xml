(ns wiki-search-xml.common
  (:require [clojure.pprint :as pprint]
            [clojure.tools.logging :as log]
            [clojure.core.async :as async]
            [slingshot.slingshot :refer [try+ get-throw-context]]
            [com.stuartsierra.component :as component]
            [wiki-search-xml.core :as core]))

(defn lazy?
  [xs]
  (condp instance? xs
    clojure.lang.LazySeq true
    clojure.lang.Cons (lazy? (rest xs))
    java.lang.Object false))

(defn str-pprint
  "Pretty prints object to a string."
  [object]
  (with-out-str (pprint/pprint object)))

(defrecord DummyComponent [name deps]
  component/Lifecycle
  (start [this] (do (log/debug "DummyComponent " name "started") (assoc this :started true)))
  (stop [this] (do (log/debug "DummyComponent " name "stopped") (assoc this :started false))))

(defn new-dummy-component [name & deps]
  (component/using (->DummyComponent name deps)
    (vec deps)))

(defmacro with-component-start
  "Starts and stops the component around the execution (do
  body). Exposes the anaphoric symbol started-system! and returns the result
  of body's last sexp. The stopped system is not returned."
  [component & body]
  `(let [~'__started__ (component/start ~component)]
     (try+
      (do ~@body)
      (catch Object caught#
        (let [thr# (:throwable (get-throw-context caught#))]
          (log/error thr# "with-component-start error in body")))
      (finally (component/stop ~'__started__)))))

(defn no-server-system
  "Strips a system from its server side"
  [system]
  (dissoc (dissoc system :wsx-server) :wsx-handler))

(defn conf->buffer
  "Returns the correct kind of buffer instance based
  on :buffer-type (can be either :dropping or :sliding)
  and :buffer-size."
  [conf]
  (let [{:keys [buffer-size buffer-type]} conf]
    (case buffer-type
      :dropping (async/dropping-buffer (or buffer-size 1))
      :sliding (async/sliding-buffer (or buffer-size 1))
      (async/buffer (or buffer-size 1)))))

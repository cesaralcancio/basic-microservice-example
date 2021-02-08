(ns basic-microservice-example.components.dummy-config
  (:use clojure.pprint)
  (:require [com.stuartsierra.component :as component]))

(defrecord DummyConfig [config, xpto]
  component/Lifecycle
  (start [this] (let []
                  (println "Starting Dummy Config -> ")
                  (pprint config)
                  (println "Starting Dummy This -> ")
                  (pprint this)
                  this))
  (stop [this] this))

(defn new-config [config-map] (->DummyConfig config-map "xpto"))
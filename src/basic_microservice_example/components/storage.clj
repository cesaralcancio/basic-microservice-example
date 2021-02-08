(ns basic-microservice-example.components.storage
  (:use clojure.pprint)
  (:require [com.stuartsierra.component :as component]
            [basic-microservice-example.protocols.storage-client :as storage-client]))

(defrecord InMemoryStorage [storage]
  component/Lifecycle
  (start [this] (let []
                  (println "Running start InMemoryStorage Storage -> ")
                  (pprint storage)
                  (println "Running start InMemoryStorage This -> ")
                  (pprint this)
                  this))
  (stop [this]
    (reset! storage {})
    this)

  storage-client/StorageClient
  (read-all [_this] @storage)
  (put! [_this update-fn] (swap! storage update-fn))
  (clear-all! [_this] (reset! storage {})))

(defn new-in-memory []
  (->InMemoryStorage (atom {})))

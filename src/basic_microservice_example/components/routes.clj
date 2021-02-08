(ns basic-microservice-example.components.routes
  (:use clojure.pprint)
  (:require [com.stuartsierra.component :as component]))

(defrecord Routes [routes]
  component/Lifecycle
  (start [this]
    (let [new-router (assoc this :routes routes)]
      (println "Start Routes routes -> ")
      (pprint routes)
      (println "Start Routes this -> ")
      (pprint this)
      (println "Start Routes new-router -> ")
      (pprint new-router)
      new-router
      ))
  (stop [this] (dissoc this :routes)))

(defn new-routes [routes] (map->Routes {:routes routes}))

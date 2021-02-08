(ns basic-microservice-example.components.meucomponente
  (:use clojure.pprint)
  (:require [com.stuartsierra.component :as component]))

(defrecord MeuComponente [config storage param1]
  component/Lifecycle
  (start [this] (let []
                  (println "Running start MeuComponente Config/Storage/Param1 -> ")
                  (pprint config)
                  (pprint storage)
                  (pprint param1)
                  (println "Running start MeuComponente This -> ")
                  (pprint this)
                  (assoc this :meu-componente {:nome "Cesar"})))
  (stop [this] this))

(defn constructor []
  (map->MeuComponente {:param1 "value1"}))

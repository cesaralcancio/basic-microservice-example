(ns basic-microservice-example.components.meucomponentedois
  (:use clojure.pprint)
  (:require [com.stuartsierra.component :as component]))

; Testando o this do meu componente
; No final o this eh o mesmo que os argumentos, tudo que for injetado como dependencia
; do meu component, vai ser "assoc" no meu componete
; junto com os argumentos que também vai ser um assoc
(defrecord MeuComponenteDois [seilaeu]
  component/Lifecycle
  (start [this] (let []
                  (println "Running start MeuComponenteDois this -> ")
                  (pprint this)
                  this))
  (stop [this] this))

(defn constructor []
  (->MeuComponenteDois {:sei "la" :eu "eu"}))

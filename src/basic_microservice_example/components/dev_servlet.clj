(ns basic-microservice-example.components.dev-servlet
  (:use clojure.pprint)
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as bootstrap]
            [io.pedestal.service-tools.dev :as dev]))

(defrecord DevServlet [service]
  component/Lifecycle
  (start [this]
    (let [
          ;new-mapping (assoc this :instance (-> service
          ;                                      :runnable-service
          ;                                      ;; do not block thread that starts web server
          ;                                      (assoc ::bootstrap/join? false)
          ;                                      bootstrap/create-server
          ;                                      bootstrap/start))
          runnable-service-from-service (:runnable-service service)
          runnable-service-with-bootstrap (assoc runnable-service-from-service ::bootstrap/join? false)
          server-created (bootstrap/create-server runnable-service-with-bootstrap)
          bootstrap-started (bootstrap/start server-created)
          this-with-bootstrap-started (assoc this :instance bootstrap-started)
          ]
      (println "Start DevServlet init!!!!!")
      (println "Start DevServlet service -> ")
      (pprint service)
      (println "Start DevServlet this -> ")
      (pprint this)
      (println "Start DevServlet runnable-service-from-service -> ")
      (pprint runnable-service-from-service)
      (println "Start DevServlet runnable-service-with-bootstrap -> ")
      (pprint runnable-service-with-bootstrap)
      (println "Start DevServlet server-created -> ")
      (pprint server-created)
      (println "Start DevServlet bootstrap-started -> ")
      (pprint bootstrap-started)
      (println "Start DevServlet this-with-bootstrap-started -> ")
      (pprint this-with-bootstrap-started)
      (println "Start DevServlet end!!!!!")
      this-with-bootstrap-started))
  (stop [this]
    (bootstrap/stop (:instance this))
    (dissoc this :instance))

  Object
  (toString [_] "<DevServlet>"))

(defn new-servlet [] (map->DevServlet {}))

(defmethod print-method DevServlet [v ^java.io.Writer w]
  (.write w "<DevServlet>"))

(defn main [start-fn & _args]
  (start-fn {:mode :embedded}))                             ; lein run

(defn run-dev [start-fn & _args]
  ;; The entry-point for 'lein run-dev', 'lein with-profile +repl-start'
  (dev/watch)                                               ;; auto-reload namespaces only in run-dev / repl-start
  (start-fn {:mode :embedded}))

;(defprotocol MyProtocol)
;(defrecord MyType MyProto∏col [a b])
;(def foo (->MyType [1 2 3] [4 5 6]))
;(def bar (map->MyType [1 2 3] [4 5 6]))
;(pprint foo)
;(println foo)
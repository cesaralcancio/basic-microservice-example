(ns basic-microservice-example.components.service
  (:use clojure.pprint)
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.interceptor.helpers :refer [before]]
            [io.pedestal.interceptor :as inter]
            [io.pedestal.http.route :as route]
            [io.pedestal.http :as bootstrap]))

; testar assoc-in
(def users [{:name "James" :age 26} {:name "John" :age 43}])
(assoc-in users [1 :age] 44)

; testar update-in
(update-in users [1 :age] inc)

; testando como funciona a cricao de um interceptor
(def p {:name "James" :age 1 ::bootstrap/interceptors ["abc"]})
(update-in p [:age] inc)
(pprint p)
(defn update-in-p [current-array]
  (let [funcao-para-associar-no-contexto (fn [context]
                                           (assoc-in context
                                                     [:request :components]
                                                     "adicionado1"))
        novo-interceptor (inter/interceptor {:name  :meu-interceptor-melhor
                                             :enter funcao-para-associar-no-contexto})
        nova-sequencia (cons novo-interceptor current-array)
        novo-vector (vec nova-sequencia)]
    (println current-array)
    (println nova-sequencia)
    (println novo-vector)
    novo-vector
    ))
(def p-updated (update-in p [::bootstrap/interceptors] update-in-p))
(pprint p-updated)

(defn- add-system [this]
  (let []
    (println "Running the add-system....")
    (before (fn [context] (let []
                            (println "Running the lambda function to assoc-in context...")
                            (assoc-in context [:request :components] this)
                            )))))

(defn system-interceptors
  "Extend to service's interceptors to include one to inject the components
   into the request object"
  [service-map this]
  (let [updated (update-in service-map
                           [::bootstrap/interceptors]
                           #(vec (->> % (cons (add-system this)))))]
    (println "service-map ->")
    (pprint service-map)
    (println "updated ->")
    (pprint updated)
    updated
    ))

(defn base-service [routes port]
  {:env                      :prod
   ::bootstrap/router        :prefix-tree
   ::bootstrap/routes        #(route/expand-routes (deref routes))
   ::bootstrap/resource-path "/public"
   ::bootstrap/type          :jetty
   ::bootstrap/port          port})

(defn prod-init [service-map]
  (bootstrap/default-interceptors service-map))

(defn dev-init [service-map]
  (-> service-map
      (merge {:env                        :dev
              ;; do not block thread that starts web server
              ::bootstrap/join?           false
              ;; Content Security Policy (CSP) is mostly turned off in dev mode
              ::bootstrap/secure-headers  {:content-security-policy-settings {:object-src "none"}}
              ;; all origins are allowed in dev mode
              ::bootstrap/allowed-origins {:creds true :allowed-origins (constantly true)}})
      ;; Wire up interceptor chains
      bootstrap/default-interceptors
      bootstrap/dev-interceptors))

(defn runnable-service [config routes this]
  (let [env (:environment config)
        port (:dev-port config)
        service-conf (base-service routes port)
        initial-config (if (= :prod env)
                         (prod-init service-conf)
                         (dev-init service-conf))]
    (println "runnable-service with (config/routes/this) -> ")
    (pprint config)
    (pprint routes)
    (pprint this)
    (println "initial-config")
    (pprint initial-config)
    (system-interceptors initial-config this)
    ;(-> (if (= :prod env)
    ;      (prod-init service-conf)
    ;      (dev-init service-conf))
    ;    (system-interceptors this))
    ))

(defrecord Service [config routes]
  component/Lifecycle
  (start [this]
    (let [runnable-service-return (runnable-service (:config config) (:routes routes) this)
          new-routes (assoc this :runnable-service runnable-service-return)]
      (println "Start service -> ")
      (pprint config)
      (pprint routes)
      (println "Start service this -> ")
      (pprint this)
      (println "Start service runnable-service-return -> ")
      (pprint runnable-service-return)
      (println "Start service new-routes -> ")
      (pprint new-routes)
      new-routes
      ))

  (stop [this]
    (dissoc this :runnable-service))

  Object
  (toString [_] "<Service>"))

(defmethod print-method Service [v ^java.io.Writer w]
  (.write w "<Service>"))

(defn new-service [] (map->Service {}))
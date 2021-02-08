(ns basic-microservice-example.components
  (:refer-clojure :exclude [test])
  (:require [com.stuartsierra.component :as component]
            [basic-microservice-example.components.dummy-config :as config]
            [basic-microservice-example.components.meucomponente :as meu-componente]
            [basic-microservice-example.components.meucomponentedois :as meu-componente-dois]
            [basic-microservice-example.components.debug-logger :as debug-logger]
            [basic-microservice-example.components.storage :as storage]
            [basic-microservice-example.service :as basic-microservice-example.service]
            [basic-microservice-example.components.dev-servlet :as dev-servlet]
            [basic-microservice-example.components.mock-servlet :as mock-servlet]
            [basic-microservice-example.components.mock-http :as mock-http]
            [basic-microservice-example.components.service :as service]
            [basic-microservice-example.components.routes :as routes]
            [basic-microservice-example.components.http-kit :as http-kit]
            [basic-microservice-example.components.system-utils :as system-utils]
            [basic-microservice-example.components.http :as http]
            [schema.core :as s]))

(def base-config-map {:environment :prod
                      :dev-port    8080})

(def local-config-map {:environment :dev
                       :dev-port    8080})

;; all the components that will be available in the pedestal http request map
(def web-app-deps
  [:config :routes :http :storage])

(defn base []
  (component/system-map
    :config (config/new-config base-config-map)
    :storage (component/using (storage/new-in-memory) [:config])

    :meu-componente (component/using (meu-componente/constructor) [:config :storage])
    :meu-componente-dois (component/using (meu-componente-dois/constructor) [:meu-componente])

    :http-impl (http-kit/new-http-client)
    :http (component/using (http/new-http) [:config :storage :http-impl])

    :routes (routes/new-routes #'basic-microservice-example.service/routes)
    :service (component/using (service/new-service) web-app-deps)
    :servlet (component/using (dev-servlet/new-servlet) [:service])))

(defn e2e []
  (s/set-fn-validation! true)
  (merge (base)
         (component/system-map
           :config (config/new-config local-config-map))))

(defn test-system []
  (merge (base)
         (component/system-map
           :config (config/new-config local-config-map)
           :servlet (component/using (mock-servlet/new-servlet) [:service])
           :debug-logger (debug-logger/new-debug-logger)
           :http (component/using (mock-http/new-mock-http) [:config])
           :service (component/using (service/new-service) (conj web-app-deps :debug-logger)))))

(def systems-map
  {:e2e-system   e2e
   :local-system e2e
   :test-system  test-system
   :base-system  base})

(defn create-and-start-system!
  ([] (create-and-start-system! :base-system))
  ([env]
   (system-utils/bootstrap! systems-map env)))

(defn ensure-system-up! [env]
  (or (deref system-utils/system)
      (create-and-start-system! env)))

(defn stop-system! [] (system-utils/stop-components!))

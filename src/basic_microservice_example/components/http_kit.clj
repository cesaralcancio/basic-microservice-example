(ns basic-microservice-example.components.http-kit
  (:use clojure.pprint)
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.client :as http-kit]
            [basic-microservice-example.protocols.http-client :as http-client]))

(def http-kit-keys
  [:url :method :body :oauth-token :user-agent :headers :form-params
   :query-params :keepalive :timeout :filter :multipart :max-redirects
   :follow-redirects :insecure?])

(defrecord HttpKit [storage]
  http-client/HttpClient
  (req! [_ {:keys [url method] :as req-map}]
    ;; Use only the keys that http-kit understands
    (let [valid-http-kit-req (select-keys req-map http-kit-keys)
          ;; Why the `identity` function is needed as the second argument to
          ;; http-kit/request remains a mystery
          response @(http-kit/request valid-http-kit-req identity)]
      (println "Running req! for HttpKit...")
      (when (:error response)
        (throw (ex-info "Http error"
                        {:from   ::req!
                         :reason :out-response-exception
                         :url    url
                         :method method
                         :cause  (:error response)})))
      response))
  component/Lifecycle
  (start [this] (let []
                  (println "Start HttpKit with Storage -> ")
                  (pprint storage)
                  (println "Start HttpKit with This (???) -> ")
                  (pprint this)
                  this))
  (stop [this] this)

  Object
  (toString [_] "<HttpKit>"))

;(defmethod print-method HttpKit [_ ^java.io.Writer w]
;  (.write w "<HttpKit>"))

(defn new-http-client []
  (map->HttpKit {}))

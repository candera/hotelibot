(ns user
  (:require [clojure.repl :refer :all]
            [clojure.tools.logging :as log]
            [clojure.tools.namespace.repl :refer (refresh)]
            [com.stuartsierra.component :as component]
            [hotelibot.routes :as routes]
            [hotelibot.system-instance :as system-instance]
            ring.adapter.jetty))

;;; Development-time components

(defrecord JettyServer [app port server]
  component/Lifecycle
  (start [this]
    (log/info :STARTING "jetty" :port port)
    (let [server (ring.adapter.jetty/run-jetty
                  app
                  {:port port
                   :join? false})]
      (log/info :STARTED "jetty" :port port :server server)
      (assoc this :server server)))
  (stop [this]
    (log/info :STOPPING "jetty" :port port :server server :server (:server this))
    (.stop (:server this))
    (log/info :STOPPED "jetty" :port port)
    this))

(defn jetty-server
  "Returns a Lifecycle wrapper around embedded Jetty for development."
  [port]
  (map->JettyServer {:app #'routes/app
                     :port port}))

(defn dev-system
  "Returns a complete system in :dev mode for development at the REPL.
  Options are key-value pairs from:

      :port        Web server port, default is 9900"
  [& {:keys [port]
      :or {port 9900}
      :as options}]
  (log/info "dev-system" :port port)
  (component/system-map :jetty (jetty-server port)
                        :options (or options {})))

;;; Development system lifecycle

(defn init
  "Initializes the development system."
  [& options]
  (alter-var-root #'system-instance/system (constantly (apply dev-system options))))

(defn system
  "Returns the system instance"
  []
 system-instance/system)

;; If desired change this to a vector of options that will get passed
;; to dev-system on (reset)
(def default-options [])

(defn go
  "Launches the development system. Ensure it is initialized first."
  [& options]
  (let [options (or options default-options)]
    (when-not system-instance/system (apply init options)))
  (alter-var-root #'system-instance/system component/start)
  (set! *print-length* 20)
  :started)

(defn stop
  "Shuts down the development system and destroy all its state."
  []
  (when system-instance/system
    (component/stop system-instance/system)
    (alter-var-root #'system-instance/system (constantly nil)))
  :stopped)

(defn reset
  "Stops the currently-running system, reload any code that has changed,
  and restart the system."
  []
  (stop)
  (refresh :after 'user/go))

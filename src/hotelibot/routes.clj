(ns hotelibot.routes
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log]
            [hotelibot.bot :as bot]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :refer :all]))

(def public-ip
  (str/trim (slurp "http://icanhazip.com")))

(defn skype-call-redirect-link
  "Create a link to the skype endpoint"
  [port handles]
  (format "http://%s:%d/skype?%s"
          public-ip
          port
          (->> handles
               (map #(str "handle=" %))
               (str/join "&"))))

(defn linker
  "Returns a function that, given an endpoint id, will return a link to it."
  [request]
  (fn [id & args]
    (log/trace "Linker called" :id id :args args)
    (case id
      :skype (apply skype-call-redirect-link (:server-port request) args))))

(defn hotelibot
  "Handles a request for the hotelibot endpoint."
  [request]
  {:body {:text (bot/handle (:params request) (linker request))}})

(defn skype-call-redirect
  "Generate a redirect to a Skype link to start a call."
  [request]
  (ring.util.response/redirect
   (format "skype:%s?call"
           (str/join ";" (-> request :params :handle)))))

(defn four-oh-four [request]
  (-> (response "Page not found")
      (status 404)))

(defn handler [request]
  (log/trace "handler called" :request request)
  (let [h (case [(:request-method request) (:uri request)]
            [:post "/hotelibot"] hotelibot
            [:get "/skype"] skype-call-redirect
            four-oh-four)]
    (h request)))

(def app
  (-> handler
      wrap-json-response
      wrap-keyword-params
      (wrap-json-body {:keywords? true})
      wrap-params))

(ns hotelibot.slack-api
  "Functions for working with the Slack API."
  (:require [clj-http.client :as client])
  (:import [com.google.common.cache CacheBuilder CacheLoader]
           [com.google.common.util.concurrent ListenableFutureTask]))

(defn api-url
  "Returns the appropriate URL for the given method"
  [method token]
  (format "https://slack.com/api/%s?token=%s" method token))

(defn users-list
  "Returns the list of all users as a sequence of maps. Caches the
  result for up to ten minutes."
  [token]
  ;; TODO: Add caching
  (let [resp (client/post (api-url "users.list" token)
                          {:as :json})]
    (when-not (-> resp :body :ok)
      (throw (ex-info "Slack API call to users.list failed"
                      {:reason ::slack-api-call-failure
                       :response resp
                       :token token
                       :method "users.list"})))
    (-> resp :body :members)))

(def tester [(clojail.testers/blacklist-symbols #{'alter-var-root
                                  'sensitive
                                  })
             (clojail.testers/blacklist-objects [java.lang.Thread])]) ; Create a blacklist.

(def sb (clojail.core/sandbox tester))

(client/post "http://localhost:8081/api/users.list"
             {:form-params {:token "..."}
              :content-type :json
              :as :json})

(client/post "https://slack.com/api/users.list?token=.."
             {:as :json})


(let [args            "hotelibot skype <@U02BQSRB3> and <@U02BQHULH> and <@U02BS61RT>"
      message-users   (into #{} (bot/parse-users args))
      all-users       (slack/users-list bot/slack-api-token)
      user-map        (zipmap (map :id all-users) all-users)
      skype-handles   (zipmap message-users
                              (map (fn [u]
                                     (-> u user-map :skype))
                                   message-users))
      present-handles (remove nil? (vals skype-handles))
      absent-handles  (->> skype-handles
                           (filter (fn [[k v]] (nil? v)))
                           keys)]
  {:absent-handles absent-handles
   :present-handles present-handles
   ;;:user-map user-map
   :skype-handles skype-handles})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(bot/skype-call-handler nil nil "hotelibot skype call <@U02BQHULH>")

(let [args            "hotelibot skype call <@U02BQHULH>"
      message-users   (into #{} (bot/parse-users args))
      all-users       (slack/users-list bot/slack-api-token)
      user-map        (zipmap (map :id all-users) all-users)
      skype-handles   (zipmap message-users
                              (map (fn [u]
                                     (-> u user-map :skype))
                                   message-users))
      present-handles (remove nil? (vals skype-handles))
      absent-handles  (->> skype-handles
                           (filter (fn [[k v]] (nil? v)))
                           keys)]
  {:present-handles present-handles
   :link (bot/skype-call-link present-handles)})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(routes/skype-call-redirect-link 8080 ["a"])

((routes/linker {:server-port 8080}) :skype ["foo"])

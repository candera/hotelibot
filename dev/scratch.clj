(def tester [(clojail.testers/blacklist-symbols #{'alter-var-root
                                  'sensitive
                                  })
             (clojail.testers/blacklist-objects [java.lang.Thread])]) ; Create a blacklist.

(def sb (clojail.core/sandbox tester))

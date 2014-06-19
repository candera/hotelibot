(ns hotelibot.bot
  "Functions that implement the logic for the hotelibot"
  (:require [clojail.core :as clojail]
            [clojail.testers]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.tools.logging :as log]))

(defn unknown-command-handler
  "Returns an error message indicating that the command was not recognized"
  [message command args]
  (format "Sorry, I don't know what to do with '%s %s'" command args))

(defn skype-call-handler
  "Returns a link that will start a Skype call with the named users."
  [message command args]
  (str "Not yet implemented: " args))

(defn say-handler
  "Just echoes back what was said."
  [message command args]
  args)

(let [sb (clojail/sandbox clojail.testers/secure-tester)]
  (defn eval-handler
    "Evaluates the passed form in a Clojail sandbox"
    [message command args]
    (let [w (java.io.StringWriter.)
          [val output] (binding [*out* w]
                         (let [result (-> args read-string eval pr-str)]
                           [result (str w)]))]
      (str val (when-not (str/blank? output) (str "\n ;; => " output))))))

(def command-handlers
  {"skype" skype-call-handler
   "say"   say-handler
   "eval"  eval-handler})

(defn handle
  "Main entry point for handling messages to the hotelibot."
  [message]
  (let [{:keys [user_id text trigger_word]} message
        trigger-len (count trigger_word)
        has-colon? (= ":" (subs text trigger-len (inc trigger-len)))
        phrase (-> text
                   (subs (if has-colon?
                           (inc trigger-len)
                           trigger-len))
                   str/trim)
        [command args] (str/split phrase #"\s" 2)
        handler (get command-handlers command unknown-command-handler)]
    (try
      (handler message command args)
      (catch Throwable t
        (log/error t "Error in handler")
        (format "Error handling command %s with arguments %s: %s"
                command args (.getMessage t))))))

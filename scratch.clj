(binding [*warn-on-reflection* true]
 (let [old (System/getSecurityManager)
       sm (SecurityManager.)]
   (try
     (System/setSecurityManager sm)
     (println "hi")
     (finally
       (System/setSecurityManager old)))))

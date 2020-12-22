(ns graphminer.net.server
  (:require
   [graphminer.net.handler :refer [app]]
   [config.core :refer [env]]
   [ring.adapter.jetty :refer [run-jetty]]
   [clojure.java.io :as io]
   [markdown-to-hiccup :as md])
  (:gen-class))


(defn -main [& args]
  (let [port (or (env :port) 3000)]
    (run-jetty #'app {:port port :join? false})))

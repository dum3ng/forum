(ns forum.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [forum.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[forum started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[forum has shut down successfully]=-"))
   :middleware wrap-dev})

(ns user
  (:require [mount.core :as mount]
            [forum.figwheel :refer [start-fw stop-fw cljs]]
            forum.core))

(defn start []
  (mount/start-without #'forum.core/repl-server))

(defn stop []
  (mount/stop-except #'forum.core/repl-server))

(defn restart []
  (stop)
  (start))



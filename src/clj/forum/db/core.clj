(ns forum.db.core
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            [mount.core :refer [defstate]]
            [forum.config :refer [env]]
            [monger.util :refer [object-id]]
            )
  (:import java.util.Date)  )

(defstate db*
  :start (-> env :database-url mg/connect-via-uri)
  :stop (-> db* :conn mg/disconnect))

(defstate db
  :start (:db db*))

(defn create-test
  [value]
  (mc/insert db "test" {:_id (str (object-id))
                        :value value})
  "ok")

(defn get-test
  []
  ;; {:_id "ssss"
  ;;  :value "nonono"}
  (mc/find-one-as-map db "test" {:value "v" })
  )
(defn- str-id []
  (str (object-id)))

(defn- str-response [b]
  (if b
    "success"
    "fail"))
;; user
(defn create-user [user]
  (str-response (mc/insert db "users"  (assoc user :_id (str-id) :create-at (Date.)))))

(defn update-user [id options]
  (str-response (mc/update db "users" {:_id id}
                           {$set options})))

(defn get-user-by-id [id]
  (-> (mc/find-one-as-map db "users" {:_id id})
      (dissoc :password)))

(defn get-user-by-name [name]
  (mc/find-one-as-map db "users" {:username name}))

;; posts
;; Post {title,content,section}
;; user {_id, username}
(defn- user->author [user]
  (-> user
      (assoc :name (:username user))
      (dissoc :username)))

(defn create-post [post section user]
  (let [now (Date.)]
    (str-response (mc/insert db "posts" (merge {:_id (str-id)
                                                :author (user->author user)
                                                :create-at now
                                                :update-at now
                                                :section (str section) }
                                               post)))))
;; the post contains only 3 fields
;; {:_id, :title, :content}
(defn update-post [post]
  (str-response (mc/update db "posts" {:_id (:_id post)}
                           {$set (dissoc post :_id)})))

(defn delete-post [id]
  (str-response (mc/remove-by-id db "posts" id)))

(defn get-posts-in-section [section]
  (mc/find-maps db "posts" {:section section}))

;; (defn get-comments-in-post [post]
;; ())

(defn add-comment-on-post [id comment user]
  (let [now (Date.)]
    (str-response (mc/update db "posts" {:_id id}
                             {$push {:comments {:author (user->author user)
                                                :content comment
                                                :create-at now
                                                :update-at now}}
                              }))))
;; coment {:_id, :content}
;; (defn update-comment-on-post [post comment user]
;;   (let [now (Date.)]
;;     (mc/)))

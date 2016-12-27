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
    "failure"))
;; user
(defn create-user
  "Create a user:
  :_id :username :password :create-at
  If insert successfully, return the user without password,
  else return nil"
  ([user]
   (let [user (assoc user :_id (str-id) :create-at (Date.))]
     (if (mc/insert db "users" user)
       user)))
  ([name pwd]
   (create-user {:username name :password pwd})))

(defn update-user [id options]
  (str-response (mc/update db "users" {:_id id}
                           {$set options})))

(defn get-user-by-id [id]
  (mc/find-one-as-map db "users" {:_id id}))

(defn get-user-by-name [name]
  (mc/find-one-as-map db "users" {:username name}))

(defn register [username password]
  (if (get-user-by-name username)
    nil
    (create-user {:username username :password password})))
;; posts
;; Post {title,content,section}
;; user {_id, username}
(defn- user->author [user]
  (-> user
      (assoc :name (:username user))
      (dissoc :username)))

(defn create-post [post section user-id]
  (let [now (Date.)
        post (merge post
                    {:_id (str-id)
                     :author user-id
                     :create-at now
                     :update-at now})]
    (do   (mc/insert db "posts" post)
          {:id (:_id post)})))
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

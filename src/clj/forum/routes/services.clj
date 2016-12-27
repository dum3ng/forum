(ns forum.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [clojure.tools.logging :as log]
            [compojure.api.meta :refer [restructure-param]]
            [buddy.auth.accessrules :refer [restrict]]
            [buddy.auth :refer [authenticated?]]
            [forum.db.core :as db ]
            [forum.layout :refer [*identity*]]
            [clojure.tools.logging :as log]))

(defn access-error [_ _]
  (unauthorized {:error "unauthorized"}))

(defn wrap-restricted [handler rule]
  (restrict handler {:handler  rule
                     :on-error access-error}))

(defmethod restructure-param :auth-rules
  [_ rule acc]
  (update-in acc [:middleware] conj [wrap-restricted rule]))

(defmethod restructure-param :current-user
  [_ binding acc]
  (update-in acc [:letks] into [binding `(:identity ~'+compojure-api-request+)]))

(s/defschema Test
  {:_id s/Str
   :value s/Str})

(s/defschema User
  {:_id s/Str
   :create-at (s/maybe s/Inst)
   :username s/Str})

(s/defschema Author
  {:_id s/Str
   :name s/Str
   :create-at s/Inst})

(s/defschema Comment
  {:author Author
   :create-at s/Inst
   :update-at s/Inst
   :content s/Str})

(s/defschema Post
  {:_id s/Str
   :title s/Str
   :content s/Str
   :section s/Str
   :create-at s/Inst
   :update-at s/Inst
   :author s/Str ;; it is an id string
   (s/optional-key :comments) [Comment]})
(s/defschema UpdatePost
  {:_id s/Str
   :title s/Str
   :content s/Str})
(s/defschema NewPost
  {:title s/Str
   :content s/Str})

(s/defschema ATest
  {"_id" s/Str
   "value" s/Str})
(def operation-response
  (s/enum "success" "fail"))

(defroutes test-routes)

(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "Sample API"
                           :description "Sample Services"}}}}

  (GET "/authenticated" []
       :auth-rules authenticated?
       :current-user user
       (ok {:user user}))
  (context "/api" []
           :tags ["thingie"]

           (GET "/test" []
                :return (s/maybe Test)
                :summary "xxx"
                (-> (db/get-test)
                    str
                    print)
                (ok (db/get-test)))
           (POST "/test" []
                 :body-params [test :- String]
                 :return String
                 :summary "post a test"
                 (ok (db/create-test test)))
           (POST "/user/new" []
                 :return operation-response
                 :body-params [username :- String, password :- String]
                 :summary "create a new user"
                 (ok (db/create-user {:username username :password password})))
           (GET "/user" []
                :return (s/maybe User)
                :query-params [id :- String]
                (ok nil))

           (POST "/register" req
                 :return s/Any
                 :body-params [username :- String, password :- String]
                 :summary "register a user"
                 (if-let [user (db/register username password)]
                   (let [user (dissoc user :password)]
                     (log/info "created user: " user)
                     (-> (created "" user)
                         (assoc-in [:session :identity] user)))
                   (ok "")))

           (POST "/login" request
                 :return String
                 :body-params [username :- String, password :- String]
                 (let [user (db/get-user-by-name username)]
                   (if (= password (:password user))
                     (->  (ok "success")
                          (assoc-in [:session :identity] (dissoc user password)))
                     (ok "failure"))))
           (POST "/logout" request
                 :return String
                 :summary "logout current user"
                 (if (authenticated? request)
                   (-> (found "/")
                       (assoc-in [:session :identity] {}))
                   (found "/login")) )

           (GET "/post" []
                :return [Post]
                :query-params [section :- String]
                :summary "get posts in a specified section, maybe []"
                (do
                  (Thread/sleep 1000)
                  (log/info "para section + " section)
                  (let [posts (db/get-posts-in-section section)]
                    (print (type posts))
                    (ok (if  (nil? posts)
                          []
                          posts)))))
           (GET "/post/t" []
                :return [Post]
                (let [posts (db/get-posts-in-section "venus")]
                  ok [])
                ;; (ok (str  (type (vec  (db/get-posts-in-section "venus")))))
                )
           (POST "/post" []
                 :return String
                 :body-params [id :- String, user :- User]
                 :summary "update a post"
                 (ok (db/update-post id user)) )
           (POST "/post/new" []
                 :auth-rules authenticated?
                 :return s/Any
                 :body-params [post :- s/Any,;; NewPost,
                               section :- String,;; String,
                               ]
                 (let [user_id (:_id *identity*)]
                   (ok (db/create-post post
                                       section
                                       user_id))))
           (POST "/post/:id/comment/new" []
                 :path-params [id :- String]
                 :return operation-response
                 :body-params [comment :- String
                               user :- User]
                 :summary "add comment on a post with specified id"
                 (ok (db/add-comment-on-post id comment user)))
           ;;TODO: update comment on a post
           ;; (POST "/post/")
           (GET "/plus" []
                :return       Long
                :query-params [x :- Long, {y :- Long 1}]
                :summary      "x+y with query-parameters. y defaults to 1."
                (ok (+ x y)))

           (POST "/minus" []
                 :return      Long
                 :body-params [x :- Long, y :- Long]
                 :summary     "x-y with body-parameters."
                 (ok (- x y)))

           (GET "/times/:x/:y" []
                :return      Long
                :path-params [x :- Long, y :- Long]
                :summary     "x*y with path-parameters"
                (ok (* x y)))

           (POST "/divide" []
                 :return      Double
                 :form-params [x :- Long, y :- Long]
                 :summary     "x/y with form-parameters"
                 (ok (/ x y)))

           (GET "/power" []
                :return      Long
                :header-params [x :- Long, y :- Long]
                :summary     "x^y with header-parameters"
                (ok (long (Math/pow x y))))))

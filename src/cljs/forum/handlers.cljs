(ns forum.handlers
  (:require [forum.db :as db]
            [re-frame.core :refer [reg-event-fx dispatch reg-event-db]]
            [re-frame.std-interceptors :refer [debug]]
            [ajax.core :as ajax :refer [GET POST]]
            [day8.re-frame.http-fx]
            [forum.util :as u]))

;; (defn- k->s
;;   "keyword to string, erase the colon"
;;   [k]
;;   (-> k
;;       str
;;       (subs 1)))

(reg-event-db
 :initialize-db
 (fn [db _]
   (merge db db/default-db)))

(reg-event-db
 :set-docs
 (fn [db [_ docs]]
   (assoc db :docs docs)))

(reg-event-db
 :set-page
 (fn [db [_ page]]
   (assoc db :page page)))

(reg-event-db
 :set-section
 (fn [db [_ section]]
   (assoc db :section section)))

(reg-event-db
 :set-post-in-section
 (fn [db [_  section id]]
   (let [posts (-> db
                   :posts-in-section
                   section)
         filter-post (filter #(= id (:_id %)) posts)
         post (if (nil? filter-post)
                {}
                (first filter-post))]
     (assoc-in db [:current-post-in-section section] post))))

(reg-event-db
 :set-user
 (fn [db [_ user]]
   (assoc db :user user)))

(reg-event-db
 :set-state-of-section
 (fn [db [_ section state]]
   (assoc-in db [:state-of-section section] state)))
;; for post
(reg-event-fx
 :submit-new-post-in-section
 (fn [{db :db} [_ section post user]]
   {:http-xhrio {:method :post
                 :uri "/api/post/new"
                 :params {:post post
                          :section section
                          :user user}
                 :timeout         8000
                 :format (ajax/json-request-format)
                 :response-format (ajax/text-response-format)
                 :on-success [:submit-new-post-success section]
                 :on-failure [:submit-new-post-failure section]}
    :db (-> db
            (assoc-in [:new-post-in-section section] post)
            (assoc-in [:submitting-new-post-in-section section] true))}))

(reg-event-fx
 :submit-new-post-success
 (fn [{db :db} [_ section result]]
   {:dispatch [:fetch-posts-in-section section]
    :db (-> db
            (assoc-in [:submitting-new-post-in-section section] false)
            (assoc-in [:new-post-in-section section] {}))}
   ;; TODO: this will fetch all the posts again,
   ;; can be better, sue timestamp
   ))

(reg-event-fx
 :submit-new-post-failure
 (fn [{db :db} [_ section result]]
   {:db    (assoc-in db [:submitting-new-post-in-section section] false)
    :dispatch-n (list [:set-modal-state true] [:set-modal-content :auth])}) )

;; for comment
(reg-event-fx
 :submit-new-comment-on-post
 (fn [{db :db} [_ id section comment user]]
   {:db (let [c {:author user
                 :create-at (js/Date.)
                 :udpate-at (js/Date.)
                 :content comment}]
          (-> db
              (assoc  :submitting-new-comment true)
              )
          )
    :http-xhrio {:uri (str "/api/post/" id "/comment/new")
                 :params {
                          :comment comment
                          :user user}
                 :method :post
                 :format (ajax/json-request-format)
                 :response-format (ajax/text-response-format)
                 :on-success [:submit-new-comment-success section]
                 :on-failure [:submit-new-comment-failure]}}))


(reg-event-fx
 :submit-new-comment-success
 (fn [{db :db} [_  section result]]
   {:dispatch [:fetch-posts-in-section section]
    :db (assoc db :submitting-new-comment false
               :comment-in-edit {}) }))

(reg-event-db
 :submit-new-comment-failure
 (fn [db [_]]
   (assoc db :submitting-new-comment false)))

;; fetch user
(reg-event-db
 :fetch-user-start
 (fn [db [_]]
   (assoc db :fetching-user true)))

(reg-event-db
 :fetch-user-end
 (fn [db [_]]
   (assoc db :fetching-user false)))

;; fetch posts
(reg-event-fx
 :fetch-posts-in-section
 (fn  [{db :db} [_ section]]
   {:dispatch [:fetch-posts-start section]
    :http-xhrio {:method :get
                 :uri "/api/post"
                 :timeout 8000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :params {:section (u/k->s section)}
                 :on-success [:fetch-posts-success section]
                 :on-failure [:fetch-posts-failure section]}}))

(reg-event-db
 :fetch-posts-start
 [debug]
 (fn [db [_ section]]
   (assoc-in db [:fetching-posts-in-section section] true)))

(reg-event-db
 :fetch-posts-success
 [debug]
 (fn [db [_  section posts]]
   (print "success + " section)
   (-> db
       (assoc-in [:fetching-posts-in-section section] false)
       (assoc-in [:posts-in-section section]  posts)
       ((fn [d] (if-let [id (-> db :current-post-in-section section :_id)]
                 (assoc-in d [:current-post-in-section section] (some #(if (= id (:_id %)) %) posts))
                 d)) )
       )))

(reg-event-db
 :fetch-posts-failure
 (fn [db [_ section result]]
   (assoc-in db [:fetching-posts-in-section section] false)))

(reg-event-db
 :timeout
 (fn [db _]
   (print ":time! : " (:timeout db))
   (js/setTimeout #(dispatch [:timeout-end]) 1000)
   (assoc db :timeout "delay start")))

(reg-event-db
 :timeout-end
 (fn [db _]
   (assoc db :timeout "delay end")))

(reg-event-fx
 :minus
 (fn [{db :db} [_ x y] ]
   {:http-xhrio {:params {:x 20
                          :y 2}
                 :uri "/api/minus"
                 :method :post
                 :format (ajax/json-request-format)
                 :response-format (ajax/text-response-format)
                 :on-success [:minus-end]
                 :on-failure [:minus-fail]
                 }}))
;; (reg-event-db
;;  :minus
;;  (fn [db [_ x y]]
;;    (POST "/api/minus"
;;          {:params {:x 23 :y 3}
;;           :handler #(dispatch [:minus-end %])
;;           :error-handler #(dispatch [:minus-fail %])})
;;    db))
(reg-event-db
 :minus-end
 (fn [db [_ result]]
   (assoc db :minus result)))

(reg-event-db
 :minus-fail
 (fn [db [_ result]]
   (assoc db :minus result)))

(reg-event-db
 :set-modal-state
 (fn [db [_ show]]
   (assoc db :modal-state show)))

(reg-event-db
 :set-modal-content
 (fn [db [_ c]]
   (assoc db :modal-content c)))

;; for registeration
(reg-event-fx
 :register
 (fn [{db :db} [_ name pwd]]
   {:db (assoc db :register-submitting true)
    :http-xhrio {:params {:username name
                          :password pwd}
                 :uri "/api/register"
                 :method :post
                 :response-format (ajax/json-response-format {:keywords? true})
                 :format (ajax/json-request-format)
                 :on-success [:register-success]
                 :on-failure [:register-failure]}}))

(reg-event-db
 :register-success
 (fn [db [_ result]]
   (case result
     ""    (-> db
               (assoc :register-submitting false)
               (assoc :register-info "username has been taken."))
     (-> db                ;; created!
         (assoc :register-submitting false)
         (assoc :self result)
         (assoc :modal-state false)))))

(reg-event-db
 :register-failure
 (fn [db [_ result]]
   (-> db
       (assoc :register-submitting false)
       (assoc :register-info (:status-text result)))))

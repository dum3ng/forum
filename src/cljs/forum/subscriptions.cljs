(ns forum.subscriptions
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
 :timeout
 (fn [db _]
   (:timeout db)))

(reg-sub
 :all
 (fn [db _]
   db))
(reg-sub
 :page
 (fn [db _]
   (:page db)))

(reg-sub
 :docs
 (fn [db _]
   (:docs db)))

(reg-sub
 :section
 (fn [db [_ section]]
   (:section db)))

(reg-sub
 :other
 (fn [db [_]]
   (:other db)))

(reg-sub
 :self
 (fn [db _]
   (:self db)))


(reg-sub
 :posts-in-section
 (fn [db [_ section]]
   (-> db
       :posts-in-section
       section)))
(reg-sub
 :states
 (fn [db _]
   (:state-of-section db)))

(reg-sub
 :state
 (fn [_ _]
   (subscribe [:states]))
 (fn [states [_] [section]]
   (section states)))

(reg-sub
 :state-of-section
 (fn [db [_ section]]
   (-> db
       :state-of-section
       section)))

(reg-sub
 :current-posts
 (fn [db _ ]
   (:current-post-in-section db)))

(reg-sub
 :current-post-in-section
 (fn [db [_ section]]
   (-> db :current-post-in-section section)))

(reg-sub
 :current-post
 (fn [_ _]
   (subscribe [:current-posts] ))
 (fn [current-posts _  [section]]
   (section current-posts)))

(reg-sub
 :submitting-new-post-in-section
 (fn [db [_ section]]
   (-> db :submitting-new-post-in-section section)))

(reg-sub
 :new-post-in-section
 (fn [db [_ section]]
   (-> db :new-post-in-section section)))

(reg-sub
 :fetching-posts-in-section
 (fn [db [_ section]]
   (-> db :fetching-posts-in-section section)))

(reg-sub
 :submitting-new-comment
 (fn [db _]
   (:submitting-new-comment db)))
(reg-sub
 :minus
 (fn [db _]
   (:minus db)))

(reg-sub
 :modal-state
 (fn [db _]
   (:modal-state db)))

(reg-sub
 :modal-content
 (fn [db _]
   (:modal-content db)))

;; for register
(reg-sub
 :register-submitting
 (fn [db _]
   (:register-submitting db)))

(reg-sub
 :register-info
 (fn [db _]
   (:register-info db)))

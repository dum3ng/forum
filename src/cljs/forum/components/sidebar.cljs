(ns forum.components.sidebar
  (:require [re-frame.core :refer [subscribe dispatch]]
            ))




(defn post-item
  [post]
  [:a {:href (str "#/forum/" (:section post)
                  "/" (:_id post))} (:title post)])
;; user should be:
;; {:_id  :create-at  :username  }
;; posts is his/her most recent 3 posts..
;; post  {:_id :title :content :create-at :update-at
;;        :author :comments }
(defn profile
  [user posts]
  [:div.row
   [:div.col-xs-4
    [:div.profile (-> user :username first)]]
   [:div.col-xs-8
    [:div (:username user)]]
   (doall
    (map (fn [p] [:div.col-xs-12 [post-item p]]) posts))])

(defn self
  []
  (let [s (subscribe [:self])]
    (fn []
      (print "slef username: " (-> @s :user :username))
      [profile (:user @s) (:recents @s)])))

(defn other
  []
  (let [o (subscribe [:other])]
    (fn []
      [profile (:user @o) (:recents @o)])))

(defn rightbar
  []
  [:div.row.rightbar
   [:p "profile"]
   [:div.col-xs-12
    [self]]
   [:div.col-xs-12
    [other]]])

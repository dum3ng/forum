(ns forum.components.list
  (:require [forum.util :as u]
            [re-frame.core :refer [dispatch subscribe]]
            [forum.components.utils :refer [wrap-toggle indicator]]))


(defn- list-item
  [section post]
  [:div.row.list-item
   [:div.col-xs-2
    [:button {:on-click #()
              } (-> post :author :name)]]
   [:div.col-xs-10
    [:a {:href (str  "#/forum/" (u/kw->str section) "/post/" (:_id post))
         :on-click #(dispatch [:set-post-in-section section (:_id post)])} (:title post)]]])

(defn list-view
  [section]
  (let [posts (subscribe [:posts-in-section section])
        fetching (subscribe [:fetching-posts-in-section section])]
    (fn [section]
      (print "fetching: " @fetching ", " fetching)
      (cond-> @posts
        (nil? @posts) (print "nil...")
        (empty? @posts) (print "empty..")
        :else (print "has posts.."))
      [:div.row
       [wrap-toggle indicator (= @fetching true)]
       [:button {:on-click #(dispatch [:fetch-posts-in-section section])} "Refresh!"]
       (if (nil? @posts)
         (do (dispatch [:fetch-posts-in-section section])
             [:div])
         (if (empty? @posts)
           [:h3 (str "No posts in " section " section")]
           [:div.col-xs-12
            (map (fn [post] ^{:key (:_id post)} [list-item section post]) @posts)]))])))

(ns forum.components.single
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as r]))

(def user {:_id "identiclaID"
           :create-at (.toISOString (js/Date.))
           :username "dumeng"})

(defn post-item
  [author content & [extra]]
  [:div.row.post-item
   [:div.col-xs-2.author-info
    [:p "author:"]
    [:p (:name author)]]
   [:div.col-xs-10.content
    [:p content]]])

(defn comment-view
  [post]
  (let [content (r/atom "")
        submitting (subscribe [:submitting-new-comment])]
    (fn [post]
      (print "submitting: " submitting)
      [:div.row
       [:h3 "New Comment"]
       [:div.col-xs-12
        [:input-group
         [:input.form-control {:on-change #(reset! content (.. % -target -value)) }]]
        [:br]]
       [:div.col-xs-12
        [:button.btn.btn-primary {:disabled @submitting
                                  :class (if @submitting "submitting")
                                  :on-click #(dispatch [:submit-new-comment-on-post (:_id  post) (keyword (:section post)) @content user])}
         "Submit!"]]
       ])))

(defn single-view
  [section]
  (let [current-post (subscribe [:current-post-in-section section])]
    (fn [section]
      [:div.row
       [:div.col-xs-12
        [:h3 (:title  @current-post)]
        [:hr]]
       [:div.col-xs-12
        [post-item (:author @current-post) (:content @current-post)]]
       [:div.col-xs-12
        (let [comments (:comments @current-post)]
          (map (fn [comment] ^{:key (:create-at comment)} [post-item (:author comment) (:content comment) ])
               comments))]
       [:div.col-xs-12
        [comment-view @current-post]]])))

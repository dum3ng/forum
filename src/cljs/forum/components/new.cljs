(ns forum.components.new
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as r]))

(def user {:_id "identiclaID"
           :create-at (.toISOString (js/Date.))
           :username "dumeng"})

(defn new-view
  [section]
  (let [new-post (subscribe [:new-post-in-section section])
        title (r/atom "untitled")
        content (r/atom "")
        self (subscribe [:self])]
    (fn [section]
      [:div.row
       [:label {:for "title"} "Title"]
       [:br]
       [:div.input-group
        [:input#title.form-control {:value @title
                                    :on-change #(reset! title (.. % -target -value))}]]
       [:br]
       [:div.input-group
        [:label {:for "content"}]
        [:textarea.form-control {:rows 10
                                 :cols 20
                                 :value @content
                                 :on-change #(reset! content (.. % -target -value))}]]
       [:br]
       [:div.input-group
        [:button.btn.btn-primary {:on-click #(do (dispatch [:submit-new-post-in-section section {:title @title :content @content } ])
                                                 (reset! title "untitled")
                                                 (reset! content ""))} "submit"]]])))

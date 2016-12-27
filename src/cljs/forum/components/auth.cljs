(ns forum.components.auth
  (:require    [re-frame.core :refer [subscribe dispatch]]
               [reagent.core :as r]))

(defn get-value
  [e]
  (..  e -target -value))

(defn login
  []
  [:div.row
   [:div.col-xs-6.offset-xs-3
    [:div.input-group
     [:span.input-group-addon
      [:img {:src "/img/account.svg"}]]
     [:input.form-control {:type "text"
                           :placeholder "username"}]]
    [:br]]
   [:div.col-xs-6.offset-xs-3
    [:div.input-group
     [:span.input-group-addon
      [:img {:src "/img/password.svg"}]]
     [:input.form-control {:type "password"
                           :placeholder "password"}]]
    [:br]
    ]
   [:div.col-xs-6.offset-xs-3
    [:div.input-group
     [:button.btn.btn-primary.form-control {:on-click #()}
      "Log In"]]]
   [:br]])

(defn register
  []
  (let [name (r/atom "")
        pwd (r/atom "")
        submitting (subscribe [:register-submitting])
        info (subscribe [:register-info])]
    (fn []
      [:div.row
       [:div.col-xs-6.offset-xs-3
        [:div.input-group
         [:span.input-group-addon
          [:img {:src "/img/account.svg"}]]
         [:input.form-control {:type "text"
                               :value @name
                               :on-change #(reset! name (get-value %))
                               :placeholder "username"}]]
        [:br]]
       [:div.col-xs-6.offset-xs-3
        [:div.input-group
         [:span.input-group-addon
          [:img {:src "/img/password.svg"}]]
         [:input.form-control {:type "password"
                               :value @pwd
                               :on-change #(reset! pwd (get-value %))
                               :placeholder "password"}]]
        [:br]]
       [:div.col-xs-6.offset-xs-3
        [:span {:style {:color "red"}} @info]
        [:div.input-group
         [:button.btn.btn-primary.form-control {:class (if @submitting "submitting")
                                                :disabled @submitting
                                                :on-click #(dispatch [:register @name @pwd])}
          "Register"]]]
       [:br]])))

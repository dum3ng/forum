(ns forum.components.header
  (:require [re-frame.core :refer [subscribe ]]
            [reagent.core :as r]))


(defn nav-link [uri title page collapsed?]
  (let [selected-page (subscribe [:page])]
    [:li.nav-item
     {:class (when (= page @selected-page) "active")}
     [:a.nav-link
      {:href uri
       :on-click #(reset! collapsed? true)} title]]))

;;
;; pages
;;
(defn page-nav []
  (r/with-let [collapsed? (r/atom true)]
    [:nav#page-nav.navbar.navbar-dark.bg-primary
     [:button.navbar-toggler.hidden-sm-up
      {:on-click #(swap! collapsed? not)} "☰"]
     [:div.collapse.navbar-toggleable-xs
      (when-not @collapsed? {:class "in"})
      [:a.navbar-brand {:href "#/"} "forum"]
      [:ul.nav.navbar-nav
       [nav-link "#/" "Home" :home collapsed?]
       [nav-link "#/forum" "Forum" :forum collapsed?]
       [nav-link "#/about" "About" :about collapsed?]]]]))

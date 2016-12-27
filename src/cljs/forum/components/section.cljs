(ns forum.components.section
  (:require [forum.components.utils :refer [wrap-toggle]]
            [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as r]
            [forum.util :as u]
            [forum.components.new-view :refer [new-view]]
            [forum.components.single :refer [single-view]]
            [forum.components.list :refer [list-view]]
            [forum.components.sidebar :refer [rightbar]]
            [forum.components.utils :refer [indicator wrap-toggle]]))


;;
;; sections
;;
(defn section-nav-link [uri title section]
  (let [c-section (subscribe [:section])]
    (fn []
      [:div.row.section-nav-item.flex-items-xs-middle.flex-items-xs-center  (when (= section @c-section) {:class "active"})
       [:div.col-xs
        [:a {:href uri} title]]
       ])))

(defn section-nav []
  (fn []
    [:div#section-nav.row.flex-items-xs-middle.flex-items-xs-center
     [:div.col-xs.flex-xs-center.section-nav
      [:div.row
       [:div.col-xs
        [section-nav-link "#/forum/earth" "earth" :earth]]
       [:div.col-xs
        [section-nav-link "#/forum/mars" "mars" :mars]]
       [:div.col-xs
        [section-nav-link "#/forum/venus" "venus" :venus]]]]]))
(defn view-switcher
  []
  (let [section (subscribe [:section])
        state (subscribe [:state] [section] )
        post (subscribe [:current-post] [section])]
    (fn []
      ;; (print "state " state " , post " post)
      ;;      (print "type state: "(type state))
      [:div.row.flex-items-xs-center.view-switcher
       [:div.col-xs-12.icon {:class (if (= @state :list) "active")}
        [:a {:href (str  "#/forum/" (u/kw->str @section) "/posts")}
         [:img {:src "/img/posts.svg"}]]]
       [:div.col-xs-12.icon  {:class (if (= @state :single) "active")}
        [:a {:href (str  "#/forum/" (u/kw->str @section) "/post/" (:_id @post))}
         [:img {:src "/img/single.svg"}]
         ]]
       [:div.col-xs-12.icon {:class (if (= @state :new) "active")}
        [:a {:href (str  "#/forum/" (u/kw->str @section) "/new")}
         [:img {:src "/img/new.svg"}]]]
       ])))

(defn right-bar
  []
  (let [user (subscribe [:user])]
    (fn []
      [:div.row.rightbar
       "rightbar"])))

(defn which-section
  [section]
  (let [state (subscribe [:state-of-section section])]
    (fn [section]
      [:div.row
       [:div.col-xs-2
        [view-switcher]]
       [:div.col-xs-10.section-main
        [wrap-toggle [single-view section] (= @state :single) {:class "col-xs-10 offset-xs-1"}]
        [wrap-toggle [new-view section] (= @state :new) {:class "col-xs-10 offset-xs-1"}]
        [wrap-toggle [list-view section] (= @state :list) {:class "col-xs-10 offset-xs-1"}]]])))

(defn section []
  (let [c-section (subscribe [:section])]
    (fn []
      [:div.row
       [:div.col-xs-12
        [wrap-toggle [which-section :earth] (= @c-section :earth)]]
       [:div.col-xs-12
        [wrap-toggle [which-section :mars] (= @c-section :mars)]]
       [:div.col-xs-12
        [wrap-toggle [which-section :venus] (= @c-section :venus)]]])))

(defn section-page []
  [:div.row
   [:div.col-xs-12
    [section-nav]]
   [:div.col-xs-9
    [section]]
   [:div.col-xs-3
    [rightbar]]])

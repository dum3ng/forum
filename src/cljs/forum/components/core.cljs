(ns forum.components.core
  (:require [forum.components.header :refer [page-nav]]
            [re-frame.core :refer [subscribe]]
            [forum.components.utils :refer [wrap-toggle tab  auth modal]]
            [forum.components.home :refer [home-page]]
            [forum.components.about :refer [about-page]]
            [forum.components.section :refer [section-page]]
            [cljsjs.jquery]))




(defn page []
  (let [c-page (subscribe [:page])]
    (fn []
      [:div.row
       [:div.col-xs-12
        [wrap-toggle home-page (= @c-page :home)]]
       [:div.col-xs-12
        [wrap-toggle section-page (= @c-page :forum) ]]
       [:div.col-xs-12
        [wrap-toggle about-page (= @c-page :about)]]])))


(defn make-tab
  [title content]
  {:title title
   :content content})

(defn tab-test
  []
  [tab [(make-tab "tab0" "content 00")
        (make-tab "tab1" "content 11")
        (make-tab "tab2" "content 22")]])

(defn modal-test
  []
  [:button {:on-click #(let [modal (js/$ "#modal")
                             backdrop (js/$ "#backdrop")]
                         (.show modal)
                         (.show backdrop))} "show modal"])
;; app
(defn app
  []
  [:div
   [page-nav]
   [modal-test]
   [modal auth]
   [:div.container
    [page]]
   [:div#backdrop.modal-backdrop.fade]])

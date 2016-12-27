(ns forum.components.core
  (:require [forum.components.header :refer [page-nav]]
            [re-frame.core :refer [subscribe]]
            [forum.components.utils :refer [wrap-toggle tab  auth modal modal-backdrop]]
            [forum.components.home :refer [home-page]]
            [forum.components.about :refer [about-page]]
            [forum.components.section :refer [section-page]]
            [cljsjs.jquery]
            [re-frame.core :refer [dispatch subscribe]]
            [goog.crypt :as crypt])
  (:import goog.crypt.Md5))




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


(defn modal-test
  []
  [:button {:on-click #(dispatch [:set-modal-state true])} "show modal"])

(defn one-modal
  []
  (let [show (subscribe [:show-modal])
        content (subscribe [:modal-content])]
    (fn []
      ())))

(defn avatar
  [email]
  (let [digester (Md5.)
        hash (do (.update digester (crypt/stringToByteArray email))
                 (.digest digester))
        hashx (crypt/byteArrayToHex hash)
        ]
    (fn [email]
      (print hash)
      [:div.avatar
       [:img {:src (str "https://www.gravatar.com/avatar/" hashx)}]])))
;; app
(defn app
  []
  [:div
   [page-nav]
   [modal-test]
   [modal]
   [:div.container
    [page]]
   [modal-backdrop]])

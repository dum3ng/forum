(ns forum.components.section
  (:require [forum.components.utils :refer [wrap-toggle]]
            [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as r]
            [forum.util :as u]
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

(def user {:_id "identiclaID"
           :create-at (.toISOString (js/Date.))
           :username "dumeng"})

(defn new-view
  [section]
  (let [new-post (subscribe [:new-post-in-section section])
        title (r/atom "untitled")
        content (r/atom "")]
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
        [:button.btn.btn-primary {:on-click #(do (dispatch [:submit-new-post-in-section section {:title @title :content @content } user])
                                                 (reset! title "untitled")
                                                 (reset! content ""))} "submit"]]])))

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

(defn c [section]
  (let [c (subscribe [:current-post-in-section section])]
    (fn [section]
      [:button {:on-click #(print @c)} "show current"])))
(defn single-view
  [section]
  (let [current-post (subscribe [:current-post-in-section section])]
    (fn [section]
      [:div.row
       [c section]
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
       [:div.col-xs-1
        [view-switcher]]
       [:div.col-xs-8.section-main
        [wrap-toggle [single-view section] (= @state :single) {:class "col-xs-10 offset-xs-1"}]
        [wrap-toggle [new-view section] (= @state :new) {:class "col-xs-10 offset-xs-1"}]
        [wrap-toggle [list-view section] (= @state :list) {:class "col-xs-10 offset-xs-1"}]]
       [:div.col-xs-3
        [:h2 "right"]]])))

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
   [:div.col-xs-12
    [section]]])

(ns forum.components.utils
  (:require [reagent.core :as r]
            [cljsjs.jquery]))

(defn jQ
  [q]
  (.querySelector js/document q))

(defn indicator
  []
  [:div
   [:span.loader {:style {:display "inline-block"}}
    [:img {:src "/img/loader.svg"}]]] )


;; wrap!
(defn wrap-toggle
  ([page show ]
   (wrap-toggle page show nil))
  ([page show extra]
   [:div.row (merge-with merge extra {:style {:display (if show "block" "none") } })
    [:div.col-xs
     (if (vector? page)
       page  ;; if page is a vector, then should not wrapped in square brackets
       [page])]])
  )


(defn tab
  "tabs should be a map contains the title and content"
  [ tabs]
  (let [index (r/atom 1)
        ranger (range (count tabs))
        titles (map :title tabs)
        titles (zipmap titles ranger)
        contents (map :content tabs)
        contents (zipmap contents ranger)]
    (fn [ tabs]
      (print "index: " @index)
      [:div.container-fluid.tab
       [:div.row
        (map (fn [[t i]] ^{:key t} [:div.col-xs.tab-title  {:class (if (= @index i) "active")
                                                           :on-click #(reset! index i)}
                                   t]) titles)]
       [:div.row
        [:div.col-xs-12.tab-content
         (map (fn [[c i]] ^{:key c
                           :class "tab-content"} [wrap-toggle [:div  c] (= @index i)]) contents)]]])))

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
      "Register"]]]
   [:br]])


(defn auth
  "the login and register component"
  []
  [tab [{:title "Login"
         :content [login]}
        {:title "Register"
         :content [register]
         }]])

(defn modal
  [content]
  [:div#modal.modal.fade {:on-click (fn [e]  (let [modal (js/$ "#modal")
                                                  backdrop (js/$ "#backdrop")
                                                  that  (.querySelector js/document "#modal")]
                                              (print that)
                                              (print (.-target e))
                                              (if (= (.-target e) (.get modal 0))
                                                (do (print "should hide")
                                                    (.hide modal )
                                                    (.hide backdrop))
                                                (print "not equeal")
                                                )))}
   [:div.modal-dialog
    [:div.modal-content
     [content]]]]
  )
;; (defn show []
;;   (let [all (subscribe [:all])
;;         minus (subscribe [:minus])
;;         timeout (subscribe [:timeout])]
;;     (print (str @all))
;;     (fn []
;;       (print "minus: " @minus)
;;       [:div
;;        [indicator]
;;        [:h3 (str "timeout: " @timeout)]
;;        [:button.btn.btn-primary {:on-click #(dispatch [:minus (.random js/Math) (.random js/Math)])}]
;;        [:button.btn.btn-primary {:on-click #(dispatch [:timeout])} "dispatch"]
;;        [:button.btn.btn-primary {:on-click #(do (print (str @all))
;;                                                 )} "show all"]])))

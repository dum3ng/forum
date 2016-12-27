(ns forum.components.utils
  (:require [reagent.core :as r]
            [cljsjs.jquery]
            [re-frame.core :refer [subscribe dispatch]]
            [forum.components.auth :refer [register login]]))

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
        (doall  (map (fn [[t i]] ^{:key t} [:div.col-xs.tab-title  {:class (if (= @index i) "active")
                                                                   :on-click #(reset! index i)}
                                           t]) titles))]
       [:div.row
        [:div.col-xs-12.tab-content
         (doall (map (fn [[c i]] ^{:key c
                                  :class "tab-content"} [wrap-toggle [:div  c] (= @index i)]) contents))]]])))


(defn auth
  "the login and register component"
  []
  [tab [{:title "Login"
         :content [login]}
        {:title "Register"
         :content [register]
         }]])

(defn modal
  []
  (let [show (subscribe [:modal-state])
        content (subscribe [:modal-content])]
    (fn []
      [:div#modal.modal.fade {:style {:display  (if @show "block" "none")}
                              :on-click (fn [e]
                                          (if (= (.get (js/$ "#modal") 0) (.-target e))
                                            (dispatch [:set-modal-state false])))}
       [:div.modal-dialog
        [:div.modal-content
         (if (= @content :auth)
           [auth]
           [:div])]]])))

(defn modal-backdrop
  []
  (let [show (subscribe [:modal-state])]
    (fn []
      [:div#backdrop.modal-backdrop.fade {:style  {:display  (if @show "block" "none")} }])))

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

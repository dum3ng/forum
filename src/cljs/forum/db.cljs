(ns forum.db)

(defn- sections-value
  ([value]
   (sections-value value value value))
  ([x y z]
   (zipmap [:earth :mars :venus] [x y z]))
  )

(def default-db
  {:page   :home
   ;; user, can be nil
   :timeout "initial"
   :docs ""
   :user {}
   :self {}
   ;; the modal-content can be
   ;; :auth  and others...
   :modal-state false
   :modal-content :auth

   :section :earth
   ;; the 3 state of a section is:
   ;; :new, :single, :list
   :state-of-section (sections-value :list)
   :posts-in-section (sections-value nil)
   ;; :posts-in-earth {}
   ;; :posts-in-mars {}
   ;; :posts-in-venus {}

   :current-post-in-section (sections-value nil)
   :new-post-in-section (sections-value nil)
   :fetching-posts-in-section  (sections-value false)
   :comment-in-editing {}
   :fetching-user false

   :submitting-new-post-in-section (sections-value false)
   :submitting-new-comment false


   :register-submitting false
   :register-info ""
   })

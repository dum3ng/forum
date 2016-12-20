(ns forum.util)


(defn k->s
  [k]
  (cond
    (keyword? k)   (-> k
                       str
                       (subs 1))
    :else (str k)))

(defn kw->str
  [k]
  (cond
    (keyword? k)   (-> k
                       str
                       (subs 1))
    :else (str k)))

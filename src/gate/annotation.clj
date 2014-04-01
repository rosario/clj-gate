(ns gate.annotation
  (:import (gate Annotation AnnotationSet Document Utils)))


(defn to-map
  "Convert annotations to {:type type :values []} map"
  [doc anns]
  (reduce #(assoc %1
                  (.getType %2)
                  (conj
                    (get %1 (.getType %2) #{})
                    (annotation-value doc %2)))
          {}
          anns))


(defn annotation-types 
  "GATE Javadoc. Get a set of java.lang.String objects representing all the annotation types present in this annotation set."
  ([document] (.. document getAnnotations getAllTypes))
  ([document as-name] (-> document (.getAnnotations as-name) .getAllTypes)))
  

(defn annotations 
  ([document] (.getAnnotations document))
  ([document a-type] (.getAnnotations document a-type))
  ([document a-type a-name] (.get (.getAnnotations document a-type) a-name)))
  ;([document offset] (.get (.getAnnotations document) (long offset))))

(defn to-map 
  "Convert annotations to {:type type :values []} map"
  [doc anns]
  (reduce #(assoc %1 
                  (.getType %2)
                  (conj 
                    (get %1 (.getType %2) #{})
                    (Utils/cleanStringFor doc %2)))
          {}
          anns))

(defn sorted-offsets [document] 
  "Return set of all offsets in the document. Good for sequential traversal of the document"
  (apply sorted-set 
         (mapcat 
           #(vec [(.. % getStartNode getOffset) (.. % getEndNode getOffset)]) 
           (annotations document))))

(defn has-type [a-set t-name] (some #{t-name} (.getAllTypes a-set)))

(defn feature [annot f-name]
  "Get named feature form the annotation's feature map"
  (.get (.getFeatures annot) f-name))

(defn annotation-set-features
  "Return named features of annotations in annotation set"
  [a-set f-name]
  (filter #(not (nil? %)) (map #(feature % f-name) a-set)))

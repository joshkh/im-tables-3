(ns im-tables.views.table.core
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as reagent]
            [im-tables.views.table.head.main :as table-head]
            [im-tables.views.table.body.main :as table-body]
            [im-tables.views.dashboard.main :as dashboard]
            [imcljs.query :as q]
            [clojure.string :refer [split starts-with?]]))


(defn split-and-drop-last
  [path-str]
  (drop-last (split path-str #"\.")))

(defn split-on-dot
  [path-str]
  (split path-str #"\."))

(defn filter-not-join
  [path-str col]
  (filter #((complement clojure.string/starts-with?) % path-str) col))

(defn filter-join
  [path-str col]
  (filter #(clojure.string/starts-with? % path-str) col))

(defn mapify-columns [view display-name]
  {:view view :display-name display-name})

(defn joined? [v])

(def split-views (map #(clojure.string/split % #"\.") ["Gene.secondaryIdentifier"
                                                       "Gene.symbol"
                                                       "Gene.primaryIdentifier"
                                                       "Gene.organism.name"
                                                       "Gene.publications.firstAuthor"
                                                       "Gene.publications.title"
                                                       "Gene.publications.year"
                                                       "Gene.publications.journal"
                                                       "Gene.publications.volume"
                                                       "Gene.publications.pages"
                                                       "Gene.publications.pubMedId"]))

(def string-views ["Gene.secondaryIdentifier"
                   "Gene.symbol"
                   "Gene.primaryIdentifier"
                   "Gene.organism.name"
                   "Gene.publications.firstAuthor"
                   "Gene.publications.title"
                   "Gene.publications.year"
                   "Gene.publications.journal"
                   "Gene.publications.volume"
                   "Gene.publications.pages"
                   "Gene.publications.pubMedId"])

(defn head-contains?
  "True if a collection's head contains all elements of another collection (sub-coll)
  (coll-head-contains? [1 2] [1 2 3 4]) => true"
  [sub-coll coll]
  (every? true? (map = sub-coll coll)))

(def head-missing? (complement head-contains?))



(defn member-of-outer-join? [outer-join-str view]
  (starts-with? view outer-join-str))

(defn group-by-starts-with
  "Given a substring and a collection of strings, shift all occurences
  of strings beginning with that substring to immediately follow the first occurence
  ex: (group-by-starts-with apple [orange apple banana applepie apricot applejuice])
  => [orange apple applepie applejuice banana apricot]"
  [string-coll starts-with]
  (let [leading (take-while (partial head-missing? starts-with) string-coll)]
    (concat leading
            (filter (partial head-contains? starts-with) string-coll)
            (filter (partial head-missing? starts-with) (drop (count leading) string-coll)))))

(defn main [loc]
  (let [dragging-item (subscribe [:style/dragging-item loc])
        dragging-over (subscribe [:style/dragging-over loc])
        collapsed-views (subscribe [:query-response/views-collapsed-by-joins])]
    (fn [loc {:keys [results views]} {:keys [limit start] :or {limit 10 start 0}}]
      [:div.relative
       [:table.table.table-condensed.table-bordered.table-striped
        [:thead
         (into [:tr]
               (->> @collapsed-views
                    ;column-headers-without-joins
                    (map-indexed (fn [idx h]
                                   ^{:key (get views idx)}
                                   [table-head/header
                                    {:header h
                                     :dragging-over @dragging-over
                                     :dragging-item @dragging-item
                                     :loc loc
                                     :idx idx
                                     :subviews nil
                                     :col-count (count @collapsed-views)
                                     :view (get views idx)}]))))]
        (into [:tbody]
              (->>
                (map second (into (sorted-map) (select-keys results (range start (+ start limit)))))
                (map (fn [r] [table-body/table-row loc r]))))]])))

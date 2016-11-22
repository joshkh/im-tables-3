(ns im-tables.views.dashboard.main
  (:require [im-tables.views.dashboard.pagination :as pager]
            [im-tables.views.dashboard.manager.columns.main :as column-manager]
            [im-tables.views.dashboard.undo :as undo]
            [im-tables.views.dashboard.save :as save]))




(defn main []
  (fn [loc response pagination]
    [:div.container-fluid
     [:div.row
      [column-manager/main loc]
      ]
     [:div.row
      [:div.col-xs-6
       [:div.btn-toolbar
        [:button.btn.btn-primary {:data-toggle "modal"
                                  :data-target "#myModal"} "Add Columns"]
        [save/main loc response]]]
      [:div.col-xs-6
       [:div.container-fluid
        [:div.row
         [:div.col-xs-2 #_[:div.btn-toolbar
                           [save/main response]
                           [undo/main]]

          [:div.col-xs-12
           ]]
         [:div.col-xs-10 [:div.pull-right



                          [:div.pull-right [pager/main loc (merge pagination
                                                                  {:total (get response :iTotalRecords)})]]
                          [:span.pull-right
                           {:style {:padding-right "20px"}}
                           (str "Showing "
                                (inc (:start pagination)) " to "
                                (+ (:start pagination) (:limit pagination)) " of "
                                (:iTotalRecords response) " rows")]]]]]]]]))


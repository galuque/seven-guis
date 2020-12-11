(ns galuque.seven-guis.guis.flight-booker
  (:require [reagent.core :as r]
            ["@material-ui/core" :refer [Button 
                                         Grid 
                                         MenuItem 
                                         TextField]]
            [galuque.seven-guis.base.helpers :as h]))

(defonce state (r/atom {:depart (new js/Date)
                        :return (new js/Date)
                        :one-way? true
                        :valid-return? true
                        :valid-depart? true
                        :today (-> (new js/Date)
                                     (h/date->map)
                                     (h/date-map->default-date))}))

(defn flight-booker []
  [:> Grid {:container true :align-items :center :justify :center :direction "column"}
   [:> TextField {:select true :default-value :one-way-flight}
    [:> MenuItem {:value :one-way-flight
                  :on-click #(swap! state update-in [:one-way?] not)}
     "one-way flight"]
    [:> MenuItem {:value :return-flight
                  :on-click #(swap! state update-in [:one-way?] not)}
     "return flight"]]
   [:> TextField {:id :depart
                  :type "date"
                  :default-value (:today @state)
                  :on-change #(do
                                (swap! state assoc :depart (new js/Date (.. % -target -value)))
                                (swap! state assoc :valid-depart? (h/is-valid-depart? @state)))}]
   [:> TextField {:id :return
                  :type "date"
                  :default-value (:today @state)
                  :disabled (:one-way? @state)
                  :on-change #(do
                                (swap! state assoc :return (new js/Date  (.. % -target -value)))
                                (swap! state assoc :valid-return? (h/is-valid-return? @state)))}]
   [:> Button {:variant "outlined"
               :disabled (or (not (:valid-return? @state))
                             (not (:valid-depart? @state)))
               :on-click #(js/alert (h/booked-message @state))}
    "book"]])

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
  (let [toggle-flight-type   #(swap! state update-in [:one-way?] not)

        handle-depart-change #(do
                                (swap! state assoc :depart (new js/Date (.. % -target -value)))
                                (swap! state assoc :valid-depart? (h/is-valid-depart? @state)))

        handle-return-change #(do
                                (swap! state assoc :return (new js/Date  (.. % -target -value)))
                                (swap! state assoc :valid-return? (h/is-valid-return? @state)))

        handle-booked-click  #(js/alert (h/booked-message @state))

        button-disabled?      (not (and (:valid-depart? @state) 
                                        (:valid-return? @state)))]
    
    [:> Grid {:container true :align-items :center :justify :center :direction :column}
     [:> TextField {:select true :default-value :one-way-flight}
      [:> MenuItem {:value :one-way-flight
                    :on-click toggle-flight-type}
       "one-way flight"]
      [:> MenuItem {:value :return-flight
                    :on-click toggle-flight-type}
       "return flight"]]
     [:> TextField {:id :depart
                    :type :date
                    :default-value (:today @state)
                    :on-change handle-depart-change}]
     [:> TextField {:id :return
                    :type :date
                    :default-value (:today @state)
                    :disabled (:one-way? @state)
                    :on-change handle-return-change}]
     [:> Button {:variant :outlined
                 :disabled button-disabled?
                 :on-click handle-booked-click}
      "book"]]))

(comment
  (<= (new js/Date) (:depart @state))
  (h/is-valid-depart? @state)
  (<= (new js/Date (:today @state)) (:depart @state))
  (> 19 18 17))
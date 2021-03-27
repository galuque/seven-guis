(ns galuque.seven-guis.guis.flight-booker
  (:require ["@material-ui/core" :refer [Button 
                                         Grid 
                                         MenuItem 
                                         TextField]]
            [cljs.core.async :as async]
            [galuque.seven-guis.base.helpers :as h :refer [events->chan by-id]]
            [reagent.core :as r])
  (:import [goog.events EventType]))

(def styles {:grid {:container true :align-items :center :justify :center :direction :column}

             :flight-type {:id :input-select :name :input-select :class "MuiInputBase-root MuiInput-root MuiInput-underline MuiInputBase-formControl MuiInput-formControl"}
             
             :option {:class "MuiSelect-root MuiSelect-select MuiSelect-selectMenu MuiInputBase-input MuiInput-input"}})

(defonce state (r/atom {:depart (js/Date.)
                        :return (js/Date.)
                        :one-way? true
                        :valid-return? false
                        :valid-depart? false}))

(defn coordinate []
  (let [toggle-flight-type  (events->chan (by-id "input-select") EventType.CHANGE)
        depart-change       (events->chan (by-id "depart")       EventType.CHANGE)
        return-change       (events->chan (by-id "return")       EventType.CHANGE)
        book-click          (events->chan (by-id "book-button")  EventType.CLICK)]
    (async/go-loop []
      (async/alt!
        toggle-flight-type
        ([_] (swap! state update-in [:one-way?] not) (recur))

        depart-change
        ([e]
         (swap! state assoc :depart (js/Date. (.. e -target -value)))
         (swap! state assoc :valid-depart? (h/valid-depart? @state))
         (swap! state assoc :valid-return? (h/valid-return? @state))
         (recur))

        return-change
        ([e]
         (swap! state assoc :return (js/Date.  (.. e -target -value)))
         (swap! state assoc :valid-return? (h/valid-return? @state))
         (recur))

        book-click
        ([_] (js/alert (h/booked-message @state)) (recur))))))

(defn flight-booker []
  (r/create-class
   {:display-name "Flight Booker"

    :component-did-mount
    (fn [] (coordinate))

    :reagent-render
    (fn []
      (let [button-disabled? (not (or (and (:one-way? @state) (:valid-depart? @state))
                                      (and (:valid-depart? @state) (:valid-return? @state))))]
        [:> Grid (:grid styles)
         [:select  (:flight-type styles)
          [:option (merge (:option styles) {:value :one-way-flight}) "one-way flight"]
          [:option (merge (:option styles) {:value :return-flight}) "return flight"]]
         [:> TextField {:id :depart
                        :type :date
                        :default-value (-> (js/Date.) (h/date->map) (h/date-map->default-date))}]
         [:> TextField {:id :return
                        :type :date
                        :disabled (:one-way? @state)}]
         [:> Button {:id :book-button
                     :variant :outlined
                     :disabled button-disabled?}
          "book"]]))}))

(comment
  (h/is-valid-depart? @state)
  (<= (js/Date.) (:depart @state)) ;; BUG: js/Date. returns UTC time, after 10 pm ART it breaks
  ) 
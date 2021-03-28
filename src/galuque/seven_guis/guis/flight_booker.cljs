(ns galuque.seven-guis.guis.flight-booker
  (:require ["@material-ui/core" :refer [Button 
                                         Grid 
                                         MenuItem 
                                         TextField]]
            [cljs.core.async :as async]
            [galuque.seven-guis.base.helpers :as h :refer [events->chan by-id]]
            [reagent.core :as r]
            [goog.date :as date])
  (:import [goog.events EventType]))

(def styles {:grid {:container true :align-items :center :justify :center :direction :column}

             :flight-type {:id :input-select :name :input-select :class "MuiInputBase-root MuiInput-root MuiInput-underline MuiInputBase-formControl MuiInput-formControl"}
             
             :option {:class "MuiSelect-root MuiSelect-select MuiSelect-selectMenu MuiInputBase-input MuiInput-input"}})

(defn coordinate! [state]
  (let [toggle-flight-type  (events->chan (by-id "input-select") EventType.CHANGE)
        depart-change       (events->chan (by-id "depart-date")  EventType.CHANGE)
        return-change       (events->chan (by-id "return-date")  EventType.CHANGE)
        book-click          (events->chan (by-id "book-button")  EventType.CLICK)]
    (async/go-loop []
      (async/alt!
        toggle-flight-type
        ([_] (swap! state update-in [:one-way?] not) (recur))

        depart-change
        ([e]
         (println (.. e -target -value))
         (swap! state assoc :depart (date/Date. (.. e -target -value)))
         (swap! state assoc :valid-depart? (h/valid-depart? @state))
         (recur))

        return-change
        ([e]
         (swap! state assoc :return (date/Date.  (.. e -target -value)))
         (swap! state assoc :valid-return? (h/valid-return? @state))
         (recur))

        book-click
        ([_] (js/alert (h/booked-message @state)) (recur))))))

(defn flight-booker []
  (let [state (r/atom {:depart (date/Date.)
                       :return (date/Date.)
                       :one-way? true
                       :valid-depart? true
                       :valid-return? true})]
    (r/create-class
     {:display-name "Flight Booker"

      :component-did-mount
      (fn [] (coordinate! state))

      :reagent-render
      (fn []
        (let [button-disabled? (not (or (and (:one-way? @state) (:valid-depart? @state))
                                        (and (:valid-depart? @state) (:valid-return? @state))))]
          [:> Grid (:grid styles)
           [:select  (:flight-type styles)
            [:option (merge (:option styles) {:value :one-way-flight})
             "one-way flight"]
            [:option (merge (:option styles) {:value :return-flight})
             "return flight"]]
           [:> TextField {:id :depart-date
                          :type :date
                          :default-value (-> (date/Date.) (h/->date-str "-"))}]
           [:> TextField {:id :return-date
                          :type :date
                          :disabled (:one-way? @state)
                          :default-value (-> (date/Date.) (h/->date-str "-"))}]
           [:> Button {:id :book-button
                       :variant :outlined
                       :disabled button-disabled?}
            "book"]]))})))

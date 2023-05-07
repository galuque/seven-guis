(ns galuque.seven-guis.flight-booker
  (:require [uix.core :as uix :refer [defui $]]
            [galuque.seven-guis.base.components :refer [button centered-box]]))

(defn local-iso-str [^js date]
  (if date
    (.toLocaleDateString date "af")
    ""))

(def initial-state {:flight-type :one-way
                    :departure  (js/Date.)
                    :return (js/Date.)})

(defn one-way? [{:keys [flight-type]}]
  (= :one-way flight-type))

(defn book-button-disabled? [{:keys [flight-type departure return]}]
  (or (not departure)
      (not return)
      (and (= :return flight-type)
           (<= return departure))))

(defn message [{:keys [flight-type departure return]}]
  (case flight-type
    :one-way (str "You have booked a one-way flight on " (.toLocaleDateString departure))
    :return (str "You have booked a round-trip flight departing on " (.toLocaleDateString departure) " and returning on " (.toLocaleDateString return))))

(defmulti flight-booker-reducer (fn [_ action] (:type action)))

(defmethod flight-booker-reducer :flight-type [state {:keys [event]}]
  (let [value  (-> event .-target .-value (keyword))]
    (assoc state :flight-type value)))

(defmethod flight-booker-reducer :departure [state {:keys [event]}]
  (let [value (.. event -target -valueAsDate)]
    (assoc state :departure value)))

(defmethod flight-booker-reducer :return [state {:keys [event]}]
  (let [value (.. event -target -valueAsDate)]
    (assoc state :return value)))

(defui flight-booker []
  (let [[state dispatch!] (uix/use-reducer flight-booker-reducer initial-state)]
    ($ centered-box
       ($ :div.flex.flex-col.space-y-4
          ($ :select#flight-type
             {:name :flight-type
              :on-change #(dispatch! {:type :flight-type :event %})}
             ($ :option
                {:value :one-way}
                "One-way Flight")
             ($ :option
                {:value :return}
                "Return Flight"))
          ($ :input
             {:type "date"
              :placeholder "Departure"
              :min (local-iso-str (js/Date.))
              :value (local-iso-str (:departure state))
              :on-change #(dispatch! {:type :departure :event %})})
          ($ :input.disabled:opacity-25
             {:type "date"
              :placeholder "Return"

              :min (local-iso-str (js/Date.))
              :disabled (one-way? state)
              :value (local-iso-str (:return state))
              :on-change #(dispatch! {:type :return :event %})})
          ($ button
             {:disabled (book-button-disabled? state)
              :on-click #(js/alert (message state))}
             "Book")))))

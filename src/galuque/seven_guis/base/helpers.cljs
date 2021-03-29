(ns galuque.seven-guis.base.helpers
  (:require [cljs.core.async :as async :refer [chan close! put!]]
            [clojure.string :as str]
            [goog.events :as events]
            [goog.date :as date]
            [goog.string :as gstring]
            [goog.string.format]))

;; General helpers

(defn <<< [f & args]
  """
  Automatically adds a callback to a parameter list
  
  (go
    (js/console.log (<! (<<< search-google \"unicorn droppings\"))))
  """
  (let [c (chan)]
    (apply f (concat args [(fn [x]
                             (if (nil? x)
                               (close! c)
                               (put! c x)))]))
    c))

(defn by-id
  "Short-hand for document.getElementById(id)"
  [id]
  (.getElementById js/document id))

(defn events->chan
  "Given a target DOM element and event type return a channel of
  observed events. Can supply the channel to receive events as third
  optional argument."
  ([el event-type] (events->chan el event-type (chan)))
  ([el event-type c]
   (events/listen el event-type
                  (fn [e] (put! c e)))
   c))

(defn log [& args]
  (.log js/console args))

;; temp converter helpers

(defn C->F
  [temp]
  (+ (* temp (/ 9 5)) 32))

(defn F->C
  [temp]
  (* (- temp 32) (/ 5 9)))

;; Flight Booker helpers (better to just use cljs-time)

(defn valid-depart? [{:keys [depart]}]
  (let [today (date/Date.)]
    (<= (.compare date/Date today depart) 0)))

(defn valid-return? [{:keys [depart return]}]
  (<= (.compare date/Date depart return) 0))

(defn ->date-str 
  [date sep]
  (let [year     (.getYear date)
        month'   (inc (.getMonth date))
        month    (if (< month' 10) (str "0" month') month')
        day'     (.getDate date)
        day      (if (< day' 10) (str "0" day') day')]
    (str year sep month sep day)))

(defn booked-message
  [{:keys [depart return one-way?]}]
  (let [depart' (->date-str depart "/")
        return' (->date-str return "/")]
    (if one-way?
      (str "You have booked a one-way flight for " depart')
      (str "You have booked a return flight from " depart' " to " return'))))

;; timer helper
(defn update-elapsed!
  [state]
  (let [dt (- (. js/Date now)
              (:start @state))]
    (swap! state assoc :elapsed dt)))

(defn display-elapsed
  [state]
  (let [{:keys [elapsed]} state
        in-secs (/ elapsed 1000)]
    (gstring/format "%.1f" in-secs)))

(defn display-final-time
  [state]
  (let [{:keys [duration]} state]
    (gstring/format "%.1f" (* 0.3 duration))))

(defn progress
  [state]
  (let [{:keys [elapsed duration]} state
        elapsed-secs (/ elapsed 1000)]
    (* 333 (/ elapsed-secs
              duration))))

(defn elapsed-percent
  [state]
  (let [{:keys [elapsed duration]} state]
    (/ (* elapsed 100) duration)))


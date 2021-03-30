(ns galuque.seven-guis.base.helpers
  (:require [cljs.core.async :as async :refer [chan close! put!]]
            [clojure.string :as str]
            [goog.events :as events]
            [goog.date :as date]))

;; General helpers

(defn <<< [f & args]
  "" "
  Automatically adds a callback to a parameter list
  
  (go
    (js/console.log (<! (<<< search-google \"unicorn droppings\"))))
  " ""
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

(defn date<= 
  "Compares start date and end date
   returns true if start date if less or equeal to end date
   otherwhise returns false"
  [start end]
  (<= (.compare date/Date start end) 0))

(defn date->iso-str
  "Takes a goog.date.Date object and returns it's ISO string representation.
   The one arity version uses \"-\" as a separator, but a diffrent character 
   can be supplied as separator in the two arity version"
  ([date]
   (date->iso-str date "-"))
  ([date sep]
   (let [year     (.getYear date)
         month'   (inc (.getMonth date))
         month    (if (< month' 10) (str "0" month') month')
         day'     (.getDate date)
         day      (if (< day' 10) (str "0" day') day')]
     (str year sep month sep day))))

(defn booked-message
  [{:keys [depart return one-way?]}]
  (let [depart' (date->iso-str depart "/")
        return' (date->iso-str return "/")]
    (if one-way?
      (str "You have booked a one-way flight for " depart')
      (str "You have booked a return flight from " depart' " to " return'))))

;; timer helper

(defn now []
  (.now js/Date))

(defn update-elapsed!
  [state]
  (let [start   (:start @state)
        elapsed (- (now) start)]
    (swap! state assoc :elapsed elapsed)))

(defn elapsed-secs
  [{:keys [elapsed]}]
  (let [secs (/ elapsed 1000)]
    (.toFixed secs 1)))

(defn time-limit
  [{:keys [duration]}]
  (.toFixed (* 0.3 duration) 1))

(defn progress
  [{:keys [elapsed duration]}]
  (let [elapsed-secs (/ elapsed 1000)
        progress     (* 333 (/ elapsed-secs
                               duration))]
    (if (<= progress 100)
      progress
      100)))


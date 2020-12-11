(ns galuque.seven-guis.base.helpers
  (:require [goog.string :as gstring]
            [goog.string.format]))

;; counter helpers

(defn C->F
  [temp]
  (+ (* temp (/ 9 5)) 32))

(defn F->C
  [temp]
  (* (- temp 32) (/ 5 9)))

(defn only-digits
  [event]
  (let [code (.-code event)]
    (when (and (not (re-matches #"Digit.*"  code))
               (not (re-matches #"Numpad.*" code)))
      (.preventDefault event))))

;; Flight Booker helpers (better to just use cljs-time)

(defn is-valid-return?
  [state]
  (let [{:keys [depart return]} state]
    (if (< depart return)
      true
      false)))

(defn is-valid-depart?
  [state]
  (if (>= (new js/Date) (:depart state))
    false
    true))

(defn date->map
  [date]
  (let [month (.getUTCMonth    date)
        day   (.getUTCDate     date)
        year  (.getUTCFullYear date)]
    {:month (+ month 1)
     :day   (if (< (js/parseInt day) 10)
              (str "0" day)
              day)
     :year  year}))

(defn date-map->default-date
  [date-map]
  (let [{:keys [month day year]} date-map]
    (str  year "-" month "-" day)))

(defn date-map->print-date
  [date-map]
  (let [{:keys [month day year]} date-map]
    (str month "/" day "/" year)))

(defn booked-message
  [state]
  (let [{:keys [depart return one-way?]} state]
    (if one-way?
      (str "You have booked a one-way flight for " (date-map->print-date (date->map depart)))
      (str "You have booked a return flight from " (date-map->print-date (date->map depart)) " to " (date-map->print-date (date->map return))))))


;; timer helpers

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


(ns galuque.seven-guis.base.helpers
  (:require [cljs.core.async :as async :refer [chan close! put!]]
            [goog.events :as events]))

;; General helpers

(defn <<<
  "Automatically adds a callback to a parameter list
  
  (go
    (js/console.log (<! (<<< search-google \"unicorn droppings\"))))"
  [f & args]
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
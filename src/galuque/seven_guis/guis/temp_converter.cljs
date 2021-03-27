(ns galuque.seven-guis.guis.temp-converter
  (:require [reagent.core :as r]
            ["@material-ui/core" :refer [Grid TextField]]
            [galuque.seven-guis.base.helpers :as h]))

(defonce celsius (r/atom 0))
(defonce farenheit (r/atom (h/C->F @celsius)))

(defn converter []
   [:> Grid {:container true :align-items :center :justify :center}
    [:> TextField {:label "Celsius"
                   :value @celsius
                   :on-change #(do
                                 (reset! celsius (.. % -target -value))
                                 (reset! farenheit (Math/ceil (h/C->F @celsius))))
                   :on-key-press #(h/only-digits %)}]
    [:> TextField {:label "Farenheit"
                   :value @farenheit
                   :on-change #(do
                                 (reset! farenheit (.. % -target -value))
                                 (reset! celsius (Math/floor (h/F->C @farenheit))))
                   :on-key-press #(h/only-digits %)}]])

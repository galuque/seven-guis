(ns galuque.seven-guis.guis.counter
  (:require [reagent.core :as r]
            ["@material-ui/core" :refer [Button Container]]))

(defonce click-count (r/atom 0))

(defn counter []
   [:> Container {:align :center}
    [:p @click-count ]
    [:> Button {:variant :outlined
                :on-click #(swap! click-count inc)}
     "Count"]])

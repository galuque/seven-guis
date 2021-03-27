(ns galuque.seven-guis.guis.counter
  (:require ["@material-ui/core" :refer [Button Container]]
            [reagent.core :as r]))

(defonce click-count (r/atom 0))

(defn counter []
  [:> Container {:align :center}
   [:p @click-count]
   [:> Button {:variant :outlined
               :on-click #(swap! click-count inc)}
    "Count"]])

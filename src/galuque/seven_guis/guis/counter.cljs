(ns galuque.seven-guis.guis.counter
  (:require ["@material-ui/core" :refer [Button Container]]
            [reagent.core :as r]))

(defn counter []
  (let [click-count (r/atom 0)]
    (fn []
      [:> Container {:align :center}
       [:p @click-count]
       [:> Button {:variant :outlined
                   :on-click #(swap! click-count inc)}
        "Count"]])))

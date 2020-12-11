(ns galuque.seven-guis.guis.timer
  (:require [reagent.core :as r]
            ["@material-ui/core" :refer [Button 
                                         Container 
                                         LinearProgress 
                                         Slider]]
            [galuque.seven-guis.base.helpers :as h]))

(defonce state (r/atom {:start (. js/Date now)
                        :duration 50
                        :elapsed  0}))

(defn timer []
  (r/with-let [tick! (js/setInterval h/update-elapsed! 100 state)]
    [:> Container {:max-width "xs"}
     [:div "Elapsed Time: " (let [secs (h/display-elapsed @state)
                                  prog (h/progress @state)]
                              (if (<= prog 100)
                                secs
                                (h/display-final-time @state)))
      " s"]
     [:> LinearProgress {:variant "determinate"
                         :value (let [prog (h/progress @state)]
                                  (if (<= prog 100)
                                    prog
                                    100))}]
     [:div "Duration"]
     [:> Slider {:value (:duration @state)
                 :on-change
                 (fn [_ val]
                   (swap! state assoc :duration val))}]
     [:> Button {:on-click
                 #(do
                    (swap! state assoc :elapsed 0)
                    (swap! state assoc :start (. js/Date now)))}
      "Reset Timer"]]
    (finally (js/clearInterval tick!))))

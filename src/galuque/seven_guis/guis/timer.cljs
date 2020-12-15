(ns galuque.seven-guis.guis.timer
  (:require [reagent.core :as r]
            ["@material-ui/core" :refer [Button 
                                         Container 
                                         LinearProgress 
                                         Slider]]
            [galuque.seven-guis.base.helpers :as h]))

(defonce state (r/atom {:start (.now js/Date)
                        :duration 50
                        :elapsed  0}))

(defn timer []
  (let [secs                   (h/display-elapsed @state)
        
        prog                   (h/progress @state)
        
        final-elapsed          (h/display-final-time @state)
        
        elapsed-display        (if (<= prog 100) secs final-elapsed)
        
        prog-display           (if (<= prog 100) prog 100)
        
        handle-durarion-change (fn [_ val]
                                 (swap! state assoc :duration val))
        
        handle-reset         #(do
                                (swap! state assoc :elapsed 0)
                                (swap! state assoc :start (.now js/Date))) ]
    
    (r/with-let [tick! (js/setInterval h/update-elapsed! 100 state)]

      [:> Container {:max-width :xs}
       [:div "Elapsed Time: " elapsed-display " s"]
       [:> LinearProgress {:variant :determinate
                           :value prog-display}]
       [:div {:style {:margin-bottom :20px}}]
       [:div "Duration"]
       [:> Slider {:value (:duration @state)
                   :on-change handle-durarion-change}]
       [:> Button {:variant :outlined
                   :on-click handle-reset}
        "Reset Timer"]]
                
      (finally (js/clearInterval tick!)))))

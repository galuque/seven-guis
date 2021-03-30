(ns galuque.seven-guis.guis.timer
  (:require [reagent.core :as r]
            ["@material-ui/core" :refer [Button
                                         Container
                                         LinearProgress
                                         Slider]]
            [galuque.seven-guis.base.helpers :as h :refer [now]]))

(defn timer []
  (let [state                  (r/atom {:start (now)
                                        :duration 50
                                        :elapsed  0})

        secs                   (h/elapsed-secs @state)

        progress               (h/progress @state)

        limit                  (h/time-limit @state)

        elapsed                (if (< progress 100) secs limit)

        handle-durarion-change (fn [_ val]
                                 (swap! state assoc :duration val))

        handle-reset           (fn []
                                 (swap! state assoc :elapsed 0 :start (now)))]
    
    (r/with-let [tick! (js/setInterval h/update-elapsed! 100 state)]
      [:> Container {:max-width :xs}
       [:div "Elapsed Time: " elapsed " s"]
       [:> LinearProgress {:variant :determinate
                           :value progress}]
       [:div {:style {:margin-bottom :20px}}]
       [:div "Duration"]
       [:> Slider {:value (:duration @state)
                   :on-change handle-durarion-change}]
       [:> Button {:variant :outlined
                   :on-click handle-reset}
        "Reset Timer"]]

      (finally (js/clearInterval tick!)))))

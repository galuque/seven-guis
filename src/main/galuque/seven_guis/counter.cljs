(ns galuque.seven-guis.counter
  (:require [uix.core :as uix :refer [defui $]]
            [galuque.seven-guis.base.components :refer [button centered-box]]))

(defui counter []
  (let [[state set-state!] (uix/use-state 0)] 
    ($ centered-box
       ($ :div.border-2.border-stone-400.rounded-lg.p-4.m-4
          ($ :p.text-lg.font-bold
             state)
          ($ button
             {:on-click #(set-state! inc)}
             "Count")
          ($ button
             {:on-click #(set-state! 0)}
             "Reset")))))

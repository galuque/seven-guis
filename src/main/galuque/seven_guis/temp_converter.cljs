(ns galuque.seven-guis.temp-converter
  (:require [uix.core :as uix :refer [defui $]]
            [galuque.seven-guis.base.components :refer [centered-box]]))

(defn C->F [celsius]
  (+ (* celsius 1.8) 32))

(defn F->C [farenheit]
  (/ (- farenheit 32) 1.8))

(defn round [n]
  (.round js/Math n))

(def initial-state {:celsius 0 :farenheit 32})

(defmulti temp-reducer (fn [_ action] (:type action)))

(defmethod temp-reducer :celsius-change [_ {:keys [event]}]
  (let [val (.. event -target -value)
        celsius (round val)
        farenheit (-> val C->F round)]
    {:celsius celsius
     :farenheit farenheit}))

(defmethod temp-reducer :farenheit-change [_ {:keys [event]}]
  (let [val (.. event -target -value)
        farenheit (round val)
        celsius (-> val F->C round)]
    {:celsius celsius
     :farenheit farenheit}))

(defui temp-converter []
  (let [[state dispatch!] (uix/use-reducer temp-reducer initial-state)]
    ($ centered-box
       ($ :div.border-2.border-stone-400.rounded-lg.p-4.m-4
          ($ :div.align-middle.border-2.border-stone-200.rounded-lg
             ($ :label.text-g.font-bold
                {:for "celsius-input"} "Celsius"))
          ($ :input.text-center.shadow.border.rounded.focus:outline-none.focus:ring-2.focus:ring-stone-600
             {:type "number"
              :id "celsius-input"
              :on-change #(dispatch! {:type :celsius-change
                                      :event %})
              :value (:celsius state)})) 
       ($ :div.border-2.border-stone-400.rounded-lg.p-4.m-4
          ($ :div.align-middle.border-2.border-stone-200.rounded-lg
             ($ :label.text-g.font-bold
                {:for "farenheit-input"} "Farenheit"))
          ($ :input.text-center.shadow.border.rounded.focus:outline-none.focus:ring-2.focus:ring-stone-600
             {:type "number"
              :id "farenheit-input"
              :on-change #(dispatch! {:type :farenheit-change
                                      :event %})
              :value (:farenheit state)})))))

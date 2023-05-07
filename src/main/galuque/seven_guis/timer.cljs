(ns galuque.seven-guis.timer
  (:require [uix.core :as uix :refer [defui $]]
            [galuque.seven-guis.base.components :refer [button centered-box]]))

(def initial-state {:elapsed 0
                    :duration (* 10 10)})

(defn round [n]
  (.round js/Math n))


(defn time-elapsed [{:keys [elapsed]}]
  (str (round (/ elapsed 10)) "s"))

(defn duration-set [{:keys [duration]}]
  (str (round (/ duration 10)) "s"))

(defmulti timer-reducer (fn [_ action] (:type action)))

(defmethod timer-reducer :tick [{:keys [elapsed duration] :as state} _]
  (if (< elapsed duration)
    (update state :elapsed inc)
    state))

(defmethod timer-reducer :set-duration [state {:keys [event]}]
  (let [val (-> event .-target .-valueAsNumber round)]
    (assoc state :duration val)))

(defmethod timer-reducer :reset [state _]
  (assoc state :elapsed 0))

(defn tick-effect [interval dispatch!]
  (let [timer (js/setInterval #(dispatch! {:type :tick})
                              interval)]
    #(js/clearInterval timer)))

(defn with-tick [interval reducer-fn initial-state]
  (let [[state dispatch!] (uix/use-reducer reducer-fn initial-state)]
    (uix/use-effect #(tick-effect interval dispatch!)
                    [interval])
    [state dispatch!]))

(defn current-progress [{:keys [elapsed duration]}]
  (if (< elapsed duration)
    (str (.toFixed (* 100 (/ elapsed duration)) 1) "%")
    "100%"))


(defui timer []
  (let [[state dispatch!] (with-tick 100 timer-reducer initial-state)
        progress (current-progress state)]
    ($ centered-box
       ($ :div.text-center.my-8
          ($ :div.w-64.m-auto
             ($ :div.bg-gray-200.rounded-full.dark:bg-gray-700
                ($ :div.bg-stone-600.text-xs.font-medium.text-blue-100.text-center.p-0.5.rounded-full
                   {:style {:width progress}} progress)))
          ($ :div
             ($ :p.mt-2.mr-3 "Elapsed:")
             ($ :p.mt-2 (time-elapsed state)))
          ($ :div
             ($ :input.range.pr-6.accent-gray-700
                {:type "range"
                 :value (:duration state)
                 :min 0
                 :max (* 10 60)
                 :on-change #(dispatch! {:type :set-duration :event %})})
             ($ :p.text-stone-800 "Duration: ")   
             ($ :p.text-stone-800 (duration-set state) ))
          ($ :div
             ($ button
                {:on-click #(dispatch! {:type :reset})}
                "Reset"))))))


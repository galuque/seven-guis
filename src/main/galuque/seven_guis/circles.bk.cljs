(ns galuque.seven-guis.circles.bk
  (:require [uix.core :as uix :refer [defui $]]
            [uix.dom :refer [flush-sync]]
            [galuque.seven-guis.base.components :refer [button centered-box]]))


(def initial-state {:circles []
                    :index -1
                    :selected nil
                    :adjusting false
                    :adjusted false})

(defrecord Circle [x y r])

(defn current-circles [{:keys [circles index]}]
  (if index
    (subvec circles 0 (inc index))
    circles))

(defn can-undo? [index]
  ((complement neg?) index))

(defn can-redo? [index circles]
  (< (inc index) (count circles)))

(defn mouse-coords [svg event]
  (let [rect (.getBoundingClientRect svg)
        x (int (* (- (.-clientX event) (.-left rect))
                  (/ (.. svg -width -baseVal -value) (.-width rect))))
        y (int (* (- (.-clientY event) (.-top rect))
                  (/ (.. svg -height -baseVal -value) (.-height rect))))]
    [x y]))

(defmulti circle-drawer-reducer (fn [_ action] (:type action)))

(defmethod circle-drawer-reducer :add [{:keys [index circles adjusting] :as state} {:keys [svg-ref event]}]
  (if adjusting
    (assoc state :adjusting false :selected nil)
    ;; else
    (let [[x y] (mouse-coords @svg-ref event)
          circle (Circle. x y 15)
          new-index (inc index)
          new-circles (conj (subvec circles 0 new-index) circle)]
      (assoc state :index new-index :circles new-circles :selected circle :adjusting false))))

(defmethod circle-drawer-reducer :circle-click [{:keys [selected adjusting] :as state} {:keys [event circle]}]
  (.stopPropagation event)
  (if (and (not= selected circle) (not adjusting))
    (assoc state :selected circle)
    (assoc state :selected nil :adjusting false)))

(defmethod circle-drawer-reducer :change-radius [{:keys [circles selected] :as state} {:keys [event]}]
  (let [r (-> event .-target .-value js/parseInt)
        selected-circle? (fn [c] (= c selected))]
    (assoc state :circles (mapv (fn [c]
                                  (if (selected-circle? c)
                                    (assoc c :r r)
                                    c))
                                circles)
           :selected (assoc selected :r r)
           :adjusting true)))

(defmethod circle-drawer-reducer :adjust [state {:keys [event]}]
  (.preventDefault event)
  (if (:selected state)
    (assoc state :adjusting true)
    state))

(defmethod circle-drawer-reducer :undo [{:keys [index] :as state} _]
  (if (can-undo? index)
    (update state :index dec)
    state))

(defmethod circle-drawer-reducer :redo [{:keys [index circles] :as state} _]
  (if (can-redo? index circles)
    (update state :index inc)
    state))


(defn sync-dispatch! [dispatch! action]
  (flush-sync (fn [] (dispatch! action))))

(defui circle-drawer []
  (let [[state dispatch!] (uix/use-reducer circle-drawer-reducer initial-state)
        #_(.log js/console (clj->js state))
        svg* (uix/use-ref nil)
        menu* (uix/use-ref nil)]
    ($ centered-box
       (when (:adjusting state)
         ($ :div
            {:style {:position "absolute"
                     :width "80%"
                     :top "35%"
                     :left "50%"
                     :transform "translate(-50%, -50%)"
                     :padding "1em"
                     :text-align "center"}}
            ($ :input
               {:type "range"
                :min 10
                :max 50
                :value (or (:r (:selected state)) "")
                :on-change #(sync-dispatch! dispatch!
                                            {:type :change-radius
                                             :event %})})))
       ($ :div
          ($ button
             {:disabled (not (can-undo? (:index state)))
              :on-click #(dispatch! {:type :undo})}
             "Undo")
          ($ button
             {:disabled (not (can-redo? (:index state) (:circles state)))
              :on-click #(dispatch! {:type :redo})}
             "Redo")

          ($ :svg
             {:ref svg*
              :on-context-menu #(sync-dispatch! dispatch!
                                                {:type :adjust
                                                 :event %})
              :on-click #(dispatch! {:type :add
                                     :svg-ref svg*
                                     :event %})}

             (for [circle (current-circles state)
                   :let [id (random-uuid)]]
               ($ :circle
                  {:key id
                   :cx (:x circle)
                   :cy (:y circle)
                   :r (:r circle)
                   :stroke "black"
                   :stroke-width 1
                   :fill (if (= circle (:selected state))
                           "gray"
                           "transparent")
                   :on-click  #(sync-dispatch! dispatch!
                                               {:type :circle-click :event % :circle circle})})))))))

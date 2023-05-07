(ns galuque.seven-guis.circles2
  (:require [uix.core :as uix :refer [defui $]]
            [uix.dom :refer [flush-sync]]
            [galuque.seven-guis.base.components :refer [button centered-box]]))


(def initial-state {:scenes [[]]
                    :index 0
                    :current-scene []
                    :selected nil
                    :adjusting false
                    :adjusted false})

(defrecord Circle [x y r])

(defn can-undo? [index]
  (pos? index))

(defn can-redo? [index scenes]
  (< (inc index) (count scenes)))

(defn mouse-coords [svg-el event]
  (let [rect (.getBoundingClientRect svg-el)
        x (int (* (- (.-clientX event) (.-left rect))
                  (/ (.. svg-el -width -baseVal -value) (.-width rect))))
        y (int (* (- (.-clientY event) (.-top rect))
                  (/ (.. svg-el -height -baseVal -value) (.-height rect))))]
    [x y]))

(defmulti circle-drawer-reducer (fn [_ action] (:type action)))

(defn update-after-adjusted [{:keys [index scenes current-scene] :as state}]
  (let [new-index (inc index)
        new-scenes (conj (subvec scenes 0 new-index) current-scene)]
    (assoc state :index new-index :scenes new-scenes :selected nil :adjusting false :adjusted false)))

(defmethod circle-drawer-reducer :svg-click [{:keys [index scenes current-scene adjusted] :as state} {:keys [svg-ref event]}]
  (if adjusted
    (update-after-adjusted state)
    ;; else
    (let [[x y] (mouse-coords @svg-ref event)
          circle (Circle. x y 15)
          new-index (inc index)
          new-circles (conj current-scene circle)
          new-scenes (conj (subvec scenes 0 new-index) new-circles)]
      (assoc state :index new-index :scenes new-scenes :current-scene new-circles :selected circle :adjusting false))))

(defmethod circle-drawer-reducer :circle-click [{:keys [selected adjusting adjusted] :as state} {:keys [event circle]}]
  (.stopPropagation event)
  (cond

    adjusted
    (update-after-adjusted state)

    (and (not= selected circle) (not adjusting))
    (assoc state :selected circle)

    adjusting
    (assoc state :adjusting false :selected nil)

    :else
    state))

(defmethod circle-drawer-reducer :change-radius [{:keys [current-scene selected] :as state} {:keys [event]}]
  (let [r (-> event .-target .-value js/parseInt)
        selected-circle? (fn [c] (= c selected))
        new-circles (mapv (fn [c]
                            (if (selected-circle? c)
                              (assoc c :r r)
                              c))
                          current-scene)]
    (assoc state :current-scene new-circles :selected (assoc selected :r r) :adjusted true)))

(defmethod circle-drawer-reducer :adjust [state {:keys [event]}]
  (.preventDefault event)
  (if (:selected state)
    (assoc state :adjusting true)
    state))

(defn move [{:keys [index scenes] :as state} fun]
  (let [index' (fun index)
        scene' (get scenes index')]
    (assoc state :index index' :current-scene scene' :selected nil :adjusting false)))

(defmethod circle-drawer-reducer :undo [{:keys [index] :as state} _]
  (if (can-undo? index)
    (move state dec)
    state))

(defmethod circle-drawer-reducer :redo [{:keys [index scenes] :as state} _]
  (if (can-redo? index scenes)
    (move state inc)
    state))

(defn sync-dispatch! [dispatch! action]
  (flush-sync (fn [] (dispatch! action))))

(defui circle-drawer []
  (let [[state dispatch!] (uix/use-reducer circle-drawer-reducer initial-state)
        _(.log js/console (clj->js state))
        svg* (uix/use-ref nil)] 
    ($ centered-box 
       ($ :div
          ($ button
             {:disabled (not (can-undo? (:index state)))
              :on-click #(dispatch! {:type :undo})}
             "Undo")
          ($ button
             {:disabled (not (can-redo? (:index state) (:scenes state)))
              :on-click #(dispatch! {:type :redo})}
             "Redo")

          ($ :svg
             {:ref svg*
              :width "1200"
              :height "300"
              :style {:border "1px solid black"
                      :background-color "white"}
              :on-context-menu #(sync-dispatch! dispatch!
                                                {:type :adjust
                                                 :event %})
              :on-click #(dispatch! {:type :svg-click
                                     :svg-ref svg*
                                     :event %})}

             (for [circle (:current-scene state)
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
                                               {:type :circle-click :event % :circle circle})})))
          (when (:adjusting state)
            ($ :div
               {:style {:width "80%"
                        :z-index 100
                        :padding "1em"
                        :text-align "center"}}
               ($ :input
                  {:type "range"
                   :min 10
                   :max 50
                   :value (or (:r (:selected state)) "")
                   :on-input #(sync-dispatch! dispatch!
                                              {:type :change-radius
                                               :event %})})
               ($ :p "Adjust the radius of the selected circle")
               ($ :p "Current radius: " (:r (:selected state)))))))))

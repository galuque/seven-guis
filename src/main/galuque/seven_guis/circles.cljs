(ns galuque.seven-guis.circles
  (:require [uix.core :as uix :refer [defui $]]
            [galuque.seven-guis.base.components :refer [button centered-box]]))

(def initial-state {:events []
                   :last-event 0})

(defprotocol Drawable
  (draw! [this canvas])
  (erase! [this canvas])
  (fill! [this canvas color]))

(defrecord Circle [x y radius]
  Drawable
  (draw! [_ canvas]
    (let [ctx (.getContext canvas "2d")]
      (doto ctx
        (.beginPath)
        (.arc x y radius 0 (* 2 Math/PI))
        (.stroke))))

  (erase! [_ canvas]
    (let [ctx (.getContext canvas "2d")]
      (doto ctx
        (.beginPath)
        (.arc x y radius 0 (* 2 Math/PI))
        (.clear))))

  (fill! [_ canvas color]
    (let [ctx (.getContext canvas "2d")]
      (doto ctx
        (.beginPath)
        (.arc x y radius 0 (* 2 Math/PI))
        (.fillStyle color)
        (.fill)))))

(defmulti circle-drawer-reducer (fn [_ action] (:type action)))

(defmethod circle-drawer-reducer :event [{:keys [events last-event] :as state} {:keys [payload]}]
  (-> state
      (assoc :events (conj (subvec events 0 last-event) payload))
      (assoc :last-event (inc last-event))))

(defmethod circle-drawer-reducer :undo [{:keys [last-event] :as state} _]
  (if (pos? last-event) 
    (assoc state :last-event (dec last-event))
    state))

(defmethod circle-drawer-reducer :redo [{:keys [events last-event] :as state} _]
  (if (< last-event (count events))
    (assoc state :last-event (inc last-event))
    state))

(defn clear-canvas! [canvas]
  (let [ctx (.getContext canvas "2d")]
    (doto ctx
      (.clearRect 0 0 (.-width canvas) (.-height canvas)))))


(defn process-events! [{:keys [last-event events]} canvas]
  (clear-canvas! canvas)
  (->> (subvec events 0 last-event)
       (map :figure)
       (map #(draw! % canvas))
       (doall)))

(defn mouse-coords [canvas event]
  (let [rect (.getBoundingClientRect canvas)
        x (int (* (- (.-clientX event) (.-left rect)) (/ (.-width canvas) (.-width rect))))
        y (int (* (- (.-clientY event) (.-top rect)) (/ (.-height canvas) (.-height rect))))]
    [x y]))

(defn circle-in-mouse-position [canvas event]
  (let [[x y] (mouse-coords canvas event)]
    (Circle. x y 50)))

(defn distance [x1 y1 x2 y2]
  (Math/sqrt (+ (Math/pow (- x1 x2) 2) (Math/pow (- y1 y2) 2))))

(defn circle-in-range? [circle canvas event]
  (let [[x y] (mouse-coords canvas event)]
    (<= (distance x y (:x circle) (:y circle)) (:radius circle))))

(defn closest-circle [{:keys [last-event events]} canvas event]
  (let [[x y] (mouse-coords canvas event)
        current-figures (subvec events 0 last-event)
        figures (map :figure current-figures)
        distances (map #(distance (:x %) (:y %) x y) figures)
        min-distance (apply min distances)]
    (get current-figures (first (keep-indexed #(when (= %2 min-distance) %1) distances)))))


(defui circle-drawer []
  (let [[state dispatch!] (uix/use-reducer circle-drawer-reducer initial-state)
        canvas* (uix/use-ref nil)
        menu* (uix/use-ref nil)]
    (uix/use-effect
     (fn []
       (do
         (.log js/console @canvas*)
         (process-events! state @canvas*)))
     [state])
    ($ :div
       ($ button
          {:disabled (zero? (:last-event state))
           :on-click #(dispatch! {:type :undo})}
          "Undo")
       ($ button
          {:disabled (>= (:last-event state) (count (:events state)))
           :on-click #(dispatch! {:type :redo})}
          "Redo")
       ($ :canvas.border-2
          {:ref canvas*
           :width 1200
           :height 300
           :on-context-menu (fn [e] (.preventDefault e)
                              (js/console.log @menu*)
                              #_(swap! menu* assoc :style {:display :block :left (.-clientX e) :top (.-clientY e)}))
           :on-click #(dispatch! {:type :event
                                  :payload {:type :draw
                                            :figure-id (random-uuid)
                                            :figure (circle-in-mouse-position @canvas* %)}})})

       ($ :div
          {:ref menu*
           :display :none
           :position :absolute
           :visibility :hidden}
          ($ :label "Diameter")
          ($ :input
             {:type "number"
              :value 50
              :on-change #(dispatch! {:type :event
                                      :payload {:type :resize
                                                :figure-id (.-value (.-target %))
                                                :figure (Circle. 10 10 (-> % .-target .-value js/parseInt))}})})))))
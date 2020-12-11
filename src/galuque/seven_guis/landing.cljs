(ns galuque.seven-guis.landing
  (:require [reagent.dom :as rd]
            ["@material-ui/core" :refer [Typography]]
            [galuque.seven-guis.guis.counter :as counter]
            [galuque.seven-guis.guis.temp-converter :as tc]
            [galuque.seven-guis.guis.flight-booker :as fb]
            [galuque.seven-guis.guis.timer :as t]))

(def app-root (.getElementById js/document "app"))

(defn main []
  [:<>
   [:div {:style {:margin-bottom "40px"}}
    [:> Typography {:variant "h3" :align :center} "7GUIs"]]
   [:div {:style {:margin-bottom "40px"}}
    [:> Typography {:variant "h5" :align :center :style {:margin-bottom "20px"}} "Counter"]
    [counter/counter]]
   [:div {:style {:margin-bottom "40px"}}
    [:> Typography {:variant "h5" :align :center :style {:margin-bottom "20px"}} "Temperature Converter"]
    [tc/converter]]
   [:div {:style {:margin-bottom "40px"}}
    [:> Typography {:variant "h5" :align :center :style {:margin-bottom "20px"}} "Flight Booker"]
    [fb/flight-booker]]
   [:div {:style {:margin-bottom "40px"}}
    [:> Typography {:variant "h5" :align :center :style {:margin-bottom "20px"}} "Timer"]
    [t/timer]]])

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (js/console.log "start")
  (rd/render [main] app-root))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (js/console.log "init")
  (start))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))


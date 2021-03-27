(ns galuque.seven-guis.landing
  (:require [reagent.dom :as rdom]
            ["@material-ui/core" :refer [Divider Typography]]
            [galuque.seven-guis.guis.counter :refer [counter]]
            [galuque.seven-guis.guis.temp-converter :refer [converter]]
            [galuque.seven-guis.guis.flight-booker :refer [flight-booker]]
            [galuque.seven-guis.guis.timer :refer [timer]]
            [galuque.seven-guis.guis.CRUD :refer [CRUD]]))

(def app-root (.getElementById js/document "app"))

(defn main []
  [:<> 
   [:div {:style {:margin-bottom "40px"}}
    [:> Typography {:variant "h3" :align :center} "7GUIs"]]
   [:> Divider {:style {:margin-bottom "40px"}}]
   [:div {:style {:margin-bottom "40px"}}
    [:> Typography {:variant "h5" :align :center :style {:margin-bottom "20px"}} "Counter"]
    [counter]]
   [:> Divider {:style {:margin-bottom "40px"}}]
   [:div {:style {:margin-bottom "40px"}}
    [:> Typography {:variant "h5" :align :center :style {:margin-bottom "20px"}} "Temperature Converter"]
    [converter]]
   [:> Divider {:style {:margin-bottom "40px"}}]
   [:div {:style {:margin-bottom "40px"}}
    [:> Typography {:variant "h5" :align :center :style {:margin-bottom "20px"}} "Flight Booker"]
    [flight-booker]]
   [:> Divider {:style {:margin-bottom "40px"}}]
   [:div {:style {:margin-bottom "40px"}}
    [:> Typography {:variant "h5" :align :center :style {:margin-bottom "20px"}} "Timer"]
    [timer]]
   [:> Divider {:style {:margin-bottom "40px"}}]
   [:div {:style {:margin-bottom "40px"}}
    [:> Typography {:variant "h5" :align :center :style {:margin-bottom "20px"}} "CRUD"]
    [CRUD]]
   [:> Divider {:style {:margin-bottom "40px"}}]])

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (js/console.log "start")
  (rdom/render [main] app-root))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (js/console.log "init")
  (start))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))


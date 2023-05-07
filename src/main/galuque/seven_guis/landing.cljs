(ns galuque.seven-guis.landing
  (:require [uix.core :refer [defui $]]
            [uix.dom]
            [galuque.seven-guis.counter :refer [counter]] 
            [galuque.seven-guis.temp-converter :refer [temp-converter]]
            [galuque.seven-guis.flight-booker :refer [flight-booker]]
            [galuque.seven-guis.timer :refer [timer]]
            [galuque.seven-guis.CRUD :refer [CRUD]]
            [galuque.seven-guis.circles2 :refer [circle-drawer]]))


(defui app [] 
  ($ :div.align-middle.text-center
     ($ :h1.text-4xl.font-bold
        "Seven GUIs")
     ($ :div
        ($ :a
           {:href "https://eugenkiss.github.io/7guis/"}
           "https://eugenkiss.github.io/7guis/"))
     #_#_#_#_#_($ counter)
     ($ temp-converter)
     ($ flight-booker)
     ($ timer)
     ($ CRUD)
     ($ circle-drawer)))

(defonce root
  (uix.dom/create-root (.getElementById js/document "app")))

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (js/console.log "start")
  (uix.dom/render-root ($ app) root))

(defn ^:export init []
  (js/console.log "init")
  (start))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))


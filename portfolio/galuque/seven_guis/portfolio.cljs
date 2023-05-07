(ns galuque.seven-guis.portfolio
  (:require [portfolio.ui :as ui]
            [galuque.seven-guis.portfolio.counter]
            [galuque.seven-guis.portfolio.timer]
            [galuque.seven-guis.portfolio.temp-converter]))

(defn ^:dev/after-load start []
  (js/console.log "start")
  (ui/start!
   {:config
    {:css-paths ["/css/styles.css"]}}))

(defn ^:export init []
  (js/console.log "init")
  (start))
(ns galuque.seven-guis.portfolio.timer
  (:require [portfolio.react-18 :refer [defscene]]
            [uix.core :as uix :refer [$]]
            [galuque.seven-guis.timer :refer [timer]]))

(defscene timer-scene
  ($ timer))

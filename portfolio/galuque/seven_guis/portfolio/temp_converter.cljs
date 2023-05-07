(ns galuque.seven-guis.portfolio.temp-converter
  (:require [portfolio.react-18 :refer [defscene]]
            [uix.core :refer [$]]
            [galuque.seven-guis.temp-converter :refer [temp-converter]]))

(defscene temp-scene
  ($ temp-converter))
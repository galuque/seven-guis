(ns galuque.seven-guis.portfolio.counter
  (:require [portfolio.react-18 :refer [defscene]]
            [uix.core :as uix :refer [$]]
            [galuque.seven-guis.counter :refer [counter]]))

(defscene counter-scene
    ($ counter))

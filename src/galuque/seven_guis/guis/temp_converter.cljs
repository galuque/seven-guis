(ns galuque.seven-guis.guis.temp-converter
  (:require ["@material-ui/core" :refer [Grid TextField]]
            [galuque.seven-guis.base.helpers :as h :refer [events->chan by-id]]
            [reagent.core :as r]
            [cljs.core.async :as async])
  (:import [goog.events EventType]))

(def styles {:grid {:container true :align-items :center :justify :center}
             
             :celsius {:label "Celsius"
                       :id :celsius-input
                       :InputLabelProps {:shrink true}
                       :default-value "0"}
             
             :farenheit {:label "Farenheit"
                         :id :farenheit-input
                         :InputLabelProps {:shrink true}
                         :default-value "32"}})

(defn coordinate! []
  (let [celsius-el      (by-id "celsius-input")
        farenheit-el    (by-id "farenheit-input")
        celsius-input   (events->chan celsius-el   EventType.INPUT)
        farenheit-input (events->chan farenheit-el EventType.INPUT)
        update-elems!   (fn [e this-el other-el convert-fn]
                          (let [value     (js/parseFloat (.. e -target -value))
                                update?   (int? value)
                                other-val (str (Math/round (convert-fn value)))]
                            (if update?
                              (do
                                (set! (.-value other-el) other-val)
                                (set! (.-backgroundColor (.-style other-el)) "")
                                (set! (.-backgroundColor (.-style this-el))  ""))
                              (do
                                (set! (.-backgroundColor (.-style other-el)) "gray")
                                (set! (.-backgroundColor (.-style this-el))  "coral")))))]
    (async/go-loop []
      (async/alt!
        celsius-input
        ([e]
         (update-elems! e celsius-el farenheit-el h/C->F)
         (recur))

        farenheit-input
        ([e]
         (update-elems! e farenheit-el celsius-el h/F->C)
         (recur))))))

(defn converter []
  (r/create-class
   {:display-name "Temperature Converter"

    :component-did-mount
    (fn [] (coordinate!))

    :reagent-render
    (fn []
      [:> Grid (:grid styles)
       [:> TextField (:celsius styles)]
       [:> TextField (:farenheit styles)]])}))

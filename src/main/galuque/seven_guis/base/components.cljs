(ns galuque.seven-guis.base.components
  (:require [uix.core :as uix :refer [defui $]]
            [cljs.core :as core]))

(defui button [{:keys [on-click disabled children]}]
  ($ :button.bg-white.hover:bg-gray-200.text-gray-800.font-semibold.py-2.px-4.border.border-gray-400.rounded.shadow.disabled:opacity-25
     {:on-click on-click
      :disabled (or disabled false)}
     children))

(defui centered-box [{:keys [children]}]
  ($ :div.flex.items-center.justify-center.border-2.border-stone-200.rounded-lg.p-4.m-4
     children))

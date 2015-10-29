(ns {{project-ns}}.cards
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session])
  (:require-macros
   [devcards.core
    :as dc
    :refer [defcard defcard-doc defcard-rg deftest]]))

(enable-console-print!)

(defcard-rg first-card
  [:div>h1 "This is your first devcard!"])

(reagent/render [:div "This is working"] (.getElementById js/document "app"))

;; remember to run 'lein figwheel devcards' and then browse to
;; http://localhost:3449/cards

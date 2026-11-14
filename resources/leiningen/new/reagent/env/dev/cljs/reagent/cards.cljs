(ns {{project-ns}}.cards
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.dom.client :as rdomc]
            [{{project-ns}}.core :as core]
            [devcards.core :as dc])
  (:require-macros
   [devcards.core
    :as dc
    :refer [defcard defcard-doc defcard-rg deftest]]))

(defcard-rg first-card
  [:div>h1 "This is your first devcard!"])

(defcard-rg home-page-card
  [core/home-page])

(rdomc/render (rdomc/create-root (.getElementById js/document "app")) [:div])

;; remember to run 'lein figwheel devcards' and then browse to
;; http://localhost:3449/cards

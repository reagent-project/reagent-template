(ns {{project-ns}}.core
  (:require [reagent.core :as reagent :refer [atom]]{{{core-cljs-requires}}}))

(defonce app-state (atom {:text "Hello Reagent!"}))

(defn main []
  (reagent/render-component [:h1 (:text @app-state)] (.getElementById js/document "app")))

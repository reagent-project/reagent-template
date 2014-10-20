(ns {{project-ns}}.core
  (:require [reagent.core :as reagent :refer [atom]]))

(defonce app-state (atom {:text "Hello Reagent!"}))

(defn init! []
  (reagent/render-component [:h1 (:text @app-state)] (.getElementById js/document "app")))

(init!)

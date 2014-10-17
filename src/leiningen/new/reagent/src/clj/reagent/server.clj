(ns {{project-ns}}.server
  (:require [{{project-ns}}.dev :refer [browser-repl start-figwheel]]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [resources]]
            [selmer.parser :refer [render-file]]
            [noir.util.middleware :refer [app-handler]]
            [environ.core :refer [env]]{{{server-clj-requires}}}))

(defroutes routes
  (GET "/" req (render-file "templates/index.html" {:dev (env :is-dev)}))
  (resources "/")
  (resources "/react" {:root "react"})
  (route/not-found "Not Found"))

(def app (app-handler [routes] :formats [:transit-json]))

(ns {{project-ns}}.handler
  (:require [{{project-ns}}.dev :refer [browser-repl start-figwheel]]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [selmer.parser :refer [render-file]]
            [environ.core :refer [env]]))

(defroutes routes
  (GET "/" [] (render-file "templates/index.html" {:dev (env :dev?)}))
  (resources "/")
  (not-found "Not Found"))

(def app (wrap-defaults routes site-defaults))

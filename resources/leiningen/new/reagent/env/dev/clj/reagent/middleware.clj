(ns {{name}}.middleware
    (:require [ring.middleware.content-type :refer [wrap-content-type]]
              [ring.middleware.params :refer [wrap-params]]
              [prone.middleware :refer [wrap-exceptions]]
              [ring.middleware.reload :refer [wrap-reload]]))

(def middleware
  [wrap-params
   wrap-content-type
   wrap-exceptions
   wrap-reload])

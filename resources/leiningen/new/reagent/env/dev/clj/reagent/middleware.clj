(ns {{name}}.middleware
  (:require {{#compojure-hook?}}
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]
            {{/compojure-hook?}}
            {{#bidi-hook?}}
            [ring.middleware.session :refer [wrap-session]
            [ring.middleware.flash :refer [wrap-flash]
            {{/bidi-hook?}}))

{{#compojure-hook?}}
(defn wrap-middleware [handler]
  (-> handler
      (wrap-defaults site-defaults)
      wrap-exceptions
      wrap-reload))
{{/compojure-hook?}}
{{#bidi-hook?}}
(defn wrap-middleware [handler]
  (-> handler
      wrap-session
      wrap-flash))
{{/bidi-hook?}}
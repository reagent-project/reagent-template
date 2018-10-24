(ns {{project-ns}}.handler
  (:require {{#compojure-hook?}}
            [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            {{/compojure-hook?}}
            {{#bidi-hook?}}
            [bidi.ring :as bidi-ring]
            [ring.util.response :as res]
            {{/bidi-hook?}}
            [hiccup.page :refer [include-js include-css html5]]
            [{{name}}.middleware :refer [wrap-middleware]]
            [config.core :refer [env]]))

(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (include-js "/js/app.js")]))
{{#devcards-hook?}}

(defn cards-page []
  (html5
    (head)
    [:body
     mount-target
     (include-js "/js/app_devcards.js")])){{/devcards-hook?}}

{{#compojure-hook?}}
(defroutes routes
  (GET "/" [] (loading-page))
  (GET "/about" [] (loading-page))
  {{#devcards-hook?}}(GET "/cards" [] (cards-page)){{/devcards-hook?}}
  (resources "/")
  (not-found "Not Found"))

(def app (wrap-middleware #'routes))
{{/compojure-hook?}}

{{#bidi-hook?}}
(defn index-handler
  [request]
  (-> (loading-page)
      (res/response)
      (res/content-type "text/html")))

(def about-handler index-handler)
(def items-handler index-handler)
(def item-handler index-handler)

{{#devcards-hook?}}
(defn cards-handler
  [request]
  (-> (cards-page)
      (res/response)
      (res/content-type "text/html")))
{{/devcards-hook?}}
(def handler
  (bidi-ring/make-handler ["/" {"" index-handler
                                "items" {"" items-handler
                                         ["/item-" :item-id] item-handler}
                                "about" about-handler}
                           "/" (bidi-ring/resources-maybe {:prefix "public/"})
                           {{#devcards-hook?}}
                           "/cards" cards-handler
                           {{/devcards-hook?}}
                           true (res/not-found "Not found")]))

(def app (wrap-middleware handler))
{{/bidi-hook?}}

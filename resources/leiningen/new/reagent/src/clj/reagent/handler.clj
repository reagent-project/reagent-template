(ns {{project-ns}}.handler
  (:require
   [reitit.ring :as reitit-ring]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
   [hiccup.page :refer [include-js include-css html5]]
   [config.core :refer [env]]))

(def mount-target
  [:div#app
   [:h2 "Welcome to {{name}}"]
   [:p "please wait while shadow-cljs is waking up ..."]
   [:p "(Check the js console for hints if nothing exciting happens.)"]])

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

(defn index-handler
  [_request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (loading-page)})

;; The app only serves the SPA shell and static files, so it needs no session
;; or anti-forgery token. Leaving them off also keeps responses free of
;; Set-Cookie, which the shadow-cljs :dev-http server can't send as a list.
;; TODO: Restore plain site-defaults once shadow-http supports header values
;; that are lists (shadow/http/server/ring.clj throws "currently not
;; supporting header vector values" in shadow-http 0.1.17).
(def defaults
  (-> site-defaults
      (assoc :session false)
      (assoc-in [:security :anti-forgery] false)))

;; prone and ring-devel are only on the classpath in the :dev profile, so the
;; dev middleware is loaded at runtime instead of required in the ns form.
(def middleware
  (cond-> [#(wrap-defaults % defaults)]
    (env :dev) (into [@(requiring-resolve 'prone.middleware/wrap-exceptions)
                      @(requiring-resolve 'ring.middleware.reload/wrap-reload)])))

(def app
  (reitit-ring/ring-handler
   (reitit-ring/router
    [["/" {:get {:handler index-handler}}]
     ["/items"
      ["" {:get {:handler index-handler}}]
      ["/:item-id" {:get {:handler index-handler
                          :parameters {:path {:item-id int?}}}}]]
     ["/about" {:get {:handler index-handler}}]])
   (reitit-ring/routes
    (reitit-ring/create-resource-handler {:path "/" :root "/public"})
    (reitit-ring/create-default-handler))
   {:middleware middleware}))

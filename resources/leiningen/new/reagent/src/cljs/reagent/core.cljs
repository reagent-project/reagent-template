(ns {{project-ns}}.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [reagent.session :as session]
   [reitit.frontend :as reitit]
   {{#clerk-hook?}}[clerk.core :as clerk]{{/clerk-hook?}}
   [accountant.core :as accountant]))

;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/" :index]
    ["/items"
     ["" :items]
     ["/:item-id" :item]]
    ["/about" :about]]))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))

;; -------------------------
;; Page components

(defn home-page []
  (fn []
    [:span.main
     [:h1 "Welcome to {{name}}"]
     [:ul
      [:li [:a {:href (path-for :items)} "Items of {{name}}"]]
      [:li [:a {:href "/broken/link"} "Broken link"]]]]))



(defn items-page []
  (fn []
    [:span.main
     [:h1 "The items of {{name}}"]
     [:ul (map (fn [item-id]
                 [:li {:name (str "item-" item-id) :key (str "item-" item-id)}
                  [:a {:href (path-for :item {:item-id item-id})} "Item: " item-id]])
               (range 1 60))]]))


(defn item-page []
  (fn []
    (let [routing-data (session/get :route)
          item (get-in routing-data [:route-params :item-id])]
      [:span.main
       [:h1 (str "Item " item " of {{name}}")]
       [:p [:a {:href (path-for :items)} "Back to the list of items"]]])))


(defn about-page []
  (fn [] [:span.main
          [:h1 "About {{name}}"]]))


;; -------------------------
;; Translate routes -> page components

(defn page-for [route]
  (case route
    :index #'home-page
    :about #'about-page
    :items #'items-page
    :item #'item-page))


;; -------------------------
;; Page mounting component

(defn current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div
       [:header
        [:p [:a {:href (path-for :index)} "Home"] " | "
         [:a {:href (path-for :about)} "About {{name}}"]]]
       [page]
       [:footer
        [:p "{{name}} was generated by the "
         [:a {:href "https://github.com/reagent-project/reagent-template"} "Reagent Template"] "."]]])))

;; -------------------------
;; Initialize app

(defn mount-root []
  (rdom/render [current-page] (.getElementById js/document "app")))

(defn init! []
  {{#clerk-hook?}}
  (clerk/initialize!)
  {{/clerk-hook?}}
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (let [match (reitit/match-by-path router path)
            current-page (:name (:data  match))
            route-params (:path-params match)]
        {{#clerk-hook?}}
        (reagent/after-render clerk/after-render!)
        {{/clerk-hook?}}
        (session/put! :route {:current-page (page-for current-page)
                              :route-params route-params})
        {{#clerk-hook?}}
        (clerk/navigate-page! path)
        {{/clerk-hook?}}))
    :path-exists?
    (fn [path]
      (boolean (reitit/match-by-path router path)))})
  (accountant/dispatch-current!)
  (mount-root))

{{#shadow-cljs-hook?}}
(defn ^:dev/after-load reload! []
  (mount-root))
{{/shadow-cljs-hook?}}
(ns {{project-ns}}.core
    (:require [reagent.core :as reagent :refer [atom]]
              {{#secretary-hook?}}
              [secretary.core :as secretary :include-macros true]
              {{/secretary-hook?}}
              {{#bidi-hook?}}
              [reagent.session :as session]
              [bidi.bidi :as bidi]
              {{/bidi-hook?}}
              [accountant.core :as accountant]))

{{#secretary-hook?}}
;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to {{name}}"]
   [:div [:a {:href "/about"} "go to about page"]]])

(defn about-page []
  [:div [:h2 "About {{name}}"]
   [:div [:a {:href "/"} "go to the home page"]]])

;; -------------------------
;; Routes

(defonce page (atom #'home-page))

(defn current-page []
  [:div [@page]])

(secretary/defroute "/" []
  (reset! page #'home-page))

(secretary/defroute "/about" []
  (reset! page #'about-page))
{{/secretary-hook?}}
{{#bidi-hook?}}
;; -------------------------
;; Routes

(def app-routes
  ["/" {"" :index
        "items" {"" :items
                 ["/item-" :item-id] :item}
        "about" :about
        "missing-route" :missing-route
        true :four-o-four}])


;; -------------------------
;; Page components


(defn home-page []
  (fn []
    [:span.main
     [:h1 "Welcome to {{name}}"]
     [:ul
      [:li [:a {:href (bidi/path-for app-routes :items)} "Items of {{name}}"]]
      [:li [:a {:href (bidi/path-for app-routes :missing-route)} "Missing-route"]]
      [:li [:a {:href "/borken/link"} "Borken link"]]]]))



(defn items-page []
  (fn []
    [:span.main
     [:h1 "The items of {{name}}"]
     [:ul (map (fn [item-id]
                 [:li {:name (str "item-" item-id) :key (str "item-" item-id)}
                  [:a {:href (bidi/path-for app-routes :item :item-id item-id)} "Item: " item-id]])
               (range 1 6))]]))


(defn item-page []
  (fn []
    (let [routing-data (session/get :route)
          item (get-in routing-data [:route-params :item-id])]
      [:span.main
       [:h1 (str "Item " item " of {{name}}")]
       [:p [:a {:href (bidi/path-for app-routes :items)} "Back to the list of items"]]])))


(defn about-page []
  (fn [] [:span.main
          [:h1 "About {{name}}"]]))


(defn four-o-four-page []
  "Non-existing routes go here"
  (fn []
    [:span.main
     [:h1 "404: It is not here"]
     [:pre.verse
      "What you are looking for,
I do not have.
How could I have,
what does not exist?"]]))


(defn default-page []
  "Configured routes, missing an implementation, go here"
  (fn []
    [:span.main
     [:h1 "404: My bad"]
     [:pre.verse
      "This page should be here,
but it is not."]]))


;; -------------------------
;; Translate routes -> page components

(defn page-for [route]
  (case route
    :index #'home-page
    :about #'about-page
    :items #'items-page
    :item #'item-page
    :four-o-four #'four-o-four-page
    #'default-page))


;; -------------------------
;; Page mounting component

(defn current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div
       [:header
        [:p [:a {:href (bidi/path-for app-routes :index)} "Go home"] " | "
         [:a {:href (bidi/path-for app-routes :about)} "See about"]]]
       [page]
       [:footer
        [:p "(Using "
         [:a {:href "https://reagent-project.github.io/"} "Reagent"] ", "
         [:a {:href "https://github.com/juxt/bidi"} "Bidi"] " & "
         [:a {:href "https://github.com/venantius/accountant"} "Accountant"]
         ")"]]])))
{{/bidi-hook?}}

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       {{#secretary-hook?}}
       (secretary/dispatch! path))
       {{/secretary-hook?}}
       {{#bidi-hook?}}
       (let [match (bidi/match-route app-routes path)
             current-page (:handler match)
             route-params (:route-params match)]
         (session/put! :route {:current-page (page-for current-page)
                               :route-params route-params})))
       {{/bidi-hook?}}
     :path-exists?
     (fn [path]
       {{#secretary-hook?}}
       (secretary/locate-route path))
       {{/secretary-hook?}}
       {{#bidi-hook?}}
       (boolean (bidi/match-route app-routes path)))
       {{/bidi-hook?}}
      :reload-same-path? true})
  (accountant/dispatch-current!)
  (mount-root))

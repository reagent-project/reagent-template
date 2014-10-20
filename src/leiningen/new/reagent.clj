(ns leiningen.new.reagent
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files
                                             sanitize sanitize-ns project-name]]
            [leiningen.core.main :as main]
            [clojure.string :refer [join]]))

(def render (renderer "reagent"))

(defn wrap-indent [wrap n list]
  (fn []
    (->> list
         (map #(str "\n" (apply str (repeat n " ")) (wrap %)))
         (join ""))))

(defn dep-list [n list]
  (wrap-indent #(str "[" % "]") n list))

(defn indent [n list]
  (wrap-indent identity n list))

(defn cljx? [opts]
  (some #{"+cljx"} opts))

(def cljx-plugin
  "com.keminglabs/cljx \"0.4.0\" :exclusions [org.clojure/clojure]")

(def cljx-source-paths
  " \"target/generated/clj\" \"target/generated/cljx\"")

(defn project-plugins [opts]
  (cond-> []
          (cljx? opts) (conj cljx-plugin)))

(defn project-nrepl-middleware [opts]
  (cond-> []
          (cljx? opts) (conj "cljx.repl-middleware/wrap-cljx")))

(defn template-data [name opts]
  {:full-name name
   :name (project-name name)
   :project-goog-module (sanitize (sanitize-ns name))
   :project-ns (sanitize-ns name)
   :sanitized (name-to-path name)
   :project-dev-plugins (dep-list 29 (project-plugins opts))
   :nrepl-middleware (indent 53 (project-nrepl-middleware opts))

   ;; cljx
   :cljx-source-paths (if (cljx? opts) cljx-source-paths "")
   :cljx-extension (if (cljx? opts) "|\\.cljx")
   :cljx-cljsbuild-spath (if (cljx? opts) " \"target/generated/cljs\"" "")
   :cljx-hook? (fn [block] (if (cljx? opts) (str block "\n") ""))
   :cljx-build? (fn [block] (if (cljx? opts) (str block "\n") ""))
   :cljx-uberjar-hook (if (cljx? opts) "cljx.hooks " "")})

(defn format-files-args [name opts]
  (let [data (template-data name opts)
        args [data
              ["project.clj"
               (render "project.clj" data)]
              ["resources/templates/index.html" (render "resources/templates/index.html" data)]
              ["resources/public/css/site.css" (render "resources/public/css/site.css" data)]
              ["src/clj/{{sanitized}}/handler.clj" (render "src/clj/reagent/handler.clj" data)]
              ["src/clj/{{sanitized}}/dev.clj" (render "src/clj/reagent/dev.clj" data)]
              ["src/cljs/{{sanitized}}/core.cljs" (render "src/cljs/reagent/core.cljs" data)]
              ["env/dev/cljs/{{sanitized}}/dev.cljs" (render "env/dev/cljs/reagent/dev.cljs" data)]
              ["env/prod/cljs/{{sanitized}}/prod.cljs" (render "env/prod/cljs/reagent/prod.cljs" data)]
              ["LICENSE" (render "LICENSE" data)]
              ["README.md" (render "README.md" data)]
              [".gitignore" (render ".gitignore" data)]

             ;; Heroku support
             ["system.properties" (render "system.properties" data)]
             ["Procfile" (render "Procfile" data)]]]

    (if (cljx? opts)
      (conj args ["src/cljx/{{sanitized}}/core.cljx" (render "src/cljx/reagent/core.cljx" data)])
      args)))

(defn reagent [name & opts]
  (main/info "Generating fresh 'lein new' Reagent project.")
  (apply ->files (format-files-args name opts)))

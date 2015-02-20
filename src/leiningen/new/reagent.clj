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




;;; Some libraries and plugins should not be included as dependencies
;;; if we are only making a library instead of an app.

(defn lib? [opts]
  (some #{"+lib"} opts))

(def lib-or-app-dependencies
  "Dependencies for development or as part of an app."
  '[[org.clojure/clojurescript "0.0-2850" :scope "provided"]
    [ring "1.3.2"]
    [ring/ring-defaults "0.1.3"]
    [prone "0.8.0"]
    [compojure "1.3.2"]
    [selmer "0.8.0"]
    [environ "1.0.0"]])

(def lib-or-app-plugins
  "Plugins for development or as part of an app."
  '[[lein-cljsbuild "1.0.4"]
    [lein-environ "1.0.0"]
    [lein-ring "0.9.1"]
    [lein-asset-minifier "0.2.2"]])

(defn app-dependencies [opts]
  (when-not (lib? opts)
    ((wrap-indent str 17 lib-or-app-dependencies))))

(defn lib-dependencies [opts]
  (when (lib? opts)
    ((wrap-indent str 34 lib-or-app-dependencies))))


(defn app-plugins [opts]
  (when-not (lib? opts)
    (str ":plugins ["((wrap-indent str 12 lib-or-app-plugins))"]")))

(defn lib-plugins [opts]
  (when (lib? opts)
    ((wrap-indent str 29 lib-or-app-plugins))))


(defn test? [opts]
  (some #{"+test"} opts))

(def test-plugin "com.cemerick/clojurescript.test \"0.3.3\"")

(def test-source-paths "\"src/cljs\" \"test/cljs\"")


(defn cljx? [opts]
  (some #{"+cljx"} opts))

(def cljx-plugin
  "com.keminglabs/cljx \"0.5.0\" :exclusions [org.clojure/clojure]")

(def cljx-source-paths
  " \"target/generated/clj\" \"target/generated/cljx\"")

(defn project-plugins [opts]
  (cond-> []
    (cljx? opts) (conj cljx-plugin)
    (test? opts) (conj test-plugin)))

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

   :app-dependencies (app-dependencies opts)
   :lib-dependencies (lib-dependencies opts)

   :app-plugins (app-plugins opts)
   :lib-plugins (lib-plugins opts)

   ;; cljx
   :cljx-source-paths (if (cljx? opts) cljx-source-paths "")
   :cljx-extension (if (cljx? opts) "|\\.cljx")
   :cljx-cljsbuild-spath (if (cljx? opts) " \"target/generated/cljs\"" "")
   :cljx-hook? (fn [block] (if (cljx? opts) (str block "\n") ""))
   :cljx-build? (fn [block] (if (cljx? opts) (str block "\n") ""))
   :cljx-uberjar-hook (if (cljx? opts) "cljx.hooks " "")

   ;; test
   :test-hook? (fn [block] (if (test? opts) (str block "") ""))})

(defn format-files-args [name opts]
  (let [data (template-data name opts)
        args [data
              ["project.clj"
               (render "project.clj" data)]
              ["resources/templates/index.html" (render "resources/templates/index.html" data)]
              ["resources/public/css/site.css" (render "resources/public/css/site.css" data)]
              ["src/clj/{{sanitized}}/handler.clj" (render "src/clj/reagent/handler.clj" data)]
              ["src/clj/{{sanitized}}/server.clj" (render "src/clj/reagent/server.clj" data)]
              ["env/dev/clj/{{sanitized}}/dev.clj" (render "env/dev/clj/reagent/dev.clj" data)]
              ["src/cljs/{{sanitized}}/core.cljs" (render "src/cljs/reagent/core.cljs" data)]
              ["env/dev/cljs/{{sanitized}}/dev.cljs" (render "env/dev/cljs/reagent/dev.cljs" data)]
              ["env/prod/cljs/{{sanitized}}/prod.cljs" (render "env/prod/cljs/reagent/prod.cljs" data)]
              ["LICENSE" (render "LICENSE" data)]
              ["README.md" (render "README.md" data)]
              [".gitignore" (render ".gitignore" data)]

             ;; Heroku support
             ["system.properties" (render "system.properties" data)]
              ["Procfile" (render "Procfile" data)]]
        args (if (cljx? opts)
               (conj args ["src/cljx/{{sanitized}}/util.cljx" (render "src/cljx/reagent/util.cljx" data)])
               args)
        args (if (test? opts)
               (conj args ["test/cljs/{{sanitized}}/core_test.cljs" (render "test/cljs/reagent/core_test.cljs" data)]
                     ["test/vendor/console-polyfill.js" (render "test/vendor/console-polyfill.js" data)]
                     ["test/vendor/es5-sham.js" (render "test/vendor/es5-sham.js" data)]
                     ["test/vendor/es5-shim.js" (render "test/vendor/es5-shim.js" data)])
               args)]
    args

    ))

(defn reagent [name & opts]
  (main/info "Generating fresh 'lein new' Reagent project.")
  (apply ->files (format-files-args name opts)))

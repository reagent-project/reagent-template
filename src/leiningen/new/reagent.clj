(ns leiningen.new.reagent
  (:require [leiningen.new.templates
             :refer [renderer name-to-path ->files
                     sanitize sanitize-ns project-name]]
            [leiningen.core.main :as main]
            [clojure.string :as string]))

(def render (renderer "reagent"))

(def valid-opts ["+test" "+less" "+sass" "+cider" "-clerk"])

(def removed-opts
  {"+figwheel" "Figwheel support was removed, projects now always use shadow-cljs."
   "+spec" "speclj support was removed, use +test for cljs.test with shadow-cljs."
   "+devcards" "Devcards support was removed."})

(defn less? [opts]
  (some #{"+less"} opts))

(defn sass? [opts]
  (some #{"+sass"} opts))

(defn test? [opts]
  (some #{"+test"} opts))

(defn cider? [opts]
  (some #{"+cider"} opts))

(defn clerk? [opts]
  (not (some #{"-clerk"} opts)))

(defn validate-opts [opts]
  (let [invalid-opts (remove (set valid-opts) opts)]
    (when (seq invalid-opts)
      (str "invalid options supplied: " (string/join " " invalid-opts)
           (some->> (keep removed-opts invalid-opts)
                    seq
                    (string/join "\n")
                    (str "\n"))
           "\nvalid options are: " (string/join " " valid-opts)))))

(defn template-data [name opts]
  {:full-name name
   :name (project-name name)
   :project-goog-module (sanitize (sanitize-ns name))
   :project-ns (sanitize-ns name)
   :sanitized (name-to-path name)

   :test-hook? (fn [block] (if (test? opts) (str block "") ""))

   :less-hook? (fn [block] (if (less? opts) (str block "") ""))

   :sass-hook? (fn [block] (if (sass? opts) (str block "") ""))

   :less-or-sass-hook?
   (fn [block] (if (or (less? opts) (sass? opts)) (str block "") ""))

   :cider-hook? (fn [block] (if (cider? opts) (str block "") ""))

   :clerk-hook? (fn [block] (if (clerk? opts) (str block "") ""))})

(defn format-files-args [name opts]
  (let [data (template-data name opts)
        args [data
              ["project.clj" (render "project.clj" data)]
              ["shadow-cljs.edn" (render "shadow-cljs.edn" data)]
              ["package.json" (render "package.json" data)]
              ["resources/public/css/site.css" (render "resources/public/css/site.css" data)]
              ["src/clj/{{sanitized}}/handler.clj" (render "src/clj/reagent/handler.clj" data)]
              ["src/clj/{{sanitized}}/server.clj" (render "src/clj/reagent/server.clj" data)]
              ["env/prod/clj/{{sanitized}}/middleware.clj" (render "env/prod/clj/reagent/middleware.clj" data)]
              ["env/dev/clj/{{sanitized}}/middleware.clj" (render "env/dev/clj/reagent/middleware.clj" data)]
              ["env/dev/clj/{{sanitized}}/repl.clj" (render "env/dev/clj/reagent/repl.clj" data)]
              ["src/cljs/{{sanitized}}/core.cljs" (render "src/cljs/reagent/core.cljs" data)]
              ["src/cljc/{{sanitized}}/util.cljc" (render "src/cljc/reagent/util.cljc" data)]
              #_["LICENSE" (render "LICENSE" data)]
              ["README.md" (render "README.md" data)]
              [".gitignore" (render "gitignore" data)]]
        args (if (test? opts)
               (conj args
                     ["test/cljs/{{sanitized}}/core_test.cljs" (render "test/cljs/reagent/core_test.cljs" data)]
                     ["karma.conf.js" (render "karma.conf.js" data)])
               args)
        args (if (less? opts)
               (conj args
                     ["src/less/site.main.less" (render "src/less/site.main.less" data)]
                     ["src/less/profile.less" (render "src/less/profile.less" data)])
               args)
        args (if (sass? opts)
               (conj args
                     ["src/sass/site.scss" (render "src/sass/site.scss" data)]
                     ["src/sass/_profile.scss" (render "src/sass/_profile.scss" data)])
               args)]
    args))

(defn reagent [name & opts]
  (main/info "Generating fresh 'lein new' Reagent project.")
  (if-let [error (validate-opts opts)]
    (println error)
    (apply ->files (format-files-args name opts))))

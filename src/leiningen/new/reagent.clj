(ns leiningen.new.reagent
  (:require [leiningen.new.templates
             :refer [renderer name-to-path ->files
                     sanitize sanitize-ns project-name]]
            [leiningen.core.main :as main]
            [clojure.string :as string]))

(def render (renderer "reagent"))

(defn wrap-indent [wrap n list]
  (fn []
    (->> list
         (map #(str "\n" (apply str (repeat n " ")) (wrap %)))
         (string/join ""))))

(defn indent [n list]
  (wrap-indent identity n list))

(def valid-opts ["+test" "+spec" "+less" "+sass" "+devcards" "+cider" "+figwheel" "-clerk"])

(defn less? [opts]
  (some #{"+less"} opts))

(defn sass? [opts]
  (some #{"+sass"} opts))

(defn test? [opts]
  (some #{"+test"} opts))

(defn spec? [opts]
  (some #{"+spec"} opts))

(defn devcards? [opts]
  (some #{"+devcards"} opts))

(defn cider? [opts]
  (some #{"+cider"} opts))

(defn figwheel? [opts]
  (some #{"+figwheel"} opts))

(defn clerk? [opts]
  (not (some #{"-clerk"} opts)))

(defn validate-opts [opts]
  (let [invalid-opts (remove (set valid-opts) opts)]
    (cond
      (seq invalid-opts)
      (str "invalid options supplied: " (string/join " " invalid-opts)
           "\nvalid options are: " (string/join " " valid-opts))

      (and (test? opts) (spec? opts)) "Both +test and +spec options can't be used together, select one.")))

(defn jvm>8? []
  (try
    (> (Double/parseDouble (subs (System/getProperty "java.version") 0 3)) 1.8)
    (catch Exception _)))

(defn template-data [name opts]
  {:full-name name
   :name (project-name name)
   :project-goog-module (sanitize (sanitize-ns name))
   :project-ns (sanitize-ns name)
   :sanitized (name-to-path name)

   :test-hook? (fn [block] (if (test? opts) (str block "") ""))
   :spec-hook? (fn [block] (if (spec? opts) (str block "") ""))

   :test-or-spec-hook?
   (fn [block] (if (or (test? opts) (spec? opts)) (str block "") ""))

   :less-hook? (fn [block] (if (less? opts) (str block "") ""))

   :sass-hook? (fn [block] (if (sass? opts) (str block "") ""))

   :less-or-sass-hook?
   (fn [block] (if (or (less? opts) (sass? opts)) (str block "") ""))

   :devcards-hook? (fn [block] (if (devcards? opts) (str block "") ""))

   :cider-hook? (fn [block] (if (cider? opts) (str block "") ""))

   :shadow-cljs-hook? (fn [block] (if (figwheel? opts) "" (str block "")))

   :clerk-hook? (fn [block] (if (clerk? opts) (str block "") ""))})

(defn format-files-args [name opts]
  (let [data (template-data name opts)
        args [data
              ["project.clj" (render "project.clj" data)]
              ["resources/public/css/site.css" (render "resources/public/css/site.css" data)]
              ["src/clj/{{sanitized}}/handler.clj" (render "src/clj/reagent/handler.clj" data)]
              ["src/clj/{{sanitized}}/server.clj" (render "src/clj/reagent/server.clj" data)]
              ["env/prod/clj/{{sanitized}}/middleware.clj" (render "env/prod/clj/reagent/middleware.clj" data)]
              ["env/dev/clj/{{sanitized}}/middleware.clj" (render "env/dev/clj/reagent/middleware.clj" data)]
              ["env/dev/clj/{{sanitized}}/repl.clj" (render "env/dev/clj/reagent/repl.clj" data)]
              ["env/dev/clj/user.clj" (render "env/dev/clj/user.clj" data)]
              ["src/cljs/{{sanitized}}/core.cljs" (render "src/cljs/reagent/core.cljs" data)]
              ["src/cljc/{{sanitized}}/util.cljc" (render "src/cljc/reagent/util.cljc" data)]
              ["env/dev/cljs/{{sanitized}}/dev.cljs" (render "env/dev/cljs/reagent/dev.cljs" data)]
              ["env/prod/cljs/{{sanitized}}/prod.cljs" (render "env/prod/cljs/reagent/prod.cljs" data)]
              #_["LICENSE" (render "LICENSE" data)]
              ["README.md" (render "README.md" data)]
              [".gitignore" (render "gitignore" data)]
             ;; Heroku support
              ["system.properties" (render "system.properties" data)]
              ["Procfile" (render "Procfile" data)]]
        args (if (test? opts)
               (conj args ["test/cljs/{{sanitized}}/core_test.cljs" (render "test/cljs/reagent/core_test.cljs" data)]
                     ["test/cljs/{{sanitized}}/doo_runner.cljs" (render "runners/doo_runner.cljs" data)])
               args)
        args (if (spec? opts)
               (conj args ["spec/cljs/{{sanitized}}/core_test.cljs" (render "spec/cljs/reagent/core_spec.cljs" data)]
                     ["spec/vendor/console-polyfill.js" (render "vendor/console-polyfill.js" data)]
                     ["spec/vendor/es5-sham.js" (render "vendor/es5-sham.js" data)]
                     ["spec/vendor/es5-shim.js" (render "vendor/es5-shim.js" data)]
                     ["runners/speclj" (render "runners/speclj" data)])
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
               args)
        args (if (devcards? opts)
               (conj args ["env/dev/cljs/{{sanitized}}/cards.cljs" (render "env/dev/cljs/reagent/cards.cljs" data)])
               args)
        args (if (figwheel? opts)
               args
               (conj args
                     ["shadow-cljs.edn" (render "shadow-cljs.edn" data)]
                     ["package.json" (render "package.json" data)]))]
    args))

(defn reagent [name & opts]
  (main/info "Generating fresh 'lein new' Reagent project.")
  (if-let [error (validate-opts opts)]
    (println error)
    (apply ->files (format-files-args name opts))))

(ns leiningen.new.reagent
  (:require [leiningen.new.templates
             :refer [renderer name-to-path ->files
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

(def valid-opts ["+test" "+spec" "+less" "+devcards" "+cider"])

(defn valid-opts? [opts]
  (every? #(some #{%} valid-opts) opts))

(defn less? [opts]
  (some #{"+less"} opts))

(def less-plugin "lein-less \"1.7.5\"")

(def less-source-paths "\"resources/public/less\"")

(defn test? [opts]
  (some #{"+test"} opts))

(def test-source-paths "\"src/cljs\" \"test/cljs\"")

(defn spec? [opts]
  (some #{"+spec"} opts))

(defn devcards? [opts]
  (some #{"+devcards"} opts))

(defn cider? [opts]
  (some #{"+cider"} opts))

(defn project-plugins [opts]
  (cond-> []
    (less? opts) (conj less-plugin)))

(defn template-data [name opts]
  {:full-name name
   :name (project-name name)
   :project-goog-module (sanitize (sanitize-ns name))
   :project-ns (sanitize-ns name)
   :sanitized (name-to-path name)
   :project-dev-plugins (dep-list 29 (project-plugins opts))

   ;; test
   :test-hook? (fn [block] (if (test? opts) (str block "") ""))

   ;; spec
   :spec-hook? (fn [block] (if (spec? opts) (str block "") ""))

   ;; less
   :less-hook? (fn [block] (if (less? opts) (str block "") ""))

   ;; devcards
   :devcards-hook? (fn [block] (if (devcards? opts) (str block "") ""))

   ;; cider
   :cider-hook? (fn [block] (if (cider? opts) (str block "") ""))})

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
              ["src/cljs/{{sanitized}}/core.cljs" (render "src/cljs/reagent/core.cljs" data)]
              ["src/cljc/{{sanitized}}/util.cljc" (render "src/cljc/reagent/util.cljc" data)]
              ["env/dev/cljs/{{sanitized}}/dev.cljs" (render "env/dev/cljs/reagent/dev.cljs" data)]
              ["env/prod/cljs/{{sanitized}}/prod.cljs" (render "env/prod/cljs/reagent/prod.cljs" data)]
              ["LICENSE" (render "LICENSE" data)]
              ["README.md" (render "README.md" data)]
              [".gitignore" (render "gitignore" data)]
             ;; Heroku support
             ["system.properties" (render "system.properties" data)]
              ["Procfile" (render "Procfile" data)]]
        args (if (test? opts)
               (conj args ["test/cljs/{{sanitized}}/core_test.cljs" (render "test/cljs/reagent/core_test.cljs" data)]
                     ["test/vendor/console-polyfill.js" (render "vendor/console-polyfill.js" data)]
                     ["test/vendor/es5-sham.js" (render "vendor/es5-sham.js" data)]
                     ["test/vendor/es5-shim.js" (render "vendor/es5-shim.js" data)])
               args)
        args (if (spec? opts)
               (conj args ["spec/cljs/{{sanitized}}/core_test.cljs" (render "spec/cljs/reagent/core_spec.cljs" data)]
                     ["spec/vendor/console-polyfill.js" (render "vendor/console-polyfill.js" data)]
                     ["spec/vendor/es5-sham.js" (render "vendor/es5-sham.js" data)]
                     ["spec/vendor/es5-shim.js" (render "vendor/es5-shim.js" data)]
                     ["runners/speclj" (render "runners/speclj" data)])
               args)
        args (if (less? opts)
               (conj args ["src/less/site.less" (render "src/less/site.less" data)])
               args)
        args (if (devcards? opts)
               (conj args ["env/dev/cljs/{{sanitized}}/cards.cljs" (render "env/dev/cljs/reagent/cards.cljs" data)])
               args)]
    args))

(defn reagent [name & opts]
  (main/info "Generating fresh 'lein new' Reagent project.")
  (if-not (valid-opts? opts)
    (println "invalid options supplied:" (clojure.string/join " " opts)
             "\nvalid options are:" (join " " valid-opts))
    (apply ->files (format-files-args name opts))))

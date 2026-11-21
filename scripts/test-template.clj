#!/usr/bin/env bb
;; Installs the template locally, generates a project from it with the given
;; template options and checks that the project builds and runs:
;;
;;   scripts/test-template.clj            # default options
;;   scripts/test-template.clj +test +sass
;;
;; The project is generated under target/generated/. Needs babashka, lein,
;; JDK 21+ and Node.js; +test also needs Chrome for Karma.
(ns test-template
  (:require [babashka.fs :as fs]
            [babashka.http-client :as http]
            [babashka.process :as p]
            [clojure.string :as str]))

(def root (-> *file* fs/absolutize fs/parent fs/parent str))
(def app-name "myapp")
;; Port for running the uberjar; the shadow-cljs dev server uses 3000 (shadow-cljs.edn).
(def port 3100)

(defn template-version []
  (second (re-find #"\"([^\"]+)\"" (first (fs/read-all-lines (fs/path root "project.clj"))))))

(defn project-dir [opts]
  (str (fs/path root "target" "generated"
                (str app-name
                     (when (seq opts) "-")
                     (str/replace (str/join " " opts) #"[+ ]" "")))))

(defmacro group
  "Runs body inside a GitHub Actions log group."
  [title & body]
  `(do (println (str "::group::" ~title))
       (flush)
       (let [result# (do ~@body)]
         (println "::endgroup::")
         result#)))

(defn run [dir & args]
  (apply p/shell {:dir dir} args))

(defn listening? [base]
  (try
    (http/get (str base "/") {:throw false :timeout 10000})
    true
    (catch java.net.ConnectException _ false)
    ;; Anything else (e.g. a timeout) means something is listening; the
    ;; path checks below report the actual problem.
    (catch Exception _ true)))

(defn check-urls
  "Waits until the server answers, then checks that each path returns 200."
  [base proc paths]
  (loop [n 120]
    (cond
      (listening? base) nil
      (not (p/alive? proc)) (throw (ex-info "Server exited before it started listening" {}))
      (zero? n) (throw (ex-info (str "Server did not start listening on " base " within 120 s") {}))
      :else (do (Thread/sleep 1000) (recur (dec n)))))
  (doseq [path paths
          :let [url (str base path)
                status (try (:status (http/get url {:throw false :timeout 30000}))
                            (catch Exception e (.getMessage e)))]]
    (println "GET" url "->" status)
    (when (not= 200 status)
      (throw (ex-info (str "Unexpected response from " url) {})))))

(defn with-server
  "Starts a server process, checks the paths and stops it (and the child
  processes npx/lein start) whatever happens."
  [{:keys [dir env cmd base paths]}]
  (let [proc (apply p/process {:dir dir :inherit true :extra-env env} cmd)]
    (try
      (check-urls base proc paths)
      (finally
        (p/destroy-tree proc)
        @proc))))

(defn -main [& opts]
  (let [opts (vec opts)
        opt? (set opts)
        version (template-version)
        dir (project-dir opts)
        cmd-str (str/join " " (into ["lein new reagent" app-name] opts))]

    (group (str "Install template " version)
      (run root "lein" "install"))

    (group (str "Generate project: " cmd-str)
      (fs/delete-tree dir)
      (fs/create-dirs (fs/parent dir))
      (apply run root "lein" "new" "reagent" app-name "--to-dir" dir
             "--template-version" version "--" opts)
      (when-not (fs/exists? (fs/path dir "project.clj"))
        (throw (ex-info "lein new did not generate a project" {}))))

    (group "npm install"
      (run dir "npm" "install" "--no-audit" "--no-fund"))

    (when (opt? "+less")
      (group "Compile less"
        (run dir "lein" "less4clj" "once")))

    (when (opt? "+sass")
      (group "Compile sass"
        (run dir "lein" "sass4clj" "once")))

    (group "shadow-cljs release (warnings as errors)"
      (run dir "npx" "shadow-cljs" "release" "app"
           "--config-merge" "{:compiler-options {:warnings-as-errors true}}"))

    (when (opt? "+test")
      (group "npm test"
        (run dir "npm" "test")))

    (group "Uberjar"
      (run dir "lein" "do" "clean," "uberjar"))

    (group "Run uberjar"
      (with-server {:dir dir
                    :env {"PORT" (str port)}
                    :cmd ["java" "-jar" (str "target/" app-name ".jar")]
                    :base (str "http://localhost:" port)
                    :paths ["/" "/about" "/items/1" "/js/app.js" "/css/site.min.css"]}))

    (group "shadow-cljs dev build and server"
      (run dir "npx" "shadow-cljs" "compile" "app")
      (with-server {:dir dir
                    :cmd ["npx" "shadow-cljs" "server"]
                    :base "http://localhost:3000"
                    :paths ["/" "/about" "/items/1" "/js/app.js" "/css/site.css"]}))

    (println "OK:" cmd-str)))

(when (= *file* (System/getProperty "babashka.file"))
  (try
    (apply -main *command-line-args*)
    (shutdown-agents)
    (catch clojure.lang.ExceptionInfo e
      (binding [*out* *err*]
        (println (ex-message e)))
      (System/exit (or (:exit (ex-data e)) 1)))))

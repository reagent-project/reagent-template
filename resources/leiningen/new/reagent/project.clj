(defproject {{full-name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring-server "0.4.0"]
                 [reagent "0.6.0-rc"]
                 [reagent-forms "0.5.24"]
                 [reagent-utils "0.1.8"]
                 [ring "1.5.0"]
                 [ring/ring-defaults "0.2.1"]
                 [compojure "1.5.0"]
                 [hiccup "1.0.5"]
                 [yogthos/config "0.8"]
                 [org.clojure/clojurescript "1.9.36"
                  :scope "provided"]
                 [secretary "1.2.3"]
                 [venantius/accountant "0.1.7"
                  :exclusions [org.clojure/tools.reader]]]

  :plugins [[lein-environ "1.0.2"]
            [lein-cljsbuild "1.1.1"]
            [lein-asset-minifier "0.2.7"
             :exclusions [org.clojure/clojure]]]

  :ring {:handler {{project-ns}}.handler/app
         :uberwar-name "{{name}}.war"}

  :min-lein-version "2.5.0"

  :uberjar-name "{{name}}.jar"

  :main {{project-ns}}.server

  :clean-targets ^{:protect false}
  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]

  :source-paths ["src/clj" "src/cljc"]
  {{#spec-hook?}}
  :test-paths ["spec/clj"]
  {{/spec-hook?}}
  :resource-paths ["resources" "target/cljsbuild"]

  :minify-assets
  {:assets
   {"resources/public/css/site.min.css" "resources/public/css/site.css"}}

  :cljsbuild
  {:builds {:min
            {:source-paths ["src/cljs" "src/cljc" "env/prod/cljs"]
             :compiler
             {:output-to "target/cljsbuild/public/js/app.js"
              :output-dir "target/uberjar"
              :optimizations :advanced
              :pretty-print  false}}
            :app
            {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
             :compiler
             {:main "{{name}}.dev"
              :asset-path "/js/out"
              :output-to "target/cljsbuild/public/js/app.js"
              :output-dir "target/cljsbuild/public/js/out"
              :source-map true
              :optimizations :none
              :pretty-print  true}}
            {{#test-hook?}}
            :test
            {:source-paths ["src/cljs" "src/cljc" "test/cljs"]
             :compiler {:main {{project-ns}}.doo-runner
                        :asset-path "/js/out"
                        :output-to "target/test.js"
                        :output-dir "target/cljstest/public/js/out"
                        :optimizations :whitespace
                        :pretty-print true}}{{/test-hook?}}
            {{#spec-hook?}}
            :test
            {:source-paths ["src/cljs" "src/cljc" "spec/cljs"]
             :compiler {:output-to "target/test.js"
                        :optimizations :whitespace
                        :pretty-print true}}{{/spec-hook?}}
            {{#devcards-hook?}}
            :devcards
            {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
             :figwheel {:devcards true}
             :compiler {:main "{{name}}.cards"
                        :asset-path "js/devcards_out"
                        :output-to "target/cljsbuild/public/js/app_devcards.js"
                        :output-dir "target/cljsbuild/public/js/devcards_out"
                        :source-map-timestamp true
                        :optimizations :none
                        :pretty-print true}}{{/devcards-hook?}}
            }
   {{#spec-hook?}}
   :test-commands {"unit" ["phantomjs" "runners/speclj" "target/test.js"]}
   {{/spec-hook?}}
   }

  {{#spec-hook?}}
   :doo {:build "test"}
   {{/spec-hook?}}

  :figwheel
  {:http-server-root "public"
   :server-port 3449
   :nrepl-port 7002
   :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"
                      {{#cider-hook?}}
                      "cider.nrepl/cider-middleware"
                      "refactor-nrepl.middleware/wrap-refactor"
                      {{/cider-hook?}}
                      ]
   :css-dirs ["resources/public/css"]
   :ring-handler {{project-ns}}.handler/app}

  {{#less-hook?}}
  :less {:source-paths ["src/less"]
         :target-path "resources/public/css"}
  {{/less-hook?}}

  {{#sass-hook?}}
  :sass {:src "src/sass"
         :dst "resources/public/css"}
  {{/sass-hook?}}

  :profiles {:dev {:repl-options {:init-ns {{project-ns}}.repl}

                   :dependencies [[ring/ring-mock "0.3.0"]
                                  [ring/ring-devel "1.5.0"]
                                  [prone "1.1.1"]
                                  [lein-figwheel "0.5.4"
                                   :exclusions [org.clojure/core.memoize
                                                ring/ring-core
                                                org.clojure/clojure
                                                org.ow2.asm/asm-all
                                                org.clojure/data.priority-map
                                                org.clojure/tools.reader
                                                org.clojure/clojurescript
                                                org.clojure/core.async
                                                org.clojure/tools.analyzer.jvm]]
                                  [org.clojure/tools.nrepl "0.2.12"]
                                  [com.cemerick/piggieback "0.2.2-SNAPSHOT"]
                                  {{#test-hook?}}
                                  [lein-doo "0.1.6"]
                                  {{/test-hook?}}
                                  {{#spec-hook?}}
                                  [speclj "3.3.1"]
                                  {{/spec-hook?}}
                                  {{#devcards-hook?}}
                                  [devcards "0.2.1-7"
                                   :exclusions [org.clojure/tools.reader]]
                                  {{/devcards-hook?}}
                                  [pjstadig/humane-test-output "0.8.0"]
                                  {{dev-dependencies}}]

                   :source-paths ["env/dev/clj"]
                   :plugins [[lein-figwheel "0.5.4"
                              :exclusions [org.clojure/core.memoize
                                           ring/ring-core
                                           org.clojure/clojure
                                           org.ow2.asm/asm-all
                                           org.clojure/data.priority-map
                                           org.clojure/tools.reader
                                           org.clojure/clojurescript
                                           org.clojure/core.async
                                           org.clojure/tools.analyzer.jvm]]
                             {{#test-hook?}}
                             [lein-doo "0.1.6"]
                             {{/test-hook?}}
                             {{#spec-hook?}}
                             [speclj "3.3.1"]
                             {{/spec-hook?}}
                             {{#cider-hook?}}
                             [cider/cider-nrepl "0.10.0-SNAPSHOT"]
                             [org.clojure/tools.namespace "0.3.0-alpha2"
                              :exclusions [org.clojure/tools.reader]]
                             [refactor-nrepl "2.0.0-SNAPSHOT"
                              :exclusions [org.clojure/clojure]]
                             {{/cider-hook?}}
                             {{{project-dev-plugins}}}]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :env {:dev true}}

             :uberjar {:hooks [minify-assets.plugin/hooks]
                       :source-paths ["env/prod/clj"]
                       :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
                       :env {:production true}
                       :aot :all
                       :omit-source true}})

(defproject {{full-name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.12.4"]
                 [ring-server "0.5.0"]
                 [reagent "2.0.1"]
                 [reagent-utils "0.3.8"]
                 [cljsjs/react "18.3.1-1"]
                 [cljsjs/react-dom "18.3.1-1"]
                 [ring "1.15.3"]
                 [ring/ring-defaults "0.7.0"]
                 [hiccup "2.0.0"]
                 [yogthos/config "1.2.1"]
                 [org.clojure/clojurescript "1.12.134" :scope "provided"]
                 [metosin/reitit "0.10.0"]
                 {{#clerk-hook?}}
                 [pez/clerk "1.0.0"]
                 {{/clerk-hook?}}
                 [venantius/accountant "0.2.5" :exclusions [org.clojure/tools.reader]]]

  :jvm-opts ["-Xmx1G"]

  :plugins [[lein-environ "1.1.0"]
            [lein-cljsbuild "1.1.7"]
            [lein-asset-minifier "0.4.6"
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

  :source-paths ["src/clj" "src/cljc" "src/cljs"]
  {{#spec-hook?}}
  :test-paths ["spec/clj"]
  {{/spec-hook?}}
  :resource-paths ["resources" "target/cljsbuild"]

  :minify-assets
  [[:css {:source "resources/public/css/site.css"
          :target "resources/public/css/site.min.css"}]]

  :cljsbuild
  {:builds {:min
            {:source-paths ["src/cljs" "src/cljc" "env/prod/cljs"]
             :compiler
             {:output-to        "target/cljsbuild/public/js/app.js"
              :output-dir       "target/cljsbuild/public/js"
              :source-map       "target/cljsbuild/public/js/app.js.map"
              :optimizations :advanced
              :infer-externs true
              :pretty-print  false}}
            :app
            {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
             :figwheel {:on-jsload "{{project-ns}}.core/mount-root"}
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
{{#test-hook?}}
   :doo {:build "test"
         :alias {:default [:chrome]}}
{{/test-hook?}}
{{#spec-hook?}}
   :doo {:build "test"}
   {{/spec-hook?}}

  :figwheel
  {:http-server-root "public"
   :server-port 3449
   :nrepl-port 7002
   :nrepl-middleware [cider.piggieback/wrap-cljs-repl
                      {{#cider-hook?}}
                      cider.nrepl/cider-middleware
                      refactor-nrepl.middleware/wrap-refactor
                      {{/cider-hook?}}
                      ]
   :css-dirs ["resources/public/css"]
   :ring-handler {{project-ns}}.handler/app}

  {{#less-hook?}}
  :less {:source-paths ["src/less"]
         :target-path "resources/public/css"}
  {{/less-hook?}}

  {{#sass-hook?}}
  :sass {:source-paths ["src/sass"]
         :target-path "resources/public/css"}
  {{/sass-hook?}}

  :profiles {:dev {:repl-options {:init-ns {{project-ns}}.repl}
                   :dependencies [[cider/piggieback "0.6.1"]
                                  [binaryage/devtools "1.0.7"]
                                  [ring/ring-mock "0.6.2"]
                                  [ring/ring-devel "1.15.3"]
                                  [prone "2021-04-23"]
                                  [figwheel-sidecar "0.5.20"]
                                  [nrepl "1.5.2"]
                                  {{#shadow-cljs-hook?}}
                                  [thheller/shadow-cljs "3.3.5"]
                                  {{/shadow-cljs-hook?}}
                                  {{#spec-hook?}}
                                  [speclj "3.3.2"]
                                  {{/spec-hook?}}
                                  {{#devcards-hook?}}
                                  [devcards "0.2.6" :exclusions [cljsjs/react]]
                                  {{/devcards-hook?}}
                                  [pjstadig/humane-test-output "0.11.0"]
                                  {{dev-dependencies}}
                                  {{#less-hook?}}
                                  ;; To silence warnings from less4clj dependecies about missing logger implementation
                                  [org.slf4j/slf4j-nop "1.7.25"]
                                  {{/less-hook?}}
                                  {{#sass-hook?}}
                                  ;; To silence warnings from sass4clj dependecies about missing logger implementation
                                  [org.slf4j/slf4j-nop "1.7.25"]
                                  {{/sass-hook?}} ]

                   :source-paths ["env/dev/clj"]
                   :plugins [[lein-figwheel "0.5.20"]
                             {{#test-hook?}}
                             [lein-doo "0.1.10"]
                             {{/test-hook?}}
                             {{#spec-hook?}}
                             [speclj "3.3.2"]
                             {{/spec-hook?}}
                             {{#cider-hook?}}
                             [cider/cider-nrepl "0.21.1"]
                             [org.clojure/tools.namespace "0.3.0-alpha4"
                              :exclusions [org.clojure/tools.reader]]
                             [refactor-nrepl "2.4.0"
                              :exclusions [org.clojure/clojure]]
                             {{/cider-hook?}}
                             {{#less-hook?}}
                             [deraen/lein-less4j "0.6.2"]
                             {{/less-hook?}}
                             {{#sass-hook?}}
                             [deraen/lein-sass4clj "0.3.1"]
                             {{/sass-hook?}}]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :env {:dev true}}

             :uberjar {:hooks [minify-assets.plugin/hooks]
                       :source-paths ["env/prod/clj"]
                       :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
                       :env {:production true}
                       :aot :all
                       :omit-source true}})

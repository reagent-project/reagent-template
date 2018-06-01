(defproject {{full-name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [ring-server "0.5.0"]
                 [reagent "0.8.1"]
                 [reagent-utils "0.3.1"]
                 [ring "1.6.3"]
                 [ring/ring-defaults "0.3.1"]
                 [compojure "1.6.1"]
                 [hiccup "1.0.5"]
                 [yogthos/config "1.1.1"]
                 [org.clojure/clojurescript "1.10.238"
                  :scope "provided"]
                 [secretary "1.2.3"]
                 [venantius/accountant "0.2.4"
                  :exclusions [org.clojure/tools.reader]]]

  :plugins [[lein-environ "1.1.0"]
            [lein-cljsbuild "1.1.7"]
            [lein-asset-minifier "0.2.7"
             :exclusions [org.clojure/clojure]]]

  :ring {:handler {{project-ns}}.handler/app
         :uberwar-name "{{name}}.war"}

  :min-lein-version "2.5.0"
  :uberjar-name "{{name}}.jar"
  :main {{project-ns}}.server
  {{#jvm-opts-hook?}}
  :jvm-opts ["--add-modules" "java.xml.bind"]
  {{/jvm-opts-hook?}}
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
             {:output-to        "target/cljsbuild/public/js/app.js"
              :output-dir       "target/cljsbuild/public/js"
              :source-map       "target/cljsbuild/public/js/app.js.map"
              :optimizations :advanced
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
  :sass {:source-paths ["src/sass"]
         :target-path "resources/public/css"}
  {{/sass-hook?}}

  :profiles {:dev {:repl-options {:init-ns {{project-ns}}.repl
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                   :dependencies [[binaryage/devtools "0.9.10"]
                                  [ring/ring-mock "0.3.2"]
                                  [ring/ring-devel "1.6.3"]
                                  [prone "1.5.2"]
                                  [figwheel-sidecar "0.5.16"]
                                  [org.clojure/tools.nrepl "0.2.13"]
                                  [com.cemerick/piggieback "0.2.2"]
                                  {{#spec-hook?}}
                                  [speclj "3.3.2"]
                                  {{/spec-hook?}}
                                  {{#devcards-hook?}}
                                  [devcards "0.2.3" :exclusions [cljsjs/react]]
                                  {{/devcards-hook?}}
                                  [pjstadig/humane-test-output "0.8.3"]
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
                   :plugins [[lein-figwheel "0.5.16"]
                             {{#test-hook?}}
                             [lein-doo "0.1.10"]
                             {{/test-hook?}}
                             {{#spec-hook?}}
                             [speclj "3.3.2"]
                             {{/spec-hook?}}
                             {{#cider-hook?}}
                             [cider/cider-nrepl "0.15.1"]
                             [org.clojure/tools.namespace "0.3.0-alpha4"
                              :exclusions [org.clojure/tools.reader]]
                             [refactor-nrepl "2.3.1"
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

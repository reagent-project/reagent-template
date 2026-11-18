(defproject {{full-name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.12.6"]
                 [ring-server "0.5.0"]
                 [reagent "2.0.1"]
                 [reagent-utils "0.3.8"]
                 [ring "1.15.6"]
                 [ring/ring-defaults "0.7.1"]
                 [hiccup "2.0.0"]
                 [yogthos/config "1.2.1"]
                 [org.clojure/clojurescript "1.12.145" :scope "provided"
                  ;; shadow-cljs brings the Closure Compiler version it needs
                  :exclusions [com.google.javascript/closure-compiler]]
                 [metosin/reitit "0.11.0"]
                 {{#clerk-hook?}}
                 [pez/clerk "1.0.0" :exclusions [org.clojure/core.async]]
                 {{/clerk-hook?}}
                 [venantius/accountant "0.2.5" :exclusions [org.clojure/tools.reader]]]

  :jvm-opts ["-Xmx1G"]

  :plugins [[lein-shell "0.5.0"]
            [lein-asset-minifier "0.5.2"
             :exclusions [org.clojure/clojure]]]

  :ring {:handler {{project-ns}}.handler/app
         :uberwar-name "{{name}}.war"}

  :min-lein-version "2.5.0"
  :uberjar-name "{{name}}.jar"
  :main {{project-ns}}.server
  :clean-targets ^{:protect false}
  [:target-path
   "resources/public/js"]

  :source-paths ["src/clj" "src/cljc" "src/cljs"]
  {{#test-hook?}}
  :test-paths ["test/cljs"]
  {{/test-hook?}}
  :resource-paths ["resources"]

  :minify-assets
  [[:css {:source "resources/public/css/site.css"
          :target "resources/public/css/site.min.css"}]]
{{#less-hook?}}

  :less4clj {:source-paths ["src/less"]
             :target-path "resources/public/css"}
{{/less-hook?}}
{{#sass-hook?}}

  :sass {:source-paths ["src/sass"]
         :target-path "resources/public/css"}
{{/sass-hook?}}

  :profiles {:dev {:repl-options {:init-ns {{project-ns}}.repl}
                   :dependencies [[thheller/shadow-cljs "3.5.14"]
                                  [binaryage/devtools "1.0.7"]
                                  [ring/ring-mock "0.6.2"]
                                  [ring/ring-devel "1.15.6"]
                                  [prone "2021-04-23"]
                                  [nrepl "1.8.0"]
                                  {{#cider-hook?}}
                                  ;; shadow-cljs adds the CIDER middleware to its nREPL server when these are on the classpath
                                  [cider/cider-nrepl "0.64.1"]
                                  [refactor-nrepl "3.16.0"]
                                  {{/cider-hook?}}
                                  [pjstadig/humane-test-output "0.11.0"]{{#less-or-sass-hook?}}
                                  ;; To silence warnings from less4clj/sass4clj dependencies about missing logger implementation
                                  [org.slf4j/slf4j-nop "2.0.20"]{{/less-or-sass-hook?}}]

                   :source-paths ["dev"]
{{#less-or-sass-hook?}}
                   :plugins [{{#less-hook?}}[deraen/lein-less4clj "0.8.0"]{{/less-hook?}}{{#sass-hook?}}
                             [deraen/lein-sass4clj "0.7.0"]{{/sass-hook?}}]
{{/less-or-sass-hook?}}

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]}

             :uberjar {:hooks [minify-assets.plugin/hooks]
                       :prep-tasks ["compile"
                                    ["shell" "npx" "shadow-cljs" "release" "app"]]
                       :aot :all
                       :omit-source true}})

# {{name}}

This is the {{name}} project.

## Requirements

* Java 21 or newer (required by shadow-cljs)
* [Node.js](https://nodejs.org/) and npm
* [Leiningen](https://leiningen.org/) 2.x

## Project structure

The app has two parts: a ClojureScript frontend, which runs in the browser and renders the
UI with Reagent, and a small Clojure backend, which runs on the JVM and serves the HTML page and
static files. The source folders are split by which side the code runs on:

| Path | Contents |
|------|----------|
| `src/clj/{{sanitized}}/` | Clojure code that runs on the server (JVM) |
| `src/clj/{{sanitized}}/handler.clj` | Ring handler: server routes, middleware and the HTML page that loads the app |
| `src/clj/{{sanitized}}/server.clj` | `-main` entry point that starts Jetty when running the uberjar |
| `src/cljs/{{sanitized}}/` | ClojureScript code that runs in the browser |
| `src/cljs/{{sanitized}}/core.cljs` | Reagent components, client-side routes and the `init!` entry point |
| `src/cljc/{{sanitized}}/` | Shared code, compiled for both the server and the browser |
| `dev/` | Code and config only on the classpath in development (the `:dev` profile) |
| `dev/config.edn` | Sets `:dev true`, read through `config.core/env` |
| `dev/{{sanitized}}/repl.clj` | REPL helpers `start-server` and `stop-server` |
| `resources/public/` | Static files served as-is, such as `css/site.css` |
| `resources/public/js/` | JavaScript compiled by shadow-cljs (generated, not committed) |
{{#test-hook?}}| `test/cljs/{{sanitized}}/` | ClojureScript tests |
| `karma.conf.js` | Karma config for running the tests in headless Chrome |
{{/test-hook?}}{{#less-hook?}}| `src/less/` | Less sources, compiled to `resources/public/css` |
{{/less-hook?}}{{#sass-hook?}}| `src/sass/` | Sass sources, compiled to `resources/public/css` |
{{/sass-hook?}}| `project.clj` | Leiningen config: Clojure dependencies, profiles and the uberjar build |
| `shadow-cljs.edn` | ClojureScript builds and the development web server |
| `package.json` | npm dependencies (React, shadow-cljs) and scripts |

Namespaces map to file paths, with dashes in names becoming underscores in paths: the
namespace `{{project-ns}}.core` lives in `src/cljs/{{sanitized}}/core.cljs`.

Code in `.cljc` files can be used from both `.clj` and `.cljs` namespaces, which makes it the place
for things such as validation or formatting that both sides need. Use
[reader conditionals](https://clojure.org/guides/reader_conditionals) (`#?(:clj ... :cljs ...)`)
for the parts that differ between the platforms.

`(env :dev)` from [config](https://github.com/yogthos/config) is true in development because
`dev/config.edn` is then on the classpath. The handler uses it to add [Prone](https://github.com/magnars/prone)
error pages and code reloading. Those libraries are dev-only dependencies, so the handler loads
them with `requiring-resolve` only in development.

## Development mode

To start the [shadow-cljs](https://github.com/thheller/shadow-cljs) compiler, navigate to the
project folder and run:

```
npm install
npx shadow-cljs watch app
```

The application will be available at [http://localhost:3000](http://localhost:3000)
once the first build completes. shadow-cljs serves the Ring handler from
`src/clj/{{sanitized}}/handler.clj` and automatically pushes ClojureScript and CSS changes to the browser.

shadow-cljs also starts an nREPL server; its port is written to `.shadow-cljs/nrepl.port`.
After connecting, run `(shadow/repl :app)` to switch to a ClojureScript REPL running in the browser.
The shadow-cljs dashboard is available at [http://localhost:9630](http://localhost:9630).
{{#cider-hook?}}

### Emacs and CIDER

The `:dev` profile includes [cider-nrepl](https://github.com/clojure-emacs/cider-nrepl) and
[refactor-nrepl](https://github.com/clojure-emacs/refactor-nrepl). shadow-cljs adds their middleware
to its nREPL server automatically when they are on the classpath, so no extra configuration is needed.

Start shadow-cljs in a terminal with `npx shadow-cljs watch app`, then in Emacs run
`M-x cider-connect-cljs`, choose `localhost` and the port from `.shadow-cljs/nrepl.port`,
select `shadow` as the REPL type and `:app` as the build. Open the app in the browser
before evaluating ClojureScript, since the REPL runs in the page.

Alternatively `M-x cider-jack-in-cljs` (choosing `shadow-cljs`) starts the shadow-cljs server
from Emacs. See the [CIDER shadow-cljs documentation](https://docs.cider.mx/cider/cljs/shadow-cljs.html).
{{/cider-hook?}}
{{#less-or-sass-hook?}}

### Style compilation
{{/less-or-sass-hook?}}
{{#less-hook?}}

To compile [less](https://github.com/Deraen/less4clj) sources and then watch for changes and recompile until interrupted, run
```
lein less4clj auto
```
{{/less-hook?}}
{{#sass-hook?}}

To compile [sass](https://github.com/Deraen/sass4clj) sources and then watch for changes and recompile until interrupted, run
```
lein sass4clj auto
```
{{/sass-hook?}}

### Running the Ring server from a Clojure REPL

```
lein repl
```

The Jetty server can be started by running:

```clojure
(start-server)
```
and stopped by running:
```clojure
(stop-server)
```

This serves the JavaScript built by shadow-cljs, so keep `npx shadow-cljs watch app` running
(or stop it first if you want to use port 3000).
{{#test-hook?}}

## Running the tests

Tests are in `test/cljs`. Namespaces ending in `-test` are picked up automatically.

To run the tests in the browser while developing:

```
npx shadow-cljs watch test
```

and open [http://localhost:8021](http://localhost:8021). The tests re-run on every change.

To run the tests once in headless Chrome with [Karma](https://karma-runner.github.io/), e.g. on CI:

```
npm test
```

This compiles the `:ci` build and runs `karma start --single-run`. Karma needs Chrome or
Chromium installed; set `CHROME_BIN` if it isn't found automatically.
{{/test-hook?}}

## Building for release

```
lein do clean, uberjar
```

This runs `npx shadow-cljs release app` to build the optimized JavaScript, so run
`npm install` first. To build only the JavaScript, run `npx shadow-cljs release app` directly.

The resulting jar can be run with `java -jar target/{{name}}.jar`.

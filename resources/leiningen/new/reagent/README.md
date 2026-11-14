# {{name}}

This is the {{name}} project.

## Requirements

* Java 21 or newer (required by shadow-cljs)
* [Node.js](https://nodejs.org/) and npm
* [Leiningen](https://leiningen.org/) 2.x

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

## Deploying to Heroku

Make sure you have [Git](http://git-scm.com/downloads) and the [Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli) installed, then simply follow the steps below.
The Heroku build needs both Node.js and Clojure, so add the buildpacks in this order:

```
heroku buildpacks:add heroku/nodejs
heroku buildpacks:add heroku/clojure
```

Optionally, test that your application runs locally by running.

```
lein do clean, uberjar
heroku local
```

Now, you can initialize your git repo and commit your application.

```
git init
git add .
git commit -m "init"
```
create your app on Heroku

```
heroku create
```

optionally, create a database for the application

```
heroku addons:add heroku-postgresql
```

The connection settings can be found at your [Heroku dashboard](https://dashboard.heroku.com/apps/) under the add-ons for the app.

deploy the application

```
git push heroku master
```

Your application should now be deployed to Heroku!
For further instructions see the [official documentation](https://devcenter.heroku.com/articles/clojure).

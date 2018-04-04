# {{name}}

This is the {{name}} project.

## Development mode

To start the Figwheel compiler, navigate to the project folder and run the following command in the terminal:

```
lein figwheel
```
{{#devcards?}}

To start the [DevCards](https://github.com/bhauman/devcards) build, run

```
lein figwheel devcards
```
{{/devcards?}}

Figwheel will automatically push cljs changes to the browser. The server will be available at [http://localhost:3449](http://localhost:3449) once Figwheel starts up. {{#devcards-hook?}} To view your devcards, type `(switch-to-build devcards)` at the Figwheel REPL and navigate to [http://localhost:3449/cards](http://localhost:3449/cards). {{/devcards-hook?}}

Figwheel also starts `nREPL` using the value of the `:nrepl-port` in the `:figwheel`
config found in `project.clj`. By default the port is set to `7002`.

The figwheel server can have unexpected behaviors in some situations such as when using
websockets. In this case it's recommended to run a standalone instance of a web server as follows:

```
lein do clean, run
```

The application will now be available at [http://localhost:3000](http://localhost:3000).

{{#less-or-sass-hook?}}
### Style compilation
{{/less-or-sass-hook?}}
{{#less-hook?}}
To compile [less](https://github.com/Deraen/less4clj) sources and then watch for changes and recompile until interrupted, run
```
lein less4j auto
```
{{/less-hook?}}
{{#sass-hook?}}
To compile [sass](https://github.com/Deraen/sass4clj) sources and then watch for changes and recompile until interrupted, run
```
lein sass4clj auto
```
{{/sass-hook?}}

### Optional development tools

Start the browser REPL:

```
$ lein repl
```
The Jetty server can be started by running:

```clojure
(start-server)
```
and stopped by running:
```clojure
(stop-server)
```

{{#test-or-spec-hook?}}
## Running the tests
{{/test-or-spec-hook?}}
{{#test-hook?}}
To run [cljs.test](https://github.com/clojure/clojurescript/blob/master/src/main/cljs/cljs/test.cljs) tests, please use

```
lein doo phantom test once
```
{{/test-hook?}}
{{#spec-hook?}}

To run [speclj](https://github.com/slagyr/speclj) tests, please use

```
lein cljsbuild test
```
{{/spec-hook?}}
{{#test-or-spec-hook?}}

For installation instructions of PhantomJS, please see [this](http://phantomjs.org/download.html).
{{/test-or-spec-hook?}}

## Building for release

```
lein do clean, uberjar
```

## Deploying to Heroku

Make sure you have [Git](http://git-scm.com/downloads) and [Heroku toolbelt](https://toolbelt.heroku.com/) installed, then simply follow the steps below.

Optionally, test that your application runs locally with foreman by running.

```
foreman start
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

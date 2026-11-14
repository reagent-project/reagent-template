# reagent-template

A [Leiningen](http://leiningen.org/) template for projects using Reagent.

![reagent-template](logo-rounded.jpg)

A Leiningen template for a Clojure/ClojureScript app based on Reagent, with a focus on providing
a batteries included setup for development and deployment.

#### Requires JDK 21+ (shadow-cljs) and Node.js

### Getting Help

For any questions or discussion please come join us in the #reagent channel on the [Clojurians Slack](http://clojurians.net/).

## Usage

Create a new application project:

```
lein new reagent <name>
```

To add [cljs.test](https://clojurescript.org/tools/testing) support use the `+test` flag:

```
lein new reagent <name> +test
```
Tests run in the browser with the shadow-cljs [`:browser-test`](https://shadow-cljs.github.io/docs/UsersGuide.html#target-browser-test)
target (`npx shadow-cljs watch test`, then open [http://localhost:8021](http://localhost:8021)), or once in headless Chrome
with [Karma](https://karma-runner.github.io/) using `npm test`.

To add [CIDER](https://github.com/clojure-emacs/cider) support (cider-nrepl and refactor-nrepl middleware in the shadow-cljs nREPL), use the `+cider` flag:

```
lein new reagent <name> +cider
```

To add  [less](https://github.com/Deraen/less4clj)/[sass](https://github.com/Deraen/sass4clj) support
use `+less` or `+sass` flags:

```
lein new reagent <name> +less
```

The `+figwheel`, `+spec` and `+devcards` options were removed: generated projects always use shadow-cljs.

### Development mode with [shadow-cljs](https://github.com/thheller/shadow-cljs)

Projects use [shadow-cljs](https://github.com/thheller/shadow-cljs) with React 19 from npm.
To start the shadow-cljs compiler run
```
npm install
npx shadow-cljs watch app
```

The application will now be available at [http://localhost:3000](http://localhost:3000). shadow-cljs serves the
project's Ring handler and automatically pushes cljs and CSS changes to the browser. It also starts an nREPL server,
the port is written to `.shadow-cljs/nrepl.port`.

In case of using `+less` option you may also want to run
```
lein less4clj auto
```
to autocompile less files.

In case of using `+sass` option you may also want to run
```
lein sass4clj auto
```
to autocompile sass/scss files.

#### Optional development tools

Start a Clojure REPL:

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

### Building for release

```
lein do clean, uberjar
```

The uberjar build runs `npx shadow-cljs release app` (so run `npm install` first).
To only build the JavaScript, run that command directly:
```
npx shadow-cljs release app
```

### Deploying to Heroku

The project generates a `system.properties` used for Heroku deployments. Make sure you have [Git](http://git-scm.com/downloads) and the [Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli) installed, then simply follow the steps below.
The uberjar build runs shadow-cljs through `npx`, so the app needs the Node.js buildpack before the Clojure one:

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

## Contents

The template packages everything you need to create a production ready ClojureScript application following current best practices. The template uses the following features and libraries:

* [Reagent](https://github.com/reagent-project/reagent) - ClojureScript interface to Facebook's React
* [shadow-cljs](https://github.com/thheller/shadow-cljs) - ClojureScript compiler with npm support and hot reload
* [reagent-utils](https://github.com/reagent-project/reagent-utils) - utilities such as session and cookie management
* [Reitit](https://metosin.github.io/reitit/) - Routing, server and client side
* [Accountant](https://github.com/venantius/accountant) - HTML5 history management, enabling href navigation in SPAs
* [Clerk](https://github.com/PEZ/clerk) - In page navigation for SPAs. Scroll restoration, anchor targeting, and such.
* [Hiccup](https://github.com/weavejester/hiccup) - server-side HTML templating
* [Ring](https://github.com/ring-clojure/ring) - Clojure HTTP interface
* [Prone](https://github.com/magnars/prone) - better exception reporting middleware for Ring
* [Heroku](https://www.heroku.com/) - the template is setup to work on Heroku out of the box, simply run `git push heroku master`

## Options

The template supports the following options:

* `+test` - ClojureScript testing support (shadow-cljs `:browser-test` and Karma)
* `+less` - use [less](https://github.com/Deraen/less4clj) for compiling Less CSS files
* `+sass` - use [sass](https://github.com/Deraen/sass4clj) for compiling Sass/Scss CSS files
* `+cider` - add [CIDER](https://github.com/clojure-emacs/cider) support
* `-clerk` - do not add [Clerk](https://github.com/PEZ/clerk) support

## Contributing & Customizing

Take a look at the open issues, especially ones marked as `help wanted`. If you see one you'd like to address don't hesitate to start a discussion or submit a pull request.

If the template isn't doing quite what you're looking for, it's easy to create a local copy of your own following these simple steps:

```
git clone https://github.com/reagent-project/reagent-template.git
cd reagent-template
lein install
```

If you feel that your customizations are general enough to be useful for others then please consider making a pull request.

## Requirements

* JDK 21+ (required by shadow-cljs)
* Node.js and npm
* Leiningen 2.x

## License

Copyright © 2015 Dmitri Sotnikov

Distributed under the The MIT License (MIT).

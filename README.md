# reagent-template

A [Leiningen](http://leiningen.org/) template for projects using Reagent.

![reagent-template](logo-rounded.jpg)

A Leiningen template for a Clojure/ClojureScript app based on Reagent, with a focus on providing
a batteries included setup for development and deployment.

#### Requires JDK 1.7+

### Getting Help

For any questions or discussion please come join us at the [Reagent Google Group](https://groups.google.com/forum/#!forum/reagent-project).

## Usage

Create a new application project:

```
lein new reagent <name>
```

To enable [cljs.test](https://github.com/clojure/clojurescript/blob/master/src/main/cljs/cljs/test.cljs) and [doo](https://github.com/bensu/doo) with [PhantomJS](http://phantomjs.org/), use `+test` flag:

```
lein new reagent <name> +test
```
To run the tests, please use `lein doo phantom test once`. For installation instructions of PhantomJS, please see [this](http://phantomjs.org/download.html).

To enable [speclj](https://github.com/slagyr/speclj) with [PhantomJS](http://phantomjs.org/), use `+spec` flag:

```
lein new reagent <name> +spec
```
To run the tests, please use `lein cljsbuild test`. For installation instructions of PhantomJS, please see [this](http://phantomjs.org/download.html).

To enable [DevCards](https://github.com/bhauman/devcards), use the `+devcards` flag:

```
lein new reagent <name> +devcards
```

To add [CIDER](https://github.com/clojure-emacs/cider) plugin, use the `+cider` flag:

```
lein new reagent <name> +cider
```

To add  [less](https://github.com/Deraen/less4clj)/[sass](https://github.com/Deraen/sass4clj) support
use `+less` or `+sass` flags:

```
lein new reagent <name> +less
```

### Development mode

To start the Figwheel compiler, navigate to the project folder and run the following command in the terminal:

```
lein figwheel
```
To start the [DevCards](https://github.com/bhauman/devcards) (if enabled with `+devcards` flag) build run

```
lein figwheel devcards
```


Figwheel will automatically push cljs changes to the browser. The server will be available at [http://localhost:3449](http://localhost:3449)
once Figwheel starts up. To view your devcards, type `(switch-to-build devcards)` at the Figwheel REPL and navigate to [http://localhost:3449/cards](http://localhost:3449/cards).

Figwheel also starts `nREPL` using the value of the `:nrepl-port` in the `:figwheel`
config found in `project.clj`. By default the port is set to `7002`.

The figwheel server can have unexpected behaviors in some situations such as when using
websockets. In this case it's recommended to run a standalone instance of a web server as follows:

```
lein do clean, run
```

The application will now be available at [http://localhost:3000](http://localhost:3000).


In case of using `+less` option you may also want to run
```
lein less4j auto
```
to autocompile less files.

In case of using `+sass` option you may also want to run
```
lein sass4clj auto
```
to autocompile sass/scss files.

#### Optional development tools

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

### Building for release

```
lein do clean, uberjar
```

### Deploying to Heroku

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

## Contents

The template packages everything you need to create a production ready ClojureScript application following current best practices. The template uses the following features and libraries:

* [Reagent](https://github.com/reagent-project/reagent) - ClojureScript interface to Facebook's React
* [reagent-forms](https://github.com/reagent-project/reagent-forms) - data binding library for Reagent
* [reagent-utils](https://github.com/reagent-project/reagent-utils) - utilities such as session and cookie management
* [Secretary](https://github.com/gf3/secretary) - client-side routing
* [Accountant](https://github.com/venantius/accountant) - additional setup facilities for client-side routing in SPA way
* [Hiccup](https://github.com/weavejester/hiccup) - server-side HTML templating
* [Compojure](https://github.com/weavejester/compojure) - a popular routing library
* [Ring](https://github.com/ring-clojure/ring) - Clojure HTTP interface
* [Prone](https://github.com/magnars/prone) - better exception reporting middleware for Ring
* [Heroku](https://www.heroku.com/) - the template is setup to work on Heroku out of the box, simply run `git push heroku master`

## Options

The template supports the following options:

* `+test` - ClojureScript testing support
* `+spec` - use [speclj](https://github.com/slagyr/speclj) for TDD/BDD
* `+less` - use [less](https://github.com/Deraen/less4clj) for compiling Less CSS files
* `+sass` - use [sass](https://github.com/Deraen/sass4clj) for compiling Sass/Scss CSS files
* `+devcards` - add [Devcards](https://github.com/bhauman/devcards) support
* `+cider` - add [CIDER](https://github.com/clojure-emacs/cider) support

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

* JDK 1.7+
* Leiningen 2.x

## License

Copyright © 2015 Dmitri Sotnikov

Distributed under the The MIT License (MIT).

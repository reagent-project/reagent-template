reagent-template
================

A [Leiningen](http://leiningen.org/) template for projects using Reagent.


![reagent-template](logo-rounded.jpg)

A Leiningen template for a Clojure/ClojureScript app based on Reagent,
with dynamic reloading of Clojure, ClojureScript, and CSS and a browser-connected REPL.

The template uses [lein-ring](https://github.com/weavejester/lein-ring) to provide
support of standalone runnable `jar` or `war` for container deployment.
In addition, the template provides the artifacts necessary for Heroku deployment.

## Usage

Create a new application project:

```
lein new reagent <name>
```

To enable [clojurescript testing](https://github.com/cemerick/clojurescript.test) with [PhantomJS](http://phantomjs.org/), use `+test` flag:

```
lein new reagent <name> +test
```
To run the tests, please use `lein cljsbuild test`. For installation instructions of PhantomJS, please see [this](http://phantomjs.org/download.html).


Create a new library project (development dependencies are moved into the :dev profile) :

```
lein new reagent <name> +lib
```


To enable [cljx](https://github.com/lynaghk/cljx) support use `+cljx` flag:

```
lein new reagent <name> +cljx
```
When using `cljx` make sure to run `lein cljx` to cross-compile `cljx` namespaces.


### Development mode

To run the development server, run

```
lein figwheel
```
Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

#### Optional development tools

Start the browser REPL:

```
$ lein repl

(require '<app-name>.dev)
(<app-name>/browser-repl)
```

### Building for release

```
lein cljsbuild clean
lein ring uberjar
```

reagent-template
================

A Leiningen template for projects using Reagent.

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

Wait a bit, then browse to [http://localhost:3000](http://localhost:3000).

#### Optional development tools

Automatically push cljs changes to the browser:

```
$ lein repl

(start-figwheel)
```

Start the browser REPL:

```
$ lein repl

(browser-repl)
```


### Building for release

```
lein cljsbuild clean
lein ring uberjar
```

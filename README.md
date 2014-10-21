reagent-template
================

A Leiningen template for projects using Reagent.

A Leiningen template for a Clojure/ClojureScript app based on Reagent,
with dynamic reloading of Clojure, ClojureScript, and CSS and a browser-connected REPL.

The template uses [lein-ring](https://github.com/weavejester/lein-ring) to provide
support of standalone runnable `jar` or `war` for container deployment.
In addition, the template provides the artifacts necessary for Heroku deployment.

## Usage

create a new project:

```
lein new reagent <name>
```

to enable [cljx](https://github.com/lynaghk/cljx) support use `+cljx` flag:

```
lein new reagent <name> +cljx
```

### Development mode

start the server:

```
lein ring server
```

start the ClojureScript compiler:

```
lein cljsbuild once
```

start the browser REPL:

```
$ lein repl

(start-figwheel)
```

Wait a bit, then browse to [http://localhost:10555](http://localhost:10555).


### Building for release

```
lein cljsbuild clean
lein ring uberjar
```

# reagent-template

A [Leiningen](http://leiningen.org/) template for projects using Reagent.

![reagent-template](logo-rounded.jpg)

A Leiningen template for a Clojure/ClojureScript app based on Reagent, with a focus on providing
a betteries included setup for development and deployment.

### Development

The template provides dynamic reloading of Clojure, ClojureScript, and CSS and a browser-connected REPL
via [Figwheel](https://github.com/bhauman/lein-figwheel).

### Deployment

The template uses [lein-ring](https://github.com/weavejester/lein-ring) to provide
support of standalone runnable `jar` or `war` for container deployment. A `Procfile`
along with the necessary artifacts for Heroku deployment is also provided.

## Getting Help

For any questions or discussion please come join us at the [Reagent Google Group](https://groups.google.com/forum/#!forum/reagent-project).

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

(browser-repl)
```

### Building for release

```
lein cljsbuild clean
lein ring uberjar
```

## Contents

The template packages everything you need to create a production ready ClojureScript application following current best practices. The template uses the following features and libraries:

* [Reagent](https://github.com/reagent-project/reagent) - ClojureScript interface to Facebook's React
* [reagent-forms](https://github.com/reagent-project/reagent-forms) - data binding library for Reagent
* [reagent-utils](https://github.com/reagent-project/reagent-utils) - utilities such as session and cookie management
* [Secretary](https://github.com/gf3/secretary) - client-side routing
* [Selmer](https://github.com/yogthos/Selmer) - HTML templating
* [Compojure](https://github.com/weavejester/compojure) - a popular routing library
* [Ring](https://github.com/ring-clojure/ring) - Clojure HTTP interface
* [Prone](https://github.com/magnars/prone) - better exception reporting middleware for Ring
* [Heroku](https://www.heroku.com/) - the template is setup to work on Heroku out of the box, simply run `git push heroku master`
* [clojurescript.test](https://github.com/cemerick/clojurescript.test) - a maximal port of clojure.test to ClojureScript
 
## Options

The template supports the following options:

* `+cljx` - enable cross-compiling of Clojure/Script sources
* `+test` - ClojureScript testing support
 
## Customizing and Contributing

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




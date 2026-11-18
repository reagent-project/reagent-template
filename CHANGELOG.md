# Changelog

## 1.0.0 (2026-11-18)

The first release since 0.10.57 (June 2022). Generated projects now use
shadow-cljs only, with Reagent 2 and React 19 from npm.

### Breaking changes

- Projects always use [shadow-cljs](https://github.com/thheller/shadow-cljs).
  lein-cljsbuild, Figwheel, figwheel-sidecar, piggieback and the cljsjs React
  packages are gone.
- Removed the `+figwheel`, `+spec` and `+devcards` options. Passing one prints
  why it was removed.
- Removed Heroku support (`Procfile`, `system.properties` and the deployment
  docs).
- Removed the `env/` folder. The `dev.cljs`/`prod.cljs` entry points are gone
  (shadow-cljs calls `core/init!`), and the dev/prod middleware namespaces are
  replaced by middleware defined in the handler.
- Removed lein-environ. The `:dev` flag now comes from `dev/config.edn`, which
  is only on the classpath in the `:dev` profile. Previously an uberjar run
  from the project folder picked up `{:dev true}` from `.lein-env`.
- Requires JDK 21+ (for shadow-cljs) and Node.js.

### Changes

- Reagent 2.0.1, React 19.3 and shadow-cljs 3.5.14. Also ClojureScript
  1.12.145, Clojure 1.12.6, Ring 1.15.6, ring-defaults 0.7.1, Reitit 0.11.0
  and nREPL 1.8.0.
- `lein uberjar` builds the JavaScript with `npx shadow-cljs release app`.
- The React root is created on first mount, so loading the core namespace
  without an `#app` element (for example in tests) works.
- shadow-cljs reloads CSS changes from `resources/public`, including less and
  sass output.
- `+test` uses the shadow-cljs `:browser-test` target
  (`npx shadow-cljs watch test`, port 8021) and runs once in headless Chrome
  with Karma using `npm test`. doo is removed.
- `+cider` adds cider-nrepl and refactor-nrepl to the `:dev` profile, and
  shadow-cljs loads their middleware into its nREPL server.
- `+less` uses the `:less4clj` config key, and `+less +sass` together no
  longer produces duplicate `:plugins`.
- The session and anti-forgery middleware from `site-defaults` are turned
  off. The shadow-cljs dev server can't send `Set-Cookie` as a list, which
  broke every page in development.
- The generated README documents the project structure, the shadow-cljs
  workflow and CIDER usage.

## 0.10.57 and earlier

No changelog was kept. See the
[git history](https://github.com/reagent-project/reagent-template/commits/master).

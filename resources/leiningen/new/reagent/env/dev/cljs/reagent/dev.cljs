(ns ^:figwheel-no-load {{project-ns}}.dev
  (:require
    [{{project-ns}}.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)

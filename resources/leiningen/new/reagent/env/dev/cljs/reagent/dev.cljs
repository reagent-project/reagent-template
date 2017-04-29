(ns ^:figwheel-no-load {{project-ns}}.dev
  (:require [{{project-ns}}.core :as core]
            [figwheel.client :as figwheel :include-macros true]))

(enable-console-print!)

(core/init!)

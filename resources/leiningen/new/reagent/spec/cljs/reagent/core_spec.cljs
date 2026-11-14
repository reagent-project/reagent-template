(ns {{project-ns}}.core-spec
  (:require-macros [speclj.core :refer [describe it should]])
  (:require [speclj.core]
            [reagent.dom.server :as rdoms]
            [{{project-ns}}.core :as rc]))

(describe "test home"
  (it "contains 'Welcome to' in home page"
      (should (re-find #"Welcome to" (rdoms/render-to-string [rc/home-page])))))

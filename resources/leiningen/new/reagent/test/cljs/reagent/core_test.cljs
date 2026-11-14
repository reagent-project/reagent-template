(ns {{project-ns}}.core-test
  (:require
   [cljs.test :refer-macros [is deftest]]
   [reagent.dom.server :as rdoms]
   [{{project-ns}}.core :as rc]))

(deftest test-home
  (is (re-find #"Welcome to" (rdoms/render-to-string [rc/home-page]))))

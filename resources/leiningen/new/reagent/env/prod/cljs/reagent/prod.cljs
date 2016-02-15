(ns {{project-ns}}.prod
  (:require [{{project-ns}}.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)

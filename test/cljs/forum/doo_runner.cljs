(ns forum.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [forum.core-test]))

(doo-tests 'forum.core-test)


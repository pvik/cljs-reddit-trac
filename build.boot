(set-env!
 :source-paths #{"src/scss" "src/cljs"}
 :resource-paths #{"html"}

 :dependencies '[[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.339"]
                 ;; common
                 [org.clojure/core.async "0.4.474"]
                 [hickory "0.7.1"]
                 ;; cljs
                 [reagent "0.8.1"]
                 [hodgepodge "0.1.3"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [prismatic/dommy "1.1.0"]
                 [hipo "0.5.2"]
                 [cljs-http "0.1.45"]
                 ;; logging
                 [com.taoensso/timbre "4.10.0"] ;; logging
                 ;; spec , test & gen
                 [org.clojure/test.check "0.9.0"]
                 ;; Dependencies for build process
                 [adzerk/boot-cljs "2.1.4" :scope "test"]
                 [adzerk/boot-test "1.2.0" :scope "test"]
                 [pandeiro/boot-http "0.8.3" :scope "test"]
                 [adzerk/boot-reload "0.6.0" :scope "test"]
                 [adzerk/boot-cljs-repl "0.4.0" :scope "test"]
                 [cider/piggieback "0.3.9" :scope "test"]
                 [nrepl "0.4.5" :scope "test"]
                 [weasel "0.7.0" :scope "test"]
                 [deraen/boot-sass "0.3.1" :scope "test"]
                 [tolitius/boot-check "0.1.11" :scope "test"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[deraen.boot-sass      :refer [sass]]
 '[pandeiro.boot-http    :refer [serve]])

(deftask dev []
  (comp (serve :dir "target/")
        (watch)
        (speak)
        (reload :on-jsload 'reddit-trac.core/main)
        (cljs-repl)
        (sass)
        (cljs :source-map true :optimizations :none)))

(deftask build []
  (cljs :optimizations :advanced))

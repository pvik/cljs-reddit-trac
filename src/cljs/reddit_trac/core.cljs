(ns reddit-trac.core
  (:require [dommy.core :as dom] 
            [hipo.core :as hipo]
            [taoensso.timbre :as log]
            [reddit-trac.handler :as handler]
            [reddit-trac.web.notification :as notification]))

(enable-console-print!)

;; Code executed on all pages
(defonce _
  (do
    (log/info "Initializing...")
    (notification/render-notifications)))

(defn main []
  (log/info "in main fn"))

(defn ^:export dashboard []
  (log/info "dashboard")
  (if (dom/sel1 :#btn-create-watch)
    (dom/listen! (dom/sel1 :#btn-create-watch)
                 :click
                 handler/create-watch)))

(defn ^:export manage []
  (log/info "dashboard"))

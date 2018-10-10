(ns reddit-trac.core
  (:require [dommy.core :as dom] 
            [hipo.core :as hipo]
            [cemerick.url :as url]
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
  (log/info "manage")
  (log/debug "query:" (:query (url/url (-> js/window .-location .-href))))
  (if (dom/sel1 :#btn-resend-watch)
    (dom/listen! (dom/sel1 :#btn-resend-watch)
                 :click
                 handler/send-manage-watch))
  (let [query (:query (url/url (-> js/window .-location .-href)))
        email (get query "email")
        token (get query "token")]
    (handler/manage-watch email token)))

(defn ^:export validate []
  (log/info "validate")
  (log/debug "query:" (:query (url/url (-> js/window .-location .-href))))
  (let [query (:query (url/url (-> js/window .-location .-href)))
        id    (get query "id")
        email (get query "email")
        token (get query "token")]
    (handler/validate-watch id email token)))

(defn ^:export deletew []
  (log/info "delete")
  (log/debug "query:" (:query (url/url (-> js/window .-location .-href))))
  (let [query (:query (url/url (-> js/window .-location .-href)))
        id    (get query "id")
        email (get query "email")
        token (get query "token")]
    (handler/delete-watch id email token)))

(ns reddit-trac.handler
  (:require [dommy.core :as dom] 
            [hipo.core :as hipo]
            [clojure.core.async :refer [<! go]]
            [taoensso.timbre :as log]
            [reddit-trac.helper :as helper]
            [reddit-trac.api :as api]
            [reddit-trac.web.template :as template]
            [reddit-trac.web.notification :as notify]))

(defn create-watch [e]
  
  (notify/clear-all-toasts)
  (dom/remove-class! (dom/sel1 :#input-email) :is-error)
  (dom/remove-class! (dom/sel1 :#input-subreddit) :is-error)
  (dom/remove-class! (dom/sel1 :#input-keywords) :is-error)
  (let [email1          (.-value (dom/sel1 :#input-email))
        subreddit1      (.-value (dom/sel1 :#input-subreddit))
        keywords1       (.-value (dom/sel1 :#input-keywords))
        email           (if (clojure.string/blank? email1) nil email1)
        subreddit       (if (clojure.string/blank? subreddit1) nil subreddit1)
        keywords        (if (clojure.string/blank? keywords1) nil keywords1)]
    (if (and email subreddit keywords)
      (go
        (dom/add-class! (dom/sel1 :#btn-create-watch) :loading)
        (let [keywords-ignore (.-value (dom/sel1 :#input-keywords-ignore))
              domain-ignore   (.-value (dom/sel1 :#input-domain-ignore))
              check-flair     (.-checked (dom/sel1 :#input-check-flair))
              resp            (<! (api/create-watch
                                   {:email           email
                                    :subreddit       subreddit
                                    :keywords        keywords
                                    :ignore-keywords keywords-ignore
                                    :ignore-domain   domain-ignore
                                    :check-flair     check-flair}))]
          (cond
            (= (:status resp) :ok)    (notify/add-toast :success
                                                        "Watch Created, Please check you email")
            (= (:status resp) :error) (notify/add-toast :error
                                                        (str "Create Failed: "
                                                             (:message resp)))
            :else (notify/add-toast :error
                                    (str "Invalid Create Response: " resp)))
          (dom/remove-class! (dom/sel1 :#btn-create-watch) :loading)))
      (do
        (if (not email)
          (dom/add-class! (dom/sel1 :#input-email) :is-error))
        (if (not subreddit)
          (dom/add-class! (dom/sel1 :#input-subreddit) :is-error))
        (if (not keywords)
          (dom/add-class! (dom/sel1 :#input-keywords) :is-error))
        (notify/add-toast :error
                          "Please fill in all required fields")))))

(defn validate-watch [id email token]
  (if (and id email token)
    (go
      (let [resp            (<! (api/validate-watch
                                 {:id id
                                  :email email
                                  :token token}))]
        (cond
          (= (:status resp) :ok)    (notify/add-toast :success
                                                      "Watch Created, Please check you email")
          (= (:status resp) :error) (notify/add-toast :error
                                                      (str "Create Failed: "
                                                           (:message resp)))
          :else (notify/add-toast :error
                                  (str "Invalid Create Response: " resp)))))
    (notify/add-toast :error
                      "Invalid Watch Parameters")))

(defn delete-watch [id email token]
  (if (and id email token)
    (go
      (let [resp            (<! (api/delete-watch
                                 {:id id
                                  :email email
                                  :token token}))]
        (cond
          (= (:status resp) :ok)    (notify/add-toast :success
                                                      "Watch Deleted!")
          (= (:status resp) :error) (notify/add-toast :error
                                                      (str "Delete Failed: "
                                                           (:message resp)))
          :else (notify/add-toast :error
                                  (str "Invalid Delete Response: " resp)))))
    (notify/add-toast :error
                      "Invalid Watch Parameters")))

(defn manage-watch [email token]
  (if (and email token)
    (do
      (helper/remove-all-child-nodes :#contents)
      (.appendChild (dom/sel1 :#contents)
                    (hipo/create [:h4 "Retrieving Watches"]))
      (go
        (let [resp            (<! (api/get-watch
                                   {:email email
                                    :token token}))]
          (cond
            (= (:status resp) :ok)
            (do
              (log/debug "watches:" (:content resp))
              (log/debug "count" (count (:content resp)))
              (log/debug "json" (.parse js/JSON (:content resp)))
              (log/debug "json->clj" (js->clj (.parse js/JSON (:content resp)) :keywordize-keys true))
              (helper/remove-all-child-nodes :#contents)
              (.appendChild (dom/sel1 :#contents)
                            (template/watches (js->clj (.parse js/JSON (:content resp)) :keywordize-keys true))))
            
            (= (:status resp) :error) (notify/add-toast :error
                                                        (str "Operation Failed: "
                                                             (:message resp)))
            :else (notify/add-toast :error
                                    (str "Invalid Response: " resp))))))))

(defn send-manage-watch [e]
  (notify/clear-all-toasts)
  (dom/remove-class! (dom/sel1 :#input-email) :is-error)
  (let [email1          (.-value (dom/sel1 :#input-email))
        email           (if (clojure.string/blank? email1) nil email1)]
    (if email
      (go
        (dom/add-class! (dom/sel1 :#btn-resend-watch) :loading)
        (let [resp            (<! (api/resend-manage-watch
                                   {:email email}))]
          (cond
            (= (:status resp) :ok)    (notify/add-toast :success
                                                        "Email Sent! Please check you email")
            (= (:status resp) :error) (notify/add-toast :error
                                                        (str "Failed: "
                                                             (:message resp)))
            :else (notify/add-toast :error
                                    (str "Invalid Response: " resp)))
          (dom/remove-class! (dom/sel1 :#btn-resend-watch) :loading)))
      (do
        (if (not email)
          (dom/add-class! (dom/sel1 :#input-email) :is-error))
        (notify/add-toast :error
                          "Please fill in email address.")))))

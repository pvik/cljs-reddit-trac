(ns reddit-trac.api
  (:require [clojure.core.async :refer [<! go]]
            [cljs-http.client :as http]
            [taoensso.timbre :as log]))

(defonce api-uri "http://localhost:3001/api")

(defn get-watch [{:keys [email token]}]
  (log/info "get-watch" email)
  (go (let [response (<! (http/get (str api-uri "/watch")
                                   {:with-credentials? false
                                    :query-params {"email" email
                                                   "token" token}}))
            body (:body response)]
        (log/debug "response:" response)
        (log/debug "status:" (:status response))
        (log/debug "body:" (:body response))
        (if (= (:status response) 200)
          {:status :ok
           :content body}
          {:status :error
           :message body}))))

(defn create-watch [watch]
  (log/info "create-watch" watch)
  (go (let [response (<! (http/put (str api-uri "/watch")
                                   {:with-credentials? false
                                    :json-params watch}))
            body (:body response)]
        (log/debug "response:" response)
        (log/debug "status:" (:status response))
        (log/debug "body:" (:body response))
        (if (= (:status response) 200)
          {:status :ok}
          {:status :error
           :message body}))))

(defn validate-watch [{:keys [id email token]}]
  (log/info "validate-watch" id "-" email)
  (go (let [response (<! (http/get (str api-uri "/watch/validate/" id)
                                   {:with-credentials? false
                                    :query-params {"email" email
                                                   "token" token}}))
            body (:body response)]
        (log/debug "response:" response)
        (log/debug "status:" (:status response))
        (log/debug "body:" (:body response))
        (if (= (:status response) 200)
          {:status :ok}
          {:status :error
           :message body}))))

(defn delete-watch [{:keys [id email token]}]
  (log/info "delete-watch" id "-" email)
  (go (let [response (<! (http/delete (str api-uri "/watch/" id)
                                      {:with-credentials? false
                                       :query-params {"email" email
                                                      "token" token}}))
            body (:body response)]
        (log/debug "response:" response)
        (log/debug "status:" (:status response))
        (log/debug "body:" (:body response))
        (if (= (:status response) 200)
          {:status :ok}
          {:status :error
           :message body}))))

(defn resend-manage-watch [{:keys [email]}]
  (log/info "resend-manage-watch" email)
  (go (let [response (<! (http/get (str api-uri "/watch/manage")
                                   {:with-credentials? false
                                    :query-params {"email" email}}))
            body (:body response)]
        (log/debug "response:" response)
        (log/debug "status:" (:status response))
        (log/debug "body:" (:body response))
        (if (= (:status response) 200)
          {:status :ok
           :content body}
          {:status :error
           :message body}))))

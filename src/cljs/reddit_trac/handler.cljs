(ns reddit-trac.handler
  (:require [dommy.core :as dom] 
            [hipo.core :as hipo]
            [clojure.core.async :refer [<! go]]
            [taoensso.timbre :as log]
            [reddit-trac.api :as api]
            [reddit-trac.web.notification :as notify]))

(defn create-watch [e]
  (dom/add-class! (dom/sel1 :#btn-create-watch) :loading)
  (go
    (let [email           (.-value (dom/sel1 :#input-email))
          keywords        (.-value (dom/sel1 :#input-keywords))
          keywords-ignore (.-value (dom/sel1 :#input-keywords-ignore))
          domain-ignore   (.-value (dom/sel1 :#input-domain-ignore))
          check-flair     (.-value (dom/sel1 :#input-check-flair))
          resp            (<! (api/create-watch
                               {:email           email
                                :keywords        keywords
                                :keywords-ignore keywords-ignore
                                :domain-ignore   domain-ignore
                                :check-flair     check-flair}))]
      (cond
        (= (:created? resp) :ok)    (do
                                      (notify/add-toast :error
                                                        "Watch Created, Please check you email"))
        (= (:created? resp) :error) (do
                                      (notify/add-toast :error
                                                        (str "Create Failed: "
                                                             (:message resp)))))
      (dom/remove-class! (dom/sel1 :#btn-create-watch) :loading))))

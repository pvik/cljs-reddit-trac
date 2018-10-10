(ns reddit-trac.web.template
  (:require [hipo.core :as hipo]))

(defn watches [watches]
  (hipo/create
   [:div
    [:h3 "Watches"]
    [:h4 (:email (first watches))]
    [:table {:class "table table-striped table-hover"}
     [:tr
      [:th "Subreddit"]
      [:th "Keywords"]
      [:th "Ignore Keywords"]
      [:th "Ignore Domain"]
      [:th "Check Flair"]
      [:th "Created On"]
      [:th ""]]
     (for [w watches]
       [:tr
        [:td (:subreddit w)]
        [:td (:keywords w)]
        [:td (:ignore-keywords w)]
        [:td (:ignore-domain w)]
        [:td (:check-flair w)]
        [:td (:created-on w)]
        [:td "Delete"]])]]))





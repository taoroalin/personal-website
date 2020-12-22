(ns graphminer.net.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [reagent.session :as session]
   [reitit.frontend :as reitit]
   [clerk.core :as clerk]
   [accountant.core :as accountant]
   [graphminer.net.blogs :refer [blogs-hiccup]]))

;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/" :index]
    ["/about" :about]
    ["/blog" :blog]]))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))

;; -------------------------
;; Page components


(defn blog []
  (print blogs-hiccup)
  (fn [] [:div blogs-hiccup]))

(defn about-page []
  (fn [] [:span.main
          [:h1 "About graphminer.net"]]))


;; -------------------------
;; Translate routes -> page components

(defn page-for [route]
  (case route
    :index #'about-page
    :about #'about-page
    :blog #'blog))


;; -------------------------
;; Page mounting component

(defn current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div
       [:header {:style {:display "flex" :flex-direction "row" :align-items "center"}}
        [:h1 {:style {:margin-right "30px"}} "Tao Lin"]
        [:p [:a {:href (path-for :index)} "About"] " | "
         [:a {:href (path-for :blog)} "Blog"] " | "
         [:a {:href "https://boiling-peak-00646.herokuapp.com/"} "Web Dev Demo"]]]
       [page]
       [:footer
        [:div {:style {:display "flex" :flex-direction "column" :margin-top "80px"}}
         [:div.my-links
          [:a {:href "https://twitter.com/taoroalin"} "twitter"]
          [:a {:href "https://github.com/taoroalin"} "github"]
          [:a {:href "mailto:taoroalin@gmail.com"} "email"]
          [:a {:href "https://roamresearch.com/#/app/graphminer"} "personal notes"]]
         [:p "This website was made with this "
          [:a {:href "https://github.com/taoroalin/personal-website"} "source code"]
          " in ClojureScript with Reagent."]]]])))

;; -------------------------
;; Initialize app

(defn mount-root []
  (rdom/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (clerk/initialize!)
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (let [match (reitit/match-by-path router path)
            current-page (:name (:data  match))
            route-params (:path-params match)]
        (reagent/after-render clerk/after-render!)
        (session/put! :route {:current-page (page-for current-page)
                              :route-params route-params})
        (clerk/navigate-page! path)))
    :path-exists?
    (fn [path]
      (boolean (reitit/match-by-path router path)))})
  (accountant/dispatch-current!)
  (mount-root))

(ns conduit.core
  (:require [rum.core :as rum]
            [citrus.core :as citrus]
            [goog.dom :as dom]
            [conduit.effects :as effects]
            [conduit.router :as router]
            [conduit.controllers.articles :as articles]
            [conduit.controllers.tags :as tags]
            [conduit.controllers.tag-articles :as tag-articles]
            [conduit.controllers.article :as article]
            [conduit.controllers.comments :as comments]
            [conduit.controllers.router :as router-controller]
            [conduit.controllers.user :as user]
            [conduit.controllers.profile :as profile]
            [conduit.components.root :refer [Root]]
            [conduit.components.home :as home]
            [conduit.components.article :refer [Article]]))

(def routes
  ["/" [["" :home]
        [["page/" :page] :home]
        [["tag/" :id] [["" :tag]
                       [["/page/" :page] :tag]]]
        [["article/" :id] :article]
        ["editor" [["" :editor]
                   [["/" :slug] :editor]]]
        ["login" :login]
        ["register" :register]]])

;; create Reconciler instance
(defonce reconciler
  (citrus/reconciler
    {:state           (atom {})
     :controllers     {:articles     articles/control
                       :tag-articles tag-articles/control
                       :tags         tags/control
                       :article      article/control
                       :comments     comments/control
                       :router       router-controller/control
                       :user         user/control
                       :profile      profile/control}
     :effect-handlers {:http          effects/http
                       :local-storage effects/local-storage
                       :redirect      effects/redirect
                       :dispatch      effects/dispatch}}))

;; initialize controllers
(defonce init-ctrl (citrus/broadcast-sync! reconciler :init))

(router/start! #(citrus/dispatch! reconciler :router :push %) routes)

(rum/mount (Root reconciler)
           (dom/getElement "app"))

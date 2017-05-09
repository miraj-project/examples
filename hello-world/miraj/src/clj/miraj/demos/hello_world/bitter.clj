(ns miraj.demos.hello-world.bitter
  (:require [miraj.core :as miraj]
            [miraj.html :as h :refer :all :exclude [meta]]
            [miraj.co-dom :as codom :refer [pprint serialize]]
            :reload))

(miraj/defpage
  "Hello World - Miraj (bitter) Demo Page"

  (:css [css.bitter])
  ;;(:js  [js.bitter])

  ;; html metadata
  {::h/title"Miraj demo: hello-world"
   ::h/description "This page demonstrates of a simple miraj webpage."
   ::h/charset "utf-8"
   ::h/viewport {::h/width :device-width
                 ::h/scale {::h/min 0.5
                            ::h/max 2
                            ::h/initial 1}
                 ::h/user-scalable true}
   ::h/pragma {::h/x-ua-compatible "IE=edge,chrome=1"
               ;; :content-language "en"   ;; obsolete
               ;; ::h/content-security-policy "default-src https://cdn.example.net; child-src 'none'; object-src 'none'"
               ;; :content-type "foo"   ;; obsolete
               ::h/default-style "main-style"
               ;; ::h/refresh "20; URL=page4.html"
               ;; :set-cookie "foo"  ;; obsolete
               ::h/pics-label "pics label"
               }

   ::h/icons [{::h/href "favicon.png" ::h/sizes #{[16 16]} ::h/type "image/png"}
              {::h/href "mac.icns" ::h/sizes #{[128 128] [512 512] [8192 8192] [32768 32768]}}]
   }

  (:body
   (h1 "Hello Miraj World!")
   (h2 :#h {:miraj.style/hover {:background-color "#A5D6A7"}} "(bitter)")
   (div :#main
        (span :!centered
              (button {:onclick "handle_click()"} "click me")))))

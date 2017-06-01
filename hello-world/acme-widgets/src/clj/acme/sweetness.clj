(ns acme.sweetness
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [miraj.core :as miraj]
            [miraj.html :as h]
            [miraj.html.protocols :as hp]
            [miraj.polymer :as p :refer [slot]]
            [miraj.polymer.protocols :as poly]
            ;; [miraj.polymer.paper :as paper]

            ;; for testing only:
            [miraj.co-dom :as codom]
            #_:reload))

(println "loading miraj.demos.hello-world.sweetness")


;; in the lib, this will be exported as miraj.demos.hello-world.widgets/sweet
(miraj/defcomponent sweet :html sweetness-sweet
  "sweet"
  (:require [miraj.polymer.paper :as paper :refer [button card]]
            [miraj.polymer.iron :as iron :refer [icon]])

  (:codom
     (paper/card {:heading "Sweet Card"}
               (h/div :message)
               (h/div (h/span "Click count: " :click-count))
               (h/div (h/span "Mouseover count: " :mouseover-count))
               (h/div
                (h/span :greeting ", " :flavor)
                (h/span
                 (p/slot)
                 ))))

  ;; prototype map - this will generate the js prototype code for the component
  {:polymer/properties {;; :greeting is a property, also set as a static attribute below
                        :greeting ^String{:value "Hello"
                                          :type String
                                          :observer (fn [new old] (sweet/observe-greeting new old))}
                        :flavor {:value "SWEET"
                                 :type String}
                        :message {:value (sweet/say)}
                        :click-count {:value 0 :type Number
                                      :observer (fn [new old]
                                                  (sweet/observe-click-count new old))}
                        :mouseover-count {:value 0 :type Number
                                           :observer (fn [new old]
                                                       (sweet/observe-mouseover-count new old))}
                        }

   ;; static html attributes on host (Polymer hostAttributes property)
   ;; see https://www.polymer-project.org/1.0/docs/devguide/registering-elements#host-attributes
   ;; these will cause html attrs to be set at create time
   ;; inspect the rendered host element to see them
   :polymer/static {:string-attr1 "attr1"
                    :boolean-attr2 true
                    :greeting "Hello"
                    :tabindex 0}

   ;; complex observers take (keyword) properties as params
   ;; this will be fired whenever either property changes
   ;; https://www.polymer-project.org/1.0/docs/devguide/observers#complex-observers
   :greeting-flavor-observer (fn [:greeting :flavor]
                               ;; pass the args as syms
                               (sweet/greeting-flavor-observer greeting flavor))

   ;; local properties - we can put them in the prototype, or in a cljs namespace
   ;; for polymer binding, they must be in the prototype
   :name {:last "Smith"
          :first "John"}

   ;; "instance" methods (https://www.polymer-project.org/1.0/docs/devguide/instance-methods)
   ;; with javascript, instance methods go in the component's prototype
   ;; with clojurescript, we don't need this - just use functions in the delegate namespace
   }

  miraj.html.protocols/Mouse
  (click [e] (this-as this (sweet/click this e)))
  (mouseover [e] (this-as this (sweet/mouseover this e)))
)

;; sweeter - we can embed css in two ways.
(miraj/defcomponent sweeter :html sweetness-sweeter
  "sweeter"
  (:require [miraj.polymer.paper :as paper :refer [button card]]
            [miraj.polymer.iron :as iron :refer [icon]])
  (:css "#foo {color: blue;}") ;; this will be moved into the <template> element
  (:codom
   (h/style "paper-card {text-align: center;}") ;; direct html coding works too, inside :codom
   (paper/card {:heading "Sweeter Card"}
    (h/span :#foo "Sweeter")
    (h/span {:miraj.style/color "red"}
     (p/slot)
     ))))

;; sweetest - fun with bindings
#_(miraj/defcomponent sweetest :html sweetness-sweetest
  "sweetest"

  ;;(:require [miraj.polymer.paper :as paper])
  (:codom
   (h/style ":host {display: block; width: 50%; border:thick blue solid;}
 span {background-color:#E0F2F1;}")
   (h/style ":host h1, :host h2 {text-align:center;}")
   (h/h1 "Sweetest!")

   (h/div (h/h4 "Another Fine Sweetest Message!"))
   (h/div 'message) ;; 1-way binding, 'message => [[message]]
   (h/div :#special.page-title
          (h/span :greeting) ;; :greeting == {{greeting}}, 2-way binding
          (h/span ", ")
          (h/span :addressee))
   (h/span "UPDATE text to change the greeting.")
   (h/input {:class "paper-font-body2" :value :input->greeting}) ;; input event sends value to greeting
   (h/input {:class "paper-font-body2" :value :input->addressee}))

  ;; polymer prototype
  {:polymer/properties {:greeting ^String{:value "hello"
                                          ;; :type String
                                          ;; FIXME: alias for helper ns
                                          :observer (fn [new old] (del/observe-greeting new old))
                                          ;;:observer (clj->js 'del/observe-greeting)
                                          }
                        :addressee {:value "SWEETEST"
                                    ;; :type String
                                    :observer #(.log js/console (str "Addressee CHG OBSERVED " %))
                                    }
                        ;; :message {:value (fn [:greeting :addressee] (str greeting ", " addressee))}
                        :message {:value (del/say)}

                        :foo {:type String}
                        ;; :value "howdy there"} ;; (fn [] "msg")}

                        ;; :foo {:value [a b c]
                        ;;       :read-only true
                        ;;       :notify true
                        ;;       :reflect-to-attribute true}
                        ;;                        :bar {:value {:a 1 :b 2}
                        ;;                              ;; :type Object
                        ;;                              }
                        ;;                        :baz Vector
                        ;;                        :buz Map
                        ;;                        :test-fn {:value 3
                        ;;                                  :observer (fn [test-str] (.log js/console (str "CHG OBSERVED " test-str))
                        ;; )}
                        ;; a value initializer
                        }

   ;; static html attributes on host (Polymer hostAttributes property)
   ;; these will cause html attr vals to be set at create time
   ;; see https://www.polymer-project.org/1.0/docs/devguide/registering-elements#host-attributes
   :polymer/static {:string-attr1 "attr1"
                    :boolean-attr2 true
                    :foo "Hello"
                    :tabindex 0}

   ;; complex observers take properties as params
   :compound-obs (fn [:greeting :addressee]
                   (.log js/console "Compound observation: " greeting ", " addressee)
                   (this-as this (set! (.-foo this) (str greeting ", " addressee " ()"))))

   ;; to use clojure fn as complex observer, we need to know the args
   ;; slurp the cljs code and get the arglist meta?
   ;; :polymer/observers [miraj.demo.basic.hello/complex-observer]
   ;; or use fn app syntax?
   ;;   :polymer/observers [(miraj.demo.basic.hello/complex-observer :greeting :addressee)]

   ;; local properties - we can put them in the prototype, or in a cljs namespace
   ;; for polymer binding, they must be in the prototype
   ;; some test data
   :name {:last "Smith"
          :first "John"}

   ;; "instance" methods (https://www.polymer-project.org/1.0/docs/devguide/instance-methods)

   ;; TODO: use protocols. each component must use defprotocol if it wants public methods?
   :public-method (fn [foo bar] 99)

   ;; use clojure fns instead of pseudo-private methods!
   :_private-method (fn [baz] )
   :_private-method2 #(.log js/console (str "Hash fn: " %))
   }

  ;; public methods

  ;; every component has an implicit This protocol:
  ;; FIXME: make This protocol methods, not listeners, just like for Lifecycle protocol
  This
  (this-fn [this]  )
  ;; (bar [this]  )

  ;;   ;; Protocol methods
  ;;miraj.polymer.protocol/Lifecycle
  poly/Lifecycle
  (created [] (println "CREATED")) ;; miraj.demo.basic.hello/created)
  (ready [] (.log js/console "READY"))
  (attached [] (.log js/console "ATTACHED"))
  ;;(bad-fn [])

  ;;   miraj.polymer.protocol/Gesture
  ;;   (tap [x] (.log js/console "TAP"))
  ;;   (fdaasdfbad-fn [])

  miraj.html.protocols/Focus
  (blur [x] (.log js/console "BLUR"))
  (focusin [x] (.log js/console "FOCUSIN"))
  (focusout [x] (.log js/console "FOCUSOUT"))
                                        ;(bad-fn [])

  miraj.html.protocols/Mouse
  (click [e] (this-as this
               ;; (println "root: " (.-root this))
               (del/click this e))) ;; [x] (.log js/console "Mouseclick"))
  (mouseover [x] (del/mouseover x)) ; (.log js/console "MOUSEOVER"))
  )

                                        ;(-> hello var meta :miraj/miraj keys)
                                        ;(-> basic var meta)
(println "loaded miraj.demos.hello-world.bitterness")
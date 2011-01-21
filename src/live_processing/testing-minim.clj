(ns live-processing.testing-minim
  (:use rosado.processing)
  (:import (javax.swing JFrame))
  (:import (processing.core PApplet))
  (:import (ddf.minim Minim AudioOutput AudioPlayer))
  (:import (ddf.minim.ugens Noise Oscil LiveInput FilePlayer ; sound generators
                            Delay Pan Balance Gain BitCrush WaveShaper ; effects
                            Line ADSR Damp GranulateRandom GranulateSteady ; envelopes
                            Constant Midi2Hz Multiplier Reciprocal Summer ; math
                            )))

;; FIXME: I need a better way to deal with *applet* (processing's PApplet instance)

(def *my-applet* nil)

(proxy [PApplet] []
  (binding [*applet* this]
    (def *my-applet* this)))

;; patching

(defn --> [source-ugen target-ugen]
  (. source-ugen patch target-ugen)
  target-ugen)

(defn <-- [target-ugen source-ugen]
  (. source-ugen patch target-ugen)
  target-ugen)

(defn --x [source-ugen target-ugen]
  (. source-ugen unpatch target-ugen)
  target-ugen)

(defn x-- [target-ugen source-ugen]
  (. source-ugen unpatch target-ugen)
  target-ugen)

;; oscil

(defn osc [frequency amplitude]
  (new Oscil (float frequency) (float amplitude)))

(defn freq [target-ugen new-frequency]
  (. target-ugen setFrequency (float new-frequency)))

;; summer

(defn summer []
  (new Summer))

(defn --+ [first-ugen second-ugen]
  (let [s (summer)]
    (--> first-ugen s)
    (--> second-ugen s)
    s))

;; multiplier

(defn multiplier []
  (new Multiplier))

(defn --* [first-ugen second-ugen]
  (let [m (multiplier)]
    (--> first-ugen m)
    (--> second-ugen m)
    m))

;; starting...

(def minim (new Minim *my-applet*))

(def out (. minim getLineOut (. Minim STEREO) 2048))

(def osc-1 (osc 440 0.5))
(def osc-2 (osc 440.5 0.5))

;; testing...

; (def sum-1 (--+ osc-1 osc-2))
; (--> sum-1 out)
; (--x sum-1 out)

;; maybe... ???

(def osc-2 (--> osc-1 osc-2))

(--> osc-2 out)
(--x osc-2 out)

; (--> (--> osc-1 osc-2) out) 










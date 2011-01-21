(ns live-processing.core
  (:use rosado.processing)
  (:import (java.awt Color Font Dimension BorderLayout))
  (:import (javax.swing JFrame JPanel JEditorPane JScrollPane))
  (:import (processing.core PApplet))
  (:import (ddf.minim Minim AudioOutput AudioPlayer))
  (:import (ddf.minim.ugens Oscil))
  (:import (ddf.minim.signals SineWave))
  (:import (jsyntaxpane DefaultSyntaxKit)))

(def minim nil)
(def out nil)
;(def sine nil)
(def groove nil)

(defn my-setup [applet]
  (size 400 700)
  (def minim (new Minim applet))
  (def groove (. minim (loadFile "/home/vilson/groove.mp3" 2048)))
  (def out (. minim getLineOut (. Minim STEREO)))
  (def sine (new SineWave 440 0.5 (. out sampleRate)))
  (. out (addSignal sine))
  (framerate 2)
  (smooth))

(defn my-draw [applet]
  (framerate 0.2)
  (fill (rand 255) (rand 255) (rand 255))
  (no-stroke)
  (no-loop)
  (def groove (. minim (loadFile "/home/vilson/error.mp3" 2048)))
  (. groove loop)
  ;(. sine (setFreq (rand 500)))
  (rect (rand 400) (rand 600) 200 200))


(def processing-applet
  (proxy [PApplet] []
    (setup []
           (binding [*applet* this]
             (my-setup this)))
    (draw []
          (binding [*applet* this]
            (my-draw this)))))

(.init processing-applet)

(def swing-frame (JFrame. "Live Processing"))

;; (defn editor-panel [app]
;;   (DefaultSyntaxKit/initKit)
;;   (let [editor-pane (JPanel.)
;;         editor (JEditorPane.)
;;         ;; button-pane (editor-buttons editor)
;;         scroller (JScrollPane. editor)
;;         font (.getFont editor)
;;         fm (.getFontMetrics editor font)
;;         width (* 81 (.charWidth fm \space))
;;         height (* 10 (.getHeight fm))]


;;     ;; (doto button-pane
;;     ;;   (.setBackground (:background app)))

;;     (doto editor
      
;;       (.setFont (:edit-font app))
;;       (.setContentType "text/clojure")
;;       (.setCaretColor Color/BLACK)
;;       (.setBackground (Color. (float 0.5) (float 1.0) (float 1.0)))
;;       (.requestFocusInWindow))

;;     (doto editor-pane
;;       (.setLayout (BorderLayout.))
;;       ;; (.add button-pane BorderLayout/NORTH)
;;       (.add scroller BorderLayout/CENTER)
;;       ;; (.add (status-panel editor) BorderLayout/SOUTH)
;;       )
    
;;     ;; (open-last-file)
;;     editor-pane))


(doto swing-frame
  (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
  (.add processing-applet)
  ;; (.add (editor-panel {:background (Color. 50 50 50)
  ;;                      :edit-font (Font. "Bitstream Vera Sans Mono" Font/PLAIN 12)}))
  (.pack)
  (.show))
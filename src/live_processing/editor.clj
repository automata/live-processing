(ns live-processing.editor
  (:use rosado.processing)
  (:import (java.awt Color Font Dimension BorderLayout GridLayout FlowLayout))
  (:import (java.awt.event ActionListener))
  (:import (javax.swing JFrame JPanel JEditorPane JScrollPane JTextField JButton JLabel))
  (:import (processing.core PApplet))
  (:import (ddf.minim Minim AudioOutput AudioPlayer))
  (:import (ddf.minim.ugens Oscil))
  (:import (ddf.minim.signals SineWave))
  (:import (jsyntaxpane DefaultSyntaxKit))
  (:gen-class))

(defn live-draw [applet]
  (background 200))

(def processing-applet
  (proxy [PApplet] []
    (setup []
           (binding [*applet* this]
             (size 200 200)
             (smooth)
             (no-stroke)
             (fill 226)
             (framerate 10)))
    (draw []
          (binding [*applet* this]
            (live-draw this)))))

(defn start-processing-applet []
  (.init processing-applet)
  (let [frame (JFrame. "Processing")]
    (doto frame
      (.setSize 500 400)
      (.add processing-applet)
      (.setVisible true))
    frame))

(defn stop-processing-applet [frame]
  (doto frame
    (.hide)
    (.dispose)))

(def default-code
  "(defn live-draw [applet]
   (background 0))")

(def *processing-applet* nil)

(defn editor-buttons-panel [editor]
  (let [panel (JPanel. (FlowLayout. FlowLayout/RIGHT))
        play-button (JButton. "Play")
        stop-button (JButton. "Stop")
        eval-button (JButton. "Eval")]
    (.addActionListener eval-button
                        (proxy [ActionListener] []
                          (actionPerformed [evt]
                                           (eval (load-string (str
                                                               "(ns live-processing.editor)"
                                                               (.getText editor)
                                                               "(intern 'live-processing.editor 'live-draw)"))))))
    (.addActionListener play-button
                        (proxy [ActionListener] []
                          (actionPerformed [evt]
                                           (def *processing-applet* (start-processing-applet)))))
    (.addActionListener stop-button
                        (proxy [ActionListener] []
                          (actionPerformed [evt]
                                           (stop-processing-applet *processing-applet*))))
    (doto panel
      (.add play-button)
      (.add stop-button)
      (.add eval-button))
    panel))

(defn editor-panel [app]
  (DefaultSyntaxKit/initKit)
  (let [editor-pane (JPanel.)
        editor (JEditorPane.)
        button-pane (editor-buttons-panel editor)
        scroller (JScrollPane. editor)
        font (.getFont editor)
        fm (.getFontMetrics editor font)
        width (* 81 (.charWidth fm \space))
        height (* 10 (.getHeight fm))]
    (doto editor
      (.setFont (:edit-font app))
      (.setContentType "text/clojure")
      (.setCaretColor Color/BLACK)
      (.setBackground (Color. (float 0.9) (float 0.9) (float 0.9)))
      (.setText default-code)
      (.requestFocusInWindow))
    (doto editor-pane
      (.setLayout (BorderLayout.))
      (.add button-pane BorderLayout/NORTH)
      (.add scroller BorderLayout/CENTER))
    editor-pane))

(defn editor-frame []
  (let [frame (JFrame. "Live Processing")]
    (doto frame
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
      (.setSize 600 600)
      (.add (editor-panel {:background (Color. 50 50 50)
                           :edit-font (Font. "Bitstream Vera Sans Mono" Font/PLAIN 12)}))
      (.setVisible true))
    frame))

(defn stop-frame [frm]
  (doto frm
    (.hide)
    (.dispose)))

(defn -main [& args]
  (editor-frame))
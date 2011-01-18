(ns clj-mhsim.view
  (:use [clj-swing core frame label button combo-box list panel document text-field tree]
        [clj-mhsim controller]))

(import '(javax.swing UIManager)
	'(java.awt BasicStroke Color Dimension Graphics Graphics2D RenderingHints)
	'(java.awt.geom AffineTransform Ellipse2D)
	'(java.awt GridBagLayout FlowLayout GridLayout GridBagConstraints)
	'(java.awt.event KeyEvent)
	clj-swing.tree.Pathed)

(:use 'clojure.contrib.swing-utils)

; コンボボックスで選択された要素
(def selected {:head (atom nil) :body (atom nil) :arm (atom nil) :wst (atom nil) :leg (atom nil)})

(defn list-update! [skill-map]
  "リストの更新"
  (println
  (loop [res [] skill skill-map]
    (if (zero? (count skill))
      res
      (recur (conj res (skill-check (first (first skill)) (first (rest (first skill))))) (rest skill))))))

(defn main []
  "シミュレータの実行"
  (frame :title "Monster Hunter Simulator"
	 :layout (GridBagLayout.)
	 :size [300 300]
	 :constrains (java.awt.GridBagConstraints.)
	 :show true
	 [:gridx 0 :gridy 0
	 _ (label "頭")
	  :gridx 1 :gridy 0
	 _ combo-head (combo-box [] :model (seq-ref-combobox-model head-names (selected :head)) :action ([_] (list-update calc-skill)))
                  :gridx 0 :gridy 1
	 _ (label "胴")
	  :gridx 1 :gridy 1
	 _ (combo-box [] :model (seq-ref-combobox-model body-names (selected :body)) :action ([_] (list-update calc-skill)))
	  :gridx 0 :gridy 2
	 _ (label "腕")
	  :gridx 1 :gridy 2
	 _ (combo-box [] :model (seq-ref-combobox-model hand-names (selected :arm)) :action ([_] (list-update calc-skill)))
	  :gridx 0 :gridy 3
	 _ (label "腰")
	  :gridx 1 :gridy 3
	 _ (combo-box [] :model (seq-ref-combobox-model west-names (selected :wst)) :action ([_] (list-update calc-skill)))
	  :gridx 0 :gridy 4
	 _ (label "脚")
	  :gridx 1 :gridy 4
	 _ (combo-box [] :model (seq-ref-combobox-model leg-names (selected :leg)) :action ([_] (list-update calc-skill)))
	  :gridx 0 :gridy 5
	 _ (scroll-panel (jlist :model (seq-ref-list-model lm)) :preferred-size [150 100])]))
(ns clj-mhsim.view
  (:use [clj-swing core frame label button combo-box list panel document text-field tree]))

(import '(javax.swing UIManager)
	'(java.awt BasicStroke Color Dimension Graphics Graphics2D RenderingHints)
	'(java.awt.geom AffineTransform Ellipse2D)
	'(java.awt GridBagLayout FlowLayout GridLayout GridBagConstraints)
	'(java.awt.event KeyEvent)
	clj-swing.tree.Pathed)

(:use 'clojure.contrib.swing-utils)

; コンボボックスで選択された要素
(def selected {:head (atom nil) :body (atom nil) :arm (atom nil) :wst (atom nil) :leg (atom nil)})
(def skills (ref '[]))

(defn list-update [skill-map checker]
  "リストの更新"
  (dosync (ref-set skills
    (loop [res [] skill skill-map]
      (if (zero? (count skill))
        res
        (recur 
         (let [name (checker (first (first skill)) (first (rest (first skill))))]
          (if (= name nil)
            res
            (conj res name)))
        (rest skill)))))))

(defn show [calculator checker heads bodys arms wsts legs ]
  "シミュレータの実行"
  (frame :title "Monster Hunter Simulator"
	 :layout (GridBagLayout.)
	 :size [300 300]
	 :constrains (java.awt.GridBagConstraints.)
	 :show true
	 [:gridx 0 :gridy 0 :anchor :LINE_END
	 _ (label "頭")
	  :gridx 1 :gridy 0
	 _ (combo-box [] :model (seq-ref-combobox-model heads (selected :head)) :action ([_] (list-update (calculator selected checker))))
                  :gridx 0 :gridy 1
	 _ (label "胴")
	  :gridx 1 :gridy 1
	 _ (combo-box [] :model (seq-ref-combobox-model bodys (selected :body)) :action ([_] (list-update (calculator selected checker))))
	  :gridx 0 :gridy 2
	 _ (label "腕")
	  :gridx 1 :gridy 2
	 _ (combo-box [] :model (seq-ref-combobox-model arms (selected :arm)) :action ([_] (list-update (calculator selected checker))))
	  :gridx 0 :gridy 3
	 _ (label "腰")
	  :gridx 1 :gridy 3
	 _ (combo-box [] :model (seq-ref-combobox-model wsts (selected :wst)) :action ([_] (list-update (calculator selected checker))))
	  :gridx 0 :gridy 4
	 _ (label "脚")
	  :gridx 1 :gridy 4
	 _ (combo-box [] :model (seq-ref-combobox-model legs (selected :leg)) :action ([_] (list-update (calculator selected checker))))
	  :gridx 0 :gridy 5
	 _ (label "発動スキル")
	  :gridx 1 :gridy 5 :gridheight 3 :gridwidth 3
	 _ (scroll-panel (jlist :model (seq-ref-list-model skills)) :preferred-size [150 100])]))
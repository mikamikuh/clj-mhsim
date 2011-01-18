(ns clj-mhsim.view
  (:use [clj-swing core frame label button combo-box list panel document text-field tree]))

(import '(javax.swing UIManager)
	'(java.awt BasicStroke Color Dimension Graphics Graphics2D RenderingHints)
	'(java.awt.geom AffineTransform Ellipse2D)
	'(java.awt GridBagLayout FlowLayout GridLayout GridBagConstraints)
	'(java.awt.event KeyEvent)
	clj-swing.tree.Pathed)

(:use 'clojure.contrib.swing-utils)

(def skill-key [[:skill-name1 :skill-point1] [:skill-name2 :skill-point2] [:skill-name3 :skill-point3] [:skill-name4 :skill-point4]])
(def lm (ref '["Bla" "Blubb"]))
(def selected {:head (atom nil) :body (atom nil) :arm (atom nil) :wst (atom nil) :leg (atom nil)})

(defn get-items [condition aaa]
  (loop [res [] v clj-mhsim.load/equip-protector]
    (if (zero? (count v))
       res
       (recur
         (if (condition (first v))
           (conj res (aaa (first v)))
            res)
         (rest v)))))

(defn get-from-name-test []
  (get-from-name "ユクモノドウギ"))

(defn get-equip [equip]
  (get-items (fn [m] (if (= equip (m :equip)) true nil)) (fn [m] m)))

(defn get-equip-name [equip]
  (get-items (fn [m] (if (= equip (m :equip)) true nil)) (fn [m] (m :name))))

(defn get-from-name [name]
  (first (get-items (fn [m] (if (= name (m :name)) true nil)) (fn [m] m))))

(def head-names (ref (get-equip-name "head")))
(def body-names (ref (get-equip-name "body")))
(def hand-names (ref (get-equip-name "arm")))
(def west-names (ref (get-equip-name "wst")))
(def leg-names (ref (get-equip-name "leg")))

(defn get-skill-def-test []
  (get-skill-def "攻撃"))

(defn get-skill-def [name]
  (loop [list clj-mhsim.load/skill-list]
    (if (= name ((first list) :kind))
      (first list)
      (if (zero? (count list))
        nil
        (recur (rest list))))))

(defn skill-check-test []
  (skill-check "攻撃" 10))

(defn skill-check [name point]
  (let [sk-def (get-skill-def name)]
  (if (<= (Integer/valueOf (sk-def :point)) (Integer/valueOf point))
    (sk-def :skill-name)
    nil)))

(defn list-update [skill-map]  
  (println
  (loop [res [] skill skill-map]
    (if (zero? (count skill))
      res
      (recur (conj res (skill-check (first (first skill)) (first (rest (first skill))))) (rest skill))))))

(defn calc-skill []
  (loop [res {} e selected]
   (if (zero? (count e))
     (list-update res)
     (recur (let [n @(first (rest (first e)))]
               (if n (add-skill res (get-from-name n)) res)) (rest e)))))

(defn skill-conj [res name point]
  (if (zero? (count name))
    res
    (conj res {name (+ (get res name 0) (Integer/valueOf point))})))

(defn add-skill [map equip]
  (loop [res map key skill-key]
    (if (zero? (count key))
      res
      (recur (skill-conj res
      (equip (first (first key))) 
(equip (first (rest (first key)))))
        (rest key)))))

(defn start []
  (frame :title "Monster Hunter Simulator"
	 :layout (GridBagLayout.)
	 :size [300 300]
	 :constrains (java.awt.GridBagConstraints.)
	 :show true
	 [:gridx 0 :gridy 0
	 _ (label "頭")
	  :gridx 1 :gridy 0
	 combo-head (combo-box [] :model (seq-ref-combobox-model head-names (selected :head)) :action ([_] (calc-skill)))
                  :gridx 0 :gridy 1
	 _ (label "胴")
	  :gridx 1 :gridy 1
	 _ (combo-box [] :model (seq-ref-combobox-model body-names (selected :body)) :action ([_] (calc-skill)))
	  :gridx 0 :gridy 2
	 _ (label "腕")
	  :gridx 1 :gridy 2
	 _ (combo-box [] :model (seq-ref-combobox-model hand-names (selected :arm)) :action ([_] (calc-skill)))
	  :gridx 0 :gridy 3
	 _ (label "腰")
	  :gridx 1 :gridy 3
	 _ (combo-box [] :model (seq-ref-combobox-model west-names (selected :wst)) :action ([_] (calc-skill)))
	  :gridx 0 :gridy 4
	 _ (label "脚")
	  :gridx 1 :gridy 4
	 _ (combo-box [] :model (seq-ref-combobox-model leg-names (selected :leg)) :action ([_] (calc-skill)))
	  :gridx 0 :gridy 5
	 _ (scroll-panel (jlist :model (seq-ref-list-model lm)) :preferred-size [150 100])]))

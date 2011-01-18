(ns clj-mhsim.controller
  (:use [clj-mhsim model view]))

(defn fnself
  "自分自身を取得する為の式を返す"
  (fn [m] m))

(defn fnequip [equip]
  "指定部位を取得する為の式を返す"
  (fnkey-val :equip equip))

(defn fnkey-val [key val]
  "マップのkeyがvalと一致する装備を取得する為の式を返す"
  (fn [m] (if (= val (m key)) true nil)))

(defn search-equip [key val]
  "keyの値がvalに一致する装備品のベクタを返す"
  (first (search (fnkey-val key val) fnself)))

(defn search
  "装備の条件(fncond)と取得する要素(fngetter)の式を渡し、一致するベクタを返す"
  [fncond fngetter]
  (loop [res [] v equip-list]
    (if (zero? (count v))
       res
       (recur
         (if (fncond (first v))
           (conj res (fngetter (first v)))
            res)
         (rest v)))))

(defn skill-def [name]
  "指定された名前のスキル定義を返す"
  (loop [list skill-list]
    (if (= name ((first list) :kind))
      (first list)
      (if (zero? (count list))
        nil
        (recur (rest list))))))

(defn skill-check [name point]
  "スキル名(スキルの種類)とポイントから発動スキル名を返す"
  (let [sk-def (skill-def name)]
    (if (<= (Integer/valueOf (sk-def :point)) (Integer/valueOf point))
      (sk-def :skill-name)
      nil)))

(defn calc-skill [equips]
  "選択した装備一覧からスキル値の合計と発動スキルのマップを返す"
  (loop [res {} e equips]
   (if (zero? (count e))
     res
     (recur (let [n @(first (rest (first e)))]
               (if n (add-skill res (search-equip :name n)) res)) (rest e)))))

(defn composite [map name point]
  "スキル名にスキルポイントを追加したマップを返す"
  (if (zero? (count name))
    map
    (conj map {name (+ (get map name 0) (Integer/valueOf point))})))

; TODO:keyのデータ構造見直し(CSVからマップへの格納方法)
(defn add-skill [map equip]
  "スキルマップと装備を渡し、スキルを計算後のマップを返す"
  (loop [res map key read-skill-key]
    (if (zero? (count key))
      res
      (recur (composite res (equip (first (first key))) (equip (first (rest (first key))))) (rest key)))))

(defn namelist [equip]
  "equip部位の名称のみのベクタを返す"
  (search (fnequip equip) (fn [m] (m :name))))

; 各部位の装備品名称ベクタ
(def head-names (ref (namelist "head")))
(def body-names  (ref (namelist "body")))
(def hand-names  (ref (namelist "hand")))
(def west-names  (ref (namelist "west")))
(def leg-names  (ref (namelist "leg")))

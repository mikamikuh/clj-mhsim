(ns clj-mhsim.model
  (:use [clojure-csv.core]))

(use '[clojure.contrib.duck-streams :only (reader)])

; スキル計算時などに読み込む為のKey
(def read-skill-key [[:skill-name1 :skill-point1] [:skill-name2 :skill-point2] [:skill-name3 :skill-point3] [:skill-name4 :skill-point4]])

; 装備品CSV読み込みKey TODO:20以上の要素は指定できない?
(def protector-key1 [:name :sex :type :rare :slot :rank :get :start-def :end-def :fire-reg :water-reg :ice-reg :thun-reg :drg-reg :skill-name1 :skill-point1 :skill-name2 :skill-point2 :skill-name3 :skill-point3])
(def protector-key2 [:skill-name4 :skill-point4 :skill-name5 :skill-point5 :item-name1 :item-num1 :item-name2 :item-num2 :item-name3 :item-num3 :item-name4 :item-num4])
(def protector-key (into protector-key1 protector-key2))

; スキル名CSV読み込みKey
(def skill-key [:skill-name :kind :point :type])

(defn into-all [& seq]
  "引数をすべてintoしたシーケンスを返す"
   (loop [res [] d seq]
     (if (zero? (count d))
       res
       (recur (into res (first d))
              (rest d)))))

(defn convert [scsv keys]
  "scsvをパースしたシーケンスとkeysのマップを返す"
  (loop [result {} key keys data (first (parse-csv scsv))]
    (if (zero? (count key))
      result
      (recur
       (conj result {(first key) (first data)})
       (rest key)
       (rest data)))))

(defn read-csv [path equip keys]
  "pathのCSVファイルを読み込みkeysと対応させ、{:equip equip}を追加したマップを返す"
  (with-open [r (reader path)]
    (loop [res [] l (. r readLine)]
      (if (= l nil)
       res
       (recur (conj res 
	(conj {:equip equip}
	   (convert l keys)))
       (. r readLine))))))

; 装備品マップ
; TODO: 武器と装飾品の追加
(def equip-list
     (into-all (read-csv "csv/MH3EQUIP_HEAD.csv" "head" protector-key)
               (read-csv "csv/MH3EQUIP_BODY.csv" "body" protector-key)
               (read-csv "csv/MH3EQUIP_ARM.csv" "arm" protector-key)
               (read-csv "csv/MH3EQUIP_LEG.csv" "leg" protector-key)
               (read-csv "csv/MH3EQUIP_WST.csv" "wst" protector-key)))

; スキルマップ
(def skill-list
     (read-csv "csv/MH3SKILL.csv" nil skill-key))

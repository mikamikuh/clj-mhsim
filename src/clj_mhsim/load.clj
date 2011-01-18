(ns clj-mhsim.load
  (:use [clojure-csv.core]))

(use '[clojure.contrib.duck-streams :only (reader)])

(defn test-test []
  (loop [res [] v (head "csv/MH3EQUIP_HEAD_.csv" "head")]
    (if (zero? (count v))
      res
      (recur
       (if
         (= ((first v) :slot) "1")
 	(conj res (first v))
 	res)
             (rest v)))))

(defn test2 []
  (head "csv/MH3EQUIP_HEAD_.csv" "head"))

(defn read-csv [path equip keys]
  (with-open [r (reader path)]
    (loop [res [] l (. r readLine)]
      (if (= l nil)
       res
       (recur (conj res 
	(conj {:equip equip}
	   (convert l keys)))
       (. r readLine))))))


(def protector-key1 [:name :sex :type :rare :slot :rank :get :start-def :end-def :fire-reg :water-reg :ice-reg :thun-reg :drg-reg :skill-name1 :skill-point1 :skill-name2 :skill-point2 :skill-name3 :skill-point3])
(def protector-key2 [:skill-name4 :skill-point4 :skill-name5 :skill-point5 :item-name1 :item-num1 :item-name2 :item-num2 :item-name3 :item-num3 :item-name4 :item-num4])
(def protector-key (into protector-key1 protector-key2))

(def skill-key [:skill-name :kind :point :type])

(defn test-convert []
  (convert "ユクモノカサ,0,0,1,0,1,1,1,21,0,0,0,3,0,加護,2,気まぐれ,2,達人,2,,,,,ユクモの木,1,丸鳥の羽,1,,,,"))

(defn convert [str keys]
  (loop [result {} key keys data (first (parse-csv str))]
    (if (zero? (count key))
      result
      (recur
       (conj result {(first key) (first data)})
       (rest key)
       (rest data)))))

(defn into-all [& data]
   (loop [res [] d data]
     (if (zero? (count d))
       res
       (recur (into res (first d))
              (rest d)))))

(def equip-protector
     (into-all (read-csv "csv/MH3EQUIP_HEAD.csv" "head" protector-key)
               (read-csv "csv/MH3EQUIP_BODY.csv" "body" protector-key)
               (read-csv "csv/MH3EQUIP_ARM.csv" "arm" protector-key)
               (read-csv "csv/MH3EQUIP_LEG.csv" "leg" protector-key)
               (read-csv "csv/MH3EQUIP_WST.csv" "wst" protector-key)))

(def skill-list
     (read-csv "csv/MH3SKILL.csv" nil skill-key))
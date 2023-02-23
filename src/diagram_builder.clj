

(ns diagram-builder
  (:require [db]
            [clojure.string :as cs]))

(def double-quotes "\"\"")

(defn column-type-to-string [{:keys [type_name column_size]}]
  (cond
    (and (= type_name "int8") (= column_size 19)) "bigint"
    (= type_name "timestamp") type_name
    :else (str type_name "(" column_size ")")))

(defn column-attribute [tmd {:keys [column_name is_nullable]}]
  (let [isPk (some #(= (:column_name %) column_name) (:primary-keys tmd))]
    (if isPk "+" (if (= is_nullable "NO") "*" " "))))

(defn is-fk-str [table-md cname]
  (let [key-names (into #{} (map :fkcolumn_name (:imported-keys table-md)))]
    (if (contains? key-names cname) "[FK]" "")))

(defn col-to-string [entity col]
  (let [cname (:column_name col)]
    (str (column-attribute entity col) double-quotes cname double-quotes ": //" (column-type-to-string col) "// " (is-fk-str entity cname))))

(defn entity-to-str [table-md]
  (let [primary-key-names (into #{} (map :column_name (:primary-keys table-md)))
        pk-cols (filter #(contains? primary-key-names (:column_name %)) (:cols table-md))
        reg-cols (filter #(not (contains? primary-key-names (:column_name %))) (:cols table-md))]
    (str "\nentity \"**" (:name table-md) "**\" {\n  "
         (cs/join "\n  " (map #(col-to-string table-md %) pk-cols))
         "\n  --\n  "
         (cs/join "\n  " (map #(col-to-string table-md %) reg-cols))
         "\n}\n")))

(defn imported-key-to-str [{:keys [fktable_name fkcolumn_name pktable_name pkcolumn_name]} all-table-names]
  (let [add-table-link (if (contains? all-table-names pktable_name) "" (str " [[/add?" pktable_name " + ]]"))]
    (str "\"**" fktable_name "**\" }-- \"**" pktable_name add-table-link "**"  "\" : " pkcolumn_name " = " fkcolumn_name)))

(defn exported-key-to-str [{:keys [fktable_name fkcolumn_name pktable_name pkcolumn_name]} all-table-names]
  (if (contains? all-table-names fktable_name)
    ""
    (let [add-table-link (if (contains? all-table-names fktable_name) "" (str " [[/add?" fktable_name " + ]]"))]
      (str "\"**" pktable_name "**\" --{ \"**" fktable_name add-table-link "**"  "\" : " fkcolumn_name " = " pkcolumn_name))))


(defn table-keys-to-str [tmd all-tmd]
  (let [all-table-names (into #{} (map :name all-tmd))]
    (str
     (cs/join "\n" (map #(exported-key-to-str % all-table-names) (:exported-keys tmd)))
     "\n"
     (cs/join "\n" (map #(imported-key-to-str % all-table-names) (:imported-keys tmd))))))

(def header "@startuml\nhide circle\nskinparam linetype ortho\n")
;;(def header "@startuml\n")
(def footer "\n@enduml\n")

(defn generate
  "Builds plantuml text digram using table metadata"
  [table-metadatas]

  (str header
       (cs/join "\n" (map entity-to-str table-metadatas))
       (cs/join "\n"  (map #(table-keys-to-str % table-metadatas) table-metadatas))
       footer))




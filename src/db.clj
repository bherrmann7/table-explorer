

(ns db
  (:require
   [clojure.java.jdbc :as j]))


(defn get-table-names [db]
  (map :table_name (j/with-db-metadata [md db]
                     (j/metadata-result (.getTables md nil nil nil (into-array ["TABLE" "VIEW"]))))))


(defn get-table-metadata [db table-name]
  (j/with-db-metadata [md db]
    {:name table-name
     :cols          (j/metadata-result (.getColumns md nil nil table-name nil))
     :primary-keys  (j/metadata-result (.getPrimaryKeys md nil nil table-name))
     :imported-keys (j/metadata-result (.getImportedKeys md nil nil table-name))
     :exported-keys (j/metadata-result (.getExportedKeys md nil nil table-name))}))


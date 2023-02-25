

(ns web-server
  (:require [ring.adapter.jetty]
            [clojure.string :as cs]
            [diagram-builder]
            [plantuml-helper]
            [db]
            [bootstrap :as bs]
            [ring.util.codec]
            [clojure.java.io]))

(defn redirect-to-root []
  {:status 302
   :headers {"Location" "/"}
   :body ""})

(def ctx (atom {:db-url nil :table-names #{}}))

(def table-form "
<div class='container'>
<form action=/connect>
  <div class='mb-3'>
    <label for='j1' class='form-label'>Jdbc Connection URL</label>
    <input type='text' class='form-control' id='j1' name=jurl>
    For example: postgresql://myuser:secret@mydb.server.com:5432/mypgdatabase
  </div>
  <div class='mb-3'>
    <label for='it' class='form-label'>Initial Table</label>
    <input type='text' class='form-control' name=init-table id='j1'>
  </div>
  <button type='submit' class='btn btn-primary'>Connect</button>
</form>
</div>
")

(defn svg-for-tables [db-url table-names]
  (try 
  (let [table-metadatas (map #(db/get-table-metadata db-url %) table-names)
        plantuml-txt (diagram-builder/generate table-metadatas)]
    (plantuml-helper/generate-svg plantuml-txt))
  (catch Throwable t (str "<div>Problem rending graph</div><p><pre>" (.getMessage t)))))

(defn handle-connect [request]
  (let [ {:strs [init-table jurl]} (ring.util.codec/form-decode (:query-string request))]
    (reset! ctx { :db-url jurl :table-names #{init-table} })
    (redirect-to-root)))

(defn handle-clear [request]
  (reset! ctx { :db-url nil :table-names #{}})
  (redirect-to-root))  

(defn handle-adding-table [request]
  (let [tname (:query-string request)]
    (reset! ctx { :db-url nil :table-names (conj (:table-names @ctx) tname) })
    (redirect-to-root)))  

(defn handle-all [request]
  (reset! ctx { :db-url (:db-url @ctx) :table-names (db/get-table-names (:db-url @ctx))})
  (redirect-to-root))

(defn handle-display-database-tables []
   (bs/wrap (str "Tables: " (cs/join " " (:table-names @ctx) )
                 " &nbsp;<form style='display: inline' action=/clear >
<button type='submit' class='btn btn-primary btn-sm' name=clear value=clear>clear</button></form>
<form style='display: inline' action=/all >
<button type='submit' class='btn btn-secondary btn-sm' name=all value=all>all</button>
</form><p><br>" (svg-for-tables (:db-url @ctx) (:table-names @ctx)))))

(defn handler [request]
  (let [uri (:uri request)]
    (cond
      (.startsWith uri "/connect") (handle-connect request)
      (nil? (:db-url @ctx)) (bs/wrap table-form)
      (.startsWith uri "/clear") (handle-clear request)
      (.startsWith uri "/all") (handle-all request)
      (.startsWith uri "/add") (handle-adding-table request)
      :else (handle-display-database-tables))))


(defn start []
  (println "Listenting for web connections at :3000")
  (ring.adapter.jetty/run-jetty #(handler %1) {:port 3000 :join? false}))


(start)



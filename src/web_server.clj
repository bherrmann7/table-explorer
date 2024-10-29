

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

(defn form [ fname example ]
  (let [ htype (if (= fname "password") "password" "input")]
  (str "<div class='row mb-3'>
    <label for='l-" fname "' class='col-form-label col-sm-2'>" fname "</label>
   <div class='col-sm-10'>
      <input type=" htype" name='" fname "' class='form-control' id='l-" fname "'>
      " example "
  </div><br>" )))

(def table-form (str "
<div class='container'>
<form action=/connect>

<div>
  
  <div class='mb-3'>
    <label for='j1' class='form-label'>Jdbc Connection URL</label>
    <input type='text' class='form-control' id='j1' name=jurl>
    For example: postgresql://myuser:secret@mydb.server.com:5432/mypgdatabase
  </div>

  <b>OR</b>
  <br>
  "
    (form "classname", "example; 'oracle.jdbc.OracleDriver'")
    (form "subprotocol", "example; 'Oracle'")
    (form "subname", "example; 'thin:@host:port/service'")
    (form "user" nil)
    (form "password" nil)
  "
  </div>

<div/>

  <div class='mb-3'>
    <label for='it' class='form-label'>Initial Table</label>
    <input type='text' class='form-control' name=init-table id='j1'>
  </div>
  <button type='submit' class='btn btn-primary'>Connect</button>
</form>
</div>
"))

(defn svg-for-tables [db-url table-names]
  (try 
  (let [table-metadatas (map #(db/get-table-metadata db-url %) (sort table-names))
        plantuml-txt (diagram-builder/generate table-metadatas)]
    (spit "/tmp/diagram.pu" plantuml-txt)
    (plantuml-helper/generate-svg plantuml-txt))
  (catch Throwable t (str "<div>Problem rending graph</div><p><pre>" (.getMessage t)))))

(defn handle-connect [request]
  (let [ {:strs [init-table jurl classname subprotocol subname user password]} (ring.util.codec/form-decode (:query-string request))
    db-conn-map (if (not-empty jurl) jurl 
                    {
                     :classname classname
                     :subprotocol subprotocol
                     :subname subname
                     :user user
                     :password password
       })]
    (reset! ctx { :db-url db-conn-map
                 :table-names #{init-table} })
    (redirect-to-root)))

(defn handle-reset [request]
  (reset! ctx { :db-url nil :table-names #{}})
  (redirect-to-root))  

(defn handle-add-table [request]
  (let [tname (:query-string request)]
    (swap! ctx #(assoc % :table-names (conj (:table-names %) tname)))
    (redirect-to-root)))

(defn handle-remove-table [request]
  (let [tname (:query-string request)]
    (swap! ctx #(assoc % :table-names (disj (:table-names %) tname)))
    (redirect-to-root)))


(defn handle-all [_]
  (swap! ctx assoc :table-names (into #{} (db/get-table-names (:db-url @ctx))))
  (redirect-to-root))

(defn handle-display-database-tables []
   (bs/wrap (str "Tables: " (cs/join " " (:table-names @ctx) )
                 " &nbsp;<div style='float: right'>

<form style='display: inline' action=/all >
<button type='submit' class='btn btn-secondary btn-sm' name=all value=all>Add All Tables</button>
</form>
&nbsp;&nbsp;
<form style='display: inline' action=/reset >
  <button type='submit' class='btn btn-secondary btn-sm' name=reset value=reset>Reset</button>
</form>

</div><p><br>" (svg-for-tables (:db-url @ctx) (:table-names @ctx)))))

(defn handler [request]
  (let [uri (:uri request)]
    (cond
      (.startsWith uri "/connect") (handle-connect request)
      (nil? (:db-url @ctx)) (bs/wrap table-form)
      (.startsWith uri "/reset") (handle-reset request)
      (.startsWith uri "/all") (handle-all request)
      (.startsWith uri "/add") (handle-add-table request)
      (.startsWith uri "/remove") (handle-remove-table request)
      :else (handle-display-database-tables))))


(defn start []
  (println "Listenting for web connections at :3000")
  (ring.adapter.jetty/run-jetty #(handler %1) {:port 3000 :join? false}))


(start)



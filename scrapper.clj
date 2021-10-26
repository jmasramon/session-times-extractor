(require
 '[clojure.java.io :as io]
 '[clojure.string :as str]
 '[clj-time.coerce :as c]
 '[clj-time.format :as f])


(defn readFile [file]
  (with-open [rdr (io/reader file)]
    (doseq [line (line-seq rdr)]
      (let [splitted (str/split (str line) #": ")
            key (first splitted)
            time (second splitted)]
        (when-not (nil? time)
          (let [p-time (read-string time)]
            (when (number? p-time)
              (let [value (f/unparse
                           (f/formatters :date-hour-minute-second-ms)
                           (c/from-long p-time))]
                (println
                 key
                 value)))))))))

(defn returnfilelist
  "returns all the files changed today in a directory"
  [d]
  (let [dir (io/file d)
        files (for [file (.listFiles dir)
                    :when (and (not (.isDirectory file))
                               (str/includes? (str file) ".json"))]
                (str file))]
    (doseq [f files] (println f)) ;; leaving this in for debugging
    files))

(seq (.list (clojure.java.io/file ".")))

(defn -main [& args]
  (println "files found:")
  (returnfilelist ".")
  (println "first session:")
  (readFile "only-times.json")
  (println "second session:")
  (readFile "only-times-3.json"))


(ns  scrapper
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clj-time.coerce :as c]
   [clj-time.format :as f]
   [clojure.java.shell :refer [sh]]))

(defn convertFile [file]
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
                 value)
                (spit (str "./human-" (subs file 2)) (str key " " value "\n") :append true)))))))))

(defn return-only-times-file-list
  "returns all the files changed today in a directory"
  [d]
  (let [dir (io/file d)
        files (for [file (.listFiles dir)
                    :when (and (not (.isDirectory file))
                               (str/includes? (str file) "times-only")
                               (str/includes? (str file) ".json"))]
                (str file))]
    (doseq [f files] (println f)) ;; leaving this in for debugging
    files))

(defn return-only-session-file-list
  "returns all the files changed today in a directory"
  [d]
  (let [dir (io/file d)
        files (for [file (.listFiles dir)
                    :when (and (not (.isDirectory file))
                               (str/includes? (str file) "session")
                               (str/includes? (str file) ".json"))]
                (str file))]
    (doseq [f files] (println f)) ;; leaving this in for debugging
    files))
;; easier version to find all files 
;;(filter #(str/includes? % ".json") (seq (.list (clojure.java.io/file "."))))

(defn convert-all-files
  [d]
  (let [files (return-only-times-file-list d)]
    (doseq [file files]
      (println "converting file: " file)
      (convertFile file))))

(defn output-file
  [file]
  (str "./times-only" (subs file 9)))

(defn json-to-flatten-times
  [d]
  (let [files (return-only-session-file-list d)]
    (doseq [file files]
      (let [new-file (output-file file)]
        (println "converting file: " file "to: " new-file)
        (sh "./time-extractor.sh" (str file) new-file)))))

(defn -main [& args]
  (json-to-flatten-times ".")
  (convert-all-files "."))


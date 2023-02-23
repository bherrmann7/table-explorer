

(ns plantuml-helper)

(import net.sourceforge.plantuml.SourceStringReader)
(import net.sourceforge.plantuml.FileFormatOption)
(import net.sourceforge.plantuml.FileFormat)

(defn write-image [plantuml-txt output-filename]
  (let [plantuml-reader (SourceStringReader. plantuml-txt)
        os (java.io.ByteArrayOutputStream.)
        _ (.generateImage plantuml-reader os (FileFormatOption. FileFormat/SVG))
        _ (.close os)
        svg (String. (.toByteArray os) (java.nio.charset.Charset/forName "UTF-8"))]
    (spit output-filename svg)))

(defn generate-svg [plantuml-txt]
  (let [plantuml-reader (SourceStringReader. plantuml-txt)
        os (java.io.ByteArrayOutputStream.)
        _ (.generateImage plantuml-reader os (FileFormatOption. FileFormat/SVG))
        _ (.close os)]
    (String. (.toByteArray os) (java.nio.charset.Charset/forName "UTF-8"))))


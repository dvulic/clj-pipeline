(ns clj-pipeline.transformer
  (:import (java.awt Point Rectangle)
           (java.awt.geom AffineTransform)
           (java.awt.image BufferedImage RenderedImage)
           (java.io File)
           (javax.imageio ImageIO)))

(defn construct-rect [p1 p2]
  (let [x-min (min (.getX p1) (.getX p2)) x-max (max (.getX p1) (.getX p2)) y-min (min (.getY p1) (.getY p2)) y-max (max (.getY p1) (.getY p2))] (Rectangle. x-min y-min (abs (- x-min x-max)) (abs (- y-min y-max))))
)

(defn scale [img-rect start-point end-point]
  (let [bound-rect (construct-rect start-point end-point) w-br (double (.-width bound-rect)) h-br (double (.-height bound-rect)) w-img-r (.-width img-rect) h-img-r (.-height img-rect)] scale (min (/ w-br w-img-r) (/ h-br h-img-r)))
)

(defn transform-expression [img origin end shear-x shear-y]
  (let [width (.getWidth img) height (.getHeight img) scale (scale (Rectangle. 0 0 width height) origin end) new-w (max 1 (int (Math/ceil (* scale (+ width (abs (* height shear-x))))))) new-h (max 1 (int (Math/ceil (* scale (+ height (abs (* width shear-y))))))) offset-x (max 0 (- (* scale shear-x height))) offset-y (max 0 (- (* scale shear-y width))) dst (BufferedImage. new-w new-h BufferedImage/TYPE_INT_ARGB) g   (.createGraphics dst) tx  (AffineTransform.)]
  (.scale tx scale scale) (.shear tx shear-x shear-y) (.translate g offset-x offset-y) (.drawImage g img tx nil) (.dispose g) dst)
)

(defn apply-expression [pose-img expr-img bounds]
  (let [pose-w (.getWidth pose-img) pose-h (.getHeight pose-img) expr-w (.getWidth expr-img) expr-h (.getHeight expr-img) center-x (.getCenterX bounds) center-y (.getCenterY bounds) draw-x (int (Math/round (- center-x (/ expr-w 2.0)))) draw-y (int (Math/round (- center-y (/ expr-h 2.0)))) out (BufferedImage. pose-w pose-h BufferedImage/TYPE_INT_ARGB) g (.createGraphics out)]
    (.drawImage g pose-img 0 0 nil) (.drawImage g expr-img draw-x draw-y nil) (.dispose g) out)
)

(defn save-image [img output-path]
  (^[RenderedImage String File] ImageIO/write img "png" (^[String] File/new output-path)))

;(Point. 336 290) (Point. 662 618)
(defn apply-transformations [pose-path expression-path shear-x shear-y]
  (let [pose (ImageIO/read (File. pose-path)) expression (ImageIO/read (File. expression-path)) opt-expr (transform-expression expression (Point. 336 290) (Point. 662 618) shear-x shear-y) output (apply-expression pose opt-expr (construct-rect (Point. 336 290) (Point. 662 618)))]
    (save-image output "combined.png")
  )
)
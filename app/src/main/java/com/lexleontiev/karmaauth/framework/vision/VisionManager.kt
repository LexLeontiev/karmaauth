package com.lexleontiev.karmaauth.framework.vision

import android.graphics.*
import com.orhanobut.logger.Logger
import org.opencv.android.Utils
import org.opencv.android.Utils.bitmapToMat
import org.opencv.core.*
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc
import java.lang.Double
import java.util.*

const val MAX_HEIGHT = 500

class VisionManager {

    private var mImagesHolder: ImagesHolder? = null

    fun setImagesHolder(holder: ImagesHolder) {
        mImagesHolder = holder
    }

    fun getImagesHolder(): ImagesHolder? {
        return mImagesHolder
    }

    /**
     * Attempt to find the four corner points for the largest contour in the image.
     *
     * @return A list of points, or null if a valid rectangle cannot be found.
     */
    fun findPoints(bitmap: Bitmap): List<PointF>? {
        var result: MutableList<PointF>? = null

        val image = Mat()
        val orig = Mat()
        Utils.bitmapToMat(getResizedBitmap(bitmap), image)
        Utils.bitmapToMat(bitmap, orig)

        val edges = edgeDetection(orig)
        val largest = findLargestContour(edges)

        if (largest != null) {
            val points = sortPoints(largest.toArray())
            result = ArrayList()
            result.add(PointF(Double.valueOf(points[0].x).toFloat(), Double.valueOf(points[0].y).toFloat()))
            result.add(PointF(Double.valueOf(points[1].x).toFloat(), Double.valueOf(points[1].y).toFloat()))
            result.add(PointF(Double.valueOf(points[2].x).toFloat(), Double.valueOf(points[2].y).toFloat()))
            result.add(PointF(Double.valueOf(points[3].x).toFloat(), Double.valueOf(points[3].y).toFloat()))
            largest.release()
        } else {
            Logger.d("Can't find rectangle!")
        }

        edges.release()
        image.release()
        orig.release()

        return result
    }

    /**
     * Resize a given bitmap to scale using the given height
     *
     * @return The resized bitmap
     */
    fun getResizedBitmap(bitmap: Bitmap): Bitmap {
        val ratio = bitmap.height / MAX_HEIGHT.toDouble()
        val width = (bitmap.width / ratio).toInt()
        return Bitmap.createScaledBitmap(bitmap, width, MAX_HEIGHT, false)
    }

    /**
     * Find the largest 4 point contour in the given Mat.
     *
     * @param src A valid Mat
     * @return The largest contour as a Mat
     */
    private fun findLargestContour(src: Mat): MatOfPoint2f? {
        val contours = ArrayList<MatOfPoint>()
        Imgproc.findContours(src, contours, Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE)

        // Get the 5 largest contours
        Collections.sort(contours) { o1, o2 ->
            val area1 = Imgproc.contourArea(o1)
            val area2 = Imgproc.contourArea(o2)
            (area2 - area1).toInt()
        }
        if (contours.size > 5) contours.subList(4, contours.size - 1).clear()

        var largest: MatOfPoint2f? = null
        for (contour in contours) {
            val approx = MatOfPoint2f()
            val c = MatOfPoint2f()
            contour.convertTo(c, CvType.CV_32FC2)
            Imgproc.approxPolyDP(c, approx, Imgproc.arcLength(c, true) * 0.02, true)

            if (approx.total() == 4L && Imgproc.contourArea(contour) > 150) {
                // the contour has 4 points, it's valid
                largest = approx
                break
            }
        }

        return largest
    }

    /**
     * Sort the points
     *
     * The order of the points after sorting:
     * 0------->1
     * ^        |
     * |        v
     * 3<-------2
     *
     * NOTE:
     * Based off of http://www.pyimagesearch.com/2014/08/25/4-point-opencv-getperspective-transform-example/
     *
     * @param src The points to sort
     * @return An array of sorted points
     */
    private fun sortPoints(src: Array<Point>): Array<Point> {
        val srcPoints = ArrayList(Arrays.asList(*src))
        val result = arrayOf(Point(), Point(), Point(), Point())

        val sumComparator = Comparator<Point> { lhs, rhs -> java.lang.Double.valueOf(lhs.y + lhs.x).compareTo(rhs.y + rhs.x) }
        val differenceComparator = Comparator<Point> { lhs, rhs -> java.lang.Double.valueOf(lhs.y - lhs.x).compareTo(rhs.y - rhs.x) }

        result[0] = Collections.min(srcPoints, sumComparator)        // Upper left has the minimal sum
        result[2] = Collections.max(srcPoints, sumComparator)        // Lower right has the maximal sum
        result[1] = Collections.min(srcPoints, differenceComparator) // Upper right has the minimal difference
        result[3] = Collections.max(srcPoints, differenceComparator) // Lower left has the maximal difference

        return result
    }

    /**
     * Detect the edges in the given Mat
     * @param src A valid Mat object
     * @return A Mat processed to find edges
     */
    private fun edgeDetection(src: Mat): Mat {
        val edges = Mat()
        Imgproc.cvtColor(src, edges, Imgproc.COLOR_BGR2GRAY)
        Imgproc.GaussianBlur(edges, edges, Size(5.0, 5.0), 0.0)
        Imgproc.Canny(edges, edges, 75.0, 200.0)
        return edges
    }

    /**
     * Transform the coordinates on the given Mat to correct the perspective.
     *
     * @param src A valid Mat
     * @param points A list of coordinates from the given Mat to adjust the perspective
     * @return A perspective transformed Mat
     */
    private fun perspectiveTransform(src: Mat, points: List<PointF>): Mat {
        val point1 = Point(points[0].x.toDouble(), points[0].y.toDouble())
        val point2 = Point(points[1].x.toDouble(), points[1].y.toDouble())
        val point3 = Point(points[2].x.toDouble(), points[2].y.toDouble())
        val point4 = Point(points[3].x.toDouble(), points[3].y.toDouble())
        val pts = arrayOf(point1, point2, point3, point4)
        return fourPointTransform(src, sortPoints(pts))
    }

    /**
     * NOTE:
     * Based off of http://www.pyimagesearch.com/2014/08/25/4-point-opencv-getperspective-transform-example/
     *
     * @param src
     * @param pts
     * @return
     */
    private fun fourPointTransform(src: Mat, pts: Array<Point>): Mat {
        val ratio = src.height() / MAX_HEIGHT.toDouble()

        val ul = pts[0]
        val ur = pts[1]
        val lr = pts[2]
        val ll = pts[3]

        val widthA = Math.sqrt(Math.pow(lr.x - ll.x, 2.0) + Math.pow(lr.y - ll.y, 2.0))
        val widthB = Math.sqrt(Math.pow(ur.x - ul.x, 2.0) + Math.pow(ur.y - ul.y, 2.0))
        val maxWidth = Math.max(widthA, widthB) * ratio

        val heightA = Math.sqrt(Math.pow(ur.x - lr.x, 2.0) + Math.pow(ur.y - lr.y, 2.0))
        val heightB = Math.sqrt(Math.pow(ul.x - ll.x, 2.0) + Math.pow(ul.y - ll.y, 2.0))
        val maxHeight = Math.max(heightA, heightB) * ratio

        val resultMat = Mat(java.lang.Double.valueOf(maxHeight).toInt(), java.lang.Double.valueOf(maxWidth).toInt(), CvType.CV_8UC4)

        val srcMat = Mat(4, 1, CvType.CV_32FC2)
        val dstMat = Mat(4, 1, CvType.CV_32FC2)
        srcMat.put(0, 0, ul.x * ratio, ul.y * ratio, ur.x * ratio, ur.y * ratio, lr.x * ratio, lr.y * ratio, ll.x * ratio, ll.y * ratio)
        dstMat.put(0, 0, 0.0, 0.0, maxWidth, 0.0, maxWidth, maxHeight, 0.0, maxHeight)

        val m = Imgproc.getPerspectiveTransform(srcMat, dstMat)
        Imgproc.warpPerspective(src, resultMat, m, resultMat.size())

        srcMat.release()
        dstMat.release()
        m.release()

        return resultMat
    }

    /**
     * Apply a threshold to give the "scanned" look
     *
     * NOTE:
     * See the following link for more info http://docs.opencv.org/3.1.0/d7/d4d/tutorial_py_thresholding.html#gsc.tab=0
     * @param src A valid Mat
     * @return The processed Bitmap
     */
    private fun applyThreshold(src: Mat): Bitmap {
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY)

        // Some other approaches
        Imgproc.adaptiveThreshold(src, src, 255.toDouble(), Imgproc.ADAPTIVE_THRESH_MEAN_C,
                Imgproc.THRESH_BINARY, 15, 6.toDouble())
//                Imgproc.threshold(src, src, 0.toDouble(), 255.toDouble(),
//                        Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)

//        Imgproc.GaussianBlur(src, src, Size(5.0, 5.0), 0.0)
//        Imgproc.adaptiveThreshold(src, src, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
//                Imgproc.THRESH_BINARY, 11, 2.0)

        val bm = Bitmap.createBitmap(src.width(), src.height(), Bitmap.Config.ARGB_8888)
        org.opencv.android.Utils.matToBitmap(src, bm)

        return bm
    }

    fun cropImage(image: Bitmap, points: List<PointF>): Bitmap {
        val cropImage = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(cropImage)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = -0x1000000

        val path = Path()
        path.reset()
        path.moveTo(points[0].x, points[0].y)
        path.lineTo(points[1].x, points[1].y)
        path.lineTo(points[2].x, points[2].y)
        path.lineTo(points[3].x, points[3].y)
        path.close()

        canvas.drawPath(path, paint)

        // Keeps the source pixels that cover the destination pixels,
        // discards the remaining source and destination pixels.
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(image, 0F, 0F, paint)

        // находим максимально полезную область изображения
        var minX = points[0].x
        var maxX = points[0].x
        var minY = points[0].y
        var maxY = points[0].y
        for (point in points) {
            if (point.x < minX) minX = point.x
            if (point.x > maxX) maxX = point.x
            if (point.y < minY) minY = point.y
            if (point.y > maxY) maxY = point.y
        }
        val startX = minX.toInt()
        val startY = minY.toInt()
        val width = maxX.toInt() - startX
        val height = maxY.toInt() - startY
        val newImage = Bitmap.createBitmap(cropImage, startX, startY, width, height)
        return newImage
    }

    fun completeImage(image: Bitmap, points: List<PointF>): Bitmap {
        val orig = Mat()
        bitmapToMat(image, orig)

        val transformed = perspectiveTransform(orig, points)
        val result = applyThreshold(transformed)

        orig.release()
        transformed.release()

        return result
    }
}
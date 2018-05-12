package com.lexleontiev.karmaauth.ui.main.step.correctdoc

import android.graphics.*
import com.lexleontiev.karmaauth.ui.main.MainContract
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode


class CorrectDocPresenter(private val mWorkflow: MainContract.Workflow)
    : CorrectDocContract.Presenter {

    private var mView: CorrectDocContract.View? = null

    override fun attachView(view: CorrectDocContract.View) {
        mView = view
        mView!!.setPresener(this)
    }

    override fun detachView() {
        mView = null
    }

    override fun start() {
        val image = mWorkflow.getSelectedImage()
        if (image != null) {
            mView?.setImage(image)
        }
    }

    override fun finishCrop(points: List<PointF>) {
        val image = mWorkflow.getSelectedImage()
        if (image != null) {
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

            mWorkflow.fileProcessed(newImage)

//            val paint = Paint()
//            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
//            val canvas = Canvas(cropImage)
//            canvas.drawBitmap(image, 0F, 0F, null)
//            val path = Path()
//            path.reset()
//            path.moveTo(points[0].x, points[0].y)
//            path.lineTo(points[1].x, points[1].y)
//            path.lineTo(points[2].x, points[2].y)
//            path.lineTo(points[3].x, points[3].y)
//            path.close()
////            canvas.drawPath(path, paint)
//            canvas.clipPath(path)
//            mView?.setImage(cropImage)

//            var minX = points[0].x
//            var maxX = points[0].x
//            var minY = points[0].y
//            var maxY = points[0].y
//            for (point in points) {
//                if (point.x < minX) minX = point.x
//                if (point.x > maxX) maxX = point.x
//                if (point.y < minY) minY = point.y
//                if (point.y > maxY) maxY = point.y
//            }
//            val startX = minX.toInt()
//            val startY = minY.toInt()
//            val width = maxX.toInt() - startX
//            val height = maxY.toInt() - startY
//            val newImage = Bitmap.createBitmap(image, startX, startY, width, height)
//            mView?.setImage(newImage)
        }
    }
}
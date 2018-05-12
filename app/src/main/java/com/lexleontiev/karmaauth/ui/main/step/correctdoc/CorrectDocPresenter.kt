package com.lexleontiev.karmaauth.ui.main.step.correctdoc

import android.graphics.*
import com.lexleontiev.karmaauth.ui.main.MainContract


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
        val image = mWorkflow.getVisionManager().getImagesHolder()?.mOriginImage
        if (image != null) {
            val resizedImage = mWorkflow.getVisionManager().getResizedBitmap(image)
            mView?.setImage(resizedImage, mWorkflow.getVisionManager().findPoints(resizedImage))
        }
    }

    override fun finishCrop(points: List<PointF>) {
        val image = mWorkflow.getVisionManager().getImagesHolder()?.mOriginImage
        if (image != null) {
            mWorkflow.fileProcessed(mWorkflow.getVisionManager().completeImage(image, points))
        }
    }
}
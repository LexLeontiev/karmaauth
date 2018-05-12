package com.lexleontiev.karmaauth.ui.main.step.correctdoc

import android.graphics.Bitmap
import android.graphics.PointF
import com.lexleontiev.karmaauth.ui.BasePresenter
import com.lexleontiev.karmaauth.ui.BaseView
import com.stepstone.stepper.Step


interface CorrectDocContract {

    interface View : BaseView<Presenter>, Step {

        fun setImage(image: Bitmap)
    }

    interface Presenter : BasePresenter {

        fun attachView(view: View)

        fun detachView()

        fun finishCrop(points: List<PointF>)
    }
}
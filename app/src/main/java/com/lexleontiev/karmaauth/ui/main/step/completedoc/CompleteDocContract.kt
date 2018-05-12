package com.lexleontiev.karmaauth.ui.main.step.completedoc

import android.graphics.Bitmap
import com.lexleontiev.karmaauth.ui.BasePresenter
import com.lexleontiev.karmaauth.ui.BaseView
import com.stepstone.stepper.Step


class CompleteDocContract {

    interface View : BaseView<Presenter>, Step {

        fun setImage(image: Bitmap)
    }

    interface Presenter : BasePresenter {

        fun attachView(view: View)

        fun detachView()

        fun complete()
    }
}
package com.lexleontiev.karmaauth.ui.main

import android.graphics.Bitmap
import com.lexleontiev.karmaauth.data.value.Document
import com.lexleontiev.karmaauth.framework.vision.VisionManager
import com.lexleontiev.karmaauth.ui.BasePresenter
import com.lexleontiev.karmaauth.ui.BaseView
import com.lexleontiev.karmaauth.ui.main.step.completedoc.CompleteDocContract
import com.lexleontiev.karmaauth.ui.main.step.correctdoc.CorrectDocContract
import com.lexleontiev.karmaauth.ui.main.step.selectdoc.SelectDocContract
import com.lexleontiev.karmaauth.ui.main.stepper.StepperContract


interface MainContract {

    interface View : BaseView<Presenter>, StepperContract.Listener {

        fun showProgress()

        fun hideProgress()

        fun showErrorSnackBar(action: () -> Unit)

        fun openEditDocumentView(document: Document)

        fun showToast(text: String)

        fun fileSelected(selected: Boolean)

        fun goToPreviousStep(position: Int)

        fun goToNextStep(position: Int)
    }

    interface Presenter : BasePresenter {

        fun sendFileForRecognition(image: Bitmap)

        fun getSelectDocPresenter(): SelectDocContract.Presenter

        fun getCorrectDocPresenter(): CorrectDocContract.Presenter

        fun getCompleteDocPresenter(): CompleteDocContract.Presenter
    }

    interface WorkflowController {

        fun goBack(position: Int)

        fun goNext(position: Int)

        fun fileSelected(image: Bitmap)

        fun fileProcessed(image: Bitmap)

        fun fileComplete(image: Bitmap)

        fun getVisionManager() : VisionManager
    }
}
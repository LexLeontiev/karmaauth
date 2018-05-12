package com.lexleontiev.karmaauth.ui.main

import android.graphics.Bitmap
import com.lexleontiev.karmaauth.data.value.Document
import com.lexleontiev.karmaauth.data.value.DocumentType
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
    }

    interface Presenter : BasePresenter {

        fun sendFileForRecognition(image: Bitmap)

        fun getSelectDocPresenter(): SelectDocContract.Presenter

        fun getCorrectDocPresenter(): CorrectDocContract.Presenter

        fun getCompleteDocPresenter(): CompleteDocContract.Presenter
    }

    interface Workflow {

        fun fileSelected(image: Bitmap)

        fun fileProcessed(image: Bitmap)

        fun fileComplete()

        fun getSelectedImage() : Bitmap?

        fun getResultImage() : Bitmap?
    }
}
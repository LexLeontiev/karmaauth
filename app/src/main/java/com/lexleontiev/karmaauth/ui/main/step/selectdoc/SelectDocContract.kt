package com.lexleontiev.karmaauth.ui.main.step.selectdoc

import android.graphics.Bitmap
import com.lexleontiev.karmaauth.data.value.DocumentType
import com.lexleontiev.karmaauth.ui.BasePresenter
import com.lexleontiev.karmaauth.ui.BaseView
import com.stepstone.stepper.Step


interface SelectDocContract {

    interface View : BaseView<Presenter>, Step {

        fun setUpDocumentDropdown(documentTypeList: List<DocumentType>)

        fun showSelectDocumentSourceDialog()

        fun fileSelected(selected: Boolean)
    }

    interface Presenter : BasePresenter {

        fun attachView(view: View)

        fun detachView()

        fun addDocument(documentType: DocumentType)

        fun fileSelected(image: Bitmap)

//        fun sendFileForRecognition(image: Bitmap)
    }
}
package com.lexleontiev.karmaauth.ui.adddocument

import android.graphics.Bitmap
import com.lexleontiev.karmaauth.data.value.Document
import com.lexleontiev.karmaauth.data.value.DocumentType
import com.lexleontiev.karmaauth.ui.BasePresenter
import com.lexleontiev.karmaauth.ui.BaseView


interface AddDocumentContract {

    interface View : BaseView<Presenter> {

        fun setUpDocumentDropdown(documentTypeList: List<DocumentType>)

        fun showSelectDocumentSourceDialog()

        fun showProgress()

        fun hideProgress()

        fun showErrorSnackBar(action: () -> Unit)

        fun openEditDocumentView(document: Document)

        fun showToast(text: String)

        fun showTestImage(image: Bitmap)
    }

    interface Presenter: BasePresenter {

        fun addDocument(documentType: DocumentType)

        fun fileSelected(image: Bitmap)

        fun sendFileForRecognition(image: Bitmap)
    }

}
package com.lexleontiev.karmaauth.ui.main.step.selectdoc

import android.graphics.Bitmap
import com.lexleontiev.karmaauth.data.value.DocumentType
import com.lexleontiev.karmaauth.ui.main.MainContract


class SelectDocPresenter(private val mWorkflow: MainContract.WorkflowController)
    : SelectDocContract.Presenter {

    private var mView: SelectDocContract.View? = null
    private val mDocumentTypes = ArrayList<DocumentType>()

    init {
        mDocumentTypes.add(DocumentType(1,"Паспорт"))
        mDocumentTypes.add(DocumentType(2,"Водительское удостоверение"))
    }

    override fun attachView(view: SelectDocContract.View) {
        mView = view
        mView!!.setPresener(this)
    }

    override fun detachView() {
        mView = null
    }

    override fun start() {
        mView?.setUpDocumentDropdown(mDocumentTypes)
    }

    override fun addDocument(documentType: DocumentType) {
        mView?.showSelectDocumentSourceDialog()
    }

    override fun fileSelected(image: Bitmap) {
        mWorkflow.fileSelected(image)
        mView?.fileSelected(true)
    }
}
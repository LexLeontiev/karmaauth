package com.lexleontiev.karmaauth.ui.editdocument

import com.lexleontiev.karmaauth.data.value.Document


class EditDocumentPresenter(private val mView: EditDocumentContract.View,
                            private val mDocument: Document)
    :  EditDocumentContract.Presenter {

    init {
        mView.setPresener(this)
    }

    override fun saveDocument() {
    }

    override fun start() {
        mView.setFirstName(mDocument.firstName)
        mView.setMiddleName(mDocument.middleName)
        mView.setLastName(mDocument.lastName)
        mView.setExtraditionAgency(mDocument.extraditionAgency)
        mView.setExtraditionDate(mDocument.extraditionDate)
        mView.setUnitCode(mDocument.unitCode)
    }


}
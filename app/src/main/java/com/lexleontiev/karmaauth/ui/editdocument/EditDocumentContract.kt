package com.lexleontiev.karmaauth.ui.editdocument

import com.lexleontiev.karmaauth.ui.BasePresenter
import com.lexleontiev.karmaauth.ui.BaseView


interface EditDocumentContract {

    interface View: BaseView<Presenter> {

        fun setFirstName(firstName: String?)

        fun setLastName(lastName: String?)

        fun setMiddleName(middleName: String?)

        fun setExtraditionAgency(extraditionAgency: String?)

        fun setExtraditionDate(extraditionDate: String?)

        fun setUnitCode(unitCode: String?)
    }

    interface Presenter : BasePresenter {

        fun saveDocument()
    }
}
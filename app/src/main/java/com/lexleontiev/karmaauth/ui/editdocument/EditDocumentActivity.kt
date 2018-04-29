package com.lexleontiev.karmaauth.ui.editdocument

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.lexleontiev.karmaauth.R
import com.lexleontiev.karmaauth.data.value.Document


class EditDocumentActivity: AppCompatActivity(), EditDocumentContract.View  {

    lateinit var mRootL: ViewGroup
    lateinit var mFirstNameV: EditText
    lateinit var mLastNameV: EditText
    lateinit var mMiddleNameV: EditText
    lateinit var mExtraditionAgencyV: EditText
    lateinit var mExtraditionDateV: EditText
    lateinit var mUnitCode: EditText
    lateinit var mSaveB: Button

    private var mPresenter: EditDocumentContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_document)
        mRootL = findViewById(R.id.root_layout)
        mFirstNameV = findViewById(R.id.document_first_name)
        mLastNameV = findViewById(R.id.document_last_name)
        mMiddleNameV = findViewById(R.id.document_middle_name)
        mExtraditionAgencyV = findViewById(R.id.document_extradition_agency)
        mExtraditionDateV = findViewById(R.id.document_extradition_date)
        mUnitCode = findViewById(R.id.document_unit_code)
        mSaveB = findViewById(R.id.document_save)
        mSaveB.setOnClickListener({
            finish()
        })

        title = "Распознанные данные"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val document = intent.getParcelableExtra<Document>("document")
        if (document != null) {
            EditDocumentPresenter(this, document)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return false
    }

    override fun setPresener(presenter: EditDocumentContract.Presenter) {
        mPresenter = presenter
        presenter.start()
    }

    override fun setFirstName(firstName: String?) {
        mFirstNameV.setText(firstName)
    }

    override fun setLastName(lastName: String?) {
        mLastNameV.setText(lastName)
    }

    override fun setMiddleName(middleName: String?) {
        mMiddleNameV.setText(middleName)
    }

    override fun setExtraditionAgency(extraditionAgency: String?) {
        mExtraditionAgencyV.setText(extraditionAgency)
    }

    override fun setExtraditionDate(extraditionDate: String?) {
        mExtraditionDateV.setText(extraditionDate)
    }

    override fun setUnitCode(unitCode: String?) {
        mUnitCode.setText(unitCode)
    }
}
package com.lexleontiev.karmaauth.ui.main

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.lexleontiev.karmaauth.R
import com.lexleontiev.karmaauth.data.value.Document
import com.lexleontiev.karmaauth.ui.editdocument.EditDocumentActivity
import com.lexleontiev.karmaauth.ui.main.step.completedoc.CompleteDocContract
import com.lexleontiev.karmaauth.ui.main.step.completedoc.CompleteDocFragment
import com.lexleontiev.karmaauth.ui.main.step.correctdoc.CorrectDocContract
import com.lexleontiev.karmaauth.ui.main.step.correctdoc.CorrectDocFragment
import com.lexleontiev.karmaauth.ui.main.step.selectdoc.SelectDocContract
import com.lexleontiev.karmaauth.ui.main.step.selectdoc.SelectDocFragment
import com.orhanobut.logger.Logger
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import org.opencv.android.OpenCVLoader


class MainActivity : AppCompatActivity(), MainContract.View, StepperLayout.StepperListener {

    lateinit var mStepperL: StepperLayout
    lateinit var mPresenter: MainContract.Presenter
    lateinit var mProgress: ProgressDialog

    init {
        if (!OpenCVLoader.initDebug()) Logger.d("Unable to load OpenCV")
        else Logger.d("OpenCV loaded")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mStepperL = findViewById(R.id.activity_main__stepper)
        mStepperL.setListener(this)
        mStepperL.adapter = StepperAdapter(supportFragmentManager, this, this)
        mStepperL.isTabNavigationEnabled = false
        fileSelected(false)
        mProgress = ProgressDialog(this, R.style.AppTheme)
        mProgress.setTitle("Подождите...")
        mProgress.setCancelable(false)
        mPresenter = MainPresenter(this)
    }

    override fun showProgress() {
        mProgress.show()
    }

    override fun hideProgress() {
        mProgress.hide()
    }

    override fun showErrorSnackBar(action: () -> Unit) {
        Snackbar.make(mStepperL, R.string.document_upload_error, Snackbar.LENGTH_LONG)
                .setAction(R.string.repeat) { action.invoke() }
                .show()
    }

    override fun openEditDocumentView(document: Document) {
        val intent = Intent(this, EditDocumentActivity::class.java)
        intent.putExtra("document", document)
        startActivity(intent)
    }

    override fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }


    override fun setPresener(presenter: MainContract.Presenter) {
        mPresenter = presenter
        mPresenter.start()
    }

    override fun onStepSelected(newStepPosition: Int) {
        showToast("onStepSelected! -> " + newStepPosition)
    }

    override fun onError(verificationError: VerificationError?) {
        showToast("onError! -> " + verificationError?.getErrorMessage())
    }

    override fun onReturn() {
        finish()
    }

    override fun onCompleted(completeButton: View?) {
        showToast("onCompleted!")
    }

    override fun createSelectStep(): SelectDocContract.View {
        val fragment =  SelectDocFragment.newInstance()
        mPresenter.getSelectDocPresenter().attachView(fragment)
        return fragment
    }

    override fun createCorrectStep(): CorrectDocContract.View {
        val fragment =  CorrectDocFragment.newInstance()
        mPresenter.getCorrectDocPresenter().attachView(fragment)
        return fragment
    }

    override fun createCompleteStep(): CompleteDocContract.View {
        val fragment =  CompleteDocFragment.newInstance()
        mPresenter.getCompleteDocPresenter().attachView(fragment)
        return fragment
    }

    override fun fileSelected(selected: Boolean) {
        mStepperL.setNextButtonEnabled(selected)
        mStepperL.setNextButtonColor(resources.getColor(
                if (selected) R.color.ms_bottomNavigationButtonTextColor
                else R.color.colorNavButtonInactive))
    }
}
package com.lexleontiev.karmaauth.ui.main.step.completedoc

import com.lexleontiev.karmaauth.ui.main.MainContract

class CompleteDocPresenter(private val mWorkflow: MainContract.Workflow)
    : CompleteDocContract.Presenter {

    private var mView: CompleteDocContract.View? = null

    override fun attachView(view: CompleteDocContract.View) {
        mView = view
        mView!!.setPresener(this)
    }

    override fun detachView() {
        mView = null
    }

    override fun start() {
        val image = mWorkflow.getResultImage()
        if (image != null) {
            mView?.setImage(image)
        }
    }

    override fun complete() {
        mWorkflow.fileComplete()
    }
}
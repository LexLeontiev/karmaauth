package com.lexleontiev.karmaauth.ui.main.step.correctdoc

import android.graphics.Bitmap
import android.graphics.PointF
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lexleontiev.karmaauth.R
import com.lexleontiev.karmaauth.ui.view.DocumentCropImageView
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError


class CorrectDocFragment: Fragment(), Step, CorrectDocContract.View  {

    private var mContentView: View? = null
    lateinit var mRootL: ViewGroup
    lateinit var mDocumentImageV: DocumentCropImageView

    private var mLayoutId: Int = 0
    private var mPresenter: CorrectDocContract.Presenter? = null


    companion object {
        fun newInstance(): CorrectDocFragment {
            val fragment = CorrectDocFragment()
            val bundle = Bundle()
            bundle.putInt("layout_id", R.layout.fragment_correct_doc)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null && arguments.containsKey("layout_id")) {
            mLayoutId = arguments.getInt("layout_id")
        } else {
            throw IllegalArgumentException("Must be created through newInstance(...)")
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (mContentView == null) {
            mContentView = inflater?.inflate(mLayoutId, container, false)
            bindData()
        }
        return mContentView
    }

    private fun bindData() {
        mRootL = mContentView!!.findViewById(R.id.fragment_correct_doc__root)
        mDocumentImageV = mContentView!!.findViewById(R.id.fragment_correct_doc__image)
    }

    override fun setImage(image: Bitmap, points: List<PointF>?) {
        mDocumentImageV.setImageBitmap(image)
        mDocumentImageV.points = points
    }

    override fun onSelected() {
        mPresenter?.start()
    }

    override fun verifyStep(): VerificationError? {
        mPresenter?.finishCrop(mDocumentImageV.points)
        return null
    }

    override fun onError(error: VerificationError) {
    }

    override fun setPresener(presenter: CorrectDocContract.Presenter) {
        mPresenter = presenter
    }


}
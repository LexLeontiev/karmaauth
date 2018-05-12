package com.lexleontiev.karmaauth.ui.main.step.completedoc

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.lexleontiev.karmaauth.R
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError


class CompleteDocFragment : Fragment(), Step, CompleteDocContract.View {

    private var mContentView: View? = null
    lateinit var mImageV: ImageView
    private var mLayoutId: Int = 0

    lateinit var mRootL: ViewGroup

    private var mPresenter: CompleteDocContract.Presenter? = null


    companion object {
        fun newInstance(): CompleteDocFragment {
            val fragment = CompleteDocFragment()
            val bundle = Bundle()
            bundle.putInt("layout_id", R.layout.fragment_complete_doc)
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
        mRootL = mContentView!!.findViewById(R.id.fragment_complete_doc__root)
        mImageV = mContentView!!.findViewById(R.id.fragment_complete_doc__image)
    }

    override fun setImage(image: Bitmap) {
        mImageV.setImageBitmap(image)
    }

    override fun onSelected() {
        mPresenter?.start()
    }

    override fun verifyStep(): VerificationError? {
        mPresenter?.complete()
        return null
    }

    override fun onError(error: VerificationError) {
    }

    override fun setPresener(presenter: CompleteDocContract.Presenter) {
        mPresenter = presenter
    }


}
package com.lexleontiev.karmaauth.ui.main.step.selectdoc

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatSpinner
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.lexleontiev.karmaauth.R
import com.lexleontiev.karmaauth.data.value.DocumentType
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import java.io.InputStream

const val CAMERA_REQUEST_CODE = 0
const val GALLERY_REQUEST_CODE = 1

class SelectDocFragment: Fragment(), Step, SelectDocContract.View  {

    private var mContentView: View? = null
    private var mLayoutId: Int = 0

    private lateinit var mRootL: ViewGroup
    private lateinit var mDocumentDropdownV: AppCompatSpinner
    private lateinit var mDocumentHintV: TextView
    private lateinit var mDocumentAddB: Button
    private lateinit var mDocumentSelectedHintV: TextView

    private var mPresenter: SelectDocContract.Presenter? = null


    companion object {
        fun newInstance(): SelectDocFragment {
            val fragment = SelectDocFragment()
            val bundle = Bundle()
            bundle.putInt("layout_id", R.layout.fragment_select_doc)
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
            mPresenter?.start()
        }
        return mContentView
    }

    private fun bindData() {
        mRootL = mContentView!!.findViewById(R.id.fragment_select_doc__root)
        mDocumentDropdownV = mContentView!!.findViewById(R.id.fragment_select_doc__select)
        mDocumentHintV = mContentView!!.findViewById(R.id.fragment_select_doc__hint)
        mDocumentAddB = mContentView!!.findViewById(R.id.fragment_select_doc__add)
        mDocumentSelectedHintV = mContentView!!.findViewById(R.id.fragment_select_doc__selected_hint)
        mDocumentAddB.setOnClickListener({
            mPresenter?.addDocument(mDocumentDropdownV.selectedItem as DocumentType)
        })
    }


    override fun onSelected() {
    }

    override fun verifyStep(): VerificationError? {
        return null
    }

    override fun onError(error: VerificationError) {
    }

    override fun setUpDocumentDropdown(documentTypeList: List<DocumentType>) {
        val adapter = ArrayAdapter<DocumentType>(context, android.R.layout.simple_spinner_item,
                documentTypeList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mDocumentDropdownV.adapter = adapter
    }

    override fun showSelectDocumentSourceDialog() {
        val db = AlertDialog.Builder(context, 0)
        db.setTitle(R.string.select_document_source)
        db.setItems(R.array.file_source_array) { dialogInterface, i ->
            var intent: Intent? = null
            var requestCode = -1
            when (i) {
                CAMERA_REQUEST_CODE -> {
                    requestCode = CAMERA_REQUEST_CODE
                    intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                }
                GALLERY_REQUEST_CODE -> {
                    requestCode = GALLERY_REQUEST_CODE
                    intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                }
            }
            startActivityForResult(intent, requestCode)
            dialogInterface.dismiss()
        }
        db.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val bitmap = data.extras!!.get("data") as? Bitmap
                    if (bitmap != null) {
                        mPresenter?.fileSelected(bitmap)
                    }
                }
                GALLERY_REQUEST_CODE -> {
                    val imageUri = data.data
                    val imageStream: InputStream?
                    imageStream = context.contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(imageStream)
                    if (bitmap != null) {
                        mPresenter?.fileSelected(bitmap)
                    }
                }
            }
        }
    }

    override fun setPresener(presenter: SelectDocContract.Presenter) {
        mPresenter = presenter
    }

    override fun fileSelected(selected: Boolean) {
        mDocumentAddB.text = if (selected) getString(R.string.document_selected)
                             else getString(R.string.document_add)
        mDocumentSelectedHintV.visibility = if (selected) VISIBLE else INVISIBLE
    }
}
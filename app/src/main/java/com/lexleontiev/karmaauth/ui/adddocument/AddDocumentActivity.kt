package com.lexleontiev.karmaauth.ui.adddocument

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.widget.AppCompatSpinner
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import com.lexleontiev.karmaauth.R
import com.lexleontiev.karmaauth.data.value.DocumentType
import com.lexleontiev.karmaauth.data.value.Document
import com.lexleontiev.karmaauth.ui.editdocument.EditDocumentActivity
import java.io.InputStream

class AddDocumentActivity : AppCompatActivity(), AddDocumentContract.View {

    lateinit var mRootL: ViewGroup
    lateinit var mDocumentDropdownV: AppCompatSpinner
    lateinit var mDocumentHintV: TextView
    lateinit var mProgressV: ProgressBar
    lateinit var mDocumentAddB: Button
    lateinit var mImageV: ImageView

    private var mPresenter: AddDocumentContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_document)
        mRootL = findViewById(R.id.root_layout)
        mDocumentDropdownV = findViewById(R.id.document_select)
        mDocumentHintV = findViewById(R.id.document_hint)
        mProgressV = findViewById(R.id.document_progress)
        mImageV = findViewById(R.id.document_image)
        mDocumentAddB = findViewById(R.id.document_add)
        mDocumentAddB.setOnClickListener({
            mPresenter?.addDocument(mDocumentDropdownV.selectedItem as DocumentType)
        })
        title = "Загрузка документа"
        AddDocumentPresenter(this)
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
                    imageStream = contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(imageStream)
                    if (bitmap != null) {
                        mPresenter?.fileSelected(bitmap)
                    }
                }
            }
        }
    }

    override fun showProgress() {
        mDocumentDropdownV.visibility = GONE
        mDocumentHintV.visibility = GONE
        mDocumentAddB.visibility = GONE
        mProgressV.visibility = VISIBLE
    }

    override fun hideProgress() {
        mDocumentDropdownV.visibility = VISIBLE
        mDocumentHintV.visibility = VISIBLE
        mDocumentAddB.visibility = VISIBLE
        mProgressV.visibility = GONE
    }

    override fun openEditDocumentView(document: Document) {
        val intent = Intent(this, EditDocumentActivity::class.java)
        intent.putExtra("document", document)
        startActivity(intent)
    }

    override fun showErrorSnackBar(action: () -> Unit) {
        Snackbar.make(mRootL, R.string.document_upload_error, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.repeat) { action.invoke() }
                .show()
    }

    override fun setUpDocumentDropdown(documentTypeList: List<DocumentType>) {
        val adapter = ArrayAdapter<DocumentType>(this, android.R.layout.simple_spinner_item,
                documentTypeList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mDocumentDropdownV.adapter = adapter
    }


    override fun setPresener(presenter: AddDocumentContract.Presenter) {
        mPresenter = presenter
        mPresenter?.start()
    }

    override fun showSelectDocumentSourceDialog() {
        val db = AlertDialog.Builder(this, 0)
        db.setTitle("Выберете источник документа")
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

    override fun showTestImage(image: Bitmap) {
        mImageV.setImageBitmap(image)
    }

    override fun showToast(text: String) {
        Toast.makeText(this,text, Toast.LENGTH_LONG).show()
    }
}


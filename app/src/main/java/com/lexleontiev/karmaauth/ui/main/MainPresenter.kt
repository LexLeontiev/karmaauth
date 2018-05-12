package com.lexleontiev.karmaauth.ui.main

import android.graphics.Bitmap
import android.util.Base64
import com.google.gson.JsonObject
import com.lexleontiev.karmaauth.data.value.Document
import com.lexleontiev.karmaauth.framework.network.BaseNetworkClient
import com.lexleontiev.karmaauth.framework.network.ServerResponse
import com.lexleontiev.karmaauth.ui.main.step.completedoc.CompleteDocContract
import com.lexleontiev.karmaauth.ui.main.step.completedoc.CompleteDocPresenter
import com.lexleontiev.karmaauth.ui.main.step.correctdoc.CorrectDocContract
import com.lexleontiev.karmaauth.ui.main.step.correctdoc.CorrectDocPresenter
import com.lexleontiev.karmaauth.ui.main.step.selectdoc.SelectDocContract
import com.lexleontiev.karmaauth.ui.main.step.selectdoc.SelectDocPresenter
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.io.ByteArrayOutputStream


class MainPresenter(private val mView: MainContract.View) : MainContract.Presenter, MainContract.Workflow {

    private val selectDocPresenter: SelectDocContract.Presenter
    private val correctDocPresenter: CorrectDocContract.Presenter
    private val completeDocPresenter: CompleteDocContract.Presenter

    private var mImage: Bitmap? = null
    private var mProcessedImage: Bitmap? = null

    private val mNetworkClient: BaseNetworkClient

    init {
        selectDocPresenter = SelectDocPresenter(this)
        correctDocPresenter = CorrectDocPresenter(this)
        completeDocPresenter = CompleteDocPresenter(this)
        mNetworkClient = BaseNetworkClient()
    }

    override fun start() {
    }

    override fun sendFileForRecognition(image: Bitmap) {
        launch(UI) {

            mView.showProgress()

            val request = async(CommonPool) {
                mNetworkClient.post("http://37.143.14.239:8000/api/images/parts/",
                        createRequestBody(image).toString())
            }

            val document = createDocumentOrError(request.await())
            if (document != null) {
                mView.openEditDocumentView(document)
                mView.hideProgress()
            } else {
                mView.hideProgress()
                mView.showErrorSnackBar { sendFileForRecognition(mProcessedImage!!) }
            }
        }
    }

    private fun createDocumentOrError(response: ServerResponse) : Document? {
        if (response.isSuccess()) {
            return Document(null, null,null, null,
                    null, null, null, null,
                    null, null)
        }
        return null
    }

    private fun createRequestBody(bitmap: Bitmap) : JsonObject {
        val body = JsonObject()
        val imageBase64 = encodeImage(bitmap)
        body.addProperty("part_image", imageBase64)
        return body
    }

    override fun getSelectDocPresenter(): SelectDocContract.Presenter {
        return selectDocPresenter
    }

    override fun getCorrectDocPresenter(): CorrectDocContract.Presenter {
        return correctDocPresenter
    }

    override fun getCompleteDocPresenter(): CompleteDocContract.Presenter {
        return completeDocPresenter
    }

    private fun encodeImage(bm: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val b = outputStream.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    /**
     * Workflow
     */

    override fun fileSelected(image: Bitmap) {
        mImage = image
        mView.fileSelected(image != null)
    }

    override fun fileProcessed(image: Bitmap) {
        mProcessedImage = image
    }

    override fun fileComplete() {
        if (mProcessedImage != null) {
            sendFileForRecognition(mProcessedImage!!)
        }
    }

    override fun getResultImage(): Bitmap? {
        return mProcessedImage
    }

    override fun getSelectedImage(): Bitmap? {
        return mImage
    }
}
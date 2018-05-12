package com.lexleontiev.karmaauth.ui.main

import android.graphics.Bitmap
import android.util.Base64
import com.google.gson.JsonObject
import com.lexleontiev.karmaauth.data.value.Document
import com.lexleontiev.karmaauth.framework.network.BaseNetworkClient
import com.lexleontiev.karmaauth.framework.network.ServerResponse
import com.lexleontiev.karmaauth.framework.vision.ImagesHolder
import com.lexleontiev.karmaauth.framework.vision.VisionManager
import com.lexleontiev.karmaauth.ui.main.step.completedoc.CompleteDocContract
import com.lexleontiev.karmaauth.ui.main.step.completedoc.CompleteDocPresenter
import com.lexleontiev.karmaauth.ui.main.step.correctdoc.CorrectDocContract
import com.lexleontiev.karmaauth.ui.main.step.correctdoc.CorrectDocPresenter
import com.lexleontiev.karmaauth.ui.main.step.selectdoc.SelectDocContract
import com.lexleontiev.karmaauth.ui.main.step.selectdoc.SelectDocPresenter
import com.orhanobut.logger.Logger
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.io.ByteArrayOutputStream


class MainPresenter(private val mView: MainContract.View) : MainContract.Presenter,
        MainContract.Workflow {

    private val selectDocPresenter: SelectDocContract.Presenter
    private val correctDocPresenter: CorrectDocContract.Presenter
    private val completeDocPresenter: CompleteDocContract.Presenter

    private val mNetworkClient: BaseNetworkClient
    private val mVisionManager: VisionManager

    init {
        selectDocPresenter = SelectDocPresenter(this)
        correctDocPresenter = CorrectDocPresenter(this)
        completeDocPresenter = CompleteDocPresenter(this)
        mNetworkClient = BaseNetworkClient()
        mVisionManager = VisionManager()
    }

    override fun start() {
    }

    override fun sendFileForRecognition(image: Bitmap) {
        launch(UI) {

            mView.showProgress()

            val request = async(CommonPool) {
                mNetworkClient.post("http://mzemskov.com/api/images/", createRequestBody(image).toString())
            }

            val document = createDocumentOrError(request.await())
            if (document != null) {
                mView.openEditDocumentView(document)
                mView.hideProgress()
            } else {
                mView.hideProgress()
                mView.showErrorSnackBar {
                    sendFileForRecognition(image)
                }
            }
        }
    }

    private fun createDocumentOrError(response: ServerResponse) : Document? {
        Logger.d(response)
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
        body.addProperty("image", imageBase64)
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
        mVisionManager.setImagesHolder(ImagesHolder(image))
        mView.fileSelected(true)
    }

    override fun fileProcessed(image: Bitmap) {
        mVisionManager.getImagesHolder()?.mCroppedImage = image
    }

    override fun fileComplete(image: Bitmap) {
        mVisionManager.getImagesHolder()?.mCompleteImage = image
        val competeImage = mVisionManager.getImagesHolder()?.mCompleteImage
        if (competeImage != null) {
            sendFileForRecognition(competeImage)
        }
    }

    override fun getVisionManager(): VisionManager {
        return mVisionManager
    }
}
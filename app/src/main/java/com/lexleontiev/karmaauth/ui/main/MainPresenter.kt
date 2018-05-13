package com.lexleontiev.karmaauth.ui.main

import android.graphics.Bitmap
import com.lexleontiev.karmaauth.data.source.ImageSource
import com.lexleontiev.karmaauth.data.value.Document
import com.lexleontiev.karmaauth.framework.network.ServerResponse
import com.lexleontiev.karmaauth.framework.network.source.ImageNetworkSource
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


class MainPresenter(private val mView: MainContract.View) : MainContract.Presenter,
        MainContract.WorkflowController {

    private val selectDocPresenter: SelectDocContract.Presenter
    private val correctDocPresenter: CorrectDocContract.Presenter
    private val completeDocPresenter: CompleteDocContract.Presenter

    private val mImageSource: ImageSource
    private val mVisionManager: VisionManager

    init {
        selectDocPresenter = SelectDocPresenter(this)
        correctDocPresenter = CorrectDocPresenter(this)
        completeDocPresenter = CompleteDocPresenter(this)
        mImageSource = ImageNetworkSource()
        mVisionManager = VisionManager()
    }

    override fun start() {
    }

    override fun sendFileForRecognition(image: Bitmap) {
        launch(UI) {

            mView.showProgress()

            val request = async(CommonPool) {
                mImageSource.add(mVisionManager.encodeImage(image))
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
            return Document()
        }
        return null
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

    /**
     * WorkflowController
     */

    override fun goBack(position: Int) {
        mView.goToPreviousStep(position)
    }

    override fun goNext(position: Int) {
        mView.goToNextStep(position)
    }

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
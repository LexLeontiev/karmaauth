package com.lexleontiev.karmaauth.ui.adddocument

import android.graphics.Bitmap
import android.util.Base64
import com.google.gson.*
import com.lexleontiev.karmaauth.data.value.Document
import com.lexleontiev.karmaauth.data.value.DocumentType
import com.lexleontiev.karmaauth.framework.network.BaseNetworkClient
import com.lexleontiev.karmaauth.framework.network.ServerResponse
import com.orhanobut.logger.Logger
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

const val CAMERA_REQUEST_CODE = 0
const val GALLERY_REQUEST_CODE = 1

class AddDocumentPresenter(private val mView: AddDocumentContract.View)
    : AddDocumentContract.Presenter {

    private val mDocumentTypes = ArrayList<DocumentType>()
    private val mNetworkClient: BaseNetworkClient
    private var mSelectedImage: Bitmap? = null

    init {
        mDocumentTypes.add(DocumentType(1,"Паспорт"))
        mNetworkClient = BaseNetworkClient()
        mView.setPresener(this)
    }

    override fun start() {
        mView.setUpDocumentDropdown(mDocumentTypes)
    }

    override fun addDocument(documentType: DocumentType) {
        Logger.d(documentType)
        mView.showSelectDocumentSourceDialog()
    }

    override fun fileSelected(image: Bitmap) {
        mSelectedImage = image
        sendFileForRecognition(image)
    }

    override fun sendFileForRecognition(image: Bitmap) {
        launch(UI) {

            mView.showProgress()

            val firstNameRecognitionRequest = async(CommonPool) {
                recognizeFirstName(image)
            }
            val lastNameRecognitionRequest = async(CommonPool) {
                recognizeLastName(image)
            }
            val middleNameRecognitionRequest = async(CommonPool) {
                recognizeMiddleName(image)
            }
            val extraditionAgencyRecognitionRequest = async(CommonPool) {
                recognizeExtraditionAgency(image)
            }
            val extraditionDateRecognitionRequest = async(CommonPool) {
                recognizeExtraditionDate(image)
            }
            val unitCodeRecognitionRequest = async(CommonPool) {
                recognizeUnitCode(image)
            }

            val document = createDocumentOrError(firstNameRecognitionRequest.await(),
                    lastNameRecognitionRequest.await(),
                    middleNameRecognitionRequest.await(),
                    extraditionAgencyRecognitionRequest.await(),
                    extraditionDateRecognitionRequest.await(),
                    unitCodeRecognitionRequest.await())

            if (document != null) {
                mView.openEditDocumentView(document)
                mView.hideProgress()
            } else {
                mView.showErrorSnackBar { sendFileForRecognition(mSelectedImage!!) }
            }
        }
    }

    private fun parseRecognitionResponse(jsonStr: String?) : String? {
        val json = JsonParser().parse(jsonStr)
        return json.asJsonObject.getAsJsonArray("responses")?.get(0)
                ?.asJsonObject?.getAsJsonArray("textAnnotations")?.get(0)
                ?.asJsonObject?.getAsJsonPrimitive("description")?.asString?.dropLast(1)
    }

    private fun recognizeLastName(bitmap: Bitmap): ServerResponse {
        return recognizeObject(bitmap, 0.45, 0.54, 0.85, 0.64)
    }

    private fun recognizeFirstName(bitmap: Bitmap): ServerResponse {
        return recognizeObject(bitmap, 0.45, 0.62, 0.85, 0.68)
    }

    private fun recognizeMiddleName(bitmap: Bitmap): ServerResponse {
        return recognizeObject(bitmap, 0.45, 0.68, 0.85, 0.72)
    }

    private fun recognizeExtraditionAgency(bitmap: Bitmap): ServerResponse {
        return recognizeObject(bitmap, 0.2, 0.1, 0.93, 0.2)
    }

    private fun recognizeExtraditionDate(bitmap: Bitmap): ServerResponse {
        return recognizeObject(bitmap, 0.18, 0.21, 0.4, 0.24)
    }

    private fun recognizeUnitCode(bitmap: Bitmap): ServerResponse {
        return recognizeObject(bitmap, 0.57, 0.21, 0.93, 0.24)
    }

    private fun recognizeObject(bitmap: Bitmap, x1: Double, y1: Double, x2: Double, y2: Double)
            : ServerResponse {
        val obj = cropImage(bitmap, x1, y1, x2, y2)
        return mNetworkClient.post("https://vision.googleapis.com/v1/images:annotate?key=AIzaSyB3MHQNEaNltuEEPLf1GsVKMPRd4uDIcPs",
                createRequestBody(obj).toString())
    }

    private fun createRequestBody(bitmap: Bitmap) : JsonObject {
        val body = JsonObject()
        val requests = JsonArray()
        val request = JsonObject()
        val features = JsonArray()
        val feature = JsonObject()
        feature.addProperty("type","TEXT_DETECTION")
        features.add(feature)
        request.add("features",features)
        val imageObject = JsonObject()
        val imageBase64 = encodeImage(bitmap)
        imageObject.addProperty("content", imageBase64)
        request.add("image",imageObject)
        requests.add(request)
        body.add("requests", requests)
        return body
    }

    private fun createDocumentOrError(firstNameResponse: ServerResponse,
                                      lastNameResponse: ServerResponse,
                                      middleNameResponse: ServerResponse,
                                      extraditionAgency: ServerResponse,
                                      extraditionDate: ServerResponse,
                                      unitCode: ServerResponse) : Document? {
        if (firstNameResponse.isSuccess() && lastNameResponse.isSuccess()
                && middleNameResponse.isSuccess()) {
            return Document(null, null,null,
                    parseRecognitionResponse(firstNameResponse.body()),
                    parseRecognitionResponse(lastNameResponse.body()),
                    parseRecognitionResponse(middleNameResponse.body()),
                    parseRecognitionResponse(extraditionAgency.body()),
                    parseRecognitionResponse(extraditionDate.body()),
                    parseRecognitionResponse(unitCode.body()),
                    null)
        }
        return null
    }

    private fun encodeImage(bm: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val b = outputStream.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private fun cropImage(bm: Bitmap, x1: Double, y1: Double, x2: Double, y2: Double) : Bitmap {
        val startXPoint = (bm.width*x1).roundToInt()
        val startYPoint = (bm.height*y1).roundToInt()
        val widthImage = (bm.width*(x2-x1)).roundToInt()
        val heightImage = (bm.height*(y2-y1)).roundToInt()
        return Bitmap.createBitmap(bm,startXPoint,startYPoint, widthImage, heightImage)
    }
}
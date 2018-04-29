package com.lexleontiev.karmaauth.data.value

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Document(var id: Int?, var documentType: DocumentType?,
                    @SerializedName("passport_image") var passportImage: String?,
                    @SerializedName("first_name") var firstName: String?,
                    @SerializedName("last_name") var lastName: String?,
                    @SerializedName("middle_name") var middleName: String?,
                    var extraditionAgency: String?, var extraditionDate: String?,
                    var unitCode: String?,
                    @SerializedName("passport_image_path") var passportImagePath: String?)
    : Parcelable
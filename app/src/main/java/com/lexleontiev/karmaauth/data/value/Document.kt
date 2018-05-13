package com.lexleontiev.karmaauth.data.value

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Document(var id: Int? = null,
                    var documentType: DocumentType? = null,
                    @SerializedName("passport_image") var passportImage: String? = null,
                    @SerializedName("first_name") var firstName: String? = null,
                    @SerializedName("last_name") var lastName: String? = null,
                    @SerializedName("middle_name") var middleName: String? = null,
                    var extraditionAgency: String? = null,
                    var extraditionDate: String? = null,
                    var unitCode: String? = null,
                    @SerializedName("passport_image_path") var passportImagePath: String? = null)
    : Parcelable
package com.lexleontiev.karmaauth.data.value

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class DocumentType(val id: Int, val name: String) : Parcelable {

    override fun toString(): String {
        return name
    }
}
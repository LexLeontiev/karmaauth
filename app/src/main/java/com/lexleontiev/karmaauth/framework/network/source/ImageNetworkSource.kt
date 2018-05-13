package com.lexleontiev.karmaauth.framework.network.source

import com.google.gson.JsonObject
import com.lexleontiev.karmaauth.data.source.ImageSource
import com.lexleontiev.karmaauth.framework.network.BASE_URL
import com.lexleontiev.karmaauth.framework.network.BaseNetworkClient
import com.lexleontiev.karmaauth.framework.network.ServerResponse

const val POST_IMAGES_ADD = "images/"

class ImageNetworkSource: BaseNetworkClient(), ImageSource {

    override fun add(base64image: String) : ServerResponse {
        val body = JsonObject()
        body.addProperty("image", base64image)
        return post(BASE_URL + POST_IMAGES_ADD, body.toString())
    }
}
package com.lexleontiev.karmaauth.data.source

import com.lexleontiev.karmaauth.framework.network.ServerResponse


interface ImageSource {

    fun add(base64image: String) : ServerResponse
}
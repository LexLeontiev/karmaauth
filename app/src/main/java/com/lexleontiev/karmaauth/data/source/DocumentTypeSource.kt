package com.lexleontiev.karmaauth.data.source

import com.lexleontiev.karmaauth.framework.network.ServerResponse


interface DocumentTypeSource {

    fun get() : ServerResponse
}
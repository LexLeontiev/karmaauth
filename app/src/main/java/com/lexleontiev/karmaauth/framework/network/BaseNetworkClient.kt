package com.lexleontiev.karmaauth.framework.network

import com.lexleontiev.karmaauth.BuildConfig

import java.io.IOException
import java.util.concurrent.TimeUnit

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class BaseNetworkClient {

    companion object {

        internal val TIMEOUT_CONNECT_SECONDS = 15

        private val JSON = MediaType.parse("application/json; charset=utf-8")
        private val CONTENT_TYPE_KEY = "Content-Type"
        private val CONTENT_TYPE_JSON = "application/json"
    }

    private val mOkHttpClient: OkHttpClient

    init {
        mOkHttpClient = OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_CONNECT_SECONDS.toLong(), TimeUnit.SECONDS)
                .build()
    }

    fun post(url: String, jsonData: String): ServerResponse {
        val body = RequestBody.create(JSON, jsonData)
        val rb = Request.Builder()
        rb.url(url).post(body)
        addHeaders(rb)
        val request = rb.build()
        try {
            mOkHttpClient.newCall(request).execute().use {
                response -> return ServerResponse(response.code(), response.body()?.string()) }
        } catch (e: IOException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            return ServerResponse(0, null)
        }

    }

    private fun addHeaders(rb: Request.Builder) {
        rb.header(CONTENT_TYPE_KEY, CONTENT_TYPE_JSON)
    }
}


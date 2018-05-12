package com.lexleontiev.karmaauth.framework.network

class ServerResponse(private val code: Int, private val body: String?) {

    val CODE_OK = 200
    val CODE_CREATED = 201

    fun code(): Int {
        return code
    }

    fun body(): String? {
        return body
    }

    fun isSuccess() : Boolean {
        return code == CODE_OK || code == CODE_CREATED
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as ServerResponse?

        if (code != that!!.code) return false
        return if (body != null) body == that.body else that.body == null

    }

    override fun hashCode(): Int {
        var result = code
        result = 31 * result + (body?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "ServerResponse{" +
                "code=" + code +
                ", body='" + body + '\''.toString() +
                '}'.toString()
    }
}
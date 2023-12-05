package com.zhou.common.network

class HttpResponseException(code: Int, msg: String) : Exception() {
    private var statusCode = code
    override var message: String? = msg


    fun getStatusCode(): Int {
        return statusCode
    }

    fun getMsg(): String? {
        return message
    }
}
package com.zhou.common.network

import java.net.URI

interface RequestHandler {
    fun getURI(): URI?

    /**
     * 中止
     */
    fun abort()
}
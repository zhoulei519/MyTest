package com.zhou.common.network

import android.util.Pair
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

object DefaultNetWorkClient {
    /**
     * 网络连接超时时间
     */
    const val CONNECT_TIMEOUT = 15 * 1000

    /**
     * 读取超时时间
     */
    const val IO_TIMEOUT = 15 * 1000

    /**
     * 执行请求
     */
    @Throws(Throwable::class)
    fun excuteRequest(
        urlPath: String?,
        data: ByteArray?,
        headerInfo: Map<String?, String?>?,
        executeMethod: String?,
        isPost: Boolean
    ): String? {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlPath)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = executeMethod
            connection.connectTimeout = CONNECT_TIMEOUT
            connection.readTimeout = IO_TIMEOUT
            connection.setRequestProperty("Accept-Encoding", "gzip")
            if (headerInfo != null && headerInfo.isNotEmpty()) {
                for ((key, value) in headerInfo) {
                    if (!isNullOrEmpty(key) && !isNullOrEmpty(value)) {
                        connection.setRequestProperty(key, value)
                    }
                }
            }
            connection.doOutput = isPost

            /**
             * 判断executeMethod如果为空抛出异常
             */
            requireNotNull(executeMethod) { "request method is not null" }


            if (data != null && data.isNotEmpty()) {
                val dataOutputStream = DataOutputStream(connection.outputStream)
                dataOutputStream.write(data)
                dataOutputStream.flush()
                dataOutputStream.close()
            }
            val responseCode = connection.responseCode
            if (responseCode == 200){
                val inputStream = connection.inputStream

                /**
                 * 内容编码格式是否是gzip
                 */
                val use_gzip = "gzip" == connection.getHeaderField("Content-Encoding")
                val contentType = connection.getHeaderField("Content-Type")
                val ss_binary = StreamParser.testIsSSBinary(contentType)
                var charset: String? = null
                val p: Pair<String, String?>? = parseContentType(contentType)
                if (p != null) {
                    if (p.second != null && Charset.isSupported(p.second)) {
                        charset = p.second
                    }
                }
                if (charset == null) {
                    charset = "UTF-8"
                }

                val off = IntArray(1)
                off[0] = 0
                val finalUrl = urlPath!!
                val finalConnection: HttpURLConnection = connection

                return ""
            }
            val msg = connection.responseMessage
            throw HttpResponseException(responseCode, msg)
        }catch (e:Throwable){
            throw e
        }finally {
            try {
                connection?.disconnect()
            } catch (e:Exception) {
                // ignore
            }
        }
    }
    private fun isNullOrEmpty(str: String?): Boolean {
        if (str != null && str.isNotEmpty())
            return false
        return true
    }

    /** 从内容类型字符串分析mime类型和字符集  */
    private fun parseContentType(contentType: String?): Pair<String, String?>? {
        if (contentType == null) {
            return null
        }
        var mime: String? = null
        var charset: String? = null
        try {
//            val mimeType = MimeType(contentType)
//            mime = mimeType.getBaseType()
//            charset = mimeType.getParameter("charset")
        } catch (t: Throwable) {
            // ignore
            t.printStackTrace()
        }
        return Pair(mime, charset)
    }
}
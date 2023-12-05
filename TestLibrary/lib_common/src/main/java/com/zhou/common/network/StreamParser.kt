package com.zhou.common.network

import com.zhou.common.utils.LogUtil
import java.io.Closeable
import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.util.zip.GZIPInputStream

/**
 * 流解析器
 */
object StreamParser {

    private const val TAG = "NetworkUtils"

    /**
     * Content-Type，用于定义网络文件的类型和网页的编码
     * application/octet-stream  为二进制流数据
     */
    private const val CONTENT_TYPE_OCTET = "application/octet-stream"

    /**
     * 最大API相应长度
     */
    private const val MAX_API_RESPONSE_LENGTH = 5 * 1024 * 1024

    /**
     * 判断类型是否为ss_binary
     */
    fun testIsSSBinary(contentType: String?): Boolean {
        if (contentType == null) {
            return false
        }
        var index = contentType.indexOf(CONTENT_TYPE_OCTET)
        if (index >= 0) {
            index = contentType.indexOf("ssmix=", index + CONTENT_TYPE_OCTET.length)
        }
        return index > 0
    }

    @Throws(IOException::class)
    fun response2buf(
        use_gzip: Boolean,
        maxLength: Int,
        `in`: InputStream?,
        off: IntArray,
        requestHandler: RequestHandler?
    ): ByteArray? {
        if (`in` == null) {
            return null
        }

        var buf: ByteArray? = null
        try {
            buf = readResponse(use_gzip, maxLength, `in`, off)
        } catch (e: Exception) {
            try {
                requestHandler?.abort()
            } catch (th: Throwable) {
                // ignore
            }
            throw e
        }
        return if (buf == null || off[0] <= 0) {
            null
        } else buf
    }

    @Throws(IOException::class)
    fun readResponse(
        use_gzip: Boolean,
        maxLength: Int,
        `in`: InputStream?,
        out_off: IntArray
    ): ByteArray? {
        var maxLength = maxLength
        var `in` = `in`
        if (maxLength <= 0) maxLength = MAX_API_RESPONSE_LENGTH
        if (maxLength < 1024 * 1024) maxLength = 1024 * 1024
        return if (`in` == null) {
            null
        } else try {
            if (use_gzip) {
                `in` = GZIPInputStream(`in`)
            }
            var buf = ByteArray(8 * 1024)
            var n = 0
            var off = 0
            val count = 4 * 1024
            while (true) {
                // some gateway pack wrong 'chunked' data, without crc32 and isize
                try {
                    if (off + count > buf.size) {
                        val newbuf = ByteArray(buf.size * 2)
                        System.arraycopy(buf, 0, newbuf, 0, off)
                        buf = newbuf
                    }
                    n = `in`.read(buf, off, count)
                    off += if (n > 0) {
                        n
                    } else {
                        break
                    }
                    if (maxLength in 1 until off) {
                        LogUtil.d(TAG, "entity length did exceed given maxLength")
                        return null
                    }
                } catch (e: EOFException) {
                    if (use_gzip && off > 0) {
                        LogUtil.d(TAG, "ungzip got exception $e")
                        break
                    } else {
                        throw e
                    }
                } catch (e: IOException) {
                    val msg = e.message
                    if (use_gzip && off > 0 && ("CRC mismatch" == msg || "Size mismatch" == msg)) {
                        LogUtil.d(TAG, "ungzip got exception $e")
                        break
                    } else {
                        throw e
                    }
                }
            }
            if (off > 0) {
                out_off[0] = off
                buf
            } else {
                null
            }
        } finally {
            safeClose(`in`)
        }
    }

    private fun safeClose(c: Closeable?) {
        safeClose(c, null)
    }

    private fun safeClose(c: Closeable?, msg: String?) {
        try {
            c?.close()
        } catch (e: Exception) {
            if (msg != null) {
                LogUtil.d(TAG, "$msg $e")
            }
        }
    }
}
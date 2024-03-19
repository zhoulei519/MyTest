package com.zhou.testlibrary.speech.xunfei

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.zhou.testlibrary.R
import com.zhou.testlibrary.utils.LogUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

/**
 * @author: zl
 * @date: 2023/12/26
 */
class WebApiSpeech : AppCompatActivity() {
    private val httpClient = OkHttpClient().newBuilder().build()
    private val host = "https://raasr.xfyun.cn"
    private val appId = "079b0fe7"
    private val keySecret = "313febfd27128bdb888a6d50d96d674a"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common)
        initView()
        initPermission()
    }

    private fun initView() {
        val btn: Button = findViewById(R.id.btn)
        btn.setOnClickListener {
            start()
        }
    }

    private fun start() {
        GlobalScope.launch {
            upload()
            getXunFeiOrder()
        }
    }

    private fun getXunFeiOrder() {
        val map = HashMap<String, Any>(16)
        map["appId"] = appId
        map["orderId"] = "DKHJQ20231226151103245x8Uu6zZlobNrTG3R"
        val lfasrSignature = LfasrSignature(appId, keySecret)
        map["signa"] = lfasrSignature.signa
        map["ts"] = lfasrSignature.ts
        map["resultType"] = "transfer,predict"

        val paramString = parseMapToPathParam(map)
        val url = "$host/v2/api/getResult?$paramString"

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Content-Type", "multipart/form-data")
            .build()
        val response = httpClient.newCall(request).execute()
        val responseData = response.body()?.string()

        val gson = Gson()
        val responseBean = gson.fromJson(responseData, ResponseData::class.java)
        LogUtil.d(responseBean.content.orderResult)
        val orderInfo = responseBean.content.orderResult.replace("\\", "").replace("\"{", "{")
            .replace("}\"", "}")
        LogUtil.d(orderInfo)
        val orderResultData = gson.fromJson(orderInfo, OrderResultData::class.java)
        var speechValue = ""
        for (lattice in orderResultData.lattice2) {
            for (ws in lattice.json1Best.st.rt[0].ws) {
                speechValue += ws.cw[0].w
            }
        }
        LogUtil.d(speechValue)
    }

    private fun upload() {
        val map = HashMap<String, Any>(16)
        val audio = File(getSDCardPathByEnvironment() + "/Download/txt.mp3")
        val fileName = audio.name
        val fileSize = audio.length()
        map["appId"] = appId
        map["fileSize"] = fileSize
        map["fileName"] = fileName
        map["duration"] = "200"
        val lfasrSignature = LfasrSignature(appId, keySecret)
        map["signa"] = lfasrSignature.signa
        map["ts"] = lfasrSignature.ts
        val paramString = parseMapToPathParam(map)
        val url = "$host/v2/api/upload?$paramString"
        val fileInputStream = FileInputStream(audio)
//        val requestBody = InputStreamRequestBody(fileInputStream, "application/json")
        val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"),audio)
        val request: Request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response: Response = httpClient.newCall(request).execute()
        response.body()?.let { LogUtil.d(it.string()) }
    }

    private fun getSDCardPathByEnvironment(): String? {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            Environment.getExternalStorageDirectory().absolutePath
        } else ""
    }

    private fun parseMapToPathParam(param: Map<String, Any>): String {
        val sb = StringBuilder()
        try {
            val entryset = param.entries
            var isFirst = true

            for ((key, value) in entryset) {
                if (!isFirst) {
                    sb.append("&")
                } else {
                    isFirst = false
                }
                sb.append(URLEncoder.encode(key, "UTF-8"))
                sb.append("=")
                sb.append(URLEncoder.encode(value.toString(), "UTF-8"))
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return sb.toString()
    }

    internal class InputStreamRequestBody(inputStream: InputStream, contentType: String) :
        RequestBody() {
        private val inputStream: InputStream
        private val contentType: String

        init {
            this.inputStream = inputStream
            this.contentType = contentType
        }

        override fun contentType(): MediaType? {
            return MediaType.parse(contentType)
        }

        override fun contentLength(): Long {
            return inputStream.available().toLong()
        }

        @Throws(IOException::class)
        override fun writeTo(sink: BufferedSink) {
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                sink.write(buffer, 0, bytesRead)
            }
        }
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private fun initPermission() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val toApplyList = ArrayList<String>()
        for (perm in permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
                    this,
                    perm
                )
            ) {
                toApplyList.add(perm)
                // 进入到这里代表没有权限.
            }
        }
        val tmpList = arrayOfNulls<String>(toApplyList.size)
        if (toApplyList.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123)
        }
    }
}
package com.zhou.testlibrary.speech.baidu.webapi

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zhou.testlibrary.R
import com.zhou.testlibrary.utils.LogUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject

/**
 * @author: zl
 * @date: 2023/12/26
 */
class WebApiSpeech : AppCompatActivity(){
    private val httpClient = OkHttpClient().newBuilder().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common)
        initView()
        initPermission()
    }

    private fun initView(){
        val btn:Button = findViewById(R.id.btn)
        btn.setOnClickListener {
            start()
        }
    }

    private fun start() {
        GlobalScope.launch {
            getSpeechTaskId()
            getSpeechValue()
        }
    }

    private fun getSpeechValue(){
        val mediaType = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, "{\"task_ids\":[\"65895bda61a2b60001609cdc\"]}")
        val request: Request = Request.Builder()
            .url("https://aip.baidubce.com/rpc/2.0/aasr/v1/query?access_token=" + getAccessToken())
            .method("POST", body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()
        val response = httpClient.newCall(request).execute()
        response.body()?.let { LogUtil.d(it.string()) }
    }

    private fun getSpeechTaskId(){
        val mediaType = MediaType.parse("application/json")
        val body = RequestBody.create(
            mediaType,
            "{\"speech_url\":\"http://36.99.136.199:8100/filestore/file/download/i/13101?extension=mp3&redirect=download\",\"format\":\"mp3\",\"pid\":80001,\"rate\":16000}"
        )
        val request: Request = Request.Builder()
            .url("https://aip.baidubce.com/rpc/2.0/aasr/v1/create?access_token=" + getAccessToken())
            .method("POST", body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()
        val response = httpClient.newCall(request).execute()
        response.body()?.let { LogUtil.d(it.string()) }
    }

    private fun getAccessToken(): String {
        val mediaType: MediaType? = MediaType.parse("application/x-www-form-urlencoded")
        val body = RequestBody.create(
            mediaType,
            "grant_type=client_credentials&client_id=SWjdz1WS8abbru4UloI4ypL4" + "&client_secret=iX5M2zdVD26BNORIfSGA9vObid9rZgHq"
        )
        val request: Request = Request.Builder()
            .url("https://aip.baidubce.com/oauth/2.0/token")
            .method("POST", body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()
        val response: Response = httpClient.newCall(request).execute()
        return response.body()?.let { JSONObject(it.string()).getString("access_token") }.let{ "" }
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
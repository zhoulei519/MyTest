package com.zhou.testlibrary.app

import android.app.Application
import android.content.Context
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk

class MyApplication : Application() {


    companion object {
        var myContext: Application? = null
        fun getContext(): Context {
            return myContext!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        myContext = this
        // 在调用TBS初始化、创建WebView之前进行如下配置
        val map = HashMap<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
    }


}
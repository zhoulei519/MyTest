package com.zhou.testlibrary.app

import android.app.Application
import android.content.Context

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
    }


}
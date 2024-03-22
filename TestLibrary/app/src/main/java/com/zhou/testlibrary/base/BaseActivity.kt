package com.zhou.testlibrary.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity




open class BaseActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun startActivity(clazz:Class<Any>){
        val intent = Intent()
        intent.setClass(this,clazz)
        startActivity(intent)
    }
}
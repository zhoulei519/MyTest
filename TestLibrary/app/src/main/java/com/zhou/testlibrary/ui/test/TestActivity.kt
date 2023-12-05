package com.zhou.testlibrary.ui.test

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.Button
import com.zhou.testlibrary.R
import com.zhou.testlibrary.base.BaseActivity


/**
 * @author: zl
 * @date: 2023/9/15
 */
class TestActivity :BaseActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val open = findViewById<Button>(R.id.btn_open)
        open.setOnClickListener {
            openFile()
        }
    }

    fun openDetail(){
        val intent = Intent.parseUri("intent://appstore/openAppDetail?appId=444#Intent;scheme=qianxin;launchFlags=0x4000000;end", Intent.URI_INTENT_SCHEME)
        startActivity(intent)
    }

    fun openLogin(){
        val intent = Intent()
        intent.action = "com.appstore.manager.login.action"
        intent.`package` = "com.zte.appstore.ui"
        startActivityForResult(intent,1)
    }

    fun openFile(){
        val uri =
            Uri.parse("content://com.android.externalstorage.documents/document/primary:Download")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
        startActivityForResult(intent, 1)
    }
}
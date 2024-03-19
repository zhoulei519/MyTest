package com.zhou.testlibrary.ui.test

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.text.TextUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.zhou.common.utils.glide.GlideUtil
import com.zhou.testlibrary.R
import com.zhou.testlibrary.base.BaseActivity


/**
 * @author: zl
 * @date: 2023/9/15
 */
class TestActivity :BaseActivity(){
    var tvPhone : TextView? = null
    lateinit var ivPic : ImageView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val open = findViewById<Button>(R.id.btn_open)
        tvPhone = findViewById(R.id.tv)
        ivPic = findViewById(R.id.iv_pic)
        open.setOnClickListener {
            openAppstore()
        }
        GlideUtil.showWithUrl("https://img1.baidu.com/it/u=2205810988,4283060315&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500",ivPic,0)
    }

    private fun openFile(){
        val uri =
            Uri.parse("content://com.android.externalstorage.documents/document/primary:Download")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
        startActivityForResult(intent, 1)
    }

    private fun openAppstore(){
        val intent = Intent()
        intent.action = "com.appstore.manager.login.action"
        intent.`package` = "com.zte.appstore.ui"
//        intent.putExtra("package_name",packageName)
        startActivityForResult(intent,101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!TextUtils.isEmpty(data?.getStringExtra("user_phone_num"))){
            tvPhone?.text = data?.getStringExtra("user_phone_num")
        }
    }
}
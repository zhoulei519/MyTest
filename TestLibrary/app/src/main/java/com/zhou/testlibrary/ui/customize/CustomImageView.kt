package com.zhou.testlibrary.ui.customize

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import com.zhou.common.utils.glide.GlideUtil
import com.zhou.testlibrary.R
import com.zhou.testlibrary.base.BaseActivity

class CustomImageView : BaseActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_image)
        val imageView = findViewById<ImageView>(R.id.iv_image)
        GlideUtil.showWithUrl("https://img1.baidu.com/it/u=834894649,3086306884&fm=253&fmt=auto&app=120&f=JPEG?w=800&h=1422",imageView)
    }
}
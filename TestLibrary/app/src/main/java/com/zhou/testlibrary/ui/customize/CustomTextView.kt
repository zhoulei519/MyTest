package com.zhou.testlibrary.ui.customize

import android.annotation.SuppressLint
import android.os.Bundle
import com.zhou.common.view.text.JustifyTextView
import com.zhou.testlibrary.base.BaseActivity
import com.zhou.testlibrary.R
import com.zhou.testlibrary.utils.LogUtil

class CustomTextView : BaseActivity(){
    private lateinit var tvShort: JustifyTextView
    private lateinit var tvLong: JustifyTextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_text)
        tvShort = findViewById(R.id.tv_short)
        tvLong = findViewById(R.id.tv_long)
        tvShort.setTitleWidth(tvLong)
        tvLong.setTitleWidth(tvLong)
    }
}
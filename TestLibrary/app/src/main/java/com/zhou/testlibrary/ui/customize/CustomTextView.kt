package com.zhou.testlibrary.ui.customize

import android.annotation.SuppressLint
import android.os.Bundle
import com.zhou.common.view.text.ExpandableTextView
import com.zhou.common.view.text.JustifyTextView
import com.zhou.testlibrary.base.BaseActivity
import com.zhou.testlibrary.R

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
        val tvExpand = findViewById<ExpandableTextView>(R.id.tv_expand)
        tvExpand.setText("我是很长的句子，需要用到折叠，要不然会很长影响页面美观度")

    }
}
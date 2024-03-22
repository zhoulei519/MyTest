package com.zhou.testlibrary.ui.customize

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import com.zhou.testlibrary.R
import com.zhou.testlibrary.base.BaseActivity

class CustomShowActivity : BaseActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_show)
        val customText = findViewById<TextView>(R.id.tv_custom_text)
        val customImage = findViewById<TextView>(R.id.tv_custom_image)
        customText.setOnClickListener {
            startActivity(CustomTextView().javaClass)
        }
        customImage.setOnClickListener {
            startActivity(CustomImageView().javaClass)
        }
    }
}
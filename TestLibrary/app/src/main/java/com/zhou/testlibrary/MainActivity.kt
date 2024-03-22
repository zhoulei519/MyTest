package com.zhou.testlibrary

import android.os.Bundle
import android.view.View
import com.zhou.testlibrary.base.BaseActivity
import com.zhou.testlibrary.ui.customize.CustomShowActivity

class MainActivity : BaseActivity() ,View.OnClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findView(R.id.tv_custom_view)
    }
    override fun onClick(view:View){
        when(view.id){
            R.id.tv_custom_view -> startActivity(CustomShowActivity().javaClass)
            else -> { // 注意这个块
                print("x 不是 1 "+ "也不是 2")
            }
        }
    }

    //函数的变长参数可以用 vararg 关键字进行标识
    private fun findView(id:Int){
        val view:View = findViewById(id)
        view.setOnClickListener(this)
    }
}
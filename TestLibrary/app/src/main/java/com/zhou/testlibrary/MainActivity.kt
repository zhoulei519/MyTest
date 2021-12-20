package com.zhou.testlibrary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.zhou.testlibrary.customize.CustomizeShow
import com.zhou.testlibrary.viewpager.ViewPagerActivity
import com.zhou.testlibrary.webview.OptimizeWebView

class MainActivity : BaseActivity() ,View.OnClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findView(R.id.btnViewPager,R.id.btnCustomize,R.id.btnWebView)
    }
    override fun onClick(view:View){
        when(view.id){
            R.id.btnViewPager -> startActivity(ViewPagerActivity().javaClass)
            R.id.btnCustomize -> startActivity(CustomizeShow().javaClass)
            R.id.btnWebView -> startActivity(OptimizeWebView().javaClass)
            else -> { // 注意这个块
                print("x 不是 1 "+ "也不是 2")
            }
        }
    }

    //函数的变长参数可以用 vararg 关键字进行标识
    private fun findView(vararg ids:Int){
        for(id in ids){
            val view:View = findViewById(id)
            view.setOnClickListener(this)
        }
    }

    private fun startActivity(clazz:Class<Any>){
        val intent = Intent()
        //获取intent对象
        intent.setClass(this,clazz)
        //intent.setClass(this,ViewPagerActivity().javaClass)
        //intent.setClass(this,ViewPagerActivity::class.java)
        // 获取class是使用::反射(那么问题来了,反射是个什么鬼?👻👻👻👻小白的悲哀啊,赶紧研究研究去)
        startActivity(intent)
    }
}
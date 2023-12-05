package com.zhou.testlibrary.ui.viewpager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.zhou.common.view.viewpager.DayLetterPageTransformer
import com.zhou.testlibrary.R

class ViewPagerActivity :AppCompatActivity() {
    private var data = ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)
        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager)
        val myAdapter = MyAdapter()
        data.add(1)
        data.add(2)
        data.add(3)
        myAdapter.setList(data)
        val dayLetterPageTransformer = DayLetterPageTransformer(this)
        dayLetterPageTransformer.setupViewPager2(viewPager2, R.id.viewpager2_page_position)
        viewPager2.setPageTransformer(dayLetterPageTransformer)

        viewPager2.adapter = myAdapter
        viewPager2.currentItem=myAdapter.itemCount/2+1
    }
}
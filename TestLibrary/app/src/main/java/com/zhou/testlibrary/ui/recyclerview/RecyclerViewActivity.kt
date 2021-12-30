package com.zhou.testlibrary.ui.recyclerview

import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.zhou.testlibrary.R
import com.zhou.testlibrary.base.BaseActivity

class RecyclerViewActivity :BaseActivity() {
    var rvUsers: SwipeRecyclerView? = null
    var rvUsers1: SideslipRecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        var rvUsers: SwipeRecyclerView = findViewById(R.id.rvUsers)
        var rvUsers1: SideslipRecyclerView = findViewById(R.id.rvUsers1)
        //设置管理器
        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers1.layoutManager = LinearLayoutManager(this)
        val adapter = TestAdapter()
        rvUsers.adapter = adapter
        rvUsers1.adapter = adapter

        rvUsers.visibility = ViewGroup.VISIBLE
//        rvUsers1.visibility = ViewGroup.VISIBLE
    }
}
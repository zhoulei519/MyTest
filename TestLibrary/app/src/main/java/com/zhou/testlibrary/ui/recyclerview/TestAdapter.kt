package com.zhou.testlibrary.ui.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zhou.testlibrary.R
import com.zhou.testlibrary.ui.viewpager.MyAdapter

class TestAdapter : RecyclerView.Adapter<TestAdapter.ViewHolder>() {

    class ViewHolder(itemView: View)  : RecyclerView.ViewHolder(itemView){

        val ll_item: LinearLayout = itemView.findViewById(R.id.ll_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_test, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 10
    }
}
package com.zhou.testlibrary.ui.viewpager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zhou.common.utils.glide.GlideUtil
import com.zhou.testlibrary.R

class MyAdapter : RecyclerView.Adapter<MyAdapter.PagerViewHolder>() {
    private var mList: List<Int> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false)
        return PagerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bindData(mList[position%mList.size])
        holder.itemView.setTag(R.id.viewpager2_page_position,position)
    }

    fun setList(list: List<Int>) {
        mList = list
    }

    override fun getItemCount(): Int {
        if (mList.size==1){
            return 1
        }
        return mList.size*200
    }
    //	ViewHolder需要继承RecycleView.ViewHolder
    class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mTextView: TextView = itemView.findViewById(R.id.viewpager2_page_position)
        private val ivImage: ImageView = itemView.findViewById(R.id.iv_image)
        private var images = arrayOf("https://img0.baidu.com/it/u=925843206,3288141497&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=769","https://img1.baidu.com/it/u=834894649,3086306884&fm=253&fmt=auto&app=120&f=JPEG?w=800&h=1422","https://i.bobopic.com/small/80469375.jpg","https://img0.baidu.com/it/u=925843206,3288141497&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=769")

        fun bindData(i: Int) {
            mTextView.text = i.toString()
            GlideUtil.showRoundedImg(images[i],ivImage,R.mipmap.ic_launcher)
        }
    }
}
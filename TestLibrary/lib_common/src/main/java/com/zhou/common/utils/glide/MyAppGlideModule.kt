package com.zhou.common.utils.glide

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory
import com.bumptech.glide.module.AppGlideModule

class MyAppGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // 配置磁盘缓存大小和位置
        val diskCacheSize = 1024 * 1024 * 500 // 500M
        val diskCacheDir = context.cacheDir.toString() + "/image_cache"
        // 创建磁盘缓存
        val diskCacheFactory = DiskLruCacheFactory(diskCacheDir, diskCacheSize.toLong())
        builder.setDiskCache(diskCacheFactory)
    }
}
package com.zhou.common.utils.glide

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.zhou.common.constant.SpKey
import com.zhou.common.utils.SpUtils

fun getGlideUrl(url: String): GlideUrl {
    return GlideUrl(url, LazyHeaders.Builder().addHeader("access_token", SpUtils.getString(SpKey.TOKEN).toString()).build())
}

package com.zhou.common.utils.glide

import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.zhou.common.utils.UIUtils
import java.io.File

object GlideUtil {
    fun showWithUrlRound(url: String, target: ImageView, error: Int, placeholder: Int, dp: Float) {
        val optionInto = RequestOptions()
            .transform(RoundedCorners(UIUtils.dp2Px(target.context,dp).toInt()))
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        Glide.with(target.context)
            .load(getGlideUrl(url))
            .placeholder(placeholder)
            .error(error).apply(optionInto).into(target)
    }
    fun showWithUrl(url: String, target: ImageView, error: Int, placeholder: Int) {
        val optionInto = RequestOptions()
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        Glide.with(target.context).load(getGlideUrl(url)).placeholder(placeholder).error(error).apply(optionInto).into(target)
    }


    fun showWithUrl(url: String, target: ImageView, error: Int = 0) {
        val optionInto = RequestOptions()
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        Glide.with(target.context).load(getGlideUrl(url)).error(error).apply(optionInto).into(target)
    }

    fun showWithFullUrl(url: String, target: ImageView, error: Int, placeholder: Int) {
        Glide.with(target.context).load(getGlideUrl(url)).placeholder(placeholder).error(error).into(target)
    }

    fun showWithRes(resId: Int, target: ImageView) {
        Glide.with(target.context).load(resId).into(target)
    }

    fun showWithPath(path: String, target: ImageView) {
        Glide.with(target.context).load(File(path)).into(target)
    }

    fun showRoundWithPath(path: String, target: ImageView) {
        Glide.with(target.context)
            .load(File(path))
            .transform(CenterCrop(), RoundedCorners(UIUtils.dp2Px(target.context,4f).toInt())).into(target)
    }

    fun showCircleWithPath(path: String, target: ImageView) {
        Glide.with(target.context).asBitmap().transform(CircleCrop()).load(File(path)).into(target)
    }

    fun showCircleImg(url: String, target: ImageView, error: Int) {
        val optionInto = RequestOptions()
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        Glide.with(target.context).asBitmap().error(error).transform(CircleCrop()).load(getGlideUrl(url)).apply(optionInto).into(target)
    }

    fun showRoundedImg(url: String, target: ImageView, error: Int ,radius: Int = UIUtils.dp2Px(target.context,4f).toInt()) {
        if(!TextUtils.isEmpty(url)){
            val optionInto = RequestOptions()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            Glide.with(target.context).asBitmap().error(error).transform(CenterCrop(), RoundedCorners(radius)).load(getGlideUrl(url)).apply(optionInto).into(target)
        }
    }

    fun showRoundedImg(target: ImageView ,error: Int,radius: Int = UIUtils.dp2Px(target.context,4f).toInt()){
        val optionInto = RequestOptions()
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        Glide.with(target.context).asBitmap().error(error).transform(CenterCrop(), RoundedCorners(radius)).load("").apply(optionInto).into(target)
    }


    fun showWithCorner(url: String, target: ImageView, error: Int, placeholder: Int,radius: Int = UIUtils.dp2Px(target.context,4f).toInt()) {
        if (TextUtils.isEmpty(url)) {
            return
        }
        val optionInto = RequestOptions()
            .transform(CenterCrop(),RoundedCorners(radius))
        Glide.with(target.context).load(getGlideUrl(url))
            .transform(CenterCrop(),RoundedCorners(radius))
            .placeholder(placeholder).error(error).apply(optionInto).into(target)
    }
}
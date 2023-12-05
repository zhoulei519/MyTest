package com.zhou.common.utils

import android.content.Context

object UIUtils {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dp2Px(context: Context?, dpValue: Float): Float {
        val scale = context?.resources?.displayMetrics?.density
        return (dpValue * scale!! + 0.5f)
    }

    /**
     * 获取屏幕宽
     */
    fun getScreenWidth(context: Context?): Float {
        var dm = context?.resources?.displayMetrics
        val screenWidth = dm?.widthPixels // 屏幕宽（像素）
        return screenWidth?.toFloat()!!
    }
}
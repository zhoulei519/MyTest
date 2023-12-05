package com.zhou.common.view.viewpager

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.zhou.common.utils.UIUtils


class DayLetterPageTransformer(context: Context) : ViewPager2.OnPageChangeCallback(), ViewPager2.PageTransformer {

    companion object {
        private val belowLollipop = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
    }

    private var curPosition = -1
    private var tag = -1
    private val translationYDelta = UIUtils.dp2Px(context, 10f)
    private val secondCardScale: Float
    private val thirdCardScale: Float

    init {

        val screenWidth = UIUtils.getScreenWidth(context)
        val dp12 = UIUtils.dp2Px(context, 12f)
        val secondCardWidth = screenWidth - dp12 * 2
        val thirdCardWidth = secondCardWidth - dp12 * 2
        secondCardScale = secondCardWidth / screenWidth
        thirdCardScale = thirdCardWidth / screenWidth
    }

    fun setupViewPager2(viewPager2: ViewPager2,tag:Int) {
        this.tag = tag
        if (belowLollipop) {
            return
        }
        viewPager2.setPageTransformer(this)
        viewPager2.registerOnPageChangeCallback(this)
        //需要同时展示三张，所以3-1=2
        viewPager2.offscreenPageLimit = 2
    }

    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        curPosition = position
        Log.d("zhou","page selected $position")
    }

    override fun transformPage(page: View, position: Float) {
        if (belowLollipop) {
            //没有动效
            return
        }
        //获取卡片位置
        val pos = page.getTag(tag) as Int

        //先将卡片重置
        resetPage(page)
        if (pos == curPosition - 1) {
            //当前卡片前一张
            page.translationY = translationYDelta * 2
            page.translationZ = 500f
        } else if (pos == curPosition) {
            //当前卡片
            page.translationZ = 400f
            val realPositionOffset = -position
            if (realPositionOffset > 0) {
                page.translationX = 0f
                page.translationY = translationYDelta * 2
            } else {
                page.translationX = realPositionOffset * page.width
                page.translationY = translationYDelta * 2 + translationYDelta * realPositionOffset
                val scale = 1f + (1f - secondCardScale) * realPositionOffset
                page.scaleX = scale
                page.scaleY = scale
            }
        } else if (pos == curPosition + 1) {
            //当前卡片后一张
            val realPositionOffset = -(position - 1)
            page.translationX = -1 * position * page.width
            page.translationY = translationYDelta + translationYDelta * realPositionOffset
            page.translationZ = 300f
            val scale = secondCardScale + (1f - secondCardScale) * realPositionOffset
            page.scaleX = scale
            page.scaleY = scale
        } else if (pos == curPosition + 2) {
            //当前卡片后二张
            val realPositionOffset = -(position - 2)
            page.translationX = -1 * position * page.width
            page.translationZ = 200f
            if (realPositionOffset < 0) {
                page.scaleX = thirdCardScale
                page.scaleY = thirdCardScale
                page.alpha = 1 + realPositionOffset
            } else {
                page.translationY = translationYDelta * realPositionOffset
                val scale = thirdCardScale + (secondCardScale - thirdCardScale) * realPositionOffset
                page.scaleX = scale
                page.scaleY = scale
            }
        } else if (pos == curPosition + 3) {
            //当前卡片后三张
            val realPositionOffset = -(position - 3)
            if (realPositionOffset > 0) {
                page.translationX = -1 * position * page.width
                page.translationY = 0f
                page.translationZ = 100f
                page.scaleX = thirdCardScale
                page.scaleY = thirdCardScale
                page.alpha = realPositionOffset
            }
        }
    }

    private fun resetPage(page: View) {
        if (!belowLollipop) {
            page.translationZ = 10f
        }
        page.translationX = 0f
        page.translationY = 0f
        page.pivotX = 0.5f * page.width
        page.pivotY = 0f
        page.scaleX = 1f
        page.scaleY = 1f
        page.alpha = 1f
    }
}
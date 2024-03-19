package com.zhou.testlibrary.ui.customize

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent




class MoveTextView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attributeSet, defStyleAttr) {

    private var downX = 0f
    private var downY = 0f
    private var moveX = 0f
    private var moveY = 0f
    private var lastTranslationX = 0f
    private var lastTranslationY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //按下的时候获取手指触摸的坐标
                downX = event.rawX
                downY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                //滑动时计算偏移量
                moveX = event.rawX - downX
                moveY = event.rawY - downY
                //随手指移动
                translationX = moveX + lastTranslationX
                translationY = moveY + lastTranslationY
            }
            MotionEvent.ACTION_UP -> {
                lastTranslationX = translationX
                lastTranslationY = translationY
            }
        }
        return true
    }

}
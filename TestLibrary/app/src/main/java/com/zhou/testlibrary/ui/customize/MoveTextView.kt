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

    private var mX = 0f
    private var mY = 0f
    var moveX = 0f
    var moveY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //按下的时候获取手指触摸的坐标
                mX = event.rawX
                mY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                //滑动时计算偏移量
                moveX = event.rawX - mX
                moveY = event.rawY - mY
                //随手指移动
                translationX = moveX
                translationY = moveY
            }
            MotionEvent.ACTION_UP -> {

            }
        }
        return true
    }

}
package com.zhou.common.view.text

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

class ClickRightTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatTextView(context, attrs){

    private var onClickRightListener: OnClickRightClickListener? = null

    interface OnClickRightClickListener {
        fun onClickRightClick()
    }

    fun setOnClickRightListener(onClickRightListener: OnClickRightClickListener) {
        this.onClickRightListener = onClickRightListener
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val drawableRight = compoundDrawables[2]
            //本次点击事件的x轴坐标，如果>当前控件宽度-控件右间距-drawable实际展示大小
            if (event.x >= width - paddingRight - drawableRight.intrinsicWidth) {
                //设置点击EditText右侧图标
                onClickRightListener?.onClickRightClick()
            }
        }
        return super.onTouchEvent(event)
    }
}
package com.zhou.testlibrary.ui.viewpager

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

//解决多指滑动问题
class DisableMultiPointerFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val actionMasked = ev.actionMasked
        if (actionMasked == MotionEvent.ACTION_POINTER_DOWN) {
            //MotionEvent.ACTION_POINTER_DOWN 非主要的手指按下，及在按下前已经有手指在屏幕上
            //不能拦截MotionEvent.ACTION_POINTER_UP，否则无法结束滑动
            return false
        }
        return super.dispatchTouchEvent(ev)
    }
}
package com.zhou.common.view.text

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import com.zhou.common.R

/**
 * @author: zhouLei
 * @date: 2023/7/11
 * 加粗TextView
 */
@SuppressLint("Recycle")
class BoldTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {
    /**
     * 数值越大，字体越粗，0.0f表示常规画笔的宽度，相当于默认情况
     */
    private var mStrokeWidth = 0.8f

    init {
        //获取xml定义属性
        val array = context.obtainStyledAttributes(attrs, R.styleable.BoldTextView, 0, 0)
        mStrokeWidth = array.getFloat(R.styleable.BoldTextView_stroke_width, mStrokeWidth)
    }

    override fun onDraw(canvas: Canvas) {
        //获取当前控件的画笔
        val paint = paint
        //设置画笔的描边宽度值
        paint.strokeWidth = mStrokeWidth
        paint.style = Paint.Style.FILL_AND_STROKE
        super.onDraw(canvas)
    }

    fun setStrokeWidth(mStrokeWidth: Float) {
        this.mStrokeWidth = mStrokeWidth
        invalidate()
    }

}
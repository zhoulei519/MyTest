package com.zhou.common.view.text

import android.content.Context
import android.graphics.Canvas
import android.text.StaticLayout
import android.util.AttributeSet
import android.widget.TextView

/**
 * @author: zhoulei03
 * @date: 2023/7/11
 * 设置同宽TextView
 */
class JustifyTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {
    private var mLineY: Int = 0
    private var mViewWidth: Float = 0f

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        val paint = paint
        paint.color = currentTextColor
        paint.drawableState = drawableState
        val text = text as String
        mLineY = 0
        mLineY += textSize.toInt()
        val width = StaticLayout.getDesiredWidth(text, 0, text.length, paint)
        drawScaledText(canvas, text, width)
        val fm = paint.fontMetrics
        val textHeight = fm.top.toInt()
        mLineY += textHeight
    }

    private fun drawScaledText(canvas: Canvas, line: String, lineWidth: Float) {
        var x = 0f
        val d = (mViewWidth - lineWidth) / (line.length - 1).toFloat()
        for (element in line) {
            val c = element.toString()
            val cw = StaticLayout.getDesiredWidth(c, paint)
            canvas.drawText(c, x, mLineY.toFloat(), paint)
            x += cw + d
        }
    }

    fun setTitleWidth(tv: TextView) {
        val text = tv.text.toString()
        val width = StaticLayout.getDesiredWidth(text, 0, text.length, tv.paint)
        mViewWidth = width
        setWidth(mViewWidth.toInt())
        invalidate()
    }
}
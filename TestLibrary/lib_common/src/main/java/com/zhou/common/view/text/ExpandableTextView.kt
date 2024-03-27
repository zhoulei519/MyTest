package com.zhou.common.view.text

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.SparseBooleanArray
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.zhou.common.R
import com.zhou.common.view.text.ClickRightTextView.OnClickRightClickListener

@SuppressWarnings("unused")
class ExpandableTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs){

    private var maxLines = 0
    private var secondTextLineStr = ""
    private var lastLineStr = "" //最后一行的内容


    private var showLines = 3
    private var text = ""
    private var textColor = 0
    private var textSize = 0f
    private var isExpand = false
    private var expandDrawable: Drawable? = null
    private var collapseDrawable: Drawable? = null
    private var mCollapsedStatus: SparseBooleanArray? = null
    private var mPosition = 0
    private val listener: OnExpandListener? = null
    private var tvFirst: TextView? = null
    private var tvSecond: ClickRightTextView? = null

    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.view_expand_text, this)
        tvFirst = view.findViewById(R.id.tv_first)
        tvSecond = view.findViewById(R.id.tv_second)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView)

        text = typedArray.getString(R.styleable.ExpandableTextView_expandText).toString()
        textColor = typedArray.getColor(
            R.styleable.ExpandableTextView_expandTextColor,
            ContextCompat.getColor(context, R.color.color_1D2129_30)
        )
        setTextColor(textColor)
        textSize = typedArray.getDimension(
            R.styleable.ExpandableTextView_expandTextSize,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                14f,
                context.resources.displayMetrics
            )
        )
        setTextSize(textSize)
        showLines = typedArray.getInteger(R.styleable.ExpandableTextView_showLines, 2)
        if (showLines < 1) {
            showLines = 1
        }
        isExpand = typedArray.getBoolean(R.styleable.ExpandableTextView_isExpand, false)

        expandDrawable = typedArray.getDrawable(R.styleable.ExpandableTextView_myExpandDrawable)
        collapseDrawable =
            typedArray.getDrawable(R.styleable.ExpandableTextView_myCollapseDrawable)

        if (expandDrawable == null) {
            expandDrawable = ContextCompat.getDrawable(context, R.drawable.icon_arrow_down)
        }
        if (expandDrawable != null) {
            expandDrawable!!.setBounds(
                0,
                0,
                expandDrawable!!.intrinsicWidth,
                expandDrawable!!.intrinsicHeight
            )
        }
        if (collapseDrawable == null) {
            collapseDrawable = ContextCompat.getDrawable(context, R.drawable.icon_arrow_up)
        }
        if (collapseDrawable != null) {
            collapseDrawable!!.setBounds(
                0,
                0,
                collapseDrawable!!.intrinsicWidth,
                collapseDrawable!!.intrinsicHeight
            )
        }
        setText(text)

        tvSecond?.setOnClickRightListener(object : OnClickRightClickListener {
            override fun onClickRightClick() {
                if (mCollapsedStatus != null) {
                    mCollapsedStatus!!.put(mPosition, !isExpand)
                }
                setExpand(!isExpand)
            }
        })

        typedArray.recycle()
    }

    /**
     * 计算行数
     */
    private fun initLines() {
        //将文本内容全部给tvFirst，方便后面计算行数，获取每行的文本内容
        tvFirst!!.text = text
        tvFirst!!.post {

            //绘制时获取tvFirst实际总行数，直接获取返回的是0
            maxLines = tvFirst!!.lineCount
            //最大行数小于等于要显示的行数
            if (maxLines <= showLines) {
                //tvSecond隐藏，只显示tvFirst
                tvSecond!!.visibility = GONE
                tvFirst!!.setLines(tvFirst!!.lineCount)
            } else {
                tvSecond!!.visibility = VISIBLE
                //获取tvFirst显示布局，可根据改布局获取文本中每行的开始位置和结束位置
                val layout = tvFirst!!.layout
                if (isExpand) { //展开
                    //获取展开最后一行的内容，展开时给tvSecond显示
                    lastLineStr = text.substring(
                        layout.getLineStart(maxLines - 1),
                        layout.getLineEnd(maxLines - 1)
                    )
                    //设置tvFirst的行数=最大行数-1
                    tvFirst!!.setLines(maxLines - 1)
                    //设置tvSecond的行数控制其显示内容，此处设置为maxLines=2是因为可能存在：
                    //最后一行的内容刚好完全填满，而tvSecond由于右边drawableRight和drawablePadding的存在所以可能显示不全，只能加多一行显示。
                    tvSecond!!.maxLines = 2
                    tvSecond!!.text = lastLineStr
                } else {
                    //设置tvFirst的行数=折叠时显示的行数-1
                    tvFirst!!.setLines(showLines - 1)
                    val start = layout.getLineStart(showLines - 1)
                    val end = layout.getLineEnd(showLines - 1)
                    secondTextLineStr = text.substring(start, end)
                    //获取折叠时最后一行的内容，给tvSecond显示
                    tvSecond!!.maxLines = 1
                    tvSecond!!.text = secondTextLineStr
                }
            }
        }
    }

    private fun setTextColor(textColor: Int) {
        this.textColor = textColor
        tvFirst!!.setTextColor(textColor)
        tvSecond!!.setTextColor(textColor)
    }

    private fun setTextSize(textSize: Float) {
        this.textSize = textSize
        tvFirst!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        tvSecond!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
    }

    /**
     * 普通使用赋值文本内容
     */
    fun setText(string: String?) {
        this.setText(string, isExpand)
    }

    private fun setText(string: String?, isExpand: Boolean) {
        text = string!!
        setExpand(isExpand)
    }

    /**
     * 在列表中使用赋值文本内容请使用此方法
     * 用SparseBooleanArray记录列表中展开的位置，防止错乱
     */
    fun setText(string: String?, collapsedStatus: SparseBooleanArray, position: Int) {
        mCollapsedStatus = collapsedStatus
        mPosition = position
        val isCollapsed = collapsedStatus[position, false]
        this.setText(string, isCollapsed)
    }

    private fun setExpand(isExpand: Boolean) {
        if (isExpand) {
            tvSecond!!.setCompoundDrawables(null, null, collapseDrawable, null)
        } else {
            tvSecond!!.setCompoundDrawables(null, null, expandDrawable, null)
        }
        if (this.isExpand != isExpand) {
            listener?.onExpandChange(isExpand)
            this.isExpand = isExpand
        }
        initLines()
    }
    interface OnExpandListener {
        fun onExpandChange(isExpand: Boolean?)
    }

}
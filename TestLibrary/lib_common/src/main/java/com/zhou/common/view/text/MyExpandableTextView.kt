package com.zhou.common.view.text

import android.content.Context
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.zhou.common.R
import java.util.regex.Pattern

/**
 * @author: zl
 * @date: 2023/9/15
 */
//object修饰为静态类
object  KotlinUtilsText {
    var AllTextSpannableStr = SpannableStringBuilder()

    /**
     * 控制特殊的点击事件
     *
     * @param context          上下文
     * @param maxLine          缩放行数，如果你的实际内容行数小于该值，则一直是全显状态，
     * @param endText          最后的那个 “显示全文”  文本
     * @param endTextColor     最后的那个 “显示全文”  文本颜色
     * @param isExpand         设置默认是展开还是收缩
     * @param textView         传递进来的view控件
     * @param originText       所有的全部的文本
     * @param specialTextColor 特殊文本的字体颜色
     * @param normalTextColor  普通文本的字体颜色
     * @param specialClickBack 特殊文本的点击回调，做你自己的处理
     */
    fun HandleSpecialClick(
        context: Context, maxLine: Int, endText: String,endTextColor:Int, isExpand: Boolean,
        textView: TextView, originText: String, specialTextColor: Int, normalTextColor: Int, specialClickBack: SpecialClickBack
    ) {
        AllTextSpannableStr.clear()
        //处理@ ##话题 url的特殊功能
        val modelSpecialList: MutableList<KotlinModelSpecial> = ArrayList()
        //普通的list的内容
        val modelNormalList: MutableList<KotlinModelNormal> = ArrayList()
        // 正则表达式  如果你有替他的正则匹配则可以在这继续添加
        val ruleStr: MutableList<String> = ArrayList()
        //匹配网址
        ruleStr.add("http[s]://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
        //拼接正则 利用()和 | 拼接后可以while一次 使用的是matcher.group()的知识
        val stringBuilder = StringBuilder()
        //        String ss="|(";
        for (i in ruleStr.indices) {
            if (i == ruleStr.size - 1) {
                stringBuilder.append("(").append(ruleStr[i]).append(")")
            } else {
                stringBuilder.append("(").append(ruleStr[i]).append(")|")
            }
        }
        Log.e("TAG", "HandleSpecialClick: $stringBuilder")
        // 编译正则表达式  Pattern处理表达式 Matcher来匹配支持
        val pattern = Pattern.compile(stringBuilder.toString())
        val m = pattern.matcher(textView.text.toString())
        while (m.find()) {
            if (modelSpecialList.size == 0) {
                val modelNormal = KotlinModelNormal(0, m.start())
                modelNormalList.add(modelNormal)
            } else {
                val modelNormal = KotlinModelNormal(modelSpecialList[modelSpecialList.size - 1].endPosition, m.start())
                modelNormalList.add(modelNormal)
            }
            //如果上方的正则表达式增加了，这也要跟着增加
            val modelSpecial1 = KotlinModelSpecial(1, m.group(1), m.start(), m.end())
            val modelSpecial2 = KotlinModelSpecial(2, m.group(2), m.start(), m.end())
            val modelSpecial3 = KotlinModelSpecial(3, m.group(3), m.start(), m.end())
            //正则匹配的为空的不添加
            if (modelSpecial1.txt != null) {
                modelSpecialList.add(modelSpecial1)
            }
            if (modelSpecial2.txt != null) {
                modelSpecialList.add(modelSpecial2)
            }
            if (modelSpecial3.txt != null) {
                modelSpecialList.add(modelSpecial3)
            }
        }
        //最后再添加普通尾部的文本
        val modelNormal = KotlinModelNormal(modelSpecialList[modelSpecialList.size - 1].endPosition, originText.length)
        modelNormalList.add(modelNormal)
        val spannableStr = SpannableStringBuilder(textView.text.toString())
        //普通文本的点击事件
        for (i in modelNormalList.indices) {
            spannableStr.setSpan(KotlinSpecialClickable(context, specialTextColor, normalTextColor, false) {
                //进行展开和收缩
                val isExpand = BooleanArray(1)
                //判断是展开状态还是收缩状态，isExpand不同的赋值，这样封装的彻底一些
                //之所以是小于等于 最小值+1  举例：是因为小空发现在某些设备上命名看着是三行，但是getLineCount却是4
                if (textView.lineCount <= maxLine + 1) {
                    isExpand[0] = true
                    textView.maxLines = Int.MAX_VALUE // 展开
                } else {
                    textView.maxLines = maxLine
                    isExpand[0] = false
                }
                Log.e("TAG", "onClick: 触发了非特殊文本需要展开和收缩" + textView.lineCount + isExpand[0])
                textView.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        if (isExpand[0]) {
                            //需要展开
                            textView.text = AllTextSpannableStr
                        } else {
                            val paddingLeft = textView.paddingLeft
                            val paddingRight = textView.paddingRight
                            val paint = textView.paint
                            //之所以加2 是因为下方最后缩减的时候中英文混合的话测量的不准确，导致bug，加3后在下方计算会多减去点尺寸，看起来又舒服，又解决bug
                            //这也是因为上方的判断是 getLineCount() <= maxLine + 1 的部分原因
                            //不信 你试试  (•́へ•́╬)ヽ(ー_ー)ノ
                            val moreText = textView.textSize * (endText.length + 3)
                            val availableTextWidth = (textView.width - paddingLeft - paddingRight) * maxLine - moreText
                            //TextUtils.ellipsize 获取指定范围的文字  如果超过指定范围 则用省略号，省略号的位置就是 TextUtils.TruncateAt 来决定的，没错这玩意就是布局xml中的ellipsize属性
                            val ellipsizeStr = SpannableStringBuilder(TextUtils.ellipsize(spannableStr, paint, availableTextWidth, TextUtils.TruncateAt.END))
                            if (ellipsizeStr.length < originText.length) {
                                val temp = ellipsizeStr.append(endText)
                                //设置最后展示文本的颜色
                                temp.setSpan(object : ClickableSpan() {
                                    override fun onClick(widget: View) {
                                        Log.e("TAG", "onClick: 点击了显示全文")
                                        isExpand[0] = true
                                        textView.maxLines = Int.MAX_VALUE // 展开
                                        textView.text = AllTextSpannableStr
                                    }

                                    @RequiresApi(Build.VERSION_CODES.M)
                                    override fun updateDrawState(ds: TextPaint) {
                                        super.updateDrawState(ds)
                                        ds.color = context.resources.getColor(endTextColor, null)
                                        //去掉下划线
                                        ds.isUnderlineText = false
                                    }
                                }, temp.length - endText.length, temp.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                                textView.text = temp
                            }
                        }
                        //芝麻粒友情提示：如果你的项目sdk版本小于16，也就是4.1系统 这的移除使用的是removeGlobalOnLayoutListener
                        //不过2022年了，相信旧版本已经成为了历史
                        textView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }, modelNormalList[i].startPosition, modelNormalList[i].endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        for (modelSpecial in modelSpecialList) {
//            spannableStr.setSpan(specialClickable,modelSpecial.getStartPosition(), modelSpecial.getEndPosition(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStr.setSpan(KotlinSpecialClickable(context, specialTextColor, normalTextColor, true) { //每个 @用户名 字符串的点击事件
                specialClickBack.onSpecialClick(modelSpecial.modelType, modelSpecial.txt)
            }, modelSpecial.startPosition, modelSpecial.endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        //默认是展开还是收缩
        if (!isExpand) {
            textView.maxLines = maxLine
            textView.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val paddingLeft = textView.paddingLeft
                    val paddingRight = textView.paddingRight
                    val paint = textView.paint
                    //之所以加2 是因为下方最后缩减的时候中英文混合的话测量的不准确，导致bug，加2后在下方计算会多减去点尺寸，看起来又舒服，又解决bug
                    //不信 你试试  (•́へ•́╬)ヽ(ー_ー)ノ
                    val moreText = textView.textSize * (endText.length + 2)
                    val availableTextWidth = (textView.width - paddingLeft - paddingRight) * maxLine - moreText
                    //TextUtils.ellipsize 获取指定范围的文字  如果超过指定范围 则用省略号，省略号的位置就是 TextUtils.TruncateAt 来决定的，没错这玩意就是布局xml中的ellipsize属性
                    val ellipsizeStr = SpannableStringBuilder(TextUtils.ellipsize(spannableStr, paint, availableTextWidth, TextUtils.TruncateAt.END))
                    if (ellipsizeStr.length < originText.length) {
                        val temp = ellipsizeStr.append(endText)
                        //设置最后展示文本的颜色
                        temp.setSpan(object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                Log.e("TAG", "onClick: 点击了显示全文")
                                textView.maxLines = Int.MAX_VALUE // 展开
                                textView.text = AllTextSpannableStr
                            }

                            @RequiresApi(Build.VERSION_CODES.M)
                            override fun updateDrawState(ds: TextPaint) {
                                super.updateDrawState(ds)
                                ds.color = context.resources.getColor(endTextColor, null)
                                //去掉下划线
                                ds.isUnderlineText = false
                            }
                        }, temp.length - endText.length, temp.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                        textView.text = temp
                    } else {
                        textView.text = AllTextSpannableStr
                    }
                    //芝麻粒友情提示：如果你的项目sdk版本小于16，也就是4.1系统 这的移除使用的是removeGlobalOnLayoutListener
                    //不过2022年了，相信旧版本已经成为了历史
                    textView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
        //变量内容赋值
        AllTextSpannableStr = spannableStr
        textView.text = spannableStr
        //激活超文本的点击事件
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    interface SpecialClickBack {
        fun onSpecialClick(modelType: Int, clickStr: String?)
    }
}

/**
 * Created by akitaka on 2022-01-07.
 *
 * @author akitaka
 * @filename
 * @describe 特殊内容的点击事件处理
 */
internal class KotlinSpecialClickable(
    private val context: Context,
    private val specialTextColor: Int,
    private val normalTextColor: Int,
    private val isSpecial: Boolean,
    private val mListener: View.OnClickListener
) :
    ClickableSpan(), View.OnClickListener {
    override fun onClick(v: View) {
        //方法重新设置文字背景为透明色。
        (v as TextView).highlightColor = context.resources.getColor(R.color.design_default_color_surface)
        mListener.onClick(v)
    }

    //设置显示样式
    @RequiresApi(Build.VERSION_CODES.M)
    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        //特殊文本的颜色
        if (isSpecial) {
            ds.color = context.resources.getColor(specialTextColor, null) //设置颜色
            //设置下划线 默认是true
            ds.isUnderlineText = false
        } else {
            //非特殊文本的颜色
            ds.color = context.resources.getColor(normalTextColor, null) //设置颜色
            ds.isUnderlineText = false
        }
    }
}

/**
 * Created by akitaka on 2022-01-19.
 *
 * @author akitaka
 * @filename KotlinModelNormal
 * @describe 普通文本的
 */
internal class KotlinModelNormal(//开始的位置
    var startPosition: Int, //结束的位置
    var endPosition: Int
) {

    override fun toString(): String {
        return "KotlinModelNormal{" +
                "startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                '}'
    }

    init {
        endPosition = endPosition
    }
}

/**
 * Created by akitaka on 2022-01-07.
 *
 * @author akitaka
 * @filename
 * @describe 特殊文本实体类 @用户  #话题  url等
 */
internal class KotlinModelSpecial(modelType: Int, txt: String, startPosition: Int, endPosition: Int) {
    /**
     * 1是用户
     * 2是话题
     * 3是url
     */
    var modelType: Int

    //文字
    var txt: String

    //开始的位置
    var startPosition: Int

    //结束的位置
    var endPosition: Int

    override fun toString(): String {
        return "KotlinModelSpecial{" +
                "txt='" + txt + '\'' +
        ", startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                '}'
    }

    init {
        this.modelType = modelType
        this.txt = txt
        this.startPosition = startPosition
        this.endPosition = endPosition
    }
}

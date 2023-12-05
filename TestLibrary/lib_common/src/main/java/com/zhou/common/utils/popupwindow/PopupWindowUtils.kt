package com.zhou.common.utils.popupwindow

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat

/**
 * @author: zhoulei03
 * @date: 2023/6/15
 */
class PopupWindowUtils(private val context: Context) {
    private val popupWindow by lazy(LazyThreadSafetyMode.NONE) {
        PopupWindow(context)
    }

    private var popupWidth: Int? = null
    private var popupHeight: Int? = null
    private var backgroundDrawableId: Int? = null

    /**
     * 设置 PopupWindow 的布局视图。
     */
    fun setContentView(view: View) {
        popupWindow.contentView = view
    }

    /**
     * 设置 PopupWindow 的宽度和高度，如果不设置，则默认为包含内容的大小。
     */
    fun setSize(width: Int, height: Int) {
        popupWidth = width
        popupHeight = height
    }

    /**
     * 设置 PopupWindow 的背景。如果没有设置，则默认为透明。
     */
    fun setBackground(backgroundDrawableId: Int) {
        this.backgroundDrawableId = backgroundDrawableId
    }

    /**
     * 显示 PopupWindow，并位于相对于 parentView 指定的位置。可以指定 x 和 y 偏移量。
     */
    fun showAsDropDown(parentView: View, xOff: Int = 0, yOff: Int = 0) {
        // 如果未设置 PopupWindow 的宽度和高度，则将其设置为包含内容的大小。
        if (popupWidth == null || popupHeight == null) {
            popupWindow.width = ViewGroup.LayoutParams.WRAP_CONTENT
            popupWindow.height = ViewGroup.LayoutParams.WRAP_CONTENT
        } else {
            popupWindow.width = popupWidth!!
            popupWindow.height = popupHeight!!
        }

        // 设置 PopupWindow 的背景。
        if (backgroundDrawableId != null) {
            popupWindow.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    context,
                    backgroundDrawableId!!
                )
            )
        } else {
            popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        // 显示 PopupWindow 并设置相应的偏移量。
        popupWindow.showAsDropDown(parentView, xOff, yOff)
    }

    /**
     * 关闭 PopupWindow。
     */
    fun dismiss() {
        popupWindow.dismiss()
    }
}
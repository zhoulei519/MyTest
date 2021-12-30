package com.zhou.testlibrary.ui .recyclerview

import android.R.attr
import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import android.view.MotionEvent

import android.view.View

import android.view.VelocityTracker
import android.graphics.Rect
import android.view.ViewConfiguration
import android.widget.LinearLayout
import kotlin.math.abs
import android.widget.Scroller

import com.zhou.testlibrary.R





class SideslipRecyclerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attributeSet, defStyleAttr) {

    /**
     * 当前点击的view
     */
    var mFlingView: View? = null

    /**
     * 展开的view
     */
    private var view: View? = null

    /**
     * 用于获取子View的Bound
     */
    private val frame = Rect()

    /**
     * 菜单部分宽度
     */
    private var mMenuViewWidth:Int = 0

    /**
     * 最小速度阀值
     */
    private val SNAP_VELOCITY:Float = 50f

    /**
     * 最小滑动距离
     */
    private val mTouchSlop:Int = ViewConfiguration.get(context).scaledTouchSlop

    /**
     * 是否是测滑
     */
    private var mIsSlide:Boolean = false;


    private val mScroller:Scroller = Scroller(context)

    /**
     * 点击的xy
     */
    private var mFirstX: Float = 0f
    private var mFirstY: Float = 0f

    /**
     * VelocityTracker用于触摸事件的速度追踪
     */
    private var mVelocityTracker: VelocityTracker = VelocityTracker.obtain()

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN ->{
                mFirstX = event.x
                mFirstY = event.y
                //获取点击的view
                mFlingView = getChildAt(getFlingViewIndex(event))
                //获取菜单的宽度
                val mDelete = mFlingView?.findViewById(R.id.ll_hidden) as LinearLayout
                mMenuViewWidth = mDelete.width

                // 已经有ItemView处于展开状态，并且这次点击的对象不是已打开的那个ItemView
                if (view != null && mFlingView !== view && view!!.scaleX !== 0f) {
                    // 将已展开的ItemView关闭
                    view!!.scrollTo(0, 0)
                    // 则拦截事件
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                mVelocityTracker.computeCurrentVelocity(1000)
                // 此处有俩判断，满足其一则认为是侧滑：
                // 1.如果x方向速度大于y方向速度，且大于最小速度限制；
                // 2.如果x方向的侧滑距离大于y方向滑动距离，且x方向达到最小滑动距离；
                val xVelocity = mVelocityTracker.xVelocity
                val yVelocity = mVelocityTracker.yVelocity
                if (abs(xVelocity) > SNAP_VELOCITY && abs(xVelocity) > abs(yVelocity)
                    || abs(event.x - mFirstX) >= mTouchSlop
                    && abs(event.x - mFirstX) > abs(event.y - mFirstY)
                ) {
                    mIsSlide = true
                    return true
                }
            }
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        obtainVelocity(event)
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx: Float = mFirstX - event.x

                // 判断边界
                if (mFlingView!!.scrollX + dx <= mMenuViewWidth && mFlingView!!.scrollX + dx > 0) {
                    // 随手指滑动
                    mFlingView!!.scrollBy(dx.toInt(), 0)
                }
            }
            MotionEvent.ACTION_UP -> {
                val scrollX = mFlingView!!.scrollX
                mVelocityTracker.computeCurrentVelocity(1000)

                if (mVelocityTracker.xVelocity < -SNAP_VELOCITY) {    // 向左侧滑，达到侧滑最低速度，则打开
                    // 计算剩余要移动的距离
                    val delt = abs(mMenuViewWidth - scrollX)
                    // 根据松手时的速度计算要移动的时间
                    val t = (delt / mVelocityTracker.xVelocity * 1000).toInt()
                    // 移动
                    mScroller.startScroll(
                        scrollX,
                        0,
                        mMenuViewWidth - scrollX,
                        0,
                        abs(t)
                    )
                    view = mFlingView
                } else if (mVelocityTracker.xVelocity >= SNAP_VELOCITY) {  // 向右侧滑达到侧滑最低速度，则关闭
                    mScroller.startScroll(scrollX, 0, -scrollX, 0, abs(scrollX))
                } else if (scrollX >= mMenuViewWidth / 2) { // 如果超过删除按钮一半，则打开
                    mScroller.startScroll(
                        scrollX,
                        0,
                        mMenuViewWidth - scrollX,
                        0,
                        abs(mMenuViewWidth - scrollX)
                    )
                    view = mFlingView
                } else {    // 其他情况则关闭
                    mScroller.startScroll(scrollX, 0, -scrollX, 0, abs(scrollX))
                }
                invalidate()
                releaseVelocity() // 释放追踪
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getFlingViewIndex(event: MotionEvent): Int {
        for (i in childCount - 1 downTo 0) {
            val child = getChildAt(i)
            if (child.visibility == VISIBLE) {
                // 获取子view的bound
                child.getHitRect(frame)
                // 判断触摸点是否在子view中
                if (frame.contains(event.x.toInt(), event.y.toInt())) {
                    return i
                }
            }
        }
        return 0
    }

    private fun obtainVelocity(e: MotionEvent) {
        mVelocityTracker.addMovement(e)
    }

    private fun releaseVelocity() {
        mVelocityTracker.clear()
        mVelocityTracker.recycle()

    }
}
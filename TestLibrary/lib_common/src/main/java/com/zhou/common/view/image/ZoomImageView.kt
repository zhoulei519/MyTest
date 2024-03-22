package com.zhou.common.view.image

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.AccelerateInterpolator
import android.widget.OverScroller
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * 可以放大缩小的ImageView
 */
class ZoomImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    androidx.appcompat.widget.AppCompatImageView(context, attrs),
    OnGlobalLayoutListener {

    private var mIsOneLoad = true

    //初始化的比例
    private var mInitScale = 0f

    //最小比例
    private var mMinScale = 0f

    //图片最大比例
    private var mMaxScale = 0f

    //双击能达到的最大比例
    private var mMidScale = 0f
    private var mScaleMatrix: Matrix? = null

    //捕获用户多点触控
    private var mScaleGestureDetector: ScaleGestureDetector? = null

    //移动
    private var gestureDetector: GestureDetector? = null

    //双击
    private var isEnlarge = false //是否放大
    private var mAnimator: ValueAnimator? = null //双击缩放动画

    //滚动
    private var scroller: OverScroller? = null
    private var mCurrentX = 0
    private var mCurrentY: Int = 0
    private var translationAnimation: ValueAnimator? = null //惯性移动动画

    //单击
    private var onClickListener: OnClickListener? = null //单击监听

    init {
        //记住，一定要把ScaleType设置成ScaleType.MATRIX，否则无法缩放
        scaleType = ScaleType.MATRIX

        scroller = OverScroller(context)
        mScaleMatrix = Matrix()
        //手势缩放
        mScaleGestureDetector =
            ScaleGestureDetector(context, object : SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    scale(detector)
                    return true
                }

                override fun onScaleEnd(detector: ScaleGestureDetector) {
                    scaleEnd(detector)
                }
            })
        //滑动和双击监听
        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                //滑动监听
                onTranslationImage(-distanceX, -distanceY)
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                //双击监听
                onDoubleDrawScale(e.x, e.y)
                return true
            }

            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                //滑动惯性处理
                mCurrentX = e2.x.toInt()
                mCurrentY = e2.y.toInt()
                val rectF = getMatrixRectF() ?: return false
                //startX为当前图片左边界的x坐标
                val startX = mCurrentX
                val startY = mCurrentY
                val vX = velocityX.roundToInt()
                val vY = velocityY.roundToInt()
                val maxX: Int = rectF.width().roundToInt()
                val maxY: Int = rectF.height().roundToInt()
                if (startX != maxX || startY != maxY) {
                    //调用fling方法，然后我们可以通过调用getCurX和getCurY来获得当前的x和y坐标
                    //这个坐标的计算是模拟一个惯性滑动来计算出来的，我们根据这个x和y的变化可以模拟出图片的惯性滑动
                    scroller?.fling(startX, startY, vX, vY, 0, maxX, 0, maxY, maxX, maxY)
                }
                if (translationAnimation != null && translationAnimation!!.isStarted)
                    translationAnimation!!.end()
                translationAnimation = ObjectAnimator.ofFloat(0f, 1f)
                translationAnimation?.setDuration(500)
                translationAnimation?.addUpdateListener {
                    if (scroller != null && scroller!!.computeScrollOffset()) {
                        //获得当前的x坐标
                        val newX = scroller!!.currX
                        val dx = newX - mCurrentX
                        mCurrentX = newX
                        //获得当前的y坐标
                        val newY = scroller!!.currY
                        val dy = newY - mCurrentY
                        mCurrentY = newY
                        //进行平移操作
                        if (dx != 0 && dy != 0) onTranslationImage(dx.toFloat(), dy.toFloat())
                    }
                }
                translationAnimation?.start()
                return super.onFling(e1, e2, velocityX, velocityY)
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                //单击事件
                onClickListener?.onClick(this@ZoomImageView)
                return true
            }
        })
    }

    override fun setOnClickListener(onClickListener: OnClickListener?) {
        this.onClickListener = onClickListener
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        if (mIsOneLoad) {
            //得到控件的宽和高
            val width = width
            val height = height
            //获取图片,如果没有图片则直接退出
            val d = drawable ?: return
            //获取图片的宽和高
            val dw = d.intrinsicWidth
            val dh = d.intrinsicHeight
            var scale = 1.0f
            if (dw > width && dh <= height) {
                scale = width * 1.0f / dw
            }
            if (dw <= width && dh > height) {
                scale = height * 1.0f / dh
            }
            if (dw <= width && dh <= height || dw >= width && dh >= height) {
                scale = min(width * 1.0f / dw, height * 1.0f / dh)
            }
            //最小比例
            mMinScale = scale / 2
            //图片原始比例，图片回复原始大小时使用
            mInitScale = scale
            //图片双击后放大的比例
            mMidScale = scale * 2
            //手势放大时最大比例
            mMaxScale = scale * 4
            //设置移动数据,把改变比例后的图片移到中心点
            val translationX = width * 1.0f / 2 - dw / 2f
            val translationY = height * 1.0f / 2 - dh / 2f
            mScaleMatrix?.postTranslate(translationX, translationY)
            mScaleMatrix?.postScale(mInitScale, mInitScale, width * 1.0f / 2, height * 1.0f / 2)
            imageMatrix = mScaleMatrix
            mIsOneLoad = false
        }
    }

    //手势操作（缩放）
    fun scale(detector: ScaleGestureDetector) {
        drawable ?: return
        //获取手势操作的值,scaleFactor>1说明放大，<1则说明缩小
        val scaleFactor = detector.scaleFactor
        //获取手势操作后的比例，当放操作后比例在[mMinScale,mMaxScale]区间时允许放大
        mScaleMatrix!!.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
        imageMatrix = mScaleMatrix
        removeBorderAndTranslationCenter()
    }

    //手势操作结束
    fun scaleEnd(detector: ScaleGestureDetector) {
        var scale: Float = getScale()
        scale *= detector.scaleFactor
        if (scale < mMinScale) {
            scaleAnimation(mMinScale, width / 2f, height / 2f)
        } else if (scale > mMaxScale) {
            scaleAnimation(mMaxScale, width / 2f, height / 2f)
        }
    }

    //手势操作（移动）
    private fun onTranslationImage(dx: Float, dy: Float) {
        var moveX = dx
        var moveY = dy
        if (drawable == null) return
        val rect: RectF? = getMatrixRectF()

        //图片宽度小于控件宽度时不允许左右移动
        if (rect != null && rect.width() <= width) moveX = 0.0f
        //图片高度小于控件宽度时，不允许上下移动
        if (rect != null && rect.height() <= height) moveY = 0.0f

        //移动距离等于0，那就不需要移动了
        if (moveX == 0.0f && moveY == 0.0f) return
        mScaleMatrix!!.postTranslate(moveX, moveY)
        imageMatrix = mScaleMatrix
        //去除移动边界
        removeBorderAndTranslationCenter()
    }

    //消除控件边界和把图片移动到中间
    private fun removeBorderAndTranslationCenter() {
        val rectF: RectF = getMatrixRectF() ?: return
        val width = width
        val height = height
        val widthF = rectF.width()
        val heightF = rectF.height()
        val left = rectF.left
        val right = rectF.right
        val top = rectF.top
        val bottom = rectF.bottom
        var translationX = 0.0f
        var translationY = 0.0f
        val centerTranslationX = width * 1.0f / 2f - (widthF / 2f + left)
        if (left > 0) {
            //左边有边界
            translationX = if (widthF > width) {
                //图片宽度大于控件宽度，移动到左边贴边
                -left
            } else {
                //图片宽度小于控件宽度，移动到中间
                centerTranslationX
            }
        } else if (right < width) {
            //右边有边界
            translationX = if (widthF > width) {
                //图片宽度大于控件宽度，移动到右边贴边
                width - right
            } else {
                //图片宽度小于控件宽度，移动到中间
                centerTranslationX
            }
        }
        val centerTranslationY = height * 1.0f / 2f - (top + heightF / 2f)
        if (top > 0) {
            //顶部有边界
            translationY = if (heightF > height) {
                //图片高度大于控件高度，去除顶部边界
                -top
            } else {
                //图片高度小于控件宽度，移动到中间
                centerTranslationY
            }
        } else if (bottom < height) {
            //底部有边界
            translationY = if (heightF > height) {
                //图片高度大于控件高度，去除顶部边界
                height - bottom
            } else {
                //图片高度小于控件宽度，移动到中间
                centerTranslationY
            }
        }
        mScaleMatrix!!.postTranslate(translationX, translationY)
        imageMatrix = mScaleMatrix
    }

    /**
     * 双击改变大小
     *
     * @param x 点击的中心点
     * @param y 点击的中心点
     */
    private fun onDoubleDrawScale(x: Float, y: Float) {
        //如果缩放动画已经在执行，那就不执行任何事件
        if (mAnimator != null && mAnimator!!.isRunning) return
        val drawScale: Float = getDoubleDrawScale()
        //执行动画缩放，不然太难看了
        scaleAnimation(drawScale, x, y)
    }

    /**
     * 缩放动画
     *
     * @param drawScale 缩放的比例
     * @param x         中心点
     * @param y         中心点
     */
    private fun scaleAnimation(drawScale: Float, x: Float, y: Float) {
        if (mAnimator != null && mAnimator!!.isRunning) return
        mAnimator = ObjectAnimator.ofFloat(getScale(), drawScale)
        mAnimator?.setDuration(500)
        mAnimator?.interpolator = AccelerateInterpolator()
        mAnimator?.addUpdateListener { animation: ValueAnimator ->
            val value: Float = animation.animatedValue as Float / getScale()
            mScaleMatrix!!.postScale(value, value, x, y)
            imageMatrix = mScaleMatrix
            removeBorderAndTranslationCenter()
        }
        mAnimator?.start()
    }

    //返回双击后改变的大小比例(我们希望缩放误差在deviation范围内)
    private fun getDoubleDrawScale(): Float {
        val deviation = 0.05f
        val drawScale: Float
        var scale: Float = getScale()
        if (abs(mMinScale - scale) < deviation) scale = mMinScale
        if (abs(mInitScale - scale) < deviation) scale = mInitScale
        if (abs(mMidScale - scale) < deviation) scale = mMidScale
        if (abs(mMaxScale - scale) < deviation) scale = mMaxScale
        if (scale != mMidScale) {
            //当前大小不等于mMidScale,则调整到mMidScale
            drawScale = mMidScale
            isEnlarge = scale < mMidScale
        } else {
            //如果等于mMidScale，则判断放大或者缩小
            //判断是放大或者缩小，如果上次是放大，则继续放大，缩小则继续缩小
            drawScale = if (isEnlarge) {
                //放大
                mMaxScale
            } else {
                //缩小
                mMinScale
            }
        }
        return drawScale
    }

    //获取图片宽高以及左右上下边界
    private fun getMatrixRectF(): RectF? {
        val drawable = drawable ?: return null
        val rectF = RectF(0f, 0f, drawable.minimumWidth.toFloat(), drawable.minimumHeight.toFloat())
        val matrix = imageMatrix
        matrix.mapRect(rectF)
        return rectF
    }

    /**
     * 获取当前图片的缩放值
     */
    private fun getScale(): Float {
        val values = FloatArray(9)
        mScaleMatrix!!.getValues(values)
        return values[Matrix.MSCALE_X]
    }

    /**
     * 解决和父控件滑动冲突 只要图片边界超过控件边界，返回true
     * @return true 禁止父控件滑动
     */
    override fun canScrollHorizontally(direction: Int): Boolean {
        val rect = getMatrixRectF()
        if (rect == null || rect.isEmpty) return false
        return if (direction > 0) {
            rect.right >= width + 1
        } else {
            rect.left <= -1
        }
    }

    override fun canScrollVertically(direction: Int): Boolean {
        val rect = getMatrixRectF()
        if (rect == null || rect.isEmpty) return false
        return if (direction > 0) {
            rect.bottom >= height + 1
        } else {
            rect.top <= -1
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mScaleGestureDetector!!.onTouchEvent(event) or
                gestureDetector!!.onTouchEvent(event)
    }

}
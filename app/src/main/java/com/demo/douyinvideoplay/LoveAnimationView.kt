package com.demo.douyinvideoplay

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.*
import android.widget.FrameLayout
import android.widget.ImageView
import kotlinx.android.synthetic.main.love_anim_item.view.*
import java.util.*

class LoveAnimationView : FrameLayout {
    private val IMAGE_SIZ = 350
    private lateinit var mContext: Context
    private var mDetector: GestureDetector? = null
    private val startDurationTime = 150L
    private val durationTime = 500L
    private val delay = 400L
    private var animationSet: AnimationSet? = null
    private var mTouchListener: LoveAnimViewTouchListener? = null

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context)
    }

    fun initView(context: Context) {
        this.mContext = context
        inflate(mContext, R.layout.love_anim_item, this)
        mDetector = GestureDetector(mContext, mListener)
    }

    private val mListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            mTouchListener?.onOneClick()
            return super.onSingleTapConfirmed(e)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            startLoveAnimal(e.getX(), e.getY())
            mTouchListener?.onDoubleClick()
            return super.onDoubleTap(e)
        }
    }

    private fun startLoveAnimal(x: Float, y: Float) {
        fg_layer.post(Runnable {
            val imageView = ImageView(mContext)
            imageView.setImageResource(R.mipmap.love_icon)
            fg_layer.addView(imageView)
            val location = intArrayOf(x.toInt(), y.toInt())
            addViewToAnimLayout(imageView, location)
            animalLoveImage(imageView)
        })
    }

    /**
     * 开始播放组合动画
     */
    private fun animalLoveImage(imageView: ImageView) {
        val random = Random()
        val index = random.nextInt(3)
        when (index) {
            0 -> imageView.setRotation(15f)
            1 -> imageView.setRotation(-15f)
        }

        animationSet = AnimationSet(false)
        val scaleAnimation = ScaleAnimation(1F, 1.5f, 1f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnimation.setDuration(startDurationTime)
        scaleAnimation.setFillAfter(false)
        scaleAnimation.setInterpolator(CustomInterpolator())
        animationSet?.addAnimation(scaleAnimation)


        val alphaAnimation = AlphaAnimation(1f, 0.0f)
        alphaAnimation.setDuration(durationTime)
        alphaAnimation.setStartOffset(delay)
        animationSet?.addAnimation(alphaAnimation)

        val scaleBigAnimation = ScaleAnimation(1F, 1.5f, 1f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        scaleBigAnimation.setDuration(durationTime)
        scaleBigAnimation.setStartOffset(delay)
        animationSet?.addAnimation(scaleBigAnimation)

        val translateAnimation = TranslateAnimation(0f, 0f, 0f, -400f)
        translateAnimation.setDuration(durationTime)
        translateAnimation.setStartOffset(delay)
        animationSet?.addAnimation(translateAnimation)
        animationSet?.setFillAfter(true)

        imageView.startAnimation(animationSet)
        animationSet?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                fg_layer.post(Runnable {
                    fg_layer.removeAllViews()
                })
            }

            override fun onAnimationStart(p0: Animation?) {
            }
        })
    }

    private fun addViewToAnimLayout(view: View, location: IntArray): View {
        val x = location[0]
        val y = location[1]
        val lp = LayoutParams(IMAGE_SIZ, IMAGE_SIZ)
        lp.leftMargin = x - IMAGE_SIZ / 2
        lp.topMargin = y - IMAGE_SIZ - 50
        view.setLayoutParams(lp)
        return view
    }

    fun setTouchListener(touchListener: LoveAnimViewTouchListener) {
        this.mTouchListener = touchListener
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        mDetector?.onTouchEvent(ev)
        return true
    }

    class CustomInterpolator : Interpolator {
        override fun getInterpolation(input: Float): Float {
            return Math.sin(input * Math.PI).toFloat()
        }
    }

    interface LoveAnimViewTouchListener {
        fun onDoubleClick()
        fun onOneClick()
    }
}


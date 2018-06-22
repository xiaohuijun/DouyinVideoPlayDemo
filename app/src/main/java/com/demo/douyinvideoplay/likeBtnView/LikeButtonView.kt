package com.demo.douyinvideoplay.likeBtnView

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.demo.douyinvideoplay.R
import kotlinx.android.synthetic.main.view_like_button.view.*


/**
 * Created by Miroslaw Stanek on 20.12.2015.
 */
class LikeButtonView : FrameLayout, View.OnClickListener {

    private var isChecked = false
    private var animatorSet: AnimatorSet? = null

    private var onClickListener: LikeBtnOnClickListener? = null

    private var starCheckedImageRes = R.mipmap.ic_star_rate_on
    private var starUnCheckedImageRes = R.mipmap.ic_star_rate_off
    private var layoutId = R.layout.view_like_button
    private var canCancelLike = true

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val mTypedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.LikeButtonView)
            try {
                starCheckedImageRes = mTypedArray.getResourceId(R.styleable.LikeButtonView_starCheckedImageRes, R.mipmap.ic_star_rate_on)
                starUnCheckedImageRes = mTypedArray.getResourceId(R.styleable.LikeButtonView_starUnCheckedImageRes, R.mipmap.ic_star_rate_off)
                layoutId = mTypedArray.getResourceId(R.styleable.LikeButtonView_layoutId, R.layout.view_like_button)
                canCancelLike = mTypedArray.getBoolean(R.styleable.LikeButtonView_canCancelLike, true)
                mTypedArray.recycle()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        LayoutInflater.from(getContext()).inflate(layoutId, this, true)
        setOnClickListener(this)
    }

    fun setChecked(checked: Boolean) {
        isChecked = checked
        ivStar.setImageResource(if (isChecked) starCheckedImageRes else starUnCheckedImageRes)
    }

    override fun onClick(v: View) {
        if (isChecked && !canCancelLike) {
            onClickListener?.onClick(isChecked)
            return
        }
        isChecked = !isChecked
        ivStar.setImageResource(if (isChecked) starCheckedImageRes else starUnCheckedImageRes)

        onClickListener?.onClick(isChecked)
        animatorSet?.cancel()

        if (isChecked) {
            ivStar.animate().cancel()
            ivStar.scaleX = 0f
            ivStar.scaleY = 0f
            vCircle.visibility = View.VISIBLE
            vDotsView.visibility = View.VISIBLE
            vCircle.innerCircleRadiusProgress = 0f
            vCircle.outerCircleRadiusProgress = 0f
            vDotsView.currentProgress = 0f

            animatorSet = AnimatorSet()

            val outerCircleAnimator = ObjectAnimator.ofFloat(vCircle, CircleView.OUTER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f)
            outerCircleAnimator.duration = 250
            outerCircleAnimator.interpolator = DECCELERATE_INTERPOLATOR

            val innerCircleAnimator = ObjectAnimator.ofFloat(vCircle, CircleView.INNER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f)
            innerCircleAnimator.duration = 200
            innerCircleAnimator.startDelay = 200
            innerCircleAnimator.interpolator = DECCELERATE_INTERPOLATOR

            val starScaleYAnimator = ObjectAnimator.ofFloat(ivStar, ImageView.SCALE_Y, 0.2f, 1f)
            starScaleYAnimator.duration = 350
            starScaleYAnimator.startDelay = 250
            starScaleYAnimator.interpolator = OVERSHOOT_INTERPOLATOR

            val starScaleXAnimator = ObjectAnimator.ofFloat(ivStar, ImageView.SCALE_X, 0.2f, 1f)
            starScaleXAnimator.duration = 350
            starScaleXAnimator.startDelay = 250
            starScaleXAnimator.interpolator = OVERSHOOT_INTERPOLATOR

            val dotsAnimator = ObjectAnimator.ofFloat<DotsView>(vDotsView, DotsView.DOTS_PROGRESS, 0f, 1f)
            dotsAnimator.setDuration(900)
            dotsAnimator.setStartDelay(50)
            dotsAnimator.setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR)

            animatorSet?.playTogether(
                    outerCircleAnimator,
                    innerCircleAnimator,
                    starScaleYAnimator,
                    starScaleXAnimator,
                    dotsAnimator
            )

            animatorSet?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator) {
                    reSetView()
                }
            })

            animatorSet?.start()
        } else {
            reSetView()
        }
    }

    private fun reSetView() {
        vCircle.visibility = View.INVISIBLE
        vCircle.innerCircleRadiusProgress = 0f
        vCircle.outerCircleRadiusProgress = 0f
        vDotsView.visibility = View.INVISIBLE
        vDotsView.currentProgress = 0f
        ivStar.scaleX = 1f
        ivStar.scaleY = 1f
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                ivStar.animate().scaleX(0.7f).scaleY(0.7f).setDuration(150).interpolator = DECCELERATE_INTERPOLATOR
                isPressed = true
            }

            MotionEvent.ACTION_MOVE -> {
                val x = event.x
                val y = event.y
                val isInside = x > 0 && x < width && y > 0 && y < height
                if (isPressed != isInside) {
                    isPressed = isInside
                }
            }

            MotionEvent.ACTION_UP -> {
                ivStar.animate().scaleX(1f).scaleY(1f).interpolator = DECCELERATE_INTERPOLATOR
                if (isPressed) {
                    performClick()
                    isPressed = false
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                ivStar.animate().scaleX(1f).scaleY(1f).interpolator = DECCELERATE_INTERPOLATOR
                isPressed = false
            }
        }
        return true
    }

    fun isChecked(): Boolean {
        return isChecked
    }

    fun setLikeBtnOnClickListener(onClickListener: LikeBtnOnClickListener) {
        this.onClickListener = onClickListener
    }

    interface LikeBtnOnClickListener {
        fun onClick(isChecked: Boolean)
    }

    fun setStarCheckedImageRes(starCheckedImageRes: Int) {
        this.starCheckedImageRes = starCheckedImageRes
    }

    fun setStarUnCheckedImageRes(starUnCheckedImageRes: Int) {
        this.starUnCheckedImageRes = starUnCheckedImageRes
    }

    companion object {
        private val DECCELERATE_INTERPOLATOR = DecelerateInterpolator()
        private val ACCELERATE_DECELERATE_INTERPOLATOR = AccelerateDecelerateInterpolator()
        private val OVERSHOOT_INTERPOLATOR = OvershootInterpolator(4f)
    }
}

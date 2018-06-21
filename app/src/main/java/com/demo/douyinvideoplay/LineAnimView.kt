package com.demo.douyinvideoplay

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.line_anim_item.view.*

class LineAnimView : FrameLayout {
    private lateinit var mContext: Context
    private var animatorSet: AnimatorSet? = null
    private var animatorList: ArrayList<Animator>? = null

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context)
    }

    private fun initView(context: Context) {
        this.mContext = context
        inflate(mContext, R.layout.line_anim_item, this)
        setVisibility(INVISIBLE)
    }

    /**
     * 开始横线动画
     */
    public fun loadLoadingAnimal() {
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE)
            animatorList = ArrayList<Animator>()
            val scaleXAnimator = ObjectAnimator.ofFloat(view_line, "ScaleX", 0f, 1f)
            scaleXAnimator.setRepeatCount(ObjectAnimator.INFINITE)
            scaleXAnimator.setRepeatMode(ObjectAnimator.RESTART)
            scaleXAnimator.setDuration(600)
            animatorList?.add(scaleXAnimator)
            val alphaAnimator = ObjectAnimator.ofFloat(view_line, "Alpha", 0f, 0.5f, 0f)
            alphaAnimator.setRepeatCount(ObjectAnimator.INFINITE)
            alphaAnimator.setRepeatMode(ObjectAnimator.RESTART)
            alphaAnimator.setDuration(600)
            animatorList?.add(alphaAnimator)
            animatorSet = AnimatorSet()
            animatorSet?.playTogether(animatorList)
            animatorSet?.start()
        }
    }

    /**
     * 取消动画
     */
    public fun dismissLoadAnimal() {
        animatorSet?.end()
        animatorSet = null
        setVisibility(INVISIBLE)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        dismissLoadAnimal()
    }
}

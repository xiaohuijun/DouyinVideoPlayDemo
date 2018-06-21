package com.demo.douyinvideoplay

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.View

class ViewPagerLayoutManager : LinearLayoutManager {
    private val TAG = "ViewPagerLayoutManager"
    private lateinit var mPagerSnapHelper: PagerSnapHelper
    private var mOnViewPagerListener: OnViewPagerListener? = null
    private var mDrift: Int = 0//位移，用来判断移动方向

    private val mChildAttachStateChangeListener = object : RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewAttachedToWindow(view: View) {
            if (childCount == 1) {
                val postion = getPosition(view)
                mOnViewPagerListener?.onPageSelected(view,postion, postion == itemCount - 1)
                view.tag = true
            }
        }

        override fun onChildViewDetachedFromWindow(view: View) {
            val tag = view.tag != null && view.tag as Boolean
            if (tag) {
                mOnViewPagerListener?.onPageRelease(view,mDrift >= 0, getPosition(view))
                view.tag = false
            }
        }
    }

    constructor(context: Context, orientation: Int) : super(context, orientation, false) {
        init()
    }

    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout) {
        init()
    }

    private fun init() {
        mPagerSnapHelper = PagerSnapHelper()
    }

    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        mPagerSnapHelper.attachToRecyclerView(view)
        view.addOnChildAttachStateChangeListener(mChildAttachStateChangeListener)
    }


    /**
     * 滑动状态的改变
     * 缓慢拖拽-> SCROLL_STATE_DRAGGING
     * 快速滚动-> SCROLL_STATE_SETTLING
     * 空闲状态-> SCROLL_STATE_IDLE
     *
     * @param state 滑动状态
     */
    override fun onScrollStateChanged(state: Int) {
        mOnViewPagerListener?.onPageScrollStateChanged(state)
        when (state) {
            RecyclerView.SCROLL_STATE_IDLE -> {
                val viewIdle = mPagerSnapHelper.findSnapView(this)
                if (viewIdle != null) {
                    val positionIdle = getPosition(viewIdle)
                    val tag = viewIdle.tag != null && viewIdle.tag as Boolean
                    if (childCount == 1 && !tag) {
                        mOnViewPagerListener?.onPageSelected(viewIdle,positionIdle, positionIdle == itemCount - 1)
                        viewIdle.tag = true
                    }
                }
            }
            RecyclerView.SCROLL_STATE_DRAGGING -> {
            }
            RecyclerView.SCROLL_STATE_SETTLING -> {
            }
        }
    }

    /**
     * 布局完成后调用
     *
     * @param state state
     */
    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
    }

    /**
     * 监听竖直方向的相对偏移量
     *
     * @param dy       dy
     * @param recycler recycler
     * @param state    state
     * @return 竖直方向的相对偏移量
     */
    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        this.mDrift = dy
        return super.scrollVerticallyBy(dy, recycler, state)
    }


    /**
     * 监听水平方向的相对偏移量
     *
     * @param dx       dx
     * @param recycler recycler
     * @param state    state
     * @return 水平方向的相对偏移量
     */
    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        this.mDrift = dx
        return super.scrollHorizontallyBy(dx, recycler, state)
    }

    /**
     * 设置监听
     *
     * @param listener 滑动监听
     */
    fun setOnViewPagerListener(listener: OnViewPagerListener) {
        this.mOnViewPagerListener = listener
    }
}

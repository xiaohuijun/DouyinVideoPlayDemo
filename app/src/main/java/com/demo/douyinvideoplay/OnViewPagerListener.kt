package com.demo.douyinvideoplay

import android.view.View

interface OnViewPagerListener {

    /*释放的监听*/
    fun onPageRelease(itemView: View, isNext: Boolean, position: Int)

    /*选中的监听以及判断是否滑动到底部*/
    fun onPageSelected(itemView: View, position: Int, isBottom: Boolean)

    /*布局完成的监听*/
    fun onPageScrollStateChanged(state: Int)
}

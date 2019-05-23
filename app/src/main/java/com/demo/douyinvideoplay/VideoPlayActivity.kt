package com.demo.douyinvideoplay

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demo.douyinvideoplay.viewpager2.ViewPager2
import kotlinx.android.synthetic.main.activity_video_play.*

class VideoPlayActivity : AppCompatActivity() {
    private val TAG = "VideoPlayActivity"
    private var mLastPosition = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        initView()
        setViewListener()
    }

    private fun initView() {
        view_pager2.adapter = MyAdapter()
        view_pager2.offscreenPageLimit = 2
    }

    private fun setViewListener() {
        view_pager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                getDouYinPageView(mLastPosition)?.onPageUnSelected();
                getDouYinPageView(position)?.onPageSelected()
                mLastPosition = position
            }
        });
    }

    inner class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private val imgs = intArrayOf(R.mipmap.img_video_1, R.mipmap.img_video_2)
        private val videos = intArrayOf(R.raw.video_1, R.raw.video_2)

        constructor() : super() {}

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_pager, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val url = "android.resource://${packageName}/${videos[position % 2]}"
            val videoEntity = VideoEntity(url, imgs[position % 2])
            holder.mDyVideoPageView?.initVieo(videoEntity)
        }

        override fun getItemCount(): Int {
            return 20
        }

        inner class ViewHolder : RecyclerView.ViewHolder {
            var mDyVideoPageView: VideoPageView? = null

            constructor(itemView: View) : super(itemView) {
                mDyVideoPageView = itemView.findViewById(R.id.mDyVideoPageView)
            }
        }
    }

    private fun getDouYinPageView(position: Int): VideoPageView? {
        if (position < 0) return null;
        val view = view_pager2?.getPageViewByPosition(position)
        val douyinVideoPageView: VideoPageView? = view?.findViewById(R.id.mDyVideoPageView)
        return douyinVideoPageView
    }

    override fun onResume() {
        MApplication.isDouyinVideoFragmentVisible = true
        getDouYinPageView(mLastPosition)?.onResume()
        super.onResume()
    }


    override fun onPause() {
        MApplication.isDouyinVideoFragmentVisible = false
        getDouYinPageView(mLastPosition)?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        getDouYinPageView(mLastPosition)?.onDestroy()
        super.onDestroy()
    }
}
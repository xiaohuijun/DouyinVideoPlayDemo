package com.demo.douyinvideoplay

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_video_play.*

class VideoPlayActivity : AppCompatActivity() {
    private val TAG = "VideoPlayActivity"
    private var mLayoutManager: ViewPagerLayoutManager? = null
    private var mLastPosition = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        initView()
        setViewListener()
    }

    private fun initView() {
        mLayoutManager = ViewPagerLayoutManager(this, OrientationHelper.VERTICAL);
        val mAdapter = MyAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private fun setViewListener() {
        mLayoutManager?.setOnViewPagerListener(object : OnViewPagerListener {
            override fun onPageRelease(itemView: View, isNext: Boolean, position: Int) {
                Log.e(TAG, "释放位置:$position 下一页:$isNext")
                val mDyVideoPageView: VideoPageView = itemView.findViewById(R.id.mDyVideoPageView)
                mDyVideoPageView.onPageUnSelected()
            }

            override fun onPageSelected(itemView: View, position: Int, isBottom: Boolean) {
                Log.e(TAG, "选中位置:$position  是否是滑动到底部:$isBottom")
                val mDyVideoPageView: VideoPageView = itemView.findViewById(R.id.mDyVideoPageView)
                mDyVideoPageView.onPageSelected()
                mLastPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
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

    private fun getDouYinPageView(postion: Int): VideoPageView? {
        val view = mLayoutManager?.findViewByPosition(postion)
        val douyinVideoPageView: VideoPageView? = view?.findViewById(R.id.mDyVideoPageView)
        return douyinVideoPageView
    }

    override fun onResume() {
        MApplication.isDouyinVideoFragmentVisible = true
        val douYinPageView = getDouYinPageView(mLastPosition)
        douYinPageView?.onResume()
        super.onResume()
    }


    override fun onPause() {
        MApplication.isDouyinVideoFragmentVisible = false;
        val douYinPageView = getDouYinPageView(mLastPosition);
        douYinPageView?.onPause();
        super.onPause();
    }

    override fun onDestroy() {
        val douYinPageView = getDouYinPageView(mLastPosition);
        if (douYinPageView != null) {
            douYinPageView.onDestroy();
        }
        super.onDestroy();
    }
}
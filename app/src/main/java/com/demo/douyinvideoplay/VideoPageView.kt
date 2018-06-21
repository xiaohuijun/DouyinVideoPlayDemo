package com.demo.douyinvideoplay

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.demo.douyinvideoplay.ijkplayer.IRenderView
import kotlinx.android.synthetic.main.view_video_page.view.*
import tv.danmaku.ijk.media.player.IMediaPlayer

class VideoPageView : LinearLayout {
    private lateinit var mContext: Context
    private var mNeedAutoResume: Boolean = false
    private var mAspectRatio: Int = 0

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

    private fun initView(context: Context) {
        mContext = context
        inflate(context, R.layout.view_video_page, this)
        setViewListener()
        setVideoPlayListener()
    }

    private fun setViewListener() {
        mLoveAinmView.setTouchListener(object : LoveAnimationView.LoveAnimViewTouchListener {
            override fun onDoubleClick() {
                //点赞
            }

            override fun onOneClick() {
                //暂停
                changeVideoState()
            }
        })
    }

    fun initVieo(douYinVideoEntity: VideoEntity) {
        mAspectRatio = IRenderView.AR_ASPECT_FILL_PARENT
        mIvCover.scaleType = ImageView.ScaleType.CENTER_CROP
        mIvCover.setImageResource(douYinVideoEntity.thumbUrl)
        mDyVideoPlayView.init(douYinVideoEntity.url, mAspectRatio)
    }

    fun onPageSelected() {
        mIvCover.setVisibility(VISIBLE)
        mIvPlayZt.setVisibility(GONE)
        mLineAnimView.loadLoadingAnimal()
        mDyVideoPlayView.resetStatisticsParams()
        mDyVideoPlayView.start()
    }

    fun onPageUnSelected() {
        mIvCover.setVisibility(VISIBLE)
        mLineAnimView.dismissLoadAnimal()
        mDyVideoPlayView.release()
    }

    fun tryRetry() {
        if (mDyVideoPlayView.isOnError) {
            mDyVideoPlayView.start()
        }
    }

    fun onResume() {
        if (mNeedAutoResume) {
            mDyVideoPlayView.resume()
        }
    }

    fun reSatrt() {
        mDyVideoPlayView.reStart()
    }


    fun onPause() {
        if (mDyVideoPlayView.isPlay) {
            mDyVideoPlayView.pause()
            mNeedAutoResume = true
        } else {
            mNeedAutoResume = false
        }
    }

    fun onDestroy() {
        mDyVideoPlayView.release()
    }

    private fun setVideoPlayListener() {
        mDyVideoPlayView.setVideoPlayListener(object : VideoPlayView.VideoPlayListener {
            override fun onPlayProgress(progress: Int) {
                var currentProgress = 0
                if (progress > 100)
                    currentProgress = 100
                else if (progress < 0)
                    currentProgress = 0
                else currentProgress = progress
                mPbVideo.setProgress(currentProgress)
            }
        })

        mDyVideoPlayView.setOnInfoListener(object : IMediaPlayer.OnInfoListener {
            override fun onInfo(mp: IMediaPlayer?, what: Int, extra: Int): Boolean {
                when (what) {
                    IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                        mIvCover.setVisibility(INVISIBLE)
                        mLineAnimView.dismissLoadAnimal()
                        mPbVideo.setVisibility(VISIBLE)
                        mIvPlayZt.setVisibility(GONE)
                        checkIsVisible()
                    }
                    IMediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                        mLineAnimView.loadLoadingAnimal()
                        mPbVideo.setVisibility(INVISIBLE)
                    }
                    IMediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                        mLineAnimView.dismissLoadAnimal()
                        mPbVideo.setVisibility(VISIBLE)
                        mIvPlayZt.setVisibility(GONE)
                        checkIsVisible()
                    }
                }
                return false
            }
        })

        mDyVideoPlayView.setOnErrorListener(object : IMediaPlayer.OnErrorListener {
            override fun onError(mp: IMediaPlayer?, what: Int, extra: Int): Boolean {
                if (extra == 1) {
                    //1 代表so文件还没有下载成功,此时显示loading,等待下载,如果此时无网络,优先网络提示
                    if (NetWorkUtil.getNetworkState(mContext) == NetWorkUtil.NONE) {
                        Toast.makeText(mContext, "网络开小差啦", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(mContext, "网络开小差啦", Toast.LENGTH_SHORT).show()
                }
                mIvPlayZt.setVisibility(VISIBLE)
                return false
            }
        })
    }

    private fun checkIsVisible() {
        if (!MApplication.isDouyinVideoFragmentVisible && mDyVideoPlayView.isPlay) {
            mDyVideoPlayView.pause()
            mNeedAutoResume = true
        }
    }

    private fun changeVideoState() {
        if (mDyVideoPlayView.isPlay) {
            mDyVideoPlayView.pause()
            mIvPlayZt.setVisibility(VISIBLE)
            return
        }
        if (mDyVideoPlayView.isOnError) {
            mDyVideoPlayView.start()
            mIvPlayZt.setVisibility(GONE)
            return
        }
        mDyVideoPlayView.resume()
        mIvPlayZt.setVisibility(GONE)
    }
}
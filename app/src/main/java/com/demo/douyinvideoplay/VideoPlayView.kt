package com.demo.douyinvideoplay

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.Toast
import com.demo.douyinvideoplay.ijkplayer.IjkVideoView
import tv.danmaku.ijk.media.player.IMediaPlayer

/**
 * 视频播放容器
 */
class VideoPlayView : LinearLayout, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnInfoListener {

    private var mContext: Context? = null
    private var ijkVideoView: IjkVideoView? = null
    private var mAudioManager: AudioManager? = null

    private var onInfoListener: IMediaPlayer.OnInfoListener? = null
    private var onErrorListener: IMediaPlayer.OnErrorListener? = null

    private var mVideoLink: String? = null
    private var mAspectRatio: Int = 0//视频填充模式
    var effectivePlayTime: Long = 0
        private set//有效播放时长
    var loopTimes = 1
        private set//循环播放次数,默认值1
    private var mSoRetryCount = 0//ijkPlayer相关so加载失败导致的视频播放失败

    private var mVideoPlayListener: VideoPlayListener? = null

    private val onAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
            }
        }
    }


    val isPlay: Boolean
        get() = ijkVideoView != null && ijkVideoView!!.isPlaying

    val isOnError: Boolean
        get() = ijkVideoView != null && ijkVideoView!!.currentStatue == IjkVideoView.STATE_ERROR

    fun setVideoPlayListener(videoPlayListener: VideoPlayListener) {
        this.mVideoPlayListener = videoPlayListener
    }

    constructor(context: Context) : super(context) {
        mContext = context
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
    }

    fun init(videoLink: String, aspectRatio: Int) {
        mVideoLink = videoLink
        mAspectRatio = aspectRatio
    }

    fun start() {
        if (TextUtils.isEmpty(mVideoLink)) {
            return
        }
        removeAllViews()
        ijkVideoView = IjkVideoView(mContext, mAspectRatio)
        ijkVideoView!!.setOnPreparedListener(this)
        ijkVideoView!!.setOnCompletionListener(this)
        ijkVideoView!!.setOnErrorListener(this)
        ijkVideoView!!.setOnInfoListener(this)
        addView(ijkVideoView)

        val uri = Uri.parse(mVideoLink)
        ijkVideoView!!.setVideoURI(uri)
        ijkVideoView!!.start()
        checkNetworkState()
        setKeepScreenOnWhenPlay(true)
    }

    fun resetStatisticsParams() {
        effectivePlayTime = 0
        loopTimes = 1
        mSoRetryCount = 0
    }

    private fun checkNetworkState() {
        if (NetWorkUtil.getNetworkState(mContext) == NetWorkUtil.MOBILE) {
            val currentTimeMillis = System.currentTimeMillis()
            if (currentTimeMillis - sPlayTime > VIDEO_RESTART_TIME) {
                sPlayTime = currentTimeMillis
                Toast.makeText(mContext, "正在使用流量播放", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun reStart() {
        if (ijkVideoView != null) {
            loopTimes++
            ijkVideoView!!.start()
            setKeepScreenOnWhenPlay(true)
        }
    }

    /**
     * @param mute false静音
     */
    fun updateVolume(mute: Boolean) {
        if (ijkVideoView == null) {
            return
        }
        val mediaPlayer = ijkVideoView!!.mediaPlayer ?: return
        if (mute) {
            mediaPlayer.setVolume(1f, 1f)
            setAudioFocus(true)
        } else {
            mediaPlayer.setVolume(0f, 0f)
            setAudioFocus(false)
        }
    }

    /**
     * 请求或者释放焦点
     *
     * @param focus 音频焦点
     */
    fun setAudioFocus(focus: Boolean) {
        if (mAudioManager == null) {
            mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        }
        if (mAudioManager == null)
            return
        if (focus) {
            //请求音频焦点
            mAudioManager?.requestAudioFocus(onAudioFocusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
        } else {//释放
            mAudioManager?.abandonAudioFocus(onAudioFocusChangeListener)
        }
    }


    fun resume() {
        ijkVideoView?.start()
        startUpdateEffectiveTimer()
        setKeepScreenOnWhenPlay(true)
    }

    fun pause() {
        ijkVideoView?.pause()
        cancelUpdateEffectiveTimer()
        setKeepScreenOnWhenPlay(false)
    }

    fun release() {
        ijkVideoView?.stopPlayback()
        cancelUpdateEffectiveTimer()
        setKeepScreenOnWhenPlay(false)
    }

    private fun setKeepScreenOnWhenPlay(keepScreenOn: Boolean) {
        ijkVideoView?.keepScreenOn = keepScreenOn
    }


    private fun startUpdateEffectiveTimer() {
        cancelUpdateEffectiveTimer()
        CountTimeUtil.getCountHandler().postDelayed(object : Runnable {
            override fun run() {
                effectivePlayTime = effectivePlayTime + 100
                onPlayProgressChanged()
                CountTimeUtil.getCountHandler().postDelayed(this, 100)
            }
        }, 100)
    }

    private fun onPlayProgressChanged() {
        try {
            val position = ijkVideoView!!.currentPosition
            val duration = ijkVideoView!!.duration
            val progress = position * 100 / if (duration <= 0) 1 else duration
            mVideoPlayListener?.onPlayProgress(progress)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun cancelUpdateEffectiveTimer() {
        CountTimeUtil.getCountHandler().removeCallbacksAndMessages(null)
    }


    fun setOnInfoListener(onInfoListener: IMediaPlayer.OnInfoListener) {
        this.onInfoListener = onInfoListener
    }


    fun setOnErrorListener(onErrorListener: IMediaPlayer.OnErrorListener) {
        this.onErrorListener = onErrorListener
    }


    override fun onCompletion(iMediaPlayer: IMediaPlayer) {
        cancelUpdateEffectiveTimer()
        reStart()
    }

    override fun onError(iMediaPlayer: IMediaPlayer, what: Int, extra: Int): Boolean {
        cancelUpdateEffectiveTimer()
        if (extra == 1) {
            if (mContext is Activity) {
                if (mSoRetryCount == 0) {
                    // TODO: 2018/4/20 重新加载so文件
                    mSoRetryCount++
                }
            }
        }
        onErrorListener?.onError(iMediaPlayer, what, extra)
        return false
    }

    override fun onInfo(iMediaPlayer: IMediaPlayer, what: Int, extra: Int): Boolean {
        when (what) {
            IMediaPlayer.MEDIA_INFO_BUFFERING_START -> cancelUpdateEffectiveTimer()
            IMediaPlayer.MEDIA_INFO_BUFFERING_END -> startUpdateEffectiveTimer()
        }
        onInfoListener?.onInfo(iMediaPlayer, what, extra)
        return false
    }

    override fun onPrepared(iMediaPlayer: IMediaPlayer) {
        startUpdateEffectiveTimer()
    }


    interface VideoPlayListener {
        fun onPlayProgress(progress: Int)
    }

    companion object {
        private val CACHE_PROTOCOL = "cache:"
        private val VIDEO_RESTART_TIME = 40 * 60 * 1000
        private var sPlayTime: Long = 0
    }
}

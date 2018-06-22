package com.demo.douyinvideoplay.likeBtnView

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Property
import android.view.View

/**
 * Created by Miroslaw Stanek on 21.12.2015.
 */
class CircleView : View {

    private val argbEvaluator = ArgbEvaluator()

    private val circlePaint = Paint()
    private val maskPaint = Paint()

    private var tempBitmap: Bitmap? = null
    private var tempCanvas: Canvas? = null

    var outerCircleRadiusProgress = 0f
        set(outerCircleRadiusProgress) {
            field = outerCircleRadiusProgress
            updateCircleColor()
            postInvalidate()
        }
    var innerCircleRadiusProgress = 0f
        set(innerCircleRadiusProgress) {
            field = innerCircleRadiusProgress
            postInvalidate()
        }

    private var maxCircleSize: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        circlePaint.style = Paint.Style.FILL
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        maxCircleSize = w / 2
        tempBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
        tempCanvas = Canvas(tempBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        tempCanvas!!.drawColor(0xffffff, PorterDuff.Mode.CLEAR)
        tempCanvas!!.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), this.outerCircleRadiusProgress * maxCircleSize, circlePaint)
        tempCanvas!!.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), this.innerCircleRadiusProgress * maxCircleSize, maskPaint)
        canvas.drawBitmap(tempBitmap!!, 0f, 0f, null)
    }

    private fun updateCircleColor() {
        var colorProgress = Utils.clamp(this.outerCircleRadiusProgress.toDouble(), 0.5, 1.0).toFloat()
        colorProgress = Utils.mapValueFromRangeToRange(colorProgress.toDouble(), 0.5, 1.0, 0.0, 1.0).toFloat()
        this.circlePaint.color = argbEvaluator.evaluate(colorProgress, START_COLOR, END_COLOR) as Int
    }

    companion object {
        private val START_COLOR = -0x5d5ae
        private val END_COLOR = -0x7ae85

        val INNER_CIRCLE_RADIUS_PROGRESS: Property<CircleView, Float> = object : Property<CircleView, Float>(Float::class.java, "innerCircleRadiusProgress") {
            override fun get(`object`: CircleView): Float {
                return `object`.innerCircleRadiusProgress
            }

            override fun set(`object`: CircleView, value: Float) {
                `object`.innerCircleRadiusProgress = value
            }
        }

        val OUTER_CIRCLE_RADIUS_PROGRESS: Property<CircleView, Float> = object : Property<CircleView, Float>(Float::class.java, "outerCircleRadiusProgress") {
            override fun get(`object`: CircleView): Float {
                return `object`.outerCircleRadiusProgress
            }

            override fun set(`object`: CircleView, value: Float) {
                `object`.outerCircleRadiusProgress = value
            }
        }
    }
}

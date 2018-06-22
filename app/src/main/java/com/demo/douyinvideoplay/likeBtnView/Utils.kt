package com.demo.douyinvideoplay.likeBtnView

/**
 * Created by Miroslaw Stanek on 21.12.2015.
 */
object Utils {
    fun mapValueFromRangeToRange(value: Double, fromLow: Double, fromHigh: Double, toLow: Double, toHigh: Double): Double {
        return toLow + (value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow)
    }

    fun clamp(value: Double, low: Double, high: Double): Double {
        return Math.min(Math.max(value, low), high)
    }
}

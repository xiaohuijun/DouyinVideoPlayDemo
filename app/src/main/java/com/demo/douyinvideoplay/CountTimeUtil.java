package com.demo.douyinvideoplay;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * des ：
 * created by ：wuchangbin
 * created on：2018/3/13
 */
public class CountTimeUtil {

    private final static HandlerThread COUNT_TIME_HANDLER_THREAD = new HandlerThread("CountEffectiveTime");
    private static Handler mCountHandler;

    public static Handler getCountHandler() {
        if (mCountHandler == null) {
            synchronized (CountTimeUtil.class) {
                if (mCountHandler == null) {
                    COUNT_TIME_HANDLER_THREAD.start();
                    mCountHandler = new Handler(COUNT_TIME_HANDLER_THREAD.getLooper());
                }
            }
        }
        return mCountHandler;
    }

}

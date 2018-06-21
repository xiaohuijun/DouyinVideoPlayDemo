package com.demo.douyinvideoplay.ijkplayer;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;

import java.util.List;

public class AppUtil {
    private final static Handler mainHandler = new Handler(Looper.getMainLooper());
    private final static HandlerThread business0HandlerThread = new HandlerThread("Business0Handler");
    private static Handler business0Handler;

    /*
    该Handler运行在主线程中，因此一些必须放在主线程来处理的事务可以用该Hanlder来处理
    */
    public static Handler getMainHandler() {
        return mainHandler;
    }

    /*
     该Handler主要用于小事务处理，对于一些耗时但30秒钟能执行完的操作，建议大家放到该Handler来处理
    */
    public static Handler getBusiness0Handler() {
        if (business0Handler == null) {
            synchronized (AppUtil.class) {
                if (business0Handler == null) {
                    business0HandlerThread.start();
                    business0Handler = new Handler(business0HandlerThread.getLooper());
                }
            }
        }
        return business0Handler;
    }

    public static Looper getBusiness0Looper() {
        return getBusiness0Handler().getLooper();
    }

    public static void post(Runnable r) {
        getBusiness0Handler().post(r);
    }

    public static void postDelayed(Runnable r, int delayTime) {
        getBusiness0Handler().postDelayed(r, delayTime);
    }

    /**
     * 删除runnable
     */
    public static void removeCallbacks(Runnable runnable) {
        getBusiness0Handler().removeCallbacks(runnable);
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = mActivityManager.getRunningAppProcesses();
        if (appProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        }
        return context.getPackageName();
    }

    public static boolean isServiceExisted(Context context, String className) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = am.getRunningServices(Integer.MAX_VALUE);
        int myUid = android.os.Process.myUid();
        for (ActivityManager.RunningServiceInfo runningServiceInfo : serviceList) {
            if (runningServiceInfo.uid == myUid && runningServiceInfo.service.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isProcessAlive(Context context, String processName) {
        if (TextUtils.isEmpty(processName)) {
            return false;
        }
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = mActivityManager.getRunningAppProcesses();
        if (appProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (processName.equals(appProcess.processName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isAppOnForeground(final Context context) {
        // Returns a list of application processes that are running on the　
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.　
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}

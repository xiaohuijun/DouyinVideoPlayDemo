package com.demo.douyinvideoplay;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtil {
    public static final int NONE = 0;// 无网络
    public static final int WIFI = 1;// Wi-Fi
    public static final int MOBILE = 2;// 3G,GPRS

    /**
     * 获取当前网络状态(wifi,3G)
     *
     * @param context
     * @return
     */
    public static int getNetworkState(Context context) {
        if (context == null) {
            return NONE;
        }
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager == null ||
                connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) == null ||
                connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) == null) {
            return NONE;
        }
        NetworkInfo.State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
            return MOBILE;
        }
        state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
            return WIFI;
        }
        return NONE;
    }
}

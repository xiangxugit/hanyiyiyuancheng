package com.xhh.ysj.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import static android.content.ContentValues.TAG;
import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by Administrator on 2018/6/21 0021.
 */

public class NetBroadCastReciver extends BroadcastReceiver {



    /**https://www.cnblogs.com/qingblog/archive/2012/07/19/2598983.html
     * 只有当网络改变的时候才会 经过广播。
     */

    @Override
    public void onReceive(Context context, Intent intent) {


    }


}

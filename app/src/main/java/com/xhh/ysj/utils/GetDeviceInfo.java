package com.xhh.ysj.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.xhh.ysj.App;

/**
 * Created by Administrator on 2018/5/7 0007.
 */

public class GetDeviceInfo extends App {

    private static final String TAG = "GetDeviceInfo";

    //获取经纬度
    private static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    static String str = "";

    /**
     * The IMEI: 仅仅只对Android手机有效
     * 采用此种方法，需要在AndroidManifest.xml中加入一个许可：android.permission.READ_PHONE_STATE，并且用户应当允许安装此应用
     * IMEI是设备唯一标识，它应该类似于 359881030314356（除非你有一个没有量产的手机，它可能有无效的IMEI，如：0000000000000）
     *
     * @return imei
     */
    public String getIMEI(Activity context) {
        int osVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
        if (osVersion > 22) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_PHONE_STATE},
                        WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            } else {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
                str = tm.getDeviceId();
                String mtype = android.os.Build.MODEL;
                LogUtils.d(TAG, mtype);
            }
        } else {
            //如果SDK小于6.0则不去动态申请权限
            TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            str = tm.getDeviceId();
            String mtype = android.os.Build.MODEL;
            LogUtils.d(TAG, mtype);
        }

        return str;

    }


    public static String getlongitudelatitude(Activity context) {

        return "";

    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
//            Toast.makeText(getApplicationContext(), "授权成功", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(getApplicationContext(), "授权拒绝", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 检测网络是否可用
     *
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
}
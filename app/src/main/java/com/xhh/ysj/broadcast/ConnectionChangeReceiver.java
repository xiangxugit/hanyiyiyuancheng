package com.xhh.ysj.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.xhh.ysj.App;
import com.xhh.ysj.R;
import com.xhh.ysj.view.activity.BaseActivity;
import com.xhh.ysj.view.activity.MainActivity;

/**
 * Created by Administrator on 2018/6/4.
 */

public class ConnectionChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "ConnectionChangeReceive";

    private BaseActivity activity;

    public ConnectionChangeReceiver(BaseActivity activity) {
        super();
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        Intent it = new Intent();

        if (networkInfo != null && networkInfo.isAvailable()) {
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
//                    Toast.makeText(context, "正在使用2G/3G/4G网络", Toast.LENGTH_SHORT).show();
                    if (MainActivity.isStarted){
                        return;
                    }
                    it.setClass(context, MainActivity.class);
                    context.startActivity(it);
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    if (MainActivity.isStarted){
                        return;
                    }
//                    Toast.makeText(context, "上传数据到服务器，删除本地数据库", Toast.LENGTH_SHORT).show();
                    it.setClass(context, MainActivity.class);
                    context.startActivity(it);
                    break;
                default:
                    break;
            }
        } else {
            MainActivity.isStarted = false;
            activity.moveToBreakDownActivity(App.getInstance().getString(R.string.break_down_reason_no_network));
        }
    }
}

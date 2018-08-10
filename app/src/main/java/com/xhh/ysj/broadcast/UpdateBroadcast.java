package com.xhh.ysj.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xhh.ysj.beans.ViewShow;
import com.xhh.ysj.interfaces.OnUpdateUI;
import com.xhh.ysj.utils.LogUtils;

/**
 * Created by Administrator on 2018/5/24 0024.
 */
public class UpdateBroadcast extends BroadcastReceiver {

    private static final String TAG = "UpdateBroadcast";

    OnUpdateUI onUpdateUI;

    @Override
    public void onReceive(Context context, Intent intent) {
        ViewShow viewShow = (ViewShow)intent.getSerializableExtra("progress");
        if(null==viewShow||null==onUpdateUI){
            LogUtils.e(TAG,"one");
        }else{
            onUpdateUI.updateUI(viewShow);
        }
    }


    public void SetOnUpdateUI(OnUpdateUI onUpdateUI){
        this.onUpdateUI = onUpdateUI;
    }

}

package com.xhh.ysj.processpreserve;

import android.nfc.Tag;
import android.serialport.DevUtil;

import com.xhh.ysj.beans.DispenserCache;
import com.xhh.ysj.utils.LogUtils;

/**
 * Created by Administrator on 2018/7/27 0027.
 */

public class ComThreadYq extends Thread{
    private static final String TAG = "ComThreadYq";
    private DevUtil devUtil=null;
    //判断是否登录
    String userid = DispenserCache.userIdTemp;

    public ComThreadYq(){
        if (null == devUtil) {
            devUtil = new DevUtil(null);
        }
    }

    @Override
    public void run() {
        while(true){
            devUtil.get_ioRunData();
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int sleeepTime = 60000;
            while(sleeepTime>0){
                try {
                    sleep(100);
                    LogUtils.e(TAG,"userid"+userid);
                    if(null==userid){
                        continue;
                    }
                    sleeepTime-=100;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
//        super.run();
    }
}

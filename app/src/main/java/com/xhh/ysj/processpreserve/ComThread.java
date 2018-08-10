package com.xhh.ysj.processpreserve;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.serialport.ComUtil;
import android.serialport.DevUtil;
import android.widget.Toast;

import org.xutils.DbManager;

import java.io.File;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.xhh.ysj.beans.DispenserCache;
import com.xhh.ysj.utils.LogUtils;
import com.xhh.ysj.utils.XutilsInit;
import com.xhh.ysj.beans.SysDeviceNoticeAO;
import com.xhh.ysj.beans.ViewShow;
import com.xhh.ysj.constants.Constant;
import com.xhh.ysj.utils.ControllerUtils;

/**
 * Created by Administrator on 2018/5/22 0022.
 */
public class ComThread extends Thread {
    private static final String TAG = "ComThread";

    private SysDeviceNoticeAO sysDeviceNoticeAO;
    public static  DevUtil devUtil;
    private final int MAXERR=5;
    private int errCount=0;
    private boolean active = true;//轮询标志
    public Context context;
    public Handler myhandler;
    public boolean updateflag = false;
    public boolean operateflag = false;
    private DbManager dbManager;
    private static final String DevPath = "/dev/ttyS3";//默认串口
    private static final int Baudrate = 115200;//默认波特率
    private static final int LoopIdle = 50;//线程空闲时间ms
    private static final int PollTime = 8000;//轮询get_ioRunData()时间间隔ms

    public boolean getActive() {
        return active;
    }
    public void setActive(boolean b) {
        active = b;
    }

    public ComThread(Context context, Handler handler){
        this.context = context;
        this.myhandler = handler;
        if(null == this.sysDeviceNoticeAO){
            this.sysDeviceNoticeAO = new SysDeviceNoticeAO();
        }
        dbManager = new XutilsInit(context).getDb();
        if (null == devUtil) {
            devUtil = new DevUtil(null);
        }
        if (devUtil.isComOpened()) {
//            Toast.makeText(context, "启动了", Toast.LENGTH_SHORT).show();

        } else {
            try {
                File dev = new File(DevPath);
                boolean r = devUtil.openCOM(dev, Baudrate, 0);
                if (!r) {
                    Toast.makeText(context, "通讯故障", Toast.LENGTH_SHORT).show();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void run() {
        while(true){
            String userid = DispenserCache.userIdTemp;
            devUtil.get_ioRunData();
            updateRunData(true);
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int sleeepTime = 6000;
            while(sleeepTime>0){
                try {
                    sleep(1000);
                    LogUtils.e(TAG,"userid"+userid);
                    sleeepTime=sleeepTime-1000;
                    if(null==userid&&active==false){
                        continue;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

//        long pollTick = SystemClock.uptimeMillis();
//        long nowTick;
//
//        while (!isInterrupted()){
//            nowTick = SystemClock.uptimeMillis();
//            if(null==devUtil){
//                devUtil = new DevUtil(null);
//            }
//            if(active && nowTick- pollTick > Constant.POOL_TIME) {
//                try {
//                    devUtil.get_ioRunData();
//                    updateRunData(true);
//                } catch (NullPointerException e) {
////                    addCode(false, ComUtil.getCodeHead() + e.toString());
//                    ComUtil.delay(2000);
//                }
//                pollTick = SystemClock.uptimeMillis();
//            }
//            else if(!active){
//                //停止轮询，可能主线程在调用devUtil发送指令
//                pollTick = SystemClock.uptimeMillis();
//            }
//            else {
//                //线程空闲
//                ComUtil.delay(LoopIdle);
//            }
//        }

//        super.run();
    }

    public void updateRunData(boolean poll) {
        HashMap<String, Object> map;
        String[][] data=devUtil.toArray();
        String sOn, sOff;
        int sta = devUtil.get_run_bSta_value();
        if(sta==1) {
            sOn = "Online";
            sOff = "";
        }
        else{
            sOn = "";
            sOff = "Offline";
        }
        Message msg = new Message();
        Bundle b = new Bundle();
        ViewShow viewShow = new ViewShow();
        viewShow.setChongxitext(devUtil.get_run_bRinse_valAlias());
        viewShow.setCooltext(devUtil.get_run_bCool_valAlias());//是否制冷
        viewShow.setCoolwatertextvalue("冷"+devUtil.get_run_coolTemp_valAlias()+"℃");//
        viewShow.setHotornot(devUtil.get_run_bHot_valAlias());//是否加热
        viewShow.setHotwatertextvalue("热"+devUtil.get_run_hotTemp_valAlias()+"℃");
        viewShow.setPpmvalue(""+devUtil.get_run_sTDS_value());
        viewShow.setPpm(""+devUtil.get_run_oTDS_value());
        viewShow.setZhishuitext(devUtil.get_run_bWater_valAlias());
        String viewShowString = JSON.toJSONString(viewShow);
//        Toast.makeText(context, "viewShowString"+viewShowString, Toast.LENGTH_SHORT).show();
        msg.obj = viewShow;
        msg.what =0;
        myhandler.sendMessage(msg);
    }

    //关机
    public void onOrOff(boolean operateflag){
        if(true == operateflag){
            ControllerUtils.operateDevice(3,true);
        }
    }
    //滤芯是否用完




}

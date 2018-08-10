package com.xhh.ysj.processpreserve;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.serialport.ComUtil;
import android.serialport.DevUtil;
import android.text.format.Time;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.coolerfall.daemon.Daemon;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.xhh.ysj.view.activity.MainActivity;
import com.xhh.ysj.beans.ViewShow;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/5/17 0017.
 */

public class DaemonService extends Service {
    private final static int GRAY_SERVICE_ID = -1001;
    private String DevPath = "/dev/ttyS3";//默认串口
    private final int Baudrate = 115200;//默认波特率
    private final int LoopIdle = 50;//线程空闲时间ms
    private DevUtil devUtil = null;
    public  static ComThread comThread ;
    public  static ComThreadYq comThreadYq ;
    final  MyHandler myHandler = new MyHandler();
    @Override
    public void onCreate() {
        super.onCreate();
        Daemon.run(this, DaemonService.class, Daemon.INTERVAL_ONE_MINUTE * 2);
        startTimeTask();
        grayGuard();
//        comThread = new ComThread(this, myHandler);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        MyHandler myHandler = new MyHandler();
        devUtil = new DevUtil(myHandler);
        File dev = new File(DevPath);
        //dev 必须具有读写权限
        boolean r = devUtil.openCOM(dev, Baudrate, 0);
        if(r) {
            if(null==comThread){
            comThread = new ComThread(this,myHandler);
            }
//            if(null==comThreadYq){
//                comThreadYq = new ComThreadYq();
//            }

//            Toast.makeText(this,"进程重启",Toast.LENGTH_SHORT).show();
            if(!comThread.isAlive()){
                comThread.start();
            }
//            if(!comThreadYq.isAlive()){
//                comThreadYq.start();
//            }

        }else{
        }



        //启动定时任务开始取数据存入数据库
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        startService(new Intent(this, DaemonService.class));
    }

    // TODO: 2018/6/16 0016 没有回收
    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Intent it = new Intent();
                    it.setAction(MainActivity.FLAG);
                    ViewShow viewShow = (ViewShow) msg.obj;
                    String viewShowString = JSON.toJSONString(viewShow);
//                    Toast.makeText(DaemonService.this,"viewShowString"+viewShowString,Toast.LENGTH_SHORT).show();

                    Bundle b = new Bundle();
                    b.putSerializable("progress", viewShow);
                    it.putExtras(b);
                    sendBroadcast(it);
                    break;
                case 1:
                    //TODO 获取list定时上报
                    break;
            }

        }
    }

    private void startTimeTask() {

    }

    public static class DaemonInnerService extends Service {

        @Override
        public void onCreate() {
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            //stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
//            unregisterReceiver(upd);
        }
    }


    private void grayGuard() {
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(GRAY_SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
        } else {
            // TODO: 2018/6/16 0016  DaemonInnerService没有在清单文件注册
            Intent innerIntent = new Intent(this, DaemonInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        }


        //发送唤醒广播来促使挂掉的UI进程重新启动起来
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent alarmIntent = new Intent();
//        alarmIntent.setAction(WakeReceiver.GRAY_WAKE_ACTION);
//        PendingIntent operation = PendingIntent.getBroadcast(this, WAKE_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            alarmManager.setWindow(AlarmManager.RTC_WAKEUP,
//                    System.currentTimeMillis(), ALARM_INTERVAL, operation);
//        }else {
//            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
//                    System.currentTimeMillis(), ALARM_INTERVAL, operation);
//        }
    }


}

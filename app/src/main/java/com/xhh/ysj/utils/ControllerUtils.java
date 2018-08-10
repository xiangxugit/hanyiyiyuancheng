package com.xhh.ysj.utils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.serialport.DevUtil;
import android.util.Log;
import android.widget.Toast;

import com.xhh.ysj.App;
import com.xhh.ysj.constants.Constant;
import com.xhh.ysj.constants.UriConstant;
import com.xhh.ysj.processpreserve.ComThread;
import com.xhh.ysj.processpreserve.DaemonService;
import com.xhh.ysj.view.activity.BaseActivity;
import com.xhh.ysj.view.activity.MainActivity;

import org.xutils.common.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import okhttp3.Request;

/**
 * Created by Administrator on 2018/4/19 0019.
 */

public class ControllerUtils {

    private static final String TAG = "ControllerUtils";
    private static int SET_IO_HEAT_ENABLED = 0;//加热使能
    private static int SET_IO_COLD_ENABLED = 1;//制冷使能
    private static int DO_IO_SWITCH = 3;//开关机指令
    private static int DO_IO_EMPTYING = 4;//排空指令
    public static int DO_IO_COVER = 5;//做开盖指令
    public static int DO_IO_RINSE = 6;
    public static int DO_HOTTING = 7;//允许加热
    public static int DO_TURN_OFF_HOTTING = 8;//禁止加热
    public static int DO_COOLING = 9;//允许制冷
    public static int DO_TURN_OFF_COOLING = 10;//禁止制冷

    private static int SETTING = 11;//设置
    private static int SHUT_DOWN = 12;//关机
    private static int OPENING = 13;//开机；
    public static int OPENCLOSE = 15;//控制板开关机

    private static BaseActivity context;

    // 使能操作
    private static DevUtil devUtil = null;
    private static ComThread comThread;
    private static final String DevPath = "/dev/ttyS3";//默认串口
    private static final int Baudrate = 115200;//默认波特率
    private static  MyHandler myHandler;

    public ControllerUtils(BaseActivity context) {
        if (null == devUtil) {
            devUtil = new DevUtil(null);
        }
        if (null == DaemonService.comThread) {
            comThread = new ComThread(context, null);
        } else {
            comThread = DaemonService.comThread;
        }
        this.context = context;
        myHandler = new MyHandler();
    }

    public static void operateDevice(int directive, boolean flag) {

            if (null == devUtil) {
                devUtil = new DevUtil(null);
            }
            if (!devUtil.isComOpened()) {
                try {
                    if (!devUtil.openCOM(new File(DevPath), Baudrate, 0)) {
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

        comThread.setActive(false);
        try {
            if (directive == SET_IO_HEAT_ENABLED) {
                //加热
//                Toast.makeText(context,"加热使能",Toast.LENGTH_SHORT).show();

                devUtil.set_ioHeatEnabled(flag);
            }
            //制冷使能
            if (directive == SET_IO_COLD_ENABLED) {
//                Toast.makeText(context,"制冷使能",Toast.LENGTH_SHORT).show();
                devUtil.set_ioColdEnabled(flag);
            }
            //开关机
            if (directive == OPENCLOSE) {
                devUtil.do_ioSWitch(flag);
                //停止制水
                //停止加热
                devUtil.set_ioColdEnabled(flag);
                devUtil.set_ioHeatEnabled(flag);

                //停止制冷
                //同步后台的开关机状态
                String switchStatus = "";
                if(flag){
                    switchStatus = "1";
                }else{
                    switchStatus = "0";
                }
                String url = RestUtils.getUrl(UriConstant.CHANGE_DEVICE_STATUS)+BaseSharedPreferences.getInt(context,Constant.DEVICE_ID_KEY)+"/"+switchStatus;
                OkHttpUtils.getAsyn(url, new OkHttpUtils.StringCallback() {
                    @Override
                    public void onFailure(int errCode, Request request, IOException e) {
                        LogUtils.e("ControllerUtils","和后台同步开关机失败");
                    }

                    @Override
                    public void onResponse(String response) {
                        LogUtils.e("ControllerUtils","和后台同步开关机成功");
                    }
                });
            }
            //排空
            if (directive == DO_IO_EMPTYING) {
                devUtil.do_ioEmptying();
            }
            //开盖
            if (directive == DO_IO_COVER) {
                devUtil.do_ioCover();
            }
            if (directive == DO_IO_RINSE) {
//                devUtil.do_ioCover();
//                devUtil.do_ioEmptying();
                devUtil.do_ioRinse();
            }

            if (directive == DO_HOTTING) {
                devUtil.set_ioHeatEnabled(true);
            }

            if (directive == DO_TURN_OFF_HOTTING) {
                devUtil.set_ioHeatEnabled(false);
            }

            if (directive == DO_COOLING) {
                devUtil.set_ioColdEnabled(true);
            }

            if (directive == DO_TURN_OFF_COOLING) {
                devUtil.set_ioColdEnabled(false);
            }

            if (directive == SETTING) {
//                devUtil.set_ioColdEnabled(true);
               /* rIntv：冲洗间隔
                rLong：冲洗时长
                hTemp：加热温度
                cTemp：制冷温度*/
                LogUtils.e(TAG,"重新设置参数");
//                int rIntv = 75;
                int rIntv = BaseSharedPreferences.getInt(App.getInstance(), Constant.DEVICE_FLUSH_INTERVAL_KEY);
//                int rLong = 20;
                int rLong = BaseSharedPreferences.getInt(App.getInstance(), Constant.DEVICE_FLUSH_DURATION_KEY);
//                int  hTemp = 30;
                int hTemp = BaseSharedPreferences.getInt(App.getInstance(), Constant.DEVICE_HEATING_TEMP_KEY);
//                int cTemp = 10;
                int cTemp = BaseSharedPreferences.getInt(App.getInstance(), Constant.DEVICE_COOLING_TEMP_KEY);
                if (devUtil.set_ioParam(rIntv, rLong, hTemp, cTemp) == 0) {
                    LogUtils.e(TAG, "设置参数成功");
                } else {
                    LogUtils.e(TAG, "设置参数失败");
                }

                //设置音量
                AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                //获取音量

                int volumenum = BaseSharedPreferences.getInt(context,Constant.DEVICE_VOLUME_KEY);
                if(0==volumenum){
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, //音量类型
                            AudioManager.STREAM_SYSTEM,
                            AudioManager.FLAG_PLAY_SOUND
                                    | AudioManager.FLAG_SHOW_UI);
                }else{
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, //音量类型
                            volumenum,
                            AudioManager.FLAG_PLAY_SOUND
                                    | AudioManager.FLAG_SHOW_UI);
                }

                int flagHotAllDay = BaseSharedPreferences.getInt(context, Constant.DEVICE_HEATING_ALL_DAY_KEY);
                int flagCoolAllDay = BaseSharedPreferences.getInt(context, Constant.DEVICE_COOLING_ALL_DAY_KEY);
                LogUtils.e(TAG,"flagHotAllDay = "+flagHotAllDay);
                LogUtils.e(TAG,"flagCoolAllDay = "+flagCoolAllDay);
                if(0==flagHotAllDay){
//        if (false) {
                    //时间段开始加热
                    LogUtils.e(TAG,"按时间段加热开始");
                    String timeHot = BaseSharedPreferences.getString(context, Constant.DEVICE_HEATING_INTERVAL_KEY);
                    String hotBeginTime = timeHot.split("-")[0];
                    String hotEndTime = timeHot.split("-")[1];
                    Date hotTimeThread = TimeRun.tasktime(Integer.parseInt(hotBeginTime.split(":")[0]), Integer.parseInt(hotBeginTime.split(":")[1]), Integer.parseInt(hotBeginTime.split(":")[2]));
                    TimeRun timeRunstarthot = new TimeRun(context, hotTimeThread, myHandler, Constant.TIME_ALL_DAY, Constant.MSG_OTHER, Constant.TIME_START_HOTTING);
                    timeRunstarthot.startTimer();
                    //时间段停止加热
                    Date coolTimeThread = TimeRun.tasktime(Integer.parseInt(hotEndTime.split(":")[0]), Integer.parseInt(hotEndTime.split(":")[1]), Integer.parseInt(hotEndTime.split(":")[2]));
                    TimeRun timeRunstartcool = new TimeRun(context, hotTimeThread, myHandler, Constant.TIME_ALL_DAY, Constant.MSG_OTHER, Constant.TIME_END_HOTTING);
                    timeRunstartcool.startTimer();
                } else {
                    ControllerUtils.operateDevice(0, true);

                }
//        if (false) {
                if(0==flagCoolAllDay){
                    //时间段制冷开始
                    LogUtils.e(TAG,"按时间段制冷开始");
                    String timeCool = BaseSharedPreferences.getString(context, Constant.DEVICE_COOLING_INTERVAL_KEY);
                    String coolBeginTime = timeCool.split("-")[0];
                    String coolEndTime = timeCool.split("-")[1];
                    Date coolEndTimeThread = TimeRun.tasktime(Integer.parseInt(coolBeginTime.split(":")[0]), Integer.parseInt(coolBeginTime.split(":")[1]), Integer.parseInt(coolBeginTime.split(":")[2]));
                    TimeRun timeRunstartcool = new TimeRun(null, coolEndTimeThread, myHandler, Constant.TIME_ALL_DAY, Constant.MSG_OTHER, Constant.TIME_START_COOLING);
                    timeRunstartcool.startTimer();
                    //时间段制冷停止
                    Date coolTimeThread = TimeRun.tasktime(Integer.parseInt(coolEndTime.split(":")[0]), Integer.parseInt(coolEndTime.split(":")[1]), Integer.parseInt(coolEndTime.split(":")[2]));
                    TimeRun timeRunendcool = new TimeRun(null, coolEndTimeThread, myHandler, Constant.TIME_ALL_DAY, Constant.MSG_OTHER, Constant.TIME_END_COOLING);
                    timeRunendcool.startTimer();
                } else {
                    ControllerUtils.operateDevice(1, true);
                }


//                mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, max, AudioManager.STREAM_VOICE_CALL);
            }





            if (directive == OPENING) {
                devUtil.set_ioColdEnabled(true);
            }
        } catch (Exception e) {
//            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            LogUtils.e(TAG, "operateDevice: 设置参数异常，e = " + e.getMessage());
        } finally {
            comThread.setActive(true);
        }
    }


    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
//
                    break;
            }
        }
    }


}

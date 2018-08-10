package com.xhh.ysj.utils;

import android.os.Handler;
import android.serialport.DevUtil;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


import com.xhh.ysj.App;
import com.xhh.ysj.R;
import com.xhh.ysj.beans.SysDeviceWaterQualityAO;
import com.xhh.ysj.constants.Constant;
import com.xhh.ysj.constants.UriConstant;
import com.xhh.ysj.view.PopLeftOperate;
import com.xhh.ysj.view.activity.BaseActivity;

import okhttp3.Request;

/**
 * Created by Administrator on 2018/4/28 0028.
 */

public class TimeRun {

    private static final String TAG = "TimeRun";

    //    https://blog.csdn.net/qinde025/article/details/6828723
    public TimerTask task;
    private DevUtil devUtil = null;
    private Handler handler = null;

    private DbManager dbManager;
    private Date time;
    private long loopjiange = 1000 * 60 * 60 * 24;
    private BaseActivity activity;
    private Integer motCfgPpFlow;//PP棉制水总流量
    private Integer motCfgGrainCarbonFlow;//颗粒活性炭使用时间(单位L)
    private Integer motCfgPressCarbonFlow;//压缩活性炭
    private Integer motCfgPoseCarbonFlow;//后置活性炭
    private Integer motCfgRoFlow;//反渗透模

    private Integer motCfgPpFlowWarning;
    private Integer motCfgGrainCarbonFlowWarning;//颗粒活性炭使用时间(单位L)
    private Integer motCfgPressCarbonFlowWarning;//压缩活性炭
    private Integer motCfgPoseCarbonFlowWarning;//后置活性炭
    private Integer motCfgRoFlowWarning;//反渗透模

    private PopLeftOperate popLeft = null; // 监听tds时， 用的左操作面板

    /**
     * @param activity
     * @param handler
     * @param loopjiange
     * @param what
     * @param operateflag 1:水质状态
     */

    public TimeRun(final BaseActivity activity, Date time, final Handler handler, final long loopjiange, final int what, final int operateflag) {
        if (null == devUtil) {
            devUtil = new DevUtil(null);
            motCfgPpFlow = BaseSharedPreferences.getInt(activity, Constant.DEVICE_PP_FLOW_KEY);//PP棉制水总流量
            motCfgGrainCarbonFlow = BaseSharedPreferences.getInt(activity, Constant.DEVICE_GRAIN_CARBON_KEY);
            motCfgPressCarbonFlow = BaseSharedPreferences.getInt(activity, Constant.DEVICE_PRESS_CARBON_KEY);//压缩活性炭
            motCfgPoseCarbonFlow = BaseSharedPreferences.getInt(activity, Constant.DEVICE_POSE_CARBON_KEY);//后置活性炭
            motCfgRoFlow = BaseSharedPreferences.getInt(activity, Constant.DEVICE_RO_FLOW_KEY);//反渗透模

            motCfgPpFlowWarning = (int) (motCfgPpFlow * Constant.PERCENT);
            motCfgGrainCarbonFlowWarning = (int) (motCfgGrainCarbonFlow * Constant.PERCENT);
            motCfgPressCarbonFlowWarning = (int) (motCfgPressCarbonFlow * Constant.PERCENT);
            motCfgPoseCarbonFlowWarning = (int) (motCfgPoseCarbonFlow * Constant.PERCENT);
            motCfgRoFlowWarning = (int) (motCfgRoFlow * Constant.PERCENT);
        }
        this.time = time;
        this.loopjiange = loopjiange;
        this.activity = activity;
        dbManager = new XutilsInit(activity).getDb();
        this.task = new TimerTask() {
            @Override
            public void run() {
                String deviceId = "" + BaseSharedPreferences.getInt(activity, Constant.DEVICE_ID_KEY);
                switch (operateflag) {
                    case Constant.TIME_OPERATE_UPDATEWATER:
                        //上传水质
                        SysDeviceWaterQualityAO sysDeviceWaterQualityAO = new SysDeviceWaterQualityAO();
                        sysDeviceWaterQualityAO.setDeviceRawWater(devUtil.get_run_sTDS_value());
                        sysDeviceWaterQualityAO.setDevicePureWater(devUtil.get_run_sTDS_value());
                        sysDeviceWaterQualityAO.setDeviceWaterQualityTime(TimeUtils.getCurrentTime());
                        sysDeviceWaterQualityAO.setHotTemp(devUtil.get_run_hotTemp_value());
                        sysDeviceWaterQualityAO.setColdTemp(devUtil.get_run_coolTemp_value());
                        sysDeviceWaterQualityAO.setHeatingStatus(devUtil.get_run_bHot_value());
                        sysDeviceWaterQualityAO.setCoolingStatus(devUtil.get_run_bCool_value());
                        sysDeviceWaterQualityAO.setWaterPurificationStatus(devUtil.get_run_bWater_value());
                        sysDeviceWaterQualityAO.setFlushStatus(devUtil.get_run_bRinse_value());
                        sysDeviceWaterQualityAO.setRawWaterStatus(devUtil.get_run_bFault_value());
                        sysDeviceWaterQualityAO.setWaterLeakageStatus(devUtil.get_run_bLeak_value());
                        sysDeviceWaterQualityAO.setSwitchStatus(devUtil.get_run_bSwitch_value());
                        sysDeviceWaterQualityAO.setWaterCupStatus(devUtil.get_run_bCup_value());
                        if (devUtil.get_run_hotWaterSW_value()) {
                            sysDeviceWaterQualityAO.setHotWaterOutletStatus(1);
                        } else {
                            sysDeviceWaterQualityAO.setHotWaterOutletStatus(0);
                        }
                        if (devUtil.get_run_coolWaterSW_value()) {
                            sysDeviceWaterQualityAO.setColdWaterOutletStatus(1);
                        } else {
                            sysDeviceWaterQualityAO.setColdWaterOutletStatus(0);
                        }
                        if (devUtil.get_run_normalWaterSW_value()) {
                            sysDeviceWaterQualityAO.setWarmWaterOutletStatus(1);
                        } else {
                            sysDeviceWaterQualityAO.setWarmWaterOutletStatus(0);
                        }
                        //加热设备温度
                        sysDeviceWaterQualityAO.setHeatingTemp(devUtil.get_pam_hotTemp_value());
                        //制冷设备温度
                        sysDeviceWaterQualityAO.setCoolingTemp(devUtil.get_pam_coolTemp_value());
                        //单位是分钟
                        sysDeviceWaterQualityAO.setFlushInterval(devUtil.get_pam_rinseTimeLong_value());
                        sysDeviceWaterQualityAO.setFlushDuration(devUtil.get_pam_rinseInterval_value());
                        try {
                            dbManager.save(sysDeviceWaterQualityAO);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        break;
                    case Constant.TIME_OPETATE_TDS:
                        int rate = (devUtil.get_run_sTDS_value() - devUtil.get_run_oTDS_value()) / devUtil.get_run_sTDS_value();
                        if (rate > 0.9) {
                            LogUtils.d(TAG, "run: 去除率大于90");
                            if (null != popLeft) {
                                popLeft.outTdsTitle.setText(activity.getString(R.string.out_tds));
                                popLeft.outTdsValueLl.setVisibility(View.VISIBLE);
                                popLeft.tvPpmRate.setVisibility(View.GONE);
                            }
                        } else if (rate > 0.8) {
                            LogUtils.d(TAG, "run: 去除率小于90%，大于80%");
                            if (null != popLeft) {
                                popLeft.outTdsTitle.setText(activity.getString(R.string.out_tds_rate));
                                popLeft.tvPpmRate.setText(activity.getString(R.string.ppm_rate_value, rate * 100));
                                popLeft.outTdsValueLl.setVisibility(View.GONE);
                                popLeft.tvPpmRate.setVisibility(View.VISIBLE);
                            }
                        } else if (rate > 0) {
                            LogUtils.d(TAG, "run: 去除率小于80%，大于0");
                            popLeft.outTdsTitle.setText(activity.getString(R.string.out_tds));
                            popLeft.outTdsValueLl.setVisibility(View.VISIBLE);
                            popLeft.tvPpmRate.setVisibility(View.GONE);
                            activity.saveDeviceServiceNotice(Constant.NOTICE_TYPE_PURIFIED_TDS_ABNORMAL,
                                    Constant.NOTICE_LEVEL_ABNORMAL, activity.getString(R.string.notice_content_tds_abnormal));
                        } else {
                            LogUtils.d(TAG, "run: 滤芯已失效");
                            activity.moveToBreakDownActivity(App.getInstance().getString(R.string.break_down_reason_tds_abnormal));
                        }
                        break;
                    case Constant.TIME_OPETATE_UPDATESCODE:
                        String sCodeUrl = RestUtils.getUrl(UriConstant.GETTEMPQCODE) + "/" + deviceId;
                        OkHttpUtils.getAsyn(sCodeUrl, new OkHttpUtils.StringCallback() {
                            @Override
                            public void onFailure(int errCode, Request request, IOException e) {
                                LogUtils.e(TAG, "onFailure: 获取支付二维码失败，" +
                                        "errCode = " + errCode + "，request = " + request.toString());
                            }

                            @Override
                            public void onResponse(String response) {
                                LogUtils.d(TAG, "onResponse: 获取支付二维码：response = " + response);
                                JSONObject scodeobj = JSONObject.parseObject(response);
                                if (null == scodeobj) {
                                    LogUtils.e(TAG, "onResponse: 获取支付二维码：response cannot parse to json object");
                                    return;
                                }
                                if ("0".equals(scodeobj.getString("code"))) {
                                    String data = scodeobj.getString("data");
                                    BaseSharedPreferences.setString(activity, Constant.SCODEKEY, data);
                                }
                            }
                        });
                        break;
                    case Constant.TIME_OPETATE_WARNING:
                        if (devUtil.get_run_bSta_value() == DevUtil.ERR_TIMEOUT) {
                            activity.saveDeviceServiceNotice(Constant.NOTICE_TYPE_COM_NO_NETWORK,
                                    Constant.NOTICE_LEVEL_BREAK_DOWN, activity.getString(R.string.notice_content_no_com_network));
                            breakDown();
                        }

                        //水质是否异常
                        if (Constant.TDSERROR > devUtil.get_run_oTDS_value()) {
                            activity.saveDeviceServiceNotice(Constant.NOTICE_TYPE_WATER_QUALITY_UNUSUAL,
                                    Constant.NOTICE_LEVEL_BREAK_DOWN, activity.getString(R.string.notice_content_water_quality_unusual));
                            breakDown();

                        }
                        //纸杯不足
                        if (devUtil.get_run_bCup_value() == 0) {
                            activity.saveFieldServiceNotice(Constant.NOTICE_TYPE_NO_CUP,
                                    Constant.NOTICE_LEVEL_ABNORMAL, activity.getString(R.string.notice_content_no_cup));
                        }
                        //耗水量异常
                        //漏电
                        if (devUtil.get_run_bLeak_value() == 02) {
                            activity.saveDeviceServiceNotice(Constant.NOTICE_TYPE_WATER_LEAK,
                                    Constant.NOTICE_LEVEL_BREAK_DOWN, activity.getString(R.string.notice_content_water_leak));
                            breakDown();
                        }
                        //原水缺水
                        if (devUtil.get_run_bFault_value() == 02) {
                            activity.saveDeviceServiceNotice(Constant.NOTICE_TYPE_RAW_WATER_LACK,
                                    Constant.NOTICE_LEVEL_BREAK_DOWN, activity.getString(R.string.notice_content_raw_water_lack));
                            breakDown();
                        }
                       //热水温度超高
                        // run_bHotAlarm
                        if(devUtil.get_run_bHotAlarm_value()==1){
                            activity.saveDeviceServiceNotice(Constant.NOTICE_TYPE_BHOTALARM,
                                    Constant.NOTICE_LEVEL_BREAK_DOWN, activity.getString(R.string.notice_content_raw_water_lack));
//                            breakDown();
                        }
                        //冷水温度超低
                        if(devUtil.get_run_bCoolAlarm_value()==1){
                            activity.saveDeviceServiceNotice(Constant.NOTICE_TYPE_BCOOLALARM,
                                    Constant.NOTICE_LEVEL_BREAK_DOWN, activity.getString(R.string.notice_content_raw_water_lack));
                        }
                        //热水加热异常，规定时间没有达到温度
                        if(devUtil.get_run_bHotError_value()==1){
                            activity.saveDeviceServiceNotice(Constant.NOTICE_BHOTERROR,
                                    Constant.NOTICE_LEVEL_BREAK_DOWN, activity.getString(R.string.notice_content_raw_water_lack));
                        }

                        //制冷异常，规定时间没有达到温度
                        if(devUtil.get_run_bCoolError_value()==1){
                            activity.saveDeviceServiceNotice(Constant.NOTICE_BCOOLERROR,
                                    Constant.NOTICE_LEVEL_BREAK_DOWN, activity.getString(R.string.notice_content_raw_water_lack));
                        }

                        //run_bFlowError 流量计故障
                        if(devUtil.get_run_bFlowError_value()==1){
                            activity.saveDeviceServiceNotice(Constant.NOTICE_BFLOWERROR,
                                    Constant.NOTICE_LEVEL_BREAK_DOWN, activity.getString(R.string.notice_content_raw_water_lack));
                        }

                        //run_bTDSError  TDS超过200
                        if(devUtil.get_run_bTDSError_value()==1){
                            activity.saveDeviceServiceNotice(Constant.NOTICE_BTDSERROR,
                                    Constant.NOTICE_LEVEL_BREAK_DOWN, activity.getString(R.string.notice_content_raw_water_lack));
                        }

                        //NOTICE_BWATERERROR  制水故障、
                        if(devUtil.get_run_bWater_value()==1){
                            activity.saveDeviceServiceNotice(Constant.NOTICE_BWATERERROR,
                                    Constant.NOTICE_LEVEL_BREAK_DOWN, activity.getString(R.string.notice_content_raw_water_lack));
                        }

                        //NOTICE_BUVERROR UV灯故障
                        if(devUtil.get_run_bUVError_value()==1){
                            activity.saveDeviceServiceNotice(Constant.NOTICE_BUVERROR,
                                    Constant.NOTICE_LEVEL_BREAK_DOWN, activity.getString(R.string.notice_content_raw_water_lack));
                        }

                        //NOTICE_BLEVELSWERROR 水位开关故障
                        if(devUtil.get_run_bLevelSWError_value() ==1){
                            activity.saveDeviceServiceNotice(Constant.NOTICE_BLEVELSWERROR,
                                    Constant.NOTICE_LEVEL_BREAK_DOWN, activity.getString(R.string.notice_content_raw_water_lack));
                        }

                        if(devUtil.get_run_bLevelHigh_value() ==1){
                            activity.saveDeviceServiceNotice(Constant.NOTICE_BLEVELHIGH,
                                    Constant.NOTICE_LEVEL_BREAK_DOWN, activity.getString(R.string.notice_content_raw_water_lack));
                        }

                        //增加预警信  冷传感器 ,01正常，02开路 开路冷水温度闪烁显示E2
//                        if(devUtil.get_run_bCSensorErr_value()==2){
//                            breakDown();
//                            ControllerUtils.operateDevice(3,false);
//                            if(null!=popLeft){
//                              popLeft.coolwatertext.setText("E2");
//                            }
//                            Constant.WENDU_COOL_LOCK = true;
//                        }else{
//                            ControllerUtils.operateDevice(3,true);
////                            popLeft.hotwatertext.setText(Constant.AD_TYPE_FREE);
//                            Constant.WENDU_COOL_LOCK = false;
//                        }
//
//                        //热传感器
//                        if(devUtil.get_run_bHSensorErr_value()==2){
//                            breakDown();
//                            ControllerUtils.operateDevice(3,false);
//                            popLeft.hotwatertext.setText("E2");
//                            Constant.WENDU_HOT_LOCK = true;
//                        }else{
//                            ControllerUtils.operateDevice(3,true);
//                            Constant.WENDU_HOT_LOCK = false;
//                        }
//
//                        //水位极高 01没有达到最高的水位的， 1已经达到最高水位
//                        if(devUtil.get_run_bExLimit_value()==1){
//                            //TODO 滴滴滴5声
//                        }else{
//                            //TODO 恢复不知道怎么恢复
//                        }
//                        get_run_bCSensorErr_value

                        break;
                    case Constant.TIME_START_HOTTING:
                        ControllerUtils.operateDevice(0, true);
                        break;
                    case Constant.TIME_END_HOTTING:
                        ControllerUtils.operateDevice(0, false);
                        break;
                    case Constant.TIME_START_COOLING:
                        ControllerUtils.operateDevice(1, true);
                        break;
                    case Constant.TIME_END_COOLING:
                        ControllerUtils.operateDevice(1, false);
                        break;
                    case Constant.TIME_DEVICE_ENDTIME:
                        LogUtils.e(TAG, "调用机器最后在线时间");
                        String url = RestUtils.getUrl(UriConstant.DEVICE_ENDTIME + BaseSharedPreferences.getInt(activity, Constant.DEVICE_ID_KEY));
                        OkHttpUtils.getAsyn(url, new OkHttpUtils.StringCallback() {
                            @Override
                            public void onFailure(int errCode, Request request, IOException e) {

                            }

                            @Override
                            public void onResponse(String response) {

                            }
                        });
                        break;
                }
            }
        };
    }


    public void breakDown() {
//        ControllerUtils.operateDevice(3, false);
//        Intent it = new Intent(activity, BreakDownActivity.class);
//        activity.startActivity(it);

    }

    public void startTimer() {
        Timer timer = new Timer(true);
        timer.schedule(task, time, loopjiange);
    }

    ;


    public boolean filterOver() {
        Boolean filterOverflag = true;
        String[][] data = devUtil.toArray();
        if (motCfgPpFlow - Integer.parseInt(data[17][1]) < motCfgPpFlowWarning) {
            filterOverflag = false;
        }

        if (motCfgGrainCarbonFlow - Integer.parseInt(data[17][1]) < motCfgGrainCarbonFlowWarning) {
            filterOverflag = false;
        }

        if (motCfgPressCarbonFlow - Integer.parseInt(data[17][1]) < motCfgPressCarbonFlowWarning) {
            filterOverflag = false;
        }

        if (motCfgPoseCarbonFlow - Integer.parseInt(data[17][1]) < motCfgPoseCarbonFlowWarning) {
            filterOverflag = false;
        }

        if (motCfgRoFlow - Integer.parseInt(data[17][1]) < motCfgRoFlowWarning) {
            filterOverflag = false;
        }


//            if (monitor.getMotCfgRoFlow() - Integer.parseInt(data[17][1]) < Constant.MOT_CFG_RO_FLOW) {
//                monitor.setMotCfgRoFlow(monitor.getMotCfgRoFlow() - Integer.parseInt(data[17][1]));
//                SysDeviceMonitorConfig_dbOperate.update(monitor);
//                filterOverflag = false;
//            }
        return filterOverflag;
    }


    public boolean filterend() {

        Boolean filterend = false;
        String[][] data = devUtil.toArray();
        if (motCfgPpFlow - Integer.parseInt(data[17][1]) < 0) {
            filterend = true;
        }

        if (motCfgGrainCarbonFlow - Integer.parseInt(data[17][1]) < 0) {
            filterend = true;
        }

        if (motCfgPressCarbonFlow - Integer.parseInt(data[17][1]) < 0) {
            filterend = true;
        }

        if (motCfgRoFlow - Integer.parseInt(data[17][1]) < 0) {
            filterend = true;
        }


//            if (monitor.getMotCfgRoFlow() - Integer.parseInt(data[17][1]) < Constant.MOT_CFG_RO_FLOW) {
//                monitor.setMotCfgRoFlow(monitor.getMotCfgRoFlow() - Integer.parseInt(data[17][1]));
//                SysDeviceMonitorConfig_dbOperate.update(monitor);
//                filterOverflag = false;
//            }
        return filterend;
    }


    public void cancelTimer() {
        this.task.cancel();
    }

    public static Date tasktime(int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour); //凌晨1点
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        Date date = calendar.getTime(); //第一次执行定时任务的时间
        return date;
    }

    public void setPopLeft(PopLeftOperate popLeft) {
        this.popLeft = popLeft;
    }
}

package com.xhh.ysj.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.serialport.DevUtil;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.alibaba.fastjson.JSONObject;
import com.xhh.ysj.beans.WaterSaleDetailAO;
import com.xhh.ysj.manager.ThreadManager;
import com.xhh.ysj.processpreserve.ComThread;
import com.xhh.ysj.R;
import com.xhh.ysj.beans.DispenserCache;
import com.xhh.ysj.constants.Constant;
import com.xhh.ysj.constants.UriConstant;
import com.xhh.ysj.processpreserve.DaemonService;
import com.xhh.ysj.utils.BaseSharedPreferences;
import com.xhh.ysj.utils.CommonUtil;
import com.xhh.ysj.utils.CountDownUtil;
import com.xhh.ysj.utils.LogUtils;
import com.xhh.ysj.utils.OkHttpUtils;
import com.xhh.ysj.utils.RestUtils;
import com.xhh.ysj.utils.TimeRun;
import com.xhh.ysj.view.activity.BaseActivity;

import okhttp3.Request;

/**
 * 自定义的PopupWindow
 */
public class PopRightOperate extends PopupWindow implements View.OnClickListener {

    private static final String TAG = "PopRightOperate";

    private static final String DevPath = "/dev/ttyS3";//默认串口
    private static final int Baudrate = 115200;//默认波特率
    private static final int LoopIdle = 50;//线程空闲时间ms
//    private static final int PollTime = 800;//轮询get_ioRunData()时间间隔ms

    private FrameLayout btnQuit;
    private TextView quitCountDown;
    public static ImageView hotWater;//取热水
    private ImageView warmWater;//温水
    private ImageView coolWater;//冷水
    private ImageView getCup;//取纸杯

    private BaseActivity activity;
    private CountDownUtil countDownUtil;
    private int countDownSec;  // 多少秒无操作则退出
    private DevUtil devUtil;
    private ComThread comThread = DaemonService.comThread;
    public static boolean sendWaterFlag = false;  // 是否正在取水
    private List<WaterSaleDetailAO> waterSaleDetailAOList = new ArrayList<>();
    private WaterSaleDetailAO waterSaleDetailAO;
    private int waterRecordType = 10;//售水模式
    private int operateType;  // 取水操作（1：饮水；2：出杯）
    private boolean isFreeDrink = true;//售水模式（true：免费喝水，false：扫码喝水）
    //    private int cupNum = 0;
    private int totalFlow = 0;
    private TimerTask task;
    private Timer timer;


    // TODO: 2018/6/15 0015 这个handler没有写回收
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_QUIT_DRINK_INTERFACE:
                    quitOperateUI();
                    break;
                case Constant.MSG_CHECK_OVERFLOW:
                    LogUtils.e(TAG, "单次出水的出水量" + BaseSharedPreferences.getInt(activity, Constant.MAX_GET_WATER_CAPACITY_KEY));
                    LogUtils.e(TAG, "所有操作最大的出水量" + BaseSharedPreferences.getInt(activity, Constant.MAX_CONSUME_CAPACITY_KEY));
                    int allwaterflow = 0;
                    if (null != waterSaleDetailAOList && 0 != waterSaleDetailAOList.size()) {
                        for (int i = 0; i < waterSaleDetailAOList.size(); i++) {
                            WaterSaleDetailAO waterSaleDetailAO = waterSaleDetailAOList.get(i);
                            if (null != waterSaleDetailAO) {

                                allwaterflow = allwaterflow + (null == waterSaleDetailAO.getWaterFlow() ? 0 : waterSaleDetailAO.getWaterFlow());
                            }
                        }
                    }
                    LogUtils.e(TAG, "获取的所有出水量" + allwaterflow);
//                    Toast.makeText(activity, "获取的所有出水量 = " + allwaterflow, Toast.LENGTH_SHORT).show();
                    //进行uI操作
                    if (null == DispenserCache.userIdTemp) {
                        if (BaseSharedPreferences.getInt(activity, Constant.MAX_GET_WATER_CAPACITY_KEY) - devUtil.get_run_waterFlow_value() <= 0) {
                            LogUtils.d(TAG, "handleMessage: 能喝水的max流量：" + devUtil.get_run_waterFlow_value());
                            quitOperateUI();
                        } else {
                            LogUtils.e(TAG, "没有超过");
                        }
                    } else {
                        //取水中
                        if (BaseSharedPreferences.getInt(activity, Constant.MAX_GET_WATER_CAPACITY_KEY) - allwaterflow < 0) {
                            LogUtils.d(TAG, "handleMessage: 能喝水的max流量：" + devUtil.get_run_waterFlow_value());
                            quitOperateUI();
                        } else {
                            LogUtils.e(TAG, "没有超过");
                        }
                        //取水结束的监控
                        if (Constant.MAX_CONSUME_CAPACITY_DEFAULT - totalFlow < 0) {
                            LogUtils.d(TAG, "handleMessage: 能喝水的totalFlow：" + totalFlow);
                            quitOperateUI();
                            singlequi(false);
                        } else {
                            LogUtils.e(TAG, "总量没有超出");
                        }
                    }
                    break;
            }
        }
    };

    public PopRightOperate(final BaseActivity activity) {
        this.activity = activity;
        if (null == comThread) {
//            Toast.makeText(activity, "comthread为空", Toast.LENGTH_SHORT).show();
            comThread = new ComThread(activity, null);
        }
        initView(activity);
        initCountDown();
    }

    public void showPopupWindow(View parent) {
        timingToQuit(true);
//        Toast.makeText(activity, "单次最大出水量 = " + BaseSharedPreferences.getInt(activity, Constant.MAX_GET_WATER_CAPACITY_KEY), Toast.LENGTH_SHORT).show();
        if (!this.isShowing()) {
            this.showAtLocation(parent, Gravity.RIGHT, 0, 0);
        }
    }

    private void initView(BaseActivity activity) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.pop_right, null);
        // 获取PopupWindow的宽高
        int h = activity.getWindowManager().getDefaultDisplay().getHeight();
        int w = activity.getWindowManager().getDefaultDisplay().getWidth();
        // 设置PopupWindow的View
        this.setContentView(contentView);
        // 设置PopupWindow弹出窗体的宽高
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置PopupWindow弹出窗体可点击（下面两行代码必须同时出现）
//        this.setFocusable(true);
        this.setOutsideTouchable(false); // 当点击外围的时候隐藏PopupWindow
        // 刷新状态
        this.update();
        // 设置PopupWindow的背景颜色为半透明的黑色
        ColorDrawable dw = new ColorDrawable(Color.parseColor("#99022641"));
        this.setBackgroundDrawable(dw);
        // 设置PopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.PopWindowAnimStyle);

        // 退出按钮
        btnQuit = contentView.findViewById(R.id.pop_right_quit_fl);
        btnQuit.setOnClickListener(this);
        // 退出计时
        quitCountDown = contentView.findViewById(R.id.pop_right_quit_tv);
        countDownSec = BaseSharedPreferences.getInt(activity, Constant.OPERATION_COUNT_DOWN_KEY);
        countDownSec = 0 >= countDownSec ? Constant.OPERATION_COUNT_DOWN_DEFAULT : countDownSec;
        quitCountDown.setText(activity.getString(R.string.operate_quit_time, countDownSec));
        // 热水控制
        hotWater = contentView.findViewById(R.id.pop_right_hot_water);
        hotWater.setOnClickListener(this);
        // 温水控制
        warmWater = contentView.findViewById(R.id.pop_right_warm_water);
        warmWater.setOnClickListener(this);
        // 冷水控制
        coolWater = contentView.findViewById(R.id.pop_right_cool_water);
        coolWater.setOnClickListener(this);
        // 出杯按钮
        getCup = contentView.findViewById(R.id.pop_right_get_cup);
        getCup.setOnClickListener(this);
    }

    private void initCountDown() {
        countDownUtil = CountDownUtil.getCountDownTimer()
                // 倒计时总时间
                .setMillisInFuture(countDownSec * 1000)
                // 每隔多久回调一次onTick
                .setCountDownInterval(1000)
                // 每回调一次onTick执行
                .setTickDelegate(new CountDownUtil.TickListener() {
                    @Override
                    public void onTick(long mMillisUntilFinished) {
                        int leftSec = (int) (mMillisUntilFinished / 1000);
                        LogUtils.d(TAG, "onTick: left = " + mMillisUntilFinished);
                        quitCountDown.setText(activity.getString(R.string.operate_quit_time, leftSec));
                    }
                })
                // 结束倒计时执行
                .setFinishDelegate(new CountDownUtil.FinishListener() {
                    @Override
                    public void onFinish() {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isFastClick()) {
            Toast.makeText(activity, activity.getString(R.string.operate_too_fast), Toast.LENGTH_SHORT).show();
            return;
        }
//        devUtil = ComThread.devUtil;
//        if (devUtil.isComOpened()) {
////            Toast.makeText(activity, "通讯是ok的", Toast.LENGTH_SHORT).show();
//        }

        startSerialComm();
        timingToQuit(false);
        operateType = Constant.POP_RIGHT_OPERATE_GET_WATER;
        fillBaseSaleRecord();
        switch (v.getId()) {
            case R.id.pop_right_quit_fl:
                quitOperateUI();
                break;
            case R.id.pop_right_hot_water:
                if (!getWater(Constant.GET_HOT_WATER, hotWater))
                    LogUtils.e(TAG, "onClick: 取热水失败");
                break;
            case R.id.pop_right_warm_water:
                if (!getWater(Constant.GET_WARM_WATER, warmWater))
                    LogUtils.e(TAG, "onClick: 取温水失败");
                break;
            case R.id.pop_right_cool_water:
                if (!getWater(Constant.GET_COOL_WATER, coolWater))
                    LogUtils.e(TAG, "onClick: 取冷水失败");
                break;
            case R.id.pop_right_get_cup:
                operateType = Constant.POP_RIGHT_OPERATE_GET_CUP;
                if (!getWater(Constant.GET_CUP, getCup))
                    LogUtils.e(TAG, "onClick: 出杯失败");
                break;
        }
    }

    /**
     * 开始计时（规定时间后退出操作界面）
     * @param isFirstComing 是否是进入页面时的计时
     */
    private void timingToQuit(boolean isFirstComing) {
        if (TextUtils.isEmpty(DispenserCache.userIdTemp) && !isFirstComing) {
            return;
        }
        countDownUtil.cancel();
        countDownUtil.start();
        if (handler.hasMessages(Constant.MSG_QUIT_DRINK_INTERFACE)) {
            handler.removeMessages(Constant.MSG_QUIT_DRINK_INTERFACE);
        }
        handler.sendEmptyMessageDelayed(Constant.MSG_QUIT_DRINK_INTERFACE,
                countDownSec * 1000);
    }

    /**
     * 启动串口通讯
     */
    private void startSerialComm() {
        if (null == devUtil) {
            devUtil = new DevUtil(null);
        }

        if (null == comThread) {
            comThread = new ComThread(activity, null);
        }
        if (devUtil.isComOpened()) {
//                        Toast.makeText(activity, "通讯启动", Toast.LENGTH_SHORT).show();
        } else {
            comThread.setActive(false);
            try {
                File dev = new File(DevPath);
                boolean r = devUtil.openCOM(dev, Baudrate, 0);
                if (!r) {
//                                Toast.makeText(activity, "通讯失败", Toast.LENGTH_SHORT).show();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 填写售水记录的基本信息（userId, deviceId, drinkMode）
     */
    private void fillBaseSaleRecord() {
        waterSaleDetailAO = new WaterSaleDetailAO();
        // 设置userId，drinkMode
        if (TextUtils.isEmpty(DispenserCache.userIdTemp)) {
            LogUtils.e(TAG, "fillBaseSaleRecord: 喝水模式：免费饮水");
//            Toast.makeText(activity, "DispenserCache.userIdTemp"+DispenserCache.userIdTemp, Toast.LENGTH_SHORT).show();
            isFreeDrink = true;
            waterSaleDetailAO.setUserId(0);
            waterSaleDetailAO.setProductChargMode(Constant.DRINK_MODE_DRINK_FREE);
        } else {
//            Toast.makeText(activity, "DispenserCache.userIdTemp"+DispenserCache.userIdTemp, Toast.LENGTH_SHORT).show();
            LogUtils.e(TAG, "fillBaseSaleRecord: 喝水模式：付费/租赁/买断");
            isFreeDrink = false;
            waterSaleDetailAO.setUserId(Integer.parseInt(DispenserCache.userIdTemp));
            waterSaleDetailAO.setProductChargMode(BaseSharedPreferences.getInt(activity, Constant.DRINK_MODE_KEY));
        }
        // 设置deviceId
        if (0 == BaseSharedPreferences.getInt(activity, Constant.DEVICE_ID_KEY)) {
            LogUtils.e(TAG, "fillBaseSaleRecord: 没有deviceId");
        } else {
            waterSaleDetailAO.setDeviceId(BaseSharedPreferences.getInt(activity, Constant.DEVICE_ID_KEY));
        }
    }

    /**
     * 出水
     *
     * @param waterType 热水/温水/冷水/出杯
     * @param view      对应的
     * @return
     */
    private boolean getWater(int waterType, ImageView view) {

//        startSerialComm();
        LogUtils.d(TAG, "getWater: waterType = " + waterType);
        waterRecordType = waterType;
        String getWaterText = "";
        int getWaterResId = 0;
        int stopWaterResId = 0;
        int cupNum = 0;
        switch (waterType) {
            case Constant.GET_HOT_WATER:
                getWaterText = activity.getString(R.string.hot_water);
                getWaterResId = R.drawable.hot_water;
                stopWaterResId = R.drawable.hot_water;
                break;
            case Constant.GET_WARM_WATER:
                getWaterText = activity.getString(R.string.warm_water);
                getWaterResId = R.drawable.warm_water;
                stopWaterResId = R.drawable.hot_water_stop;
                break;
            case Constant.GET_COOL_WATER:
                getWaterText = activity.getString(R.string.cool_water);
                getWaterResId = R.drawable.cool_water;
                stopWaterResId = R.drawable.cool_water_stop;
                break;
            case Constant.GET_CUP:
                getWaterText = activity.getString(R.string.get_cup);
                getWaterResId = R.drawable.get_cup;
                stopWaterResId = R.drawable.get_cup;
                break;
        }
        if (null != comThread) {
            comThread.setActive(false);
        }

        try {
            if (view.getTag().toString().equals(getWaterText)) {
                // 情况一：未出水，点击变成出水（此种情况先按点击设置sendWaterFlag，然后按实际情况设置sendWaterFlag）
                // 正在取水则无法操作
                if (sendWaterFlag) {
                    Toast.makeText(activity, activity.getString(R.string.get_watering), Toast.LENGTH_SHORT).show();
                    return false;
                }
                LogUtils.d(TAG, "getWater: sendWaterFlag要变为true---1");
                sendWaterFlag = true;
                if (0 != stopWaterResId) {
                    LogUtils.d(TAG, "getWater: 图标变为停止出水");
                    view.setImageResource(stopWaterResId);
                }
                // 出杯，则做出杯操作
                if (Constant.GET_CUP == waterType) {
                    LogUtils.d(TAG, "getWater: sendWaterFlag要变为false---2");
                    sendWaterFlag = false;
                    if (devUtil.do_ioCup() == 0) {
                        getCup.setTag(activity.getString(R.string.get_cup));
                        LogUtils.e(TAG, "getWater: 出杯成功");
                        cupNum = 1;
                    } else {
                        getCup.setTag(activity.getString(R.string.get_cup));
                        LogUtils.e(TAG, "getWater: 出杯失败");
                        cupNum = 0;
                    }
                    settleAndFillRecord(false, cupNum);
                    return true;
                }
                // 出热水，则跳转
                if (Constant.GET_HOT_WATER == waterType) {
                    LogUtils.d(TAG, "getWater: sendWaterFlag要变为false---3");
                    sendWaterFlag = false;
                    activity.showPopLeftOperate();
                    activity.showPopRightOperate();
                    activity.showPopWarning();
                    return true;
                }
                // 其他，则做取水操作
                int sw = 1;
                if (devUtil.do_ioWater(waterType, sw) == 0) {
                    LogUtils.e(TAG, "getWater: 出水成功");
                    LogUtils.d(TAG, "getWater: sendWaterFlag要变为true---4");
                    sendWaterFlag = true;
                } else {
                    LogUtils.e(TAG, "getWater: 出水失败");
                    LogUtils.d(TAG, "getWater: sendWaterFlag要变为false---5");
                    sendWaterFlag = false;
                    if (0 != stopWaterResId) {
                        LogUtils.d(TAG, "getWater: 图标变为喝水");
                        view.setImageResource(getWaterResId);
                    }
                }
                view.setTag(activity.getString(R.string.stop_get_water));
            } else {
                // 情况二：正在出水，点击停水（此种情况按实际情况设置sendWaterFlag）

                if (0 != getWaterResId) {

                    LogUtils.d(TAG, "getWater: 图标变为喝水");
                    view.setImageResource(getWaterResId);
                }
                int sw = 2;
                byte b = devUtil.do_ioWater(waterType, sw);
//                Toast.makeText(activity, "停止成功"+b, Toast.LENGTH_SHORT).show();
                if (b == 0) {
                    Toast.makeText(activity, "变图标停止:"+b, Toast.LENGTH_SHORT).show();
                    LogUtils.e(TAG, "getWater: 出水停止成功");
                    LogUtils.d(TAG, "getWater: sendWaterFlag要变为false---6");
                    sendWaterFlag = false;
//                    else if(b == 1 || b==5){
                } else {
                    LogUtils.e(TAG, "getWater: 出水停止失败");
                    LogUtils.d(TAG, "getWater: sendWaterFlag要变为true---7");
                    sendWaterFlag = true;
                    if (0 != stopWaterResId) {
                        LogUtils.d(TAG, "getWater: 图标变为停止出水");
                        view.setImageResource(stopWaterResId);
                    }
                }
                view.setTag(getWaterText);
            }
//            listenWaterYield();
            settleAndFillRecord(false, cupNum);
        } catch (Exception e) {
            LogUtils.e(TAG, "getWater: 用水/出杯操作报错，e = " + e.getMessage());
        } finally {
            comThread.setActive(true);
        }
        return true;
    }

    /**
     * 监听出水量
     */
    private void listenWaterYield() {
        if (sendWaterFlag) {
            Time warningTime = new Time();
            warningTime.setToNow();
            task = new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(Constant.MSG_CHECK_OVERFLOW);
                }
            };
            Date time = TimeRun.tasktime(warningTime.hour, warningTime.minute, warningTime.second);
            timer = new Timer(true);
            timer.schedule(task, time, 3000);
        }
    }

    /**
     * 结算并填写售水记录
     */
    private void settleAndFillRecord(boolean isTurnOffSettle, int cupNum) {
        // 停止取水时
        LogUtils.d(TAG, "getWater: sendWaterFlag结算---" + sendWaterFlag);
        if (!sendWaterFlag) {
            // 结算出水量或纸杯
            int singleWaterFlow = devUtil.get_run_waterFlow_value();
            switch (waterRecordType) {
                case Constant.GET_HOT_WATER:
                    waterSaleDetailAO.setWaterColdFlow(0);
                    waterSaleDetailAO.setWaterWarmFlow(0);
                    waterSaleDetailAO.setWaterHotFlow(singleWaterFlow);
                    waterSaleDetailAO.setWaterCupNum(0);
                    break;
                case Constant.GET_WARM_WATER:
                    waterSaleDetailAO.setWaterColdFlow(0);
                    waterSaleDetailAO.setWaterWarmFlow(singleWaterFlow);
                    waterSaleDetailAO.setWaterHotFlow(0);
                    waterSaleDetailAO.setWaterCupNum(0);
                    break;
                case Constant.GET_COOL_WATER:
                    waterSaleDetailAO.setWaterColdFlow(singleWaterFlow);
                    waterSaleDetailAO.setWaterWarmFlow(0);
                    waterSaleDetailAO.setWaterHotFlow(0);
                    waterSaleDetailAO.setWaterCupNum(0);
                    break;
                case Constant.GET_CUP:
                    waterSaleDetailAO.setWaterWarmFlow(0);
                    waterSaleDetailAO.setWaterHotFlow(0);
                    waterSaleDetailAO.setWaterColdFlow(0);
                    waterSaleDetailAO.setWaterCupNum(cupNum);
                    break;
            }
            LogUtils.d(TAG, "settleAndFillRecord: waterRecordType（热温冷杯） = " + waterRecordType);
            if (Constant.GET_CUP != waterRecordType) {
                waterSaleDetailAO.setWaterFlow(singleWaterFlow);
                LogUtils.d(TAG, "settleAndFillRecord: 本次流量 = " + singleWaterFlow);
                totalFlow = totalFlow + singleWaterFlow;
            } else {
                LogUtils.d(TAG, "settleAndFillRecord: 本次出杯 = " + cupNum);
            }
            if (null != waterSaleDetailAO &&
                    (null != waterSaleDetailAO.getWaterHotFlow()
                            || null != waterSaleDetailAO.getWaterWarmFlow()
                            || null != waterSaleDetailAO.getWaterColdFlow()
                            || null != waterSaleDetailAO.getWaterCupNum())) {
                waterSaleDetailAOList.add(waterSaleDetailAO);
            }
            // 不再监听流量
            if (null != timer) {
                timer.cancel();
            }
            // 如果是免费喝水，则退出界面
            if (!isTurnOffSettle && isFreeDrink) {
                quitOperateUI();
            }
        }
    }

    /**
     * 退出操作界面
     */
    private void quitOperateUI() {
        LogUtils.d(TAG, "quitOperateUI: 退出喝水界面");
        DispenserCache.isFreeAdDone = false;
        if (null != task) {
            task.cancel();
        }
        countDownUtil.cancel();
        handler.removeMessages(Constant.MSG_QUIT_DRINK_INTERFACE);
        activity.dismissOperatePop();
        activity.showPopWantWater();
        turnOff();
    }

    private void singlequi(boolean isFreeDrink) {
        if (null != task) {
            task.cancel();
        }
        turnoffContinue(true);
    }

    /**
     * 关闭出水，准备数据，不结算
     */
    public void turnoffContinue(boolean drinkType) {
        // UI
        coolWater.setImageResource(R.drawable.cool_water);
        coolWater.setTag(activity.getString(R.string.cool_water));
        warmWater.setTag(activity.getString(R.string.warm_water));
        warmWater.setImageResource(R.drawable.warm_water);
        hotWater.setImageResource(R.drawable.hot_water);
        hotWater.setTag(activity.getString(R.string.hot_water));
        // 水还没停，停下
        LogUtils.d(TAG, "getWater: sendWaterFlag关闭时---" + sendWaterFlag);
        // 无论sendWaterFlag是否为true，都再执行一次停止操作，防止报文与实际情况不符
        try {
            int sw = 2;
            // 关闭热水
            if (0 != devUtil.do_ioWater(Constant.GET_HOT_WATER, sw)) {
                devUtil.do_ioWater(Constant.GET_HOT_WATER, sw);
            }
            // 关闭温水
            if (0 != devUtil.do_ioWater(Constant.GET_WARM_WATER, sw)) {
                devUtil.do_ioWater(Constant.GET_WARM_WATER, sw);
            }
            // 关闭冷水
            if (0 != devUtil.do_ioWater(Constant.GET_COOL_WATER, sw)) {
                devUtil.do_ioWater(Constant.GET_COOL_WATER, sw);
            }
            // 关闭所有
            if (0 != devUtil.do_ioWater(Constant.GET_ALL, sw)) {
                devUtil.do_ioWater(Constant.GET_ALL, sw);
                //                Toast.makeText(activity, "停止出水失败", Toast.LENGTH_SHORT).show();
                startSerialComm();
                //                    turnOff();
                devUtil.do_ioWater(Constant.GET_ALL, sw);
            } else {
                //                Toast.makeText(activity, "停止出水成功", Toast.LENGTH_SHORT).show();
            }
            //            listenWaterYield();
            if (sendWaterFlag) {
                sendWaterFlag = false;
                settleAndFillRecord(drinkType, 0);
            }
        } catch (Exception e) {
            //            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            comThread.setActive(true);
        }

    }

    /**
     * 关闭出水
     */
    private void turnOff() {
        // UI
        coolWater.setImageResource(R.drawable.cool_water);
        coolWater.setTag(activity.getString(R.string.cool_water));
        warmWater.setTag(activity.getString(R.string.warm_water));
        warmWater.setImageResource(R.drawable.warm_water);
        hotWater.setImageResource(R.drawable.hot_water);
        hotWater.setTag(activity.getString(R.string.hot_water));
        // 无论sendWaterFlag是否为true，都再执行一次停止操作，防止报文与实际情况不符
        LogUtils.d(TAG, "turnOff: sendWaterFlag关闭时---" + sendWaterFlag);
        ThreadManager.getInstance().createShortPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int sw = 2;
                    // 关闭热水
                    if (0 != devUtil.do_ioWater(Constant.GET_HOT_WATER, sw)) {
                        devUtil.do_ioWater(Constant.GET_HOT_WATER, sw);
                    }
                    // 关闭温水
                    if (0 != devUtil.do_ioWater(Constant.GET_WARM_WATER, sw)) {
                        devUtil.do_ioWater(Constant.GET_WARM_WATER, sw);
                    }
                    // 关闭冷水
                    if (0 != devUtil.do_ioWater(Constant.GET_COOL_WATER, sw)) {
                        devUtil.do_ioWater(Constant.GET_COOL_WATER, sw);
                    }
                    // 关闭所有
//                if (0 != devUtil.do_ioWater(Constant.GET_ALL, sw)) {
//                    devUtil.do_ioWater(Constant.GET_ALL, sw)
//                    startSerialComm();
////                    turnOff();
//                    devUtil.do_ioWater(Constant.GET_ALL, sw);
//                }else{
//                    Toast.makeText(activity, "停止出水成功", Toast.LENGTH_SHORT).show();
//                }
//                listenWaterYield();
                    if (sendWaterFlag) {
                        sendWaterFlag = false;
                        settleAndFillRecord(true, 0);
                    }
                } catch (Exception e) {
//                Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
                } finally {
                    comThread.setActive(true);
                }
            }
        });

        String saomaGetWater = RestUtils.getUrl(UriConstant.WATERSALE);
        String postData = JSONObject.toJSONString(waterSaleDetailAOList);
        LogUtils.e(TAG,"postData"+postData);
        DispenserCache.userIdTemp = null;
//        Toast.makeText(activity, "结算", Toast.LENGTH_SHORT).show();
        waterSaleDetailAOList.clear();
        if ("[]".equals(postData)) {
            LogUtils.e(TAG, "turnOff: 没有喝水");
        } else {
            LogUtils.e(TAG, "turnOff: 上报喝水量..");
            OkHttpUtils.postAsyn(saomaGetWater, new OkHttpUtils.StringCallback() {
                @Override
                public void onFailure(int errCode, Request request, IOException e) {
                    LogUtils.e(TAG, "turnOff: 上报喝水量失败");
                }

                @Override
                public void onResponse(String response) {
                    LogUtils.e(TAG, "上报喝水量：response = " + response);
                }

            }, postData);
        }


    }
}

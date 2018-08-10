package com.xhh.ysj.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.serialport.DevUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.xhh.ysj.App;
import com.xhh.ysj.beans.SysDeviceNoticeAO;
import com.xhh.ysj.utils.LogUtils;
import com.xhh.ysj.utils.OkHttpUtils;
import com.xhh.ysj.utils.RestUtils;
import com.xhh.ysj.utils.UploadLocalData;
import com.xhh.ysj.utils.XutilsInit;
import com.xhh.ysj.R;
import com.xhh.ysj.beans.AdvsPlayRecode;
import com.xhh.ysj.processpreserve.ComThread;
import com.xhh.ysj.processpreserve.DaemonService;
import com.xhh.ysj.beans.AdvsVideo;
import com.xhh.ysj.beans.DispenserCache;
import com.xhh.ysj.beans.PushEntity;
import com.xhh.ysj.broadcast.ConnectionChangeReceiver;
import com.xhh.ysj.broadcast.MessageReceiver;
import com.xhh.ysj.constants.Constant;
import com.xhh.ysj.constants.UriConstant;
import com.xhh.ysj.interfaces.DownloadCallback;
import com.xhh.ysj.manager.DownloadManager;
import com.xhh.ysj.manager.IjkManager;
import com.xhh.ysj.utils.BaseSharedPreferences;
import com.xhh.ysj.utils.CommonUtil;
import com.xhh.ysj.utils.FileUtil;
import com.xhh.ysj.utils.ControllerUtils;
import com.xhh.ysj.utils.TimeRun;
import com.xhh.ysj.utils.TimeUtils;
import com.xhh.ysj.utils.VideoUtils;
import com.xhh.ysj.view.PopBuy;
import com.xhh.ysj.view.PopLeftOperate;
import com.xhh.ysj.view.PopRightOperate;
import com.xhh.ysj.view.PopWarning;
import com.xhh.ysj.view.PopWaterSale;
import com.xhh.ysj.view.PopQrCode;
import com.xhh.ysj.view.PopWantWater;

import okhttp3.Request;

public class MainActivity extends BaseActivity implements IjkManager.PlayerStateListener {

    private static final String TAG = "MainActivity";
    public static final String FLAG = "UPDATE";
    private final static int DATA_DELETE = 2;

    private ImageView ivDefault;

    // 私有变量
    private MyHandler myHandler;
    private DbManager dbManager;
    private ComThread comThread;//comThread服务是用来获取设备数据
    private DevUtil devUtil;//设备操作的工具类
    private ConnectionChangeReceiver connectReceiver;
    private DynamicReceiver dynamicReceiver;

    // popWindow
    private PopWantWater popWantWater = null;
    private PopWaterSale popWaterSale = null;
    private PopQrCode popQrCode = null;
    private PopLeftOperate popLeft = null;
    private PopRightOperate popRight = null;
    private PopBuy popBuy = null;
    private PopWarning popWarning = null;

    //左边操作窗口的组件
    private TextView hotWaterText;
    private TextView coolWaterText;
    private TextView ppmValue;
    private TextView ppm;//下方的

    // 四个使能按钮
    private LinearLayout toBeHot;
    private LinearLayout toBeCool;
    private LinearLayout zhiShui;
    private LinearLayout chongXi;

    // 设备状态
    private ImageView hotIcon;//是否加热的imageview
    private TextView hotText;//是否加热text
    private ImageView coolIcon;//是否制冷的imageView
    private TextView coolText;//是否制冷text
    private ImageView waterProduceIcon;//是否制水的imageView
    private TextView waterProduceText;//是否制水的text
    private ImageView flushIcon;//冲洗imageView
    private TextView flushText;//冲洗text;
    private Button exit;

    // 视频播放
    /*播放器*/
    private IjkManager playerManager;
    /*推送策略中的视频列表*/
    private List<AdvsVideo> pushAdVideoList;
    /*所有要播放的闲时视频列表*/
    private List<AdvsVideo> allAdVideoList; //（同步后应该与push列表一致）
    /*当前要播放的闲时视频列表*/
    private List<AdvsVideo> curAdVideoList;
    /*当前播放的视频在initAdVideoList中的index*/
    private int initAdIndex;
    /*当前播放的视频在curAdVideoList中的index*/
    private int curAdIndex;
    /*当前下载的视频在pushAdVideoList中的index*/
    private int pushAdIndex;
    /*是否正在下载*/
    private boolean isDownloading;
    /*当前播放的是初始视频*/
    private boolean isPlayInitVideo;
    /*当前时间*/
    private String curTime;

    //监控断网时的变量
    public static Boolean isStarted = false;
    // 滤芯种类
    private SparseArray<String> filters = new SparseArray<>();

    // -------------------------- 生命周期 start --------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initData();
        initView();
        initVideo();
        initComThread();
        isStarted = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != playerManager) {
            playerManager.start();
        }
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (DispenserCache.isFreeAdDone) {
                    LogUtils.d(TAG, "dismissPop: 看完广告视频，可以喝水了");
                    dismissPop(popWantWater);
                    showPopLeftOperate();
                    showPopRightOperate();
                } else {
                    LogUtils.d(TAG, "dismissPop: “我要喝水”要出现了");
                    showPopWantWater();
                }
            }
        }, 100);
    }

    @Override
    protected void onPause() {
        if (null != playerManager) {
            playerManager.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO: 2018/6/12 0012 清空各种
        if (null != myHandler) {
            myHandler.removeCallbacksAndMessages(null);
            myHandler = null;
        }
        dismissAllPop();
        if (null != dialog) {
            dialog.dismiss();
            dialog = null;
        }
        if (null != dynamicReceiver) {
            unregisterReceiver(dynamicReceiver);
        }
        if (null != connectReceiver) {
            unregisterReceiver(connectReceiver);
        }
        isStarted = false;
        super.onDestroy();
    }

    // -------------------------- 生命周期 end --------------------------

    private void initData() {
        LogUtils.e(TAG,"userName = "+BaseSharedPreferences.getInt(App.getInstance(), Constant.DEVICE_ID_KEY));
        LogUtils.e(TAG,"password = "+BaseSharedPreferences.getString(App.getInstance(), Constant.API_PASSWORD_KEY));
        LogUtils.e(TAG,"deviceId = "+BaseSharedPreferences.getInt(App.getInstance(), Constant.DEVICE_ID_KEY));

        mContext = MainActivity.this;
        mActivity = MainActivity.this;
        myHandler = new MyHandler();
        controllerUtils = new ControllerUtils(MainActivity.this);
        // 加载滤芯种类
        filters.put(1, getString(R.string.filter_type_pp));
        filters.put(2, getString(R.string.filter_type_grain));
        filters.put(3, getString(R.string.filter_type_press));
        filters.put(4, getString(R.string.filter_type_pose));
        filters.put(5, getString(R.string.filter_type_ro));
        // 获取设备信息
        getDeviceConfigFromLocal(mActivity, Constant.GET_INFO_FOR_UPDATE_CONFIG);
        // 数据库
        dbManager = new XutilsInit(mContext).getDb();
        // 广播接收
        registerMyReceiver();
        // 租期检查
        loopCheckRentTime();
        // 视频播放
        DispenserCache.freeAdVideoList = new ArrayList<>();
        DispenserCache.initAdVideoList = new ArrayList<>();
        pushAdVideoList = new ArrayList<>();
        allAdVideoList = new ArrayList<>();
        curAdVideoList = new ArrayList<>();
        // （从本地文件中取出推送数据，看是否需要处理）
        getPushStrategy();
        // （从数据库中取出数据填充列表）
        try {
            List<AdvsVideo> allAds = dbManager.findAll(AdvsVideo.class);
            if (null != allAds && 0 != allAds.size()) {
                for (AdvsVideo ad : allAds) {
                    if (null == ad) continue;
                    dividerAds(allAds);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        curTime = TimeUtils.getCurrentTime();
        curAdIndex = 0;
        //监控设备
        startService(new Intent(mContext, DaemonService.class));
        listenDevice();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        ivDefault = findViewById(R.id.main_default_iv);
        ivDefault.setVisibility(View.GONE);
        //左边的操作界面组件获得
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.pop_left, null);
        hotWaterText = (TextView) view.findViewById(R.id.hot_water_text);
        coolWaterText = (TextView) view.findViewById(R.id.cool_water_text);
        ppmValue = (TextView) view.findViewById(R.id.out_ppm_value);
        ppm = (TextView) view.findViewById(R.id.raw_ppm);
    }

    private void initVideo() {
        // 初始化播放器
        playerManager = new IjkManager(this, R.id.idle_ad_video);
        playerManager.setFullScreenOnly(true);
        playerManager.setScaleType(IjkManager.SCALETYPE_FILLPARENT);
        playerManager.playInFullScreen(true);
        playerManager.setOnPlayerStateChangeListener(this);
        // 获取当前时段应播视频的列表
        loopPlayVideo();
        scheduleUploadVideo();
    }

    private void initComThread() {
        if (null == DaemonService.comThread) {
            comThread = new ComThread(mContext, null);
        }
        if (null == devUtil) {
            devUtil = new DevUtil(null);
        }
    }

    private void registerMyReceiver() {
        // 网络变化广播接收器
        IntentFilter connectFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        connectReceiver = new ConnectionChangeReceiver(this);
        this.registerReceiver(connectReceiver, connectFilter);
        // 信鸽广播接收器
        dynamicReceiver = new DynamicReceiver();
        IntentFilter dynamicFilter = new IntentFilter();
        dynamicFilter.addAction(MessageReceiver.PUSHACTION);
        dynamicFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(dynamicReceiver, dynamicFilter);

    }

    /**
     * 定期检查租期
     */
    private void loopCheckRentTime() {
        Time time = new Time();
        time.setToNow();
        Date updatetime = TimeRun.tasktime(time.hour, time.minute, time.second);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                checkRentTime();
            }
        };
        Timer timer = new Timer(true);
        timer.schedule(task, updatetime, Constant.CHECK_RENT_DEADLINE_PERIOD * 1000);
    }

    /**
     * 检查租期
     */
    private void checkRentTime() {
        if (Constant.DRINK_MODE_MACHINE_RENT == drinkMode) {
            String deadline = BaseSharedPreferences.getString(mContext, Constant.RENT_DEADLINE_KEY);
            String currentTime = TimeUtils.getCurrentTime();
            if (!TimeUtils.isAvailDate(deadline, currentTime)) {
                LogUtils.i(TAG, "run: 租期已到！currentTime = " + currentTime + ", deadline = " + deadline);
                moveToBreakDownActivity(getString(R.string.break_down_reason_expired));
                // 预警
                saveDeviceServiceNotice(Constant.NOTICE_TYPE_RENT_EXPIRED,
                        Constant.NOTICE_LEVEL_ABNORMAL, mContext.getString(R.string.notice_content_rent_expired));
            }
        }
    }

    public void listenDevice() {
//        //水质监听
//        Time uploadTime = new Time();
//        uploadTime.setToNow();
//        Date updatetime = TimeRun.tasktime(uploadTime.hour, uploadTime.minute, uploadTime.second);
//        TimeRun timeRun = new TimeRun(mActivity, updatetime, myHandler, Constant.WARNING_TIME, Constant.MSG_DATA_DELETE, Constant.TIME_OPERATE_UPDATEWATER);
//        timeRun.startTimer();

        // 去除率监听
        Time tdsTime = new Time();
        tdsTime.setToNow();
        Date updatetimeTds = TimeRun.tasktime(tdsTime.hour, tdsTime.minute, tdsTime.second);
        TimeRun timeRunTds = new TimeRun(mActivity, updatetimeTds, myHandler, Constant.CHECK_TDS_PERIOD, Constant.MSG_DATA_DELETE, Constant.TIME_OPERATE_UPDATEWATER);
        timeRunTds.setPopLeft(popLeft);
        timeRunTds.startTimer();

//        String tdsUrl = RestUtils.getUrl(UriConstant.WATERQUALITYLIST);
//        UploadLocalData.getInstance(mActivity).upload(tdsUrl, Constant.TIME_OPETATE_TDS, Constant.CHECK_TDS_PERIOD);

        //定时刷新二维码
        Time sCodeTime = new Time();
        sCodeTime.setToNow();
        Date sCodeTimeUpdate = TimeRun.tasktime(sCodeTime.hour, sCodeTime.minute, sCodeTime.second);
        TimeRun timeRunScode = new TimeRun(mActivity, sCodeTimeUpdate, myHandler, Constant.UPDATE_SCODE, Constant.MSG_UPDATE_SCODE, Constant.TIME_OPETATE_UPDATESCODE);
        timeRunScode.startTimer();

        //更新最后在线时间
        Time deViceEndTime = new Time();
        deViceEndTime.setToNow();
        Date deviceEndTimeUpdate = TimeRun.tasktime(deViceEndTime.hour,deViceEndTime.minute,deViceEndTime.second);
        TimeRun timeRunDeviceEndTime = new TimeRun(mActivity,deviceEndTimeUpdate,myHandler,Constant.DEVICE_ENDTIME,Constant.MSG_OTHER,Constant.TIME_DEVICE_ENDTIME);
        timeRunDeviceEndTime.startTimer();

//        //预警
//        Time warningTime = new Time();
//        warningTime.setToNow();
//        Date waringTimeUpdate = TimeRun.tasktime(warningTime.hour, warningTime.minute, warningTime.second);
//        TimeRun waringRunScode = new TimeRun(mActivity, waringTimeUpdate, myHandler, Constant.WARNING_TIME, Constant.MSG_UPDATE_SCODE, Constant.TIME_OPETATE_WARNING);
//        waringRunScode.startTimer();

        // 水质上报
        String waterUrl = RestUtils.getUrl(UriConstant.WATERQUALITYLIST);
        long waterUploadCycle = BaseSharedPreferences.getInt(mContext, Constant.DEVICE_UP_TIME_KEY) * 60 * 1000;
        if (0 == waterUploadCycle) {
            waterUploadCycle = Constant.DEVICE_UP_TIME_DEFAULT;
        }
        UploadLocalData.getInstance(mActivity).upload(waterUrl, Constant.TIME_OPERATE_UPDATEWATER, waterUploadCycle);

        // 预警上报
        String noticeUrl = RestUtils.getUrl(UriConstant.NOTICEQUALITYLIST);
        UploadLocalData.getInstance(mActivity).upload(noticeUrl, Constant.TIME_OPETATE_WARNING, Constant.WARNING_TIME);

        // 按时段加热
        int flagHotAllDay = BaseSharedPreferences.getInt(mActivity, Constant.DEVICE_HEATING_ALL_DAY_KEY);
        int flagCoolAllDay = BaseSharedPreferences.getInt(mActivity, Constant.DEVICE_COOLING_ALL_DAY_KEY);
        LogUtils.e(TAG,"flagHotAllDay = "+flagHotAllDay);
        LogUtils.e(TAG,"flagCoolAllDay = "+flagCoolAllDay);
        if(0==flagHotAllDay){
//        if (false) {
            //时间段开始加热
            LogUtils.e(TAG,"按时间段加热开始");
            String timeHot = BaseSharedPreferences.getString(mActivity, Constant.DEVICE_HEATING_INTERVAL_KEY);
            String hotBeginTime = timeHot.split("-")[0];
            String hotEndTime = timeHot.split("-")[1];
            Date hotTimeThread = TimeRun.tasktime(Integer.parseInt(hotBeginTime.split(":")[0]), Integer.parseInt(hotBeginTime.split(":")[1]), Integer.parseInt(hotBeginTime.split(":")[2]));
            TimeRun timeRunstarthot = new TimeRun(mActivity, hotTimeThread, myHandler, Constant.TIME_ALL_DAY, Constant.MSG_OTHER, Constant.TIME_START_HOTTING);
            timeRunstarthot.startTimer();
            //时间段停止加热
            Date coolTimeThread = TimeRun.tasktime(Integer.parseInt(hotEndTime.split(":")[0]), Integer.parseInt(hotEndTime.split(":")[1]), Integer.parseInt(hotEndTime.split(":")[2]));
            TimeRun timeRunstartcool = new TimeRun(mActivity, hotTimeThread, myHandler, Constant.TIME_ALL_DAY, Constant.MSG_OTHER, Constant.TIME_END_HOTTING);
            timeRunstartcool.startTimer();
        } else {
            controllerUtils.operateDevice(0, true);

        }
//        if (false) {
        if(0==flagCoolAllDay){
            //时间段制冷开始
            LogUtils.e(TAG,"按时间段制冷开始");
            String timeCool = BaseSharedPreferences.getString(mContext, Constant.DEVICE_COOLING_INTERVAL_KEY);
            String coolBeginTime = timeCool.split("-")[0];
            String coolEndTime = timeCool.split("-")[1];
            Date coolEndTimeThread = TimeRun.tasktime(Integer.parseInt(coolBeginTime.split(":")[0]), Integer.parseInt(coolBeginTime.split(":")[1]), Integer.parseInt(coolBeginTime.split(":")[2]));
            TimeRun timeRunstartcool = new TimeRun(mActivity, coolEndTimeThread, myHandler, Constant.TIME_ALL_DAY, Constant.MSG_OTHER, Constant.TIME_START_COOLING);
            timeRunstartcool.startTimer();
            //时间段制冷停止
            Date coolTimeThread = TimeRun.tasktime(Integer.parseInt(coolEndTime.split(":")[0]), Integer.parseInt(coolEndTime.split(":")[1]), Integer.parseInt(coolEndTime.split(":")[2]));
            TimeRun timeRunendcool = new TimeRun(mActivity, coolEndTimeThread, myHandler, Constant.TIME_ALL_DAY, Constant.MSG_OTHER, Constant.TIME_END_COOLING);
            timeRunendcool.startTimer();
        } else {
            controllerUtils.operateDevice(1, true);
        }
    }
      private void scheduleUploadVideo() {
        String url = RestUtils.getUrl(UriConstant.AD_VIDEO_RECORD_LIST);
//        UploadLocalData ulManager = new UploadLocalData(mContext);
//        ulManager.upload(url, Constant.TIME_OPETATE_VIDEO, Constant.UPLOAD_TIME);
        UploadLocalData.getInstance(mActivity).upload(url, Constant.TIME_OPETATE_VIDEO,
                Constant.AD_RECORD_UPLOAD_PERIOD * 1000);
    }

    /**
     * 循环播放广告视频
     */
    private void loopPlayVideo() {
        LogUtils.d(TAG, "loopPlayVideo: 开始循环播放广告视频");
        if (getCurrentVideoList()) {
            // 有视频，则顺序播放
            playVideo();
        } else {
            // 无视频，则播放初始视频
            playInitVideo();
        }
    }

    /**
     * 获取当前应该播放的闲时视频的列表
     *
     * @return 返回值为当前时段是否有videoList
     */
    private boolean getCurrentVideoList() {
        LogUtils.d(TAG, "getCurrentVideoList: 开始获取当前应播视频列表");
        LogUtils.d(false, TAG, "allAdVideoList.size = " + allAdVideoList.size());
        try {
            List<AdvsVideo> allDbAdList = dbManager.findAll(AdvsVideo.class);
            LogUtils.d(false, TAG, "allDbAdList.size = " + (null == allDbAdList ? 0 : allDbAdList.size()));
        } catch (DbException e) {
            e.printStackTrace();
        }
        boolean hasVideo = false;
        for (AdvsVideo ad : allAdVideoList) {
            int index = VideoUtils.getAdIndexFromList(ad, curAdVideoList);
            if (-1 == index) {
                // 列表中不存在此广告，则加入此广告
                LogUtils.d(TAG, "getCurrentVideoList: 当前视频列表中无此广告，查看是否加入" + ad.getAdvsVideoLocaltionPath());
                if (TimeUtils.isCurrentDateTimeInPlan(
                        ad.getAdvsPlayBeginDatetimes(), ad.getAdvsPlayEndDatetimes(),
                        ad.getAdvsPlayBeginTime(), ad.getAdvsPlayEndTime(), curTime)) {
                    LogUtils.d(TAG, "getCurrentVideoList: 时辰已到，加入");
                    curAdVideoList.add(ad);
                    hasVideo = true;
                } else {
                    LogUtils.d(TAG, "getCurrentVideoList: 时辰未到，再等等");
                }
            } else if (-1 < index) {
                // 列表中存在此广告，则更新此广告数据
                LogUtils.d(TAG, "getCurrentVideoList: 当前视频列表中有此广告，更新");
                if (TimeUtils.isCurrentDateTimeInPlan(
                        ad.getAdvsPlayBeginDatetimes(), ad.getAdvsPlayEndDatetimes(),
                        ad.getAdvsPlayBeginTime(), ad.getAdvsPlayEndTime(), curTime)) {
                    curAdVideoList.set(index, ad);
                    hasVideo = true;
                }
            }
        }
        if (!hasVideo)
            LogUtils.d(TAG, "getCurrentVideoList: 当前时段无视频！");
        return hasVideo;
    }

    /**
     * 播放初始视频
     */
    private void playInitVideo() {
        if (null == DispenserCache.initAdVideoList || 0 == DispenserCache.initAdVideoList.size()) {
            LogUtils.d(TAG, "playInitVideo: 无初始视频！");
            ivDefault.setVisibility(View.VISIBLE);
            return;
        }
        ivDefault.setVisibility(View.GONE);
        isPlayInitVideo = true;
        AdvsVideo ad = DispenserCache.initAdVideoList.get(initAdIndex % DispenserCache.initAdVideoList.size());
        playerManager.play(ad.getAdvsVideoLocaltionPath());
    }

    /**
     * 播放curDownAdVideoList中的视频
     */
    private void playVideo() {
        if (null == curAdVideoList || 0 == curAdVideoList.size()) return;
        AdvsVideo ad = curAdVideoList.get(curAdIndex % curAdVideoList.size());
        if (!TimeUtils.isCurrentDateTimeInPlan(
                ad.getAdvsPlayBeginDatetimes(), ad.getAdvsPlayEndDatetimes(),
                ad.getAdvsPlayBeginTime(), ad.getAdvsPlayEndTime(), curTime)) {
            // 当前列表中有过了时间段的视频，则remove掉，重新刷新一次
            curAdVideoList.remove(ad);
            if (0 == curAdVideoList.size()) {
                playInitVideo();
                return;
            } else {
                playVideo();
                return;
            }
        }
        isPlayInitVideo = false;
        LogUtils.d(TAG, "playVideo: 本地路径：" + ad.getAdvsVideoLocaltionPath());
        playerManager.play(ad.getAdvsVideoLocaltionPath());
    }

    /**
     * 从推送中解析出pushAdVideoList
     */
    private boolean getPushStrategy() {
        LogUtils.d(TAG, "getPushStrategy: 开始处理strategy的json数据");
        // 从本地文件取出数据，如果是已经处理的数据，则不响应；反之则处理。
        String decode = CommonUtil.decode(FileUtil.readTxtFile(UriConstant.APP_ROOT_PATH +
                UriConstant.VIDEO_DIR + UriConstant.VIDEO_PUSH_FILE_NAME));
        if (TextUtils.isEmpty(decode)) {
            LogUtils.d(TAG, "getPushStrategy: 无推送数据！");
            return false;
        }
        FileUtil.saveContentToSdcard(UriConstant.APP_ROOT_PATH +
                        UriConstant.VIDEO_DIR + UriConstant.VIDEO_PUSH_FILE_NAME,
                CommonUtil.encode(Constant.VIDEO_PUSH_HANDLE_DOING + decode.substring(1)));
        String status = decode.substring(0, 1);
        if (Constant.VIDEO_PUSH_HANDLE_DOING.equals(status)) {
            LogUtils.d(TAG, "getPushStrategy: 已经处理过推送，不响应");
            return false;
        }
//        List<AdvsVideo> adList = JSONArray.parseArray(decode.substring(1), AdvsVideo.class);
        List<AdvsVideo> adList = null;
        try {
            adList = JSONArray.parseArray(decode.substring(1), AdvsVideo.class);
        } catch (JSONException e) {
            LogUtils.d(TAG, "getPushStrategy: 推送数据无法转换成 AdvsVideo！");
        }
        if (null == adList || 0 == adList.size()) {
            LogUtils.d(TAG, "getPushStrategy: 推送数据有误！");
            return false;
        }
        for (AdvsVideo ad : adList) {
            if (null == ad) continue;
            String upDate = ad.getAdvsPlayBeginDatetimes();
            String downDate = ad.getAdvsPlayEndDatetimes();
            LogUtils.d(TAG, "getPushStrategy: upDate = " + upDate + ", downDate = " + downDate + ", url = " + ad.getAdvsVideoDownloadPath());
            if (TimeUtils.isFutureSchedule(upDate, downDate, curTime)) {
                LogUtils.d(TAG, "getPushStrategy: ooooooooooooooook! put!");
                pushAdVideoList.add(ad);
            }
        }
        LogUtils.d(TAG, "getPushStrategy: json处理完毕");
        // 删除本次策略中没有的广告的本地文件
        try {
            List<AdvsVideo> allDbAdList = dbManager.findAll(AdvsVideo.class);
            if (null == allDbAdList) {
                return true;
            }
            for (AdvsVideo ad : allDbAdList) {
                int index = VideoUtils.getAdIndexFromList(ad, pushAdVideoList);
                LogUtils.d(TAG, "getPushStrategy: index = " + index);
                if (-1 == index) {
                    String localPath = ad.getAdvsVideoLocaltionPath();
                    LogUtils.d(TAG, "getPushStrategy: 删除本地文件：" + localPath);
                    FileUtil.deleteFile(localPath);

                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void downloadAllVideo() {
        LogUtils.d(TAG, "downloadAllVideo: dl_info: 进入循环下载");
        // 下载完毕，则去同步各种数据
        if (pushAdIndex >= pushAdVideoList.size()) {
            LogUtils.d(TAG, "downloadAllVideo: dl_info: 全部视频状态为已下载，return");
            myHandler.sendEmptyMessageDelayed(Constant.MSG_ALL_DOWN_COMPLETE, Constant.ALL_DOWN_WAIT_TIME);
            return;
        }
        // 没下完，则下载
        AdvsVideo ad = pushAdVideoList.get(pushAdIndex);
        // 如果为空，则下载下一个
        if (null == ad) {
            LogUtils.d(TAG, "downloadAllVideo: dl_info: 本条广告为空，return");
            pushAdIndex++;
            downloadAllVideo();
            return;
        }
        // 已经是本地的，则更新isLocal并下载下一个
        String localPath = VideoUtils.checkIfVideoIsLocal(ad, allAdVideoList);
        if (!TextUtils.isEmpty(localPath)) {
            LogUtils.d(TAG, "downloadAllVideo: dl_info: 本条广告已有本地路径，return");
            ad.setLocal(true);
            ad.setAdvsVideoLocaltionPath(localPath);
            pushAdVideoList.set(pushAdIndex, ad);
            pushAdIndex++;
            downloadAllVideo();
            return;
        }
        // 下载地址为空，上报地址错误
        if (TextUtils.isEmpty(ad.getAdvsVideoDownloadPath())) {
            LogUtils.d(TAG, "downloadAllVideo: onError: dl_info: URL为空！");
            saveDeviceServiceNotice(Constant.NOTICE_TYPE_AD_URL_WRONG, Constant.NOTICE_LEVEL_ABNORMAL,
                    mContext.getString(R.string.notice_content_incorrect_url));
            pushAdVideoList.remove(ad);
            return;
        }
        downloadVideo(ad);
    }

    private void downloadVideo(AdvsVideo ad) {
        if (isDownloading) {
            LogUtils.d(TAG, "downloadVideo: dl_info: 正在下载，return..");
            if (myHandler.hasMessages(Constant.MSG_WAITING_THEN_DOWNLOAD)) {
                myHandler.removeMessages(Constant.MSG_WAITING_THEN_DOWNLOAD);
            }
            myHandler.sendEmptyMessageDelayed(Constant.MSG_WAITING_THEN_DOWNLOAD,
                    Constant.IS_DOWNING_WAIT_TIME * 1000);
            return;
        }
        String downloadPath = ad.getAdvsVideoDownloadPath();
        LogUtils.d(TAG, "downloadVideo: dl_info: 开始下载广告视频 pushAdIndex = " + pushAdIndex + ", url = " + downloadPath);
        isDownloading = true;
        DownloadManager dlManager = DownloadManager.getInstance();
        dlManager.setDownloadCallback(new DownloadCallback() {
            @Override
            public void onComplete(String localPath) {
                LogUtils.d(TAG, "onComplete: dl_info: 下载完成！localPath -- " + localPath);
                isDownloading = false;
                AdvsVideo ad = pushAdVideoList.get(pushAdIndex);
                ad.setLocal(true);
                ad.setAdvsVideoLocaltionPath(localPath);
                pushAdVideoList.set(pushAdIndex, ad);
                pushAdIndex++;
                downloadAllVideo();
            }

            @Override
            public void onError(String msg) {
                LogUtils.d(TAG, "onError: dl_info: 下载错误！msg -- " + msg);
                isDownloading = false;
                // 网址错误则上报错误信息；其他错误则放在最后再下
                if (msg.contains(Constant.DOWN_ERROR_MSG_WRONG_URL) || msg.contains(Constant.DOWN_ERROR_MSG_WRONG_BASE_URL)) {
                    LogUtils.d(TAG, "onError: dl_info: URL有误！");
                    saveDeviceServiceNotice(Constant.NOTICE_TYPE_AD_URL_WRONG, Constant.NOTICE_LEVEL_ABNORMAL,
                            mContext.getString(R.string.notice_content_incorrect_url));
                    pushAdVideoList.remove(pushAdIndex);
                    downloadAllVideo();
                    return;
                }
                LogUtils.d(TAG, "onError: dl_info: 将本广告视频移动至list最后");
                AdvsVideo advsVideo = pushAdVideoList.get(pushAdIndex);
                pushAdVideoList.remove(pushAdIndex);
                pushAdVideoList.add(advsVideo);
                if (myHandler.hasMessages(Constant.MSG_WAITING_THEN_DOWNLOAD)) {
                    myHandler.removeMessages(Constant.MSG_WAITING_THEN_DOWNLOAD);
                }
                myHandler.sendEmptyMessageDelayed(Constant.MSG_WAITING_THEN_DOWNLOAD,
                        Constant.IS_DOWNING_WAIT_TIME * 1000);
            }

            @Override
            public void onProgress(int progress) {
                LogUtils.d(TAG, "onProgress: dl_info: 正在下载.. progress = " + progress);
            }
        });
        dlManager.startDown(mContext, Constant.DOWNLOADAPK_ID,
                downloadPath/*.substring(0, downloadPath.lastIndexOf('/') + 1)*/, downloadPath,
                UriConstant.APP_ROOT_PATH + UriConstant.VIDEO_DIR);
    }

    private void refreshAllAdVideoData() {
        LogUtils.d(TAG, "refreshAllAdVideoData: 开始更新数据库及缓存list");
        playerManager.stop();
        // 同步数据到数据库
        try {
            dbManager.delete(AdvsVideo.class);
            dbManager.saveOrUpdate(pushAdVideoList);
            FileUtil.deleteFile(UriConstant.APP_ROOT_PATH + UriConstant.VIDEO_DIR + UriConstant.VIDEO_PUSH_FILE_NAME);
        } catch (DbException e) {
            e.printStackTrace();
        }
        // 同步数据到各个list
        curAdIndex = 0;
        allAdVideoList.clear();
        DispenserCache.initAdVideoList.clear();
        DispenserCache.freeAdVideoList.clear();
        dividerAds(pushAdVideoList);
        // 将默认图片隐藏
        ivDefault.setVisibility(View.GONE);
    }

    private void dividerAds(List<AdvsVideo> adVideoList) {
        LogUtils.d(TAG, "dividerAds: 开始广告视频分类");
        if (null == adVideoList || 0 == adVideoList.size()) {
            LogUtils.d(TAG, "dividerAds: adVideoList为空");
            return;
        }
        for (AdvsVideo ad : adVideoList) {
            switch (ad.getAdvsType()) {
                case Constant.AD_TYPE_IDLE:
                    allAdVideoList.add(ad);
                    break;
                case Constant.AD_TYPE_FREE:
                    DispenserCache.freeAdVideoList.add(ad);
                    break;
                case Constant.AD_TYPE_INIT:
                    DispenserCache.initAdVideoList.add(ad);
                    break;
            }
        }
    }

    public Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }

    /**
     * 判断Activity是否可用
     *
     * @return
     */
    private boolean isActivityValidate() {
        return !(this.isDestroyed() || this.isFinishing());
//        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
//        for (ActivityManager.RunningTaskInfo info : list) {
//            // 注意这里的 topActivity 包含 packageName和className
//            if (info.topActivity.toString().equals(this) || info.baseActivity.toString().equals(this)) {
//                LogUtils.i(TAG, info.topActivity.getPackageName() + " info.baseActivity.getPackageName()=" + info.baseActivity.getPackageName());
//                return true;
//            }
//        }
//        LogUtils.d(TAG, "isActivityValidate: 不可用！");
//        return false;
    }

    // -------------------------- 回调 start --------------------------
    // --------- ijk 监听 start ---------

    @Override
    public void onComplete() {
        LogUtils.d(TAG, "onComplete: isPlayInitVideo = " + isPlayInitVideo);
        curTime = TimeUtils.getCurrentTime();
        if (isPlayInitVideo) {
            initAdIndex += 1;
        } else {
//            for (int i = 0; i < 500; i++) {
                AdvsVideo curAd = allAdVideoList.get(curAdIndex % curAdVideoList.size());
                AdvsPlayRecode curAdRecord = new AdvsPlayRecode(curAd.getAdvsId(), deviceId, TimeUtils.getCurrentTime()/*,
                        curAd.getAdvsVideoLengthOfTime(), curAd.getAdvsChargMode(),
                        curAd.getAdvsIndustry(), curAd.getAdvsType()*/);
                try {
                    dbManager.save(curAdRecord);
                    List<AdvsPlayRecode> all = dbManager.findAll(AdvsPlayRecode.class);
                    LogUtils.d(TAG, "onComplete: all.size = " + all.size());
                } catch (DbException e) {
                    e.printStackTrace();
                }
//            }

            curAdIndex += 1;
        }
        loopPlayVideo();
    }

    /**
     * 错误
     * 暂时只有一种错误回调，即what = MediaPlayer.MEDIA_ERROR_UNKNOWN， extra = 0，后期可做修改（IjkVideoView中）
     *
     * @param what
     */
    @Override
    public void onError(int what, int extra) {
//        LogUtils.d(TAG, "onError: what = " + what + ", extra = " + extra);
        if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            //媒体服务器挂掉了。此时，程序必须释放MediaPlayer 对象，并重新new 一个新的。
            LogUtils.e(TAG, "onError: 视频播放：网络服务错误");
        } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
            LogUtils.e(TAG, "onError: 视频播放：文件不存在或错误，或网络不可访问错误");
        } else if (what == -10000) {
            LogUtils.e(TAG, "onError: 视频播放：本地文件被删除");
            // 列表删除这条广告
            AdvsVideo ad;
            if (isPlayInitVideo) {
                ad = DispenserCache.initAdVideoList.get(initAdIndex % DispenserCache.initAdVideoList.size());
                DispenserCache.initAdVideoList.remove(ad);
            } else {
                ad = curAdVideoList.get(curAdIndex % curAdVideoList.size());
                allAdVideoList.remove(ad);
            }
            // 数据库删除这条广告
            try {
                dbManager.delete(ad);
            } catch (DbException e) {
                e.printStackTrace();
            }
            // 上报错误
            // TODO: 2018/7/31 0031 上报本地视频被删除的错误
        } else {
            LogUtils.e(TAG, "onError: 视频播放：错误！what = " + what + ", extra = " + extra);
        }
        if (isPlayInitVideo) {
            initAdIndex += 1;
        } else {
            curAdIndex += 1;
        }
        playerManager.onDestroy();//释放
        loopPlayVideo();
//        playVideo();//播放
    }

    @Override
    public void onInfo(int what, int extra) {
        LogUtils.d(TAG, "onInfo: what = " + what + ", extra = " + extra);
    }
    // --------- ijk 监听 end ---------

    // -------------------------- 回调 end --------------------------

    // -------------------------- 内部类 start --------------------------

    public class DynamicReceiver extends XGPushBaseReceiver {
        @Override
        public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {
            LogUtils.e(TAG, "onRegisterResult: ");

        }

        @Override
        public void onUnregisterResult(Context context, int i) {
            LogUtils.e(TAG, "onUnregisterResult: ");
        }

        @Override
        public void onSetTagResult(Context context, int i, String s) {
            LogUtils.e(TAG, "onSetTagResult: ");
        }

        @Override
        public void onDeleteTagResult(Context context, int i, String s) {
            LogUtils.e(TAG, "onDeleteTagResult: ");
        }

        @Override
        public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
//            Toast.makeText(context, "收到推送消息", Toast.LENGTH_SHORT).show();
            LogUtils.d(TAG, "onTextMessage: receive new push");
            String pushString = xgPushTextMessage.getContent();
            LogUtils.i(TAG, "onTextMessage: 收到消息: " + pushString);
            PushEntity pushEntity = JSONObject.parseObject(pushString, PushEntity.class);
            if (null == pushEntity) {
                LogUtils.d(TAG, "onTextMessage: 推送为空！");
                return;
            }
            // 获取内容
            String content = pushEntity.getOperationContent();
            if (TextUtils.isEmpty(content)) {
                LogUtils.d(TAG, "onTextMessage: 推送内容为空！");
                LogUtils.d(TAG, "onTextMessage: pushEntity = " + pushEntity.toString());
                return;
            }
//            Toast.makeText(MainActivity.this, "pushEntity.getOperationType()"+pushEntity.getOperationType(), Toast.LENGTH_SHORT).show();
            LogUtils.d(TAG, "onTextMessage: 操作类型：" + pushEntity.getOperationType());
            String url;
            switch (pushEntity.getOperationType()) {
                case Constant.PUSH_OPERATION_TYPE_OPERATE:
                    LogUtils.d(TAG, "onTextMessage: 推送类型为：操作。");
                    //TODO 收到推送后的操作  1冲洗 2开盖 3开关机
                    ControllerUtils controllerUtils = new ControllerUtils(MainActivity.this);

                    String operateflag = ""+pushEntity.getOperationContent();
                    Toast.makeText(context,"推送过来的操作类型"+operateflag,Toast.LENGTH_SHORT).show();

                    if (Constant.DEVICE_OPERATE_FLUSH.equals(operateflag)) {
                        controllerUtils.operateDevice(ControllerUtils.DO_IO_RINSE, false);
                    }
                    if (Constant.DEVICE_OPERATE_UNCAP.equals(operateflag)) {
                        controllerUtils.operateDevice(ControllerUtils.DO_IO_COVER, false);
                    }



                    if (Constant.DEVICE_OPERATE_ON_OFF.equals(operateflag)) {
                        controllerUtils.operateDevice(2, false);
                    }
                    if (Constant.DO_HOTTING == operateflag) {
                        controllerUtils.operateDevice(ControllerUtils.DO_HOTTING, false);
                    }
                    if (Constant.DO_COOLING == operateflag) {
                        controllerUtils.operateDevice(ControllerUtils.DO_COOLING, false);
                    }
                    if (Constant.DO_TURNOFFHOTTING == operateflag) {
                        controllerUtils.operateDevice(ControllerUtils.DO_TURN_OFF_HOTTING, false);
                    }
                    if (Constant.DO_TURNOFFCOOLING == operateflag) {
                        controllerUtils.operateDevice(ControllerUtils.DO_TURN_OFF_COOLING, false);
                    }
                    if(pushEntity.getOperationContent().equals("3")){
                        controllerUtils.operateDevice(3, true);
                    }
                    if(pushEntity.getOperationContent().equals("4")){
                        controllerUtils.operateDevice(ControllerUtils.OPENCLOSE, false);
                    }
                    break;
                case Constant.PUSH_OPERATION_TYPE_CONFIG:
                case Constant.PUSH_OPERATION_TYPE_TARGET:
                    LogUtils.d(TAG, "onTextMessage: 推送类型为：视频（配置/行业）。");
                    url = RestUtils.getUrl(UriConstant.GET_AD_VIDEO_LIST + content);
                    OkHttpUtils.postAsyn(url, new OkHttpUtils.StringCallback() {
                        @Override
                        public void onFailure(int errCode, Request request, IOException e) {
                            LogUtils.d(TAG, "onFailure: 获取视频策略失败！errCode = " + errCode +
                                    ", response = " + request.toString());
                        }

                        @Override
                        public void onResponse(String response) {
                            LogUtils.d(TAG, "onResponse: 获取视频策略成功！ response = " + response);
                            JSONObject jsonObject = JSONObject.parseObject(response);
                            if (null == jsonObject) {
                                LogUtils.d(TAG, "onResponse: 视频策略获取response数据错误！");
                                return;
                            }
                            Object data = jsonObject.get("data");
                            if (null == data) {
                                LogUtils.d(TAG, "onResponse: 视频策略获取response的data数据错误！");
                                return;
                            }
                            // 存入本地文件
                            LogUtils.d(TAG, "onTextMessage: 开始将push的strategy存入本地..");
                            FileUtil.saveContentToSdcard(UriConstant.APP_ROOT_PATH +
                                            UriConstant.VIDEO_DIR + UriConstant.VIDEO_PUSH_FILE_NAME,
                                    CommonUtil.encode(Constant.VIDEO_PUSH_HANDLE_TO_DO + data.toString()));
                            // 发送延时消息处理
                            if (myHandler.hasMessages(Constant.MSG_NEW_AD_VIDEO_STRATEGY_PUSH)) {
                                myHandler.removeMessages(Constant.MSG_NEW_AD_VIDEO_STRATEGY_PUSH);
                            }
                            myHandler.sendEmptyMessageDelayed(Constant.MSG_NEW_AD_VIDEO_STRATEGY_PUSH,
                                    Constant.RECEIVE_PUSH_VIDEO_STRATEGY_WAIT_TIME * 1000);
                        }
                    });

                    break;
                case Constant.PUSH_OPERATION_TYPE_LOGIN:
                    LogUtils.d(TAG, "onTextMessage: 推送类型为：登录。");
                    String userId = pushEntity.getOperationContent();
                    url = RestUtils.getUrl(UriConstant.GET_USER_INFO + userId);
                    OkHttpUtils.getAsyn(url, new OkHttpUtils.StringCallback() {
                        @Override
                        public void onFailure(int errCode, Request request, IOException e) {
                            LogUtils.d(TAG, "onFailure: 获取用户登录信息失败！errCode = " + errCode +
                                    ", response = " + request.toString());
                        }

                        @Override
                        public void onResponse(String response) {
                            LogUtils.d(TAG, "onResponse: 获取用户登录信息成功！ response = " + response);
                        }
                    });
                    DispenserCache.userIdTemp = userId;
                    dismissPop(popWantWater);
                    dismissPop(popQrCode);
                    showPopLeftOperate();
                    showPopRightOperate();
                    break;
                case Constant.PUSH_OPERATION_TYPE_UPDATE_ID:
                    LogUtils.d(TAG, "onTextMessage: 推送类型为：激活。");
                    launchLoadApp(Constant.LOAD_APP_FOR_ACTIVATE);
                    break;
                case Constant.PUSH_OPERATION_TYPE_UPDATE_APK:
                    LogUtils.d(TAG, "onTextMessage: 推送类型为：更新APK。");
                    launchLoadApp(Constant.LOAD_APP_FOR_UPDATE);
                    break;
                case Constant.PUSH_OPERATION_TYPE_FILTER_OVER:
                    LogUtils.d(TAG, "onTextMessage: 推送类型为：滤芯耗尽。");
                    String filterType = getString(R.string.filter_type_default);
                    if (CommonUtil.isNumeric(content)) {
                        filterType = filters.get(Integer.parseInt(content), getString(R.string.filter_type_default));
                    } else {
                        LogUtils.d(TAG, "onTextMessage: content格式错误");
                    }
                    moveToBreakDownActivity(filterType + getString(R.string.break_down_reason_filter_over));
                    break;
                case Constant.PUSH_OPERATION_TYPE_UPDATE_CONFIG:
                    LogUtils.d(TAG, "onTextMessage: 推送类型为：更新参数。");
//                    if (null != breakInstance) {
//                        LogUtils.d(TAG, "onTextMessage: 之前breakDown了，关闭之。");
//                        breakInstance.finish();
//                    }
                    if (null != BreakDownActivity.instance) {
                        LogUtils.d(TAG, "onTextMessage: 之前breakDown了，关闭之。");
                        BreakDownActivity.instance.finish();
                    } else {
                        LogUtils.d(TAG, "onTextMessage: 之前没毛病，没啥。");
                    }
                    activateDevice(mActivity, deviceId, Constant.GET_INFO_FOR_UPDATE_CONFIG);
                    break;
            }
        }

        @Override
        public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
            LogUtils.d(TAG, "onNotifactionClickedResult:");
        }

        @Override
        public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
            LogUtils.d(TAG, "onNotifactionShowedResult:");
        }
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
//                    Toast.makeText(mContext, "定时   String[][] data = msg.getData().ge;检测水质", Toast.LENGTH_SHORT).show();
                    break;
                case Constant.MSG_DATA_DELETE:
                    String test = msg.obj.toString();
//                    Toast.makeText(mContext, "定时" + test, Toast.LENGTH_SHORT).show();
                    break;
                case Constant.MSG_NEW_AD_VIDEO_STRATEGY_PUSH:
                    LogUtils.d(TAG, "handleMessage: start deal video strategy push");
                    pushAdIndex = 0;
                    curTime = TimeUtils.getCurrentTime();
                    if (getPushStrategy()) {
                        downloadAllVideo();
                    }
                    break;
                case Constant.MSG_UPDATE_SCODE:
                    LogUtils.e(TAG, "成功" + "更新二维码成功");
                    break;
                case Constant.MSG_WAITING_THEN_DOWNLOAD:
                    downloadAllVideo();
                    break;
                case Constant.MSG_ALL_DOWN_COMPLETE:
                    refreshAllAdVideoData();
                    loopPlayVideo();
                    break;
            }
        }
    }

    // -------------------------- 内部类 end --------------------------

    // -------------------------- View start --------------------------

    /**
     * 更新参数时会调用这个方法
     */
    protected void moveToMainActivity() {
        checkRentTime();
    }

    /**
     * 跳转到免费广告Activity
     *
     * @param adDuration
     */
    public void moveToFreeAdActivity(int adDuration) {
        Intent intent = new Intent(mContext, FreeAdActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.KEY_FREE_AD_DURATION, adDuration);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 我要喝水
     */
    public void showPopWantWater() {
        if (isActivityValidate()) {
            if (null == popWantWater) {
                popWantWater = new PopWantWater(mActivity, drinkMode);
            }
            popWantWater.showPopupWindow(new View(mContext));
        }
    }

    /**
     * 售水模式
     */
    public void showPopWaterSale() {
        if (isActivityValidate()) {
            if (null == popWaterSale) {
                popWaterSale = new PopWaterSale(mActivity);
            }
            popWaterSale.showPopupWindow(new View(mContext));
        }
    }

    /**
     * 租赁模式/买断模式：二维码
     */
    public void showPopQrCode() {
        if (isActivityValidate()) {
            if (null == popQrCode) {
                popQrCode = new PopQrCode(mActivity);
            }
            popQrCode.showPopupWindow(new View(mContext));
        }
    }

    /**
     * 左操作面板
     */
    public void showPopLeftOperate() {
        if (isActivityValidate()) {
            if (null == popLeft) {
                popLeft = new PopLeftOperate(mActivity);
            }
            popLeft.showPopupWindow(new View(mContext));
        }
    }

    /**
     * 右操作面板
     */
    public void showPopRightOperate() {
        if (isActivityValidate()) {
            if (null == popRight) {
                popRight = new PopRightOperate(mActivity);
            }
            popRight.showPopupWindow(new View(mContext));
        }
    }

    /**
     * 充值
     */
    public void showPopBuy() {
        if (isActivityValidate()) {
            if (null == popBuy) {
                popBuy = new PopBuy(mActivity, "test");
            }
            popBuy.showPopupWindow(new View(mContext));
        }
    }

    /**
     * 热水警告
     */
    public void showPopWarning() {
        if (isActivityValidate()) {
            if (null == popWarning) {
                popWarning = new PopWarning(mActivity);
            }
            popWarning.showPopupWindow(new View(mContext));
        }
    }

    public void dismissPop(PopupWindow pop) {
        if (isActivityValidate()) {
            if (null != pop && pop.isShowing()) {
                LogUtils.d(TAG, "dismissPop: ");
                pop.dismiss();
            }
        }
    }

    public void dismissOperatePop() {
        dismissPop(popLeft);
        dismissPop(popRight);
        dismissPop(popWarning);
    }

    public void dismissAllPop() {
        dismissPop(popWantWater);
        dismissPop(popWaterSale);
        dismissPop(popQrCode);
        dismissPop(popLeft);
        dismissPop(popRight);
        dismissPop(popWarning);
        dismissPop(popBuy);
    }

    // -------------------------- View end --------------------------

}

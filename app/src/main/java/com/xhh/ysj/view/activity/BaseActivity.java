package com.xhh.ysj.view.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.xhh.ysj.App;
import com.xhh.ysj.R;
import com.xhh.ysj.beans.AdvsVideo;
import com.xhh.ysj.beans.Exceptiona;
import com.xhh.ysj.beans.SysDeviceMonitorConfig;
import com.xhh.ysj.beans.SysDeviceNoticeAO;
import com.xhh.ysj.constants.Constant;
import com.xhh.ysj.constants.UriConstant;
import com.xhh.ysj.interfaces.DownloadCallback;
import com.xhh.ysj.manager.DownloadManager;
import com.xhh.ysj.utils.BaseSharedPreferences;
import com.xhh.ysj.utils.ControllerUtils;
import com.xhh.ysj.utils.FileUtil;
import com.xhh.ysj.utils.LogUtils;
import com.xhh.ysj.utils.OkHttpUtils;
import com.xhh.ysj.utils.RestUtils;
import com.xhh.ysj.utils.TimeUtils;
import com.xhh.ysj.utils.XutilsInit;

import org.xutils.ex.DbException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Request;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    protected static final int GET_DEV_CONFIG_SERVER_BLOCK = 1;
    protected static final int GET_DEV_CONFIG_DATA_ABNORMAL = 2;
    protected static final int GET_INIT_VIDEO_SERVER_BLOCK = 3;
    protected static final int GET_INIT_VIDEO_DATA_ABNORMAL = 4;

    // 存放设备id的文本名
    public static final String DEVICE_ID_TXT_NAME = "deviceID.txt";
    // 存放设备id的文本名
    public static final String API_PWD_TXT_NAME = "api.txt";
    //要调用另一个APP的activity所在的包名
    public static String packageName = "purewater.com.leadapp";
    //要调用另一个APP的activity名字
    public static String activityName = ".MainActivity";

    public Context mContext;
    public BaseActivity mActivity;
    public List<AdvsVideo> adList;
    public int dlIndex;
    public Handler handler;
    public boolean isDownloading;
    protected static AlertDialog dialog;
    protected static BreakDownActivity breakInstance;

    // 设备信息
    public static int deviceId;
    public static int drinkMode/* = 1*/;
    public static String rentDeadline;
    public static String contractInfo;

    public ControllerUtils controllerUtils;

    /**
     * 我要喝水
     */
    public void showPopWantWater() {

    }

    /**
     * 售水模式
     */
    public void showPopWaterSale() {

    }

    /**
     * 租赁模式/买断模式：二维码
     */
    public void showPopQrCode() {

    }

    /**
     * 左操作面板
     */
    public void showPopLeftOperate() {

    }

    /**
     * 右操作面板
     */
    public void showPopRightOperate() {

    }

    /**
     * 充值
     */
    public void showPopBuy() {

    }

    /**
     * 热水警告
     */
    public void showPopWarning() {

    }

    /**
     * 使pop消失
     * @param pop
     */
    public void dismissPop(PopupWindow pop) {

    }

    /**
     * 关闭所有pop
     */
    protected void dismissAllPop() {

    }

    /**
     * 使左右的操作pop消失
     */
    public void dismissOperatePop() {

    }

    /**
     * 跳转到免费广告Activity
     *
     * @param adDuration
     */
    public void moveToFreeAdActivity(int adDuration) {

    }

    /**
     * 跳转到MainActivity
     */
    protected void moveToMainActivity() {

    }

    /**
     * 跳转到机器故障Activity
     *
     * @param errReason
     */
    public void moveToBreakDownActivity(String errReason) {
        dismissAllPop();
        Intent intent = new Intent(mContext, BreakDownActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.KEY_BREAK_DOWN_ERR_REASON, errReason);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 跳转到临时Activity
     */
    public void moveToTempActivity() {
        Intent intent = new Intent(mContext, TempActivity.class);
        startActivity(intent);
        finish();
    }

    // -----------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        adList = new ArrayList<>();
        handler = new Handler();
        controllerUtils = new ControllerUtils(BaseActivity.this);
    }

    protected static void activateDevice(BaseActivity activity, int deviceId, int getReason) {
        FileUtil.deleteFile(UriConstant.APP_ROOT_PATH + DEVICE_ID_TXT_NAME);
        String path = FileUtil.createMkdirsAndFiles(UriConstant.APP_ROOT_PATH, DEVICE_ID_TXT_NAME);
        FileUtil.write(path, Integer.toString(deviceId), true);
        // 如果apiPwd是空的，就从文件读取
        if (TextUtils.isEmpty(BaseSharedPreferences.getString(App.getInstance(), Constant.API_PASSWORD_KEY))) {
            File file = new File(UriConstant.APP_ROOT_PATH + API_PWD_TXT_NAME);
            if (file.exists()) {
                String apiPwd = FileUtil.readFile(file.getPath());
                LogUtils.d(TAG, "initData: 文件中的apiPwd = " + apiPwd);
                BaseSharedPreferences.setString(App.getInstance(), Constant.API_PASSWORD_KEY, apiPwd);
            }
        }
        getDeviceInfo(activity, deviceId, getReason);
    }

    private static void getDeviceInfo(final BaseActivity activity, int deviceId, final int getReason) {
        LogUtils.d(TAG, "getDeviceInfo: getReason = " + getReason);
        String url = "";
        switch (getReason) {
            case Constant.GET_INFO_FOR_UPDATE_CONFIG:
                url = RestUtils.getUrl(UriConstant.GET_NEW_DEVICE_INFO + deviceId);
                break;
            case Constant.GET_INFO_FOR_ACTIVATE:
                url = RestUtils.getUrl(UriConstant.GET_DEVICE_CONFIG + deviceId);
                break;
            case Constant.GET_INFO_FOR_UPDATE_APK:
                url = RestUtils.getUrl(UriConstant.GET_NEW_DEVICE_INFO + deviceId);
                break;
        }
        LogUtils.d(TAG, "getDeviceInfo: url = " + url);
        OkHttpUtils.getAsyn(url, new OkHttpUtils.StringCallback() {
            @Override
            public void onFailure(int errCode, Request request, IOException e) {
                LogUtils.e(TAG, "onFailure: get device info by id -- failed! " +
                        "errCode = " + errCode + ", request = " + request.toString());
                dealDeviceConfigResponseError(activity, GET_DEV_CONFIG_SERVER_BLOCK, getReason);
            }

            @Override
            public void onResponse(String response) {
                LogUtils.e(TAG, "onResponse: get device info by id -- response = " + response);
                JSONObject jsonObject = JSONObject.parseObject(response);
                if (null == jsonObject) {
                    LogUtils.e(TAG, "onResponse: get device info by id -- response cannot parse to JsonObject!");
                    dealDeviceConfigResponseError(activity, GET_DEV_CONFIG_DATA_ABNORMAL, getReason);
                    return;
                }
                if (!jsonObject.containsKey("code")) {
                    LogUtils.e(TAG, "onResponse: get device info by id -- response has not key \"code\"!");
                    dealDeviceConfigResponseError(activity, GET_DEV_CONFIG_DATA_ABNORMAL, getReason);
                    return;
                }
                if (0 == jsonObject.getInteger("code")) {
                    String data = jsonObject.getString("data");
                    if (TextUtils.isEmpty(data)) {
                        LogUtils.e(TAG, "onResponse: get device info by id -- data in response is empty!");
                        dealDeviceConfigResponseError(activity, GET_DEV_CONFIG_DATA_ABNORMAL, getReason);
                        return;
                    }
                    LogUtils.d(TAG, "onResponse: get device info by id -- response ok!");
                    BaseSharedPreferences.setString(App.getInstance(), Constant.DEVICE_CONFIG_STRING_KEY, data);
                    SysDeviceMonitorConfig deviceConfig = JSONObject.parseObject(data, SysDeviceMonitorConfig.class);
                    if (checkConfigAndSave(activity, deviceConfig)) {
                        if (Constant.GET_INFO_FOR_ACTIVATE == getReason) {
                            getInitVideo(activity, getReason);
                        } else {
                            getDeviceConfigFromLocal(activity, getReason);
                            // 如果是收到更新apk的消息后的获取信息，则获取完成后自动跳转到MainActivity
                            if (Constant.GET_INFO_FOR_UPDATE_APK == getReason) {
                                activity.moveToMainActivity();
                            }
                        }
                    } else {
                        LogUtils.e(TAG, "onResponse: get device info by id -- checkConfigAndSave() return false!");
                        dealDeviceConfigResponseError(activity, GET_DEV_CONFIG_DATA_ABNORMAL, getReason);
                    }
                } else {
                    LogUtils.e(TAG, "onResponse: get device info by id -- code in response is not 0!");
                    dealDeviceConfigResponseError(activity, GET_DEV_CONFIG_DATA_ABNORMAL, getReason);
                }

            }
        });
    }

    /**
     * config中是否包含所有所需信息
     * @param config
     * @return
     */
    public static boolean checkConfigAndSave(BaseActivity activity, SysDeviceMonitorConfig config) {
        if (null == config) {
            LogUtils.e(TAG, "checkConfigAndSave: config is null!");
            return false;
        }
        // 商业模式
        Integer drinkMode = config.getProductChargMode();
        boolean modeExist = false;
        int[] modes = {Constant.DRINK_MODE_WATER_SALE, Constant.DRINK_MODE_MACHINE_SALE, Constant.DRINK_MODE_MACHINE_RENT};
        for (int mode : modes) {
            if (null != drinkMode && drinkMode == mode) {
                modeExist = true;
                break;
            }
        }
        if (null == drinkMode || !modeExist) {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DRINK_MODE_KEY, Constant.DRINK_MODE_DEFAULT);
            uploadDataError("商业模式错误");
        } else {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DRINK_MODE_KEY, drinkMode);
        }
        // 租期
        String rentDeadline = null;
        Date productRentTime = config.getProductRentTime();
        if (null != productRentTime) {
            rentDeadline = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(productRentTime);
        }
        if (TextUtils.isEmpty(rentDeadline) || !TimeUtils.isAvailDate(rentDeadline, TimeUtils.getCurrentTime())) {
            BaseSharedPreferences.setString(App.getInstance(), Constant.RENT_DEADLINE_KEY, Constant.RENT_DEADLINE_DEFAULT);
            uploadDataError("租期到期时间错误");
        } else {
            BaseSharedPreferences.setString(App.getInstance(), Constant.RENT_DEADLINE_KEY, rentDeadline);
        }
        // 维护人员联系方式
        String contractInfo = config.getAdminUserTelephone();
        if (TextUtils.isEmpty(contractInfo)) {
            BaseSharedPreferences.setString(App.getInstance(), Constant.CONTRACT_INFO_KEY, Constant.CONTRACT_INFO_DEFAULT);
            uploadDataError("维护人员联系方式错误");
        } else {
            BaseSharedPreferences.setString(App.getInstance(), Constant.CONTRACT_INFO_KEY, contractInfo);
        }
        // PP棉
        Integer ppFlow = config.getMotCfgPpFlow();
        if (null == ppFlow || ppFlow < 2847000 || ppFlow > 8541000) {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_PP_FLOW_KEY, Constant.DEVICE_PP_FLOW_DEFAULT);
            uploadDataError("PP棉最大制水量错误");
        } else {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_PP_FLOW_KEY, ppFlow);
        }
        // 颗粒活性炭
        Integer grainCarbon = config.getMotCfgGrainCarbonFlow();
        if (null == grainCarbon || grainCarbon < 5677000 || grainCarbon > 17032000) {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_GRAIN_CARBON_KEY, Constant.DEVICE_GRAIN_CARBON_DEFAULT);
            uploadDataError("颗粒活性炭最大制水量错误");
        } else {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_GRAIN_CARBON_KEY, grainCarbon);
        }
        // 压缩活性炭
        Integer pressCarbon = config.getMotCfgPressCarbonFlow();
        if (null == pressCarbon || pressCarbon < 5677000 || pressCarbon > 17032000) {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_PRESS_CARBON_KEY, Constant.DEVICE_PRESS_CARBON_DEFAULT);
            uploadDataError("压缩活性炭最大制水量错误");
        } else {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_PRESS_CARBON_KEY, pressCarbon);
        }
        // 后置活性炭
        Integer poseCarbon = config.getMotCfgPoseCarbonFlow();
        if (null == poseCarbon || poseCarbon < 5677000 || poseCarbon > 17032000) {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_POSE_CARBON_KEY, Constant.DEVICE_POSE_CARBON_DEFAULT);
            uploadDataError("后置活性炭最大制水量错误");
        } else {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_POSE_CARBON_KEY, poseCarbon);
        }
        // RO反渗透膜
        Integer roFlow = config.getMotCfgRoFlow();
        if (null == roFlow || roFlow < 4844000 || roFlow > 14532000) {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_RO_FLOW_KEY, Constant.DEVICE_RO_FLOW_DEFAULT);
            uploadDataError("RO反渗透膜最大制水量错误");
        } else {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_RO_FLOW_KEY, roFlow);
        }
        // 监控数据上报时间
        Integer upTime = config.getMotCfgUpTime();
        if (null == upTime || upTime < 10 || upTime > 86400) {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_UP_TIME_KEY, Constant.DEVICE_UP_TIME_DEFAULT);
            uploadDataError("监控数据上报时间错误");
        } else {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_UP_TIME_KEY, upTime);
        }
        // 音量
        Integer motCfgVolume = config.getMotCfgVolume();
        if (null == motCfgVolume || motCfgVolume < 0 || motCfgVolume > 100) {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_VOLUME_KEY, Constant.DEVICE_VOLUME_DEFAULT);
            uploadDataError("设备音量错误");
        } else {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_VOLUME_KEY, motCfgVolume);
        }
        // 冲洗间隔
        Integer flushInterval = config.getMotCfgFlushInterval();
        if (null == flushInterval || flushInterval < 30 || flushInterval > 240) {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_FLUSH_INTERVAL_KEY, Constant.DEVICE_FLUSH_INTERVAL_DEFAULT);
            uploadDataError("冲洗间隔错误");
        } else {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_FLUSH_INTERVAL_KEY, flushInterval);
        }
        // 冲洗时长
        Integer flushDuration = config.getMotCfgFlushDuration();
        if (null == flushDuration || flushDuration < 10 || flushDuration > 40) {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_FLUSH_DURATION_KEY, Constant.DEVICE_FLUSH_DURATION_DEFAULT);
            uploadDataError("冲洗时长错误");
        } else {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_FLUSH_DURATION_KEY, flushDuration);
        }
        // 加热临界温度
        Integer heatingTemp = config.getMotCfgHeatingTemp();
        if (null == heatingTemp || heatingTemp < 80 || heatingTemp > 100) {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_HEATING_TEMP_KEY, Constant.DEVICE_HEATING_TEMP_DEFAULT);
            uploadDataError("加热临界温度错误");
        } else {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_HEATING_TEMP_KEY, heatingTemp);
        }
        // 制冷临界温度
        Integer coolingTemp = config.getMotCfgCoolingTemp();
        if (null == coolingTemp || coolingTemp < 0 || coolingTemp > 10) {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_COOLING_TEMP_KEY, Constant.DEVICE_COOLING_TEMP_DEFAULT);
            uploadDataError("制冷临界温度错误");
        } else {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_COOLING_TEMP_KEY, coolingTemp);
        }
        // 是否全天加热
        Integer heatingAllday = config.getMotCfgHeatingAllday();
        if (null == heatingAllday || (heatingAllday != 0 && heatingAllday != 1)) {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_HEATING_ALL_DAY_KEY, Constant.DEVICE_HEATING_ALL_DAY_DEFAULT);
            uploadDataError("是否全天加热错误");
        } else {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_HEATING_ALL_DAY_KEY, heatingAllday);
        }
        // 是否全天制冷
        Integer coolingAllday = config.getMotCfgCoolingAllday();
        if (null == coolingAllday || (coolingAllday != 0 && coolingAllday != 1)) {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_COOLING_ALL_DAY_KEY, Constant.DEVICE_COOLING_ALL_DAY_DEFAULT);
            uploadDataError("是否全天加热错误");
        } else {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.DEVICE_COOLING_ALL_DAY_KEY, coolingAllday);
        }
        // 加热时段
        String heatingInterval = config.getMotCfgHeatingInterval();
        BaseSharedPreferences.setString(App.getInstance(), Constant.DEVICE_HEATING_INTERVAL_KEY,
                TextUtils.isEmpty(heatingInterval) ? Constant.DEVICE_HEATING_INTERVAL_DEFAULT : heatingInterval);
        // 制冷时段
        String coolingInterval = config.getMotCfgCoolingInterval();
        BaseSharedPreferences.setString(App.getInstance(), Constant.DEVICE_COOLING_INTERVAL_KEY,
                TextUtils.isEmpty(coolingInterval) ? Constant.DEVICE_COOLING_INTERVAL_DEFAULT : coolingInterval);
        // 设备编号
        String deviceNumber = config.getDeviceNumber();
        BaseSharedPreferences.setString(App.getInstance(), Constant.DEVICE_NUMBER_KEY,
                TextUtils.isEmpty(deviceNumber) ? Constant.DEVICE_NUMBER_DEFAULT : deviceNumber);
        // 单次取水量
        Integer maxGet = config.getMaxGetwaterCapacity();
        if (null == maxGet || maxGet < 50 || maxGet > 4000) {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.MAX_GET_WATER_CAPACITY_KEY, Constant.MAX_GET_WATER_CAPACITY_DEFAULT);
            uploadDataError("单次取水量错误");
        } else {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.MAX_GET_WATER_CAPACITY_KEY, maxGet);
        }
        // 单次消费取水量
        Integer maxCosume = config.getMaxConsumeCapacity();
        if (null == maxCosume || maxCosume < 50 || maxCosume > 10000) {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.MAX_CONSUME_CAPACITY_KEY, Constant.MAX_CONSUME_CAPACITY_DEFAULT);
            uploadDataError("单次消费取水量错误");
        } else {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.MAX_CONSUME_CAPACITY_KEY, maxCosume);
        }
        // 免费广告倒计时
        Integer adCountDown = config.getAdvsCountDown();
        if (null == adCountDown || adCountDown < 5 || adCountDown > 180) {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.ADVS_COUNT_DOWN_KEY, Constant.ADVS_COUNT_DOWN_DEFAULT);
            uploadDataError("免费广告倒计时错误");
        } else {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.ADVS_COUNT_DOWN_KEY, adCountDown);
        }
        // 取水操作倒计时
        Integer operateCountDown = config.getOperationCountDown();
        if (null == operateCountDown || operateCountDown < 20 || operateCountDown > 40) {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.OPERATION_COUNT_DOWN_KEY, Constant.OPERATION_COUNT_DOWN_DEFAULT);
            uploadDataError("免费广告倒计时错误");
        } else {
            BaseSharedPreferences.setInt(App.getInstance(), Constant.OPERATION_COUNT_DOWN_KEY, operateCountDown);
        }
        return true;
    }

    private static void uploadDataError(String content) {
        String postdata = "";
        Exceptiona exceptiona = new Exceptiona();
        exceptiona.setExceptionalContent(content);
        exceptiona.setExceptionalStatus(0);//默认是没有处理的
        exceptiona.setExceptionalSubject("android bug提交");
        exceptiona.setExceptionalType(6);
        exceptiona.setExceptionalTime(TimeUtils.getCurrentTime());
        postdata = JSON.toJSONString(exceptiona);
        String url = RestUtils.getUrl(RestUtils.getUrl(UriConstant.SAVE_CRASH_LOG));
        OkHttpUtils.postAsyn(url, new OkHttpUtils.StringCallback() {
            @Override
            public void onFailure(int errCode, Request request, IOException e) {
            }
            @Override
            public void onResponse(String response) {
                LogUtils.e(TAG,"CrashHandler提交成功"+response);
            }
        },postdata);

    }

    /**
     * 获取设备信息时回应的数据有问题时的处理
     */
    private static void dealDeviceConfigResponseError(final BaseActivity activity, int reason, int getReason) {
        LogUtils.d(TAG, "dealDeviceConfigResponseError: 获取设备参数失败");
        String msg = "";
        switch (reason) {
            case GET_DEV_CONFIG_SERVER_BLOCK:
            case GET_INIT_VIDEO_SERVER_BLOCK:
                msg = App.getInstance().getString(R.string.dialog_msg_no_device_config_for_server);
                break;
            case GET_DEV_CONFIG_DATA_ABNORMAL:
                msg = App.getInstance().getString(R.string.dialog_msg_no_device_config_for_data);
                break;
            case GET_INIT_VIDEO_DATA_ABNORMAL:
                msg = App.getInstance().getString(R.string.dialog_msg_no_video_config_for_data);
                break;
        }
        uploadDataError(msg);
        // 更新参数时的参数错误，仅上报，不做处理；其他的则上报并弹框。
        if (Constant.GET_INFO_FOR_UPDATE_CONFIG != getReason) {
            dialog = new AlertDialog.Builder(activity.mContext)
                    .setCancelable(false)
                    .setMessage(msg)
                    .setPositiveButton(App.getInstance().getString(R.string.dialog_btn_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (null != dialog) {
                                dialog.dismiss();
                                activity.moveToTempActivity();
                            }
                        }
                    })
                    .create();
            dialog.show();
        }
    }

    private static void getInitVideo(final BaseActivity activity, final int getReason) {
        OkHttpUtils.postAsyn(RestUtils.getUrl(UriConstant.GET_INIT_AD_VIDEO_LIST), new OkHttpUtils.StringCallback() {
            @Override
            public void onFailure(int errCode, Request request, IOException e) {
                LogUtils.d(TAG, "onFailure: get init video: 获取视频策略失败！errCode = " + errCode +
                        ", response = " + request.toString());
                dealDeviceConfigResponseError(activity, GET_INIT_VIDEO_SERVER_BLOCK, getReason);
            }

            @Override
            public void onResponse(String response) {
                LogUtils.d(TAG, "onResponse: get init video: 获取视频策略成功！ response = " + response);
                JSONObject jsonObject = JSONObject.parseObject(response);
                if (null == jsonObject) {
                    LogUtils.d(TAG, "onResponse: get init video: 视频策略获取response数据错误！");
                    dealDeviceConfigResponseError(activity, GET_INIT_VIDEO_DATA_ABNORMAL, getReason);
                    return;
                }
                Object data = jsonObject.get("data");
                if (null == data) {
                    LogUtils.d(TAG, "onResponse: get init video: 视频策略获取response的data数据错误！");
                    dealDeviceConfigResponseError(activity, GET_INIT_VIDEO_DATA_ABNORMAL, getReason);
                    return;
                }
                try {
                    activity.adList = JSONArray.parseArray(data.toString(), AdvsVideo.class);
                } catch (JSONException e) {
                    LogUtils.d(TAG, "onResponse: get init video: 推送数据无法转换成 AdvsVideo！");
                    dealDeviceConfigResponseError(activity, GET_INIT_VIDEO_DATA_ABNORMAL, getReason);
                }
                if (null == activity.adList || 0 == activity.adList.size()) {
                    LogUtils.d(TAG, "onResponse: get init video: 推送数据有误！");
                    dealDeviceConfigResponseError(activity, GET_INIT_VIDEO_DATA_ABNORMAL, getReason);
                    return;
                }
                activity.dlIndex = 0;
                activity.downloadInitVideo(activity.adList);
            }
        });
    }

    protected void downloadInitVideo(List<AdvsVideo> adList) {
        // 下载完毕，则去同步各种数据
        if (dlIndex >= adList.size()) {
            LogUtils.d(TAG, "downloadInitVideo: dl_info: 全部视频状态为已下载，return");
            handler.sendEmptyMessageDelayed(Constant.MSG_ALL_DOWN_COMPLETE, Constant.ALL_DOWN_WAIT_TIME);
            return;
        }
        // 没下完，则下载
        AdvsVideo ad = adList.get(dlIndex);
        // 如果为空，则下载下一个
        if (null == ad) {
            LogUtils.d(TAG, "downloadInitVideo: dl_info: 本条广告为空，return");
            dlIndex++;
            downloadInitVideo(adList);
            return;
        }
        // 下载地址为空，上报地址错误
        if (TextUtils.isEmpty(ad.getAdvsVideoDownloadPath())) {
            LogUtils.d(TAG, "onError: dl_info: URL为空！");
            saveDeviceServiceNotice(Constant.NOTICE_TYPE_AD_URL_WRONG, Constant.NOTICE_LEVEL_ABNORMAL,
                    mContext.getString(R.string.notice_content_incorrect_url));
            adList.remove(ad);
            return;
        }
        downloadVideo(ad);
    }

    /**
     * 下载初始视频
     * @param ad
     */
    private void downloadVideo(final AdvsVideo ad) {
        if (isDownloading) {
            LogUtils.d(TAG, "downloadVideo: dl_info: 正在下载，return..");
            if (handler.hasMessages(Constant.MSG_WAITING_THEN_DOWNLOAD)) {
                handler.removeMessages(Constant.MSG_WAITING_THEN_DOWNLOAD);
            }
            handler.sendEmptyMessageDelayed(Constant.MSG_WAITING_THEN_DOWNLOAD,
                    Constant.IS_DOWNING_WAIT_TIME * 1000);
            return;
        }
        String downloadPath = ad.getAdvsVideoDownloadPath();
        LogUtils.d(TAG, "downloadVideo: dl_info: 开始下载广告视频 dlIndex = " + dlIndex + ", url = " + downloadPath);
        isDownloading = true;
        DownloadManager dlManager = DownloadManager.getInstance();
        dlManager.setDownloadCallback(new DownloadCallback() {
            @Override
            public void onProgress(int progress) {
                LogUtils.d(TAG, "onProgress: dl_info: 正在下载.. progress = " + progress);

            }

            @Override
            public void onComplete(String localPath) {
                LogUtils.d(TAG, "downloadInitVideo: dl_info: 第" + dlIndex + "个初始视频下载完成。localPath -- " + localPath);
                isDownloading = false;
                AdvsVideo ad = adList.get(dlIndex);
                ad.setLocal(true);
                ad.setAdvsVideoLocaltionPath(localPath);
                adList.set(dlIndex, ad);
                dlIndex++;
                downloadInitVideo(adList);
            }

            @Override
            public void onError(String msg) {
                LogUtils.d(TAG, "onError: dl_info: 下载错误！msg -- " + msg);
                isDownloading = false;
                // 网址错误则上报错误信息；其他错误则放在最后再下
                if (msg.contains(Constant.DOWN_ERROR_MSG_WRONG_URL) || msg.contains(Constant.DOWN_ERROR_MSG_WRONG_BASE_URL)) {
                    LogUtils.d(TAG, "onError: dl_info: URL有误！");
                    if (dlIndex < adList.size()) {
                        saveDeviceServiceNotice(Constant.NOTICE_TYPE_AD_URL_WRONG, Constant.NOTICE_LEVEL_ABNORMAL,
                                mContext.getString(R.string.notice_content_incorrect_url));
                        adList.remove(dlIndex);
                        downloadInitVideo(adList);
                    }
                    return;
                }
                LogUtils.d(TAG, "onError: dl_info: 将本广告视频移动至list最后");
                AdvsVideo advsVideo = adList.get(dlIndex);
                adList.remove(dlIndex);
                adList.add(advsVideo);
                if (handler.hasMessages(Constant.MSG_WAITING_THEN_DOWNLOAD)) {
                    handler.removeMessages(Constant.MSG_WAITING_THEN_DOWNLOAD);
                }
                handler.sendEmptyMessageDelayed(Constant.MSG_WAITING_THEN_DOWNLOAD,
                        Constant.IS_DOWNING_WAIT_TIME * 1000);

            }
        });
        String s = downloadPath.substring(0, downloadPath.lastIndexOf('/') + 1);
        LogUtils.d(TAG, "downloadVideo: dl_info: baseUrl = " + s);
        dlManager.startDown(mContext, Constant.DOWNLOADAPK_ID,
                downloadPath/*.substring(0, downloadPath.lastIndexOf('/') + 1)*/, downloadPath,
                UriConstant.APP_ROOT_PATH + UriConstant.VIDEO_DIR);
    }

    /**
     * 保存设备维护的预警信息
     * @param type
     * @param level
     * @param content
     */
    public void saveDeviceServiceNotice(int type, int level, String content) {
        SysDeviceNoticeAO notice = new SysDeviceNoticeAO(
                BaseSharedPreferences.getInt(mContext, Constant.DEVICE_ID_KEY), type, level,
                mContext.getString(R.string.notice_subject_device_service), content, TimeUtils.getCurrentTime());
        String noticeString = JSON.toJSONString(notice);
        LogUtils.e(TAG,"noticeString"+noticeString);
        try {
            new XutilsInit(mContext).getDb().save(notice);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存现场维护的预警信息
     * @param type
     * @param level
     * @param content
     */
    public void saveFieldServiceNotice(int type, int level, String content) {
        SysDeviceNoticeAO notice = new SysDeviceNoticeAO(
                BaseSharedPreferences.getInt(mContext, Constant.DEVICE_ID_KEY), type, level,
                mContext.getString(R.string.notice_subject_field_service), content, TimeUtils.getCurrentTime());
        try {

            new XutilsInit(mContext).getDb().save(notice);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备信息
     */
    protected static void getDeviceConfigFromLocal(BaseActivity activity, int getReason) {
        int upTime = 0;
        int volume = 0;
        int motCfgsingle = 0;
        int maxConsumeCapacity = 0;
        int device_flush_interval = 0;//冲洗间隔
        int device_flush_duration = 0;//持续时长
        int device_heating_interval = 0;//热水温度
        int device_cooling_interval = 0;//冷水温度
        int advs_count_down = 0;//广告倒计时
        int operation_count_down = 0;//操作倒计时
        // 1、从SharedPreference获取
        if (BaseSharedPreferences.containInt(App.getInstance(), Constant.DEVICE_ID_KEY)) {
            deviceId = BaseSharedPreferences.getInt(App.getInstance(), Constant.DEVICE_ID_KEY);
        }
        if (BaseSharedPreferences.containInt(App.getInstance(), Constant.DRINK_MODE_KEY)) {
            drinkMode = BaseSharedPreferences.getInt(App.getInstance(), Constant.DRINK_MODE_KEY);
        }
        if (BaseSharedPreferences.containInt(App.getInstance(), Constant.RENT_DEADLINE_KEY)) {
            rentDeadline = BaseSharedPreferences.getString(App.getInstance(), Constant.RENT_DEADLINE_KEY);
        }
        if (BaseSharedPreferences.containInt(App.getInstance(), Constant.CONTRACT_INFO_KEY)) {
            contractInfo = BaseSharedPreferences.getString(App.getInstance(), Constant.CONTRACT_INFO_KEY);
        }
        //no end
        //上报时间间隔
        if(BaseSharedPreferences.containInt(App.getInstance(),Constant.DEVICE_UP_TIME_KEY)){
             upTime = BaseSharedPreferences.getInt(App.getInstance(), Constant.DEVICE_UP_TIME_KEY);
        }
        // 设备音量
        if(BaseSharedPreferences.containInt(App.getInstance(),Constant.DEVICE_VOLUME_KEY)){
            volume = BaseSharedPreferences.getInt(App.getInstance(), Constant.DEVICE_VOLUME_KEY);
        }
        //设备单次出水最大量
        if(BaseSharedPreferences.containInt(App.getInstance(),Constant.MAX_CONSUME_CAPACITY_KEY)){
            maxConsumeCapacity = BaseSharedPreferences.getInt(App.getInstance(), Constant.MAX_CONSUME_CAPACITY_KEY);
        }
        //设备单次取水的最大量
        if(BaseSharedPreferences.containInt(App.getInstance(),Constant.MAX_GET_WATER_CAPACITY_KEY)){
            motCfgsingle = BaseSharedPreferences.getInt(App.getInstance(), Constant.MAX_GET_WATER_CAPACITY_KEY);
        }
        //冲洗间隔
        if(BaseSharedPreferences.containInt(App.getInstance(),Constant.DEVICE_FLUSH_INTERVAL_KEY)){
            device_flush_interval = BaseSharedPreferences.getInt(App.getInstance(),Constant.DEVICE_FLUSH_INTERVAL_KEY);
        }
        //冲洗时长
        if(BaseSharedPreferences.containInt(App.getInstance(),Constant.DEVICE_FLUSH_DURATION_KEY)){
            device_flush_duration = BaseSharedPreferences.getInt(App.getInstance(),Constant.DEVICE_FLUSH_DURATION_KEY);
        }
        //热水温度
        if(BaseSharedPreferences.containInt(App.getInstance(),Constant.DEVICE_HEATING_TEMP_KEY)){
            device_heating_interval = BaseSharedPreferences.getInt(App.getInstance(),Constant.DEVICE_HEATING_TEMP_KEY);
        }
        //冷水温度
        if(BaseSharedPreferences.containInt(App.getInstance(),Constant.DEVICE_COOLING_TEMP_KEY)){
            device_cooling_interval = BaseSharedPreferences.getInt(App.getInstance(),Constant.DEVICE_COOLING_TEMP_KEY);
        }
        //广告倒计时
        if(BaseSharedPreferences.containInt(App.getInstance(),Constant.ADVS_COUNT_DOWN_KEY)){
            advs_count_down = BaseSharedPreferences.getInt(App.getInstance(),Constant.ADVS_COUNT_DOWN_KEY);
        }
        //操作倒计时
        if(BaseSharedPreferences.containInt(App.getInstance(),Constant.OPERATION_COUNT_DOWN_KEY)){
            operation_count_down = BaseSharedPreferences.getInt(App.getInstance(),Constant.OPERATION_COUNT_DOWN_KEY);
        }

        // 2、没有则跳转到BreakDown页面
        if (0 == deviceId) {
            LogUtils.d(TAG, "getDeviceConfigFromLocal: 无deviceId！");
//            activity.moveToBreakDownActivity(App.getInstance().getString(R.string.break_down_reason_no_device_id));
            dealDeviceConfigResponseError(activity,GET_DEV_CONFIG_DATA_ABNORMAL, getReason);
            return;
        }
        if (0 == drinkMode) {
            LogUtils.d(TAG, "getDeviceConfigFromLocal: 无drinkMode！");
//            activity.moveToBreakDownActivity(App.getInstance().getString(R.string.break_down_reason_no_drink_mode));
            dealDeviceConfigResponseError(activity,GET_DEV_CONFIG_DATA_ABNORMAL, getReason);
            return;
        }
        if (Constant.DRINK_MODE_MACHINE_RENT == drinkMode && TextUtils.isEmpty(rentDeadline)) {
            LogUtils.d(TAG, "getDeviceConfigFromLocal: rentDeadline！");
//            activity.moveToBreakDownActivity(App.getInstance().getString(R.string.break_down_reason_no_rent_deadline));
            dealDeviceConfigResponseError(activity,GET_DEV_CONFIG_DATA_ABNORMAL, getReason);
            return;
        }
        if (TextUtils.isEmpty(contractInfo)) {
            LogUtils.d(TAG, "getDeviceConfigFromLocal: contractInfo！");
//            activity.moveToBreakDownActivity(App.getInstance().getString(R.string.break_down_reason_no_contract_info));
            dealDeviceConfigResponseError(activity,GET_DEV_CONFIG_DATA_ABNORMAL, getReason);
            return;
        }
        if (0 == upTime) {
            LogUtils.d(TAG, "getDeviceConfigFromLocal: upTime为0！");
            dealDeviceConfigResponseError(activity,GET_DEV_CONFIG_DATA_ABNORMAL, getReason);
            return;
        }

        if (0 == maxConsumeCapacity) {
            LogUtils.d(TAG, "getDeviceConfigFromLocal: 最大取水量为0！");
            dealDeviceConfigResponseError(activity,GET_DEV_CONFIG_DATA_ABNORMAL, getReason);
            return;
        }

        if (0 == motCfgsingle) {
            LogUtils.d(TAG, "getDeviceConfigFromLocal: 单次取水量为0！");
            dealDeviceConfigResponseError(activity,GET_DEV_CONFIG_DATA_ABNORMAL, getReason);
            return;
        }

        if (0 == device_flush_interval) {
            LogUtils.d(TAG, "getDeviceConfigFromLocal: 冲洗间隔没有设置！");
            dealDeviceConfigResponseError(activity,GET_DEV_CONFIG_DATA_ABNORMAL, getReason);
            return;
        }

        if (0 == device_flush_duration) {
            LogUtils.d(TAG, "getDeviceConfigFromLocal: 冲洗持续时长没有设置！");
            dealDeviceConfigResponseError(activity,GET_DEV_CONFIG_DATA_ABNORMAL, getReason);
            return;
        }
        if (0 == device_heating_interval) {
            LogUtils.d(TAG, "getDeviceConfigFromLocal: 热水温度没有设置！");
            dealDeviceConfigResponseError(activity,GET_DEV_CONFIG_DATA_ABNORMAL, getReason);
            return;
        }

        if (0 == device_cooling_interval) {
            LogUtils.d(TAG, "getDeviceConfigFromLocal: 冷水温度没有设置！");
            dealDeviceConfigResponseError(activity,GET_DEV_CONFIG_DATA_ABNORMAL, getReason);
            return;
        }

        if (0 == advs_count_down) {
            LogUtils.d(TAG, "getDeviceConfigFromLocal: 免费广告播放时长没有设置！");
            dealDeviceConfigResponseError(activity,GET_DEV_CONFIG_DATA_ABNORMAL, getReason);
            return;
        }

        if (0 == operation_count_down) {
            LogUtils.d(TAG, "getDeviceConfigFromLocal: 操作最长时长没有设置！");
            dealDeviceConfigResponseError(activity,GET_DEV_CONFIG_DATA_ABNORMAL, getReason);
            return;
        }

        // 3、设置设备
        activity.setDevice();

    }

    /**
     * 将设备参数
     */
    protected void setDevice() {
        //设置加热的参数
        controllerUtils = new ControllerUtils(BaseActivity.this);
        controllerUtils.operateDevice(11, true);
        //按照时段播放
    }

    /**
     * 跳转到LoadApp执行更新指令
     */
    protected void launchLoadApp(int commandId) {
        LogUtils.i(TAG, "TPush: 即将启动LoadApp，启动原因（1：更新 2：激活）-- " + commandId);
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageName);

        final PackageManager packageManager = mContext.getPackageManager();
        List<ResolveInfo> resolveInfo =
                packageManager.queryIntentActivities(resolveIntent, 0);

        if (resolveInfo != null && resolveInfo.size() != 0) {
            ResolveInfo ri = resolveInfo.iterator().next();
            if (ri != null) {
                packageName = ri.activityInfo.packageName;
                activityName = ri.activityInfo.name;
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName(packageName, activityName);
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.KEY_LAUNCH_LOAD_APP_COMMAND, commandId);
            intent.putExtras(bundle);
            intent.setComponent(cn);
//            if (null != breakInstance) {
//                breakInstance.finish();
//            }
            if (null != BreakDownActivity.instance) {
                BreakDownActivity.instance.finish();
            }
            startActivity(intent);
            finish();
        }
    }


    protected void showToast(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }
}

package com.xhh.ysj.constants;

public class Constant {
    public static final int DOWNLOADAPK_ID = 10;
    public static final String DB_NAME = "xinhonghai.db";
    public static final int DOWNLOAD_MAX_RETRY_TIME = 3;  //
    public static final String DOWN_ERROR_MSG_WRONG_URL = "404 Not Found";
    public static final String DOWN_ERROR_MSG_WRONG_BASE_URL = "Incorrect BaseUrl";
    public static final String DOWN_ERROR_MSG_WRONG_NETWORK_UNAVAIL = "Network Unavailable";
    public static final String DOWN_ERROR_MSG_WRONG_NO_MISTAKE = "下载出错，下载被停止了";

    public static final String VIDEO_PUSH_HANDLE_DOING = "1";  // push文件第一个字符存储的状态：正在处理
    public static final String VIDEO_PUSH_HANDLE_TO_DO = "0";  // push文件第一个字符存储的状态：尚未处理

    /*各种等待时间*/
    public static final int FAST_CLICK_DELAY_TIME = 1000;  // 这个时间以内的认为是重复点击（毫秒）
    public static final int XG_PUSH_ID_TIMEOUT_TIME = 15000;  // 申请pushId的二维码的超时时间
    public static final int POOL_TIME = 10000;// 轮询get_ioRunData()时间间隔ms（付工那边的代码start）
    public static final int RECEIVE_PUSH_VIDEO_STRATEGY_WAIT_TIME = 1 * 60; // 收到推送策略后等多久再处理（秒）
    public static final int IS_DOWNING_WAIT_TIME = 10; // 要下载视频时如果正在下载，等待多久再发请求（秒）
    public static final int ALL_DOWN_WAIT_TIME = 3000; // 全部下载完毕后等多久在开始刷新及播放（毫秒）
    public static final int UPDATE_SCODE = 6 * 60 * 60 * 1000; // 二维码更新周期
    public static final long UPLOAD_TIME = 60 * 60 * 1000;// 水质上报周期
    public static final long WARNING_TIME = 60 * 1000;//预警上报周期
//    public static final long WATER_SAVETIME = 1000*60;//获取水质的时间间隔
//    public static final long WATER_WARNINGTIME = 1000*60;//水质预警的获取时间
//    public static final long TEMPSCODE_TIME = 24 * 60 * 60 * 1000;
    public static final long AD_RECORD_UPLOAD_PERIOD = 1 * 60 * 60;// 广告视频上报播放记录的周期（秒）
    public static final long CHECK_RENT_DEADLINE_PERIOD = 24 * 60 * 60;// 检查一次租期是否到期的周期（秒）
    public static final long CHECK_TDS_PERIOD = 5 * 60 * 1000;// 检查一次租期是否到期的周期（毫秒）
    public static final long TIME_ALL_DAY = 24 * 60 * 60 * 1000;
    public static final long DEVICE_ENDTIME = 1000*60*60;//设备最后在线时间
    public static final int DISMISS_POP_TIME = 30 * 1000; // 喝水类型弹框和二维码弹框出现后，多久消失（毫秒）

    /*水的各种标准*/
    public static final double TDSERROR = 200;//出水的水质比这个更低的话就是水质有问题

    /*推送操作类型*/
    public static final int PUSH_OPERATION_TYPE_OPERATE = 1; // 操作
    public static final int PUSH_OPERATION_TYPE_CONFIG = 2; // 配置（视频）
    public static final int PUSH_OPERATION_TYPE_LOGIN = 3; // 登录
    public static final int PUSH_OPERATION_TYPE_UPDATE_ID = 4; // 更新信鸽ID
    public static final int PUSH_OPERATION_TYPE_UPDATE_APK = 5; // 更新APK
    public static final int PUSH_OPERATION_TYPE_TARGET = 6; // 行业标签（视频）
    public static final int PUSH_OPERATION_TYPE_FILTER_OVER = 7; // 滤芯过期
    public static final int PUSH_OPERATION_TYPE_UPDATE_CONFIG = 8; // 参数更新

    /*预警故障类别*/
    public static final int NOTICE_TYPE_NO_NETWORK = 1;  // 断网
    public static final int NOTICE_TYPE_LESS_FILTER = 2;  // 滤芯不足
    public static final int NOTICE_TYPE_NO_FILTER = 3;  // 滤芯用完
    public static final int NOTICE_TYPE_WATER_QUALITY_UNUSUAL = 4;  // 水质异常
    public static final int NOTICE_TYPE_LESS_CUP = 5;  // 水杯不足
    public static final int NOTICE_TYPE_NO_CUP = 6;  // 水杯用完
    public static final int NOTICE_TYPE_WATER_CONSUMPTION_UNUSUAL = 7; // 出水量异常
    public static final int NOTICE_TYPE_ELEC_LEAK = 8;  // 漏电
    public static final int NOTICE_TYPE_WATER_LEAK = 9;  // 漏水
    public static final int NOTICE_TYPE_RAW_WATER_LACK = 10;  // 原水缺水
    public static final int NOTICE_TYPE_PURIFIED_TDS_ABNORMAL = 11;  // 原水缺水
    public static final int NOTICE_TYPE_AD_URL_WRONG = 12;  // 广告视频下载地址错误
    public static final int NOTICE_TYPE_COM_NO_NETWORK = 13;  // 串口通讯异常
    public static final int NOTICE_TYPE_RENT_EXPIRED = 14;  // 租期到期
    public static final int NOTICE_TYPE_BHOTALARM = 15;//热水温度超高
    public static final int NOTICE_TYPE_BCOOLALARM = 16;//冷水温度超高
    public static final int NOTICE_BHOTERROR = 17;//热水异常（建热30分钟五毒上升《2）
    public static final int NOTICE_BCOOLERROR = 18;//制冷异常
    public static final int NOTICE_BFLOWERROR = 19;//流量计故障
    public static final int NOTICE_BTDSERROR = 20;//TDS超过200
    public static final int NOTICE_BWATERERROR = 21;//制水故障
    public static final int NOTICE_BUVERROR = 22;//UV灯故障
    public static final int NOTICE_BLEVELSWERROR = 23;//水位开关故障
    public static final int NOTICE_BLEVELHIGH = 24;//超水位

    /*设备操作*/
    public static final String DEVICE_OPERATE_FLUSH = "1";
    public static final String DEVICE_OPERATE_UNCAP = "2";
    public static final String DEVICE_OPERATE_ON_OFF = "3";
    public static final  String DO_HOTTING = "7";//允许加热
    public static final String DO_TURNOFFHOTTING = "8";//禁止加热
    public static final String DO_COOLING = "9";//允许制冷
    public static final String DO_TURNOFFCOOLING = "10";//禁止制冷

    /*预警级别*/
    public static final int NOTICE_LEVEL_ABNORMAL = 0;
    public static final int NOTICE_LEVEL_BREAK_DOWN = 1;

    /*intent和bundle的key*/
    public static final String KEY_LAUNCH_DISPENSER_APP_ERRCODE = "errCode";
    public static final String KEY_LAUNCH_LOAD_APP_COMMAND = "command";
    public static final String KEY_FREE_AD_DURATION = "free_ad_duration";
    public static final String KEY_BREAK_DOWN_ERR_REASON = "break_down_errcode";

    /*msg的what值*/
    public static final int MSG_NEW_AD_VIDEO_STRATEGY_PUSH = 11;
    public static final int MSG_WAITING_THEN_DOWNLOAD = 12;
    public static final int MSG_UPDATE_SCODE = 13;
    public static final int MSG_DATA_DELETE = 2;
    public static final int MSG_ALL_DOWN_COMPLETE = 14;
    public static final int MSG_QUIT_DRINK_INTERFACE = 15;
    public static final int MSG_CHECK_OVERFLOW = 16;
    public static final int MSG_XG_PUSH_ID_TIMEOUT = 17;
    public static final int MSG_OTHER = 18;
    public static final int MSG_DISMISS_POP = 19;


    /*定时任务的操作类型*/
    public static final int TIME_OPERATE_UPDATEWATER = 1;
    public static final int TIME_OPETATE_UPDATESCODE = 2;
    public static final int TIME_OPETATE_WARNING = 3;
    public static final int TIME_OPETATE_VIDEO = 4;
    public static final int TIME_DEVICE_ENDTIME = 5;//设置设备最后在线时间
    public static final int TIME_OPETATE_TDS = 6;//设置去除率

    public static final int TIME_START_HOTTING = 7;
    public static final int TIME_END_HOTTING= 8;
    public static final int TIME_START_COOLING = 9;
    public static final int TIME_END_COOLING = 10;


    /*BaseSharedPreferences的Key值*/
    public static final String SCODEKEY = "scode";
    public static final String DEVICE_CONFIG_STRING_KEY = "device_config_string";
    public static final String DEVICE_ID_KEY = "device_id";
    public static final String DRINK_MODE_KEY = "drink_mode";
    public static final String RENT_DEADLINE_KEY = "rent_deadline";
    public static final String CONTRACT_INFO_KEY = "contract_info";
    public static final String DEVICE_PP_FLOW_KEY = "device_pp_flow";
    public static final String DEVICE_GRAIN_CARBON_KEY = "device_grain_carbon";
    public static final String DEVICE_PRESS_CARBON_KEY = "device_press_carbon";
    public static final String DEVICE_POSE_CARBON_KEY = "device_pose_carbon";
    public static final String DEVICE_RO_FLOW_KEY = "device_ro_flow";
    public static final String DEVICE_UP_TIME_KEY = "device_up_time";
    public static final String DEVICE_VOLUME_KEY = "device_volume";
    public static final String DEVICE_FLUSH_INTERVAL_KEY = "device_flush_interval";
    public static final String DEVICE_FLUSH_DURATION_KEY = "device_flush_duration";
    public static final String DEVICE_HEATING_TEMP_KEY = "device_heating_temp";
    public static final String DEVICE_COOLING_TEMP_KEY = "device_cooling_temp";
    public static final String DEVICE_HEATING_ALL_DAY_KEY = "device_heating_all_day";
    public static final String DEVICE_COOLING_ALL_DAY_KEY = "device_cooling_all_day";
    public static final String DEVICE_HEATING_INTERVAL_KEY = "device_heating_interval";
    public static final String DEVICE_COOLING_INTERVAL_KEY = "device_cooling_interval";
    public static final String DEVICE_NUMBER_KEY = "device_number";
    public static final String MAX_GET_WATER_CAPACITY_KEY = "max_get_water_capacity";//单次出水最大量
    public static final String MAX_CONSUME_CAPACITY_KEY = "max_consume_capacity";//单次取水最大量
    public static final String ADVS_COUNT_DOWN_KEY = "advs_count_down";//免费广告倒计时
    public static final String OPERATION_COUNT_DOWN_KEY = "operation_count_down";//界面操作倒计时
    public static final String API_PASSWORD_KEY = "api_password";


    /*广告类型*/
    public static final int AD_TYPE_IDLE = 0;
    public static final int AD_TYPE_FREE = 1;
    public static final int AD_TYPE_INIT = 2;

    /*饮水模式*/
    public static final int DRINK_MODE_WATER_SALE = 1;
    public static final int DRINK_MODE_MACHINE_SALE = 2;
    public static final int DRINK_MODE_MACHINE_RENT = 3;
    public static final int DRINK_MODE_DRINK_FREE = 4;  // 这不是一个喝水模式，但是在传记录上报的时候要用


    /*拉起激活App的操作id*/
    public static final int LOAD_APP_FOR_UPDATE = 1;
    public static final int LOAD_APP_FOR_ACTIVATE = 2;

    /*设备参数的默认值*/
    public static final int DRINK_MODE_DEFAULT = DRINK_MODE_WATER_SALE;
    public static final String RENT_DEADLINE_DEFAULT = "2099-12-31 23:59:59";
    public static final String CONTRACT_INFO_DEFAULT = "维护人员";
    public static final int DEVICE_PP_FLOW_DEFAULT = 5694;  // l
    public static final int DEVICE_GRAIN_CARBON_DEFAULT = 11355;  // l
    public static final int DEVICE_PRESS_CARBON_DEFAULT = 11355;  // l
    public static final int DEVICE_POSE_CARBON_DEFAULT = 11355;  // l
    public static final int DEVICE_RO_FLOW_DEFAULT = 9688;  // l
    public static final int DEVICE_UP_TIME_DEFAULT = 1;  // min
    public static final int DEVICE_VOLUME_DEFAULT = 50;
    public static final int DEVICE_FLUSH_INTERVAL_DEFAULT = 120;  // min
    public static final int DEVICE_FLUSH_DURATION_DEFAULT = 20;  // s
    public static final int DEVICE_HEATING_TEMP_DEFAULT = 95;  // ℃
    public static final int DEVICE_COOLING_TEMP_DEFAULT = 5;  // ℃
    public static final int DEVICE_HEATING_ALL_DAY_DEFAULT = 1;
    public static final int DEVICE_COOLING_ALL_DAY_DEFAULT = 1;
    public static final String DEVICE_HEATING_INTERVAL_DEFAULT = "00002400";
    public static final String DEVICE_COOLING_INTERVAL_DEFAULT = "00002400";
    public static final String DEVICE_NUMBER_DEFAULT = "***";
    public static final int MAX_GET_WATER_CAPACITY_DEFAULT = 300;  // ml
    public static final int MAX_CONSUME_CAPACITY_DEFAULT = 5000;  // ml
    public static final int ADVS_COUNT_DOWN_DEFAULT = 15;  // s
    public static final int OPERATION_COUNT_DOWN_DEFAULT = 30;  // s

    /*百分比*/
    public static final double PERCENT = 0.9;

    /*打印*/
    public static final String OVERFLOW ="超过流量";

    public static final int POP_RIGHT_OPERATE_GET_WATER = 1; //1是饮水
    public static final int POP_RIGHT_OPERATE_GET_CUP = 2; //2是出杯

    // 售水类型（1：热水；2：温水；3：冷水）
    public static final int GET_HOT_WATER = 1;
    public static final int GET_WARM_WATER = 2;
    public static final int GET_COOL_WATER = 3;
    public static final int GET_CUP = 4;
    public static final int GET_ALL = 255;

    // 获取设备信息的原因
    public static final int GET_INFO_FOR_UPDATE_CONFIG = 1;
    public static final int GET_INFO_FOR_ACTIVATE = 2;
    public static final int GET_INFO_FOR_UPDATE_APK = 3;

    //左边的热水温度和冷水温度是否锁定
    public static  boolean WENDU_HOT_LOCK = false;
    public static  boolean WENDU_COOL_LOCK = false;


}

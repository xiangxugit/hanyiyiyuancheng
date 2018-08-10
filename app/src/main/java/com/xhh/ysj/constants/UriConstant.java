package com.xhh.ysj.constants;

import android.os.Environment;

public class UriConstant {
      public static String IP = "api.xhhpw.com";
//    public static String IP = "39.104.72.178";
//    public static String IP = "192.168.0.200";//外网服务器
//    public static String IP="192.168.0.61";//季工的ip
//    public static String IP = "192.168.0.51";//韦英
//    public static String IP = "192.168.0.50";//阿强

//    public static String PORT = "28301";
    public static String PORT = "80";
//      public static String PORT = "";
      public final static String LOGIN = "huapage/mobi/app/login/user";
    //需要loginName loginPassword
    public final static String GETTOKEN = "api/v1/token";
    //获取最新的apk
    public final static String NEWAPK = "api/v1/apk/new";

    //水质信息采集
    public final static String WATERQUALITY = "api/v1/device/data/waterQuality";

    //水质信息采集按照list上传
    public final static String WATERQUALITYLIST = "api/v1/device/data/waterQualityList";

    //获取临时二维码
    public final static String GETTEMPQCODE = "api/v1/andorid/getSQ";

    //设备通知接口
    public final static String NOTICEQUALITY = "api/v1/device/notice/noticeQuality";

    //设备通知List接口
    public final static String NOTICEQUALITYLIST = "api/v1/device/notice/noticeQualityList";

    //售水几率
    public final static String WATERSALE = "api/v1/device/sale/water";

    //设备配置信息获取
    public final static String GET_DEVICE_CONFIG = "api/v1/device/config/";

    // 激活时获取初始视频
    public static final String GET_INIT_AD_VIDEO_LIST = "/api/v1/advs/video/getInitVideo";

    // 获取广告视频列内容
    public static final String GET_AD_VIDEO_LIST = "/api/v1/advs/config/advsPlayConfigId/";

    // 广告视频播放记录
    public static final String AD_VIDEO_RECORD_LIST = "api/v1/advs/play/advsPlayQualityList";

    //用户登录信息
    public final static String GET_USER_INFO = "/api/v1/user/login/";

    //关联设备与信鸽ID
    public final static String ADD_PUSHID = "/api/v1/device/addPushId/";

    //售水接口2.0
    public final static String GETWATER_VERSIONTWO = "/api/v1/device/addPushId/";

    //设备最后在线时间接口
    public final static String DEVICE_ENDTIME = "/api/v1/device/online/";

    //获取最新设备信息
    public final static String GET_NEW_DEVICE_INFO = "/api/v1/device/monitorConfig/";

    //开关机的问题
    public final static String CHANGE_DEVICE_STATUS = "/api/v1/device/switchStatus/";

    // 设备激活成功(get)
    public final static String ACTIVATE_DEVICE_STATUS = "/api/v1/device/changeStatus/";

    //BUG上报
    public final static String SAVE_CRASH_LOG = "/api/v1/exceptional/save";
    // 根目录
    public static final String ROOT_PATH = "/mnt/sdcard/";
    public static final String APP_ROOT_PATH = "/mnt/sdcard/xhh/";
//    Environment.getExternalStorageDirectory().getPath()

    // 日志文件夹
    public static final String LOG_DIR = "log/";

    public static final String CRASH_DIR = "log/crash/";

    // 视频文件夹
    public static final String VIDEO_DIR = "video/";
    // 初始视频文件夹
    public static final String INIT_VIDEO_DIR = "init/video/";
    // 初始视频1文件名
    public static final String VIDEO_INIT_FILE_NAME_1 = "Init1.mp4";
    // 初始视频2文件名
    public static final String VIDEO_INIT_FILE_NAME_2 = "Init2.mp4";
    // 视频消息推送暂存文件名
    public static final String VIDEO_PUSH_FILE_NAME = "PushContent.txt";
    // database文件夹
    public static final String DB_DIR = "db/";

    // 分隔符
    public static final String FILE_SEPARATE = "/";
}

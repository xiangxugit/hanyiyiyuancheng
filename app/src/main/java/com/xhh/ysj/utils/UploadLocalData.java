package com.xhh.ysj.utils;

import android.content.Context;
import android.serialport.DevUtil;
import android.text.format.Time;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.alibaba.fastjson.JSONObject;
import com.xhh.ysj.App;
import com.xhh.ysj.R;
import com.xhh.ysj.beans.AdvsPlayRecode;
import com.xhh.ysj.beans.SysDeviceNoticeAO;
import com.xhh.ysj.beans.SysDeviceWaterQualityAO;
import com.xhh.ysj.constants.Constant;
import com.xhh.ysj.manager.ThreadManager;
import com.xhh.ysj.view.activity.BaseActivity;

import okhttp3.Request;

/**
 * Created by Administrator on 2018/5/10 0010.
 */

public class UploadLocalData {
    private static final String TAG = "UploadLocalData";
    private static final int AD_VIDEO_UPLOAD_LIMIT = 100;
    private static UploadLocalData instance;

    private BaseActivity activity;
    //    private String url;
//    private List<? extends Object> contentList;
//    private String tablename;
//    private long cycle;
//    private int operateflag;
    private DbManager dbManager;
    public TimerTask task;
//    public String postdata;

//     public UploadLocalData(Context activity, String url, int operateFlag, long cycle){
//        this.activity = activity;
//        this.url = url;
//        this.operateflag = operateFlag;
//        this.cycle = cycle;
//        dbManager = new XutilsInit(activity).getDb();
//    }


    //    public static UploadLocalData getInstance(Context activity, String url, List<? extends Object> contentList, long cycle) {
    public static UploadLocalData getInstance(BaseActivity activity) {
        if (instance == null) {
            synchronized (UploadLocalData.class) {
                if (instance == null) {
                    instance = new UploadLocalData(activity);
                }
            }
        }
        return instance;
    }


    public UploadLocalData(BaseActivity activity/*, String url, int operateflag, long cycle*/) {
        this.activity = activity;
//        this.url = url;
//        this.operateflag = operateflag;
//        this.cycle = cycle;
        if (null == dbManager) {
            dbManager = new XutilsInit(App.getInstance()).getDb();
        }
//        upload();
    }

    private void uploadData(final int operateflag, String urlString) {
//        String contentJSON = JSON.toJSONString(uploaddata);
//        判断是哪一种的东西
        String url = urlString;
        List<? extends Object> contentList = new ArrayList<>();
        String postdata = "";
        try {
            switch (operateflag) {
                case Constant.TIME_OPERATE_UPDATEWATER:
                    //水质上报
                    contentList = dbManager.findAll(SysDeviceWaterQualityAO.class);
                    if (null != contentList) {
                        LogUtils.d(TAG, "uploadData: 水质记录个数：" + contentList.size());
                    }
                    break;
                case Constant.TIME_OPETATE_WARNING:
                    //预警上报
                    contentList = dbManager.findAll(SysDeviceNoticeAO.class);
                    if (null != contentList) {
                        LogUtils.d(TAG, "uploadData: 预警个数：" + contentList.size());
                    }
                    break;
                case Constant.TIME_OPETATE_VIDEO:
                    // 广告视频播放记录上报
                    contentList = dbManager.findAll(AdvsPlayRecode.class);
                    if (null != contentList) {
                        LogUtils.d(TAG, "uploadData: 广告视频记录个数：" + contentList.size());
                    }
//                  List<SysDeviceWaterQualityAO> listQualityAO = dbManager.findAll(AdvsPlayRecode.class);
                    break;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        LogUtils.e(TAG, "uploadData: 上报类型（1、水质；3、预警；4、广告）：" + operateflag);
        if (null == contentList || 0 == contentList.size()) {
            LogUtils.d(TAG, "uploadData: 上报信息为空，operationFlag = " + operateflag);
            return;
        }
        // 分页上传（500一分，有多的按余数上传）
        int size = contentList.size();
        int times = size % AD_VIDEO_UPLOAD_LIMIT == 0 ? size / AD_VIDEO_UPLOAD_LIMIT : size / AD_VIDEO_UPLOAD_LIMIT + 1;
        LogUtils.i(TAG, "uploadData: 类型"+ operateflag + "数据所需批次：" + times);
        if (times > ThreadManager.LONG_POOL_SIZE) {
            size = contentList.subList(0, ThreadManager.LONG_POOL_SIZE * AD_VIDEO_UPLOAD_LIMIT).size();
            times = ThreadManager.LONG_POOL_SIZE;
            LogUtils.i(TAG, "uploadData: 批次过多，调整为：" + times);
        }
        int remainder = size % AD_VIDEO_UPLOAD_LIMIT;  // 余数
        for (int i = 0; i < times; i++) {
            final int index = i;
            final List<?> finalContentList = contentList.subList(index * AD_VIDEO_UPLOAD_LIMIT,
                    index == times - 1 && remainder != 0 ?  // 最后一批，余数不为0时，传余数个，余数为0时，传Limit个
                            index * AD_VIDEO_UPLOAD_LIMIT + remainder: (index + 1) * AD_VIDEO_UPLOAD_LIMIT);
            if (0 == finalContentList.size()) {
                return;
            }
            postdata = JSON.toJSONString(finalContentList);
            LogUtils.i(TAG, "uploadData: 第" + (index + 1) + "批次类型" + operateflag + "数据：" + postdata);
            OkHttpUtils.getInstance().postAsyn(url, new OkHttpUtils.StringCallback() {
                @Override
                public void onFailure(int errCode, Request request, IOException e) {
                    LogUtils.e(TAG, "onFailure: " + "第" + (index + 1) + "批次" +
                            "类型" + operateflag + "同步失败, errCode = " + errCode + ", request = " + request.toString());
                }

                @Override
                public void onResponse(String response) {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (null == jsonObject) {
                        LogUtils.i(TAG, "onResponse: " + "第" + (index + 1) + "批次" +
                                "类型" + operateflag + "同步异常，不删除数据。异常原因：response为空。");
                        return;
                    }
                    if (!jsonObject.containsKey("code")) {
                        LogUtils.i(TAG, "onResponse: " + "第" + (index + 1) + "批次" +
                                "类型" + operateflag + "同步异常，不删除数据。异常原因：response 中无 “code字段”。" +
                                "response = " + response);
                        return;
                    }
                    if (0 != jsonObject.getInteger("code")) {
                        LogUtils.i(TAG, "onResponse: " + "第" + (index + 1) + "批次" +
                                "类型" + operateflag + "同步异常，不删除数据。异常原因：response 中 code != 0。" +
                                "response = " + response);
                        return;
                    }
                    LogUtils.i(TAG, "onResponse: " + "第" + (index + 1) + "批次" +
                            "类型" + operateflag + "同步成功, response = " + response);
                    deleteLocalUploadData(finalContentList);
                }
            }, postdata);
        }
    }


    public void upload(final String url, final int operateFlag, long cycle) {
        LogUtils.d(TAG, "upload: url = " + url + ", flag = " + operateFlag + ", cycle = " + cycle);
        if (0 == cycle) {
            LogUtils.e(TAG, "upload: error！！！upload cycle is 0！");
            return;
        }
        Time sCodeTime = new Time();
        sCodeTime.setToNow();
        Date updateTime = TimeRun.tasktime(sCodeTime.hour, sCodeTime.minute, sCodeTime.second);
        task = new TimerTask() {
            @Override
            public void run() {
                uploadData(operateFlag, url);
            }
        };
        Timer timer = new Timer(true);
        timer.schedule(task, updateTime, cycle);
    }


    private void deleteLocalUploadData(List<? extends Object> content) {
        try {
            if (null == content) {
                return;
            }
            dbManager.delete(content);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


}

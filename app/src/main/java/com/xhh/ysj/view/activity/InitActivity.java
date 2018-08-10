package com.xhh.ysj.view.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.xhh.ysj.App;
import com.xhh.ysj.R;
import com.xhh.ysj.beans.AdvsVideo;
import com.xhh.ysj.beans.DispenserCache;
import com.xhh.ysj.beans.PushEntity;
import com.xhh.ysj.broadcast.MessageReceiver;
import com.xhh.ysj.constants.Constant;
import com.xhh.ysj.constants.UriConstant;
import com.xhh.ysj.utils.BaseSharedPreferences;
import com.xhh.ysj.utils.Create2QR2;
import com.xhh.ysj.utils.FileUtil;
import com.xhh.ysj.utils.LogUtils;
import com.xhh.ysj.utils.NetWorkCanUseOrNot;
import com.xhh.ysj.utils.OkHttpUtils;
import com.xhh.ysj.utils.RestUtils;
import com.xhh.ysj.utils.XutilsInit;

import okhttp3.Request;

import static com.xhh.ysj.utils.PermissionUtils.permissions;

public class InitActivity extends BaseActivity {
    private static final String TAG = "InitActivity";
    public static final int STEP_UNACTIVATE = 1;
    public static final int STEP_ACTIVATED = 2;

    private LinearLayout firstStep;
    private LinearLayout secondStep;
    private ImageView qcode;//扫描二维码激活系统
    private TextView tvNoNetwork;
    private MessageReceiver messageReceiver;//信鸽Receiver
    private int getDeviceIdCount;
    // 权限集合
    List<String> permissionList = new ArrayList<>();
    DynamicReceiver myReceiver;
    IntentFilter connectFilter;
    private InitActivityNetLister connectReceiver;
    private ProgressDialog progressDialog;//进度条
    private LinearLayout networkerror;
    private TextView onceagain;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 记录屏幕分辨率
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        Toast.makeText(this, "分辨率：" + dm.widthPixels + " * " + dm.heightPixels, Toast.LENGTH_SHORT).show();
        LogUtils.d(TAG, "屏幕分辨率：" + dm.widthPixels + " * " + dm.heightPixels);
		// 初始工作
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        if (null != handler) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if (null != myReceiver) {
            unregisterReceiver(myReceiver);
        }
        if (null != dialog) {
            dialog.dismiss();
            dialog = null;
        }
        super.onDestroy();
        if(null!=connectFilter){
            unregisterReceiver(connectReceiver);
        }
    }

    public void initView() {

    }

    private void initData() {
//        int a = 1/0;
        mContext = InitActivity.this;
        mActivity = InitActivity.this;
        adList = new ArrayList<>();
        getDeviceIdCount = 0;
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constant.MSG_WAITING_THEN_DOWNLOAD:
                        downloadInitVideo(adList);
                        break;
                    case Constant.MSG_ALL_DOWN_COMPLETE:
                        noticeServerActivateDone();
                        refreshAllAdVideoData();
                        moveToMainActivity();
                        break;
                    case Constant.MSG_XG_PUSH_ID_TIMEOUT:
//                        showToast(getString(R.string.xg_push_id_timeout_toast));
//                        launchLoadApp(Constant.LOAD_APP_FOR_ACTIVATE);
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                        break;
                }
            }
        };
        initPermission();
//        initPush();

        //读取设备id文件
        File file = new File(UriConstant.APP_ROOT_PATH + DEVICE_ID_TXT_NAME);
        if (file.exists()) {
            String id = FileUtil.readFile(file.getPath());
            int deviceId = Integer.parseInt(id);
            LogUtils.d(TAG, "initData: 文件中的id = " + id + ", 转成int的deviceId = " + deviceId);
            if (0 != deviceId) {
                BaseSharedPreferences.setInt(mContext, Constant.DEVICE_ID_KEY, deviceId);
                LogUtils.e(TAG, "已有deviceId，直接激活设备！");
                if (handler.hasMessages(Constant.MSG_XG_PUSH_ID_TIMEOUT)) {
                    handler.removeMessages(Constant.MSG_XG_PUSH_ID_TIMEOUT);
                }
                activateDevice(mActivity, deviceId, Constant.GET_INFO_FOR_UPDATE_APK);
//                moveToMainActivity();
                setContentView(R.layout.loading);
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("加载中...");
                progressDialog.setMessage("加载中...");  //设置信息
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  //设置信息格式
                return;
            }
        } else  if (BaseSharedPreferences.getInt(mContext, Constant.DEVICE_ID_KEY) != 0) {
                LogUtils.e(TAG, "已有deviceId，直接激活设备！");
                if (handler.hasMessages(Constant.MSG_XG_PUSH_ID_TIMEOUT)) {
                    handler.removeMessages(Constant.MSG_XG_PUSH_ID_TIMEOUT);
                }
                activateDevice(mActivity, deviceId, Constant.GET_INFO_FOR_UPDATE_APK);
//                moveToMainActivity();
            setContentView(R.layout.loading);
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("加载中...");
            progressDialog.setMessage("加载中...");  //设置信息
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  //设置信息格式

                return;
            }else{

            setContentView(R.layout.activity_init);
            networkerror = findViewById(R.id.networkerror);
            firstStep = findViewById(R.id.init_step_1);
            secondStep = findViewById(R.id.init_step_2);
            qcode = findViewById(R.id.qcode);
            tvNoNetwork = findViewById(R.id.init_no_network_tv);
            onceagain = findViewById(R.id.oneceagain);
            onceagain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });

            connectFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            connectReceiver = new InitActivityNetLister();
            this.registerReceiver(connectReceiver, connectFilter);
//            Toast.makeText(InitActivity.this, "最后的else", Toast.LENGTH_SHORT).show();
            initPush();
        }


        // 查看是否是由于更新Apk失败时的拉起
        Intent intent = getIntent();
        if (null != intent && intent.hasExtra(Constant.KEY_LAUNCH_DISPENSER_APP_ERRCODE)) {
            int updateErrcode = intent.getIntExtra(Constant.KEY_LAUNCH_DISPENSER_APP_ERRCODE, 0);
            // TODO: 2018/7/23 0023 上报更新失败
        }
    }

    /**
     * 初始化权限
     */
    private void initPermission() {
        int i;
        for (i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permissions[i]);
            }
        }
        if (permissionList.isEmpty()) {//未授予的权限为空，表示都授予了
//            Toast.makeText(this, "已经授权", Toast.LENGTH_LONG).show();
        } else {//请求权限方法
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(this, permissions, i);
        }
    }

    /**
     * 初始化信鸽
     */
    private void initPush() {
        // 开启logcat输出，方便debug，发布时请关闭
        // XGPushConfig.enableDebug(this, true);
        // 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(), XGIOperateCallback)带callback版本
        // 具体可参考详细的开发指南
        // 传递的参数为ApplicationContext
        LogUtils.d(TAG, "initPush: 开始申请信鸽ID");
        XGPushConfig.enableDebug(this, true);
        handler.sendEmptyMessageDelayed(Constant.MSG_XG_PUSH_ID_TIMEOUT, Constant.XG_PUSH_ID_TIMEOUT_TIME);
        XGPushManager.registerPush(this, new XGIOperateCallback() {
            @Override
            public void onSuccess(final Object data, int flag) {
                //Android在清理掉本地缓存或者卸载重装的时候重新注册会下发新的token
                LogUtils.i(TAG, "TPush: " + "注册成功，设备token为：" + data);
                if (handler.hasMessages(Constant.MSG_XG_PUSH_ID_TIMEOUT)) {
                    handler.removeMessages(Constant.MSG_XG_PUSH_ID_TIMEOUT);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bit = Create2QR2.createBitmap(data.toString());
                        qcode.setImageBitmap(bit);
                    }
                });
                // 2018.07.20 讨论结果：不用同步pushId和deviceId
                /*String url = RestUtils.getUrl(UriConstant.ADD_PUSHID + deviceId + "/" + data);
                OkHttpUtils.getAsyn(url, new OkHttpUtils.StringCallback() {
                    @Override
                    public void onFailure(int errCode, Request request, IOException e) {
                        LogUtils.d(TAG, "onFailure: 关联设备与信鸽ID失败！errCode = " + errCode +
                                ", request = " + request.toString());
                        showToast(mContext.getString(R.string.xg_push_id_device_id_sync_fail_toast));
                        launchLoadApp(Constant.LOAD_APP_FOR_ACTIVATE);
                    }

                    @Override
                    public void onResponse(String response) {
                        LogUtils.d(TAG, "onResponse: 关联设备与信鸽ID成功！ response = " + response);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bit = Create2QR2.createBitmap(data.toString());
                                qcode.setImageBitmap(bit);
                            }
                        });
                    }
                });*/
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                LogUtils.i(TAG, "TPush: " + "注册失败，errCode = " + errCode + ", msg = " + msg);
                showToast(getString(R.string.xg_push_id_fail_toast));
//                launchLoadApp(Constant.LOAD_APP_FOR_ACTIVATE);
                if(NetWorkCanUseOrNot.isMobile(InitActivity.this)&&NetWorkCanUseOrNot.isWifi(InitActivity.this)&&NetWorkCanUseOrNot.isNetworkOnline()){
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }else{
                    showToast("网络错误");
                    networkerror.setVisibility(View.VISIBLE);
                }

            }

        });

        myReceiver = new DynamicReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MessageReceiver.PUSHACTION);
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(myReceiver, filter);
    }

    /**
     * 信鸽消息透传
     */
    public class DynamicReceiver extends XGPushBaseReceiver {
        @Override
        public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {

        }

        @Override
        public void onUnregisterResult(Context context, int i) {

        }

        @Override
        public void onSetTagResult(Context context, int i, String s) {

        }

        @Override
        public void onDeleteTagResult(Context context, int i, String s) {

        }

        @Override
        public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {

            LogUtils.d(TAG, "onTextMessage: receive new push");
            String pushString = xgPushTextMessage.getContent();
            LogUtils.e(TAG, "onTextMessage: 收到消息: " + pushString);
            PushEntity pushEntity = JSONObject.parseObject(pushString, PushEntity.class);
            if (null == pushEntity) {
                LogUtils.d(TAG, "onTextMessage: 推送为空！");
                return;
            }
            // 获取内容
            int operationType = pushEntity.getOperationType();
            String content = pushEntity.getOperationContent();
            if (0 == operationType/* || TextUtils.isEmpty(content)*/) {
                LogUtils.d(TAG, "onTextMessage: 推送类型或内容为空！");
                LogUtils.d(TAG, "onTextMessage: pushEntity = " + pushEntity.toString());
                return;
            }
            LogUtils.d(TAG, "onTextMessage: 操作类型：" + operationType);
            switch (operationType) {
                case Constant.PUSH_OPERATION_TYPE_UPDATE_APK:
                    LogUtils.d(TAG, "onTextMessage: 操作类型：更新APK");
                    launchLoadApp(Constant.LOAD_APP_FOR_UPDATE);
                    break;
                case Constant.PUSH_OPERATION_TYPE_UPDATE_ID:
                    LogUtils.d(TAG, "onTextMessage: 操作类型：信鸽ID");
                    JSONObject jsonObject = JSON.parseObject(pushString);
                    int deviceId = jsonObject.getInteger("deviceId");
                    LogUtils.d(TAG, "onTextMessage: Api pwd = " + content);
                    BaseSharedPreferences.setString(App.getInstance(), Constant.API_PASSWORD_KEY, content);
                    if (deviceId == 0 || TextUtils.isEmpty(content)) {
                        getDeviceIdCount++;
                        if (getDeviceIdCount < 3) {
                            showToast(getString(R.string.get_device_config_no_id_toast1));
                        } else {
                            showToast(getString(R.string.get_device_config_no_id_toast2));
                        }
                        return;
                    }
                    showStep(STEP_ACTIVATED);
                    BaseSharedPreferences.setInt(mContext, Constant.DEVICE_ID_KEY, deviceId);
                    // api pwd 保存到文件
                    FileUtil.deleteFile(UriConstant.APP_ROOT_PATH + API_PWD_TXT_NAME);
                    String path = FileUtil.createMkdirsAndFiles(UriConstant.APP_ROOT_PATH, API_PWD_TXT_NAME);
                    FileUtil.write(path, content, true);
                    // 将设备id保存至文件，并获取设备信息激活
                    activateDevice(mActivity, deviceId, Constant.GET_INFO_FOR_ACTIVATE);
                    break;
            }
        }

        @Override
        public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {

        }

        @Override
        public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {

        }
    }

    private void noticeServerActivateDone() {
        LogUtils.d(TAG, "noticeServerActivateDone: 视频下载完毕，即将通知服务器激活完成");
        OkHttpUtils.getAsyn(RestUtils.getUrl(UriConstant.ACTIVATE_DEVICE_STATUS
                + BaseSharedPreferences.getInt(mContext, Constant.DEVICE_ID_KEY)), new OkHttpUtils.StringCallback() {
            @Override
            public void onFailure(int errCode, Request request, IOException e) {
                LogUtils.e(TAG, "onFailure: 获取DeviceNumber失败！");
            }

            @Override
            public void onResponse(String response) {
                LogUtils.e(TAG, "onSuccess: 获取DeviceNumber成功！DeviceNumber = " + response);
                String deviceNum = "";

                if(null!=response){
                    JSONObject deviceNumobj = JSONObject.parseObject(response);
                     deviceNum = deviceNumobj.getString("data");
                }
//                BaseSharedPreferences.setString(mContext,);
                if(null!=deviceNum){
                BaseSharedPreferences.setString(mContext, Constant.DEVICE_NUMBER_KEY,
                        TextUtils.isEmpty(deviceNum)
                                || 7 > deviceNum.length() || 15 < deviceNum.length()
                                ? Constant.DEVICE_NUMBER_DEFAULT : deviceNum);
            }
            }
        });
    }

    private void refreshAllAdVideoData() {
        LogUtils.d(TAG, "refreshAllAdVideoData: 开始更新数据库及缓存list");
        DbManager dbManager = new XutilsInit(mContext).getDb();
        // 同步数据到数据库
        try {
            dbManager.delete(AdvsVideo.class);
            dbManager.saveOrUpdate(adList);
        } catch (DbException e) {
            e.printStackTrace();
        }
        // 同步数据到各个list
        DispenserCache.initAdVideoList.clear();
        DispenserCache.initAdVideoList.addAll(adList);
    }

    /**
     * 跳转到主页
     */
    public void moveToMainActivity() {
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                //判断是否勾选禁止后不再询问
                boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                if (showRequestPermission) {
                    showToast("权限未申请");
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 控制流程 控件的显示/隐藏
     *
     * @param step
     */
    public void showStep(int step) {
        if (step == STEP_UNACTIVATE) {
            LogUtils.d(TAG, "showStep: STEP_UNACTIVATE");
            firstStep.setVisibility(View.VISIBLE);
            secondStep.setVisibility(View.GONE);
        } else if (step == STEP_ACTIVATED) {
            LogUtils.d(TAG, "showStep: STEP_ACTIVATED");
            firstStep.setVisibility(View.GONE);
            secondStep.setVisibility(View.VISIBLE);
        }
    }


    public class InitActivityNetLister extends BroadcastReceiver {
        private static final String TAG = "ConnectionChangeReceive";

        private BaseActivity activity;

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                switch (networkInfo.getType()) {
                    case ConnectivityManager.TYPE_MOBILE:
                        //网络断网界面隐藏
                        tvNoNetwork.setVisibility(View.GONE);
                        Toast.makeText(context, "正在使用2G/3G/4G网络", Toast.LENGTH_SHORT).show();
                        break;
                    case ConnectivityManager.TYPE_WIFI:
                        //w网络断网文字隐藏
                        tvNoNetwork.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            } else {
                //显示网络问题
                tvNoNetwork.setVisibility(View.VISIBLE);
            }
        }
    }
}

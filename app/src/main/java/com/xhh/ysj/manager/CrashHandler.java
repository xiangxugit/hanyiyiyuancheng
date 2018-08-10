package com.xhh.ysj.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.xhh.ysj.beans.Exceptiona;
import com.xhh.ysj.constants.Constant;
import com.xhh.ysj.constants.UriConstant;
import com.xhh.ysj.utils.FileUtil;
import com.xhh.ysj.utils.LogUtils;
import com.xhh.ysj.utils.OkHttpUtils;
import com.xhh.ysj.utils.RestUtils;
import com.xhh.ysj.utils.TimeUtils;
import com.xhh.ysj.view.activity.InitActivity;

import org.json.JSONObject;

import okhttp3.Request;

/**
 * 全局捕获异常：程序发生Uncaught异常的时候,有该类来接管程序,并记录错误日志
 */
@SuppressLint("SimpleDateFormat")
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static String TAG = "CrashHandler";
    public static final int LIMIT_CRASH_LOG_COUNT = 5;
    private static String DIR_PATH = UriConstant.APP_ROOT_PATH + UriConstant.CRASH_DIR;

    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private static CrashHandler instance = new CrashHandler();
    private Context mContext;
    private boolean isRestart;

    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<>();

    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");

    /** 保证只有一个CrashHandler实例 */
    private CrashHandler() {
    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        return instance;
    }

    /**
     * 初始化
     * @param context
     */
    public void init(Context context, boolean isRestart) {
        this.mContext = context;
        this.isRestart = isRestart;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && null != mDefaultHandler) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            SystemClock.sleep(2000);
            // 重启程序
            if (isRestart) {
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(mContext, InitActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("crash", true);
                PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, restartIntent); // 1秒钟后重启应用
            }
            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     * @param ex
     * @return true:如果处理了该异常信息; 否则返回false.
     */
    private boolean handleException(Throwable ex) {
        try {
            if (null == ex)
                return false;
            // 使用Toast来显示异常信息
            ThreadManager.getInstance().createShortPool().execute(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(mContext, "很抱歉，程序出现异常，即将关闭。",
                            Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            });
            // 收集设备参数信息
            collectDeviceInfo(mContext);
            // 保存日志文件
            saveCrashInfoFile(ex);
            FileUtil.limitAppLogCount(DIR_PATH, LIMIT_CRASH_LOG_COUNT);
//            deleteOverflow();
            SystemClock.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isRestart;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            if (ctx == null)
                return ;
            PackageManager pm = ctx.getPackageManager();
            if (null != pm) {
                PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
                if (null != pi) {
                    String versionName = null == pi.versionName ? "null" : pi.versionName;
                    String versionCode = pi.versionCode + "";
                    String packageName = null == pi.versionName ? "null" : pi.packageName;
                    infos.put("versionName", versionName);
                    infos.put("versionCode", versionCode);
                    infos.put("packageName",packageName);
                }
            }
            // 获取手机型号，系统版本，以及SDK版本
            infos.put("手机型号:", Build.MODEL);
            infos.put("系统版本", ""+ Build.VERSION.SDK_INT);
            infos.put("Android版本", Build.VERSION.RELEASE);
            // 收集crash info
            Field[] fields = Build.class.getDeclaredFields();
            if (null != fields && fields.length > 0) {
                for (Field field : fields) {
                    if (null != field) {
                        field.setAccessible(true);
                        infos.put(field.getName(), field.get(null).toString());
                        LogUtils.d(TAG, field.getName() + " : " + field.get(null));
                    }
                }
            }
        } catch (NameNotFoundException e) {
            LogUtils.e(TAG, "an error occured when collect package info: "+ e);
        } catch (Exception e) {
            LogUtils.e(TAG, "an error occured when collect crash info:" + e);
        }
    }

    /**
     * 保存错误信息到文件中
     * @param ex
     * @return 返回文件名称,便于将文件传送到服务器
     * @throws Exception
     */
    private String saveCrashInfoFile(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        try {
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");
            String date = sDateFormat.format(new Date());
            sb.append("\r\n" + date + "\n");
            for (Map.Entry<String, String> entry : infos.entrySet()) {
                sb.append(entry.getKey() + "=" + entry.getValue() + "\n");
            }
            sb.append("\n");

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (null != cause) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.flush();
            printWriter.close();
            String result = writer.toString();
            sb.append("Exception:\n");
            sb.append(result);
            return writeFile(sb.toString());
        } catch (Exception e) {
            LogUtils.e(TAG, "an error occured while writing file..." + e);
            sb.append("an error occured while writing file...\r\n");
        }
        return null;
    }

    private String writeFile(String sb) throws Exception {
        // 创建日志文件名称
        String time = formatter.format(new Date());
        String fileName = "crash-" + time + ".log";

        // 创建日志文件夹
        File folder = new File(DIR_PATH);
        if (!folder.exists())
            folder.mkdirs();

        // 创建日志文件
        File file = new File(DIR_PATH + fileName);
        if (!file.exists())
            FileUtil.createMkdirsAndFiles(DIR_PATH, fileName);
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(sb);
        bufferedWriter.flush();
        bufferedWriter.close();
//        sendCrashLogToServer(folder, file);
        sendCrashLogStringToServer(sb);
        return fileName;
    }

    /**
     * 发送日志文件到服务器
     * @param folder 文件路径
     * @param file 文件
     */
    public void sendCrashLogToServer(File folder, File file) {
    }

    /**
     * 发送日志文件到服务器
     * @param bugString 错误日志
     */
    public void sendCrashLogStringToServer(String bugString){
        String postdata = "";
        Exceptiona exceptiona = new Exceptiona();
        exceptiona.setExceptionalContent(bugString);
        exceptiona.setExceptionalStatus(0);//默认是没有处理的
        exceptiona.setExceptionalSubject("android 参数错误");
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

}

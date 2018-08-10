package com.xhh.ysj;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.danikula.videocache.HttpProxyCacheServer;
import com.marswin89.marsdaemon.DaemonApplication;
import com.marswin89.marsdaemon.DaemonConfigurations;

import org.xutils.x;

import com.xhh.ysj.manager.CrashHandler;
import com.xhh.ysj.processpreserve.Receiver1;
import com.xhh.ysj.processpreserve.Receiver2;
import com.xhh.ysj.processpreserve.Service1;
import com.xhh.ysj.processpreserve.Service2;
import com.xhh.ysj.utils.LogUtils;

/**
 * Created by Administrator on 2018/4/26 0026.
 */

public class App extends DaemonApplication {

    private static final String TAG = "App";

    private static App instance;
    private static boolean APP_DBG = false; // 是否是debug模式
    private HttpProxyCacheServer proxy;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e(TAG,"进入app的时候执行");
        instance = this;
        APP_DBG = isDebug();
        // 初始化崩溃日志收集器
        CrashHandler.getInstance().init(this, false);
        //初始化xutils
        x.Ext.init(this);
        x.Ext.setDebug(false);
    }

//    public static  DbManager getdb(){
//        return db;
//    }

    public static Context getInstance() {
        return instance;
    }


    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }

    public static boolean isAppDbg() {
        return APP_DBG;
    }

    private boolean isDebug(){
        ApplicationInfo applicationInfo = this.getApplicationInfo();
        return null != applicationInfo &&
                (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    @Override
    protected DaemonConfigurations getDaemonConfigurations() {
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                "com.xhh.ysj.Processpreserving::process1",
                Service1.class.getCanonicalName(),
                Receiver1.class.getCanonicalName());

        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                "com.xhh.ysj.Processpreserving::process2",
                Service2.class.getCanonicalName(),
                Receiver2.class.getCanonicalName());

        DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
        //return new DaemonConfigurations(configuration1, configuration2);//listener can be null
        return new DaemonConfigurations(configuration1, configuration2, listener);
    }



    class MyDaemonListener implements DaemonConfigurations.DaemonListener{
        @Override
        public void onPersistentStart(Context context) {
        }

        @Override
        public void onDaemonAssistantStart(Context context) {
        }

        @Override
        public void onWatchDaemonDaed() {
        }
    }
}


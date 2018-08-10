package com.xhh.ysj.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import com.xhh.ysj.constants.Constant;
import com.xhh.ysj.constants.ErrCodeConstant;
import com.xhh.ysj.interfaces.DownloadCallback;
import com.xhh.ysj.utils.CommonUtil;
import com.xhh.ysj.utils.FileUtil;
import com.xhh.ysj.utils.LogUtils;
import com.xhh.ysj.utils.SPDownloadUtil;

public class DownloadManager {

    private static final String TAG = "DownloadManager";

    private static DownloadManager instance;
    private final DownloadSubscriber downloadSubscriber;
    private Context mContext;
    private int downloadId;
    private String baseUrl;
    private String downloadUrl;
    private String localDir;
    private String fileName;
    private DownloadCallback downloadCallback;
    private HttpProgressOnNextListener listener;

    public static DownloadManager getInstance() {
        if (instance == null) {
            synchronized (DownloadManager.class) {
                if (instance == null) {
                    instance = new DownloadManager();
                }
            }
        }
        return instance;
    }

    private DownloadManager() {
        listener = new HttpProgressOnNextListener() {
            @Override
            public void onStart() {
                LogUtils.d(TAG, "onStart: dl_info: listener里面的");

            }

            @Override
            public void onNext(Object o) {
                LogUtils.d(TAG, "onNext: dl_info: listener里面的");
            }

            @Override
            public void onComplete() {
                LogUtils.d(TAG, "onComplete: dl_info: listener里面的");

            }

            @Override
            public void onError(Throwable e) {
                LogUtils.d(TAG, "onError: dl_info: listener里面的");

            }

            @Override
            public void updateProgress(long readLength, long countLength) {
                LogUtils.d(TAG, "onError: dl_info: listener里面的, readLength = " + readLength
                        + ", countLength = " + countLength);
            }
        };
        downloadSubscriber = new DownloadSubscriber(listener);
    }

    public void setDownloadCallback(DownloadCallback downloadCallback) {
        this.downloadCallback = downloadCallback;
    }

    public void startDown(Context mContext, int downloadId, String baseUrl, String downloadUrl, String localDir) {
        this.mContext = mContext;
        this.downloadId = downloadId;
        this.baseUrl = baseUrl;
        this.downloadUrl = downloadUrl;
        this.localDir = localDir;
        this.fileName = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);
        download();
    }

    /**
     * 备注：必须在startDown之后调用，否则不起作用
     */
    public void stopDown() {
        LogUtils.d(TAG, "stopDown: dl_info: 即将停止下载");
        listener.onStop();
        downloadSubscriber.unSubscribe();
//        downloadCallback.onError(Constant.DOWN_ERROR_MSG_WRONG_NO_MISTAKE);
    }

    public void pause() {
        LogUtils.d(TAG, "pause: dl_info: 即将暂停下载");
        listener.onPause();
        downloadSubscriber.unSubscribe();
    }

    /**
     * 备注：不建议使用此方法，有可能下载的参数获取失败，或者重复下载，建议尽量使用startDown代替此方法
     */
    @Deprecated
    public void continueDown() {
        if (TextUtils.isEmpty(baseUrl) || TextUtils.isEmpty(downloadUrl)
                || TextUtils.isEmpty(localDir) || TextUtils.isEmpty(fileName)) {
            LogUtils.d(TAG, "continueDown: dl_info: 下载路径或保存路径为空！");
            return;
        }
        download();
    }

    private void download() {
        LogUtils.d(TAG, "download: dl_info: 即将开始下载");
        LogUtils.d(TAG, "download: dl_info: baseUrl --" + baseUrl);
        LogUtils.d(TAG, "download: dl_info: url --" + downloadUrl);
        LogUtils.d(TAG, "download: dl_info: local_path --" + localDir + fileName);

        if (!isNetworkAvailable(mContext)) {
            downloadCallback.onError(Constant.DOWN_ERROR_MSG_WRONG_NETWORK_UNAVAIL);
            return;
        }
        RetrofitHttp.getInstance(baseUrl).isUrlAvailable(baseUrl, 2000, new RetrofitHttp.UrlCheckCallback() {
            @Override
            public void onSuccess() {
                final File file = new File(localDir + fileName);
                long range = 0;
                int progress = 0;
                if (file.exists()) {
                    range = SPDownloadUtil.getInstance().get(downloadUrl, 0);
                    LogUtils.d(TAG, "download: dl_info: range = " + range);
                    if (0 == file.length()) {
                        LogUtils.d(TAG, "download: 文件长度为0，重建文件并下载");
                        FileUtil.deleteFile(localDir + fileName);
                        download();
                        return;
                    }
                    progress = (int) (range * 100 / file.length());
                    if (range == file.length()) {
                        LogUtils.d(TAG, "download: 文件长度为已有长度，直接完成");
                        downloadCallback.onComplete(localDir + fileName);
                        return;
                    }
                }
/*        final RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notify_download);
        remoteViews.setProgressBar(R.id.pb_progress, 100, progress, false);
        remoteViews.setTextViewText(R.id.tv_progress, "已下载" + progress + "%");

        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContent(remoteViews)
                        .setTicker("正在下载")
                        .setSmallIcon(R.mipmap.ic_launcher);

        mNotification = builder.build();

        mNotifyManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyManager.notify(downloadId, mNotification);*/
                RetrofitHttp.getInstance(baseUrl).downloadFile(range,
                        downloadUrl, localDir, fileName, downloadCallback, downloadSubscriber);
            }

            @Override
            public void onFailure() {
                LogUtils.e(TAG, "isUrlAvailable: dl_info: url有误！连接打不开! url = " + baseUrl);
                downloadCallback.onError(Constant.DOWN_ERROR_MSG_WRONG_BASE_URL);
            }
        });
    }

    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            LogUtils.d(TAG, "deleteFile: dl_info: 是文件");
            if (file.delete()) {
                LogUtils.d(TAG, "deleteFile: dl_info: 删除单个文件" + fileName + "成功！");
                return true;
            } else {
                LogUtils.d(TAG, "deleteFile: dl_info: 删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            LogUtils.d(TAG, "deleteFile: dl_info: 删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }
        return true;
    }
}


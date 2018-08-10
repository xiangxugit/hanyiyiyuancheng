package com.xhh.ysj.manager;

import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import com.xhh.ysj.interfaces.ApiService;
import com.xhh.ysj.interfaces.DownloadCallback;
import com.xhh.ysj.utils.LogUtils;
import com.xhh.ysj.utils.SPDownloadUtil;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHttp {

    private static final int DEFAULT_TIMEOUT = 10;
    private static final String TAG = "RetrofitClient";

    private ApiService apiService;

    private OkHttpClient okHttpClient;

    public static String baseUrl;
    private static RetrofitHttp sIsntance;

    private UrlCheckCallback callback;

    public static RetrofitHttp getInstance(String baseUrl) {
        if (sIsntance == null) {
            synchronized (RetrofitHttp.class) {
                if (sIsntance == null) {
                    sIsntance = new RetrofitHttp(baseUrl);
                }
            }
        }
        return sIsntance;
    }

    private RetrofitHttp(String baseUrl) {
        this.baseUrl = baseUrl;
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public void isUrlAvailable(final String urlString, final int timeout, final UrlCheckCallback callback){
        this.callback = callback;
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                long lo = System.currentTimeMillis();
                URL url;
                try {
                    url = new URL(urlString);
                    URLConnection co =  url.openConnection();
                    co.setConnectTimeout(timeout);
                    co.connect();
                    callback.onSuccess();
                } catch (Exception e1) {
                    url = null;
                    callback.onFailure();
                }
//                System.out.println(System.currentTimeMillis()-lo);
            }
        });
    }

    /**
     *
     * @param range
     * @param url 不含baseUrl
     * @param fileDir 本地路径文件夹
     * @param fileName 本地路径文件名
     * @param downloadCallback
     */
    public void downloadFile(final long range, final String url,
                             final String fileDir, final String fileName,
                             final DownloadCallback downloadCallback,
                             DownloadSubscriber downloadSubscriber) {
        //断点续传时请求的总长度
        File file = new File(fileDir, fileName);
        String totalLength = "-";
        if (file.exists()) {
            totalLength += file.length();
        }

        apiService.executeDownload("bytes=" + Long.toString(range) + totalLength, url)
                .subscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, Object>() {
                    @Override
                    public Object apply(ResponseBody responseBody) throws Exception {
                        LogUtils.d(TAG, "apply: -------------");
                        writeCache(responseBody, range, fileDir, fileName, downloadCallback, url);
                        return null;
                    }
                })
                .subscribe(downloadSubscriber);
    }

    private void writeCache(ResponseBody responseBody, long range, String fileDir, String fileName, DownloadCallback downloadCallback, String url) {
        LogUtils.d(TAG, "writeCache: range = " + range + ", filePath = " + fileDir + fileName + ", url = "+ url);
        RandomAccessFile randomAccessFile = null;
        InputStream inputStream = null;
        long total = range;
        long responseLength = 0;
        try {
            byte[] buf = new byte[2048];
            int len = 0;
            responseLength = responseBody.contentLength();
            inputStream = responseBody.byteStream();
            String filePath = fileDir;
            File file = new File(filePath, fileName);
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            randomAccessFile = new RandomAccessFile(file, "rwd");
            if (range == 0) {
                randomAccessFile.setLength(responseLength);
            }
            randomAccessFile.seek(range);

            int progress = 0;
            int lastProgress = 0;

            while ((len = inputStream.read(buf)) != -1) {
                randomAccessFile.write(buf, 0, len);
                total += len;
                lastProgress = progress;
                progress = (int) (total * 100 / randomAccessFile.length());
                if (progress > 0 && progress != lastProgress) {
                    downloadCallback.onProgress(progress);
                }
            }
            downloadCallback.onComplete(fileDir + fileName);
        } catch (Exception e) {
            LogUtils.d(TAG, e.getMessage());
            downloadCallback.onError(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                SPDownloadUtil.getInstance().save(url, total);
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }

                if (inputStream != null) {
                    inputStream.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public interface UrlCheckCallback {
        void onSuccess();
        void onFailure();
    }
}

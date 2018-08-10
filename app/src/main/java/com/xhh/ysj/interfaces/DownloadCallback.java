package com.xhh.ysj.interfaces;

public interface DownloadCallback {

    void onProgress(int progress);

    void onComplete(String localPath);

    void onError(String msg);
}

package com.xhh.ysj.manager;

import java.lang.ref.WeakReference;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import com.xhh.ysj.utils.LogUtils;

public class DownloadSubscriber<T> implements Observer<T> {
    private static final String TAG = "DownloadSubscriber";
    //弱引用结果回调
    private WeakReference<HttpProgressOnNextListener> mSubscriberOnNextListener;
    /*用于取消订阅*/
    private Disposable disposable;

    public DownloadSubscriber(HttpProgressOnNextListener listener) {
        this.mSubscriberOnNextListener = new WeakReference<>(listener);
    }

    @Override
    public void onSubscribe(Disposable d) {
        LogUtils.d(TAG, "onSubscribe: dl_info: Subcriber里面的");
        disposable = d;
        if(null != mSubscriberOnNextListener.get()){
            mSubscriberOnNextListener.get().onStart();
        }
    }

    @Override
    public void onNext(T t) {
        LogUtils.d(TAG, "onNext: dl_info: Subcriber里面的");
        if (null != mSubscriberOnNextListener.get()) {
            mSubscriberOnNextListener.get().onNext(t);
        }
    }


    @Override
    public void onError(Throwable e) {
        LogUtils.d(TAG, "onError: dl_info: Subcriber里面的");
        /*停止下载*/
        DownloadManager.getInstance().stopDown();
        if(null != mSubscriberOnNextListener.get()){
            mSubscriberOnNextListener.get().onError(e);
        }
    }

    @Override
    public void onComplete() {
        LogUtils.d(TAG, "onComplete: dl_info: Subcriber里面的");
        if(null != mSubscriberOnNextListener.get()){
            mSubscriberOnNextListener.get().onComplete();
        }
    }

    public void unSubscribe() {
        LogUtils.d(TAG, "unSubscribe: dl_info: Subcriber里面的");
        if (null != disposable) {
            disposable.dispose();
        }
    }
}

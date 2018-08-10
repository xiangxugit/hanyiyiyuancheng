package com.xhh.ysj.utils;

import android.os.CountDownTimer;

public class CountDownUtil {

    /**
     * 倒计时结束的回调接口
     */
    public interface FinishListener {
        void onFinish();
    }

    /**
     * 定期回调的接口
     */
    public interface TickListener {
        void onTick(long mMillisUntilFinished);
    }

    private final static long ONE_SECOND = 1000;

    /**
     * 总倒计时时间
     */
    private long mMillisInFuture = 0;

    /**
     * 定期回调的时间 必须大于0 否则会出现ANR
     */
    private long mCountDownInterval = 1;

    /**
     * 倒计时结束的回调
     */
    private FinishListener mFinishListener;

    /**
     * 定期回调
     */
    private TickListener mTickListener;
    private MyCountDownTimer mCountDownTimer;

    /**
     * 获取 CountDownTimerUtils
     * @return CountDownTimerUtils
     */
    public static CountDownUtil getCountDownTimer() {
        return new CountDownUtil();
    }

    /**
     * 设置定期回调的时间 调用{@link #setTickDelegate(TickListener)}
     * @param mCountDownInterval 定期回调的时间 必须大于0
     * @return CountDownTimerUtils
     */
    public CountDownUtil setCountDownInterval(long mCountDownInterval) {
        if (mCountDownInterval > 0) {
            this.mCountDownInterval = mCountDownInterval;
        }
        return this;
    }

    /**
     * 设置倒计时结束的回调
     * @param mFinishListener 倒计时结束的回调接口
     * @return CountDownTimerUtils
     */
    public CountDownUtil setFinishDelegate(FinishListener mFinishListener) {
        this.mFinishListener = mFinishListener;
        return this;
    }

    /**
     * 设置总倒计时时间
     * @param mMillisInFuture 总倒计时时间
     * @return CountDownTimerUtils
     */
    public CountDownUtil setMillisInFuture(long mMillisInFuture) {
        this.mMillisInFuture=mMillisInFuture;
        return this;
    }

    /**
     * 设置定期回调
     * @param mTickListener 定期回调接口
     * @return CountDownTimerUtils
     */
    public CountDownUtil setTickDelegate(TickListener mTickListener) {
        this.mTickListener = mTickListener;
        return this;
    }

    public void create() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        if (mCountDownInterval <= 0) {
            mCountDownInterval = mMillisInFuture + ONE_SECOND;
        }
        mCountDownTimer = new MyCountDownTimer(mMillisInFuture, mCountDownInterval);
        mCountDownTimer.setTickListener(mTickListener);
        mCountDownTimer.setFinishListener(mFinishListener);
    }

    /**
     * 开始倒计时
     */
    public void start() {
        if (mCountDownTimer == null) {
            create();
        }
        mCountDownTimer.start();
    }

    /**
     * 取消倒计时
     */
    public void cancel() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    private static class MyCountDownTimer extends CountDownTimer {
        private FinishListener mFinishListener;
        private TickListener mTickListener;
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            if (mTickListener != null) {
                mTickListener.onTick(millisUntilFinished);
            }
        }
        @Override
        public void onFinish() {
            if (mFinishListener != null) {
                mFinishListener.onFinish();
            }
        }
        void setFinishListener(FinishListener mFinishListener) {
            this.mFinishListener = mFinishListener;
        }
        void setTickListener(TickListener mTickListener) {
            this.mTickListener = mTickListener;
        }
    }
}

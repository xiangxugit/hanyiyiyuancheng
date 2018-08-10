package com.xhh.ysj.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.List;

import com.xhh.ysj.utils.BaseSharedPreferences;
import com.xhh.ysj.utils.CountDownUtil;
import com.xhh.ysj.utils.LogUtils;
import com.xhh.ysj.utils.XutilsInit;
import com.xhh.ysj.R;
import com.xhh.ysj.beans.AdvsPlayRecode;
import com.xhh.ysj.beans.AdvsVideo;
import com.xhh.ysj.beans.DispenserCache;
import com.xhh.ysj.constants.Constant;
import com.xhh.ysj.manager.IjkManager;
import com.xhh.ysj.utils.TimeUtils;
import com.xhh.ysj.view.CircleTextProgressbar;
import com.xhh.ysj.view.TextProgressBar;

public class FreeAdActivity extends Activity implements IjkManager.PlayerStateListener, CircleTextProgressbar.OnCountdownProgressListener, View.OnClickListener, TextProgressBar.OnProgressBarListener {

    private static final String TAG = "FreeAdActivity";

    private CircleTextProgressbar cpbProgress;  // 圆形进度倒计时
    private TextProgressBar tpbProgress;  // 横条文字进度倒计时
    private Button btnQuit;
    private Context mContext;
    private IjkManager playerManager;
    private DbManager dbManager;
    private int deviceId;
    private int playDuration;  // 秒
    private boolean isPlayInitVideo;
    private int initAdIndex;
    private CountDownUtil countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initData();
        initView();
        initCountDown();
        initVideo();
    }

    @Override
    protected void onDestroy() {
        if (null != playerManager) {
            playerManager.stop();
            playerManager.onDestroy();
            playerManager = null;
        }
        if (null != countDownTimer) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        LogUtils.d(TAG, "onBackPressed: 无法回退");
    }

    private void initData() {
        mContext = FreeAdActivity.this;
        deviceId = BaseSharedPreferences.getInt(mContext, Constant.DEVICE_ID_KEY);
        dbManager = new XutilsInit(FreeAdActivity.this).getDb();
        playDuration = BaseSharedPreferences.getInt(mContext, Constant.ADVS_COUNT_DOWN_KEY);
        playDuration = 0 >= playDuration ? Constant.ADVS_COUNT_DOWN_DEFAULT : playDuration;
        LogUtils.d(TAG, "initData: 免费广告播放时长：" + playDuration);
        Intent intent = getIntent();
        if (null != intent) {
            Bundle bundle = intent.getExtras();
            if (null != bundle && bundle.containsKey(Constant.KEY_FREE_AD_DURATION)) {
                playDuration = bundle.getInt(Constant.KEY_FREE_AD_DURATION);
            }
        }
    }

    private void initView() {
        setContentView(R.layout.activity_free_ad);
        cpbProgress = findViewById(R.id.free_ad_progress_cpb);
        tpbProgress = findViewById(R.id.free_ad_progress_tpb);
        btnQuit = findViewById(R.id.free_ad_quit_btn);

        tpbProgress.setPrefix("观看广告");
        tpbProgress.setSuffix("秒方可喝水");
        tpbProgress.setProgressTextSize(20);
        tpbProgress.setProgressTextColor(Color.YELLOW);
        tpbProgress.setProgressTextVisibility(TextProgressBar.ProgressTextVisibility.Visible);
        tpbProgress.setUnreachedBarColor(Color.RED);
        tpbProgress.setReachedBarHeight(10);
        tpbProgress.setReachedBarHeight(5);
        tpbProgress.setOnProgressBarListener(this);
        tpbProgress.setVisibility(View.GONE);

        cpbProgress.setOutLineColor(getResources().getColor(R.color.dark_gray));
        cpbProgress.setInCircleColor(getResources().getColor(R.color.light_gray));
        cpbProgress.setProgressColor(getResources().getColor(R.color.royalblue));
        cpbProgress.setProgressLineWidth(5);
        cpbProgress.setText(playDuration + "");
        cpbProgress.setVisibility(View.VISIBLE);
        cpbProgress.setCountdownProgressListener(1, this);

        btnQuit.setVisibility(View.GONE);
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cpbProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initCountDown() {
        /*new CountDownTimer(playDuration * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int leftSec = (int) (millisUntilFinished / 1000);
                LogUtils.d(TAG, "onTick: left = " + millisUntilFinished);
                cpbProgress.setProgress((playDuration - leftSec) * 100 / playDuration);
                cpbProgress.setText(getString(R.string.free_ad_wait, leftSec));

                tpbProgress.setProgress((playDuration - leftSec) * 100 / playDuration);
                tpbProgress.setProgressText(leftSec + "");
            }

            @Override
            public void onFinish() {
//                cpbProgress.setVisibility(View.GONE);
//                btnQuit.setVisibility(View.VISIBLE);
                DispenserCache.isFreeAdDone = true;
                finish();
            }
        }.start();*/
        countDownTimer = CountDownUtil.getCountDownTimer();
        countDownTimer
                // 倒计时总时间
                .setMillisInFuture(playDuration * 1000)
                // 每隔多久回调一次onTick
                .setCountDownInterval(1000)
                // 每回调一次onTick执行
                .setTickDelegate(new CountDownUtil.TickListener() {
                    @Override
                    public void onTick(long mMillisUntilFinished) {
                        int leftSec = (int) (mMillisUntilFinished / 1000);
                        LogUtils.d(TAG, "onTick: left = " + mMillisUntilFinished);
                        cpbProgress.setProgress((playDuration - leftSec) * 100 / playDuration);
                        cpbProgress.setText(getString(R.string.free_ad_wait, leftSec));

                        tpbProgress.setProgress((playDuration - leftSec) * 100 / playDuration);
                        tpbProgress.setProgressText(leftSec + "");
                    }
                })
                // 结束倒计时执行
                .setFinishDelegate(new CountDownUtil.FinishListener() {
                    @Override
                    public void onFinish() {
//                        cpbProgress.setVisibility(View.GONE);
//                        btnQuit.setVisibility(View.VISIBLE);
                        DispenserCache.isFreeAdDone = true;
                        finish();
                    }
                }).start();
    }

    private void initVideo() {
        // 初始化播放器
        playerManager = new IjkManager(this, R.id.free_ad_video);
        playerManager.setFullScreenOnly(true);
        playerManager.setScaleType(IjkManager.SCALETYPE_FILLPARENT);
        playerManager.playInFullScreen(true);
        playerManager.setOnPlayerStateChangeListener(this);
        playVideo();
    }

    private void playVideo() {
        if (null != DispenserCache.freeAdVideoList && 0 != DispenserCache.freeAdVideoList.size()) {
            LogUtils.d(TAG, "playVideo: 有广告，播放广告视频");
            isPlayInitVideo = false;
            String proxyUrl = DispenserCache.freeAdVideoList.get(DispenserCache.freeAdIndex
                    % DispenserCache.freeAdVideoList.size()).getAdvsVideoLocaltionPath();
            playerManager.play(proxyUrl);
        } else if (null != DispenserCache.initAdVideoList && 0 != DispenserCache.initAdVideoList.size()) {
            LogUtils.d(TAG, "playVideo: 无广告，播放初始视频");
            isPlayInitVideo = true;
            String proxyUrl = DispenserCache.initAdVideoList.get(initAdIndex
                    % DispenserCache.initAdVideoList.size()).getAdvsVideoLocaltionPath();
            playerManager.play(proxyUrl);
        }
    }

    // ------------ ijk 监听 start ------------
    @Override
    public void onComplete() {
        if (isPlayInitVideo) {
            LogUtils.d(TAG, "onComplete: initIndex = " + initAdIndex);
            initAdIndex += 1;
        } else {
            LogUtils.d(TAG, "onComplete: freeIndex = " + DispenserCache.freeAdIndex);
            AdvsVideo curAd = DispenserCache.freeAdVideoList.get(DispenserCache.freeAdIndex
                    % DispenserCache.freeAdVideoList.size());
            AdvsPlayRecode curAdRecord = new AdvsPlayRecode(curAd.getAdvsId(), deviceId, TimeUtils.getCurrentTime(),
                    curAd.getAdvsVideoLengthOfTime(), curAd.getAdvsChargMode(),
                    curAd.getAdvsIndustry(), curAd.getAdvsType());
            try {
                dbManager.save(curAdRecord);
                List<AdvsPlayRecode> all = dbManager.findAll(AdvsPlayRecode.class);
                LogUtils.d(TAG, "onComplete: all.size = " + all.size());
            } catch (DbException e) {
                e.printStackTrace();
            }
            DispenserCache.freeAdIndex++;
            playVideo();
        }
    }

    @Override
    public void onError(int what, int extra) {
        if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            //媒体服务器挂掉了。此时，程序必须释放MediaPlayer 对象，并重新new 一个新的。
            LogUtils.e(TAG, "onError: 视频播放：网络服务错误");
        } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
            LogUtils.e(TAG, "onError: 视频播放：文件不存在或错误，或网络不可访问错误");
        } else if (what == -10000) {
            LogUtils.e(TAG, "onError: 视频播放：本地文件被删除");
            // 列表删除这条广告
            AdvsVideo ad;
            if (isPlayInitVideo) {
                ad = DispenserCache.initAdVideoList.get(initAdIndex % DispenserCache.initAdVideoList.size());
                DispenserCache.initAdVideoList.remove(ad);
            } else {
                ad = DispenserCache.freeAdVideoList.get(DispenserCache.freeAdIndex
                        % DispenserCache.freeAdVideoList.size());
                DispenserCache.freeAdVideoList.remove(ad);
            }
            // 数据库删除这条广告
            try {
                dbManager.delete(ad);
            } catch (DbException e) {
                e.printStackTrace();
            }
            // 上报错误
            // TODO: 2018/7/31 0031 上报本地视频被删除的错误
        } else {
            LogUtils.e(TAG, "onError: 视频播放：错误！what = " + what + ", extra = " + extra);
        }
    }

    @Override
    public void onInfo(int what, int extra) {

    }
    // ------------ ijk 监听 end ------------

    @Override
    public void onClick(View v) {
        DispenserCache.isFreeAdDone = true;
        finish();
    }

    @Override
    public void onProgress(int what, int progress) {
        LogUtils.d(TAG, "onProgress: what = " + what + ", progress = " + progress);
    }

    @Override
    public void onProgressChange(int current, int max) {
        LogUtils.d(TAG, "onProgress: current = " + current + ", max = " + max);
    }
}

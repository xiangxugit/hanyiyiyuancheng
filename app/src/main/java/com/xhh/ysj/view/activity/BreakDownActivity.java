package com.xhh.ysj.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.xhh.ysj.R;
import com.xhh.ysj.constants.Constant;
import com.xhh.ysj.utils.BaseSharedPreferences;
import com.xhh.ysj.utils.LogUtils;

public class BreakDownActivity extends Activity {

    private static final String TAG = "BreakDownActivity";
    private TextView tvReason;
    private TextView tvContract;
    protected static BreakDownActivity instance;
    private Context mContext;
    private String errReason;
    private String contractInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        LogUtils.d("MainActivity-BreakDownActivity", "onDestroy: --");
        MainActivity.breakInstance = null;
        instance = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        LogUtils.d(TAG, "onBackPressed: 设备已坏，无法返回！");
    }

    private void initData() {
        LogUtils.d("MainActivity-BreakDownActivity", "initData: --");
        mContext = BreakDownActivity.this;
        MainActivity.breakInstance = BreakDownActivity.this;
        instance = BreakDownActivity.this;
        LogUtils.d("MainActivity-BreakDownActivity", "initData: --instance 复制完毕");
        contractInfo = BaseSharedPreferences.getString(mContext, Constant.CONTRACT_INFO_KEY);
        if (TextUtils.isEmpty(contractInfo)) {
            contractInfo = "维护人员";
        }
        Intent intent = getIntent();
        if (null != intent) {
            Bundle bundle = intent.getExtras();
            if (null != bundle) {
                errReason = bundle.getString(Constant.KEY_BREAK_DOWN_ERR_REASON, getString(R.string.break_down_reason_default));
                LogUtils.e(TAG, "initData: break down! error reason: " + errReason);
            }
        }
    }

    private void initView() {
        setContentView(R.layout.activity_break_down);
        tvReason = findViewById(R.id.break_down_reason_tv);
        tvContract = findViewById(R.id.break_down_contract_info_tv);
        tvReason.setText(errReason);
        tvContract.setText(contractInfo);
    }
}

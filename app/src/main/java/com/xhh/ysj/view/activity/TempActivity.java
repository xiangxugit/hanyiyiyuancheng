package com.xhh.ysj.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.xhh.ysj.R;
import com.xhh.ysj.constants.Constant;
import com.xhh.ysj.constants.UriConstant;
import com.xhh.ysj.utils.BaseSharedPreferences;
import com.xhh.ysj.utils.FileUtil;

import java.io.File;

public class TempActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        // 1、清除缓存的deviceId和本地文件夹
        FileUtil.deleteFile(new File(UriConstant.APP_ROOT_PATH));
        BaseSharedPreferences.setInt(TempActivity.this, Constant.DEVICE_ID_KEY, 0);
        // 2、跳转回InitActivity
        Intent intent = new Intent(TempActivity.this, InitActivity.class);
        startActivity(intent);
        finish();
    }
}

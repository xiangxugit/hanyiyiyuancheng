package com.xhh.ysj.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.xhh.ysj.R;
import com.xhh.ysj.constants.Constant;
import com.xhh.ysj.constants.UriConstant;
import com.xhh.ysj.view.activity.BaseActivity;
import com.xhh.ysj.view.PopQrCode;

import okhttp3.Request;

import static com.xhh.ysj.view.activity.BaseActivity.deviceId;

public class   CommonUtil {

    private static final String TAG = "CommonUtil";

    /**
     * Base64加密
     * @param str
     * @return
     */
    public static String encode(String str) {
        String result = "";
        if( str != null) {
            try {
                result = new String(Base64.encode(str.getBytes("utf-8"), Base64.NO_WRAP),"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Base64解密
     * @param str
     * @return
     */
    public static String decode(String str) {
        String result = "";
        if (str != null) {
            try {
                result = new String(Base64.decode(str, Base64.NO_WRAP), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // "^-?\\d+$"
    // /^-?[0-9]\d*$/
    // ^\d+$
    // [0-9]*
    public static boolean isNumeric(String str){
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^-?\\d+$");
        Matcher isNum = pattern.matcher(str);
        if(!isNum.matches() ){
            return false;
        }
        return true;
    }

    /**
     * 是否是快速点击（是则return，防止快速连续点击）
     * @return
     */
    public static long lastClickTime;
    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= Constant.FAST_CLICK_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

    public static void showQrCode(final BaseActivity mContext) {
        LogUtils.d(TAG, "showQrCode: 显示扫码取水二维码");
        mContext.showPopQrCode();
        final ImageView qcode = PopQrCode.qrcode;
        String qrCodeString = BaseSharedPreferences.getString(mContext, Constant.SCODEKEY);
        LogUtils.d(TAG, "showQrCode: qrCodeString = " + qrCodeString);
        if(null == qrCodeString){
            //重新请求
            String sCodeUrl = RestUtils.getUrl(UriConstant.GETTEMPQCODE) + "/" + BaseSharedPreferences.getInt(mContext,Constant.DEVICE_ID_KEY);
            OkHttpUtils.getAsyn(sCodeUrl, new OkHttpUtils.StringCallback() {
                @Override
                public void onFailure(int errCode, Request request, IOException e) {
                    LogUtils.e(TAG, "onFailure: 获取支付二维码失败，" +
                            "errCode = " + errCode + "，request = " + request.toString());
                    PopQrCode.getwater.setText(R.string.pleasepaytogetwater);
                }
                @Override
                public void onResponse(String response) {
                    LogUtils.d(TAG, "onResponse: 获取支付二维码：response = " + response);
                    JSONObject scodeobj = JSONObject.parseObject(response);
                    if (null == scodeobj) {
                        LogUtils.e(TAG, "onResponse: 获取支付二维码：response cannot parse to json object");
                        return;
                    }
                    if ("0".equals(scodeobj.getString("code"))) {
                        String data = scodeobj.getString("data");
                        BaseSharedPreferences.setString(mContext, Constant.SCODEKEY, data);
                        Bitmap qcodebitmap = Create2QR2.createBitmap(data);
                        qcode.setImageBitmap(qcodebitmap);
                    }
                }
            });
        } else {
            Bitmap qcodebitmap = Create2QR2.createBitmap(qrCodeString);
            qcode.setImageBitmap(qcodebitmap);
        }
        TextView rightText = PopQrCode.getwater;
        rightText.setText(R.string.get_water_bind);
        //请求二维码
//        String getTempQCodeurl = RestUtils.getUrl(UriConstant.GETTEMPQCODE);
//        OkHttpUtils.getAsyn(getTempQCodeurl, new OkHttpUtils.StringCallback() {
//            @Override
//            public void onFailure(int errCode, Request request, IOException e) {
//
//            }
//            @Override
//            public void onResponse(String response) {
//                Bitmap qcodebitmap = Create2QR2.createBitmap(response);
//                qcode.setImageBitmap(qcodebitmap);
//                TextView rightText = PopQrCode.getwater;
//                rightText.setText("扫码关注，完成用户绑定");
//            }
//        });
    }
}


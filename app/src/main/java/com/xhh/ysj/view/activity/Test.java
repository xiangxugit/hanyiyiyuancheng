package com.xhh.ysj.view.activity;

import android.content.Context;

/**
 * Created by Administrator on 2018/7/3 0003.
 */

public class Test {

    private static float density = 0f;
    private static float defaultDensity = 1.5f;// 高分辨率的手机density普遍接近1.5

    private Test() {   }

    public static void setDensity(float density) {
        Test.density = density;
    }

    public static float getDensity(Context context) {
        return  context.getResources().getDisplayMetrics().density;
    }

    public static int getScreenWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight(Context context){
        return context.getResources().getDisplayMetrics().heightPixels;
    }
    /**
     * 根据手机的分辨率 dp 转成px(像素)
     */
    public static int dip2px(float dpValue) {
        int px;
        if (density == 0) {

            px = (int) (dpValue * defaultDensity + 0.5f);
        } else {
            px = (int) (dpValue * density + 0.5f);
        }
        return px;
    }

    /**
     * 根据手机的分辨率px(像素) 转成dp
     */
    public static float px2dip(float pxValue,Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return  px2dip( pxValue,  scale);
    }

    public static float px2dip(float pxValue, float scale) {
        return  (pxValue / scale + 0.5f);
    }


    public static float px2sp(float pxValue, float fontScale) {

        return  (pxValue / fontScale + 0.5f);
    }

    public static float dip2px(float dipValue, float scale) {
        return  (dipValue * scale + 0.5f);
    }


}

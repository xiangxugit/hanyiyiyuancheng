package com.xhh.ysj.view;

import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.serialport.DevUtil;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.xhh.ysj.R;
import com.xhh.ysj.beans.ViewShow;
import com.xhh.ysj.broadcast.UpdateBroadcast;
import com.xhh.ysj.constants.Constant;
import com.xhh.ysj.interfaces.OnUpdateUI;
import com.xhh.ysj.processpreserve.ComThread;
import com.xhh.ysj.utils.BaseSharedPreferences;
import com.xhh.ysj.utils.ImageUtils;
import com.xhh.ysj.utils.LogUtils;
import com.xhh.ysj.view.activity.BaseActivity;

import java.io.File;

/**
 * 自定义的PopupWindow
 */
public class PopLeftOperate extends PopupWindow {

    private static final String TAG = "PopLeftOperate";
    private static final String FLAG = "UPDATE";

    private TextView deviceNoText;
    private TextView modeText;
    public static TextView hotwatertext;//在右边的POPRIGHT方便控制左边的界面
    public static TextView coolwatertext;//在右边的POPRIGHT方便控制左边的界面
    public TextView outTdsTitle;
    public TextView tvPpmRate;
    public LinearLayout outTdsValueLl;
    private TextView outTdsValue;
    private TextView rawPpm;//下方的
    private ImageView hotIco;//是否加热的imageview
    private TextView hotOrNot;//是否加热text
    private ImageView coolIco;//是否制冷的imageView
    private TextView coolText;//是否制冷text
    private ImageView produceWaterIco;//是否制水的imageView
    private TextView produceWaterText;//是佛止水的text
    private ImageView flushIco;//冲洗imageView
    private TextView flushText;//冲洗text;

    private BaseActivity activity;
    private UpdateBroadcast myBroadcast;
    private static final String DevPath = "/dev/ttyS3";//默认串口
    private static final int Baudrate = 115200;//默认波特率
    private DevUtil devUtil;
    ImageUtils imageUtils = new ImageUtils();
    public PopLeftOperate(final BaseActivity activity) {
//        if (null == devUtil) {
//            devUtil = new DevUtil(null);
//        }
//        if (devUtil.isComOpened()) {
//            Toast.makeText(activity, "启动了", Toast.LENGTH_SHORT).show();
//        } else {
//            try {
//                File dev = new File(DevPath);
//                boolean r = devUtil.openCOM(dev, Baudrate, 0);
//                if (!r) {
//                    Toast.makeText(activity, "通讯失败", Toast.LENGTH_SHORT).show();
//                }
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }
//        }
        this.activity = activity;
        // 通过layout的id找到布局View
        View contentView = LayoutInflater.from(activity).inflate(R.layout.pop_left, null);
        // 获取PopupWindow的宽高
        int h = activity.getWindowManager().getDefaultDisplay().getHeight();
        int w = activity.getWindowManager().getDefaultDisplay().getWidth();
        // 设置PopupWindow的View
        this.setContentView(contentView);
        // 设置PopupWindow弹出窗体的宽高
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置PopupWindow弹出窗体可点击（下面两行代码必须同时出现）
//        this.setFocusable(true);
        this.setOutsideTouchable(false); // 当点击外围的时候隐藏PopupWindow
        // 刷新状态
        this.update();
        // 设置PopupWindow的背景颜色为半透明的黑色
        ColorDrawable dw = new ColorDrawable(Color.parseColor("#99022641"));
        this.setBackgroundDrawable(dw);
        // 设置PopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.PopWindowAnimStyle);

        DevUtil devUtil = ComThread.devUtil;
        imageUtils = new ImageUtils();
        myBroadcast = new UpdateBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FLAG);
        activity.registerReceiver(myBroadcast, intentFilter);

        // 这里也可以从contentView中获取到控件，并为它们绑定控件
        deviceNoText = contentView.findViewById(R.id.device_number);
        modeText = contentView.findViewById(R.id.drink_mode);
        hotwatertext = contentView.findViewById(R.id.hot_water_text);
        coolwatertext =  contentView.findViewById(R.id.cool_water_text);
        outTdsTitle = contentView.findViewById(R.id.pop_operate_p_tds_title);
        tvPpmRate = contentView.findViewById(R.id.out_ppm_rate_tv);
        outTdsValueLl = contentView.findViewById(R.id.out_ppm_value_ll);
        outTdsValue = contentView.findViewById(R.id.out_ppm_value);
        rawPpm = contentView.findViewById(R.id.raw_ppm);
        hotIco = contentView.findViewById(R.id.hot_ico);
        hotOrNot = contentView.findViewById(R.id.hot_or_not);
        coolIco = contentView.findViewById(R.id.cool_ico);
        coolText = contentView.findViewById(R.id.cooltext);
        produceWaterIco = contentView.findViewById(R.id.produce_water_ico);
        produceWaterText = contentView.findViewById(R.id.produce_water_text);
        flushIco = contentView.findViewById(R.id.flush_ico);
        flushText = contentView.findViewById(R.id.flush_text);
        int drinkMode = BaseSharedPreferences.getInt(activity, Constant.DRINK_MODE_KEY);
        switch (drinkMode) {
            case Constant.DRINK_MODE_WATER_SALE:
                modeText.setVisibility(View.VISIBLE);
                modeText.setText(activity.getString(R.string.drink_mode_water_sale));
                break;
            case Constant.DRINK_MODE_MACHINE_SALE:
                modeText.setVisibility(View.VISIBLE);
                modeText.setText(activity.getString(R.string.drink_mode_machine_sale));
                break;
            case Constant.DRINK_MODE_MACHINE_RENT:
                modeText.setVisibility(View.VISIBLE);
                modeText.setText(activity.getString(R.string.drink_mode_rent));
                break;
            default:
                modeText.setVisibility(View.GONE);
                break;
        }
        LogUtils.d(TAG, "是否加热：" + devUtil.get_run_bHot_value());
        LogUtils.d(TAG, "即将设置deviceNo." + activity.getString(R.string.device_number,
                BaseSharedPreferences.getString(activity, Constant.DEVICE_NUMBER_KEY)));
        deviceNoText.setText(activity.getString(R.string.device_number,
                BaseSharedPreferences.getString(activity, Constant.DEVICE_NUMBER_KEY)));
        myBroadcast.SetOnUpdateUI(new OnUpdateUI() {
            @Override
            public void updateUI(ViewShow data) {
//                Toast.makeText(activity,data.toString(),Toast.LENGTH_LONG).show();
                deviceNoText.setText(activity.getString(R.string.device_number,
                        BaseSharedPreferences.getString(activity, Constant.DEVICE_NUMBER_KEY)));
//                if(Constant.WENDU_HOT_LOCK){
//
//                }else{
                    hotwatertext.setText(data.getHotwatertextvalue());
//                }

//                if(Constant.WENDU_COOL_LOCK){
//
//                }
//                else{
                    coolwatertext.setText(data.getCoolwatertextvalue());
//                }
                outTdsValue.setText(data.getPpm());
                rawPpm.setText(activity.getString(R.string.ppm_value, data.getPpmvalue()));
                hotOrNot.setText(data.getHotornot());
                coolText.setText(data.getCooltext());
                produceWaterText.setText(data.getZhishuitext());
                flushText.setText(data.getChongxitext());

                if (data.getHotornot().equals("正在加热")) {
//               imageUtils.setFlickerAnimation(hotIco);
                    imageUtils.setFlickerAnimation(hotIco);

                } else {
                    imageUtils.clearFlickerAnimation(hotIco);
                }
                if (data.getCooltext().equals("正在制冷")) {
                    imageUtils.setFlickerAnimation(coolIco);

                } else {
                    imageUtils.clearFlickerAnimation(coolIco);
                }
                if (data.getZhishuitext().equals("正在制水")) {
                    imageUtils.setFlickerAnimation(produceWaterIco);

                } else {
                    imageUtils.clearFlickerAnimation(produceWaterIco);
                }
                if (data.getChongxitext().equals("正在冲洗")) {
                    imageUtils.setFlickerAnimation(flushIco);
                } else {
                    imageUtils.clearFlickerAnimation(flushIco);
                }
            }
        });
    }

    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            this.showAtLocation(parent, Gravity.LEFT, 0, 0);
        }
    }

}
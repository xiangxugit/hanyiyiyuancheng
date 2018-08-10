package com.xhh.ysj.view;

import android.serialport.DevUtil;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.xhh.ysj.R;
import com.xhh.ysj.processpreserve.ComThread;
import com.xhh.ysj.utils.CommonUtil;
import com.xhh.ysj.utils.LogUtils;
import com.xhh.ysj.view.activity.BaseActivity;

import java.io.File;


/**
 * 自定义的PopupWindow
 */
public class PopWarning extends PopupWindow {
    private static final String TAG = "PopWarning";
    private BaseActivity activity;
    private LinearLayout outcupleft;
    private LinearLayout outcupright;
    private TextView bottomsure;
    private DevUtil devUtil;
    private static final String DevPath = "/dev/ttyS3";//默认串口
    private static final int Baudrate = 115200;//默认波特率
    public ComThread comThread ;
    public PopWarning(BaseActivity activity) {
        // 通过layout的id找到布局View
        this.activity = activity;
        View contentView = LayoutInflater.from(activity).inflate(R.layout.pop_warning, null);
        // 获取PopupWindow的宽高
        int h = activity.getWindowManager().getDefaultDisplay().getHeight();
        int w = activity.getWindowManager().getDefaultDisplay().getWidth();
        // 设置PopupWindow的View
        this.setContentView(contentView);
        // 设置PopupWindow弹出窗体的宽高
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置PopupWindow弹出窗体可点击（下面两行代码必须同时出现）
        this.setFocusable(true);
        this.setOutsideTouchable(false); // 当点击外围的时候隐藏PopupWindow
        // 刷新状态
        this.update();
        // 设置PopupWindow的背景颜色为半透明的黑色
//        ColorDrawable dw = new ColorDrawable(Color.parseColor("#66000000"));
//        this.setBackgroundDrawable(dw);
        // 设置PopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.PopWindowAnimStyle);

        // 这里也可以从contentView中获取到控件，并为它们绑定控件
        outcupleft = contentView.findViewById(R.id.pop_warning_confirm_rl);
        outcupleft.setOnClickListener(onclick);
        outcupright = contentView.findViewById(R.id.pop_warning_cancel_rl);
        outcupright.setOnClickListener(onclick);
        bottomsure = (TextView) contentView.findViewById(R.id.bottom_sure);

    }

    // 显示PopupWindow，有两种方法：showAsDropDown、showAtLocation
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            this.showAtLocation(parent, Gravity.CENTER, 20, 0);
        }
    }


    View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
            switch (v.getId()) {
                case R.id.pop_warning_confirm_rl:
                    if (null == devUtil) {
                        devUtil = new DevUtil(null);
                    }

                    if (null == comThread) {
                        comThread = new ComThread(activity, null);
                    }
                    if (devUtil.isComOpened()) {
//                        Toast.makeText(activity, "通讯启动", Toast.LENGTH_SHORT).show();
                    } else {
                        comThread.setActive(false);
                        try {
                            File dev = new File(DevPath);
                            boolean r = devUtil.openCOM(dev, Baudrate, 0);
                            if (!r) {
//                                Toast.makeText(activity, "通讯失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                    String hotwatertext = PopRightOperate.hotWater.getTag().toString();
                    if (CommonUtil.isFastClick()) {
//                        Toast.makeText(activity, activity.getString(R.string.operate_too_fast), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (activity.getString(R.string.hot_water).equals(hotwatertext)) {
                            PopRightOperate.hotWater.setTag(activity.getString(R.string.stop_get_water));
                            PopRightOperate.sendWaterFlag = true;
                            PopRightOperate.hotWater.setImageResource(R.drawable.hot_water_stop);
                            int sw = 1;
                            try {
                                if (devUtil.do_ioWater(1, sw) == 0) {
//                                    String userid = ""
//                                    Toast.makeText(activity, "操作成功", Toast.LENGTH_SHORT).show();
                                    LogUtils.d(TAG, "出热水操作成功");
                                } else {
//                                    Toast.makeText(activity, "操作失败", Toast.LENGTH_SHORT).show();
                                    LogUtils.d(TAG, "出热水操作失败");
                                }
                            } catch (Exception e) {
//                                 Toast.makeText(activity, "报错", Toast.LENGTH_SHORT).show();
                                LogUtils.d(TAG, "出热水操作报错");
                            } finally {
                                comThread.setActive(true);
                            }
                        } else {
                            //关闭取热水
                            PopRightOperate.hotWater.setTag(activity.getString(R.string.hot_water));
                            String s = "出热水指令执行";
                            int sw = 2;
                            try {
                                if (devUtil.do_ioWater(1, sw) == 0) {
//                                    Toast.makeText(activity, s + "成功", Toast.LENGTH_SHORT).show();
                                    LogUtils.d(TAG, "停止热水操作成功");
                                } else {
//                                    Toast.makeText(activity, s + "失败", Toast.LENGTH_SHORT).show();
                                    LogUtils.d(TAG, "停止热水操作失败");
                                }

                            } catch (Exception e) {
//                                Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
                                LogUtils.d(TAG, "停止热水操作报错");
                            } finally {
                                comThread.setActive(true);
                            }
                            //获取到本次出水的量
                            String[][] data = devUtil.toArray();
                            int getwaterdata = Integer.parseInt(data[17][1]);
                        }
                    }

                   /* if(devUtil.isComOpened()==true){
                        if (devUtil.do_ioWater(1, sw) == 0) {
                            Toast.makeText(activity, s + "成功", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(activity, s + "失败", Toast.LENGTH_SHORT).show();
                        }
                    }else{

                        if (devUtil.do_ioWater(1, sw) == 0) {
                            Toast.makeText(activity, s + "成功", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(activity, s + "失败", Toast.LENGTH_SHORT).show();
                        }

                    }*/
                    // 延迟15秒
                    break;
//                case R.id.outcupright:
//                    Toast.makeText(activity,"取消水",Toast.LENGTH_SHORT).show();
//                    break;
            }
        }

    };

}
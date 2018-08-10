package com.xhh.ysj.view;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.xhh.ysj.R;
import com.xhh.ysj.constants.Constant;
import com.xhh.ysj.utils.BaseSharedPreferences;
import com.xhh.ysj.utils.CommonUtil;
import com.xhh.ysj.view.activity.BaseActivity;

/**
 * 自定义的PopupWindow
 */
public class PopWaterSale extends PopupWindow {
    private LinearLayout freeGetWater;
    private LinearLayout payGetWater;
    private BaseActivity activity;

    // TODO: 2018/7/12 0012 这个handler没回收
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dismissPopupWindow();
        }
    };

    public PopWaterSale(BaseActivity activity) {
        this.activity = activity;
        // 通过layout的id找到布局View
        View contentView = LayoutInflater.from(activity).inflate(R.layout.pop_water_sale, null);

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
        this.setOutsideTouchable(true); // 当点击外围的时候隐藏PopupWindow
        // 刷新状态
        this.update();
        // 设置PopupWindow的背景颜色为半透明的黑色
        /*ColorDrawable dw = new ColorDrawable(Color.parseColor("#66000000"));
        this.setBackgroundDrawable(dw);*/
        // 设置PopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.PopWindowAnimStyle);

        // 这里也可以从contentView中获取到控件，并为它们绑定控件
        freeGetWater = contentView.findViewById(R.id.left_pop);
        payGetWater = contentView.findViewById(R.id.right_pop);
        freeGetWater.setOnClickListener(onclick);
        payGetWater.setOnClickListener(onclick);
    }

    // 显示PopupWindow，有两种方法：showAsDropDown、showAtLocation
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            this.showAtLocation(parent, Gravity.CENTER, 0, 0);
        }
        handler.sendEmptyMessageDelayed(Constant.MSG_DISMISS_POP, Constant.DISMISS_POP_TIME);
    }

    @Override
    public void dismiss() {
        if (null != handler && handler.hasMessages(Constant.MSG_DISMISS_POP)) {
            handler.removeMessages(Constant.MSG_DISMISS_POP);
        }
        super.dismiss();
    }

    public void dismissPopupWindow() {
        if (null != this && this.isShowing()) {
            this.dismiss();
        }
    }

    View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
            switch (v.getId()) {
                case R.id.left_pop:
                    dismiss();
                    int countDown = BaseSharedPreferences.getInt(activity, Constant.ADVS_COUNT_DOWN_KEY);
                    countDown = 0 >= countDown ? Constant.ADVS_COUNT_DOWN_DEFAULT : countDown;
                    activity.moveToFreeAdActivity(countDown);
                    break;
                case R.id.right_pop:
                    CommonUtil.showQrCode(activity);
                    break;
            }
        }

    };
}
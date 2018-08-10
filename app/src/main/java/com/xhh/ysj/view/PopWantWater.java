package com.xhh.ysj.view;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xhh.ysj.R;
import com.xhh.ysj.constants.Constant;
import com.xhh.ysj.utils.CommonUtil;
import com.xhh.ysj.view.activity.BaseActivity;

/**
 * 自定义的PopupWindow
 */
public class PopWantWater extends PopupWindow {
    private final int drinkMode;
    private BaseActivity activity;
    private ImageView wantwater;
    private TextView getwater;

    public PopWantWater(BaseActivity activity, int drinkMode) {
        // 通过layout的id找到布局View
        this.activity = activity;
        this.drinkMode = drinkMode;
        View contentView = LayoutInflater.from(activity).inflate(R.layout.pop_want_water, null);
        // 获取PopupWindow的宽高
        int h = activity.getWindowManager().getDefaultDisplay().getHeight();
        int w = activity.getWindowManager().getDefaultDisplay().getWidth();
        // 设置PopupWindow的View
        this.setContentView(contentView);
        // 设置PopupWindow弹出窗体的宽高
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置PopupWindow弹出窗体可点击（下面两行代码必须同时出现）
        this.setFocusable(false);
        this.setTouchable(true);
        this.setOutsideTouchable(false); // 当点击外围的时候隐藏PopupWindow
        // 刷新状态
        this.update();
        // 设置PopupWindow的背景颜色为半透明的黑色
//        ColorDrawable dw = new ColorDrawable(Color.parseColor("#66000000"));
//        this.setBackgroundDrawable(dw);
        // 设置PopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.PopWindowAnimStyle);

        wantwater = (ImageView) contentView.findViewById(R.id.want_water);
        wantwater.setOnClickListener(onclick);
        // 这里也可以从contentView中获取到控件，并为它们绑定控件
    }

    // 显示PopupWindow，有两种方法：showAsDropDown、showAtLocation
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // showAsDropDown方法，在parent下方的(x,y)位置显示，x、y是第二和第三个参数
            // this.showAsDropDown(parent, parent.getWidth() / 2 - 400, 18);
            // showAtLocation方法，在parent的某个位置参数，具体哪个位置由后三个参数决定
            this.showAtLocation(parent, Gravity.RIGHT, 0, 100);
        } else {
//            this.showAtLocation(parent, Gravity.RIGHT, 0, 100);
//            this.dismiss();
        }
    }


    View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            dismiss();
            switch (v.getId()) {
                case R.id.want_water:
                    switch (drinkMode) {
                        case Constant.DRINK_MODE_WATER_SALE:
                            activity.showPopWaterSale();
                            break;
                        case Constant.DRINK_MODE_MACHINE_SALE:
                            CommonUtil.showQrCode(activity);
                            break;
                        case Constant.DRINK_MODE_MACHINE_RENT:
                            CommonUtil.showQrCode(activity);
                            break;
                    }
                    
                    break;
            }
        }

    };


}
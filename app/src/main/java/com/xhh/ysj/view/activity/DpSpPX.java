package com.xhh.ysj.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xhh.ysj.R;

public class DpSpPX extends AppCompatActivity {
    private TextView getDensity;
    private TextView getDensityvalue;
    private EditText pxvalue;
    private TextView getdp;
    private TextView dpvalue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dp_sp_px);

        getDensity = findViewById(R.id.getDensity);
        getDensityvalue = findViewById(R.id.getDensityvalue);
        pxvalue = findViewById(R.id.pxvalue);
        getdp = findViewById(R.id.getdp);
        dpvalue = findViewById(R.id.dpvalue);
        getDensity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDensityvalue.setText(""+Test.getDensity(DpSpPX.this));;
            }
        });

        getdp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                float a = Float.parseFloat(getDensityvalue.getText().toString());
                float b = Float.parseFloat(pxvalue.getText().toString());
                dpvalue.setText("dp为："+Test.px2dip(b,DpSpPX.this));
//                dpvalue.setText(Test.px2dip(Float.parseFloat(getDensityvalue.getText().toString()),Float.parseFloat(pxvalue.getText().toString())));
            }
        });


    }
}

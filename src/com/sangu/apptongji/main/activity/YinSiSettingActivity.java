package com.sangu.apptongji.main.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sangu.apptongji.DemoHelper;
import com.sangu.apptongji.R;
import com.sangu.apptongji.main.FXConstant;
import com.sangu.apptongji.main.utils.MySingleton;
import com.sangu.apptongji.main.zhengshiinfo.AboutZhengshiActivity;
import com.sangu.apptongji.ui.BaseActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017-06-02.
 */

public class YinSiSettingActivity extends BaseActivity {
    private RelativeLayout re_qiye_yincang;
    private RelativeLayout re_qiye_zhaopin;
    private RelativeLayout re_qiye_jiameng;
    private RelativeLayout rl_about;
    private TextView textView13;
    private TextView tv_yincang;
    private String locationState=null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_shoufei_join);
        RequestQueue queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        locationState = this.getIntent().getStringExtra("locationState");
        rl_about = (RelativeLayout) findViewById(R.id.rl_about);
        re_qiye_yincang = (RelativeLayout) findViewById(R.id.re_qiye_yincang);
        re_qiye_zhaopin = (RelativeLayout) findViewById(R.id.re_qiye_zhaopin);
        re_qiye_jiameng = (RelativeLayout) findViewById(R.id.re_qiye_jiameng);
        textView13 = (TextView) findViewById(R.id.textView13);
        tv_yincang = (TextView) findViewById(R.id.tv_yincang);
        textView13.setText("?????????????????????");
        re_qiye_zhaopin.setVisibility(View.GONE);
        re_qiye_jiameng.setVisibility(View.GONE);
        if ("00".equals(locationState)){
            tv_yincang.setText("??????");
        }else {
            tv_yincang.setText("?????????");
        }
        rl_about.setVisibility(View.VISIBLE);
        rl_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(YinSiSettingActivity.this, AboutZhengshiActivity.class));
            }
        });
        re_qiye_yincang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater4 = LayoutInflater.from(YinSiSettingActivity.this);
                RelativeLayout layout4 = (RelativeLayout) inflater4.inflate(R.layout.dialog_alert, null);
                final Dialog dialog4 = new AlertDialog.Builder(YinSiSettingActivity.this,R.style.Dialog).create();
                dialog4.show();
                dialog4.getWindow().setContentView(layout4);
                dialog4.setCanceledOnTouchOutside(true);
                dialog4.setCancelable(true);
                TextView title_tv4 = (TextView) layout4.findViewById(R.id.title_tv);
                Button btnCancel4 = (Button) layout4.findViewById(R.id.btn_cancel);
                final Button btnOK4 = (Button) layout4.findViewById(R.id.btn_ok);
                btnOK4.setText("??????");
                btnCancel4.setText("??????");
                if ("00".equals(locationState)) {
                    title_tv4.setText("???????????????????????????");
                }else {
                    title_tv4.setText("????????????????????????");
                }
                btnCancel4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog4.dismiss();
                    }
                });
                btnOK4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog4.dismiss();
                        if ("00".equals(locationState)) {
                            weizhi("?????????");
                        }else {
                            weizhi("??????");
                        }
                    }
                });
            }
        });
    }

    private void weizhi(final String shangbanzt) {
        String url = FXConstant.URL_UPDATE;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e("yinsiset,s",s);
                if ("00".equals(locationState)) {
                    tv_yincang.setText("?????????");
                    locationState = "01";
                }else {
                    tv_yincang.setText("??????");
                    locationState = "00";
                }
                Toast.makeText(getApplicationContext(), "????????????",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("uLoginId", DemoHelper.getInstance().getCurrentUsernName());
                if ("?????????".equals(shangbanzt)) {
                    param.put("locationState", "01");
                    param.put("workState", "01");
                }else {
                    param.put("locationState", "00");
                    param.put("workState", "00");
                }
                return param;
            }
        };
        MySingleton.getInstance(YinSiSettingActivity.this).addToRequestQueue(request);
    }

    @Override
    public void back(View view) {
        super.back(view);
    }
}

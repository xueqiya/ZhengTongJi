package com.sangu.apptongji.wxapi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sangu.apptongji.Constant;
import com.sangu.apptongji.R;
import com.sangu.apptongji.main.activity.SettingSelfActivity;
import com.sangu.apptongji.main.activity.TopVipActivity;
import com.sangu.apptongji.main.alluser.order.avtivity.BZJJNActivity;
import com.sangu.apptongji.main.alluser.order.avtivity.BZJZJActivity;
import com.sangu.apptongji.main.alluser.order.avtivity.ChongZhiActivity;
import com.sangu.apptongji.main.alluser.order.avtivity.UOrderDetailActivity;
import com.sangu.apptongji.main.alluser.order.avtivity.UOrderDetailThreeActivity;
import com.sangu.apptongji.main.alluser.order.avtivity.UOrderDetailTwoActivity;
import com.sangu.apptongji.main.alluser.order.avtivity.ZHuZhActivity;
import com.sangu.apptongji.main.qiye.JoinQiyeShfeiActivity;
import com.sangu.apptongji.main.qiye.QitePaidanActivity;
import com.sangu.apptongji.ui.BaseActivity;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by lishao on 2017-04-06.
 */

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    private IWXAPI api=null;

//    public MyItemClickListener mItemClickListener;
//
//    public interface MyItemClickListener {
//        void onItemClick(String result);
//    }
//    public  void setOnItemClickListener(MyItemClickListener listener){
//        this.mItemClickListener = listener;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Constant.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        Log.d("??????????????????>>req>>", "onPayFinish, errCode = " + req.toString());
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d("??????????????????>>resp>>", "onPayFinish, errCode = " + resp.errCode);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0){
                LayoutInflater inflaterDl = LayoutInflater.from(WXPayEntryActivity.this);
                RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.dialog_ok, null);
                final Dialog dialog = new AlertDialog.Builder(WXPayEntryActivity.this,R.style.Dialog).create();
                dialog.show();
                dialog.getWindow().setContentView(layout);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                TextView tv_title = (TextView) dialog.findViewById(R.id.title_tv);
                Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
                tv_title.setText("????????????");

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        SharedPreferences sp = getSharedPreferences("sangu_chongzhi", Context.MODE_PRIVATE);
                        String act = sp.getString("activity","ChongZhiActivity");
                        if ("ChongZhiActivity".equals(act)) {
                            startActivity(new Intent(WXPayEntryActivity.this, ChongZhiActivity.class).putExtra("zhifu","????????????"));
                        }else if ("BZJJNActivity".equals(act)){
                            startActivity(new Intent(WXPayEntryActivity.this, BZJJNActivity.class).putExtra("zhifu","????????????"));
                        }else if ("BZJZJActivity".equals(act)){
                            startActivity(new Intent(WXPayEntryActivity.this, BZJZJActivity.class).putExtra("zhifu","????????????"));
                        }else if ("ZHuZhActivity".equals(act)){
                            startActivity(new Intent(WXPayEntryActivity.this, ZHuZhActivity.class).putExtra("zhifu","????????????"));
                        }else if ("QiYeDetailsActivity".equals(act)){
                            SharedPreferences mSharedPreferences = getSharedPreferences("sangu_chongzhi_zhuangtai", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putString("zhuangtai","??????");
                            editor.commit();
                        }else if ("UOrderDetailActivity".equals(act)){
                            startActivity(new Intent(WXPayEntryActivity.this, UOrderDetailActivity.class).putExtra("zhifu","????????????"));
                        }else if ("UOrderDetailTwoActivity".equals(act)){
                            startActivity(new Intent(WXPayEntryActivity.this, UOrderDetailTwoActivity.class).putExtra("zhifu","????????????"));
                        }else if ("UOrderDetailThreeActivity".equals(act)){
                            startActivity(new Intent(WXPayEntryActivity.this, UOrderDetailThreeActivity.class).putExtra("zhifu","????????????"));
                        }else if ("QitePaidanActivity".equals(act)){
                            startActivity(new Intent(WXPayEntryActivity.this, QitePaidanActivity.class).putExtra("zhifu","????????????"));
                        }else if ("SettingSelfActivity".equals(act)){
                            startActivity(new Intent(WXPayEntryActivity.this, SettingSelfActivity.class).putExtra("zhifu","????????????"));
                        }else if ("JoinQiyeShfeiActivity".equals(act)){
                            startActivity(new Intent(WXPayEntryActivity.this, JoinQiyeShfeiActivity.class).putExtra("zhifu","????????????"));
                        }else if ("OfflineOrderActivity".equals(act)){
                            SharedPreferences mSharedPreferences = getSharedPreferences("sangu_chongzhi_zhuangtai", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putString("zhuangtai","??????");
                            editor.commit();
                        }else if ("MomentsPublishActivity".equals(act)){
                            SharedPreferences mSharedPreferences = getSharedPreferences("sangu_chongzhi_zhuangtai", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putString("zhuangtai","??????");
                            editor.commit();
                        }else if ("QianDingHetongActivity".equals(act)){
                            SharedPreferences mSharedPreferences = getSharedPreferences("sangu_chongzhi_zhuangtai", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putString("zhuangtai","??????");
                            editor.commit();
                        }else if ("MessageOrderTopActivity".equals(act)){
                            //????????????
                            SharedPreferences mSharedPreferences = getSharedPreferences("sangu_chongzhi_zhuangtai", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putString("zhuangtai","??????");
                            editor.commit();
                          //  startActivity(new Intent(WXPayEntryActivity.this, MessageOrderTopActivity.class).putExtra("zhifu","????????????"));
                        }else if ("SupportTopActivity".equals(act)){
                            //????????????
                            SharedPreferences mSharedPreferences = getSharedPreferences("sangu_chongzhi_zhuangtai", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putString("zhuangtai","??????");
                            editor.commit();
                           // startActivity(new Intent(SupportTopActivity.this, SupportFunctionAvtivity.class).putExtra("zhifu","????????????"));
                        }else if ("TopVipActivity".equals(act)){
                            //??????vip
                            SharedPreferences mSharedPreferences = getSharedPreferences("sangu_chongzhi_zhuangtai", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putString("zhuangtai","??????");
                            editor.commit();
                            startActivity(new Intent(WXPayEntryActivity.this, TopVipActivity.class).putExtra("zhifu","????????????"));
                        }else if ("CommentRewardActivity".equals(act)){
                            //????????????
                            SharedPreferences mSharedPreferences = getSharedPreferences("sangu_chongzhi_zhuangtai", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putString("zhuangtai","??????");
                            editor.commit();
                          //  mItemClickListener.onItemClick("????????????");
                            //  startActivity(new Intent(WXPayEntryActivity.this, TopVipActivity.class).putExtra("zhifu","????????????"));
                        }
                        finish();
                    }
                });
            }else if (resp.errCode == -2){
                LayoutInflater inflaterDl = LayoutInflater.from(WXPayEntryActivity.this);
                RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.dialog_ok, null);
                final Dialog dialog = new AlertDialog.Builder(WXPayEntryActivity.this,R.style.Dialog).create();
                dialog.show();
                dialog.getWindow().setContentView(layout);
                dialog.setCanceledOnTouchOutside(false);
                TextView tv_title = (TextView) dialog.findViewById(R.id.title_tv);
                Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
                tv_title.setText("????????????");
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();
                    }
                });
            }else {
                Toast.makeText(WXPayEntryActivity.this,"????????????????????????",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}

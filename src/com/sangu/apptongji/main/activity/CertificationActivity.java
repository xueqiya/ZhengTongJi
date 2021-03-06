package com.sangu.apptongji.main.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.fanxin.easeui.EaseConstant;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sangu.apptongji.Constant;
import com.sangu.apptongji.DemoHelper;
import com.sangu.apptongji.R;
import com.sangu.apptongji.main.FXConstant;
import com.sangu.apptongji.main.utils.MySingleton;
import com.sangu.apptongji.main.utils.OkHttpManager;
import com.sangu.apptongji.main.utils.Param;
import com.sangu.apptongji.main.utils.ToastUtils;
import com.sangu.apptongji.main.widget.CustomProgressDialog;
import com.sangu.apptongji.ui.BaseActivity;
import com.sangu.apptongji.ui.ChatActivity;
import com.yalantis.ucrop.entity.LocalMedia;

import org.json.JSONException;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017-04-05.
 */

public class CertificationActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_name=null;
    private EditText et_shenfenzheng=null;
    private EditText et_yinhangka=null;
    private EditText et_phone=null;
    private EditText et_kaihuhang=null;
    private ImageView iv_zhengmian=null;
    private ImageView iv_hand_zhengmian=null;
    private ImageView iv_hand_del2=null;
    private ImageView iv_hand_fanmian=null;
    private ImageView iv_hand_del1=null;
    private ImageView iv_del1=null;
    private ImageView iv_fanmian=null;
    private ImageView iv_del2=null;
    private TextView tv_zhuangtai=null;
    private TextView tv_error=null;
    private TextView tv_xiugai=null;
    private TextView tv_kefu=null;
    private Button btn_commit=null;
    String path1=null,path2=null,path3=null,path4=null;
    private int zhengfan;
    private CustomProgressDialog mProgress=null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgress!=null){
            if (mProgress.isShowing()){
                mProgress.dismiss();
            }
        }
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_shiming_renzheng);
        WeakReference<CertificationActivity> reference =  new WeakReference<CertificationActivity>(CertificationActivity.this);
        RequestQueue queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        mProgress = CustomProgressDialog.createDialog(reference.get());
        mProgress.setMessage("??????????????????...");
        mProgress.setCancelable(true);
        mProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mProgress.dismiss();
            }
        });
        mProgress.show();
        initView();
        setlistener();
        queryRenzheng();
    }

    private void queryRenzheng() {
        String url = FXConstant.URL_CHAXUN_RENZHENG;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (mProgress!=null){
                    if (mProgress.isShowing()){
                        mProgress.dismiss();
                    }
                }
                Log.d("chen", "onResponse" + s);
                try {
                    org.json.JSONObject object = new org.json.JSONObject(s);
                    String code = object.getString("code");
                    if (code.equals("SUCCESS")){
                        org.json.JSONObject object1 = object.getJSONObject("list");
                        if (object1!=null&&!"".equals(object1)){
                            String userName = object1.getString("userName");
                            String uId = object1.getString("uId");
                            String bankCard = object1.getString("bankCard");
                            String phoneNumber = object1.getString("phoneNumber");
                            String bankName = object1.getString("bankName");
                            String image1 = object1.getString("image1");
                            String image2 = object1.getString("image2");
                            String image3 = object1.getString("image3");
                            String image4 = object1.getString("image4");
                            String id = object1.getString("id");
                            String examine = object1.getString("examine");
                            String reason = object1.getString("reason");
                            if ("????????????".equals(examine)||"????????????".equals(examine)) {
                                et_name.setText(userName);
                                et_shenfenzheng.setText(id);
                              //  et_yinhangka.setText(bankCard);
                                et_phone.setText(phoneNumber);
                              //  et_kaihuhang.setText(bankName);
                                ImageLoader.getInstance().displayImage(FXConstant.URL_RENZHENG_TUPIAN+image1,iv_zhengmian);
                                ImageLoader.getInstance().displayImage(FXConstant.URL_RENZHENG_TUPIAN+image2,iv_fanmian);
                                ImageLoader.getInstance().displayImage(FXConstant.URL_RENZHENG_TUPIAN+image3,iv_hand_zhengmian);
                                ImageLoader.getInstance().displayImage(FXConstant.URL_RENZHENG_TUPIAN+image4,iv_hand_fanmian);
                                btn_commit.setVisibility(View.INVISIBLE);
                                tv_zhuangtai.setVisibility(View.VISIBLE);
                                tv_zhuangtai.setText("???????????????"+examine);
                                et_name.setEnabled(false);
                              //  et_kaihuhang.setEnabled(false);
                                et_shenfenzheng.setEnabled(false);
                               // et_yinhangka.setEnabled(false);
                                et_phone.setEnabled(false);
                                btn_commit.setEnabled(false);
                                iv_zhengmian.setEnabled(false);
                                iv_fanmian.setEnabled(false);
                                iv_hand_zhengmian.setEnabled(false);
                                iv_hand_fanmian.setEnabled(false);
                            }else {
                                et_name.setText(userName);
                                et_shenfenzheng.setText(id);
                              //  et_yinhangka.setText(bankCard);
                                et_phone.setText(phoneNumber);
                              //  et_kaihuhang.setText(bankName);
                                ImageLoader.getInstance().displayImage(FXConstant.URL_RENZHENG_TUPIAN+image1,iv_zhengmian);
                                ImageLoader.getInstance().displayImage(FXConstant.URL_RENZHENG_TUPIAN+image2,iv_fanmian);
                                ImageLoader.getInstance().displayImage(FXConstant.URL_RENZHENG_TUPIAN+image3,iv_hand_zhengmian);
                                ImageLoader.getInstance().displayImage(FXConstant.URL_RENZHENG_TUPIAN+image4,iv_hand_fanmian);
                                btn_commit.setVisibility(View.INVISIBLE);
                                tv_zhuangtai.setVisibility(View.VISIBLE);
                                tv_zhuangtai.setText("???????????????"+examine);
                                tv_error.setText("???????????????" + reason);
                                tv_error.setVisibility(View.VISIBLE);
                                tv_xiugai.setVisibility(View.VISIBLE);
                                tv_xiugai.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        updateRenzheng();
                                    }
                                });
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError!=null) {
                    Log.e("cerfication", volleyError.getMessage());
                    Log.d("chen", "cerfication" + volleyError.getMessage());
                }
                Toast.makeText(CertificationActivity.this,"??????????????????",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("uId",DemoHelper.getInstance().getCurrentUsernName());
                return param;
            }
        };
        MySingleton.getInstance(CertificationActivity.this).addToRequestQueue(request);
    }

    private void updateRenzheng() {
        String shenfenzheng = et_shenfenzheng.getText().toString().trim();
       // String yinhangka = et_yinhangka.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
       // String kaihuhang = et_kaihuhang.getText().toString().trim();
        String name = et_name.getText().toString().trim();

        if (TextUtils.isEmpty(shenfenzheng)) {
            ToastUtils.showNOrmalToast(CertificationActivity.this.getApplicationContext(),"?????????????????????");
            return;
        }
//        if (TextUtils.isEmpty(yinhangka)) {
//            ToastUtils.showNOrmalToast(CertificationActivity.this.getApplicationContext(),"?????????????????????");
//            return;
//        }
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showNOrmalToast(CertificationActivity.this.getApplicationContext(),"?????????????????????");
            return;
        }
//        if (TextUtils.isEmpty(kaihuhang)) {
//            ToastUtils.showNOrmalToast(CertificationActivity.this.getApplicationContext(),"?????????????????????");
//            return;
//        }
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showNOrmalToast(CertificationActivity.this.getApplicationContext(),"??????????????????");
            return;
        }
        List<Param> param=new ArrayList<>();
        param.add(new Param("userName",name));
        param.add(new Param("examine","????????????"));
        param.add(new Param("ID", shenfenzheng));
       // param.add(new Param("bankCard",yinhangka));
        param.add(new Param("phoneNumber", phone));
       // param.add(new Param("bankName", kaihuhang));
        param.add(new Param("uId", DemoHelper.getInstance().getCurrentUsernName()));
        Log.d("chen", name + shenfenzheng  + phone + DemoHelper.getInstance().getCurrentUsernName() + "");
        final List<File> files1 = new ArrayList<File>();
        if (path1!=null&&!"".equals(path1)) {
            File file1 = null;
            file1 = new File(path1);
            if (file1.exists()) {
                files1.add(file1);
            }
        }
        final List<File> files2 = new ArrayList<File>();
        if (path2!=null&&!"".equals(path2)) {
            File file = null;
            file = new File(path2);
            if (file.exists()) {
                files2.add(file);
            }
        }
        final List<File> files3 = new ArrayList<File>();
        if (path3!=null&&!"".equals(path3)) {
            File file3 = null;
            file3 = new File(path3);
            if (file3.exists()) {
                files3.add(file3);
            }
        }
        final List<File> files4 = new ArrayList<File>();
        if (path4!=null&&!"".equals(path4)) {
            File file4 = null;
            file4 = new File(path4);
            if (file4.exists()) {
                files4.add(file4);
            }
        }
        String str1 = "image1";
        String str2 = "image2";
        String str3 = "image3";
        String str4 = "image4";
        OkHttpManager.getInstance().postTfile(param,str1,files1,str2,files2,str3,files3,str4,files4, FXConstant.URL_XIUGAI_RENZHENG,new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("chen", "updateRenzheng onResponse" + jsonObject.toString());
                String code = jsonObject.getString("code");
                if (mProgress!=null){
                    if (mProgress.isShowing()){
                        mProgress.dismiss();
                    }
                }
                if (code.equals("SUCCESS")) {
                    Toast.makeText(CertificationActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    btn_commit.setVisibility(View.INVISIBLE);
                    tv_zhuangtai.setVisibility(View.VISIBLE);
                    tv_zhuangtai.setText("???????????????????????????");
                    et_name.setEnabled(false);
                    et_shenfenzheng.setEnabled(false);
                //    et_yinhangka.setEnabled(false);
                    et_phone.setEnabled(false);
                 //   et_kaihuhang.setEnabled(false);
                    iv_fanmian.setEnabled(false);
                    iv_hand_fanmian.setEnabled(false);
                    iv_zhengmian.setEnabled(false);
                    iv_hand_zhengmian.setEnabled(false);
                    tv_xiugai.setVisibility(View.INVISIBLE);
                } else {
                    Log.e("??????????????????","code:"+code);
                    Toast.makeText(CertificationActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(String errorMsg) {
                if (mProgress!=null){
                    if (mProgress.isShowing()){
                        mProgress.dismiss();
                    }
                }
                Log.d("chen", "updateRenzheng onFailure" + errorMsg);
                Log.e("??????????????????",errorMsg);
                Toast.makeText(CertificationActivity.this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setlistener() {
        TextChange textChange = new TextChange();
        et_shenfenzheng.addTextChangedListener(textChange);
        et_phone.addTextChangedListener(textChange);
      //  et_yinhangka.addTextChangedListener(textChange);
     //   et_kaihuhang.addTextChangedListener(textChange);
        iv_zhengmian.setOnClickListener(this);
        iv_hand_zhengmian.setOnClickListener(this);
        iv_fanmian.setOnClickListener(this);
        iv_hand_fanmian.setOnClickListener(this);
        btn_commit.setOnClickListener(this);
        iv_del1.setOnClickListener(this);
        iv_hand_del1.setOnClickListener(this);
        iv_del2.setOnClickListener(this);
        iv_hand_del2.setOnClickListener(this);
        tv_kefu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent11 = new Intent(CertificationActivity.this, ChatActivity.class);
                intent11.putExtra("userId", "18337101357");
                intent11.putExtra("chatType", Constant.CHATTYPE_SINGLE);
                intent11.putExtra(EaseConstant.EXTRA_USER_TYPE, "??????");
                intent11.putExtra(EaseConstant.EXTRA_USER_IMG,"zhengshiduo.png");
                intent11.putExtra(EaseConstant.EXTRA_USER_NAME,"???????????????");
                intent11.putExtra(EaseConstant.EXTRA_USER_SHARERED,"???");
                startActivity(intent11);
            }
        });
    }

    private void initView() {
        et_name = (EditText) findViewById(R.id.et_name);
        et_shenfenzheng = (EditText) findViewById(R.id.et_shenfenzheng);
      //  et_yinhangka = (EditText) findViewById(R.id.et_yinhangka);
        et_phone = (EditText) findViewById(R.id.et_phone);
      //  et_kaihuhang = (EditText) findViewById(R.id.et_kaihuhang);
        iv_zhengmian = (ImageView) findViewById(R.id.iv_zhengmian);
        iv_hand_zhengmian = (ImageView) findViewById(R.id.iv_hand_zhengmian);
        iv_hand_del2 = (ImageView) findViewById(R.id.iv_hand_del2);
        iv_hand_fanmian = (ImageView) findViewById(R.id.iv_hand_fanmian);
        iv_hand_del1 = (ImageView) findViewById(R.id.iv_hand_del1);
        iv_del1 = (ImageView) findViewById(R.id.iv_del1);
        iv_del2 = (ImageView) findViewById(R.id.iv_del2);
        iv_fanmian = (ImageView) findViewById(R.id.iv_fanmian);
        tv_zhuangtai = (TextView) findViewById(R.id.tv_zhuangtai);
        tv_error = (TextView) findViewById(R.id.tv_error);
        tv_xiugai = (TextView) findViewById(R.id.tv_xiugai);
        tv_kefu = (TextView) findViewById(R.id.tv_kefu);
        btn_commit = (Button) findViewById(R.id.btn_commit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_del1:
                iv_zhengmian.setImageResource(R.drawable.fx_icon_add);
                iv_del1.setVisibility(View.GONE);
                break;
            case R.id.iv_del2:
                iv_fanmian.setImageResource(R.drawable.fx_icon_add);
                iv_del2.setVisibility(View.GONE);
                break;
            case R.id.iv_hand_del1:
                iv_hand_zhengmian.setImageResource(R.drawable.fx_icon_add);
                iv_hand_del1.setVisibility(View.GONE);
                break;
            case R.id.iv_hand_del2:
                iv_hand_fanmian.setImageResource(R.drawable.fx_icon_add);
                iv_hand_del2.setVisibility(View.GONE);
                break;
            case R.id.iv_zhengmian:
                zhengfan = 1;
                goCamera();
                break;
            case R.id.iv_fanmian:
                zhengfan = 2;
                goCamera();
                break;
            case R.id.iv_hand_zhengmian:
                zhengfan = 3;
                goCamera();
                break;
            case R.id.iv_hand_fanmian:
                zhengfan = 4;
                goCamera();
                break;
            case R.id.btn_commit:
                if (path1==null||"".equals(path1)){
                    Toast.makeText(CertificationActivity.this,"????????????????????????!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (path2==null||"".equals(path2)){
                    Toast.makeText(CertificationActivity.this,"????????????????????????!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (path3==null||"".equals(path3)){
                    Toast.makeText(CertificationActivity.this,"??????????????????????????????!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (path4==null||"".equals(path4)){
                    Toast.makeText(CertificationActivity.this,"??????????????????????????????!",Toast.LENGTH_SHORT).show();
                    return;
                }
                LayoutInflater inflater2 = LayoutInflater.from(CertificationActivity.this);
                RelativeLayout layout2 = (RelativeLayout) inflater2.inflate(R.layout.dialog_alert, null);
                final Dialog dialog2 = new AlertDialog.Builder(CertificationActivity.this).create();
                dialog2.show();
                dialog2.getWindow().setContentView(layout2);
                dialog2.setCanceledOnTouchOutside(true);
                dialog2.setCancelable(true);
                TextView title_tv2 = (TextView) layout2.findViewById(R.id.title_tv);
                Button btnCancel2 = (Button) layout2.findViewById(R.id.btn_cancel);
                final Button btnOK2 = (Button) layout2.findViewById(R.id.btn_ok);
                btnOK2.setText("??????");
                btnCancel2.setText("??????");
                title_tv2.setText("????????????????????????");
                btnCancel2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                    }
                });
                btnOK2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                        if (mProgress!=null){
                            if (!mProgress.isShowing()){
                                mProgress.setMessage("????????????...");
                                mProgress.show();
                            }
                        }
                        tijiao();
                    }
                });
                break;
        }
    }

    private void goCamera(){
        WeakReference<CertificationActivity> reference = new WeakReference<CertificationActivity>(CertificationActivity.this);
        FunctionOptions options = new FunctionOptions.Builder()
                .setType(FunctionConfig.TYPE_IMAGE) // ??????or?????? FunctionConfig.TYPE_IMAGE  TYPE_VIDEO
                .setCropMode(1) // ???????????? ?????????1:1???3:4???3:2???16:9
//                            .setOffsetX() // ?????????????????????
//                            .setOffsetY() // ?????????????????????
                .setCompress(false) //????????????
                .setEnablePixelCompress(true) //????????????????????????
                .setEnableQualityCompress(true) //?????????????????????
                .setMaxSelectNum(8) // ????????????????????????
                .setMinSelectNum(0)// ?????????????????????????????????????????????????????????
                .setSelectMode(FunctionConfig.MODE_SINGLE) // ?????? or ??????
                .setShowCamera(true) //???????????????????????? ??????????????????type ????????????????????????
                .setEnablePreview(true) // ????????????????????????
                .setEnableCrop(false) // ????????????????????????
                .setCircularCut(false)// ????????????????????????
                .setPreviewVideo(true) // ??????????????????(??????) mode or ????????????
                .setCheckedBoxDrawable(0)
                .setRecordVideoDefinition(FunctionConfig.HIGH) // ???????????????
                .setRecordVideoSecond(60) // ????????????
                .setCustomQQ_theme(0)// ????????????QQ?????????????????????????????????????????????
                .setGif(false)// ????????????gif????????????????????????
                .setCropW(0) // cropW-->???????????? ???????????????100  ????????????????????????????????? ?????????????????????
                .setCropH(0) // cropH-->???????????? ???????????????100 ????????????????????????????????? ?????????????????????
                .setMaxB(102400) // ??????????????? ??????:200kb  ?????????202400???202400 / 1024 = 200kb
                .setPreviewColor(ContextCompat.getColor(reference.get(), R.color.blue)) //??????????????????
                .setCompleteColor(ContextCompat.getColor(reference.get(), R.color.blue)) //?????????????????????
                .setPreviewBottomBgColor(0) //???????????????????????????
                .setPreviewTopBgColor(0)//???????????????????????????
                .setBottomBgColor(0) //???????????????????????????
                .setGrade(Luban.THIRD_GEAR) // ???????????? ????????????
                .setCheckNumMode(false)
                .setCompressQuality(100) // ??????????????????,????????????
                .setImageSpanCount(4) // ????????????
                .setVideoS(0)// ??????????????????????????? ??????:???
                .setSelectMedia(null) // ?????????????????????????????????????????????????????????????????????
                .setCompressFlag(1) // 1 ?????????????????? 2 luban??????
                .setCompressW(0) // ????????? ???????????????????????????????????????
                .setCompressH(0) // ????????? ???????????????????????????????????????
                .setThemeStyle(ContextCompat.getColor(reference.get(), R.color.blue)) // ??????????????????
                .setNumComplete(false) // 0/9 ??????  ??????
                .setClickVideo(false)// ????????????
                .setFreeStyleCrop(false) // ????????????????????????????????????
                .create();
        // ??????????????????????????????????????????
        PictureConfig.getInstance().init(options).openPhoto(reference.get(), resultCallback);
    }

    /**
     * ??????????????????
     */
    private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {

        }

        @Override
        public void onSelectSuccess(LocalMedia media) {
            // ????????????
            if (zhengfan==2){
                path2 = media.getPath();
                File file2 = new File(path2);
                Glide.with(CertificationActivity.this).load(file2).into(iv_fanmian);
                //iv_fanmian.setImageURI(Uri.fromFile(new File(path2)));
                iv_del2.setVisibility(View.VISIBLE);
            }else if(zhengfan == 1) {
                path1 = media.getPath();
                File file2 = new File(path1);
                Glide.with(CertificationActivity.this).load(file2).into(iv_zhengmian);
                //iv_zhengmian.setImageURI(Uri.fromFile(new File(path1)));
                iv_del1.setVisibility(View.VISIBLE);
            }else if(zhengfan == 3) {
                //????????????
                path3 = media.getPath();
                File file2 = new File(path3);
                Glide.with(CertificationActivity.this).load(file2).into(iv_hand_zhengmian);
                //iv_hand_zhengmian.setImageURI(Uri.fromFile(new File(path3)));
                iv_hand_del1.setVisibility(View.VISIBLE);
            } else if(zhengfan == 4) {
                path4 = media.getPath();
                File file2 = new File(path4);
                Glide.with(CertificationActivity.this).load(file2).into(iv_hand_fanmian);
               // iv_hand_fanmian.setImageURI(Uri.fromFile(new File(path4)));
                iv_hand_del2.setVisibility(View.VISIBLE);
            }
        }
    };

    private void tijiao() {
        String shenfenzheng = et_shenfenzheng.getText().toString().trim();
       // String yinhangka = et_yinhangka.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
      //  String kaihuhang = et_kaihuhang.getText().toString().trim();
        String name = et_name.getText().toString().trim();
        if (TextUtils.isEmpty(shenfenzheng)) {
            ToastUtils.showNOrmalToast(CertificationActivity.this.getApplicationContext(),"?????????????????????");
            return;
        }
//        if (TextUtils.isEmpty(yinhangka)) {
//            ToastUtils.showNOrmalToast(CertificationActivity.this.getApplicationContext(),"?????????????????????");
//            return;
//        }
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showNOrmalToast(CertificationActivity.this.getApplicationContext(),"?????????????????????");
            return;
        }
//        if (TextUtils.isEmpty(kaihuhang)) {
//            ToastUtils.showNOrmalToast(CertificationActivity.this.getApplicationContext(),"?????????????????????");
//            return;
//        }
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showNOrmalToast(CertificationActivity.this.getApplicationContext(),"??????????????????");
            return;
        }
        List<Param> param=new ArrayList<>();
        param.add(new Param("userName",name));
        param.add(new Param("ID", shenfenzheng));
      //  param.add(new Param("bankCard",yinhangka));
        param.add(new Param("phoneNumber", phone));
      //  param.add(new Param("bankName", kaihuhang));
        param.add(new Param("uId", DemoHelper.getInstance().getCurrentUsernName()));
        final List<File> files1 = new ArrayList<File>();
        File file1 = null;
        file1 = new File(path1);
        if (file1.exists()) {
            files1.add(file1);
        }
        final List<File> files2 = new ArrayList<File>();
        File file = null;
        file = new File(path2);
        if (file.exists()) {
            files2.add(file);
        }

        final List<File> files3 = new ArrayList<File>();
        if (path3!=null&&!"".equals(path3)) {
            File file3 = null;
            file3 = new File(path3);
            if (file3.exists()) {
                files3.add(file3);
            }
        }
        final List<File> files4 = new ArrayList<File>();
        if (path4!=null&&!"".equals(path4)) {
            File file4 = null;
            file4 = new File(path4);
            if (file4.exists()) {
                files4.add(file4);
            }
        }
        String str1 = "image1";
        String str2 = "image2";
        String str3 = "image3";
        String str4 = "image4";
        if (files1.size()>0&&files2.size()>0&&files3.size()>0&&files4.size()>0) {
            OkHttpManager.getInstance().postTfile(param,str1,files1,str2,files2,str3,files3,str4,files4, FXConstant.URL_SHIMING_RENZHENG,new OkHttpManager.HttpCallBack() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    Log.d("chen", "??????" + jsonObject.toString());
                    String code = jsonObject.getString("code");
                    if (mProgress!=null){
                        if (mProgress.isShowing()){
                            mProgress.dismiss();
                        }
                    }
                    if (code.equals("SUCCESS")) {
                        Toast.makeText(CertificationActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                        btn_commit.setVisibility(View.INVISIBLE);
                        tv_zhuangtai.setVisibility(View.VISIBLE);
                      //  tv_error.setVisibility(View.VISIBLE);
                        et_name.setEnabled(false);
                        et_shenfenzheng.setEnabled(false);
                     //   et_yinhangka.setEnabled(false);
                        et_phone.setEnabled(false);
                     //   et_kaihuhang.setEnabled(false);
                        iv_fanmian.setEnabled(false);
                        iv_zhengmian.setEnabled(false);
                        iv_hand_fanmian.setEnabled(false);
                        iv_hand_zhengmian.setEnabled(false);
                    } else {
                        Log.e("??????????????????","code:"+code);
                        Toast.makeText(CertificationActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(String errorMsg) {
                    if (mProgress!=null){
                        if (mProgress.isShowing()){
                            mProgress.dismiss();
                        }
                    }
                    Log.d("chen", "??????onFailure" + errorMsg);
                    Log.e("??????????????????",errorMsg);
                    Toast.makeText(CertificationActivity.this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            if (mProgress!=null){
                if (mProgress.isShowing()){
                    mProgress.dismiss();
                }
            }
            Toast.makeText(CertificationActivity.this, "???????????????????????????????????????????????????????????????" , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FunctionConfig.CAMERA_RESULT:
                    if (data != null) {
                        List<LocalMedia> medias = (List<LocalMedia>) data.getSerializableExtra(FunctionConfig.EXTRA_RESULT);
                        if (zhengfan==2){
                            path2 = medias.get(0).getPath();
                            File file2 = new File(path2);
                            Glide.with(CertificationActivity.this).load(file2).into(iv_fanmian);
                            //iv_fanmian.setImageURI(Uri.fromFile(new File(path2)));
                        }else if (zhengfan == 1){
                            path1 = medias.get(0).getPath();
                            File file2 = new File(path1);
                            Glide.with(CertificationActivity.this).load(file2).into(iv_zhengmian);
                            //iv_zhengmian.setImageURI(Uri.fromFile(new File(path1)));
                        }else if (zhengfan == 3){
                            path3 = medias.get(0).getPath();
                            File file2 = new File(path3);
                            Glide.with(CertificationActivity.this).load(file2).into(iv_hand_zhengmian);
                            //iv_hand_zhengmian.setImageURI(Uri.fromFile(new File(path3)));
                        }else if (zhengfan == 4){
                            path4 = medias.get(0).getPath();
                            File file2 = new File(path4);
                            Glide.with(CertificationActivity.this).load(file2).into(iv_hand_fanmian);
                            //iv_hand_fanmian.setImageURI(Uri.fromFile(new File(path4)));
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void back(View view) {
        super.back(view);
    }

    // EditText?????????
    class TextChange implements TextWatcher {
        @Override
        public void afterTextChanged(Editable arg0) {

        }
        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
        }
        @Override
        public void onTextChanged(CharSequence cs, int start, int before,
                                  int count) {
            boolean Sign1=false,Sign2 = false,Sign3=false;
            String shenfenzheng = et_shenfenzheng.getText().toString().trim();
          //  String yinhangka = et_yinhangka.getText().toString().trim();
            String phone = et_phone.getText().toString().trim();
            try {
                if (shenfenzheng.length()>14) {
                    Sign1 = IDCardValidate(shenfenzheng);
                }
//                if (yinhangka.length()>14) {
//                    Sign2 = checkBankCard(yinhangka);
//                }
                if (phone.length() == 11) {
                    Sign3 = true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.d("chen", Sign1 + "" + Sign2+ "" + Sign3 + "");
            if (Sign1 & Sign3) {
                btn_commit.setEnabled(true);
                btn_commit.setBackgroundResource(R.drawable.fx_bg_btn_green);
            }
            // ???layout???????????????Button???text???????????????????????????????????????????????????????????????Button???????????????
            else {
                btn_commit.setEnabled(false);
                btn_commit.setBackgroundResource(R.drawable.fx_bg_btn_gray);
            }
        }

    }
    /**
     * ???????????????????????????
     * @param cardId
     * @return
     */
    private boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId
                .substring(0, cardId.length() - 1));
        Log.e("????????????????????????,bit",bit+"");
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }//
    private char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null
                || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            // ??????????????????????????????N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    /**
     * ????????????????????????
     * @param phone
     * @return
     */
    private boolean checkPhone(String phone) {
        Pattern pattern = Pattern
                .compile("^(13[0-9]|15[0-9]|153|15[6-9]|180|18[23]|18[5-9])\\d{8}$");
        Matcher matcher = pattern.matcher(phone);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * ?????????????????????????????????
     *
     * @param IDStr
     *            ????????????
     * @return true ?????????false ??????
     * @throws ParseException
     */
    public static boolean IDCardValidate(String IDStr) throws ParseException {
        String[] ValCodeArr = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };
        String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2" };
        String Ai = "";
        // ================ ???????????????18??? ================
        if (IDStr.length() != 18) {
            return false;
        }
        // ================ ?????? ??????????????????????????? ================
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        }
        if (isNumeric(Ai) == false) {
            //errorInfo = "?????????15???????????????????????? ; 18????????????????????????????????????????????????";
            return false;
        }
        // ================ ???????????????????????? ================
        String strYear = Ai.substring(6, 10);// ??????
        String strMonth = Ai.substring(10, 12);// ??????
        String strDay = Ai.substring(12, 14);// ???
        if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
//          errorInfo = "????????????????????????";
            return false;
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150 || (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
                //errorInfo = "????????????????????????????????????";
                return false;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            //errorInfo = "?????????????????????";
            return false;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            //errorInfo = "?????????????????????";
            return false;
        }
        // ================ ????????????????????? ================
        Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            //errorInfo = "??????????????????????????????";
            return false;
        }
        // ================ ???????????????????????? ================
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (IDStr.length() == 18) {
            if (Ai.equals(IDStr) == false) {
                //errorInfo = "????????????????????????????????????????????????";
                return false;
            }
        } else {
            return true;
        }
        return true;
    }

    /**
     * ???????????????????????????
     *
     * @return Hashtable ??????
     */
    @SuppressWarnings("unchecked")
    private static Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "??????");
        hashtable.put("12", "??????");
        hashtable.put("13", "??????");
        hashtable.put("14", "??????");
        hashtable.put("15", "?????????");
        hashtable.put("21", "??????");
        hashtable.put("22", "??????");
        hashtable.put("23", "?????????");
        hashtable.put("31", "??????");
        hashtable.put("32", "??????");
        hashtable.put("33", "??????");
        hashtable.put("34", "??????");
        hashtable.put("35", "??????");
        hashtable.put("36", "??????");
        hashtable.put("37", "??????");
        hashtable.put("41", "??????");
        hashtable.put("42", "??????");
        hashtable.put("43", "??????");
        hashtable.put("44", "??????");
        hashtable.put("45", "??????");
        hashtable.put("46", "??????");
        hashtable.put("50", "??????");
        hashtable.put("51", "??????");
        hashtable.put("52", "??????");
        hashtable.put("53", "??????");
        hashtable.put("54", "??????");
        hashtable.put("61", "??????");
        hashtable.put("62", "??????");
        hashtable.put("63", "??????");
        hashtable.put("64", "??????");
        hashtable.put("65", "??????");
//      hashtable.put("71", "??????");
//      hashtable.put("81", "??????");
//      hashtable.put("82", "??????");
//      hashtable.put("91", "??????");
        return hashtable;
    }

    /**
     * ???????????????????????????????????????
     *
     * @param str
     * @return
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param
     * @return
     */
    public static boolean isDate(String strDate) {
        Pattern pattern = Pattern
                .compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * ?????????????????????????????????
     *
     * @param IDStr
     *            ????????????
     * @return ???????????????"" ???????????????String??????
     * @throws ParseException
     *//*
    private boolean IDCardValidate(String IDStr) throws ParseException {
        String errorInfo = "??????";// ??????????????????
        String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4",
                "3", "2" };
        String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
                "9", "10", "5", "8", "4", "2" };
        String Ai = "";
        // ================ ??????????????? 15??????18??? ================
        if (IDStr.length() != 15 && IDStr.length() != 18) {
            errorInfo = "??????????????????????????????15??????18??????";
            Log.e("????????????????????????",errorInfo);
            return false;
        }
        // =======================(end)========================

        // ================ ?????? ??????????????????????????? ================
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        } else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        if (isNumeric(Ai) == false) {
            errorInfo = "?????????15???????????????????????? ; 18????????????????????????????????????????????????";
            Log.e("????????????????????????",errorInfo);
            return false;
        }
        // =======================(end)========================

        // ================ ???????????????????????? ================
        String strYear = Ai.substring(6, 10);// ??????
        String strMonth = Ai.substring(10, 12);// ??????
        String strDay = Ai.substring(12, 14);// ??????
        if (isDataFormat(strYear + "-" + strMonth + "-" + strDay) == false) {
            errorInfo = "????????????????????????";
            Log.e("????????????????????????",errorInfo);
            return false;
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                    || (gc.getTime().getTime() - s.parse(
                    strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
                errorInfo = "????????????????????????????????????";
                Log.e("????????????????????????",errorInfo);
                return false;
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            errorInfo = "?????????????????????";
            Log.e("????????????????????????",errorInfo);
            return false;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            errorInfo = "?????????????????????";
            Log.e("????????????????????????",errorInfo);
            return false;
        }
        // =====================(end)=====================
        // ================ ????????????????????? ================
        Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            errorInfo = "??????????????????????????????";
            Log.e("????????????????????????",errorInfo);
            return false;
        }
        // ==============================================

        // ================ ???????????????????????? ================
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi
                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))
                    * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (IDStr.length() == 18) {
            if (Ai.equals(IDStr) == false) {
                errorInfo = "????????????????????????????????????????????????";
                Log.e("????????????????????????",errorInfo);
                return false;
            }
        } else {
            Log.e("????????????????????????,1",errorInfo);
            return true;
        }
        // =====================(end)=====================
        Log.e("????????????????????????,2",errorInfo);
        return true;
    }
    *//**
     * ???????????????????????????????????????
     *
     * @param str
     * @return
     *//*
    private boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    *//**
     * ???????????????????????????
     *
     * @return Hashtable ??????
     *//*
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "??????");
        hashtable.put("12", "??????");
        hashtable.put("13", "??????");
        hashtable.put("14", "??????");
        hashtable.put("15", "?????????");
        hashtable.put("21", "??????");
        hashtable.put("22", "??????");
        hashtable.put("23", "?????????");
        hashtable.put("31", "??????");
        hashtable.put("32", "??????");
        hashtable.put("33", "??????");
        hashtable.put("34", "??????");
        hashtable.put("35", "??????");
        hashtable.put("36", "??????");
        hashtable.put("37", "??????");
        hashtable.put("41", "??????");
        hashtable.put("42", "??????");
        hashtable.put("43", "??????");
        hashtable.put("44", "??????");
        hashtable.put("45", "??????");
        hashtable.put("46", "??????");
        hashtable.put("50", "??????");
        hashtable.put("51", "??????");
        hashtable.put("52", "??????");
        hashtable.put("53", "??????");
        hashtable.put("54", "??????");
        hashtable.put("61", "??????");
        hashtable.put("62", "??????");
        hashtable.put("63", "??????");
        hashtable.put("64", "??????");
        hashtable.put("65", "??????");
        hashtable.put("71", "??????");
        hashtable.put("81", "??????");
        hashtable.put("82", "??????");
        hashtable.put("91", "??????");
        return hashtable;
    }

    *//**
     * ??????????????????????????????YYYY-MM-DD??????
     *
     * @param str
     * @return
     *//*
    private boolean isDataFormat(String str) {
        boolean flag = false;
        // String
        // regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";
        String regxStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
        Pattern pattern1 = Pattern.compile(regxStr);
        Matcher isNo = pattern1.matcher(str);
        if (isNo.matches()) {
            flag = true;
        }
        return flag;
    }*/
}

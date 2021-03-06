package com.sangu.apptongji.main.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sangu.apptongji.DemoHelper;
import com.sangu.apptongji.R;
import com.sangu.apptongji.main.FXConstant;
import com.sangu.apptongji.main.alluser.entity.Userful;
import com.sangu.apptongji.main.alluser.presenter.IUAZPresenter;
import com.sangu.apptongji.main.alluser.presenter.impl.UAZPresenter;
import com.sangu.apptongji.main.alluser.view.IUAZView;
import com.sangu.apptongji.main.utils.MySingleton;
import com.sangu.apptongji.main.utils.OkHttpManager;
import com.sangu.apptongji.main.utils.Param;
import com.sangu.apptongji.main.utils.SoundPlayUtils;
import com.sangu.apptongji.ui.BaseActivity;
import com.sangu.apptongji.utils.WeiboDialogUtils;
import com.yalantis.ucrop.entity.LocalMedia;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZBAuthTaskActivity extends BaseActivity implements IUAZView {


    private TextView tv_pushComent,tv_midBtn;
    private ImageView commentImage;
    private IUAZPresenter uazPresenter;

    private ArrayList<String> imagePaths1=new ArrayList<>();
    private ArrayList<String> imagePaths2=new ArrayList<>();
    private List<LocalMedia> selectMedia1 = new ArrayList<>();
    private List<LocalMedia> selectMedia2 = new ArrayList<>();

    private Dialog mWeiboDialog;

    private LinearLayout ll_fristType,ll_secondType,ll_thridType;

    private TextView tv_pushDown,tv_midBtn2,tv_midBtn3;
    private ImageView image_downImage1,image_downImage2,wechatImage;

    private String currentTag="";//
    private TextView tv_example;

    private TextView tv_searchKey;
    private ImageView image_logo;

    private String keyWord="";

    private String wechatAuth = "0";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setContentView(R.layout.activity_zbauthtask);

        ll_fristType = (LinearLayout) findViewById(R.id.ll_fristType);
        ll_secondType = (LinearLayout) findViewById(R.id.ll_secondType);
        ll_thridType = (LinearLayout) findViewById(R.id.ll_thridType);

        String type = getIntent().getStringExtra("type");

        if (type.equals("0")){

            ll_fristType.setVisibility(View.VISIBLE);
            ll_secondType.setVisibility(View.GONE);
            ll_thridType.setVisibility(View.GONE);

        }else if (type.equals("1"))
        {
            ll_fristType.setVisibility(View.GONE);
            ll_secondType.setVisibility(View.VISIBLE);
            ll_thridType.setVisibility(View.GONE);

        }else {

            ll_fristType.setVisibility(View.GONE);
            ll_secondType.setVisibility(View.GONE);
            ll_thridType.setVisibility(View.VISIBLE);

        }

        initView();

    }


    private void initView(){

        tv_pushComent = (TextView) findViewById(R.id.tv_pushComent);
        tv_midBtn = (TextView) findViewById(R.id.tv_midBtn);
        commentImage = (ImageView) findViewById(R.id.commentImage);

        tv_pushComent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse("market://details?id="
                        + getPackageName());//???????????????APP??????
                Intent intent5 = new Intent(Intent.ACTION_VIEW, uri);
                intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent5,1);

            }
        });

        tv_midBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imagePaths1.size()>0){

                    mWeiboDialog = WeiboDialogUtils.createLoadingDialog(ZBAuthTaskActivity.this, "?????????...");

                    String url = FXConstant.URL_TASKIMAGEUPDATE;
                    List<Param> params=new ArrayList<>();
                    params.add(new Param("uLoginId", DemoHelper.getInstance().getCurrentUsernName()));

                    OkHttpManager.getInstance().posts2(params,"taskImage1", imagePaths1, null,new ArrayList<String>(), null, "", null, "", null, "", null, ""
                            , null, "", null, "", null, "", null, "", url, new OkHttpManager.HttpCallBack() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {

                                    WeiboDialogUtils.closeDialog(mWeiboDialog);
                                    Toast.makeText(getApplicationContext(),"???????????????",Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onFailure(String errorMsg) {
                                    Log.e("zbauth",errorMsg);
                                    WeiboDialogUtils.closeDialog(mWeiboDialog);
                                    Toast.makeText(getApplicationContext(),"?????????????????????????????????",Toast.LENGTH_SHORT).show();
                                }
                            });

                }else {

                    Toast.makeText(getApplicationContext(),"????????????????????????",Toast.LENGTH_SHORT).show();

                }

            }
        });

        commentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                goCamera(1,"01");

            }
        });


        uazPresenter = new UAZPresenter(this,this);
        uazPresenter.loadThisDetail(DemoHelper.getInstance().getCurrentUsernName());





        tv_pushDown = (TextView) findViewById(R.id.tv_pushDown);
        tv_midBtn2 = (TextView) findViewById(R.id.tv_midBtn2);
        image_downImage1 = (ImageView) findViewById(R.id.image_downImage1);
        image_downImage2 = (ImageView) findViewById(R.id.image_downImage2);


        tv_pushDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse("market://search?");
                Intent intent5 = new Intent(Intent.ACTION_VIEW, uri);
                intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent5,1);


            }
        });

        tv_midBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (imagePaths1.size() > 1){

                    mWeiboDialog = WeiboDialogUtils.createLoadingDialog(ZBAuthTaskActivity.this, "?????????...");

                    String url = FXConstant.URL_TASKIMAGEUPDATE;
                    List<Param> params=new ArrayList<>();
                    params.add(new Param("uLoginId", DemoHelper.getInstance().getCurrentUsernName()));

                    OkHttpManager.getInstance().posts2(params,"taskImage2", imagePaths1, null,new ArrayList<String>(), null, "", null, "", null, "", null, ""
                            , null, "", null, "", null, "", null, "", url, new OkHttpManager.HttpCallBack() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {

                                    WeiboDialogUtils.closeDialog(mWeiboDialog);
                                    Toast.makeText(getApplicationContext(),"???????????????",Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onFailure(String errorMsg) {
                                    Log.e("zbauth",errorMsg);
                                    WeiboDialogUtils.closeDialog(mWeiboDialog);
                                    Toast.makeText(getApplicationContext(),"?????????????????????????????????",Toast.LENGTH_SHORT).show();
                                }
                            });

                }else {

                    Toast.makeText(getApplicationContext(),"????????????????????????",Toast.LENGTH_SHORT).show();

                }

            }
        });


        image_downImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentTag = "1";//????????????????????????
                goCamera(2,"02");

            }
        });


        image_downImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentTag = "2";//????????????????????????

                goCamera(2,"02");

            }
        });


        tv_midBtn3 = (TextView) findViewById(R.id.tv_midBtn3);
        wechatImage = (ImageView) findViewById(R.id.wechatImage);

        tv_midBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (wechatAuth.equals("1")){

                    //????????????
                    UpdateMeraccountSubsidy();

                }else {

                    if (imagePaths1.size()>0){

                        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(ZBAuthTaskActivity.this, "?????????...");

                        String url = FXConstant.URL_TASKIMAGEUPDATE;
                        List<Param> params=new ArrayList<>();
                        params.add(new Param("uLoginId", DemoHelper.getInstance().getCurrentUsernName()));

                        OkHttpManager.getInstance().posts2(params,"taskImage3", imagePaths1, null,new ArrayList<String>(), null, "", null, "", null, "", null, ""
                                , null, "", null, "", null, "", null, "", url, new OkHttpManager.HttpCallBack() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {

                                        WeiboDialogUtils.closeDialog(mWeiboDialog);
                                        Toast.makeText(getApplicationContext(),"???????????????",Toast.LENGTH_SHORT).show();



                                        finish();
                                    }

                                    @Override
                                    public void onFailure(String errorMsg) {
                                        Log.e("zbauth",errorMsg);
                                        WeiboDialogUtils.closeDialog(mWeiboDialog);
                                        Toast.makeText(getApplicationContext(),"?????????????????????????????????",Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }else {

                        Toast.makeText(getApplicationContext(),"????????????????????????",Toast.LENGTH_SHORT).show();

                    }

                }

            }
        });


        wechatImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentTag = "3";//????????????????????????????????????????????????

                goCamera(2,"01");

            }
        });


        tv_example = (TextView) findViewById(R.id.tv_example);

        tv_example.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ZBAuthTaskActivity.this,ZBAuthExmpleActivity.class);

                startActivityForResult(intent,0);

            }
        });


        tv_searchKey = (TextView) findViewById(R.id.tv_searchKey);
        image_logo = (ImageView) findViewById(R.id.image_logo);

        tv_searchKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //???????????????????????????
                ClipboardManager cm = (ClipboardManager) getSystemService(ZBAuthTaskActivity.CLIPBOARD_SERVICE);
                // ?????????????????????ClipData
                ClipData mClipData = ClipData.newPlainText("Label", keyWord);
                // ???ClipData?????????????????????????????????
                cm.setPrimaryClip(mClipData);

                Toast.makeText(ZBAuthTaskActivity.this,"????????????????????????????????????",Toast.LENGTH_SHORT).show();

                //"market://details?id=" + getPackageName()
                Uri uri = Uri.parse("market://search?");
                Intent intent5 = new Intent(Intent.ACTION_VIEW, uri);
                intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent5,1);

            }
        });

        GetNoticeInfo();

    }



    private void UpdateMeraccountSubsidy(){

        String url = FXConstant.URL_UPDATEZHHU;

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                com.alibaba.fastjson.JSONObject object = JSON.parseObject(s);

                if (object.getString("code").equals("SUCCESS")){

                    SoundPlayUtils.play(2);
                    LayoutInflater inflaterDl = LayoutInflater.from(ZBAuthTaskActivity.this);
                    RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.dialog_hongbao, null);
                    final Dialog dialog = new AlertDialog.Builder(ZBAuthTaskActivity.this,R.style.Dialog).create();
                    dialog.show();
                    dialog.getWindow().setContentView(layout);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.setCancelable(true);
                    TextView tv_title1 = (TextView) layout.findViewById(R.id.tv_title1);
                    TextView tv_title = (TextView) layout.findViewById(R.id.tv_title);
                    TextView tv_yue = (TextView) layout.findViewById(R.id.tv_yue);
                    TextView tv_pushclick = (TextView) layout.findViewById(R.id.tv_pushclick);
                    tv_pushclick.setVisibility(View.VISIBLE);
                    TextPaint tp = tv_title.getPaint();
                    tp.setFakeBoldText(true);
                    tv_title.setText("1???");
                    tv_title1.setText("????????????");
                    tv_yue.setText("????????? ????????????");

                    tv_pushclick.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialog.dismiss();

                            Toast.makeText(ZBAuthTaskActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                            finish();

                        }
                    });

                }else {

                    Toast.makeText(ZBAuthTaskActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                    finish();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                Toast.makeText(ZBAuthTaskActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                finish();

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> param = new HashMap<>();

                param.put("merId",DemoHelper.getInstance().getCurrentUsernName());
                param.put("redPromoteBalance","1");
                param.put("redPromoteCount","0");
                param.put("redPromoteType","2"); //??????????????? ??????????????????????????????

                return param;
            }

        };

        MySingleton.getInstance(ZBAuthTaskActivity.this).addToRequestQueue(request);

    }


    private void GetNoticeInfo(){

        String url = FXConstant.URL_GONGGAO;
        StringRequest request = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                com.alibaba.fastjson.JSONObject object = JSON.parseObject(s);
                com.alibaba.fastjson.JSONObject object1 = object.getJSONObject("notice");


                String image1 = object1.getString("image1");//?????????
                String image2 = object1.getString("image2");//??????
                keyWord = image1;
                tv_searchKey.setText("???"+image1+"???");

                ImageLoader.getInstance().displayImage(FXConstant.URL_ADVERTURL+image2,image_logo);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();

                param.put("deviceType","androidzbAuth");

                return param;
            }
        };

        MySingleton.getInstance(ZBAuthTaskActivity.this).addToRequestQueue(request);

    }


    private void goCamera(int count,String type){
        List<LocalMedia> medias = new ArrayList<>();

        medias = selectMedia1;

        int selectMode;
        if ("01".equals(type)) {
            selectMode = FunctionConfig.MODE_SINGLE;

        } else {
            selectMode = FunctionConfig.MODE_MULTIPLE;
        }

        WeakReference<ZBAuthTaskActivity> reference = new WeakReference<ZBAuthTaskActivity>(ZBAuthTaskActivity.this);
        FunctionOptions options = new FunctionOptions.Builder()
                .setType(FunctionConfig.TYPE_IMAGE) // ??????or?????? FunctionConfig.TYPE_IMAGE  TYPE_VIDEO
                .setCropMode(1) // ???????????? ?????????1:1???3:4???3:2???16:9
//                            .setOffsetX() // ?????????????????????
//                            .setOffsetY() // ?????????????????????
                .setCompress(true) //????????????
                .setEnablePixelCompress(true) //????????????????????????
                .setEnableQualityCompress(true) //?????????????????????
                .setMaxSelectNum(count) // ????????????????????????
                .setMinSelectNum(0)// ?????????????????????????????????????????????????????????
                .setSelectMode(selectMode) // ?????? or ??????
                .setShowCamera(false) //???????????????????????? ??????????????????type ????????????????????????
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
                .setSelectMedia(medias) // ?????????????????????????????????????????????????????????????????????
                .setCompressFlag(1) // 1 ?????????????????? 2 luban??????
                .setCompressW(0) // ????????? ???????????????????????????????????????
                .setCompressH(0) // ????????? ???????????????????????????????????????
                .setThemeStyle(ContextCompat.getColor(reference.get(), R.color.blue)) // ??????????????????
                .setNumComplete(false) // 0/9 ??????  ??????
                .setClickVideo(false)// ????????????
                .setFreeStyleCrop(false) // ????????????????????????????????????
                .create();

        PictureConfig.getInstance().init(options).openPhoto(reference.get(), resultCallback1);
    }

    /**
     * ??????????????????
     */
    private PictureConfig.OnSelectResultCallback resultCallback1 = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            imagePaths1.clear();
            // ????????????
            selectMedia1 = resultList;

            LocalMedia media;
            LocalMedia media2;

            String path;
            String path2;

            if (resultList.size() == 1){

                media = resultList.get(0);
                if (media.isCut() && !media.isCompressed()) {
                    // ?????????
                    path = media.getCutPath();
                } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                    // ?????????,???????????????????????????,??????????????????????????????
                    path = media.getCompressPath();
                } else {
                    // ????????????
                    path = media.getPath();
                }

                Bitmap bitmap = BitmapFactory.decodeFile(path);
                if (currentTag.equals("1")){

                    image_downImage1.setImageBitmap(bitmap);

                }else if (currentTag.equals("2")){

                    image_downImage2.setImageBitmap(bitmap);

                }

            }else if (resultList.size() == 2){

                media = resultList.get(0);
                if (media.isCut() && !media.isCompressed()) {
                    // ?????????
                    path = media.getCutPath();
                } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                    // ?????????,???????????????????????????,??????????????????????????????
                    path = media.getCompressPath();
                } else {
                    // ????????????
                    path = media.getPath();
                }

                Bitmap bitmap = BitmapFactory.decodeFile(path);
                image_downImage1.setImageBitmap(bitmap);

                media2 = resultList.get(1);
                if (media2.isCut() && !media2.isCompressed()) {
                    // ?????????
                    path2 = media2.getCutPath();
                } else if (media2.isCompressed() || (media2.isCut() && media2.isCompressed())) {
                    // ?????????,???????????????????????????,??????????????????????????????
                    path2 = media2.getCompressPath();
                } else {
                    // ????????????
                    path2 = media2.getPath();
                }

                Bitmap bitmap2 = BitmapFactory.decodeFile(path2);
                image_downImage2.setImageBitmap(bitmap2);
            }

            if (selectMedia1 != null) {

                for (int i=0;i<selectMedia1.size();i++){
                    imagePaths1.add(selectMedia1.get(i).getCompressPath());
                }
            }
        }

        @Override
        public void onSelectSuccess(LocalMedia media) {
         //   imagePaths1.clear();
            // ????????????
            selectMedia1.add(media);

            String path;
            if (media.isCut() && !media.isCompressed()) {
                // ?????????
                path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                // ?????????,???????????????????????????,??????????????????????????????
                path = media.getCompressPath();
            } else {
                // ????????????
                path = media.getPath();
            }

            if (selectMedia1 != null) {
//                adapter.setList(selectMedia1);
//                adapter.notifyDataSetChanged();

              //  String path = media.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(path);

                if (currentTag.equals("1")){


                    image_downImage1.setImageBitmap(bitmap);

                    if (imagePaths1.size()>0){
                        imagePaths1.set(0,selectMedia1.get(0).getCompressPath());
                    }else {
                        imagePaths1.add(selectMedia1.get(0).getCompressPath());
                    }


                }else if (currentTag.equals("2")){

                    image_downImage2.setImageBitmap(bitmap);

                    if (imagePaths1.size() == 0){
                        imagePaths1.add("");
                        imagePaths1.add(selectMedia1.get(0).getCompressPath());

                    }else if (imagePaths1.size() == 1){

                        imagePaths1.add(selectMedia1.get(0).getCompressPath());

                    }else {

                        imagePaths1.set(1,selectMedia1.get(0).getCompressPath());

                    }

                }else
                {

                    if (currentTag.equals("3")){

                        wechatImage.setImageBitmap(bitmap);
                        for (int i=0;i<selectMedia1.size();i++){
                            imagePaths1.add(selectMedia1.get(i).getCompressPath());
                        }

                    }else {

                        commentImage.setImageBitmap(bitmap);
                        for (int i=0;i<selectMedia1.size();i++){
                            imagePaths1.add(selectMedia1.get(i).getCompressPath());
                        }
                    }

                }



            }
        }
    };


    @Override
    public void back(View view) {
        super.back(view);
    }


    @Override
    public void updateThisUser(Userful user2) {

        String taskImage1 = user2.getTaskImage1();

        String taskImage2 = user2.getTaskImage2();

        String taskImage3 = user2.getTaskImage3();
        wechatAuth = user2.getWechatAuth();

        String type = getIntent().getStringExtra("type");

        if (type != null && type.equals("0")){

            if (taskImage1.length()>0){

                //??????????????????

                tv_midBtn.setEnabled(false);
                tv_midBtn.setText("?????????");
                commentImage.setEnabled(false);

                String[] images = taskImage1.split("\\|");

                ImageLoader.getInstance().displayImage(FXConstant.URL_AVATAR+images[0],commentImage);

            }

        }else if (type != null && type.equals("1")){

            if (taskImage2.length()>0){

                //??????????????????

                tv_midBtn2.setEnabled(false);
                tv_midBtn2.setText("?????????");
                image_downImage1.setEnabled(false);
                image_downImage2.setEnabled(false);

                String[] images = taskImage2.split("\\|");

                ImageLoader.getInstance().displayImage(FXConstant.URL_AVATAR+images[0],image_downImage1);
                ImageLoader.getInstance().displayImage(FXConstant.URL_AVATAR+images[1],image_downImage2);

            }

        }else if (type != null && type.equals("2")){


            if (taskImage3.length()>0){

                //??????????????????

                if (wechatAuth.equals("1")){

                    tv_midBtn3.setText("????????????");

                }else if (wechatAuth.equals("2")){

                    tv_midBtn3.setEnabled(false);
                    tv_midBtn3.setText("?????????");

                }else {

                    tv_midBtn3.setEnabled(false);
                    tv_midBtn3.setText("?????????");

                }


                wechatImage.setEnabled(false);

                ImageLoader.getInstance().displayImage(FXConstant.URL_AVATAR+taskImage3,wechatImage);

            }

        }

    }

    @Override
    public void showproError() {

    }

    @Override
    public void showproLoading() {

    }

    @Override
    public void hideproLoading() {

    }

}

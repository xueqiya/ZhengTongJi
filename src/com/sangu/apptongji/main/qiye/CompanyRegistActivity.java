package com.sangu.apptongji.main.qiye;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sangu.apptongji.DemoApplication;
import com.sangu.apptongji.DemoHelper;
import com.sangu.apptongji.R;
import com.sangu.apptongji.main.FXConstant;
import com.sangu.apptongji.main.adapter.GridImageAdapter;
import com.sangu.apptongji.main.alluser.entity.Userful;
import com.sangu.apptongji.main.alluser.presenter.IProfilePresenter;
import com.sangu.apptongji.main.alluser.presenter.impl.ProfilePresenter;
import com.sangu.apptongji.main.alluser.view.IProfileView;
import com.sangu.apptongji.main.mapsearch.MapPickerActivity;
import com.sangu.apptongji.main.moments.BiImageActivity;
import com.sangu.apptongji.main.qiye.entity.QiYeInfo;
import com.sangu.apptongji.main.qiye.presenter.IQiYeInfoPresenter;
import com.sangu.apptongji.main.qiye.presenter.impl.QiYeInfoPresenter;
import com.sangu.apptongji.main.qiye.view.IQiYeDetailView;
import com.sangu.apptongji.main.utils.FullyGridLayoutManager;
import com.sangu.apptongji.main.utils.MySingleton;
import com.sangu.apptongji.main.utils.OkHttpManager;
import com.sangu.apptongji.main.utils.Param;
import com.sangu.apptongji.main.utils.ToastUtils;
import com.sangu.apptongji.main.widget.CustomProgressDialog;
import com.sangu.apptongji.ui.BaseActivity;
import com.sangu.apptongji.utils.UserPermissionUtil;
import com.yalantis.ucrop.entity.LocalMedia;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016-12-27.
 */

public class CompanyRegistActivity extends BaseActivity implements IProfileView ,IQiYeDetailView{
    private Button btn_send;
    private EditText et_gshzz_zhch;
    private EditText et_fd_dbr;
    private EditText et_lx_fsh;
    private EditText et_jy_fw;
    private EditText et_qiye_qch;
    private EditText et_qy_dz;
    private EditText et_tv_profession;
    private TextView tv_qiye_shenhe;
    private ImageView sdv_image1;
    private ImageView sdv_image2;
    private ImageView sdv_image3;
    private LinearLayout ll6;
    private LinearLayout ll1;

    private List<LocalMedia> selectMedia1 = new ArrayList<>();
    private List<LocalMedia> selectMedia2 = new ArrayList<>();
    private RecyclerView recyclerView;
    private GridImageAdapter adapter;
    private RecyclerView recyclerView2;
    private GridImageAdapter adapter2;

    private CustomProgressDialog dialog=null;
    private IProfilePresenter presenter=null;
    private IQiYeInfoPresenter qiyePresenter=null;
    private String path1 = "",path2 = "",path3 = "",lat,lng,lat1="",lng1="",biaoshi,qiyeId;
    private boolean isfinish = false;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_company_regist);
        btn_send = (Button) findViewById(R.id.btn_send);
        et_gshzz_zhch = (EditText) findViewById(R.id.et_gshzz_zhch);
        et_fd_dbr = (EditText) findViewById(R.id.et_fd_dbr);
        et_lx_fsh = (EditText) findViewById(R.id.et_lx_fsh);
        et_jy_fw = (EditText) findViewById(R.id.et_jy_fw);
        et_qiye_qch = (EditText) findViewById(R.id.et_qiye_qch);
        et_qy_dz = (EditText) findViewById(R.id.et_qy_dz);
        et_tv_profession = (EditText) findViewById(R.id.et_tv_profession);
        tv_qiye_shenhe = (TextView) findViewById(R.id.tv_qiye_shenhe);
        sdv_image1 = (ImageView) findViewById(R.id.sdv_image1);
        sdv_image2 = (ImageView) findViewById(R.id.sdv_image2);
        sdv_image3 = (ImageView) findViewById(R.id.sdv_image3);
        ll6 = (LinearLayout) findViewById(R.id.ll6);
        ll1 = (LinearLayout) findViewById(R.id.ll1);
        RequestQueue queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        qiyeId = this.getIntent().getStringExtra("qiyeId");
        biaoshi = this.getIntent().getStringExtra("biaoshi");
        if (biaoshi.equals("0")) {
            presenter = new ProfilePresenter(this,this);
            lat = TextUtils.isEmpty(DemoApplication.getInstance().getCurrentLat()) ? "34.762711" : DemoApplication.getInstance().getCurrentLat();
            lng = TextUtils.isEmpty(DemoApplication.getInstance().getCurrentLng()) ? "113.744531" : DemoApplication.getInstance().getCurrentLng();
            initView();
        }else {
            qiyePresenter = new QiYeInfoPresenter(this,this);
            initView2();
            qiyePresenter.loadQiYeInfo(qiyeId);
        }
    }

    @Override
    public void updateUserInfo(Userful user) {
        if (isfinish){
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void showproLoading() {

    }

    @Override
    public void hideproLoading() {

    }

    @Override
    public void showproError() {

    }

    @Override
    public void updateQiyeInfo(QiYeInfo qiYeInfo) {
        String comname = qiYeInfo.getCompanyName();
        String frmc = qiYeInfo.getResv3();
        String fanwei = qiYeInfo.getComSignature();
        String comAddress = qiYeInfo.getComAddress();
        String lianxifangshi = qiYeInfo.getComTel();
        String zhizhao = qiYeInfo.getComEmail();
        String remark = qiYeInfo.getRemark();
        final String zhizhaotupian = qiYeInfo.getBusinessLicense();
        String resv2 = qiYeInfo.getIpCardPic();
        final String resv21 = resv2.split("\\|")[0];
        final String resv22 = resv2.split("\\|")[1];
        ImageLoader.getInstance().displayImage(FXConstant.URL_QIYEFAREN_SHENFENZHENG+resv21,sdv_image1,DemoApplication.mOptions2);
        ImageLoader.getInstance().displayImage(FXConstant.URL_QIYEFAREN_SHENFENZHENG+resv22,sdv_image2,DemoApplication.mOptions2);
        ImageLoader.getInstance().displayImage(FXConstant.URL_QIYE_ZHIZHAO+zhizhaotupian,sdv_image3,DemoApplication.mOptions2);
        sdv_image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CompanyRegistActivity.this, BiImageActivity.class).putExtra("imageUrl",FXConstant.URL_QIYEFAREN_SHENFENZHENG+resv21));
            }
        });
        sdv_image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CompanyRegistActivity.this, BiImageActivity.class).putExtra("imageUrl",FXConstant.URL_QIYEFAREN_SHENFENZHENG+resv22));
            }
        });
        sdv_image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CompanyRegistActivity.this, BiImageActivity.class).putExtra("imageUrl",FXConstant.URL_QIYE_ZHIZHAO+zhizhaotupian));
            }
        });
        try {
            comname = URLDecoder.decode(comname, "UTF-8");
            frmc = URLDecoder.decode(frmc, "UTF-8");
            fanwei = URLDecoder.decode(fanwei, "UTF-8");
            comAddress = URLDecoder.decode(comAddress, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        et_lx_fsh.setText(lianxifangshi);
        et_gshzz_zhch.setText(zhizhao);
        et_jy_fw.setText(fanwei);
        et_qiye_qch.setText(comname);
        et_fd_dbr.setText(frmc);
        et_qy_dz.setText(comAddress);
        if ("2".equals(remark)){
            initView();
            initView3();
        }
    }
    @Override
    public void showLoading() {
    }
    @Override
    public void hideLoading() {
    }
    @Override
    public void showError() {
    }
    private void initView2() {
        recyclerView = (RecyclerView) this.findViewById(R.id.recycler);
        recyclerView2 = (RecyclerView) this.findViewById(R.id.recycler2);
        ll1.setFocusable(true);
        ll1.setFocusableInTouchMode(true);
        recyclerView.setVisibility(View.GONE);
        recyclerView2.setVisibility(View.GONE);
        sdv_image1.setVisibility(View.VISIBLE);
        sdv_image2.setVisibility(View.VISIBLE);
        sdv_image3.setVisibility(View.VISIBLE);
        btn_send.setEnabled(false);
        et_qy_dz.setEnabled(false);
        et_fd_dbr.setEnabled(false);
        et_gshzz_zhch.setEnabled(false);
        et_jy_fw.setEnabled(false);
        et_lx_fsh.setEnabled(false);
        et_qiye_qch.setEnabled(false);
        tv_qiye_shenhe.setVisibility(View.VISIBLE);
    }
    private void initView3() {
        btn_send.setText("???  ???");
        ll1.setFocusable(true);
        ll1.setFocusableInTouchMode(true);
        sdv_image1.setVisibility(View.VISIBLE);
        sdv_image2.setVisibility(View.VISIBLE);
        sdv_image3.setVisibility(View.VISIBLE);
        btn_send.setEnabled(true);
        et_qy_dz.setEnabled(true);
        et_fd_dbr.setEnabled(true);
        et_gshzz_zhch.setEnabled(true);
        et_jy_fw.setEnabled(true);
        et_lx_fsh.setEnabled(true);
        et_qiye_qch.setEnabled(true);
        tv_qiye_shenhe.setVisibility(View.VISIBLE);
        tv_qiye_shenhe.setText("???????????????????????????????????????????????????");
        sdv_image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goCamera(2,"01");
            }
        });
        sdv_image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goCamera(2,"01");
            }
        });
        sdv_image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goCamera(1,"02");
            }
        });
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(CompanyRegistActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(CompanyRegistActivity.this, onAddPicClickListener1);
        adapter.setList(selectMedia1);
        adapter.setSelectMax(2);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                switch (selectMedia1.get(position).getType()) {
                    case FunctionConfig.TYPE_IMAGE:
                        // ???????????? ??????????????? ???????????????????????????
//                        PictureConfig.getInstance().externalPicturePreview(MainActivity.this, "/custom_file", position, selectMedia);
                        PictureConfig.getInstance().externalPicturePreview(CompanyRegistActivity.this, position, selectMedia1);
                        break;
                    case FunctionConfig.TYPE_VIDEO:
                        // ????????????
                        if (selectMedia1.size() > 0) {
                            PictureConfig.getInstance().externalPictureVideo(CompanyRegistActivity.this, selectMedia1.get(position).getPath());
                        }
                        break;
                }
            }
        });
        recyclerView2 = (RecyclerView) findViewById(R.id.recycler2);
        FullyGridLayoutManager manager2 = new FullyGridLayoutManager(CompanyRegistActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView2.setLayoutManager(manager2);
        adapter2 = new GridImageAdapter(CompanyRegistActivity.this, onAddPicClickListener2);
        adapter2.setList(selectMedia2);
        adapter2.setSelectMax(1);
        recyclerView2.setAdapter(adapter2);
        adapter2.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                switch (selectMedia2.get(position).getType()) {
                    case FunctionConfig.TYPE_IMAGE:
                        // ???????????? ??????????????? ???????????????????????????
//                        PictureConfig.getInstance().externalPicturePreview(MainActivity.this, "/custom_file", position, selectMedia);
                        PictureConfig.getInstance().externalPicturePreview(CompanyRegistActivity.this, position, selectMedia2);
                        break;
                    case FunctionConfig.TYPE_VIDEO:
                        // ????????????
                        if (selectMedia2.size() > 0) {
                            PictureConfig.getInstance().externalPictureVideo(CompanyRegistActivity.this, selectMedia2.get(position).getPath());
                        }
                        break;
                }
            }
        });

        et_qy_dz.setFocusable(false);
        et_qy_dz.setFocusableInTouchMode(false);
        et_qy_dz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(CompanyRegistActivity.this, MapPickerActivity.class),0);
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectMedia1.size()==2&&selectMedia2.size()==1) {
                    if ("".equals(lat1)||"".equals(lng1)){
                        Toast.makeText(CompanyRegistActivity.this,"????????????????????????,???????????????",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    send();
                }else {
                    Toast.makeText(CompanyRegistActivity.this,"?????????????????????????????????????????????????????????",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * ????????????????????????
     */
    private GridImageAdapter.onAddPicClickListener onAddPicClickListener1 = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick(int type, int position) {
            switch (type) {
                case 0:
                    goCamera(2,"01");
                    break;
                case 1:
                    // ????????????
                    if (position==0){
                        path1 = null;
                    }else if (position==1){
                        path2 = null;
                    }
                    if (selectMedia1.size()>position) {
                        selectMedia1.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                    break;
            }
        }
    };
    /**
     * ????????????????????????
     */
    private GridImageAdapter.onAddPicClickListener onAddPicClickListener2 = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick(int type, int position) {
            switch (type) {
                case 0:
                    goCamera(1,"02");
                    break;
                case 1:
                    // ????????????
                    path3 = null;
                    if (selectMedia2.size()>position) {
                        selectMedia2.remove(position);
                        adapter2.notifyItemRemoved(position);
                    }
                    break;
            }
        }
    };

    private void goCamera(int count,String type){
        List<LocalMedia> selectMedia;
        if (type.equals("01")){
            selectMedia = selectMedia1;
        }else {
            selectMedia = selectMedia2;
        }
        WeakReference<CompanyRegistActivity> reference = new WeakReference<CompanyRegistActivity>(CompanyRegistActivity.this);
        FunctionOptions options = new FunctionOptions.Builder()
                .setType(FunctionConfig.TYPE_IMAGE) // ??????or?????? FunctionConfig.TYPE_IMAGE  TYPE_VIDEO
                .setCropMode(1) // ???????????? ?????????1:1???3:4???3:2???16:9
//                            .setOffsetX() // ?????????????????????
//                            .setOffsetY() // ?????????????????????
                .setCompress(false) //????????????
                .setEnablePixelCompress(true) //????????????????????????
                .setEnableQualityCompress(true) //?????????????????????
                .setMaxSelectNum(count) // ????????????????????????
                .setMinSelectNum(0)// ?????????????????????????????????????????????????????????
                .setSelectMode(FunctionConfig.MODE_MULTIPLE) // ?????? or ??????
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
                .setSelectMedia(selectMedia) // ?????????????????????????????????????????????????????????????????????
                .setCompressFlag(1) // 1 ?????????????????? 2 luban??????
                .setCompressW(0) // ????????? ???????????????????????????????????????
                .setCompressH(0) // ????????? ???????????????????????????????????????
                .setThemeStyle(ContextCompat.getColor(reference.get(), R.color.blue)) // ??????????????????
                .setNumComplete(false) // 0/9 ??????  ??????
                .setClickVideo(false)// ????????????
                .setFreeStyleCrop(false) // ????????????????????????????????????
                .create();
        if ("01".equals(type)) {
            // ?????????
            PictureConfig.getInstance().init(options).openPhoto(reference.get(), resultCallback1);
        } else {
            // ??????????????????????????????????????????
            PictureConfig.getInstance().init(options).openPhoto(reference.get(), resultCallback2);
        }
    }
    /**
     * ??????????????????
     */
    private PictureConfig.OnSelectResultCallback resultCallback1 = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            if (sdv_image1.getVisibility()==View.VISIBLE){
                sdv_image1.setVisibility(View.GONE);
            }
            if (sdv_image2.getVisibility()==View.VISIBLE){
                sdv_image2.setVisibility(View.GONE);
            }
            if (recyclerView.getVisibility()!=View.VISIBLE){
                recyclerView.setVisibility(View.VISIBLE);
            }
            // ????????????
            selectMedia1 = resultList;
            Log.i("callBack_result", selectMedia1.size() + "");
            LocalMedia media = resultList.get(0);
            if (media.isCut() && !media.isCompressed()) {
                // ?????????
                String path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                // ?????????,???????????????????????????,??????????????????????????????
                String path = media.getCompressPath();
            } else {
                // ????????????
                String path = media.getPath();
            }
            if (selectMedia1 != null) {
                adapter.setList(selectMedia1);
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onSelectSuccess(LocalMedia media) {
            if (sdv_image1.getVisibility()==View.VISIBLE){
                sdv_image1.setVisibility(View.GONE);
            }
            if (sdv_image2.getVisibility()==View.VISIBLE){
                sdv_image2.setVisibility(View.GONE);
            }
            if (recyclerView.getVisibility()!=View.VISIBLE){
                recyclerView.setVisibility(View.VISIBLE);
            }
            // ????????????
            selectMedia1.add(media);
            if (selectMedia1 != null) {
                adapter.setList(selectMedia1);
                adapter.notifyDataSetChanged();
            }
        }
    };
    /**
     * ??????????????????
     */
    private PictureConfig.OnSelectResultCallback resultCallback2 = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            if (sdv_image3.getVisibility()==View.VISIBLE){
                sdv_image3.setVisibility(View.GONE);
            }
            if (recyclerView2.getVisibility()!=View.VISIBLE){
                recyclerView2.setVisibility(View.VISIBLE);
            }
            // ????????????
            selectMedia2 = resultList;
            Log.i("callBack_result", selectMedia2.size() + "");
            LocalMedia media = resultList.get(0);
            if (media.isCut() && !media.isCompressed()) {
                // ?????????
                String path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                // ?????????,???????????????????????????,??????????????????????????????
                String path = media.getCompressPath();
            } else {
                // ????????????
                String path = media.getPath();
            }
            if (selectMedia2 != null) {
                adapter2.setList(selectMedia2);
                adapter2.notifyDataSetChanged();
            }
        }

        @Override
        public void onSelectSuccess(LocalMedia media) {
            if (sdv_image3.getVisibility()==View.VISIBLE){
                sdv_image3.setVisibility(View.GONE);
            }
            if (recyclerView2.getVisibility()!=View.VISIBLE){
                recyclerView2.setVisibility(View.VISIBLE);
            }
            // ????????????
            selectMedia2.add(media);
            if (selectMedia2 != null) {
                adapter2.setList(selectMedia2);
                adapter2.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    if (data!=null){
                        lat1 = data.getStringExtra("latitude");
                        lng1 = data.getStringExtra("longitude");
                        String dizhi = data.getStringExtra("street");
                        et_qy_dz.setText(dizhi);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (selectMedia1!=null){
            selectMedia1.clear();
            selectMedia1=null;
        }
        if (selectMedia2!=null){
            selectMedia2.clear();
            selectMedia2=null;
        }
    }

    private void send() {
        String gszhzh = et_gshzz_zhch.getText().toString().trim();
        String comname = et_qiye_qch.getText().toString().trim();
        String frmc = et_fd_dbr.getText().toString().trim();
        String phone = et_lx_fsh.getText().toString().trim();
        String fanwei = et_jy_fw.getText().toString().trim();
        String comAddress = et_qy_dz.getText().toString().trim();
        String comprofession = et_tv_profession.getText().toString().trim();

        if (comname.length() == 0){

            Toast.makeText(getApplicationContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }
        if (gszhzh.length() == 0){

            Toast.makeText(getApplicationContext(), "????????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }
        if (frmc.length() == 0){

            Toast.makeText(getApplicationContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() == 0){

            Toast.makeText(getApplicationContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }
        if (comprofession.length() == 0){

            Toast.makeText(getApplicationContext(), "???????????????", Toast.LENGTH_SHORT).show();
            return;
        }


        if (comprofession.length() > 6){

            Toast.makeText(getApplicationContext(), "?????????6??????????????????", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            comname = URLEncoder.encode(comname, "UTF-8");
            frmc = URLEncoder.encode(frmc, "UTF-8");
            fanwei = URLEncoder.encode(fanwei, "UTF-8");
            comAddress = URLEncoder.encode(comAddress, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (selectMedia1.size()==2&&selectMedia2.size()==1) {
            path1 = selectMedia1.get(0).getPath();
            path2 = selectMedia1.get(1).getPath();
            path3 = selectMedia2.get(0).getPath();
        }
        WeakReference<CompanyRegistActivity> reference = new WeakReference<CompanyRegistActivity>(CompanyRegistActivity.this);
        dialog = CustomProgressDialog.createDialog(reference.get());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("????????????...");
        dialog.show();
        List<Param> param=new ArrayList<>();
        param.add(new Param("comEmail", gszhzh));
        param.add(new Param("companyName", comname));
        param.add(new Param("comTel", phone));
        param.add(new Param("comSignature", fanwei));
        param.add(new Param("resv3", frmc));
        param.add(new Param("comAddress", comAddress));
        param.add(new Param("comLatitude", lat1));
        param.add(new Param("comLongitude", lng1));
        param.add(new Param("userProfessionList[1].upName", comprofession));
        param.add(new Param("userId", DemoHelper.getInstance().getCurrentUsernName()));
        final List<File> files1 = new ArrayList<File>();
        File file1 = null;
        File file2 = null;
        file1 = new File(path1);
        file2 = new File(path2);
        if (file1.exists()) {
            files1.add(file1);
            files1.add(file2);
        }
        final List<File> files2 = new ArrayList<File>();
        File file = null;
        file = new File(path3);
        if (file.exists()) {
            files2.add(file);
        }
        if (files1.size()>0&&files2.size()>0) {
            OkHttpManager.getInstance().postsfz(param,files1,files2,FXConstant.URL_COMPANY_INSERT,new OkHttpManager.HttpCallBack() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    if (dialog!=null&&dialog.isShowing()){
                        dialog.dismiss();
                    }
                    String code = jsonObject.getString("code");
                    if (code.equals("SUCCESS")) {
//                        FileUtils.deleteAllFiles(new File(Environment.getExternalStorageDirectory()+"/bizchat/"));
                        duanxintongzhi();
                    } else {
                        Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(String errorMsg) {
                    if (dialog!=null&&dialog.isShowing()){
                        dialog.dismiss();
                    }
                    Log.e("??????????????????",errorMsg);
                    Toast.makeText(getApplicationContext(), "?????????????????????????????????", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            if (dialog!=null&&dialog.isShowing()){
                dialog.dismiss();
            }
            Toast.makeText(getApplicationContext(), "????????????????????????????????????????????????????????????" , Toast.LENGTH_SHORT).show();
        }
    }

    private void duanxintongzhi() {
        final String id = DemoHelper.getInstance().getCurrentUsernName();
        String url = FXConstant.URL_DUANXIN_TONGZHI;
        final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(getApplicationContext(), "???????????????", Toast.LENGTH_SHORT).show();
                isfinish = true;
                presenter.updateData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("volleyError",volleyError.getMessage());
                Toast.makeText(getApplicationContext(), "??????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("message","??????????????? ??????:??????"+id+"????????????????????????????????????,???????????????!");
                param.put("telNum","13513895563");//"13513895563"
                return param;
            }
        };
        UserPermissionUtil.getUserPermission(this, DemoHelper.getInstance().getCurrentUsernName(), "3", new UserPermissionUtil.UserPermissionListener() {
            @Override
            public void onAllow() {
                MySingleton.getInstance(CompanyRegistActivity.this).addToRequestQueue(request);

            }

            @Override
            public void onBan() {

                ToastUtils.showNOrmalToast(getApplicationContext(), "????????????????????????????????????");

            }
        });
    }


    @Override
    public void back(View view) {
        super.back(view);
    }
}

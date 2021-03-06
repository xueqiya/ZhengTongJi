package com.sangu.apptongji.main.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fanxin.easeui.widget.EaseSwitchButton;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;
import com.sangu.apptongji.R;
import com.sangu.apptongji.main.FXConstant;
import com.sangu.apptongji.main.qiye.entity.QiYeInfo;
import com.sangu.apptongji.main.qiye.presenter.IQiYeInfoPresenter;
import com.sangu.apptongji.main.qiye.presenter.impl.QiYeInfoPresenter;
import com.sangu.apptongji.main.qiye.view.IQiYeDetailView;
import com.sangu.apptongji.main.utils.OkHttpManager;
import com.sangu.apptongji.main.utils.Param;
import com.sangu.apptongji.utils.SelectPicPopupWindow;
import com.yalantis.ucrop.entity.LocalMedia;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomePagePosterActivity extends CoreActivity implements IQiYeDetailView {
    private String companyId;
    private IQiYeInfoPresenter qiYeInfoPresenter = null;
    @BindView(R.id.iv_img)
    ImageView ivImg;
    @BindView(R.id.sb_type1)
    EaseSwitchButton sbType1;
    @BindView(R.id.sb_type2)
    EaseSwitchButton sbType2;
    @BindView(R.id.sb_type3)
    EaseSwitchButton sbType3;
    @BindView(R.id.sb_type4)
    EaseSwitchButton sbType4;
    @BindView(R.id.cb_qq)
    AppCompatCheckBox cbQQ;
    @BindView(R.id.cb_wx)
    AppCompatCheckBox cbWX;
    @BindView(R.id.et_url)
    EditText etURL;
    private SelectPicPopupWindow menuWindow = null;
    private List<LocalMedia> selectMedia = new ArrayList<>();
    String path = "";
    String signShareType = "";
    private QiYeInfo qiYeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_poster);
        ButterKnife.bind(this);
        companyId = getIntent().getStringExtra("companyId");
        qiYeInfoPresenter = new QiYeInfoPresenter(HomePagePosterActivity.this, this);
        qiYeInfoPresenter.loadQiYeInfo(companyId);
    }

    @OnClick({R.id.tv_back, R.id.iv_back, R.id.iv_img, R.id.sb_type1, R.id.sb_type2, R.id.sb_type3, R.id.sb_type4,R.id.tv_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_img:
                selectImgs();
                break;
            case R.id.sb_type1:
                if (sbType1.isSwitchOpen()) {
                    sbType1.closeSwitch();
                } else {
                    sbType1.openSwitch();
                    sbType2.closeSwitch();
                    sbType3.closeSwitch();
                }
                setSBType();
                break;
            case R.id.sb_type2:
                if (sbType2.isSwitchOpen()) {
                    sbType2.closeSwitch();
                } else {
                    sbType2.openSwitch();
                    sbType1.closeSwitch();
                    sbType3.closeSwitch();
                }
                setSBType();
                break;
            case R.id.sb_type3:
                if (sbType3.isSwitchOpen()) {
                    sbType3.closeSwitch();
                } else {
                    sbType3.openSwitch();
                    sbType1.closeSwitch();
                    sbType2.closeSwitch();
                }
                setSBType();
                break;
            case R.id.sb_type4:
                if (sbType4.isSwitchOpen()) {
                    sbType4.closeSwitch();
                } else {
                    sbType4.openSwitch();
                }
                break;
            case R.id.tv_commit:
                updateCompany();
                break;
        }
    }

    private void updateCompany() {
        if (signShareType.equals("02")) {
            String url = etURL.getText().toString().trim();
            if (TextUtils.isEmpty(url)) {
                showToast("?????????????????????");
                return;
            }
        } else if (signShareType.equals("03") && !qiYeInfo.getSignShareType().equals("03")) {
            if (TextUtils.isEmpty(path)) {
                showToast("?????????????????????");
                return;
            }
        }
        showDialog();
        List<Param> params = new ArrayList<>();
        params.add(new Param("companyId", companyId));
        params.add(new Param("companyName", this.qiYeInfo.getCompanyName()));
        params.add(new Param("signShareChannel", getchannel()));
        params.add(new Param("signShareType", signShareType));
        params.add(new Param("signShareNeed", sbType4.isSwitchOpen() ? "01" : "00"));
        params.add(new Param("signShareContent", etURL.getText().toString().trim()));
        OkHttpManager.getInstance().postUpdateCompany(params, "signShareImageFile", path, FXConstant.URL_UPDATE_QIYE, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("update==", jsonObject.toJSONString());
                disDialog();
                try {
                    String code = jsonObject.getString("code");
                    if(code.equals("SUCCESS")){
                        finish();
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                disDialog();
            }
        });
    }

    private String getchannel() {
        String wx = "0", qq = "0";
        if (cbQQ.isChecked()) {
            qq = "1";
        }
        if (cbWX.isChecked()) {
            wx = "1";
        }
        return qq + "|" + wx;
    }

    private void setSBType() {
        if (sbType1.isSwitchOpen()) {
            signShareType = "01";
        } else if (sbType2.isSwitchOpen()) {
            signShareType = "02";
        } else if (sbType3.isSwitchOpen()) {
            signShareType = "03";
        } else {
            sbType1.closeSwitch();
            sbType2.closeSwitch();
            sbType3.closeSwitch();
            signShareType = "00";
        }
    }

    private void setSignShareType(String signShareType, String path) {
        if (signShareType.equals("01")) {
            sbType1.openSwitch();
            sbType2.closeSwitch();
            sbType3.closeSwitch();
        } else if (signShareType.equals("02")) {
            sbType1.closeSwitch();
            sbType2.openSwitch();
            sbType3.closeSwitch();
        } else if (signShareType.equals("03")) {
            sbType1.closeSwitch();
            sbType2.closeSwitch();
            sbType3.openSwitch();
            Glide.with(HomePagePosterActivity.this)
                    .load(path)
                    .centerCrop()
                    .placeholder(R.color.color_f6)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivImg);
        }else{
            sbType1.closeSwitch();
            sbType2.closeSwitch();
            sbType3.closeSwitch();
        }
    }

    @Override
    public void updateQiyeInfo(QiYeInfo qiYeInfo) throws UnsupportedEncodingException {
        this.qiYeInfo = qiYeInfo;
        etURL.setText(qiYeInfo.getSignShareContent());
        String signShareChannel=qiYeInfo.getSignShareChannel();
        String[] channel=signShareChannel.split("\\|");
        if(channel.length==2){
            if(channel[0].equals("1")){
                cbQQ.setChecked(true);
            }
            if(channel[1].equals("1")){
                cbWX.setChecked(true);
            }
        }
        if(qiYeInfo.getSignShareNeed().equals("01")){
            sbType4.openSwitch();
        }
        setSignShareType(qiYeInfo.getSignShareType(), FXConstant.URL_QIYE_TOUXIANG + qiYeInfo.getSignShareImage());
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

    private void selectImgs() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        menuWindow = new SelectPicPopupWindow(HomePagePosterActivity.this, 0, itemsOnClick);
        //??????????????????
        menuWindow.showAtLocation(ivImg, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.item_popupwindows_camera:        //??????????????????
                    goCamera(true);
                    break;
                case R.id.item_popupwindows_Photo:       //??????????????????????????????
                    goCamera(false);
                    break;
                default:
                    break;
            }
        }
    };

    private void goCamera(boolean mode) {
        WeakReference<HomePagePosterActivity> reference = new WeakReference<HomePagePosterActivity>(HomePagePosterActivity.this);
        FunctionOptions options = new FunctionOptions.Builder()
                .setType(FunctionConfig.TYPE_IMAGE) // ??????or?????? FunctionConfig.TYPE_IMAGE  TYPE_VIDEO
                .setCropMode(1) // ???????????? ?????????1:1???3:4???3:2???16:9
//                            .setOffsetX() // ?????????????????????
//                            .setOffsetY() // ?????????????????????
                .setCompress(false) //????????????
                .setEnablePixelCompress(true) //????????????????????????
                .setEnableQualityCompress(true) //?????????????????????
                .setMaxSelectNum(1) // ????????????????????????
                .setMinSelectNum(0)// ?????????????????????????????????????????????????????????
                .setSelectMode(FunctionConfig.MODE_SINGLE) // ?????? or ??????
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
        if (mode) {
            // ?????????
            PictureConfig.getInstance().init(options).startOpenCamera(reference.get());
        } else {
            // ??????????????????????????????????????????
            PictureConfig.getInstance().init(options).openPhoto(reference.get(), resultCallback);
        }
    }

    @SuppressLint("SdCardPath")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FunctionConfig.CAMERA_RESULT:
                    if (data != null) {
                        List<LocalMedia> medias = (List<LocalMedia>) data.getSerializableExtra(FunctionConfig.EXTRA_RESULT);
                        if (medias != null) {
                            LocalMedia media = medias.get(0);
                            int type = media.getType();
                            if (media.isCut() && !media.isCompressed()) {
                                // ?????????
                                path = media.getCutPath();
                            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                                // ?????????,???????????????????????????,??????????????????????????????
                                path = media.getCompressPath();
                            } else {
                                // ??????
                                path = media.getPath();
                            }
                            if (media.isCompressed()) {
                                Log.i("compress image result", new File(media.getCompressPath()).length() / 1024 + "k");
                                Log.i("????????????::", media.getPath());
                                Log.i("????????????::", media.getCompressPath());
                            }
                            Glide.with(HomePagePosterActivity.this)
                                    .load(path)
                                    .centerCrop()
                                    .placeholder(R.color.color_f6)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivImg);
                        }
                    }
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * ??????????????????
     */
    private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            LocalMedia media = resultList.get(0);
            int type = media.getType();
            if (media.isCut() && !media.isCompressed()) {
                // ?????????
                path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                // ?????????,???????????????????????????,??????????????????????????????
                path = media.getCompressPath();
            } else {
                // ??????
                path = media.getPath();
            }
            if (media.isCompressed()) {
                Log.i("compress image result", new File(media.getCompressPath()).length() / 1024 + "k");
                Log.i("????????????::", media.getPath());
                Log.i("????????????::", media.getCompressPath());
            }
            Glide.with(HomePagePosterActivity.this)
                    .load(path)
                    .centerCrop()
                    .placeholder(R.color.color_f6)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivImg);
        }

        @Override
        public void onSelectSuccess(final LocalMedia media) {
            int type = media.getType();
            if (media.isCut() && !media.isCompressed()) {
                // ?????????
                path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                // ?????????,???????????????????????????,??????????????????????????????
                path = media.getCompressPath();
            } else {
                // ??????
                path = media.getPath();
            }
            if (media.isCompressed()) {
                Log.i("compress image result", new File(media.getCompressPath()).length() / 1024 + "k");
                Log.i("????????????::", media.getPath());
                Log.i("????????????::", media.getCompressPath());
            }
            Glide.with(HomePagePosterActivity.this)
                    .load(path)
                    .centerCrop()
                    .placeholder(R.color.color_f6)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivImg);
        }
    };

}

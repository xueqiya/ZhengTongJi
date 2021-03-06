package com.sangu.apptongji.main.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;
import com.sangu.apptongji.R;
import com.sangu.apptongji.main.FXConstant;
import com.sangu.apptongji.main.adapter.GridImageAdapter;
import com.sangu.apptongji.main.alluser.order.avtivity.DatePickActivity;
import com.sangu.apptongji.main.mapsearch.MapPickerActivity;
import com.sangu.apptongji.main.utils.FullyGridLayoutManager;
import com.sangu.apptongji.main.utils.OkHttpManager;
import com.sangu.apptongji.main.utils.Param;
import com.sangu.apptongji.ui.BaseActivity;
import com.sangu.apptongji.utils.WeiboDialogUtils;
import com.yalantis.ucrop.entity.LocalMedia;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CompanyMemorandumActivity extends BaseActivity {


    private RecyclerView recyclerView;
    private GridImageAdapter adapter;
    private ArrayList<String> imagePaths1=new ArrayList<>();
    private List<LocalMedia> selectMedia1 = new ArrayList<>();
    private String biaoshi;
    private RelativeLayout rl_selectdate,rl_selectAdress;
    private TextView tv_date,tv_adress;
    private String mylat="",mylon="",street="";
    private TextView tv_title2;
    private String companyId,memoTime,selectTime;
    private TextView tv_memoTitle,tv_memoRemark;
    private Dialog mWeiboDialog;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setContentView(R.layout.activity_companymemorandum);

        companyId = getIntent().getStringExtra("companyId");
        memoTime = getIntent().getStringExtra("memoTime");

        initView();

    }


    private void initView(){

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(CompanyMemorandumActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(CompanyMemorandumActivity.this, onAddPicClickListener1);
        adapter.setList(selectMedia1);
        adapter.setSelectMax(4);
        //adapter.setType(1);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                switch (selectMedia1.get(position).getType()) {
                    case FunctionConfig.TYPE_IMAGE:
                        // ???????????? ??????????????? ???????????????????????????
//                        PictureConfig.getInstance().externalPicturePreview(MainActivity.this, "/custom_file", position, selectMedia);
                        PictureConfig.getInstance().externalPicturePreview(CompanyMemorandumActivity.this, position, selectMedia1);
                        break;
                    case FunctionConfig.TYPE_VIDEO:
                        // ????????????
                        if (selectMedia1.size() > 0) {
                            PictureConfig.getInstance().externalPictureVideo(CompanyMemorandumActivity.this, selectMedia1.get(position).getPath());
                        }
                        break;
                }
            }
        });


        tv_date = (TextView) findViewById(R.id.tv_date);
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = dateFormat.format(date);
        selectTime = currentTime;
        currentTime = currentTime.substring(0, 4) + "???" + currentTime.substring(4, 6) + "???" + currentTime.substring(6, 8) + "??? "
                + currentTime.substring(8, 10) + ":" + currentTime.substring(10, 12);
        tv_date.setText(currentTime);
        rl_selectdate = (RelativeLayout) findViewById(R.id.rl_selectdate);
        rl_selectdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent3 = new Intent(CompanyMemorandumActivity.this, DatePickActivity.class);
                intent3.putExtra("date", "");
                startActivityForResult(intent3, 0);

            }
        });

        tv_adress = (TextView) findViewById(R.id.tv_adress);
        rl_selectAdress = (RelativeLayout) findViewById(R.id.rl_selectAdress);
        rl_selectAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(CompanyMemorandumActivity.this, MapPickerActivity.class).putExtra("biaoshi","01"),1);
            }
        });


        tv_memoTitle = (TextView) findViewById(R.id.tv_memoTitle);
        tv_memoRemark = (TextView) findViewById(R.id.tv_memoRemark);

        tv_title2 = (TextView) findViewById(R.id.tv_title2);
        tv_title2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //???????????????

                final LayoutInflater inflater1 = LayoutInflater.from(CompanyMemorandumActivity.this);
                RelativeLayout layout1 = (RelativeLayout) inflater1.inflate(R.layout.dialog_alert, null);
                final Dialog collectionDialog = new AlertDialog.Builder(CompanyMemorandumActivity.this, R.style.Dialog).create();
                collectionDialog.show();
                collectionDialog.getWindow().setContentView(layout1);
                collectionDialog.setCanceledOnTouchOutside(true);
                collectionDialog.setCancelable(true);
                TextView title_tv1 = (TextView) layout1.findViewById(R.id.title_tv);
                Button btnCancel1 = (Button) layout1.findViewById(R.id.btn_cancel);
                final Button btnOK1 = (Button) layout1.findViewById(R.id.btn_ok);
                TextView title = (TextView) layout1.findViewById(R.id.tv_title);
                title.setText("??????");
                btnOK1.setText("??????");
                btnCancel1.setText("??????");

                title_tv1.setText("??????????????????????????????");

                btnCancel1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        collectionDialog.dismiss();
                    }
                });

                btnOK1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        collectionDialog.dismiss();

                        InsertCompanyMemo();

                    }
                });

            }
        });

    }


    //???????????????
    private void InsertCompanyMemo (){

        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(CompanyMemorandumActivity.this, "?????????...");

        String url = FXConstant.URL_INSERTMEMO;
        List<Param> params=new ArrayList<>();

        params.add(new Param("companyid", companyId));


        params.add(new Param("type", "0"));
        params.add(new Param("uid", "0"));


        params.add(new Param("memotype", "0"));
        params.add(new Param("memotypeid", "0"));
        params.add(new Param("memotime", memoTime));
        params.add(new Param("content", tv_memoTitle.getText().toString().trim()));
        params.add(new Param("begintime", selectTime));
        params.add(new Param("endtime", "0"));
        params.add(new Param("remindtime", "10"));

        if (tv_memoRemark.getText().toString().trim().length() != 0){

            params.add(new Param("comment", tv_memoRemark.getText().toString().trim()));
        }

        if (mylat.length()!=0 && mylon.length() != 0){

            params.add(new Param("resv1", mylat));
            params.add(new Param("resv2", mylon));

        }

        if (street.length() != 0){
            params.add(new Param("region", street));
        }

        OkHttpManager.getInstance().posts2(params, "imageFile", imagePaths1, null, new ArrayList<String>(), null, "", null, "", null, "", null, ""
                , null, "", null, "", null, "", null, "", url, new OkHttpManager.HttpCallBack() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        WeiboDialogUtils.closeDialog(mWeiboDialog);

                        if (jsonObject.getString("code").equals("SUCCESS")){

                            Toast.makeText(getApplicationContext(), "???????????????", Toast.LENGTH_SHORT).show();
                            finish();

                        }else {

                            Toast.makeText(getApplicationContext(), "???????????????", Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        WeiboDialogUtils.closeDialog(mWeiboDialog);
                        Log.e("dianzidanjuac,e", errorMsg);
                        Toast.makeText(getApplicationContext(), "???????????????", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode==RESULT_OK){

            switch (requestCode){
                case 0:
                    if (data!=null){
                        String qingjiaQi = data.getStringExtra("date");
                        selectTime = qingjiaQi;
                        qingjiaQi = qingjiaQi.substring(0, 4) + "???" + qingjiaQi.substring(4, 6) + "???" + qingjiaQi.substring(6, 8) + "??? "
                                + qingjiaQi.substring(8, 10) + ":" + qingjiaQi.substring(10, 12);
                        tv_date.setText(qingjiaQi);
                    }
                    break;
                case 1:
                    if (data!=null){

                        mylat = data.getStringExtra("latitude");
                        mylon = data.getStringExtra("longitude");
                        street = data.getStringExtra("street");
                        tv_adress.setText(street);

                    }
                    break;

            }

        }

    }

    /**
     * ????????????????????????
     */
    private GridImageAdapter.onAddPicClickListener onAddPicClickListener1 = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick(int type, int position) {
            switch (type) {
                case 0:
                    goCamera(4,"01");
                    break;
                case 1:
                    // ????????????
                    imagePaths1.remove(position);
                    selectMedia1.remove(position);
                    adapter.notifyItemRemoved(position);
                    break;
            }
        }
    };


    private void goCamera(int count,String type){
        List<LocalMedia> medias = new ArrayList<>();
        if ("01".equals(type)) {
            medias = selectMedia1;
        } else {
          //  medias = selectMedia2;
        }
        WeakReference<CompanyMemorandumActivity> reference = new WeakReference<CompanyMemorandumActivity>(CompanyMemorandumActivity.this);
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
                .setSelectMedia(medias) // ?????????????????????????????????????????????????????????????????????
                .setCompressFlag(1) // 1 ?????????????????? 2 luban??????
                .setCompressW(0) // ????????? ???????????????????????????????????????
                .setCompressH(0) // ????????? ???????????????????????????????????????
                .setThemeStyle(ContextCompat.getColor(reference.get(), R.color.blue)) // ??????????????????
                .setNumComplete(false) // 0/9 ??????  ??????
                .setClickVideo(false)// ????????????
                .setFreeStyleCrop(false) // ????????????????????????????????????
                .create();
        if ("01".equals(type)) {
            //
            PictureConfig.getInstance().init(options).openPhoto(reference.get(), resultCallback1);
        } else {
            //
          //  PictureConfig.getInstance().init(options).openPhoto(reference.get(), resultCallback2);
        }
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
            Log.i("callBack_result", selectMedia1.size() + "");
            LocalMedia media = resultList.get(0);
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
                adapter.setList(selectMedia1);
                adapter.notifyDataSetChanged();
                for (int i=0;i<selectMedia1.size();i++){
                    imagePaths1.add(selectMedia1.get(i).getCompressPath());
                }
            }
        }

        @Override
        public void onSelectSuccess(LocalMedia media) {
            imagePaths1.clear();
            // ????????????
            selectMedia1.add(media);
            if (selectMedia1 != null) {
                adapter.setList(selectMedia1);
                adapter.notifyDataSetChanged();
                for (int i=0;i<selectMedia1.size();i++){
                    imagePaths1.add(selectMedia1.get(i).getCompressPath());
                }
            }
        }
    };


}

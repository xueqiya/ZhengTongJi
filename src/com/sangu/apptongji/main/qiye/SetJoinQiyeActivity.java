package com.sangu.apptongji.main.qiye;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.fanxin.easeui.utils.FileStorage;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;
import com.sangu.apptongji.R;
import com.sangu.apptongji.main.FXConstant;
import com.sangu.apptongji.main.adapter.GridImageAdapter;
import com.sangu.apptongji.main.utils.FullyGridLayoutManager;
import com.sangu.apptongji.main.utils.OkHttpManager;
import com.sangu.apptongji.main.utils.Param;
import com.sangu.apptongji.main.widget.CustomProgressDialog;
import com.sangu.apptongji.ui.BaseActivity;
import com.sangu.apptongji.utils.SelectPicPopupWindow;
import com.yalantis.ucrop.entity.LocalMedia;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017-04-28.
 */

public class SetJoinQiyeActivity extends BaseActivity {

    private CheckBox cb_shoufeijiaru;
    private EditText et_jiarujine;
    private EditText et_content;
    private Button btn_commit;
    private ArrayList<String> imagePaths1=null;
    private List<LocalMedia> selectMedia = new ArrayList<>();
    private RecyclerView recyclerView;
    private GridImageAdapter adapter;
    private SelectPicPopupWindow menuWindow=null;

    private String image=null,body=null,feiyong=null;
    private String [] str;
    public String biaoshi,companyName,companId;
    private boolean istuFirst = true;
    HeadThread headThread=null;
    CustomProgressDialog pd = null;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if (pd!=null&&pd.isShowing()) {
                        pd.dismiss();
                    }
                    adapter.notifyDataSetChanged();
                    if (headThread!=null) {
                        headThread.interrupt();
                        headThread = null;
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (headThread!=null) {
            headThread.interrupt();
            headThread = null;
        }
        if (selectMedia!=null){
            selectMedia.clear();
            selectMedia=null;
        }
        if (imagePaths1!=null){
            imagePaths1.clear();
            imagePaths1=null;
        }
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_joinqiye_set);
        WeakReference<SetJoinQiyeActivity> reference =  new WeakReference<SetJoinQiyeActivity>(SetJoinQiyeActivity.this);
        pd = CustomProgressDialog.createDialog(reference.get());
        pd.setMessage("??????????????????...");
        pd.setCancelable(true);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                pd.dismiss();
            }
        });
        pd.show();
        imagePaths1 = new ArrayList<>();
        body = this.getIntent().getStringExtra("body");
        feiyong = this.getIntent().getStringExtra("feiyong");
        image = this.getIntent().getStringExtra("images");
        biaoshi = this.getIntent().getStringExtra("biaoshi");
        companId = this.getIntent().getStringExtra("companId");
        companyName = this.getIntent().getStringExtra("companyName");
        initView();
        initViews();
    }
    private void initView() {
        btn_commit = (Button) findViewById(R.id.btn_commit);
        et_content = (EditText) findViewById(R.id.et_content);
        et_jiarujine = (EditText) findViewById(R.id.et_jiarujine);
        cb_shoufeijiaru = (CheckBox) findViewById(R.id.cb_shoufeijiaru);
        et_jiarujine.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_jiarujine.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable edt)
            {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2)
                {
                    edt.delete(posDot + 3, posDot + 4);
                }
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });
        et_content.setText(body);
        if (feiyong!=null&&Double.parseDouble(feiyong)>0){
            cb_shoufeijiaru.setChecked(true);
            et_jiarujine.setText(feiyong);
            et_jiarujine.setEnabled(true);
        }else {
            et_jiarujine.setEnabled(false);
            cb_shoufeijiaru.setChecked(false);
        }
        cb_shoufeijiaru.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb_shoufeijiaru.isChecked()){
                    et_jiarujine.setEnabled(true);
                }else {
                    et_jiarujine.setEnabled(false);
                    et_jiarujine.setText(null);
                }
            }
        });
        Log.e("setjoinqiye,2",image);
        if (image!=null&&!"".equals(image)) {
            if (istuFirst) {
                if (headThread == null) {
                    headThread = new HeadThread();
                    headThread.start();
                }
            }
        }else {
            if (pd!=null&&pd.isShowing()) {
                pd.dismiss();
            }
        }
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = et_content.getText().toString().trim();
                String feiyong = et_jiarujine.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(getApplicationContext(), "?????????????????????....",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (imagePaths1.size() == 0) {
                    Toast.makeText(getApplicationContext(), "???????????????....",
                            Toast.LENGTH_SHORT).show();
                    return;
                }else if (imagePaths1.size()>8){
                    Toast.makeText(getApplicationContext(), "????????????????????????....",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pd!=null&&!pd.isShowing()) {
                    pd.show();
                }
                String key = null;
                List<Param> params=new ArrayList<>();
                params.add(new Param("companyName", companyName));
                params.add(new Param("companyId", companId));
                if (biaoshi.equals("0")){
                    key = "recruitImages";
                    params.add(new Param("recruitBody",content));
                    if (cb_shoufeijiaru.isChecked()) {
                        params.add(new Param("recruitMoney", feiyong));
                    }else {
                        params.add(new Param("recruitMoney", "0"));
                    }
                }else {
                    key = "joinImages";
                    params.add(new Param("joinBody",content));
                    if (cb_shoufeijiaru.isChecked()) {
                        params.add(new Param("joinMoney", feiyong));
                    }else {
                        params.add(new Param("joinMoney", "0"));
                    }
                }
                OkHttpManager.getInstance().posts(params,key,imagePaths1, FXConstant.URL_UPDATE_QIYE, new OkHttpManager.HttpCallBack() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if (pd!=null&&pd.isShowing()) {
                            pd.dismiss();
                        }
                        Log.e("param,obj",jsonObject.toJSONString());
                        String code = jsonObject.getString("code");
                        Log.e("param,code",code);
                        if (code.equals("SUCCESS")) {
                            Toast.makeText(getApplicationContext(), "????????????",Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),"??????????????????????????????",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(String errorMsg) {
                        if (pd!=null&&pd.isShowing()) {
                            pd.dismiss();
                        }
                        Toast.makeText(getApplicationContext(),"??????????????????",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void initViews(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(SetJoinQiyeActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(SetJoinQiyeActivity.this, onAddPicClickListener);
        adapter.setList(selectMedia);
        adapter.setSelectMax(8);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                switch (selectMedia.get(position).getType()) {
                    case FunctionConfig.TYPE_IMAGE:
                        // ???????????? ??????????????? ???????????????????????????
//                        PictureConfig.getInstance().externalPicturePreview(MainActivity.this, "/custom_file", position, selectMedia);
                        PictureConfig.getInstance().externalPicturePreview(SetJoinQiyeActivity.this, position, selectMedia);
                        break;
                    case FunctionConfig.TYPE_VIDEO:
                        // ????????????
                        if (selectMedia.size() > 0) {
                            PictureConfig.getInstance().externalPictureVideo(SetJoinQiyeActivity.this, selectMedia.get(position).getPath());
                        }
                        break;
                }
            }
        });
    }
    /**
     * ????????????????????????
     */
    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick(int type, int position) {
            switch (type) {
                case 0:
                    selectImgs();
                    break;
                case 1:
                    // ????????????
                    imagePaths1.remove(position);
                    selectMedia.remove(position);
                    adapter.notifyItemRemoved(position);
                    break;
            }
        }
    };

    private void selectImgs(){
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(SetJoinQiyeActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        menuWindow = new SelectPicPopupWindow(SetJoinQiyeActivity.this,0,itemsOnClick);
        //??????????????????
        menuWindow.showAtLocation(SetJoinQiyeActivity.this.findViewById(R.id.llImage), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
    private void goCamera(boolean mode){
        WeakReference<SetJoinQiyeActivity> reference = new WeakReference<SetJoinQiyeActivity>(SetJoinQiyeActivity.this);
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
        if (mode) {
            // ?????????
            PictureConfig.getInstance().init(options).startOpenCamera(reference.get());
        } else {
            // ??????????????????????????????????????????
            PictureConfig.getInstance().init(options).openPhoto(reference.get(), resultCallback);
        }
    }

    /**
     * ??????????????????
     */
    private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            imagePaths1.clear();
            // ????????????
            selectMedia = resultList;
            Log.i("callBack_result", selectMedia.size() + "");
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
            if (selectMedia != null) {
                adapter.setList(selectMedia);
                adapter.notifyDataSetChanged();
                for (int i=0;i<selectMedia.size();i++){
                    loadImage2(selectMedia.get(i).getPath());
                }
            }
        }

        @Override
        public void onSelectSuccess(LocalMedia media) {
            imagePaths1.clear();
            // ????????????
            selectMedia.add(media);
            if (selectMedia != null) {
                adapter.setList(selectMedia);
                adapter.notifyDataSetChanged();
                for (int i=0;i<selectMedia.size();i++){
                    loadImage2(selectMedia.get(i).getPath());
                }
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FunctionConfig.CAMERA_RESULT:
                    if (data != null) {
                        List<LocalMedia> medias = (List<LocalMedia>) data.getSerializableExtra(FunctionConfig.EXTRA_RESULT);
                        if (selectMedia != null) {
                            selectMedia.add(medias.get(0));
                            adapter.setList(selectMedia);
                            adapter.notifyDataSetChanged();
                            imagePaths1.add(medias.get(0).getPath());
                        }
                    }
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void back(View view) {
        super.back(view);
    }

    class HeadThread extends Thread{
        @Override
        public void run() {
            super.run();
            if (istuFirst){
                File f1,f2,f3,f4,f5,f6,f7,f8;
                if (image!=null&&!"".equals(image)&&image.length()>1) {
                    str = image.split("\\|");
                    if (str.length > 0) {
                        f1 = new FileStorage("qiyeImage").createCropFile(str[0],null);
                        if (f1.exists()) {
                            loadImage1(Environment.getExternalStorageDirectory()+"/zhengshier/qiyeImage/"+str[0]);
                        } else {
                            uploadImage(str[0], pd);
                        }
                    }
                    if (str.length > 1) {
                        f2 = new FileStorage("qiyeImage").createCropFile(str[1],null);
                        if (f2.exists()) {
                            loadImage1(Environment.getExternalStorageDirectory()+"/zhengshier/qiyeImage/"+str[1]);
                        } else {
                            uploadImage(str[1], pd);
                        }
                    }
                    if (str.length > 2) {
                        f3 = new FileStorage("qiyeImage").createCropFile(str[2],null);
                        if (f3.exists()) {
                            loadImage1(Environment.getExternalStorageDirectory()+"/zhengshier/qiyeImage/"+str[2]);
                        } else {
                            uploadImage(str[2], pd);
                        }
                    }
                    if (str.length > 3) {
                        f4 = new FileStorage("qiyeImage").createCropFile(str[3],null);
                        if (f4.exists()) {
                            loadImage1(Environment.getExternalStorageDirectory()+"/zhengshier/qiyeImage/"+str[3]);
                        } else {
                            uploadImage(str[3], pd);
                        }
                    }
                    if (str.length > 4) {
                        f5 = new FileStorage("qiyeImage").createCropFile(str[4],null);
                        if (f5.exists()) {
                            loadImage1(Environment.getExternalStorageDirectory()+"/zhengshier/qiyeImage/"+str[4]);
                        } else {
                            uploadImage(str[4], pd);
                        }
                    }
                    if (str.length > 5) {
                        f6 = new FileStorage("qiyeImage").createCropFile(str[5],null);
                        if (f6.exists()) {
                            loadImage1(Environment.getExternalStorageDirectory()+"/zhengshier/qiyeImage/"+str[5]);
                        } else {
                            uploadImage(str[5], pd);
                        }
                    }
                    if (str.length > 6) {
                        f7 = new FileStorage("qiyeImage").createCropFile(str[6],null);
                        if (f7.exists()) {
                            loadImage1(Environment.getExternalStorageDirectory()+"/zhengshier/qiyeImage/"+str[6]);
                        } else {
                            uploadImage(str[6], pd);
                        }
                    }
                    if (str.length > 7) {
                        f8 = new FileStorage("qiyeImage").createCropFile(str[7],null);
                        if (f8.exists()) {
                            loadImage1(Environment.getExternalStorageDirectory()+"/zhengshier/qiyeImage/"+str[7]);
                        } else {
                            uploadImage(str[7], pd);
                        }
                    }
                }
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
            istuFirst = false;
        }
    }
    private void uploadImage(final String image, final CustomProgressDialog pd) {
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        OkHttpClient client = new OkHttpClient();
        String url=null;
        if ("0".equals(biaoshi)){
            url = FXConstant.URL_QIYE_ZHAOPIN+image;
        }else {
            url = FXConstant.URL_QIYE_JIAMENG+image;
        }
        try {
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            InputStream is = response.body().byteStream();
            Bitmap bm = BitmapFactory.decodeStream(is);
            if (bm!=null) {
                saveToSD(bm,image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToSD(Bitmap mBitmap,String imageName) {
        File file = new FileStorage("qiyeImage").createCropFile(imageName,null);
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(SetJoinQiyeActivity.this, "com.sangu.app.fileprovider", file);//??????FileProvider????????????content?????????Uri
            grantUriPermission(getPackageName(), imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            imageUri = Uri.fromFile(file);
        }
        File f = new File(file.getPath());
        try {
            //???????????????????????????????????????
            if(!f.exists()){
                f.createNewFile();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            FileOutputStream fos = new FileOutputStream(f);
            int options = 100;
            // ????????????80kb???????????????,??????????????????
            while (baos.toByteArray().length / 1024 > 200 && options > 10) {
                // ??????baos
                baos.reset();
                // ????????????options%?????????????????????????????????baos???
                mBitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                options -= 30;
            }
            fos.write(baos.toByteArray());
            fos.close();
            baos.close();
            loadImage1(Environment.getExternalStorageDirectory()+"/zhengshier/qiyeImage/"+imageName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadImage1(String filepath){
        imagePaths1.add(filepath);
        int type = FunctionConfig.TYPE_IMAGE;
        LocalMedia media = new LocalMedia();
        media.setType(type);
        media.setCompressed(false);
        media.setCut(false);
        media.setIsChecked(true);
        media.setNum(imagePaths1.size());
        media.setPath(filepath);
        selectMedia.add(media);
    }
    private void loadImage2(String filepath){
        imagePaths1.add(filepath);
    }

}

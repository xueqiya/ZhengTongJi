package com.sangu.apptongji.main.alluser.order.avtivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
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
import com.permissionutil.PermissionListener;
import com.permissionutil.PermissionUtil;
import com.sangu.apptongji.DemoHelper;
import com.sangu.apptongji.R;
import com.sangu.apptongji.main.FXConstant;
import com.sangu.apptongji.main.adapter.GridImageAdapter;
import com.sangu.apptongji.main.address.AddressListTwoActivity;
import com.sangu.apptongji.main.alluser.entity.DianziDanju;
import com.sangu.apptongji.main.alluser.presenter.IDjDetailPresenter;
import com.sangu.apptongji.main.alluser.presenter.impl.DjDetailPresenter;
import com.sangu.apptongji.main.alluser.view.IDjDetailView;
import com.sangu.apptongji.main.utils.FullyGridLayoutManager;
import com.sangu.apptongji.main.utils.MySingleton;
import com.sangu.apptongji.main.utils.OkHttpManager;
import com.sangu.apptongji.main.utils.Param;
import com.sangu.apptongji.main.utils.ToastUtils;
import com.sangu.apptongji.ui.BaseActivity;
import com.sangu.apptongji.utils.UserPermissionUtil;
import com.yalantis.ucrop.entity.LocalMedia;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017-07-20.
 */

public class DianziDanJuFourActivity extends BaseActivity implements View.OnClickListener,IDjDetailView{
    private IDjDetailPresenter presenter;
    private String biaoshi;
    private TextView tv_baocun;
    private EditText et_miaoshu,et_name,et_phone,et_dizhi;
    private ImageView iv_sign1,iv_sign2,iv_sign3,iv_sign4,iv_sign5,iv_sign6,iv_sign7,iv_sign8;
    private TextView tv_sign1,tv_sign2,tv_sign3,tv_sign4,tv_sign5,tv_sign6,tv_sign7,tv_sign8;
    private TextView tev_sign1,tev_sign2,tev_sign3,tev_sign4,tev_sign5,tev_sign6,tev_sign7,tev_sign8;
    private TextView tv_sign1_time,tv_sign2_time,tv_sign3_time,tv_sign4_time,tv_sign5_time,tv_sign6_time,tv_sign7_time,tv_sign8_time;
    private LinearLayout ll_sign2,ll_sign3,ll_sign4;
    private View tv2,tv3,tv4;
    private RelativeLayout rl_button;
    private Button btn_tianjia,btn_quxiao;
    private int current_neirong=1;
    private String path1,path2,path3,path4,path5,path6,path7,path8,title;
    private String time1,time2,time3,time4,time5,time6,time7,time8,timestamp,image1,image2,image3,image4,image5,image6,image7,image8;
    private boolean isBaocun=false;
    private ArrayList<String> imagePaths1=new ArrayList<>();
    private ArrayList<String> imagePaths2=new ArrayList<>();
    private List<LocalMedia> selectMedia1 = new ArrayList<>();
    private List<LocalMedia> selectMedia2 = new ArrayList<>();
    private RecyclerView recyclerView;
    private GridImageAdapter adapter;
    private RecyclerView recyclerView2;
    private GridImageAdapter adapter2;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_dianzi_danju_four);
        RequestQueue queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        initView();
        initViews();
        setListener();
        biaoshi = getIntent().getStringExtra("biaoshi");
        timestamp = getIntent().getStringExtra("timestamp");
        if ("00".equals(biaoshi)){
            isBaocun = true;
        }else {
            isBaocun = false;
            tv_baocun.setText("发送");
            presenter = new DjDetailPresenter(this,this);
            presenter.loadDZdjDetail(DemoHelper.getInstance().getCurrentUsernName(),timestamp);
        }
    }

    private void initViews(){
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(DianziDanJuFourActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(DianziDanJuFourActivity.this, onAddPicClickListener1);
        adapter.setList(selectMedia1);
        adapter.setSelectMax(4);
        adapter.setType(1);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                switch (selectMedia1.get(position).getType()) {
                    case FunctionConfig.TYPE_IMAGE:
                        // 预览图片 可长按保存 也可自定义保存路径
//                        PictureConfig.getInstance().externalPicturePreview(MainActivity.this, "/custom_file", position, selectMedia);
                        PictureConfig.getInstance().externalPicturePreview(DianziDanJuFourActivity.this, position, selectMedia1);
                        break;
                    case FunctionConfig.TYPE_VIDEO:
                        // 预览视频
                        if (selectMedia1.size() > 0) {
                            PictureConfig.getInstance().externalPictureVideo(DianziDanJuFourActivity.this, selectMedia1.get(position).getPath());
                        }
                        break;
                }
            }
        });
        recyclerView2 = (RecyclerView) findViewById(R.id.recycler2);
        FullyGridLayoutManager manager2 = new FullyGridLayoutManager(DianziDanJuFourActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView2.setLayoutManager(manager2);
        adapter2 = new GridImageAdapter(DianziDanJuFourActivity.this, onAddPicClickListener2);
        adapter2.setList(selectMedia2);
        adapter2.setSelectMax(4);
        adapter2.setType(2);
        recyclerView2.setAdapter(adapter2);
        adapter2.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                switch (selectMedia2.get(position).getType()) {
                    case FunctionConfig.TYPE_IMAGE:
                        // 预览图片 可长按保存 也可自定义保存路径
//                        PictureConfig.getInstance().externalPicturePreview(MainActivity.this, "/custom_file", position, selectMedia);
                        PictureConfig.getInstance().externalPicturePreview(DianziDanJuFourActivity.this, position, selectMedia2);
                        break;
                    case FunctionConfig.TYPE_VIDEO:
                        // 预览视频
                        if (selectMedia2.size() > 0) {
                            PictureConfig.getInstance().externalPictureVideo(DianziDanJuFourActivity.this, selectMedia2.get(position).getPath());
                        }
                        break;
                }
            }
        });
    }

    /**
     * 删除图片回调接口
     */
    private GridImageAdapter.onAddPicClickListener onAddPicClickListener1 = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick(int type, int position) {
            switch (type) {
                case 0:
                    if ("00".equals(biaoshi)){
                        goCamera(4,"01");
                    }
                    break;
                case 1:
                    // 删除图片
                    if ("00".equals(biaoshi)) {
                        imagePaths1.remove(position);
                        selectMedia1.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                    break;
            }
        }
    };
    /**
     * 删除图片回调接口
     */
    private GridImageAdapter.onAddPicClickListener onAddPicClickListener2 = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick(int type, int position) {
            switch (type) {
                case 0:
                    if ("00".equals(biaoshi)) {
                        goCamera(4, "02");
                    }
                    break;
                case 1:
                    // 删除图片
                    if ("00".equals(biaoshi)) {
                        imagePaths2.remove(position);
                        selectMedia2.remove(position);
                        adapter2.notifyItemRemoved(position);
                    }
                    break;
            }
        }
    };

    private void goCamera(int count,String type){
        List<LocalMedia> medias = new ArrayList<>();
        if ("01".equals(type)) {
            medias = selectMedia1;
        } else {
            medias = selectMedia2;
        }
        WeakReference<DianziDanJuFourActivity> reference = new WeakReference<DianziDanJuFourActivity>(DianziDanJuFourActivity.this);
        FunctionOptions options = new FunctionOptions.Builder()
                .setType(FunctionConfig.TYPE_IMAGE) // 图片or视频 FunctionConfig.TYPE_IMAGE  TYPE_VIDEO
                .setCropMode(1) // 裁剪模式 默认、1:1、3:4、3:2、16:9
//                            .setOffsetX() // 自定义裁剪比例
//                            .setOffsetY() // 自定义裁剪比例
                .setCompress(true) //是否压缩
                .setEnablePixelCompress(true) //是否启用像素压缩
                .setEnableQualityCompress(true) //是否启质量压缩
                .setMaxSelectNum(count) // 可选择图片的数量
                .setMinSelectNum(0)// 图片或视频最低选择数量，默认代表无限制
                .setSelectMode(FunctionConfig.MODE_MULTIPLE) // 单选 or 多选
                .setShowCamera(false) //是否显示拍照选项 这里自动根据type 启动拍照或录视频
                .setEnablePreview(true) // 是否打开预览选项
                .setEnableCrop(false) // 是否打开剪切选项
                .setCircularCut(false)// 是否采用圆形裁剪
                .setPreviewVideo(true) // 是否预览视频(播放) mode or 多选有效
                .setCheckedBoxDrawable(0)
                .setRecordVideoDefinition(FunctionConfig.HIGH) // 视频清晰度
                .setRecordVideoSecond(60) // 视频秒数
                .setCustomQQ_theme(0)// 可自定义QQ数字风格，不传就默认是蓝色风格
                .setGif(false)// 是否显示gif图片，默认不显示
                .setCropW(0) // cropW-->裁剪宽度 值不能小于100  如果值大于图片原始宽高 将返回原图大小
                .setCropH(0) // cropH-->裁剪高度 值不能小于100 如果值大于图片原始宽高 将返回原图大小
                .setMaxB(102400) // 压缩最大值 例如:200kb  就设置202400，202400 / 1024 = 200kb
                .setPreviewColor(ContextCompat.getColor(reference.get(), R.color.blue)) //预览字体颜色
                .setCompleteColor(ContextCompat.getColor(reference.get(), R.color.blue)) //已完成字体颜色
                .setPreviewBottomBgColor(0) //预览图片底部背景色
                .setPreviewTopBgColor(0)//预览图片标题背景色
                .setBottomBgColor(0) //图片列表底部背景色
                .setGrade(Luban.THIRD_GEAR) // 压缩档次 默认三档
                .setCheckNumMode(false)
                .setCompressQuality(100) // 图片裁剪质量,默认无损
                .setImageSpanCount(4) // 每行个数
                .setVideoS(0)// 查询多少秒内的视频 单位:秒
                .setSelectMedia(medias) // 已选图片，传入在次进去可选中，不能传入网络图片
                .setCompressFlag(1) // 1 系统自带压缩 2 luban压缩
                .setCompressW(0) // 压缩宽 如果值大于图片原始宽高无效
                .setCompressH(0) // 压缩高 如果值大于图片原始宽高无效
                .setThemeStyle(ContextCompat.getColor(reference.get(), R.color.blue)) // 设置主题样式
                .setNumComplete(false) // 0/9 完成  样式
                .setClickVideo(false)// 点击声音
                .setFreeStyleCrop(false) // 裁剪是移动矩形框或是图片
                .create();
        if ("01".equals(type)) {
            // 只拍照
            PictureConfig.getInstance().init(options).openPhoto(reference.get(), resultCallback1);
        } else {
            // 先初始化参数配置，在启动相册
            PictureConfig.getInstance().init(options).openPhoto(reference.get(), resultCallback2);
        }
    }

    /**
     * 图片回调方法
     */
    private PictureConfig.OnSelectResultCallback resultCallback1 = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            imagePaths1.clear();
            // 多选回调
            selectMedia1 = resultList;
            Log.i("callBack_result", selectMedia1.size() + "");
            LocalMedia media = resultList.get(0);
            String path;
            if (media.isCut() && !media.isCompressed()) {
                // 裁剪过
                path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                path = media.getCompressPath();
            } else {
                // 原图地址
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
            // 单选回调
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
    /**
     * 图片回调方法
     */
    private PictureConfig.OnSelectResultCallback resultCallback2 = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            imagePaths2.clear();
            // 多选回调
            selectMedia2 = resultList;
            Log.i("callBack_result", selectMedia2.size() + "");
            LocalMedia media = resultList.get(0);
            if (media.isCut() && !media.isCompressed()) {
                // 裁剪过
                String path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                String path = media.getCompressPath();
            } else {
                // 原图地址
                String path = media.getPath();
            }
            if (selectMedia2 != null) {
                adapter2.setList(selectMedia2);
                adapter2.notifyDataSetChanged();
                for (int i=0;i<selectMedia2.size();i++){
                    imagePaths2.add(selectMedia2.get(i).getCompressPath());
                }
            }
        }

        @Override
        public void onSelectSuccess(LocalMedia media) {
            imagePaths2.clear();
            // 单选回调
            selectMedia2.add(media);
            if (selectMedia2 != null) {
                adapter2.setList(selectMedia2);
                adapter2.notifyDataSetChanged();
                for (int i=0;i<selectMedia2.size();i++){
                    imagePaths2.add(selectMedia2.get(i).getCompressPath());
                }
            }
        }
    };

    @Override
    public void back(View view) {
        super.back(view);
    }

    private void setListener() {
        tv_sign1.setOnClickListener(this);
        tv_sign2.setOnClickListener(this);
        tv_sign3.setOnClickListener(this);
        tv_sign4.setOnClickListener(this);
        tv_sign5.setOnClickListener(this);
        tv_sign6.setOnClickListener(this);
        tv_sign7.setOnClickListener(this);
        tv_sign8.setOnClickListener(this);
        tv_baocun.setOnClickListener(this);
        btn_tianjia.setOnClickListener(this);
        btn_quxiao.setOnClickListener(this);
    }

    private void initView() {
        tv_baocun = (TextView) findViewById(R.id.tv_baocun);
        tv_sign1 = (TextView) findViewById(R.id.tv_sign1);
        tv_sign2 = (TextView) findViewById(R.id.tv_sign2);
        tv_sign3 = (TextView) findViewById(R.id.tv_sign3);
        tv_sign4 = (TextView) findViewById(R.id.tv_sign4);
        tv_sign5 = (TextView) findViewById(R.id.tv_sign5);
        tv_sign6 = (TextView) findViewById(R.id.tv_sign6);
        tv_sign7 = (TextView) findViewById(R.id.tv_sign7);
        tv_sign8 = (TextView) findViewById(R.id.tv_sign8);
        tev_sign1 = (TextView) findViewById(R.id.tev_sign1);
        tev_sign2 = (TextView) findViewById(R.id.tev_sign2);
        tev_sign3 = (TextView) findViewById(R.id.tev_sign3);
        tev_sign4 = (TextView) findViewById(R.id.tev_sign4);
        tev_sign5 = (TextView) findViewById(R.id.tev_sign5);
        tev_sign6 = (TextView) findViewById(R.id.tev_sign6);
        tev_sign7 = (TextView) findViewById(R.id.tev_sign7);
        tev_sign8 = (TextView) findViewById(R.id.tev_sign8);
        tv_sign1_time = (TextView) findViewById(R.id.tv_sign1_time);
        tv_sign2_time = (TextView) findViewById(R.id.tv_sign2_time);
        tv_sign3_time = (TextView) findViewById(R.id.tv_sign3_time);
        tv_sign4_time = (TextView) findViewById(R.id.tv_sign4_time);
        tv_sign5_time = (TextView) findViewById(R.id.tv_sign5_time);
        tv_sign6_time = (TextView) findViewById(R.id.tv_sign6_time);
        tv_sign7_time = (TextView) findViewById(R.id.tv_sign7_time);
        tv_sign8_time = (TextView) findViewById(R.id.tv_sign8_time);
        iv_sign1 = (ImageView) findViewById(R.id.iv_sign1);
        iv_sign2 = (ImageView) findViewById(R.id.iv_sign2);
        iv_sign3 = (ImageView) findViewById(R.id.iv_sign3);
        iv_sign4 = (ImageView) findViewById(R.id.iv_sign4);
        iv_sign5 = (ImageView) findViewById(R.id.iv_sign5);
        iv_sign6 = (ImageView) findViewById(R.id.iv_sign6);
        iv_sign7 = (ImageView) findViewById(R.id.iv_sign7);
        iv_sign8 = (ImageView) findViewById(R.id.iv_sign8);
        et_miaoshu = (EditText) findViewById(R.id.et_miaoshu);
        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_dizhi = (EditText) findViewById(R.id.et_dizhi);
        ll_sign2 = (LinearLayout) findViewById(R.id.ll_sign2);
        ll_sign3 = (LinearLayout) findViewById(R.id.ll_sign3);
        ll_sign4 = (LinearLayout) findViewById(R.id.ll_sign4);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        rl_button = (RelativeLayout) findViewById(R.id.rl_button);
        btn_tianjia = (Button) findViewById(R.id.btn_tianjia);
        btn_quxiao = (Button) findViewById(R.id.btn_quxiao);
    }

    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(date);
    }
    private void sendPushMessage2(final String userId) {
        final String myId = DemoHelper.getInstance().getCurrentUsernName();
        String url = FXConstant.URL_SENDPUSHMSG;
        StringRequest request = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("u_id",userId);
                param.put("body","电子单据消息");
                param.put("type","11");
                param.put("userId",myId);
                param.put("companyId","0");
                param.put("companyName","0");
                param.put("companyAdress","0");
                param.put("dynamicSeq","0");
                param.put("createTime","0");
                param.put("fristId","0");
                param.put("dType","0");
                return param;
            }
        };
        MySingleton.getInstance(DianziDanJuFourActivity.this).addToRequestQueue(request);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_baocun:
                if (isBaocun){
                    final String biaoti = et_miaoshu.getText().toString().trim();
                    String name = TextUtils.isEmpty(et_name.getText().toString().trim())?"0":et_name.getText().toString().trim();
                    String phone = TextUtils.isEmpty(et_phone.getText().toString().trim())?"0":et_phone.getText().toString().trim();
                    String dizhi = TextUtils.isEmpty(et_dizhi.getText().toString().trim())?"0":et_dizhi.getText().toString().trim();
                    final String neirong = name+"|"+phone+"|"+dizhi;
                    if (TextUtils.isEmpty(biaoti)){
                        Toast.makeText(getApplicationContext(),"请输入标题",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    LayoutInflater inflater1 = LayoutInflater.from(DianziDanJuFourActivity.this);
                    RelativeLayout layout1 = (RelativeLayout) inflater1.inflate(R.layout.dialog_alert, null);
                    final Dialog dialog1 = new AlertDialog.Builder(DianziDanJuFourActivity.this,R.style.Dialog).create();
                    dialog1.show();
                    dialog1.getWindow().setContentView(layout1);
                    dialog1.setCanceledOnTouchOutside(true);
                    dialog1.setCancelable(true);
                    TextView title_tv1 = (TextView) layout1.findViewById(R.id.title_tv);
                    Button btnCancel1 = (Button) layout1.findViewById(R.id.btn_cancel);
                    final Button btnOK1 = (Button) layout1.findViewById(R.id.btn_ok);
                    TextView title = (TextView) layout1.findViewById(R.id.tv_title);
                    title.setText("温馨提示");
                    btnOK1.setText("确定");
                    btnCancel1.setText("取消");
                    title_tv1.setText("确定保存该电子单据吗?");
                    btnCancel1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog1.dismiss();
                        }
                    });
                    btnOK1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog1.dismiss();
                            String url = FXConstant.URL_UPDATE_DZ_DANJU;
                            List<Param> params=new ArrayList<>();
                            if ("00".equals(biaoshi)) {
                                url = FXConstant.URL_INSERT_DZ_DANJU;
                                params.add(new Param("u_id", DemoHelper.getInstance().getCurrentUsernName()));
                                params.add(new Param("content", neirong));
                                params.add(new Param("title", biaoti));
                                params.add(new Param("flag", "04"));
                            }else {
                                params.add(new Param("timestamp", timestamp));
                                params.add(new Param("u_id", DemoHelper.getInstance().getCurrentUsernName()));
                            }
                            if (path1!=null&&!"".equals(path1)){
                                params.add(new Param("time1", getNowTime()));
                            }
                            if (path2!=null&&!"".equals(path2)){
                                params.add(new Param("time2", getNowTime()));
                            }
                            if (path3!=null&&!"".equals(path3)){
                                params.add(new Param("time3", getNowTime()));
                            }
                            if (path4!=null&&!"".equals(path4)){
                                params.add(new Param("time4", getNowTime()));
                            }
                            if (path5!=null&&!"".equals(path5)){
                                params.add(new Param("time5", getNowTime()));
                            }
                            if (path6!=null&&!"".equals(path6)){
                                params.add(new Param("time6", getNowTime()));
                            }
                            if (path7!=null&&!"".equals(path7)){
                                params.add(new Param("time7", getNowTime()));
                            }
                            if (path8!=null&&!"".equals(path8)){
                                params.add(new Param("time8", getNowTime()));
                            }
                            if ("00".equals(biaoshi)) {
                                OkHttpManager.getInstance().posts2(params, "beforeService", imagePaths1, "afterService", imagePaths2, "image1", path1, "image2", path2, "image3", path3, "image4", path4
                                        , "image5", path5, "image6", path6, "image7", path7, "image8", path8, url, new OkHttpManager.HttpCallBack() {
                                            @Override
                                            public void onResponse(JSONObject jsonObject) {
                                                if ("01".equals(biaoshi)){
                                                    updateUscount(DemoHelper.getInstance().getCurrentUsernName(),false);
                                                }
                                                Log.e("dianzidanjuac", jsonObject.toString());
                                                Toast.makeText(getApplicationContext(), "保存成功！", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }

                                            @Override
                                            public void onFailure(String errorMsg) {
                                                Log.e("dianzidanjuac,e", errorMsg);
                                                Toast.makeText(getApplicationContext(), "保存失败！", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }else {
                                OkHttpManager.getInstance().posts2(params, null, new ArrayList<String>(), null, new ArrayList<String>(), "image1", path1, "image2", path2, "image3", path3, "image4", path4
                                        , "image5", path5, "image6", path6, "image7", path7, "image8", path8, url, new OkHttpManager.HttpCallBack() {
                                            @Override
                                            public void onResponse(JSONObject jsonObject) {
                                                Log.e("dianzidanjuac", jsonObject.toString());
                                                Toast.makeText(getApplicationContext(), "保存成功！", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }

                                            @Override
                                            public void onFailure(String errorMsg) {
                                                Log.e("dianzidanjuac,e", errorMsg);
                                                Toast.makeText(getApplicationContext(), "保存失败！", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    });
                }else {
                    LayoutInflater inflaterDl = LayoutInflater.from(DianziDanJuFourActivity.this);
                    RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_order_send, null);
                    final Dialog dialog = new AlertDialog.Builder(DianziDanJuFourActivity.this,R.style.Dialog).create();
                    dialog.show();
                    dialog.getWindow().setContentView(layout);
                    dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                    dialog.setCanceledOnTouchOutside(true);
                    RelativeLayout re_item1 = (RelativeLayout) dialog.findViewById(R.id.re_item1);
                    RelativeLayout re_item2 = (RelativeLayout) dialog.findViewById(R.id.re_item2);
                    RelativeLayout re_item4 = (RelativeLayout) dialog.findViewById(R.id.re_item4);
                    final EditText et_phone3 = (EditText) dialog.findViewById(R.id.et_phone3);
                    Button btn_phone3 = (Button) dialog.findViewById(R.id.btn_phone3);
                    re_item1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            PermissionUtil permissionUtil = new PermissionUtil(DianziDanJuFourActivity.this);
                            permissionUtil.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                                    new PermissionListener() {
                                        @Override
                                        public void onGranted() {
                                            //所有权限都已经授权
                                            Intent intent = new Intent(DianziDanJuFourActivity.this,AddressListTwoActivity.class);
                                            intent.putExtra("biaoshi","04");
                                            intent.putExtra("title",title);
                                            intent.putExtra("timestamp",timestamp);
                                            startActivityForResult(intent,0);
                                        }
                                        @Override
                                        public void onDenied(List<String> deniedPermission) {
                                            //Toast第一个被拒绝的权限
                                            Toast.makeText(getApplicationContext(),"您拒绝了访问通讯录权限！",Toast.LENGTH_SHORT).show();
                                        }
                                        @Override
                                        public void onShouldShowRationale(List<String> deniedPermission) {
                                            //Toast第一个勾选不在提示的权限
                                            Toast.makeText(getApplicationContext(),"您拒绝了访问通讯录权限，请前往设置手动打开权限！",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                    re_item2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Intent intent3 = new Intent(DianziDanJuFourActivity.this,FriendActivity.class);
                            intent3.putExtra("biaoshi","04");
                            intent3.putExtra("timestamp",timestamp);
                            startActivityForResult(intent3,0);
                        }
                    });
                    btn_phone3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            String userId = et_phone3.getText().toString().trim();
                            if (TextUtils.isEmpty(userId)||userId.length()!=11) {
                                Toast.makeText(getApplicationContext(), "请输入正确格式的电话号码!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            queryUserInfo(userId);
                        }
                    });
                    re_item4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
                break;
            case R.id.btn_tianjia:
                if (current_neirong<4) {
                    current_neirong++;
                    if (current_neirong==2){
                        ll_sign2.setVisibility(View.VISIBLE);
                        tv2.setVisibility(View.VISIBLE);
                    }else if (current_neirong==3){
                        ll_sign3.setVisibility(View.VISIBLE);
                        tv3.setVisibility(View.VISIBLE);
                    }else if (current_neirong==4){
                        ll_sign4.setVisibility(View.VISIBLE);
                        tv4.setVisibility(View.VISIBLE);
                    }
                }else {
                    Toast.makeText(DianziDanJuFourActivity.this,"最多添加八个签名哦...",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_quxiao:
                if (current_neirong>1) {
                    current_neirong--;
                    if (current_neirong==1){
                        if ((image3!=null&&!"".equals(image3))||(image4!=null&&!"".equals(image4))){
                            Toast.makeText(DianziDanJuFourActivity.this,"没有可以取消的内容了...",Toast.LENGTH_SHORT).show();
                        }else {
                            ll_sign2.setVisibility(View.GONE);
                            tv2.setVisibility(View.GONE);
                        }
                    }else if (current_neirong==2){
                        if ((image5!=null&&!"".equals(image5))||(image6!=null&&!"".equals(image6))){
                            Toast.makeText(DianziDanJuFourActivity.this,"没有可以取消的内容了...",Toast.LENGTH_SHORT).show();
                        }else {
                            ll_sign3.setVisibility(View.GONE);
                            tv3.setVisibility(View.GONE);
                        }
                    }else if (current_neirong==3){
                        if ((image7!=null&&!"".equals(image7))||(image8!=null&&!"".equals(image8))){
                            Toast.makeText(DianziDanJuFourActivity.this,"没有可以取消的内容了...",Toast.LENGTH_SHORT).show();
                        }else {
                            ll_sign4.setVisibility(View.GONE);
                            tv4.setVisibility(View.GONE);
                        }
                    }
                }else {
                    Toast.makeText(DianziDanJuFourActivity.this,"没有可以取消的内容了...",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_sign1:
                startActivityForResult(new Intent(DianziDanJuFourActivity.this,QianMingActivity.class).putExtra("biaoshi","22"),1);
                break;
            case R.id.iv_sign1:
                startActivityForResult(new Intent(DianziDanJuFourActivity.this,QianMingActivity.class).putExtra("biaoshi","22"),1);
                break;
            case R.id.tv_sign2:
                startActivityForResult(new Intent(DianziDanJuFourActivity.this,QianMingActivity.class).putExtra("biaoshi","22"),2);
                break;
            case R.id.iv_sign2:
                startActivityForResult(new Intent(DianziDanJuFourActivity.this,QianMingActivity.class).putExtra("biaoshi","22"),2);
                break;
            case R.id.tv_sign3:
                startActivityForResult(new Intent(DianziDanJuFourActivity.this,QianMingActivity.class).putExtra("biaoshi","22"),3);
                break;
            case R.id.iv_sign3:
                startActivityForResult(new Intent(DianziDanJuFourActivity.this,QianMingActivity.class).putExtra("biaoshi","22"),3);
                break;
            case R.id.tv_sign4:
                startActivityForResult(new Intent(DianziDanJuFourActivity.this,QianMingActivity.class).putExtra("biaoshi","22"),4);
                break;
            case R.id.iv_sign4:
                startActivityForResult(new Intent(DianziDanJuFourActivity.this,QianMingActivity.class).putExtra("biaoshi","22"),4);
                break;
            case R.id.tv_sign5:
                startActivityForResult(new Intent(DianziDanJuFourActivity.this,QianMingActivity.class).putExtra("biaoshi","22"),5);
                break;
            case R.id.iv_sign5:
                startActivityForResult(new Intent(DianziDanJuFourActivity.this,QianMingActivity.class).putExtra("biaoshi","22"),5);
                break;
            case R.id.tv_sign6:
                startActivityForResult(new Intent(DianziDanJuFourActivity.this,QianMingActivity.class).putExtra("biaoshi","22"),6);
                break;
            case R.id.iv_sign6:
                startActivityForResult(new Intent(DianziDanJuFourActivity.this,QianMingActivity.class).putExtra("biaoshi","22"),6);
                break;
            case R.id.tv_sign7:
                startActivityForResult(new Intent(DianziDanJuFourActivity.this,QianMingActivity.class).putExtra("biaoshi","22"),7);
                break;
            case R.id.iv_sign7:
                startActivityForResult(new Intent(DianziDanJuFourActivity.this,QianMingActivity.class).putExtra("biaoshi","22"),7);
                break;
            case R.id.tv_sign8:
                startActivityForResult(new Intent(DianziDanJuFourActivity.this,QianMingActivity.class).putExtra("biaoshi","22"),8);
                break;
            case R.id.iv_sign8:
                startActivityForResult(new Intent(DianziDanJuFourActivity.this,QianMingActivity.class).putExtra("biaoshi","22"),8);
                break;
        }
    }

    private void queryUserInfo(final String userId) {
        String url = FXConstant.URL_Get_UserInfo+userId;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                JSONObject object = JSON.parseObject(s);
                String code = object.getString("code");
                sendPushMessage2(userId);
                if ("用户名为空".equals(code)){
                    SendMessage1(userId);
                }else {
                    senddztoUser(userId);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        MySingleton.getInstance(DianziDanJuFourActivity.this).addToRequestQueue(request);
    }

    private void SendMessage1(final String userId) {
        final String str;
        if (title!=null&&title.length()>6){
            str = title.substring(0,6)+"...";
        }else {
            str = title;
        }
        String url = FXConstant.URL_DUANXIN_TONGZHI;
        final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                senddztoUser(userId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "发送失败，网络错误！", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("message", "【正事多】单据"+str+",需要您签字,请注册手机端“正事多”在电子单据中查看");
                param.put("telNum", userId);
                Log.e("utorderdeac,sm1",param.toString());
                return param;
            }
        };
        UserPermissionUtil.getUserPermission(this, DemoHelper.getInstance().getCurrentUsernName(), "3", new UserPermissionUtil.UserPermissionListener() {
            @Override
            public void onAllow() {
                MySingleton.getInstance(DianziDanJuFourActivity.this).addToRequestQueue(request);
            }

            @Override
            public void onBan() {

                ToastUtils.showNOrmalToast(getApplicationContext(), "您的账户已被禁止发送短信");

            }
        });

    }

    private void senddztoUser(final String username) {
        String url = FXConstant.URL_FASONG_DZ_DANJU;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e("friendac,s",s);
                updateUscount(username,true);
                com.alibaba.fastjson.JSONObject object = JSON.parseObject(s);
                String code = object.getString("code");
                if ("success".equals(code)){
                    Toast.makeText(getApplicationContext(),"发送成功！",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),"发送失败！",Toast.LENGTH_SHORT).show();
                Log.e("friendac",volleyError.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("timestamp",timestamp);
                param.put("u_id",username);
                return param;
            }
        };
        MySingleton.getInstance(DianziDanJuFourActivity.this).addToRequestQueue(request);
    }
    private void updateUscount(final String username,final boolean add) {
        String url = FXConstant.URL_UPDATE_UNREADUSER;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e("addfriend,s",s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("addfriend,e",volleyError.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                if (add) {
                    param.put("billCount", "1");
                }else {
                    param.put("billCount", "-1");
                }
                param.put("userId",username);
                return param;
            }
        };
        MySingleton.getInstance(DianziDanJuFourActivity.this).addToRequestQueue(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            switch (requestCode){
                case 1:
                    isBaocun = true;
                    tv_baocun.setText("保存");
                    path1 = data.getStringExtra("jdimageName");
                    tv_sign1.setVisibility(View.INVISIBLE);
                    iv_sign1.setVisibility(View.VISIBLE);
                    iv_sign1.setImageURI(Uri.fromFile(new File(path1)));
                    break;
                case 2:
                    isBaocun = true;
                    tv_baocun.setText("保存");
                    path2 = data.getStringExtra("jdimageName");
                    tv_sign2.setVisibility(View.INVISIBLE);
                    iv_sign2.setVisibility(View.VISIBLE);
                    iv_sign2.setImageURI(Uri.fromFile(new File(path2)));
                    break;
                case 3:
                    isBaocun = true;
                    tv_baocun.setText("保存");
                    path3 = data.getStringExtra("jdimageName");
                    tv_sign3.setVisibility(View.INVISIBLE);
                    iv_sign3.setVisibility(View.VISIBLE);
                    iv_sign3.setImageURI(Uri.fromFile(new File(path3)));
                    break;
                case 4:
                    isBaocun = true;
                    tv_baocun.setText("保存");
                    path4 = data.getStringExtra("jdimageName");
                    tv_sign4.setVisibility(View.INVISIBLE);
                    iv_sign4.setVisibility(View.VISIBLE);
                    iv_sign4.setImageURI(Uri.fromFile(new File(path4)));
                    break;
                case 5:
                    isBaocun = true;
                    tv_baocun.setText("保存");
                    path5 = data.getStringExtra("jdimageName");
                    tv_sign5.setVisibility(View.INVISIBLE);
                    iv_sign5.setVisibility(View.VISIBLE);
                    iv_sign5.setImageURI(Uri.fromFile(new File(path5)));
                    break;
                case 6:
                    isBaocun = true;
                    tv_baocun.setText("保存");
                    path6 = data.getStringExtra("jdimageName");
                    tv_sign6.setVisibility(View.INVISIBLE);
                    iv_sign6.setVisibility(View.VISIBLE);
                    iv_sign6.setImageURI(Uri.fromFile(new File(path6)));
                    break;
                case 7:
                    isBaocun = true;
                    tv_baocun.setText("保存");
                    path7 = data.getStringExtra("jdimageName");
                    tv_sign7.setVisibility(View.INVISIBLE);
                    iv_sign7.setVisibility(View.VISIBLE);
                    iv_sign7.setImageURI(Uri.fromFile(new File(path7)));
                    break;
                case 8:
                    isBaocun = true;
                    tv_baocun.setText("保存");
                    path8 = data.getStringExtra("jdimageName");
                    tv_sign8.setVisibility(View.INVISIBLE);
                    iv_sign8.setVisibility(View.VISIBLE);
                    iv_sign8.setImageURI(Uri.fromFile(new File(path8)));
                    break;
            }
        }
    }

    @Override
    public void updateDzdjDetail(DianziDanju djDetail) {
        title = djDetail.getTitle();
        String count = djDetail.getContent();
        String beforeService = djDetail.getBeforeService();
        String afterService = djDetail.getAfterService();
        time1 = djDetail.getTime1();
        time2 = djDetail.getTime2();
        time3 = djDetail.getTime3();
        time4 = djDetail.getTime4();
        time5 = djDetail.getTime5();
        time6 = djDetail.getTime6();
        time7 = djDetail.getTime7();
        time8 = djDetail.getTime8();
        image1 = djDetail.getImage1();
        image2 = djDetail.getImage2();
        image3 = djDetail.getImage3();
        image4 = djDetail.getImage4();
        image5 = djDetail.getImage5();
        image6 = djDetail.getImage6();
        image7 = djDetail.getImage7();
        image8 = djDetail.getImage8();
        et_miaoshu.setText(title);
        String sImage1 = null,sImage2= null,sImage3= null,sImage4= null,sImage5= null,sImage6= null,sImage7= null,sImage8= null;
        String [] images1=null;
        String [] images2=null;
        selectMedia1.clear();
        selectMedia2.clear();
        imagePaths1.clear();
        imagePaths2.clear();
        if (beforeService!=null&&!"".equals(beforeService)){
            images1 = beforeService.split("\\|");
        }
        if (afterService!=null&&!"".equals(afterService)){
            images2 = afterService.split("\\|");
        }
        int type = FunctionConfig.TYPE_IMAGE;
        if (images1!=null){
            if (images1.length>0){
                sImage1 = images1[0];
                LocalMedia media = new LocalMedia();
                media.setType(type);
                media.setCompressed(false);
                media.setCut(false);
                media.setIsChecked(true);
                media.setNum(images1.length);
                imagePaths1.add(FXConstant.URL_DZDANJU + sImage1);
                media.setPath(FXConstant.URL_DZDANJU + sImage1);
                selectMedia1.add(media);
                recyclerView.setEnabled(false);
                recyclerView2.setEnabled(false);
                recyclerView.setClickable(false);
                recyclerView2.setClickable(false);
            }
            if (images1.length>1){
                sImage2 = images1[1];
                LocalMedia media = new LocalMedia();
                media.setType(type);
                media.setCompressed(false);
                media.setCut(false);
                media.setIsChecked(true);
                media.setNum(images1.length);
                imagePaths1.add(FXConstant.URL_DZDANJU + sImage2);
                media.setPath(FXConstant.URL_DZDANJU + sImage2);
                selectMedia1.add(media);
                recyclerView.setEnabled(false);
                recyclerView2.setEnabled(false);
                recyclerView.setClickable(false);
                recyclerView2.setClickable(false);
            }
            if (images1.length>2){
                sImage3 = images1[2];
                LocalMedia media = new LocalMedia();
                media.setType(type);
                media.setCompressed(false);
                media.setCut(false);
                media.setIsChecked(true);
                media.setNum(images1.length);
                imagePaths1.add(FXConstant.URL_DZDANJU + sImage3);
                media.setPath(FXConstant.URL_DZDANJU + sImage3);
                selectMedia1.add(media);
                recyclerView.setEnabled(false);
                recyclerView2.setEnabled(false);
                recyclerView.setClickable(false);
                recyclerView2.setClickable(false);
            }
            if (images1.length>3){
                sImage4 = images1[3];
                LocalMedia media = new LocalMedia();
                media.setType(type);
                media.setCompressed(false);
                media.setCut(false);
                media.setIsChecked(true);
                media.setNum(images1.length);
                imagePaths1.add(FXConstant.URL_DZDANJU + sImage4);
                media.setPath(FXConstant.URL_DZDANJU + sImage4);
                selectMedia1.add(media);
                recyclerView.setEnabled(false);
                recyclerView2.setEnabled(false);
                recyclerView.setClickable(false);
                recyclerView2.setClickable(false);
            }
        }
        if (images2!=null){
            if (images2.length>0){
                sImage5 = images2[0];
                LocalMedia media = new LocalMedia();
                media.setType(type);
                media.setCompressed(false);
                media.setCut(false);
                media.setIsChecked(true);
                media.setNum(images2.length);
                imagePaths2.add(FXConstant.URL_DZDANJU + sImage5);
                media.setPath(FXConstant.URL_DZDANJU + sImage5);
                selectMedia2.add(media);
                recyclerView.setEnabled(false);
                recyclerView2.setEnabled(false);
                recyclerView.setClickable(false);
                recyclerView2.setClickable(false);
            }
            if (images2.length>1){
                sImage6 = images2[1];
                LocalMedia media = new LocalMedia();
                media.setType(type);
                media.setCompressed(false);
                media.setCut(false);
                media.setIsChecked(true);
                media.setNum(images2.length);
                imagePaths2.add(FXConstant.URL_DZDANJU + sImage6);
                media.setPath(FXConstant.URL_DZDANJU + sImage6);
                selectMedia2.add(media);
                recyclerView.setEnabled(false);
                recyclerView2.setEnabled(false);
                recyclerView.setClickable(false);
                recyclerView2.setClickable(false);
            }
            if (images2.length>2){
                sImage7 = images2[2];
                LocalMedia media = new LocalMedia();
                media.setType(type);
                media.setCompressed(false);
                media.setCut(false);
                media.setIsChecked(true);
                media.setNum(images2.length);
                imagePaths2.add(FXConstant.URL_DZDANJU + sImage7);
                media.setPath(FXConstant.URL_DZDANJU + sImage7);
                selectMedia2.add(media);
                recyclerView.setEnabled(false);
                recyclerView2.setEnabled(false);
                recyclerView.setClickable(false);
                recyclerView2.setClickable(false);
            }
            if (images2.length>3){
                sImage8 = images2[3];
                LocalMedia media = new LocalMedia();
                media.setType(type);
                media.setCompressed(false);
                media.setCut(false);
                media.setIsChecked(true);
                media.setNum(images2.length);
                imagePaths2.add(FXConstant.URL_DZDANJU + sImage8);
                media.setPath(FXConstant.URL_DZDANJU + sImage8);
                selectMedia2.add(media);
                recyclerView.setEnabled(false);
                recyclerView2.setEnabled(false);
                recyclerView.setClickable(false);
                recyclerView2.setClickable(false);
            }
        }
        if (selectMedia1.size()>0){
            adapter.notifyDataSetChanged();
        }
        if (selectMedia2.size()>0){
            adapter2.notifyDataSetChanged();
        }
        String name = null,phone= null,dizhi= null;
        String [] str = null;
        if (count!=null&&!"".equals(count)){
            str = count.split("\\|");
        }
        if (str.length>0){
            name = str[0];
        }
        if (str.length>1){
            phone = str[1];
        }
        if (str.length>2){
            dizhi = str[2];
        }
        if (name!=null&&!"0".equals(name)){
            et_name.setText(name);
        }
        if (phone!=null&&!"0".equals(phone)){
            et_phone.setText(phone);
        }
        if (dizhi!=null&&!"0".equals(dizhi)){
            et_dizhi.setText(dizhi);
        }
        et_miaoshu.setEnabled(false);
        et_name.setEnabled(false);
        et_phone.setEnabled(false);
        et_dizhi.setEnabled(false);
        String signTime1,signTime2,signTime3,signTime4,signTime5,signTime6,signTime7,signTime8;
        if ((image1!=null&&!"".equals(image1)&&!image1.equalsIgnoreCase("null"))||(image2!=null&&!"".equals(image2)&&!image2.equalsIgnoreCase("null"))){
            current_neirong = 1;
            if (image1!=null&&!"".equals(image1)&&!image1.equalsIgnoreCase("null")) {
                ImageLoader.getInstance().displayImage(FXConstant.URL_DZDANJU + image1, iv_sign1);
                signTime1 = time1.substring(0,4)+"-"+time1.substring(4,6)+"-"+time1.substring(6,8)+" " +time1.substring(8,10)+":"+time1.substring(10,12);
                tv_sign1.setVisibility(View.INVISIBLE);
                tev_sign1.setVisibility(View.VISIBLE);
                tv_sign1_time.setVisibility(View.VISIBLE);
                tv_sign1_time.setText(signTime1);
                tv_sign1.setEnabled(false);
                iv_sign1.setEnabled(false);
            }
            if (image2!=null&&!"".equals(image2)&&!image2.equalsIgnoreCase("null")) {
                ImageLoader.getInstance().displayImage(FXConstant.URL_DZDANJU + image2, iv_sign2);
                signTime2 = time2.substring(0,4)+"-"+time2.substring(4,6)+"-"+time2.substring(6,8)+" " +time2.substring(8,10)+":"+time2.substring(10,12);
                tv_sign2.setVisibility(View.INVISIBLE);
                tev_sign2.setVisibility(View.VISIBLE);
                tv_sign2_time.setVisibility(View.VISIBLE);
                tv_sign2_time.setText(signTime2);
                tv_sign2.setEnabled(false);
                iv_sign2.setEnabled(false);
            }
            if ((image3!=null&&!"".equals(image3)&&!image3.equalsIgnoreCase("null"))||(image4!=null&&!"".equals(image4)&&!image4.equalsIgnoreCase("null"))){
                current_neirong = 2;
                ll_sign2.setVisibility(View.VISIBLE);
                tv2.setVisibility(View.VISIBLE);
                if (image3!=null&&!"".equals(image3)&&!image3.equalsIgnoreCase("null")) {
                    ImageLoader.getInstance().displayImage(FXConstant.URL_DZDANJU + image3, iv_sign3);
                    signTime3 = time3.substring(0,4)+"-"+time3.substring(4,6)+"-"+time3.substring(6,8)+" " +time3.substring(8,10)+":"+time3.substring(10,12);
                    tv_sign3.setVisibility(View.INVISIBLE);
                    tev_sign3.setVisibility(View.VISIBLE);
                    tv_sign3_time.setVisibility(View.VISIBLE);
                    tv_sign3_time.setText(signTime3);
                    tv_sign3.setEnabled(false);
                    iv_sign3.setEnabled(false);
                }
                if (image4!=null&&!"".equals(image4)&&!image4.equalsIgnoreCase("null")) {
                    ImageLoader.getInstance().displayImage(FXConstant.URL_DZDANJU + image4, iv_sign4);
                    signTime4 = time4.substring(0,4)+"-"+time4.substring(4,6)+"-"+time4.substring(6,8)+" " +time4.substring(8,10)+":"+time4.substring(10,12);
                    tv_sign4.setVisibility(View.INVISIBLE);
                    tev_sign4.setVisibility(View.VISIBLE);
                    tv_sign4_time.setVisibility(View.VISIBLE);
                    tv_sign4_time.setText(signTime4);
                    tv_sign4.setEnabled(false);
                    iv_sign4.setEnabled(false);
                }
                if ((image5!=null&&!"".equals(image5)&&!image5.equalsIgnoreCase("null"))||(image6!=null&&!"".equals(image6)&&!image6.equalsIgnoreCase("null"))){
                    current_neirong = 3;
                    ll_sign3.setVisibility(View.VISIBLE);
                    tv3.setVisibility(View.VISIBLE);
                    if (image5!=null&&!"".equals(image5)&&!image5.equalsIgnoreCase("null")) {
                        ImageLoader.getInstance().displayImage(FXConstant.URL_DZDANJU + image5, iv_sign5);
                        signTime5 = time5.substring(0,4)+"-"+time5.substring(4,6)+"-"+time5.substring(6,8)+" " +time5.substring(8,10)+":"+time5.substring(10,12);
                        tv_sign5.setVisibility(View.INVISIBLE);
                        tev_sign5.setVisibility(View.VISIBLE);
                        tv_sign5_time.setVisibility(View.VISIBLE);
                        tv_sign5_time.setText(signTime5);
                        tv_sign5.setEnabled(false);
                        iv_sign5.setEnabled(false);
                    }
                    if (image6!=null&&!"".equals(image6)&&!image6.equalsIgnoreCase("null")) {
                        ImageLoader.getInstance().displayImage(FXConstant.URL_DZDANJU + image6, iv_sign6);
                        signTime6 = time6.substring(0,4)+"-"+time6.substring(4,6)+"-"+time6.substring(6,8)+" " +time6.substring(8,10)+":"+time6.substring(10,12);
                        tv_sign6.setVisibility(View.INVISIBLE);
                        tev_sign6.setVisibility(View.VISIBLE);
                        tv_sign6_time.setVisibility(View.VISIBLE);
                        tv_sign6_time.setText(signTime6);
                        tv_sign6.setEnabled(false);
                        iv_sign6.setEnabled(false);
                    }
                    if ((image7!=null&&!"".equals(image7)&&!image7.equalsIgnoreCase("null"))||(image8!=null&&!"".equals(image8)&&!image8.equalsIgnoreCase("null"))){
                        current_neirong = 4;
                        ll_sign4.setVisibility(View.VISIBLE);
                        tv4.setVisibility(View.VISIBLE);
                        if (image7!=null&&!"".equals(image7)&&!image7.equalsIgnoreCase("null")) {
                            ImageLoader.getInstance().displayImage(FXConstant.URL_DZDANJU + image7, iv_sign7);
                            signTime7 = time7.substring(0,4)+"-"+time7.substring(4,6)+"-"+time7.substring(6,8)+" " +time7.substring(8,10)+":"+time7.substring(10,12);
                            tv_sign7.setVisibility(View.INVISIBLE);
                            tev_sign7.setVisibility(View.VISIBLE);
                            tv_sign7_time.setVisibility(View.VISIBLE);
                            tv_sign7_time.setText(signTime7);
                            tv_sign7.setEnabled(false);
                            iv_sign7.setEnabled(false);
                        }
                        if (image8!=null&&!"".equals(image8)&&!image8.equalsIgnoreCase("null")) {
                            ImageLoader.getInstance().displayImage(FXConstant.URL_DZDANJU + image8, iv_sign8);
                            signTime8 = time8.substring(0,4)+"-"+time8.substring(4,6)+"-"+time8.substring(6,8)+" " +time8.substring(8,10)+":"+time8.substring(10,12);
                            tv_sign8.setVisibility(View.INVISIBLE);
                            tev_sign8.setVisibility(View.VISIBLE);
                            tv_sign8_time.setVisibility(View.VISIBLE);
                            tv_sign8_time.setText(signTime8);
                            tv_sign8.setEnabled(false);
                            iv_sign8.setEnabled(false);
                        }
                        rl_button.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ("01".equals(biaoshi)) {
            presenter.loadDZdjDetail(DemoHelper.getInstance().getCurrentUsernName(), timestamp);
        }
    }

}
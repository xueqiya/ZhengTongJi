package com.sangu.apptongji.main.alluser.order.avtivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
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
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.pay.demo.PayResult;
import com.alipay.sdk.pay.demo.util.OrderInfoUtil2_0;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fanxin.easeui.utils.FileStorage;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.permissionutil.PermissionListener;
import com.permissionutil.PermissionUtil;
import com.sangu.apptongji.Constant;
import com.sangu.apptongji.DemoApplication;
import com.sangu.apptongji.DemoHelper;
import com.sangu.apptongji.R;
import com.sangu.apptongji.main.FXConstant;
import com.sangu.apptongji.main.adapter.GridImageAdapter;
import com.sangu.apptongji.main.address.AddressListTwoActivity;
import com.sangu.apptongji.main.alluser.entity.FWFKInfo;
import com.sangu.apptongji.main.alluser.order.entity.OrderDetail;
import com.sangu.apptongji.main.alluser.presenter.IFWFKPresenter;
import com.sangu.apptongji.main.alluser.presenter.IOrderDetailPresenter;
import com.sangu.apptongji.main.alluser.presenter.IPricePresenter;
import com.sangu.apptongji.main.alluser.presenter.impl.FWFKPresenter;
import com.sangu.apptongji.main.alluser.presenter.impl.OrderDetailPresenter;
import com.sangu.apptongji.main.alluser.presenter.impl.PricePresenter;
import com.sangu.apptongji.main.alluser.view.IFWFKView;
import com.sangu.apptongji.main.alluser.view.IOrderDetailView;
import com.sangu.apptongji.main.alluser.view.IPriceView;
import com.sangu.apptongji.main.qiye.QiYeYuGoActivity;
import com.sangu.apptongji.main.utils.FullyGridLayoutManager;
import com.sangu.apptongji.main.utils.MySingleton;
import com.sangu.apptongji.main.utils.ToastUtils;
import com.sangu.apptongji.main.utils.ZhifuHelpUtils;
import com.sangu.apptongji.main.widget.CustomProgressDialog;
import com.sangu.apptongji.main.widget.OnPasswordInputFinish;
import com.sangu.apptongji.main.widget.PasswordView;
import com.sangu.apptongji.ui.BaseActivity;
import com.sangu.apptongji.utils.UserPermissionUtil;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yalantis.ucrop.entity.LocalMedia;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017-08-18.
 */

public class UTOrderDetailFourActivity extends BaseActivity implements IPriceView,IOrderDetailView,IFWFKView {
    private static final int SDK_PAY_FLAG = 1;
    private IOrderDetailPresenter orderDetailPresenter=null;
    private IFWFKPresenter presenter=null;
    private IWXAPI api=null;
    private IPricePresenter pricePresenter;
    private String pass;
    private double yuE;
    private int errorTime=3;

    private ArrayList<String> imagePaths1=new ArrayList<>();
    private ArrayList<String> imagePaths2=new ArrayList<>();
    private String orderId,merId,orderProject,orderNumber,orderAmt,orderSum,oImage,mImage,beizhu,companyId,userId,send_id1,send_id2
            ,image1,image2,image3,image4,time1,time2,time3,resv4,oSignature,mSignature,orderTime,dynamicSeq,create_time;
    private String biaoshi2,biaoshi1,resv5="",resv5Geren="",resv5Qiye="",biaoshi,orderState,totalAmt,orderBody;
    private List<LocalMedia> selectMedia1 = new ArrayList<>();
    private List<LocalMedia> selectMedia2 = new ArrayList<>();
    private RecyclerView recyclerView;
    private GridImageAdapter adapter;
    private RecyclerView recyclerView2;
    private GridImageAdapter adapter2;
    boolean isFirst = true;

    private CustomProgressDialog mProgress=null;
    int allSize=0,downloadSize=0,allSize1=0,allSize2=0;
    String filePath1=null,filePath2=null,filePath3=null,filePath4=null,filePath5=null,filePath6=null,filePath7=null,filePath8=null;
    private EditText et_miaoshu=null,et_name=null,et_phone=null,et_dizhi=null,et_feiyong=null,et_Ubeizhu;
    private TextView tv_kehu=null,tv_kehu_time=null,tv_jiedan=null,tv_jiedan_time=null,tv_paidan_id1,tv_paidan_id1_time,
            tv_paidan_caiwu,tv_paidan_caiwu_time,tv_paidan_kehu,tv_paidan_kehu_time,tv_zhifuxianshi,tv_qiye_ticheng,
            tev_kehu=null,tev_jiedan=null,tv_fasong=null,tev_paidan_id1,tev_paidan_caiwu,tev_paidan_kehu,tv_count_fankui;
    private LinearLayout ll3_paidan,ll2,ll2_paidan,ll_btn;
    private RelativeLayout rl_xinxi2=null,rl_miaoshu=null,rl_btn1,rlM_beizhu,rl_paidan_id1,rl_ticheng;
    private ImageView iv_kehu=null,iv_jiedan=null,iv_paidan_id1,iv_paidan_caiwu,iv_paidan_kehu,iv_jiantou;
    private Button btn_comit=null,btn_cansul,btn_comit2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                     */
                    String resultInfo = payResult.getResult();// ?????????????????????????????????
                    String resultStatus = payResult.getResultStatus();
                    // ??????resultStatus ???9000?????????????????????
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // ??????????????????????????????????????????????????????????????????????????????
                        sendPushMessage(merId,"000");
                        Toast.makeText(UTOrderDetailFourActivity.this, "????????????,??????????????????", Toast.LENGTH_SHORT).show();
                    } else {
                        // ???????????????????????????????????????????????????????????????????????????
                        Toast.makeText(UTOrderDetailFourActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String zhifuZhuangtai = intent.getStringExtra("zhifu");
        if (zhifuZhuangtai!=null&&"????????????".equals(zhifuZhuangtai)){
            sendPushMessage(merId,"000");
            Toast.makeText(UTOrderDetailFourActivity.this, "????????????,??????????????????", Toast.LENGTH_SHORT).show();
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if (allSize==downloadSize){
                        ArrayList<String> paths2 = new ArrayList<>();
                        imagePaths1.clear();
                        imagePaths2.clear();
                        int type = FunctionConfig.TYPE_IMAGE;
                        if (allSize1>0) {
                            LocalMedia media = new LocalMedia();
                            media.setType(type);
                            media.setCompressed(false);
                            media.setCut(false);
                            media.setIsChecked(true);
                            imagePaths1.add(filePath1);
                            media.setNum(imagePaths1.size());
                            media.setPath(filePath1);
                            selectMedia1.add(media);
                        }
                        if (allSize1>1){
                            LocalMedia media = new LocalMedia();
                            media.setType(type);
                            media.setCompressed(false);
                            media.setCut(false);
                            media.setIsChecked(true);
                            imagePaths1.add(filePath2);
                            media.setNum(imagePaths1.size());
                            media.setPath(filePath2);
                            selectMedia1.add(media);
                        }
                        if (allSize1>2){
                            LocalMedia media = new LocalMedia();
                            media.setType(type);
                            media.setCompressed(false);
                            media.setCut(false);
                            media.setIsChecked(true);
                            imagePaths1.add(filePath3);
                            media.setNum(imagePaths1.size());
                            media.setPath(filePath3);
                            selectMedia1.add(media);
                        }
                        if (allSize1>3){
                            LocalMedia media = new LocalMedia();
                            media.setType(type);
                            media.setCompressed(false);
                            media.setCut(false);
                            media.setIsChecked(true);
                            imagePaths1.add(filePath4);
                            media.setNum(imagePaths1.size());
                            media.setPath(filePath4);
                            selectMedia1.add(media);
                        }
                        if (allSize1>0) {
                            adapter.notifyDataSetChanged();
                        }
                        if (allSize2>0) {
                            LocalMedia media = new LocalMedia();
                            media.setType(type);
                            media.setCompressed(false);
                            media.setCut(false);
                            media.setIsChecked(true);
                            imagePaths2.add(filePath5);
                            media.setNum(imagePaths2.size());
                            media.setPath(filePath5);
                            selectMedia2.add(media);
                        }
                        if (allSize2>1){
                            LocalMedia media = new LocalMedia();
                            media.setType(type);
                            media.setCompressed(false);
                            media.setCut(false);
                            media.setIsChecked(true);
                            imagePaths2.add(filePath6);
                            media.setNum(imagePaths2.size());
                            media.setPath(filePath6);
                            selectMedia2.add(media);
                        }
                        if (allSize2>2){
                            LocalMedia media = new LocalMedia();
                            media.setType(type);
                            media.setCompressed(false);
                            media.setCut(false);
                            media.setIsChecked(true);
                            imagePaths2.add(filePath7);
                            media.setNum(imagePaths2.size());
                            media.setPath(filePath7);
                            selectMedia2.add(media);
                        }
                        if (allSize2>3){
                            LocalMedia media = new LocalMedia();
                            media.setType(type);
                            media.setCompressed(false);
                            media.setCut(false);
                            media.setIsChecked(true);
                            imagePaths2.add(filePath8);
                            media.setNum(imagePaths2.size());
                            media.setPath(filePath8);
                            selectMedia2.add(media);
                        }
                        if (allSize2>0) {
                            adapter2.notifyDataSetChanged();
                        }
                        if (mProgress!=null&&mProgress.isShowing()) {
                            mProgress.dismiss();
                        }
                    }
                    break;
            }
        }
    };

    private RelativeLayout countDown=null;
    private TextView daysTv,hoursTv=null, minutesTv=null,secondsTv=null,tv2=null,tv_all;
    String hours = "00",mins="00",second="00",yueding=null,resv2=null;
    private long mDay;
    private long mHour;
    private long mMin;
    private long mSecond;
    private boolean isRun = true;
    private boolean isJian = true;
    private Handler timeHandler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1) {
                tv2.setText("??????????????????");
                computeTime();
                if ((mDay==0&&mHour==0&&mMin==0&&mSecond==0)||(mDay==0&&mHour==00&&mMin==00&&mSecond==00)) {
                    isJian = false;
                }
            }else if (msg.what==2){
                tv2.setText("??????????????????");
                addTime();
            }
            if (mDay==0){
                daysTv.setVisibility(View.GONE);
            }else {
                daysTv.setVisibility(View.VISIBLE);
            }
            if (mHour<10){
                hours = "0"+mHour;
            }else {
                hours = String.valueOf(mHour);
            }
            if (mMin<10){
                mins = "0"+mMin;
            }else {
                mins = String.valueOf(mMin);
            }
            if (mSecond<10){
                second = "0"+mSecond ;
            }else {
                second = String.valueOf(mSecond);
            }
            daysTv.setText(mDay+"???");
            hoursTv.setText(hours);
            minutesTv.setText(mins);
            secondsTv.setText(second);
        }
    };

    /**
     * ???????????????
     */
    private void startRun() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRun) {
                    try {
                        Thread.sleep(1000); // sleep 1000ms
                        Message message = Message.obtain();
                        if (isJian){
                            message.what = 1;
                        }else {
                            message.what = 2;
                        }
                        timeHandler1.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    private void addTime() {
        mSecond++;
        if (mSecond>59){
            mMin++;
            mSecond = 0;
            if (mMin>59){
                mMin = 0;
                mHour++;
                if (mHour>23){
                    mHour = 0;
                    mDay++;
                }
            }
        }
    }
    /**
     * ???????????????
     */
    private void computeTime() {
        mSecond--;
        if (mSecond < 0) {
            mMin--;
            mSecond = 59;
            if (mMin < 0) {
                mMin = 59;
                mHour--;
                if (mHour<0){
                    mHour = 24;
                    mDay--;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_order_moshisi);
        api = WXAPIFactory.createWXAPI(this,null);
        api.registerApp(Constant.APP_ID);
        pricePresenter = new PricePresenter(this,this);
        pricePresenter.updatePriceData(DemoHelper.getInstance().getCurrentUsernName());
        WeakReference<UTOrderDetailFourActivity> reference =  new WeakReference<UTOrderDetailFourActivity>(UTOrderDetailFourActivity.this);
        orderDetailPresenter = new OrderDetailPresenter(this,reference.get());
        presenter = new FWFKPresenter(this,reference.get());
        imagePaths1 = new ArrayList<>();
        imagePaths2 = new ArrayList<>();
        mProgress = CustomProgressDialog.createDialog(reference.get());
        mProgress.setMessage("??????????????????...");
        mProgress.setCancelable(true);
        mProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mProgress.dismiss();
            }
        });
        RequestQueue queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        Intent intent = this.getIntent();
        orderTime = intent.getStringExtra("orderTime");
        orderBody = intent.getStringExtra("orderBody");
        companyId = intent.getStringExtra("companyId");
        userId = intent.getStringExtra("userId");
        orderId = intent.getStringExtra("orderId");
        orderState = intent.getStringExtra("orderState");
        merId = intent.getStringExtra("merId");
        totalAmt = intent.hasExtra("totalAmt")?intent.getStringExtra("totalAmt"):"";
        biaoshi = intent.hasExtra("biaoshi")?intent.getStringExtra("biaoshi"):"";
        biaoshi2 = intent.hasExtra("biaoshi2")?intent.getStringExtra("biaoshi2"):"";
        biaoshi1 = intent.hasExtra("biaoshi1")?intent.getStringExtra("biaoshi1"):"";
        initView();
        initViews();
        btn_cansul.setVisibility(View.VISIBLE);
        rlM_beizhu.setVisibility(View.GONE);
        rl_miaoshu.setFocusable(true);
        rl_miaoshu.setFocusableInTouchMode(true);
        rl_miaoshu.requestFocus();
        if ("01".equals(biaoshi)){
            iv_jiantou.setVisibility(View.VISIBLE);
            ll2_paidan.setVisibility(View.GONE);
            ll2.setVisibility(View.GONE);
            ll3_paidan.setVisibility(View.VISIBLE);
        }
        if ("02".equals(biaoshi)){
            iv_jiantou.setVisibility(View.VISIBLE);
            ll2_paidan.setVisibility(View.VISIBLE);
            ll2.setVisibility(View.VISIBLE);
            ll3_paidan.setVisibility(View.GONE);
        }
        if (!"01".equals(biaoshi2)){
            et_miaoshu.setEnabled(false);
            et_phone.setEnabled(false);
            et_dizhi.setEnabled(false);
            et_name.setEnabled(false);
            et_feiyong.setEnabled(false);
            if (orderState.equals("12")){
                initView0();
            }else if (orderState.equals("03")) {
                initView2();
            } else if (orderState.equals("05") || orderState.equals("07")) {
                initView1();
            } else if (orderState.equals("04")) {
                initView3();
            } else if (orderState.equals("06")) {
                initView4();
            } else if (orderState.equals("08") || orderState.equals("09")) {
                initView5();
            } else if (orderState.equals("10") || orderState.equals("11")) {
                presenter.loadFWFKList(orderId);
                initView6();
            }
        }
        if ("????????????".equals(biaoshi)){
            initView7();
        }
        if ("01".equals(biaoshi1)){
            tv_fasong.setVisibility(View.VISIBLE);
            tv_fasong.setEnabled(true);
            tv_fasong.setText("??????");
            tv_fasong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(UTOrderDetailFourActivity.this, QiYeYuGoActivity.class).putExtra("companyId",companyId)
                            .putExtra("orderId",orderId).putExtra("totalAmt",totalAmt));
                }
            });
        }
        orderDetailPresenter.loadOrderDetail(orderId);
    }

    private void initView0() {
        rlM_beizhu.setVisibility(View.GONE);
        et_Ubeizhu.setVisibility(View.GONE);
        btn_cansul.setVisibility(View.GONE);
        btn_comit.setVisibility(View.VISIBLE);
        btn_comit.setText("???  ???");
        btn_comit.setEnabled(true);
        btn_comit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflaterDl = LayoutInflater.from(UTOrderDetailFourActivity.this);
                RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_setting_dialog, null);
                final Dialog dialog = new AlertDialog.Builder(UTOrderDetailFourActivity.this,R.style.Dialog).create();
                dialog.show();
                dialog.getWindow().setContentView(layout);
                dialog.setCanceledOnTouchOutside(true);
                RelativeLayout re_item1 = (RelativeLayout) dialog.findViewById(R.id.re_item1);
                RelativeLayout re_item2 = (RelativeLayout) dialog.findViewById(R.id.re_item2);
                RelativeLayout re_item3 = (RelativeLayout) dialog.findViewById(R.id.re_item3);
                RelativeLayout re_item5 = (RelativeLayout) dialog.findViewById(R.id.re_item5);
                re_item5.setVisibility(View.VISIBLE);
                TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
                TextView tv_item1 = (TextView) dialog.findViewById(R.id.tv_item1);
                TextView tv_item2 = (TextView) dialog.findViewById(R.id.tv_item2);
                TextView tv_item5 = (TextView) dialog.findViewById(R.id.tv_item5);
                tv_title.setText("?????????????????????");
                tv_item1.setText("??? ??? ??? ???");
                tv_item2.setText("??? ??? ??? ???");
                tv_item5.setText("???????????????");
                re_item1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        pricePresenter.updatePriceData(DemoHelper.getInstance().getCurrentUsernName());

                        zhifu3(orderSum);
                    }
                });
                re_item2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        rechargefromWx(orderSum,orderId);
                    }
                });
                re_item5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        rechargefromZhFb(orderSum,orderId);
                    }
                });
                re_item3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void zhifu3(String sum) {
        if (pass==null||"".equals(pass)){
            ZhifuHelpUtils.showErrorMiMaSHZH(this,pass,"000");
            return;
        }
        if (pass.length()!=6||!ZhifuHelpUtils.isNumeric(pass)){
            ZhifuHelpUtils.showErrorMiMaXG(this,pass,"000");
            return;
        }
        if (errorTime<=0){
            ZhifuHelpUtils.showErrorLing(this);
            return;
        }
        if (DemoApplication.getInstance().getFreezeCurrentType().equalsIgnoreCase("00")) {
        final double orderSu = Double.valueOf(sum);
        Double d = yuE - orderSu;
        if (d >= 0) {
            LayoutInflater inflaterDl = LayoutInflater.from(UTOrderDetailFourActivity.this);
            final RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.dialog_zhifu_pass, null);
            final Dialog dialog = new AlertDialog.Builder(UTOrderDetailFourActivity.this,R.style.Dialog).create();
            dialog.show();
            dialog.getWindow().setContentView(layout);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(lp);
            final PasswordView pwdView = (PasswordView)layout.findViewById(R.id.pwd_view);
            pwdView.setOnFinishInput(new OnPasswordInputFinish() {
                @Override
                public void inputFinish() {
                    final ProgressDialog pd = new ProgressDialog(UTOrderDetailFourActivity.this);
                    pd.setMessage("????????????...");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();
                    if (pwdView.getStrPassword().equals(pass)) {
                        dialog.dismiss();
                        fukuan();
                    } else {
                        pd.dismiss();
                        int times;
                        if (errorTime>0){
                            times = errorTime-1;
                        }else {
                            times = 0;
                        }
                        reduceShRZFCount(times+"");
                        if (times==0) {
                            ZhifuHelpUtils.showErrorLing(UTOrderDetailFourActivity.this);
                        }else {
                            ZhifuHelpUtils.showErrorTishi(UTOrderDetailFourActivity.this,times + "",null,"000");
                        }
                        dialog.dismiss();
                    }
                }
            });
            /**
             *  ??????????????????????????????????????????cancelImageView???????????????????????????
             *  ??????????????????????????????????????????????????????????????????
             *  ??????????????????toast?????? "Biu Biu Biu"?????????"Cancel"*/
            pwdView.getCancelImageView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            pwdView.getForgetTextView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String bs;
                    bs = "000";
                    startActivity(new Intent(UTOrderDetailFourActivity.this, WJPaActivity.class).putExtra("biaoshi",bs));
                }
            });
        } else {
            LayoutInflater inflaterDl = LayoutInflater.from(UTOrderDetailFourActivity.this);
            RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_setting_dialog, null);
            final Dialog dialog = new AlertDialog.Builder(UTOrderDetailFourActivity.this,R.style.Dialog).create();
            dialog.show();
            dialog.getWindow().setContentView(layout);
            dialog.setCanceledOnTouchOutside(true);
            RelativeLayout re_item1 = (RelativeLayout) dialog.findViewById(R.id.re_item1);
            RelativeLayout re_item2 = (RelativeLayout) dialog.findViewById(R.id.re_item2);
            RelativeLayout re_item3 = (RelativeLayout) dialog.findViewById(R.id.re_item3);
            RelativeLayout re_item5 = (RelativeLayout) dialog.findViewById(R.id.re_item5);
            re_item1.setVisibility(View.GONE);
            TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
            TextView tv_item1 = (TextView) dialog.findViewById(R.id.tv_item1);
            TextView tv_item2 = (TextView) dialog.findViewById(R.id.tv_item2);
            TextView tv_item5 = (TextView) dialog.findViewById(R.id.tv_item5);
            tv_title.setText("??????????????????(??????"+yuE+"???),?????????????????????????????????????????????");
            tv_item1.setText("");
            tv_item2.setText("????????????");
            tv_item5.setText("???????????????");
            re_item1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            re_item2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    rechargefromWx(orderSum,orderId);
                }
            });
            re_item5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    rechargefromZhFb(orderSum,orderId);
                }
            });
            re_item3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        } else {
            ToastUtils.showNOrmalToast(this.getApplicationContext(), "????????????????????????");
        }
    }

    private void reduceShRZFCount(final String times) {
        String url = FXConstant.URL_UPDATEZHHU;
        StringRequest request3 = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                com.alibaba.fastjson.JSONObject object = JSON.parseObject(s);
                String code = object.getString("code");
                if (code.equals("SUCCESS")){
                    if (errorTime>0) {
                        errorTime--;
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params1 = new HashMap<>();
                params1.put("merId", DemoHelper.getInstance().getCurrentUsernName());
                params1.put("enterErrorTimes", times+"");
                return params1;
            }
        };
        MySingleton.getInstance(UTOrderDetailFourActivity.this).addToRequestQueue(request3);
    }

    private void fukuan() {
        String url = FXConstant.URL_DingDan_Pay;
        StringRequest request1 = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject object = new JSONObject(s);
                    String code = object.getString("code");
                    if (code.equals("success")) {
                        sendPushMessage(merId,"000");
                        Toast.makeText(UTOrderDetailFourActivity.this, "????????????,?????????????????????", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UTOrderDetailFourActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(UTOrderDetailFourActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("orderId", orderId);
                params.put("userId", DemoHelper.getInstance().getCurrentUsernName());
                params.put("balance", orderSum);
                return params;
            }
        };
        if (DemoApplication.getInstance().getFreezeCurrentType().equalsIgnoreCase("00")) {
            MySingleton.getInstance(UTOrderDetailFourActivity.this).addToRequestQueue(request1);

        } else {
            ToastUtils.showNOrmalToast(UTOrderDetailFourActivity.this, "????????????????????????");
        }
    }
    private void sendPushMessage2(final String userId,final int type) {
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
                param.put("body","????????????");
                if (type==0) {
                    param.put("type","000");
                }else {
                    param.put("type","12");
                }
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
        MySingleton.getInstance(UTOrderDetailFourActivity.this).addToRequestQueue(request);
    }
    private void sendPushMessage(final String hxid1,final String type) {
        String comId = "0";
        if (companyId!=null&&!"".equals(companyId)){
            comId = companyId;
        }
        final String myId = DemoHelper.getInstance().getCurrentUsernName();
        final String finalComId = comId;
        String url = FXConstant.URL_SENDPUSHMSG;
        StringRequest request = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                updateUscount(hxid1);
                reduceUscount(DemoHelper.getInstance().getCurrentUsernName());
                duanxintongzhi(hxid1, "??????????????? ??????:??????????????????????????????", type);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                updateUscount(hxid1);
                reduceUscount(DemoHelper.getInstance().getCurrentUsernName());
                duanxintongzhi(hxid1, "??????????????? ??????:??????????????????????????????", type);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("u_id",hxid1);
                param.put("body","????????????");
                param.put("type",type);
                param.put("userId",myId);
                param.put("companyId", finalComId);
                param.put("companyName","0");
                param.put("companyAdress","0");
                param.put("dynamicSeq","0");
                param.put("createTime","0");
                param.put("fristId","0");
                param.put("dType","0");
                return param;
            }
        };
        MySingleton.getInstance(UTOrderDetailFourActivity.this).addToRequestQueue(request);
    }

    private void reduceUscount(final String currentUsernName) {
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
                param.put("orderUnReadCount","-1");
                param.put("userId",currentUsernName);
                return param;
            }
        };
        MySingleton.getInstance(UTOrderDetailFourActivity.this).addToRequestQueue(request);
    }

    private void duanxintongzhi(final String id, final String message,final String type) {
        String url = FXConstant.URL_DUANXIN_TONGZHI;
        final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                setResult(RESULT_OK);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                setResult(RESULT_OK);
                finish();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("message",message);
                param.put("telNum",id);
                return param;
            }
        };
        UserPermissionUtil.getUserPermission(this, DemoHelper.getInstance().getCurrentUsernName(), "3", new UserPermissionUtil.UserPermissionListener() {
            @Override
            public void onAllow() {
                MySingleton.getInstance(UTOrderDetailFourActivity.this).addToRequestQueue(request);

            }

            @Override
            public void onBan() {

                ToastUtils.showNOrmalToast(getApplicationContext(), "????????????????????????????????????");

            }
        });
    }

    private void rechargefromWx(String zongjia, String uId) {
        String chongzhiId=null;
        chongzhiId = DemoHelper.getInstance().getCurrentUsernName();
        zongjia = (int)(Double.parseDouble(zongjia)*100)+"";
        if (Double.parseDouble(zongjia)>0) {
            final String mubiaoId = chongzhiId + "_" + "2" + "_" + uId;
            Toast.makeText(UTOrderDetailFourActivity.this, "???????????????...", Toast.LENGTH_SHORT).show();
            String wxUrl = FXConstant.URL_ZHIFUWX_DIAOQI;
            final String finalBalance = zongjia;
            StringRequest request = new StringRequest(Request.Method.POST, wxUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    try {
                        SharedPreferences mSharedPreferences = getSharedPreferences("sangu_chongzhi", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("activity", "UTOrderDetailFourActivity");
                        editor.commit();
                        JSONObject object = new JSONObject(s);
                        Log.e("chongzhiac,s", s);
                        String appid = "", mch_id = "", nonce_str = "", sign = "", prepayId = "", timestamp = "";
                        appid = object.getString("appid");
                        nonce_str = object.getString("noncestr");
                        mch_id = object.getString("partnerid");
                        prepayId = object.getString("prepayid");
                        timestamp = object.getString("timestamp");
                        sign = object.getString("sign");
                        PayReq req = new PayReq();
                        req.appId = appid;
                        req.partnerId = mch_id;
                        req.prepayId = prepayId;
                        req.packageValue = "Sign=WXPay";
                        req.nonceStr = nonce_str;
                        //???????????????????????????(??????1000???????????????)
                        req.timeStamp = timestamp;
                        //???????????????????????????,????????????????????????????????????sign????????????,??????????????????????????????????????????????????????sign???,??????????????????,?????????????????????(????????????????????????....)
//                sign = OrderInfoUtil2_0.createSign(parameters);
                        Log.e("TAG", "timestamp=====" + req.timeStamp);
                        req.sign = sign;
                        // ?????????????????????????????????????????????????????????????????????IWXMsg.registerApp????????????????????????
                        api.sendReq(req);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(UTOrderDetailFourActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<>();
                    param.put("body", "?????????-????????????");
                    param.put("detail", "?????????-????????????");
                    param.put("out_trade_no", getNowTime2());
                    param.put("total_fee", finalBalance);
                    param.put("spbill_create_ip", getHostIP());
                    param.put("attach", mubiaoId);
                    return param;
                }
            };
            MySingleton.getInstance(UTOrderDetailFourActivity.this).addToRequestQueue(request);
        }else {
            Toast.makeText(UTOrderDetailFourActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
        }
    }

    private String getNowTime2() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        Date date = new Date();
        return format.format(date);
    }

    private String getHostIP() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("yao", "SocketException");
            e.printStackTrace();
        }
        return hostIp;
    }

    protected void rechargefromZhFb(final String balance, final String uId){
        String chongzhiId=null;
        chongzhiId = DemoHelper.getInstance().getCurrentUsernName();
        try {
            chongzhiId = URLEncoder.encode(chongzhiId,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (balance!=null&&Double.parseDouble(balance)>0) {
            String url = FXConstant.URL_ZhiFu;
            Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap2(Constant.APPID_WX,balance,chongzhiId,"2",uId,null,"?????????-????????????");
            final String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
            final String orderinfo = OrderInfoUtil2_0.getSign(params);
            StringRequest request = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    try {
                        JSONObject object = new JSONObject(s);
                        String sign = object.getString("sign");
                        sign = URLEncoder.encode(sign, "UTF-8");
                        final String orderInfo = orderParam + "&" + "sign=" + sign;
                        Runnable payRunnable = new Runnable() {
                            @Override
                            public void run() {
                                PayTask alipay = new PayTask(UTOrderDetailFourActivity.this);
                                Map<String, String> result = alipay.payV2(orderInfo, true);
                                Message msg = new Message();
                                msg.what = SDK_PAY_FLAG;
                                msg.obj = result;
                                mHandler.sendMessage(msg);
                            }
                        };
                        Thread payThread = new Thread(payRunnable);
                        payThread.start();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(UTOrderDetailFourActivity.this,"???????????????????????????",Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> param = new HashMap<>();
                    param.put("orderInfo",orderinfo);
                    return param;
                }
            };
            MySingleton.getInstance(UTOrderDetailFourActivity.this).addToRequestQueue(request);
        }else {
            Toast.makeText(UTOrderDetailFourActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView6() {
        rlM_beizhu.setVisibility(View.GONE);
        et_Ubeizhu.setVisibility(View.GONE);
        btn_cansul.setVisibility(View.GONE);
        ll_btn.setVisibility(View.INVISIBLE);
        rl_btn1.setVisibility(View.VISIBLE);
        btn_comit.setVisibility(View.VISIBLE);
        tv_fasong.setVisibility(View.INVISIBLE);
//        tv_fenxiang.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveCurrentImage();
//            }
//        });
    }
    private void initView7() {
        rl_ticheng.setVisibility(View.VISIBLE);
        tv_fasong.setEnabled(false);
        et_Ubeizhu.setEnabled(false);
        btn_comit.setEnabled(false);
        btn_cansul.setEnabled(false);
        rlM_beizhu.setEnabled(false);
        iv_kehu.setEnabled(false);
        tv_jiedan_time.setEnabled(false);
        tv_kehu_time.setEnabled(false);
        tv_kehu.setEnabled(false);
        tv_jiedan.setEnabled(false);
        iv_jiedan.setEnabled(false);
    }

    private void initView5() {
        et_Ubeizhu.setEnabled(false);
        btn_cansul.setVisibility(View.GONE);
        btn_comit.setText("??????????????????");
        btn_comit.setEnabled(true);
        btn_comit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(UTOrderDetailFourActivity.this);
                builder.setMessage("????????????????????????????");
                builder.setTitle("??????");
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String url = FXConstant.URL_Update_OrderState;
                        StringRequest request1 = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                try {
                                    JSONObject object = new JSONObject(s);
                                    String code = object.getString("code");
                                    if (code.equals("success")) {
                                        dialog.dismiss();
                                        Toast.makeText(UTOrderDetailFourActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(UTOrderDetailFourActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                dialog.dismiss();
                                Toast.makeText(UTOrderDetailFourActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params3 = new HashMap<String, String>();
                                params3.put("orderId", orderId);
                                params3.put("orderState", "09");
                                params3.put("userId", DemoHelper.getInstance().getCurrentUsernName());
                                return params3;
                            }
                        };
                        MySingleton.getInstance(UTOrderDetailFourActivity.this).addToRequestQueue(request1);
                    }
                });
                builder.show();
            }
        });
    }

    private void initView4() {
        et_Ubeizhu.setEnabled(false);
        btn_cansul.setVisibility(View.GONE);
        btn_comit.setVisibility(View.GONE);
    }

    private void initView3() {
        et_dizhi.setEnabled(false);
        et_name.setEnabled(false);
        et_miaoshu.setEnabled(false);
        et_phone.setEnabled(false);
        btn_comit.setText("????????????");
        btn_comit.setEnabled(true);
        btn_cansul.setEnabled(true);
        et_Ubeizhu.setEnabled(true);
        btn_cansul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_Ubeizhu.setFocusable(true);
                et_Ubeizhu.setFocusableInTouchMode(true);
                et_Ubeizhu.requestFocus();
                final String beizhu = et_Ubeizhu.getText().toString().trim();
                if (!TextUtils.isEmpty(beizhu)) {
                    Intent intent = new Intent(UTOrderDetailFourActivity.this,QianMingActivity.class);
                    intent.putExtra("orderId",orderId);
                    intent.putExtra("remark", beizhu);
                    intent.putExtra("biaoshi","13");
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(UTOrderDetailFourActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        tv_fasong.setVisibility(View.VISIBLE);
        tv_fasong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (send_id2!=null&&!"".equals(send_id2)&&!send_id2.equalsIgnoreCase(null)){
                    LayoutInflater inflaterDl = LayoutInflater.from(UTOrderDetailFourActivity.this);
                    RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.dialog_ok, null);
                    final Dialog dialog2 = new AlertDialog.Builder(UTOrderDetailFourActivity.this,R.style.Dialog).create();
                    dialog2.show();
                    dialog2.getWindow().setContentView(layout);
                    dialog2.setCanceledOnTouchOutside(false);
                    dialog2.setCancelable(false);
                    TextView tv_title = (TextView) dialog2.findViewById(R.id.title_tv);
                    Button btn_ok = (Button) dialog2.findViewById(R.id.btn_ok);
                    tv_title.setText("?????????????????????????????????????????????????????????????????????");
                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog2.dismiss();
                        }
                    });
                    return;
                }
                LayoutInflater inflaterDl = LayoutInflater.from(UTOrderDetailFourActivity.this);
                RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_order_send, null);
                final Dialog dialog = new AlertDialog.Builder(UTOrderDetailFourActivity.this,R.style.Dialog).create();
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
                        PermissionUtil permissionUtil = new PermissionUtil(UTOrderDetailFourActivity.this);
                        permissionUtil.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                                new PermissionListener() {
                                    @Override
                                    public void onGranted() {
                                        //???????????????????????????
                                        Intent intent = new Intent(UTOrderDetailFourActivity.this,AddressListTwoActivity.class);
                                        intent.putExtra("biaoshi","03");
                                        intent.putExtra("orderId",orderId);
                                        intent.putExtra("orderBody",orderBody);
                                        intent.putExtra("hasId1",send_id1);
                                        intent.putExtra("hasId2",send_id2);
                                        intent.putExtra("hasId3",merId);
                                        intent.putExtra("hasId4",DemoHelper.getInstance().getCurrentUsernName());
                                        startActivityForResult(intent,0);
                                    }
                                    @Override
                                    public void onDenied(List<String> deniedPermission) {
                                        //Toast???????????????????????????
                                        Toast.makeText(getApplicationContext(),"????????????????????????????????????",Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onShouldShowRationale(List<String> deniedPermission) {
                                        //Toast????????????????????????????????????
                                        Toast.makeText(getApplicationContext(),"????????????????????????????????????????????????????????????????????????",Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });
                re_item2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent3 = new Intent(UTOrderDetailFourActivity.this,FriendActivity.class);
                        intent3.putExtra("biaoshi","03");
                        intent3.putExtra("orderId",orderId);
                        intent3.putExtra("orderBody",orderBody);
                        intent3.putExtra("hasId1",send_id1);
                        intent3.putExtra("hasId2",send_id2);
                        intent3.putExtra("hasId3",merId);
                        intent3.putExtra("hasId4",DemoHelper.getInstance().getCurrentUsernName());
                        startActivityForResult(intent3,0);
                    }
                });
                btn_phone3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String userId = et_phone3.getText().toString().trim();
                        if (TextUtils.isEmpty(userId)||userId.length()!=11) {
                            Toast.makeText(getApplicationContext(), "????????????????????????????????????!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (userId.equals(DemoHelper.getInstance().getCurrentUsernName())||userId.equals(merId)||userId.equals(send_id1)||userId.equals(send_id2)){
                            LayoutInflater inflaterDl = LayoutInflater.from(UTOrderDetailFourActivity.this);
                            RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.dialog_ok, null);
                            final Dialog dialog2 = new AlertDialog.Builder(UTOrderDetailFourActivity.this,R.style.Dialog).create();
                            dialog2.show();
                            dialog2.getWindow().setContentView(layout);
                            dialog2.setCanceledOnTouchOutside(false);
                            dialog2.setCancelable(false);
                            TextView tv_title = (TextView) dialog2.findViewById(R.id.title_tv);
                            Button btn_ok = (Button) dialog2.findViewById(R.id.btn_ok);
                            tv_title.setText("??????????????????????????????,?????????????????????");
                            btn_ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog2.dismiss();
                                }
                            });
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
        });

        if ((resv5!=null&&resv5.length()>0)||(companyId!=null&&companyId.length()>0)){
            tv_kehu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UTOrderDetailFourActivity.this, QianMingActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("merId", merId);
                    intent.putExtra("balance", orderSum);
                    intent.putExtra("companyId", companyId);
                    intent.putExtra("companyAmt",resv5Qiye);
                    intent.putExtra("balance1", resv5Geren);
                    intent.putExtra("imageUrl1",imagePaths1);
                    intent.putExtra("imageUrl2",imagePaths2);
                    intent.putExtra("dynamicSeq", dynamicSeq);
                    intent.putExtra("create_time", create_time);
                    intent.putExtra("biaoshi", "11");
                    startActivity(intent);
                    finish();
                }
            });
            btn_comit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UTOrderDetailFourActivity.this, QianMingActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("merId", merId);
                    intent.putExtra("balance", orderSum);
                    intent.putExtra("companyId", companyId);
                    intent.putExtra("companyAmt",resv5Qiye);
                    intent.putExtra("balance1", resv5Geren);
                    intent.putExtra("imageUrl1",imagePaths1);
                    intent.putExtra("imageUrl2",imagePaths2);
                    intent.putExtra("dynamicSeq", dynamicSeq);
                    intent.putExtra("create_time", create_time);
                    intent.putExtra("biaoshi", "11");
                    startActivity(intent);
                    finish();
                }
            });
        }else {
            tv_kehu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UTOrderDetailFourActivity.this, QianMingActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("merId", merId);
                    intent.putExtra("balance", orderSum);
                    intent.putExtra("imageUrl1",imagePaths1);
                    intent.putExtra("imageUrl2",imagePaths2);
                    intent.putExtra("dynamicSeq", dynamicSeq);
                    intent.putExtra("create_time", create_time);
                    intent.putExtra("biaoshi", "11");
                    startActivity(intent);
                    finish();
                }
            });
            btn_comit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UTOrderDetailFourActivity.this, QianMingActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("merId", merId);
                    intent.putExtra("balance", orderSum);
                    intent.putExtra("imageUrl1",imagePaths1);
                    intent.putExtra("imageUrl2",imagePaths2);
                    intent.putExtra("dynamicSeq", dynamicSeq);
                    intent.putExtra("create_time", create_time);
                    intent.putExtra("biaoshi", "11");
                    startActivity(intent);
                    finish();
                }
            });
        }
//        tv_kehu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(UTOrderDetailFourActivity.this,QianMingActivity.class);
//                intent.putExtra("orderId",orderId);
//                intent.putExtra("merId",merId);
//                intent.putExtra("balance", orderSum);
//                intent.putExtra("biaoshi","11");
//                startActivity(intent);
//                finish();
//            }
//        });
//        btn_comit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(UTOrderDetailFourActivity.this,QianMingActivity.class);
//                intent.putExtra("orderId",orderId);
//                intent.putExtra("merId",merId);
//                intent.putExtra("balance", orderSum);
//                intent.putExtra("biaoshi","11");
//                startActivity(intent);
//                finish();
//            }
//        });
    }

    private void initView2() {
        btn_cansul.setVisibility(View.VISIBLE);
        btn_comit.setVisibility(View.VISIBLE);
        btn_cansul.setEnabled(false);
        btn_comit.setEnabled(true);
        btn_cansul.setText("??????????????????");
        btn_cansul.setBackgroundResource(R.drawable.fx_bg_btn_gray);
        btn_comit.setText("????????????");
        et_Ubeizhu.setEnabled(true);
        btn_comit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_Ubeizhu.setFocusable(true);
                et_Ubeizhu.setFocusableInTouchMode(true);
                et_Ubeizhu.requestFocus();
                final String beizhu = et_Ubeizhu.getText().toString().trim();
                if (!TextUtils.isEmpty(beizhu)) {
                    Intent intent = new Intent(UTOrderDetailFourActivity.this,QianMingActivity.class);
                    intent.putExtra("orderId",orderId);
                    intent.putExtra("remark", beizhu);
                    intent.putExtra("biaoshi","13");
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(UTOrderDetailFourActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        tv_fasong.setVisibility(View.VISIBLE);
        tv_fasong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (send_id2!=null&&!"".equals(send_id2)&&!send_id2.equalsIgnoreCase(null)){
                    LayoutInflater inflaterDl = LayoutInflater.from(UTOrderDetailFourActivity.this);
                    RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.dialog_ok, null);
                    final Dialog dialog2 = new AlertDialog.Builder(UTOrderDetailFourActivity.this,R.style.Dialog).create();
                    dialog2.show();
                    dialog2.getWindow().setContentView(layout);
                    dialog2.setCanceledOnTouchOutside(false);
                    dialog2.setCancelable(false);
                    TextView tv_title = (TextView) dialog2.findViewById(R.id.title_tv);
                    Button btn_ok = (Button) dialog2.findViewById(R.id.btn_ok);
                    tv_title.setText("?????????????????????????????????????????????????????????????????????");
                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog2.dismiss();
                        }
                    });
                    return;
                }
                LayoutInflater inflaterDl = LayoutInflater.from(UTOrderDetailFourActivity.this);
                RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_order_send, null);
                final Dialog dialog = new AlertDialog.Builder(UTOrderDetailFourActivity.this,R.style.Dialog).create();
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
                        PermissionUtil permissionUtil = new PermissionUtil(UTOrderDetailFourActivity.this);
                        permissionUtil.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                                new PermissionListener() {
                                    @Override
                                    public void onGranted() {
                                        //???????????????????????????
                                        Intent intent = new Intent(UTOrderDetailFourActivity.this,AddressListTwoActivity.class);
                                        intent.putExtra("biaoshi","03");
                                        intent.putExtra("orderId",orderId);
                                        intent.putExtra("orderBody",orderBody);
                                        intent.putExtra("hasId1",send_id1);
                                        intent.putExtra("hasId2",send_id2);
                                        intent.putExtra("hasId3",merId);
                                        intent.putExtra("hasId4",DemoHelper.getInstance().getCurrentUsernName());
                                        startActivityForResult(intent,0);
                                    }
                                    @Override
                                    public void onDenied(List<String> deniedPermission) {
                                        //Toast???????????????????????????
                                        Toast.makeText(getApplicationContext(),"????????????????????????????????????",Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onShouldShowRationale(List<String> deniedPermission) {
                                        //Toast????????????????????????????????????
                                        Toast.makeText(getApplicationContext(),"????????????????????????????????????????????????????????????????????????",Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });
                re_item2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent3 = new Intent(UTOrderDetailFourActivity.this,FriendActivity.class);
                        intent3.putExtra("biaoshi","03");
                        intent3.putExtra("orderId",orderId);
                        intent3.putExtra("orderBody",orderBody);
                        intent3.putExtra("hasId1",send_id1);
                        intent3.putExtra("hasId2",send_id2);
                        intent3.putExtra("hasId3",merId);
                        intent3.putExtra("hasId4",DemoHelper.getInstance().getCurrentUsernName());
                        startActivityForResult(intent3,0);
                    }
                });
                btn_phone3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String userId = et_phone3.getText().toString().trim();
                        if (TextUtils.isEmpty(userId)||userId.length()!=11) {
                            Toast.makeText(getApplicationContext(), "????????????????????????????????????!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (userId.equals(DemoHelper.getInstance().getCurrentUsernName())||userId.equals(merId)||userId.equals(send_id1)||userId.equals(send_id2)){
                            LayoutInflater inflaterDl = LayoutInflater.from(UTOrderDetailFourActivity.this);
                            RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.dialog_ok, null);
                            final Dialog dialog2 = new AlertDialog.Builder(UTOrderDetailFourActivity.this,R.style.Dialog).create();
                            dialog2.show();
                            dialog2.getWindow().setContentView(layout);
                            dialog2.setCanceledOnTouchOutside(false);
                            dialog2.setCancelable(false);
                            TextView tv_title = (TextView) dialog2.findViewById(R.id.title_tv);
                            Button btn_ok = (Button) dialog2.findViewById(R.id.btn_ok);
                            tv_title.setText("??????????????????????????????,?????????????????????");
                            btn_ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog2.dismiss();
                                }
                            });
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
        });
    }

    private void queryUserInfo(final String userId) {
        String url = FXConstant.URL_Get_UserInfo+userId;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                com.alibaba.fastjson.JSONObject object = JSON.parseObject(s);
                String code = object.getString("code");
                sendPushMessage2(userId,1);
                if ("???????????????".equals(code)){
                    SendMessage(userId,0,"");
                    SendMessage1(userId);
                }else {
                    com.alibaba.fastjson.JSONObject userInfo = object.getJSONObject("userInfo");
                    String name = userInfo.getString("uName");
                    if (name == null||"".equals(name)){
                        name="";
                    }else {
                        name = name+":";
                    }
                    SendMessage(userId,1,name);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        MySingleton.getInstance(UTOrderDetailFourActivity.this).addToRequestQueue(request);
    }

    private void SendMessage1(final String userId) {
        String name = DemoApplication.getInstance().getCurrentUser().getName();
        final String loginId = DemoHelper.getInstance().getCurrentUsernName();
        if (name==null||"".equals(name)){
            name = loginId;
        }
        String url = FXConstant.URL_DUANXIN_TONGZHI;
        final String finalName = name;
        final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                sendToUser(userId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "??????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("message", "?????????????????????("+orderBody+"),?????????????????????,?????????????????????????????????????????????????????????");
                param.put("telNum", userId);
                Log.e("utorderdeac,sm1",param.toString());
                return param;
            }
        };
        UserPermissionUtil.getUserPermission(this, DemoHelper.getInstance().getCurrentUsernName(), "3", new UserPermissionUtil.UserPermissionListener() {
            @Override
            public void onAllow() {
                MySingleton.getInstance(UTOrderDetailFourActivity.this).addToRequestQueue(request);

            }

            @Override
            public void onBan() {

                ToastUtils.showNOrmalToast(getApplicationContext(), "????????????????????????????????????");

            }
        });
    }

    private void SendMessage(final String userId, final int type,final String name) {
        String lists;
        if (type==1){
            lists = userId+","+merId;
        }else {
            lists = merId;
        }
        if (send_id1!=null&&!"".equals(send_id1)){
            lists = lists+","+send_id1;
        }
        String url = FXConstant.URL_DUANXIN_TONGZHI;
        final String finalLists = lists;
        final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (type==1) {
                    sendToUser(userId);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (type==1) {
                    sendToUser(userId);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("message", "?????????????????????(" + orderBody + "),??????("+name+userId+")?????????????????????");
                param.put("telNum", finalLists);
                Log.e("utorderdeac,sm",param.toString());
                return param;
            }
        };
        UserPermissionUtil.getUserPermission(this, DemoHelper.getInstance().getCurrentUsernName(), "3", new UserPermissionUtil.UserPermissionListener() {
            @Override
            public void onAllow() {
                MySingleton.getInstance(UTOrderDetailFourActivity.this).addToRequestQueue(request);

            }

            @Override
            public void onBan() {

                ToastUtils.showNOrmalToast(getApplicationContext(), "????????????????????????????????????");

            }
        });
    }

    private void sendToUser(final String username) {
        String url = FXConstant.URL_FASONG_DZDANJU;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                updateaddUscount(username);
                com.alibaba.fastjson.JSONObject object = JSON.parseObject(s);
                String code = object.getString("code");
                if ("success".equals(code)){
                    Toast.makeText(getApplicationContext(),"???????????????",Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),"??????????????????,???????????????",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("order_id",orderId);
                param.put("send_id",DemoHelper.getInstance().getCurrentUsernName());
                param.put("u_id",username);
                return param;
            }
        };
        MySingleton.getInstance(UTOrderDetailFourActivity.this).addToRequestQueue(request);
    }
    private void updateaddUscount(final String username) {
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
                param.put("thirdPartyCount","1");
                param.put("userId",username);
                return param;
            }
        };
        MySingleton.getInstance(UTOrderDetailFourActivity.this).addToRequestQueue(request);
    }

    private void initView1() {
        tv_zhifuxianshi.setText("?????????");
        rlM_beizhu.setVisibility(View.GONE);
        btn_cansul.setVisibility(View.GONE);
        btn_comit.setVisibility(View.VISIBLE);
        tv_fasong.setVisibility(View.INVISIBLE);
//        tv_fasong.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveCurrentImage();
//            }
//        });
        if ("05".equals(orderState)) {
            rlM_beizhu.setVisibility(View.GONE);
            et_Ubeizhu.setVisibility(View.GONE);
            btn_comit.setText("????????????");
            btn_comit.setEnabled(true);
            btn_comit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(UTOrderDetailFourActivity.this)
                            .setTitle("??????")
                            .setMessage("????????????????????????")
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    shenqingshouhou();
                                }
                            })
                            .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            });
        }else {
            rlM_beizhu.setVisibility(View.VISIBLE);
            btn_comit.setVisibility(View.VISIBLE);
            btn_comit.setText("????????????");
            btn_comit.setEnabled(false);
            btn_comit.setBackgroundResource(R.drawable.fx_bg_btn_gray);
        }
    }
    private void shenqingshouhou() {
        String url = FXConstant.URL_SHOUHOU_UPDATE;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(UTOrderDetailFourActivity.this,"????????????,?????????????????????",Toast.LENGTH_SHORT).show();
                if (resv5.length()>0||companyId.length()>0){
                    updateQjcount();
                }else {
                    updateUscount(merId);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(UTOrderDetailFourActivity.this,"?????????????????????",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("orderId",orderId);
                param.put("state","10");
                return param;
            }
        };
        MySingleton.getInstance(UTOrderDetailFourActivity.this).addToRequestQueue(request);
    }
    private void updateUscount(final String hxid1) {
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
                param.put("orderUnReadCount","1");
                param.put("userId",hxid1);
                return param;
            }
        };
        MySingleton.getInstance(UTOrderDetailFourActivity.this).addToRequestQueue(request);
    }


    private void updateQjcount() {
        String url = FXConstant.URL_UPDATE_UNREADQIYE;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e("addfriend,s",s);
                setResult(RESULT_OK);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("addfriend,e",volleyError.toString());
                setResult(RESULT_OK);
                finish();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("orderCount","1");
                param.put("companyId",companyId);
                return param;
            }
        };
        MySingleton.getInstance(UTOrderDetailFourActivity.this).addToRequestQueue(request);
    }

    private void initView() {
        hoursTv = (TextView) findViewById(R.id.tv_hours);
        tv2 = (TextView) findViewById(R.id.tv2);
        daysTv = (TextView) findViewById(R.id.tv_days);
        minutesTv = (TextView) findViewById(R.id.tv_mins);
        secondsTv = (TextView) findViewById(R.id.tv_mills);
        tv_all = (TextView) findViewById(R.id.tv_all);
        countDown = (RelativeLayout) findViewById(R.id.countDown);
        ll_btn = (LinearLayout) findViewById(R.id.ll_btn);
        ll2_paidan = (LinearLayout) findViewById(R.id.ll2_paidan);
        ll2 = (LinearLayout) findViewById(R.id.ll2);
        ll3_paidan = (LinearLayout) findViewById(R.id.ll3_paidan);
        et_miaoshu = (EditText) findViewById(R.id.et_miaoshu);
        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_dizhi = (EditText) findViewById(R.id.et_dizhi);
        et_feiyong = (EditText) findViewById(R.id.et_feiyong);
        et_Ubeizhu = (EditText) findViewById(R.id.et_Ubeizhu);
        tev_paidan_caiwu = (TextView) findViewById(R.id.tev_paidan_caiwu);
        tev_paidan_kehu = (TextView) findViewById(R.id.tev_paidan_kehu);
        tev_kehu = (TextView) findViewById(R.id.tev_kehu);
        tev_jiedan = (TextView) findViewById(R.id.tev_jiedan);
        tev_paidan_id1 = (TextView) findViewById(R.id.tev_paidan_id1);
        tv_fasong = (TextView) findViewById(R.id.tv_fasong);
        tv_kehu = (TextView) findViewById(R.id.tv_kehu);
        tv_kehu_time = (TextView) findViewById(R.id.tv_kehu_time);
        tv_jiedan = (TextView) findViewById(R.id.tv_jiedan);
        tv_qiye_ticheng = (TextView) findViewById(R.id.tv_qiye_ticheng);
        tv_jiedan_time = (TextView) findViewById(R.id.tv_jiedan_time);
        tv_paidan_id1 = (TextView) findViewById(R.id.tv_paidan_id1);
        tv_paidan_id1_time = (TextView) findViewById(R.id.tv_paidan_id1_time);
        tv_paidan_caiwu = (TextView) findViewById(R.id.tv_paidan_caiwu);
        tv_paidan_caiwu_time = (TextView) findViewById(R.id.tv_paidan_caiwu_time);
        tv_paidan_kehu = (TextView) findViewById(R.id.tv_paidan_kehu);
        tv_paidan_kehu_time = (TextView) findViewById(R.id.tv_paidan_kehu_time);
        tv_zhifuxianshi = (TextView) findViewById(R.id.tv_zhifuxianshi);
        tv_count_fankui = (TextView) findViewById(R.id.tv_count_fankui);
        rl_xinxi2 = (RelativeLayout) findViewById(R.id.rl_xinxi2);
        rl_miaoshu = (RelativeLayout) findViewById(R.id.rl_miaoshu);
        rl_btn1 = (RelativeLayout) findViewById(R.id.rl_btn1);
        rlM_beizhu = (RelativeLayout) findViewById(R.id.rlM_beizhu);
        rl_paidan_id1 = (RelativeLayout) findViewById(R.id.rl_paidan_id1);
        rl_ticheng = (RelativeLayout) findViewById(R.id.rl_ticheng);
        iv_kehu = (ImageView) findViewById(R.id.iv_kehu);
        iv_jiedan = (ImageView) findViewById(R.id.iv_jiedan);
        iv_paidan_id1 = (ImageView) findViewById(R.id.iv_paidan_id1);
        iv_paidan_caiwu = (ImageView) findViewById(R.id.iv_paidan_caiwu);
        iv_paidan_kehu = (ImageView) findViewById(R.id.iv_paidan_kehu);
        iv_jiantou = (ImageView) findViewById(R.id.iv_jiantou);
        btn_comit = (Button) findViewById(R.id.btn_comit);
        btn_cansul = (Button) findViewById(R.id.btn_cansul);
        btn_comit2 = (Button) findViewById(R.id.btn_comit2);
    }

    private void downloadFile(final String url, String fileName, final int size){
        File saveFile = new FileStorage("offline").createCropFile(fileName,null);
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(UTOrderDetailFourActivity.this, "com.sangu.app.fileprovider", saveFile);//??????FileProvider????????????content?????????Uri
            grantUriPermission(getPackageName(), imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            imageUri = Uri.fromFile(saveFile);
        }
        File f = new File(saveFile.getPath());
        if (size==1) {
            filePath1 = f.getAbsolutePath();
        }else if (size==2){
            filePath2 = f.getAbsolutePath();
        }else if (size==3){
            filePath3 = f.getAbsolutePath();
        }else if (size==4){
            filePath4 = f.getAbsolutePath();
        }else if (size==5){
            filePath5 = f.getAbsolutePath();
        }else if (size==6){
            filePath6 = f.getAbsolutePath();
        }else if (size==7){
            filePath7 = f.getAbsolutePath();
        }else if (size==8){
            filePath8 = f.getAbsolutePath();
        }
        if(!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (f.exists()) {
            final File finalSaveFile = f;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    OkHttpClient client = new OkHttpClient();
                    try {
                        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();
                        okhttp3.Response response = client.newCall(request).execute();
                        InputStream is = response.body().byteStream();
                        final Bitmap bm = BitmapFactory.decodeStream(is);
                        if (bm != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    saveToSD(bm, finalSaveFile);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    private void saveToSD(Bitmap mBitmap,File f) {
        try {
            //???????????????????????????????????????
            if(!f.exists()){
                f.createNewFile();
            }
            //?????????
            FileOutputStream out = new FileOutputStream(f);
            /** mBitmap.compress ????????????
             *
             *  Bitmap.CompressFormat.PNG   ???????????????
             *   100  ??????????????????0-100???
             *   out  ???????????????
             */
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            downloadSize++;
            if (allSize==downloadSize) {
                handler.sendEmptyMessage(1);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initViews(){
        et_feiyong.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(UTOrderDetailFourActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(UTOrderDetailFourActivity.this, onAddPicClickListener1);
        adapter.setList(selectMedia1);
        adapter.setSelectMax(4);
        adapter.setType(1);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                switch (selectMedia1.get(position).getType()) {
                    case FunctionConfig.TYPE_IMAGE:
                        // ???????????? ??????????????? ???????????????????????????
                        PictureConfig.getInstance().externalPicturePreview(UTOrderDetailFourActivity.this, position, selectMedia1);
                        break;
                    case FunctionConfig.TYPE_VIDEO:
                        // ????????????
                        if (selectMedia1.size() > 0) {
                            PictureConfig.getInstance().externalPictureVideo(UTOrderDetailFourActivity.this, selectMedia1.get(position).getPath());
                        }
                        break;
                }
            }
        });
        recyclerView2 = (RecyclerView) findViewById(R.id.recycler2);
        FullyGridLayoutManager manager2 = new FullyGridLayoutManager(UTOrderDetailFourActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView2.setLayoutManager(manager2);
        adapter2 = new GridImageAdapter(UTOrderDetailFourActivity.this, onAddPicClickListener2);
        adapter2.setList(selectMedia2);
        adapter2.setSelectMax(4);
        adapter2.setType(2);
        recyclerView2.setAdapter(adapter2);
        adapter2.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                switch (selectMedia2.get(position).getType()) {
                    case FunctionConfig.TYPE_IMAGE:
                        // ???????????? ??????????????? ???????????????????????????
//                        PictureConfig.getInstance().externalPicturePreview(MainActivity.this, "/custom_file", position, selectMedia);
                        PictureConfig.getInstance().externalPicturePreview(UTOrderDetailFourActivity.this, position, selectMedia2);
                        break;
                    case FunctionConfig.TYPE_VIDEO:
                        // ????????????
                        if (selectMedia2.size() > 0) {
                            PictureConfig.getInstance().externalPictureVideo(UTOrderDetailFourActivity.this, selectMedia2.get(position).getPath());
                        }
                        break;
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
    /**
     * ????????????????????????
     */
    private GridImageAdapter.onAddPicClickListener onAddPicClickListener2 = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick(int type, int position) {
            switch (type) {
                case 0:
                    goCamera(4,"02");
                    break;
                case 1:
                    // ????????????
                    imagePaths2.remove(position);
                    selectMedia2.remove(position);
                    adapter2.notifyItemRemoved(position);
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
        WeakReference<UTOrderDetailFourActivity> reference = new WeakReference<UTOrderDetailFourActivity>(UTOrderDetailFourActivity.this);
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
            imagePaths1.clear();
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
    /**
     * ??????????????????
     */
    private PictureConfig.OnSelectResultCallback resultCallback2 = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            imagePaths2.clear();
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
                for (int i=0;i<selectMedia2.size();i++){
                    imagePaths2.add(selectMedia2.get(i).getCompressPath());
                }
            }
        }

        @Override
        public void onSelectSuccess(LocalMedia media) {
            imagePaths2.clear();
            // ????????????
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            orderDetailPresenter.loadOrderDetail(orderId);
        }
    }

    @Override
    public void back(View view) {
        super.back(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imagePaths1!=null){
            imagePaths1.clear();
            imagePaths1=null;
        }
        if (selectMedia1!=null){
            selectMedia1.clear();
            selectMedia1=null;
        }
        if (selectMedia2!=null){
            selectMedia2.clear();
            selectMedia2=null;
        }
        if (imagePaths2!=null){
            imagePaths2.clear();
            imagePaths2=null;
        }
    }

    @Override
    public void updataFuWuFanKuiList(List<FWFKInfo> fuWuFKinfo) {
        int count = fuWuFKinfo.size();
        tv_count_fankui.setText("["+count+"]");
        btn_comit2.setText("????????????");
        btn_comit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderState.equals("11")) {
                    new AlertDialog.Builder(UTOrderDetailFourActivity.this)
                            .setTitle("??????")
                            .setMessage("????????????????????????")
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    shenqingshouhou();
                                }
                            })
                            .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }else if (orderState.equals("10")){
                    startActivity(new Intent(UTOrderDetailFourActivity.this, FWFKListActivity.class)
                            .putExtra("biaoshi", "00").putExtra("orderId", orderId).putExtra("resv5",resv5).putExtra("companyId",companyId));
                }
            }
        });
        tv_count_fankui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UTOrderDetailFourActivity.this, FWFKListActivity.class)
                        .putExtra("biaoshi", "02").putExtra("orderId", orderId).putExtra("resv5",resv5).putExtra("companyId",companyId));
            }
        });
    }

    @Override
    public void updateCurrentOrderDetail(OrderDetail orderDetail) {
        dynamicSeq = orderDetail.getDynamicSeq();
        create_time = orderDetail.getCreate_time();
        yueding = TextUtils.isEmpty(orderDetail.getResv1())?"":orderDetail.getResv1();
        resv2 = TextUtils.isEmpty(orderDetail.getResv2())?"":orderDetail.getResv2();
        image1 = TextUtils.isEmpty(orderDetail.getImage1())?"":orderDetail.getImage1();
        image2 = TextUtils.isEmpty(orderDetail.getImage2())?"":orderDetail.getImage2();
        image3 = TextUtils.isEmpty(orderDetail.getImage3())?"":orderDetail.getImage3();
        image4 = TextUtils.isEmpty(orderDetail.getImage2())?"":orderDetail.getImage4();
        time1 = TextUtils.isEmpty(orderDetail.getTime1())?"":orderDetail.getTime1();
        time2 = TextUtils.isEmpty(orderDetail.getTime2())?"":orderDetail.getTime2();
        time3 = TextUtils.isEmpty(orderDetail.getTime3())?"":orderDetail.getTime3();
        resv4 = TextUtils.isEmpty(orderDetail.getResv4())?"":orderDetail.getResv4();
        oSignature = TextUtils.isEmpty(orderDetail.getoSignature())?"":orderDetail.getoSignature();
        mSignature = TextUtils.isEmpty(orderDetail.getmSignature())?"":orderDetail.getmSignature();
        send_id1 = orderDetail.getSend_id1();
        send_id2 = orderDetail.getSend_id2();
        orderProject = TextUtils.isEmpty(orderDetail.getOrderProject())?"":orderDetail.getOrderProject();
        orderNumber = TextUtils.isEmpty(orderDetail.getOrderNumber())?"":orderDetail.getOrderNumber();
        orderAmt = TextUtils.isEmpty(orderDetail.getOrderAmt())?"":orderDetail.getOrderAmt();
        orderSum = TextUtils.isEmpty(orderDetail.getOrderSum())?"":orderDetail.getOrderSum();
        oImage = TextUtils.isEmpty(orderDetail.getoImage())?"":orderDetail.getoImage();
        mImage = TextUtils.isEmpty(orderDetail.getmImage())?"":orderDetail.getmImage();
        beizhu = TextUtils.isEmpty(orderDetail.getRemark())?"":orderDetail.getRemark();
        resv5 = TextUtils.isEmpty(orderDetail.getResv5())?"":orderDetail.getResv5();
//        yueding = TextUtils.isEmpty(orderDetail.getResv1())?"":orderDetail.getResv1();
//        resv2 = TextUtils.isEmpty(orderDetail.getResv2())?"":orderDetail.getResv2();
        String oSigntrue = TextUtils.isEmpty(orderDetail.getoSignature())?"":orderDetail.getoSignature();
        String mSigntrue = TextUtils.isEmpty(orderDetail.getmSignature())?"":orderDetail.getmSignature();
        if (isFirst) {
            String sImage1 = orderDetail.getSimage1();
            String sImage2 = orderDetail.getSimage2();
            String sImage3 = orderDetail.getSimage3();
            String sImage4 = orderDetail.getSimage4();
            String sImage5 = orderDetail.getSimage5();
            String sImage6 = orderDetail.getSimage6();
            String sImage7 = orderDetail.getSimage7();
            String sImage8 = orderDetail.getSimage8();
            if (sImage1 != null && !"".equals(sImage1) && !"null".equals(sImage1)) {
                allSize++;
                allSize1++;
            }
            if (sImage2 != null && !"".equals(sImage2) && !"null".equals(sImage2)) {
                allSize++;
                allSize1++;
            }
            if (sImage3 != null && !"".equals(sImage3) && !"null".equals(sImage3)) {
                allSize++;
                allSize1++;
            }
            if (sImage4 != null && !"".equals(sImage4) && !"null".equals(sImage4)) {
                allSize++;
                allSize1++;
            }
            if (sImage5 != null && !"".equals(sImage5) && !"null".equals(sImage5)) {
                allSize++;
                allSize2++;
            }
            if (sImage6 != null && !"".equals(sImage6) && !"null".equals(sImage6)) {
                allSize++;
                allSize2++;
            }
            if (sImage7 != null && !"".equals(sImage7) && !"null".equals(sImage7)) {
                allSize++;
                allSize2++;
            }
            if (sImage8 != null && !"".equals(sImage8) && !"null".equals(sImage8)) {
                allSize++;
                allSize2++;
            }
            int type = FunctionConfig.TYPE_IMAGE;
            if (sImage1 != null && !"".equals(sImage1) && !sImage1.equalsIgnoreCase("null")) {
                if (orderState.equals("04")) {
                    if (mProgress!=null&&!mProgress.isShowing()){
                        mProgress.show();
                    }
                    downloadFile(FXConstant.URL_SIGN_FOUR + sImage1, sImage1, 1);
                } else {
                    LocalMedia media = new LocalMedia();
                    media.setType(type);
                    media.setCompressed(false);
                    media.setCut(false);
                    media.setIsChecked(true);
                    imagePaths1.add(FXConstant.URL_SIGN_FOUR + sImage1);
                    media.setNum(imagePaths1.size());
                    media.setPath(FXConstant.URL_SIGN_FOUR + sImage1);
                    selectMedia1.add(media);
                    recyclerView.setEnabled(false);
                    recyclerView2.setEnabled(false);
                }
            }
            if (sImage2 != null && !"".equals(sImage2) && !sImage2.equalsIgnoreCase("null")) {
                if (orderState.equals("04")) {
                    if (mProgress!=null&&!mProgress.isShowing()){
                        mProgress.show();
                    }
                    downloadFile(FXConstant.URL_SIGN_FOUR + sImage2, sImage2, 2);
                } else {
                    LocalMedia media = new LocalMedia();
                    media.setType(type);
                    media.setCompressed(false);
                    media.setCut(false);
                    media.setIsChecked(true);
                    imagePaths1.add(FXConstant.URL_SIGN_FOUR + sImage2);
                    media.setPath(FXConstant.URL_SIGN_FOUR + sImage2);
                    media.setNum(imagePaths1.size());
                    selectMedia1.add(media);
                    recyclerView.setEnabled(false);
                    recyclerView2.setEnabled(false);
                }
            }
            if (sImage3 != null && !"".equals(sImage3) && !sImage3.equalsIgnoreCase("null")) {
                if (orderState.equals("04")) {
                    if (mProgress!=null&&!mProgress.isShowing()){
                        mProgress.show();
                    }
                    downloadFile(FXConstant.URL_SIGN_FOUR + sImage3, sImage3, 3);
                } else {
                    LocalMedia media = new LocalMedia();
                    media.setType(type);
                    media.setCompressed(false);
                    media.setCut(false);
                    media.setIsChecked(true);
                    imagePaths1.add(FXConstant.URL_SIGN_FOUR + sImage3);
                    media.setPath(FXConstant.URL_SIGN_FOUR + sImage3);
                    media.setNum(imagePaths1.size());
                    selectMedia1.add(media);
                    recyclerView.setEnabled(false);
                    recyclerView2.setEnabled(false);
                }
            }
            if (sImage4 != null && !"".equals(sImage4) && !sImage4.equalsIgnoreCase("null")) {
                if (orderState.equals("04")) {
                    if (mProgress!=null&&!mProgress.isShowing()){
                        mProgress.show();
                    }
                    downloadFile(FXConstant.URL_SIGN_FOUR + sImage4, sImage4, 4);
                } else {
                    LocalMedia media = new LocalMedia();
                    media.setType(type);
                    media.setCompressed(false);
                    media.setCut(false);
                    media.setIsChecked(true);
                    imagePaths1.add(FXConstant.URL_SIGN_FOUR + sImage4);
                    media.setPath(FXConstant.URL_SIGN_FOUR + sImage4);
                    media.setNum(imagePaths1.size());
                    selectMedia1.add(media);
                    recyclerView.setEnabled(false);
                    recyclerView2.setEnabled(false);
                }
            }
            if (imagePaths1.size() > 0) {
                Log.e("uorderdetail,fp", "??????1");
                adapter.notifyDataSetChanged();
            }
            if (sImage5 != null && !"".equals(sImage5) && !sImage5.equalsIgnoreCase("null")) {
                if (orderState.equals("04")) {
                    if (mProgress!=null&&!mProgress.isShowing()){
                        mProgress.show();
                    }
                    downloadFile(FXConstant.URL_SIGN_FOUR + sImage5, sImage5, 5);
                } else {
                    LocalMedia media = new LocalMedia();
                    media.setType(type);
                    media.setCompressed(false);
                    media.setCut(false);
                    media.setIsChecked(true);
                    imagePaths2.add(FXConstant.URL_SIGN_FOUR + sImage5);
                    media.setPath(FXConstant.URL_SIGN_FOUR + sImage5);
                    media.setNum(imagePaths2.size());
                    selectMedia2.add(media);
                    recyclerView.setEnabled(false);
                    recyclerView2.setEnabled(false);
                }
            }
            if (sImage6 != null && !"".equals(sImage6) && !sImage6.equalsIgnoreCase("null")) {
                if (orderState.equals("04")) {
                    if (mProgress!=null&&!mProgress.isShowing()){
                        mProgress.show();
                    }
                    downloadFile(FXConstant.URL_SIGN_FOUR + sImage6, sImage6, 6);
                } else {
                    LocalMedia media = new LocalMedia();
                    media.setType(type);
                    media.setCompressed(false);
                    media.setCut(false);
                    media.setIsChecked(true);
                    imagePaths2.add(FXConstant.URL_SIGN_FOUR + sImage6);
                    media.setPath(FXConstant.URL_SIGN_FOUR + sImage6);
                    media.setNum(imagePaths2.size());
                    selectMedia2.add(media);
                    recyclerView.setEnabled(false);
                    recyclerView2.setEnabled(false);
                }
            }
            if (sImage7 != null && !"".equals(sImage7) && !sImage7.equalsIgnoreCase("null")) {
                if (orderState.equals("04")) {
                    if (mProgress!=null&&!mProgress.isShowing()){
                        mProgress.show();
                    }
                    downloadFile(FXConstant.URL_SIGN_FOUR + sImage7, sImage7, 7);
                } else {
                    LocalMedia media = new LocalMedia();
                    media.setType(type);
                    media.setCompressed(false);
                    media.setCut(false);
                    media.setIsChecked(true);
                    imagePaths2.add(FXConstant.URL_SIGN_FOUR + sImage7);
                    media.setPath(FXConstant.URL_SIGN_FOUR + sImage7);
                    media.setNum(imagePaths2.size());
                    selectMedia2.add(media);
                    recyclerView.setEnabled(false);
                    recyclerView2.setEnabled(false);
                }
            }
            if (sImage8 != null && !"".equals(sImage8) && !sImage8.equalsIgnoreCase("null")) {
                if (orderState.equals("04")) {
                    if (mProgress!=null&&!mProgress.isShowing()){
                        mProgress.show();
                    }
                    downloadFile(FXConstant.URL_SIGN_FOUR + sImage8, sImage8, 8);
                } else {
                    LocalMedia media = new LocalMedia();
                    media.setType(type);
                    media.setCompressed(false);
                    media.setCut(false);
                    media.setIsChecked(true);
                    imagePaths2.add(FXConstant.URL_SIGN_FOUR + sImage8);
                    media.setPath(FXConstant.URL_SIGN_FOUR + sImage8);
                    media.setNum(imagePaths2.size());
                    selectMedia2.add(media);
                    recyclerView.setEnabled(false);
                    recyclerView2.setEnabled(false);
                }
            }
            if (imagePaths2.size() > 0) {
                Log.e("uorderdetail,fp", "??????2");
                adapter2.notifyDataSetChanged();
            }
            isFirst = false;
        }
        if (send_id2!=null&&!"".equals(send_id2)&&!send_id2.equalsIgnoreCase("null")){
            iv_jiantou.setVisibility(View.VISIBLE);
            ll2_paidan.setVisibility(View.VISIBLE);
            ll2.setVisibility(View.VISIBLE);
            ll3_paidan.setVisibility(View.GONE);
            biaoshi = "02";
            if (time2==null||"".equals(time2)){
                tev_paidan_kehu.setVisibility(View.INVISIBLE);
                tv_paidan_kehu_time.setVisibility(View.INVISIBLE);
            }else {
                tev_paidan_kehu.setVisibility(View.VISIBLE);
                tv_paidan_kehu_time.setVisibility(View.VISIBLE);
                tv_paidan_kehu_time.setText(time2);
            }
            if (time3==null||"".equals(time3)){
                tev_paidan_caiwu.setVisibility(View.INVISIBLE);
                tv_paidan_caiwu_time.setVisibility(View.INVISIBLE);
            }else {
                tev_paidan_caiwu.setVisibility(View.VISIBLE);
                tv_paidan_caiwu_time.setVisibility(View.VISIBLE);
                tv_paidan_caiwu_time.setText(time3);
            }
            if (!image3.equals("")){
                tv_paidan_kehu.setText("???????????????");
            }
            if (!image1.equals("")){
                tv_paidan_kehu.setVisibility(View.INVISIBLE);
                ImageLoader.getInstance().displayImage(FXConstant.URL_PAIDANQIANZI + image1,iv_paidan_kehu);
            }
            if (!image4.equals("")){
                tv_paidan_caiwu.setText("???????????????");
            }
            if (!image2.equals("")){
                tv_paidan_caiwu.setVisibility(View.INVISIBLE);
                ImageLoader.getInstance().displayImage(FXConstant.URL_PAIDANQIANZI + image2,iv_paidan_caiwu);
            }
        }else if (send_id1!=null&&!"".equals(send_id1)&&!send_id1.equalsIgnoreCase("null")){
            iv_jiantou.setVisibility(View.VISIBLE);
            ll2_paidan.setVisibility(View.GONE);
            ll2.setVisibility(View.GONE);
            ll3_paidan.setVisibility(View.VISIBLE);
            biaoshi = "01";
            if (time2==null||"".equals(time2)){
                rl_paidan_id1.setVisibility(View.GONE);
                tev_paidan_id1.setVisibility(View.INVISIBLE);
            }else {
                rl_paidan_id1.setVisibility(View.VISIBLE);
                tev_paidan_id1.setVisibility(View.VISIBLE);
                tv_paidan_id1_time.setText(time2);
            }
            if (!image3.equals("")){
                tv_paidan_id1.setText("???????????????");
            }
            if (!image1.equals("")){
                tv_paidan_id1.setVisibility(View.INVISIBLE);
                ImageLoader.getInstance().displayImage(FXConstant.URL_PAIDANQIANZI + image1,iv_paidan_id1);
            }
        }else {
            iv_jiantou.setVisibility(View.GONE);
            ll2_paidan.setVisibility(View.GONE);
            ll2.setVisibility(View.GONE);
            ll3_paidan.setVisibility(View.GONE);
            biaoshi = "00";
        }
        String ticheng = "0%";
        if (resv5.length()>0){
            resv5Geren = resv5.split("\\|")[0];
            resv5Qiye = resv5.split("\\|")[1];
            if (resv5.split("\\|").length>2) {
                ticheng = resv5.split("\\|")[2];
            }
        }
        if ((mImage==null&&mSignature==null)||(mImage.equals("")&&mSignature.equals(""))||(mImage.equalsIgnoreCase("null")&&mSignature.equalsIgnoreCase("null"))){
            iv_jiantou.setImageResource(R.drawable.fuzejiantouone);
        }else if ((image1==null&&image3==null)||(image1.equals("")&&image3.equals(""))||(image1.equalsIgnoreCase("null")&&image3.equalsIgnoreCase("null"))){
            if (send_id2==null||"".equals(send_id2)||send_id2.equalsIgnoreCase(null)) {
                iv_jiantou.setImageResource(R.drawable.fuzejiantousix);
            }else {
                iv_jiantou.setImageResource(R.drawable.fuzejiantoutwo);
            }
        }else if ((image2==null&&image4==null)||(image2.equals("")&&image4.equals(""))||(image2.equalsIgnoreCase("null")&&image4.equalsIgnoreCase("null"))){
            if (send_id2==null||"".equals(send_id2)||send_id2.equalsIgnoreCase(null)) {
                iv_jiantou.setImageResource(R.drawable.fuzejiantoufour);
            }else {
                iv_jiantou.setImageResource(R.drawable.fuzejiantouthree);
            }
        }else if ((oImage==null&&oSignature==null)||(oImage.equals("")&&oSignature.equals(""))||(oImage.equalsIgnoreCase("null")&&oSignature.equals("null"))){
            iv_jiantou.setImageResource(R.drawable.fuzejiantoufour);
        }else {
            iv_jiantou.setImageResource(R.drawable.fuzejiantoufive);
        }
        if (oImage!=null&&resv4!=null&&!"".equals(oImage)&&!"".equals(resv4)){
            iv_jiantou.setImageResource(R.drawable.fuzejiantoufive);
        }
        tv_qiye_ticheng.setText(ticheng);
        if (time1==null||"".equals(time1)){
            tev_jiedan.setVisibility(View.INVISIBLE);
            tv_jiedan_time.setVisibility(View.INVISIBLE);
        }else {
            tev_jiedan.setVisibility(View.VISIBLE);
            tv_jiedan_time.setVisibility(View.VISIBLE);
        }
        if (resv4==null||"".equals(resv4)){
            tev_kehu.setVisibility(View.VISIBLE);
            tv_kehu_time.setVisibility(View.VISIBLE);
            tv_kehu_time.setText(orderTime);
        }else {
            tev_kehu.setVisibility(View.VISIBLE);
            tev_kehu.setText("??????:");
            tv_kehu_time.setVisibility(View.VISIBLE);
            tv_kehu_time.setText(resv4);
        }
        tv_jiedan_time.setText(time1);
        if (!oSigntrue.equals("")){
            tv_kehu.setText("???????????????");
        }
        if (!mSigntrue.equals("")){
            tv_jiedan.setText("???????????????");
        }
        if (!"".equals(resv2)){
            String[] res = resv2.split(",");
            isRun = false;
            countDown.setVisibility(View.GONE);
            tv_all.setVisibility(View.VISIBLE);
            tv_all.setText(res[1]);
            if (res[0].equals("0")){
                tv2.setText("??????????????????");
            }else if (res[0].equals("1")){
                tv2.setText("??????????????????");
            }
        }else {
            if (!"".equals(yueding)) {
                String time1 = getNowTime();
                long year = Long.parseLong(yueding.substring(0, 4));
                long year1 = Long.parseLong(time1.substring(0, 4));
                long mou = Long.parseLong(yueding.substring(4, 6));
                long mou1 = Long.parseLong(time1.substring(4, 6));
                long day = Long.parseLong(yueding.substring(6, 8));
                long day1 = Long.parseLong(time1.substring(6, 8));
                long hour = Long.parseLong(yueding.substring(8, 10));
                long hour1 = Long.parseLong(time1.substring(8, 10));
                long min = Long.parseLong(yueding.substring(10, 12));
                long min1 = Long.parseLong(time1.substring(10, 12));
                mSecond = Long.parseLong(time1.substring(12, 14));
                long minAll = (year - year1) * 365 * 24 * 60 + (mou - mou1) * 30 * 24 * 60 + (day - day1) * 24 * 60 + (hour - hour1) * 60 + (min - min1);
                if (minAll <= 0) {
                    isJian = false;
                    minAll = -minAll;
                    tv2.setText("??????????????????");
                } else {
                    isJian = true;
                    tv2.setText("??????????????????");
                }
                mDay = minAll / 60 /24;
                if (mDay==0) {
                    daysTv.setVisibility(View.GONE);
                    mHour = minAll / 60;
                    mMin = minAll - mHour * 60;
                }else {
                    daysTv.setVisibility(View.VISIBLE);
                    mHour = (minAll - mDay*60*24)/60;
                    mMin = minAll - mDay*60*24 - mHour*60;
                }
                if (mHour < 10) {
                    hours = "0" + mHour;
                } else {
                    hours = String.valueOf(mHour);
                }
                if (mMin < 10) {
                    mins = "0" + mMin;
                } else {
                    mins = String.valueOf(mMin);
                }
                if (mSecond < 10) {
                    second = "0" + mSecond;
                } else {
                    second = String.valueOf(mSecond);
                }
                daysTv.setText(mDay+"???");
                hoursTv.setText(hours);
                minutesTv.setText(mins);
                secondsTv.setText(second);
                startRun();
            } else {
                isRun = false;
                countDown.setVisibility(View.GONE);
                tv_all.setVisibility(View.VISIBLE);
                tv_all.setText("?????????");
            }
        }
        if (!oImage.equals("")){
            tv_kehu.setVisibility(View.INVISIBLE);
            String[] avatar = oImage.split("\\|");
            oImage = avatar[0];
            ImageLoader.getInstance().displayImage(FXConstant.URL_SIGN + oImage,iv_kehu);
        }
        if (!mImage.equals("")){
            tv_jiedan.setVisibility(View.INVISIBLE);
            String[] avatar = mImage.split("\\|");
            mImage = avatar[0];
            ImageLoader.getInstance().displayImage(FXConstant.URL_SIGN + mImage,iv_jiedan);
        }
        String name = null,phone= null,dizhi= null;
        String [] str = null;
        if (orderSum!=null&&!"".equals(orderSum)) {
            double jine = Double.parseDouble(orderSum);
            final String strj = String.format("%.2f", jine);
            et_feiyong.setText(strj);
        }else {
            et_feiyong.setText("0.00");
        }
        et_miaoshu.setText(orderProject);
//        et_dingdan_bianhao.setText(orderAmt);
        et_feiyong.setEnabled(false);
        et_miaoshu.setTextColor(Color.BLACK);
        et_dizhi.setTextColor(Color.BLACK);
        et_name.setTextColor(Color.BLACK);
        et_phone.setEnabled(false);
        if (orderNumber!=null&&!"".equals(orderNumber)){
            str = orderNumber.split("\\|");
        }
        if (str!=null&&str.length>0){
            name = str[0];
        }
        if (str!=null&&str.length>1){
            phone = str[1];
        }
        if (str!=null&&str.length>2){
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
        if (beizhu.equals("")&&mImage.equals("")){
            rlM_beizhu.setVisibility(View.VISIBLE);
        }else {
            rlM_beizhu.setVisibility(View.VISIBLE);
            et_Ubeizhu.setText(beizhu);
            et_Ubeizhu.setTextColor(Color.BLACK);
        }
        et_Ubeizhu.setText(beizhu);
        if ("01".equals(biaoshi2)){
            et_miaoshu.setEnabled(false);
            et_phone.setEnabled(false);
            et_dizhi.setEnabled(false);
            et_name.setEnabled(false);
            et_feiyong.setEnabled(false);
            recyclerView.setEnabled(false);
            recyclerView2.setEnabled(false);
            rlM_beizhu.setVisibility(View.GONE);
            et_Ubeizhu.setVisibility(View.GONE);
            btn_cansul.setVisibility(View.GONE);
            ll_btn.setVisibility(View.VISIBLE);
            btn_comit.setVisibility(View.VISIBLE);
            if ("05".equals(orderState)||"07".equals(orderState)){
                btn_comit.setText("????????????");
                btn_comit.setEnabled(false);
                btn_comit.setBackgroundResource(R.drawable.fx_bg_btn_gray);
            }else {
                final String myUserId = DemoHelper.getInstance().getCurrentUsernName();
                if (myUserId.equals(send_id1)){
                    if (!"".equals(image1)){
                        btn_comit.setText("??? ??? ???");
                        btn_comit.setEnabled(false);
                        btn_comit.setBackgroundResource(R.drawable.fx_bg_btn_gray);
                    }else {
                        btn_comit.setText("???    ???");
                        btn_comit.setEnabled(true);
                        btn_comit.setBackgroundResource(R.drawable.fx_bg_btn_green);
                        btn_comit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(UTOrderDetailFourActivity.this, QianMingActivity.class);
                                intent.putExtra("orderId", orderId);
                                intent.putExtra("biaoshi", "1201");
                                startActivityForResult(intent,0);
                            }
                        });
                    }
                }
                if (myUserId.equals(send_id2)){
                    if (!"".equals(image2)){
                        btn_comit.setText("??? ??? ???");
                        btn_comit.setEnabled(false);
                        btn_comit.setBackgroundResource(R.drawable.fx_bg_btn_gray);
                    }else {
                        btn_comit.setText("???    ???");
                        btn_comit.setEnabled(true);
                        btn_comit.setBackgroundResource(R.drawable.fx_bg_btn_green);
                        btn_comit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(UTOrderDetailFourActivity.this, QianMingActivity.class);
                                intent.putExtra("orderId", orderId);
                                intent.putExtra("balance", orderSum);
                                intent.putExtra("biaoshi", "1101");
                                startActivityForResult(intent,0);
                            }
                        });
                    }
                }
            }
        }
    }
    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(date);
    }

    @Override
    public void updateCurrentPrice(Object success) {
        yuE = DemoApplication.getInstance().getCurrenPrice();
        pass = DemoApplication.getInstance().getCurrentPayPass();
        com.alibaba.fastjson.JSONObject object = JSON.parseObject(String.valueOf(success));
        errorTime = object.getInteger("enterErrorTimes");
    }
}

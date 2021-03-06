package com.sangu.apptongji.main.alluser.maplocation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sangu.apptongji.DemoApplication;
import com.sangu.apptongji.DemoHelper;
import com.sangu.apptongji.R;
import com.sangu.apptongji.main.FXConstant;
import com.sangu.apptongji.main.activity.UserDetailsActivity;
import com.sangu.apptongji.main.alluser.entity.Userful;
import com.sangu.apptongji.main.qiye.QingjiaActivity;
import com.sangu.apptongji.main.qiye.entity.MemberInfo;
import com.sangu.apptongji.main.qiye.entity.QiYeInfo;
import com.sangu.apptongji.main.qiye.presenter.IMemberPresenter;
import com.sangu.apptongji.main.qiye.presenter.IQiYeInfoPresenter;
import com.sangu.apptongji.main.qiye.presenter.impl.MemberPresenter;
import com.sangu.apptongji.main.qiye.presenter.impl.QiYeInfoPresenter;
import com.sangu.apptongji.main.qiye.view.IMemberView;
import com.sangu.apptongji.main.qiye.view.IQiYeDetailView;
import com.sangu.apptongji.main.utils.JSONParser;
import com.sangu.apptongji.main.utils.JSONUtil;
import com.sangu.apptongji.main.utils.MySingleton;
import com.sangu.apptongji.main.utils.ScreenshotUtil;
import com.sangu.apptongji.main.utils.ToastUtils;
import com.sangu.apptongji.ui.BaseActivity;
import com.sangu.apptongji.utils.WeiboDialogUtils;
import com.sangu.apptongji.widget.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by Administrator on 2016-10-13.
 */

public class BaiDuQiyeLocationActivity extends BaseActivity implements OnMapClickListener,IQiYeDetailView,IMemberView{
    private MapView mMapView = null;
    private TextView tvTitle = null;
    LocationClient mLocClient = null;
    LocationClientOption option = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    boolean isFirstLoc = true; // ??????????????????
    private BaiduMap mBaiduMap = null;
    private InfoWindow mInfoWindow = null;
    private LinearLayout ll_search = null;
    private double mLat1, mLon1;
    private IMemberPresenter memberPresenter=null;
    String strId=null;
    String currentId = DemoHelper.getInstance().getCurrentUsernName();
    String signState = "";
    String signTime = "";
    String nowTime = getcurrentTime().substring(0,8);
    private Dialog mWeiboDialog;
    private String shBzhutai = null;
    List<String> strLat = new ArrayList<>(), strLong = new ArrayList<>(), strLoginId = new ArrayList<>(), strName = new ArrayList<>(), strSex = new ArrayList<>();
    String signRemark = null;
    private TextView tvmaj1 = null, tvmaj2 = null, tvmaj3 = null, tvmaj4 = null, tv_titl = null, tv_zy1_bao = null, tv_zy2_bao = null, tv_zy3_bao = null,
            tv_zy4_bao = null, tvName = null, tvsign = null, tvAge = null, tv_bottom = null, tv_distance = null, iv_zy1_tupian = null,
            iv_zy2_tupian = null, iv_zy3_tupian = null, iv_zy4_tupian = null, tv_title;
    private TextView tvmaj13 = null, tvmaj23 = null, tvmaj33 = null, tvmaj43 = null, tv_titl3 = null, tv_zy1_bao3 = null, tv_zy2_bao3 = null, tv_zy3_bao3 = null,
            tv_zy4_bao3 = null, tvName3 = null, tvsign3 = null, tvAge3 = null, tv_bottom3 = null, tv_distance3 = null, iv_zy1_tupian3 = null,
            iv_zy2_tupian3 = null, iv_zy3_tupian3 = null, iv_zy4_tupian3 = null, tv_company_count3, tv_company3;
    private Button btnXq = null, btnDh = null;
    private Button btnXq3 = null, btnDh3 = null;
    private CircleImageView ivAvatar = null,ivAvatar3;
    private ImageView ivSex3 = null,ivSex;
    private IQiYeInfoPresenter qiYeInfoPresenter = null;
    View vi2 = null, vi3 = null;
    List<Marker> oa = new ArrayList<>();
    Marker oaQiye;
    private boolean isWifiConnected = false;
    private boolean isFinished = false;
    private boolean isLFinished = false;
    private boolean isdistance = false;
    private boolean isFirst = true;
    private LocationMode mCurrentMode;
    private boolean isFenxiang = false;

    private QiYeInfo companyInfo;
    private MemberInfo memberInfo;
    private String comClockInfo;

    private String shangbanTime, xiabanTime, qiyeLat = "", qiyeLon = "", fenxiangPic, managerId;
    private Handler myMaphandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

                    com.alibaba.fastjson.JSONObject object = JSON.parseObject(comClockInfo);

                    String latitude = object.getString("latitude");
                    String longitude = object.getString("longitude");

                    //qiyeLat == null || qiyeLon == null
                    if (latitude == null || longitude == null || "".equals(latitude) || "".equals(longitude)) {
                        Toast.makeText(BaiDuQiyeLocationActivity.this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double lat = Double.parseDouble(latitude);
                    double lon = Double.parseDouble(longitude);
                    final LatLng ll = JSONUtil.convertGPSToBaidu(new LatLng(lat, lon));
                    final MarkerOptions mop = new MarkerOptions()
                            .position(ll)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.icon_geo))
                            .zIndex(9)
                            .draggable(false);
                    oaQiye = (Marker) mBaiduMap.addOverlay(mop);
                    double distance = DistanceUtil.getDistance(ll, new LatLng(mLat1, mLon1));
                    if (distance <= 1000) {
                        String str = String.format("%.2f", distance);
                        tv_distance.setText(str + "m");
                    } else if (distance<10000000){
                        String str = String.format("%.2f", distance / 1000);
                        if (str!=null&&Double.parseDouble(str)/1000>=10000){
                            tv_distance.setText("??????");
                        }else {
                            tv_distance.setText(str + "km");
                        }
                    }else {
                        tv_distance.setText("??????");
                    }
                    if (isFinished && isFirst) {
                        isFirst = false;
                        MapStatus mMapStatus = new MapStatus.Builder()
                                .target(ll)
                                .zoom(15.0f)
                                .build();
                        //??????MapStatusUpdate??????????????????????????????????????????????????????
                        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                        //??????????????????
                        mBaiduMap.setMapStatus(mMapStatusUpdate);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mInfoWindow = new InfoWindow(vi2, ll, -47);
                                            mBaiduMap.showInfoWindow(mInfoWindow);
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    break;
            }
        }
    };

    @Override
    public void onMapClick(LatLng latLng) {
        if (mInfoWindow != null) {
            mBaiduMap.hideInfoWindow();
        }
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_baidumap);
        qiYeInfoPresenter = new QiYeInfoPresenter(this,this);
        memberPresenter = new MemberPresenter(this,this);
        SharedPreferences sp2 = getSharedPreferences("sangu_denglu_info", Context.MODE_PRIVATE);
        String qiyeId = sp2.getString("qiyeId","");
        mCurrentMode = LocationMode.NORMAL;
        RequestQueue queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        ll_search = (LinearLayout) findViewById(R.id.ll_search);
        tv_bottom = (TextView) findViewById(R.id.tv_bottom);
        tv_title = (TextView) findViewById(R.id.tv_title);
        vi2 = LayoutInflater.from(this).inflate(R.layout.item_find_fragments1, null);
        vi3 = LayoutInflater.from(this).inflate(R.layout.item_find_fragments, null);
        tv_title.setText("????????????");
        mMapView = (MapView) findViewById(R.id.bmapView);
        mCurrentMode = LocationMode.NORMAL;
        mMapView.removeViewAt(1);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setOnMapClickListener(this);
        initMapView();
        initV2();
        initV3();
        qiYeInfoPresenter.loadQiYeInfo(qiyeId);
        memberPresenter.loadMemberList(0);
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        option = new LocationClientOption();
        option.setOpenGps(true); // ??????gps
        option.setCoorType("bd09ll"); // ??????????????????
        option.setScanSpan(2000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        tv_bottom.setText("??????????????????????????????");
        ll_search.setVisibility(View.GONE);
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                for (int i = 0; i < oa.size(); i++) {
                    if (marker.equals(oaQiye)) {
                        if (qiyeLat == null || qiyeLon == null || "".equals(qiyeLat) || "".equals(qiyeLon)) {
                            Toast.makeText(BaiDuQiyeLocationActivity.this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
                        } else {
                            double lat = Double.parseDouble(qiyeLat);
                            double lon = Double.parseDouble(qiyeLon);
                            final LatLng ll = JSONUtil.convertGPSToBaidu(new LatLng(lat, lon));
                            MapStatus mMapStatus = new MapStatus.Builder()
                                    .target(ll)
                                    .build();
                            //??????MapStatusUpdate??????????????????????????????????????????????????????
                            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                            //??????????????????
                            mBaiduMap.setMapStatus(mMapStatusUpdate);
                            mInfoWindow = new InfoWindow(vi2, ll, -47);
                            mBaiduMap.showInfoWindow(mInfoWindow);
                        }
                    } else if (marker.equals(oa.get(i))) {
                        String id = strLoginId.get(i);
                        String url = FXConstant.URL_Get_UserInfo + id;
                        final int finalI = i;
                        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                marker.setToTop();
                                LatLng ll1 = marker.getPosition();
                                double lat = ll1.latitude + 0.0001;
                                double lng = ll1.longitude;
                                LatLng ll2 = new LatLng(lat, lng);
                                MapStatus mMapStatus = new MapStatus.Builder()
                                        .target(ll2)
                                        .build();
                                //??????MapStatusUpdate??????????????????????????????????????????????????????
                                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                                //??????????????????
                                mBaiduMap.setMapStatus(mMapStatusUpdate);
                                mInfoWindow = new InfoWindow(vi3, ll2, -100);
                                mBaiduMap.showInfoWindow(mInfoWindow);
                                setView(s, strLat.get(finalI), strLong.get(finalI));
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                //callback.onError("??????????????????");
                            }
                        });
                        MySingleton.getInstance(BaiDuQiyeLocationActivity.this).addToRequestQueue(request);
                    }
                }
                return true;
            }
        });
    }


    private void GetCompanyListInfo(){


        String url = FXConstant.URL_QUERY_QIYEMAJAR;

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("??????????????????", volleyError + "");
                Toast.makeText(BaiDuQiyeLocationActivity.this, "??????????????????...", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();


                return params;
            }
        };
        MySingleton.getInstance(BaiDuQiyeLocationActivity.this).addToRequestQueue(request);


    }


    private void setView(String s, final String strLat, final String strLon) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONObject object = jsonObject.getJSONObject("userInfo");
            Userful user2 = JSONParser.parseUser(object);
            final String id = user2.getLoginId();
            String imageStr = TextUtils.isEmpty(user2.getImage()) ? "" : user2.getImage();
            String name = TextUtils.isEmpty(user2.getName()) ? user2.getLoginId() : user2.getName();
            String sex = TextUtils.isEmpty(user2.getSex()) ? "" : user2.getSex();
            String ZY1 = TextUtils.isEmpty(user2.getUpName1()) ? "" : user2.getUpName1();
            String ZY2 = TextUtils.isEmpty(user2.getUpName2()) ? "" : user2.getUpName2();
            String ZY3 = TextUtils.isEmpty(user2.getUpName3()) ? "" : user2.getUpName3();
            String ZY4 = TextUtils.isEmpty(user2.getUpName4()) ? "" : user2.getUpName4();
            String sign = TextUtils.isEmpty(user2.getSignaTure()) ? "" : user2.getSignaTure();
            String age = TextUtils.isEmpty(user2.getuAge()) ? "27" : user2.getuAge();
            String image1 = TextUtils.isEmpty(user2.getZyImage1()) ? "" : user2.getZyImage1();
            String image2 = TextUtils.isEmpty(user2.getZyImage2()) ? "" : user2.getZyImage2();
            String image3 = TextUtils.isEmpty(user2.getZyImage3()) ? "" : user2.getZyImage3();
            String image4 = TextUtils.isEmpty(user2.getZyImage4()) ? "" : user2.getZyImage4();
            String margan1 = TextUtils.isEmpty(user2.getMargin1()) ? "" : user2.getMargin1();
            String margan2 = TextUtils.isEmpty(user2.getMargin2()) ? "" : user2.getMargin2();
            String margan3 = TextUtils.isEmpty(user2.getMargin3()) ? "" : user2.getMargin3();
            String margan4 = TextUtils.isEmpty(user2.getMargin3()) ? "" : user2.getMargin3();
            if (!"".equals(image1) && image1 != null) {
                iv_zy1_tupian3.setVisibility(View.VISIBLE);
            } else {
                iv_zy1_tupian3.setVisibility(View.INVISIBLE);
            }
            if (margan1 != null) {
                if (!"".equals(margan1) && Double.valueOf(margan1) > 0) {
                    tv_zy1_bao3.setVisibility(View.VISIBLE);
                } else {
                    iv_zy1_tupian3.setVisibility(View.INVISIBLE);
                }
            }
            if (!"".equals(image2) && image2 != null) {
                iv_zy2_tupian3.setVisibility(View.VISIBLE);
            } else {
                iv_zy2_tupian3.setVisibility(View.INVISIBLE);
            }
            if (margan2 != null) {
                if (!"".equals(margan2) && Double.valueOf(margan2) > 0) {
                    tv_zy2_bao3.setVisibility(View.VISIBLE);
                } else {
                    tv_zy2_bao3.setVisibility(View.INVISIBLE);
                }
            }
            if (!"".equals(image3) && image3 != null) {
                iv_zy3_tupian3.setVisibility(View.VISIBLE);
            } else {
                iv_zy3_tupian3.setVisibility(View.INVISIBLE);
            }
            if (margan3 != null) {
                if (!"".equals(margan3) && Double.valueOf(margan3) > 0) {
                    tv_zy3_bao3.setVisibility(View.VISIBLE);
                } else {
                    tv_zy3_bao3.setVisibility(View.INVISIBLE);
                }
            }
            if (!"".equals(image4) && image4 != null) {
                iv_zy4_tupian3.setVisibility(View.VISIBLE);
            } else {
                iv_zy4_tupian3.setVisibility(View.INVISIBLE);
            }
            if (margan4 != null) {
                if (!"".equals(margan4) && Double.valueOf(margan4) > 0) {
                    tv_zy4_bao3.setVisibility(View.VISIBLE);
                } else {
                    tv_zy4_bao3.setVisibility(View.INVISIBLE);
                }
            }
            if (("00").equals(sex)) {
                ivSex3.setImageResource(R.drawable.nv);
                tvAge3.setBackgroundColor(Color.rgb(234, 121, 219));
                tv_titl3.setBackgroundResource(R.drawable.fx_bg_text_red);
            } else {
                ivSex3.setImageResource(R.drawable.nan);
                tvAge3.setBackgroundColor(Color.rgb(0, 172, 255));
                tv_titl3.setBackgroundResource(R.drawable.fx_bg_text_gra);
            }
            if (!imageStr.equals("")) {
                String[] images = imageStr.split("\\|");
                ivAvatar3.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(FXConstant.URL_AVATAR + images[0], ivAvatar3, DemoApplication.mOptions);
            } else {
                ivAvatar3.setVisibility(View.INVISIBLE);
                tv_titl3.setVisibility(View.VISIBLE);
                tv_titl3.setText(name);
            }
            String company = TextUtils.isEmpty(user2.getCompany()) ? "??????????????????" : user2.getCompany();
            String uNation = user2.getuNation();
            String resv5 = user2.getResv5();
            String resv6 = user2.getResv6();
            if (company == null || company.equals("")) {
                company = "??????????????????";
            }
            if ("00".equals(resv6)&&!"1".equals(uNation)){
                company = "??????????????????";
            }
            if (resv5==null||"".equals(resv5)){
                company = "??????????????????";
            }
            try {
                company = URLDecoder.decode(company, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String member = user2.getMenberNum();
            if (member == null || "".equals(member)) {
                member = "0";
            }
            if (!company.equals("??????????????????")) {
                tv_company_count3.setVisibility(View.VISIBLE);
            }
            if (strLat != null && String.valueOf(mLat1) != null) {
                if (!("".equals(strLat) || "".equals(strLon) || mLat1 == 0.0)) {
                    double latitude1 = Double.valueOf(strLat);
                    double longitude1 = Double.valueOf(strLon);
                    final LatLng ll1 = new LatLng(mLat1, mLon1);
                    LatLng ll = new LatLng(latitude1, longitude1);
                    double distance = DistanceUtil.getDistance(ll, ll1);
                    double dou = distance / 1000;
                    if (dou<10000) {
                        String str = String.format("%.2f", dou);//format ?????????????????????
                        if (str!=null&&dou>=10000){
                            tv_distance.setText("??????");
                        }else {
                            tv_distance.setText(str + "km");
                        }
                    }else {
                        tv_distance3.setText("??????");
                    }
                } else {
                    tv_distance3.setText("3km??????");
                }
            } else {
                tv_distance3.setText("3km??????");
            }
            tv_company_count3.setText("(" + member + ")");
            tv_company3.setText(company);
            tvAge3.setText(age);
            tvmaj13.setText(ZY1);
            tvmaj23.setText(ZY2);
            tvmaj33.setText(ZY3);
            tvmaj43.setText(ZY4);
            tvName3.setText(name);
            tvsign3.setText(sign);
            String shareRed = user2.getShareRed();
            if (shareRed != null && !"".equals(shareRed) && !shareRed.equalsIgnoreCase("null") && Double.parseDouble(shareRed) > 0) {
                tvName3.setTextColor(Color.RED);
            } else {
                tvName3.setTextColor(Color.BLACK);
            }
            btnDh3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startNavi(Double.valueOf(strLat), Double.valueOf(strLon));
                    //finish();
                }
            });
            btnXq3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BaiDuQiyeLocationActivity.this, UserDetailsActivity.class);
                    intent.putExtra(FXConstant.JSON_KEY_HXID, id);
                    startActivity(intent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startNavi(double lat, double Lng) {
        LatLng pt1 = new LatLng(mLat1, mLon1);
        LatLng pt2 = new LatLng(lat, Lng);
        // ?????? ????????????
        RouteParaOption para = new RouteParaOption()
                .startPoint(pt1).endPoint(pt2);
        try {
            BaiduMapRoutePlan.openBaiduMapDrivingRoute(para, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateQiyeInfo(QiYeInfo user2){

        companyInfo = user2;

        String companyShortName = TextUtils.isEmpty(user2.getCompanyShortName()) ? "0" : user2.getCompanyShortName();
        if ("1".equals(companyShortName)) {
            isFenxiang = true;
            fenxiangPic = user2.getResv6();
        }
        qiyeLat = user2.getComLatitude();
        qiyeLon = user2.getComLongitude();
        String loginTime = TextUtils.isEmpty(user2.getLoginTime()) ? null : user2.getLoginTime();
        Log.d("chen", "loginTime" + loginTime);
        if (loginTime!=null&&!"".equals(loginTime)&&loginTime.length() > 0) {
            shangbanTime = loginTime.split("\\|")[0];
            xiabanTime = loginTime.split("\\|")[1];
        }
        final String name = TextUtils.isEmpty(user2.getCompanyName()) ? "" : user2.getCompanyName();
        String imageStr = TextUtils.isEmpty(user2.getComImage()) ? "" : user2.getComImage();
        String ZY1 = TextUtils.isEmpty(user2.getUpName1()) ? "" : user2.getUpName1();
        String ZY2 = TextUtils.isEmpty(user2.getUpName2()) ? "" : user2.getUpName2();
        String ZY3 = TextUtils.isEmpty(user2.getUpName3()) ? "" : user2.getUpName3();
        String ZY4 = TextUtils.isEmpty(user2.getUpName4()) ? "" : user2.getUpName4();
        String sign = TextUtils.isEmpty(user2.getComSignature()) ? "" : user2.getComSignature();
        String image1 = TextUtils.isEmpty(user2.getZyImage1()) ? "" : user2.getZyImage1();
        String image2 = TextUtils.isEmpty(user2.getZyImage2()) ? "" : user2.getZyImage2();
        String image3 = TextUtils.isEmpty(user2.getZyImage3()) ? "" : user2.getZyImage3();
        String image4 = TextUtils.isEmpty(user2.getZyImage4()) ? "" : user2.getZyImage4();
        String margan1 = TextUtils.isEmpty(user2.getMargin1()) ? "" : user2.getMargin1();
        String margan2 = TextUtils.isEmpty(user2.getMargin2()) ? "" : user2.getMargin2();
        String margan3 = TextUtils.isEmpty(user2.getMargin3()) ? "" : user2.getMargin3();
        String margan4 = TextUtils.isEmpty(user2.getMargin3()) ? "" : user2.getMargin3();
        String name1 = null;
        try {
            name1 = URLDecoder.decode(name, "UTF-8");
            sign = URLDecoder.decode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (!"".equals(image1) && image1 != null) {
            iv_zy1_tupian.setVisibility(View.VISIBLE);
        }
        if (margan1 != null) {
            if (!"".equals(margan1) && Double.valueOf(margan1) > 0) {
                tv_zy1_bao.setVisibility(View.VISIBLE);
            }
        }
        if (!"".equals(image2) && image2 != null) {
            iv_zy2_tupian.setVisibility(View.VISIBLE);
        }
        if (margan2 != null) {
            if (!"".equals(margan2) && Double.valueOf(margan2) > 0) {
                tv_zy2_bao.setVisibility(View.VISIBLE);
            }
        }
        if (!"".equals(image3) && image3 != null) {
            iv_zy3_tupian.setVisibility(View.VISIBLE);
        }
        if (margan3 != null) {
            if (!"".equals(margan3) && Double.valueOf(margan3) > 0) {
                tv_zy3_bao.setVisibility(View.VISIBLE);
            }
        }
        if (!"".equals(image4) && image4 != null) {
            iv_zy4_tupian.setVisibility(View.VISIBLE);
        }
        if (margan4 != null) {
            if (!"".equals(margan4) && Double.valueOf(margan4) > 0) {
                tv_zy4_bao.setVisibility(View.VISIBLE);
            }
        }
        if (!imageStr.equals("")) {
            String[] images = imageStr.split("\\|");
            ivAvatar.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(FXConstant.URL_QIYE_TOUXIANG + images[0], ivAvatar, DemoApplication.mOptions);
        } else {
            ivAvatar.setVisibility(View.INVISIBLE);
            tv_titl.setVisibility(View.VISIBLE);
            tv_titl.setText(name1);
        }
        tvmaj1.setText(ZY1);
        tvmaj2.setText(ZY2);
        tvmaj3.setText(ZY3);
        tvmaj4.setText(ZY4);
        tvName.setText(name1);
        tvsign.setText(sign);
        String shareRed = user2.getShareRed();
        final String friendsNumber = user2.getFriendsNumber();
        String onceJine = null;
        if (friendsNumber != null && !"".equals(friendsNumber)) {
            onceJine = friendsNumber.split("\\|")[0];
        }
        if (shareRed != null && !"".equals(shareRed) && Double.parseDouble(shareRed) > 0 && onceJine != null && !"".equals(onceJine) && Double.parseDouble(onceJine) > 0) {
            tvName.setTextColor(Color.RED);
        } else {
            tvName.setTextColor(Color.BLACK);
        }
        btnXq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BaiDuQiyeLocationActivity.this, QingjiaActivity.class).putExtra("name", name)
                        .putExtra("biaoshi", "").putExtra("managerId", managerId));
            }
        });
        isFinished = true;
    }

    private void showdshShbTishi() {
        String remark = "04";
        qiandao(remark);
    }

    private void showdwShbTishi(final String str) {
        final String remark = jisuanRemark();
        if (remark.equals("05")) {
            LayoutInflater inflater2 = LayoutInflater.from(BaiDuQiyeLocationActivity.this);
            RelativeLayout layout2 = (RelativeLayout) inflater2.inflate(R.layout.dialog_alert, null);
            final Dialog dialog2 = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this).create();
            dialog2.show();
            dialog2.getWindow().setContentView(layout2);
            dialog2.setCanceledOnTouchOutside(true);
            dialog2.setCancelable(true);
            TextView title_tv2 = (TextView) layout2.findViewById(R.id.title_tv);
            Button btnCancel2 = (Button) layout2.findViewById(R.id.btn_cancel);
            final Button btnOK2 = (Button) layout2.findViewById(R.id.btn_ok);
            btnOK2.setText("??????");
            btnCancel2.setText("??????");
            title_tv2.setText("????????????????????????????????????,??????????????????????");
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
                    qiandao("05");
                }
            });
        } else {
            if (!isWifiConnected(getApplicationContext()) && str.equals("??????")) {
                Toast.makeText(BaiDuQiyeLocationActivity.this, "??????wifi??????????????????????????????", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if ("02".equals(remark)) {
                    LayoutInflater inflater2 = LayoutInflater.from(BaiDuQiyeLocationActivity.this);
                    RelativeLayout layout2 = (RelativeLayout) inflater2.inflate(R.layout.dialog_alert, null);
                    final Dialog dialog2 = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this).create();
                    dialog2.show();
                    dialog2.getWindow().setContentView(layout2);
                    dialog2.setCanceledOnTouchOutside(true);
                    dialog2.setCancelable(true);
                    TextView title_tv2 = (TextView) layout2.findViewById(R.id.title_tv);
                    Button btnCancel2 = (Button) layout2.findViewById(R.id.btn_cancel);
                    final Button btnOK2 = (Button) layout2.findViewById(R.id.btn_ok);
                    btnOK2.setText("??????");
                    btnCancel2.setText("??????");
                    if (isdistance) {
                        title_tv2.setText("??????????????????????????????????????????,??????????????????????????????????????????????");
                    } else {
                        title_tv2.setText("?????????????????????,????????????????????????????????????????????????????");
                    }
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
                            qiandao(remark);
                        }
                    });
                } else {
                    qiandao(remark);
                }
            }
        }
    }

    private void showXbTishi() {
        final String remark = jisuanRemark();
        if (remark.equals("05")) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this);
            builder.setMessage("????????????????????????????????????,??????????????????????");
            builder.setTitle("????????????");
            builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    qiandao("05");
                    dialog.dismiss();
                }
            });
            builder.show();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this);
            builder.setMessage("??????????????????????");
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
                    qiandao(remark);
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    private void showTishi(final String zhuangtai) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this);
        builder.setMessage("??????????????????????????????????????????,????????????????????????");
        builder.setTitle("????????????");
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
                fenxiang(zhuangtai);
            }
        });
        builder.show();
    }


    //??????????????????????????????????????????

    private void NewsSignJudge(){

        if (companyInfo.getSignShareType().equals("00")){

            //?????????00 ?????????????????????????????????

            //??????????????????????????????????????????

            if (companyInfo.getSignShareNeed().equals("01")){

                //????????????


            }else {

                //???????????????
                final LayoutInflater inflater1 = LayoutInflater.from(BaiDuQiyeLocationActivity.this);
                RelativeLayout layout1 = (RelativeLayout) inflater1.inflate(R.layout.dialog_alert, null);
                final Dialog collectionDialog = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this, R.style.Dialog).create();
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

                title_tv1.setText("???????????????????????????");

                btnCancel1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        collectionDialog.dismiss();

                        //????????? ??????????????????
                        //?????????????????????????????????
                        NewSignByTimeAndLocation();

                    }
                });

                btnOK1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        collectionDialog.dismiss();

                        //??????


                    }
                });

            }

        }else {

            //??????00 ?????????????????????????????????
            //????????????????????????
            final LayoutInflater inflater1 = LayoutInflater.from(BaiDuQiyeLocationActivity.this);
            RelativeLayout layout1 = (RelativeLayout) inflater1.inflate(R.layout.dialog_alert, null);
            final Dialog collectionDialog = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this, R.style.Dialog).create();
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

            title_tv1.setText("???????????????????????????");

            btnCancel1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    collectionDialog.dismiss();
                }
            });

            btnOK1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    collectionDialog.dismiss();

                    //?????????????????????????????????
                    NewSignByTimeAndLocation();

                }
            });

        }

    }

    //??????????????????????????????
    private void NewSignByTimeAndLocation(){

        //??????????????????????????????????????????????????????


        //??????????????????????????????????????????
        com.alibaba.fastjson.JSONObject object = JSON.parseObject(comClockInfo);

        String comLatitude = object.getString("latitude");//??????
        String comLongitude = object.getString("longitude");//??????

        final LatLng ll = JSONUtil.convertGPSToBaidu(new LatLng(Double.parseDouble(comLatitude), Double.parseDouble(comLongitude)));

        String setSignaTime = object.getString("setSignaTime");//?????????????????????

        //??????????????????
        String mornTime = object.getString("mornTime");//????????????
        String noonTime = object.getString("noonTime");//????????????
        String afternoonTime = object.getString("afternoonTime");//????????????
        String nightTime = object.getString("nightTime");//????????????

        //?????????|?????????|?????????|?????????
        String[] signTimes = setSignaTime.split("\\|");

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String current =  dateFormat.format(date);
        String currentTime1 = dateFormat.format(date).substring(8,10);
        String currentTime2 = dateFormat.format(date).substring(10,12);

        String[] fristTimes = signTimes[0].split("\\:");//????????????
        String[] secondTimes = signTimes[2].split("\\:");//????????????
        String[] thridTimes = signTimes[3].split("\\:");//????????????

        if (companyInfo.getSignPattern().equals("00")){

            //???????????????????????????

            //??????????????????  ???comClockInfo?????????????????????????????????????????????
            //???????????????????????????

            //??????????????????????????????????????????????????????????????????????????? ??????????????????????????????
            if ((mornTime != null && mornTime.length()>0) || (Double.parseDouble(currentTime1)>Double.parseDouble(secondTimes[0]) ||
                    (Double.parseDouble(currentTime1)==Double.parseDouble(secondTimes[0]) && Double.parseDouble(currentTime2)>Double.parseDouble(secondTimes[1])))){

                //????????????  ????????????????????? ???????????????????????????????????????
                //????????????????????????????????????????????????

                //????????????  ??????????????????????????????
                if (currentTime1.equals(thridTimes[0])){

                    //??????????????????????????????????????????????????????
                    //?????????????????????8???30 ?????????8???29??????8??????

                    if (Double.parseDouble(currentTime2) == Double.parseDouble(thridTimes[1]) || Double.parseDouble(currentTime2) < Double.parseDouble(thridTimes[1])){

                        //????????????????????????

                        double distance = DistanceUtil.getDistance(ll, new LatLng(mLat1, mLon1));

                        //????????????500?????????????????????
                        if (distance>500){

                            LayoutInflater inflaterDl = LayoutInflater.from(BaiDuQiyeLocationActivity.this);
                            RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_moddel_dialog, null);
                            final Dialog dialoga = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this, R.style.Dialog).create();
                            dialoga.show();
                            dialoga.getWindow().setContentView(layout);
                            dialoga.setCanceledOnTouchOutside(true);
                            RelativeLayout re_item1 = (RelativeLayout) dialoga.findViewById(R.id.re_item1);
                            RelativeLayout re_item2 = (RelativeLayout) dialoga.findViewById(R.id.re_item2);
                            RelativeLayout re_item3 = (RelativeLayout) dialoga.findViewById(R.id.re_item3);

                            TextView tv_title = (TextView) dialoga.findViewById(R.id.tv_title);
                            TextView tv_item1 = (TextView) dialoga.findViewById(R.id.tv_item1);
                            TextView tv_item2 = (TextView) dialoga.findViewById(R.id.tv_item2);
                            TextView tv_item3 = (TextView) dialoga.findViewById(R.id.tv_item3);

                            tv_title.setText("????????????????????????????????????????????????");
                            tv_item1.setText("????????????");
                            tv_item2.setText("????????????");
                            tv_item3.setText("??????");

                            re_item1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialoga.dismiss();

                                    //????????????
                            //        NewUpdateWorkState("0202");
                            //        tongji("02", companyInfo.getCompanyId(), DemoHelper.getInstance().getCurrentUsernName());

                                }
                            });

                            re_item2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialoga.dismiss();

                                    //????????????
                                    UploadAbnormalInfoWithType("????????????");
                                }
                            });

                            re_item3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialoga.dismiss();
                                }
                            });


                        }else {

                            //??????????????????
                            NewUpdateWorkState("0102");
                        }


                    }else {

                        //?????????
                        NewUpdateWorkState("0202");
                        tongji("02", companyInfo.getCompanyId(), DemoHelper.getInstance().getCurrentUsernName());
                    }

                }else if (Double.parseDouble(currentTime1) < Double.parseDouble(thridTimes[0])){
                    //??????????????????????????? ????????????????????????
                    //??????????????????

                    double distance = DistanceUtil.getDistance(ll, new LatLng(mLat1, mLon1));

                    //????????????500?????????????????????
                    if (distance>500){

                        LayoutInflater inflaterDl = LayoutInflater.from(BaiDuQiyeLocationActivity.this);
                        RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_moddel_dialog, null);
                        final Dialog dialoga = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this, R.style.Dialog).create();
                        dialoga.show();
                        dialoga.getWindow().setContentView(layout);
                        dialoga.setCanceledOnTouchOutside(true);
                        RelativeLayout re_item1 = (RelativeLayout) dialoga.findViewById(R.id.re_item1);
                        RelativeLayout re_item2 = (RelativeLayout) dialoga.findViewById(R.id.re_item2);
                        RelativeLayout re_item3 = (RelativeLayout) dialoga.findViewById(R.id.re_item3);

                        TextView tv_title = (TextView) dialoga.findViewById(R.id.tv_title);
                        TextView tv_item1 = (TextView) dialoga.findViewById(R.id.tv_item1);
                        TextView tv_item2 = (TextView) dialoga.findViewById(R.id.tv_item2);
                        TextView tv_item3 = (TextView) dialoga.findViewById(R.id.tv_item3);

                        tv_title.setText("????????????????????????????????????????????????");
                        tv_item1.setText("????????????");
                        tv_item2.setText("????????????");
                        tv_item3.setText("??????");

                        re_item1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialoga.dismiss();

                                //????????????


                            }
                        });

                        re_item2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialoga.dismiss();

                                //????????????
                                UploadAbnormalInfoWithType("????????????");
                            }
                        });

                        re_item3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialoga.dismiss();
                            }
                        });

                    }else {

                        //??????????????????
                        NewUpdateWorkState("0102");
                    }

                }else {

                    //?????????????????????  ?????????????????????
                    NewUpdateWorkState("0202");
                    tongji("02", companyInfo.getCompanyId(), DemoHelper.getInstance().getCurrentUsernName());
                }

            }else {

                //????????????


                //????????????  ??????????????????????????????
                if (currentTime1.equals(fristTimes[0])) {

                    //??????????????? ???????????????

                    if (Double.parseDouble(currentTime2) == Double.parseDouble(fristTimes[1]) || Double.parseDouble(currentTime2) < Double.parseDouble(fristTimes[1])){

                        //????????????????????????

                        double distance = DistanceUtil.getDistance(ll, new LatLng(mLat1, mLon1));

                        //????????????500?????????????????????
                        if (distance>500){

                            LayoutInflater inflaterDl = LayoutInflater.from(BaiDuQiyeLocationActivity.this);
                            RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_moddel_dialog, null);
                            final Dialog dialoga = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this, R.style.Dialog).create();
                            dialoga.show();
                            dialoga.getWindow().setContentView(layout);
                            dialoga.setCanceledOnTouchOutside(true);
                            RelativeLayout re_item1 = (RelativeLayout) dialoga.findViewById(R.id.re_item1);
                            RelativeLayout re_item2 = (RelativeLayout) dialoga.findViewById(R.id.re_item2);
                            RelativeLayout re_item3 = (RelativeLayout) dialoga.findViewById(R.id.re_item3);

                            TextView tv_title = (TextView) dialoga.findViewById(R.id.tv_title);
                            TextView tv_item1 = (TextView) dialoga.findViewById(R.id.tv_item1);
                            TextView tv_item2 = (TextView) dialoga.findViewById(R.id.tv_item2);
                            TextView tv_item3 = (TextView) dialoga.findViewById(R.id.tv_item3);

                            tv_title.setText("????????????????????????????????????????????????");
                            tv_item1.setText("????????????");
                            tv_item2.setText("????????????");
                            tv_item3.setText("??????");

                            re_item1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialoga.dismiss();

                                    //????????????


                                }
                            });

                            re_item2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialoga.dismiss();

                                    //????????????
                                    UploadAbnormalInfoWithType("????????????");
                                }
                            });

                            re_item3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialoga.dismiss();
                                }
                            });


                        }else {

                            //??????????????????
                            NewUpdateWorkState("01");
                        }


                    }else {

                        //?????????
                        NewUpdateWorkState("02");
                        tongji("02", companyInfo.getCompanyId(), DemoHelper.getInstance().getCurrentUsernName());
                    }

                }else if (Double.parseDouble(currentTime1) < Double.parseDouble(fristTimes[0])){

                    //????????????

                    double distance = DistanceUtil.getDistance(ll, new LatLng(mLat1, mLon1));

                    //????????????500?????????????????????
                    if (distance>500){

                        LayoutInflater inflaterDl = LayoutInflater.from(BaiDuQiyeLocationActivity.this);
                        RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_moddel_dialog, null);
                        final Dialog dialoga = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this, R.style.Dialog).create();
                        dialoga.show();
                        dialoga.getWindow().setContentView(layout);
                        dialoga.setCanceledOnTouchOutside(true);
                        RelativeLayout re_item1 = (RelativeLayout) dialoga.findViewById(R.id.re_item1);
                        RelativeLayout re_item2 = (RelativeLayout) dialoga.findViewById(R.id.re_item2);
                        RelativeLayout re_item3 = (RelativeLayout) dialoga.findViewById(R.id.re_item3);

                        TextView tv_title = (TextView) dialoga.findViewById(R.id.tv_title);
                        TextView tv_item1 = (TextView) dialoga.findViewById(R.id.tv_item1);
                        TextView tv_item2 = (TextView) dialoga.findViewById(R.id.tv_item2);
                        TextView tv_item3 = (TextView) dialoga.findViewById(R.id.tv_item3);

                        tv_title.setText("????????????????????????????????????????????????");
                        tv_item1.setText("????????????");
                        tv_item2.setText("????????????");
                        tv_item3.setText("??????");

                        re_item1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialoga.dismiss();

                                //????????????


                            }
                        });

                        re_item2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialoga.dismiss();

                                //????????????
                                UploadAbnormalInfoWithType("????????????");
                            }
                        });

                        re_item3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialoga.dismiss();
                            }
                        });


                    }else {

                        //??????????????????
                        NewUpdateWorkState("01");
                    }

                }else {

                    //????????????
                    NewUpdateWorkState("02");
                    tongji("02", companyInfo.getCompanyId(), DemoHelper.getInstance().getCurrentUsernName());

                }

            }

        }else {

            //???????????????????????????


            //????????????  ??????????????????????????????
            if (currentTime1.equals(fristTimes[0])) {

                //??????????????? ???????????????

                if (Double.parseDouble(currentTime2) == Double.parseDouble(fristTimes[1]) || Double.parseDouble(currentTime2) < Double.parseDouble(fristTimes[1])){

                    //????????????????????????

                    double distance = DistanceUtil.getDistance(ll, new LatLng(mLat1, mLon1));

                    //????????????500?????????????????????
                    if (distance>500){

                        LayoutInflater inflaterDl = LayoutInflater.from(BaiDuQiyeLocationActivity.this);
                        RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_moddel_dialog, null);
                        final Dialog dialoga = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this, R.style.Dialog).create();
                        dialoga.show();
                        dialoga.getWindow().setContentView(layout);
                        dialoga.setCanceledOnTouchOutside(true);
                        RelativeLayout re_item1 = (RelativeLayout) dialoga.findViewById(R.id.re_item1);
                        RelativeLayout re_item2 = (RelativeLayout) dialoga.findViewById(R.id.re_item2);
                        RelativeLayout re_item3 = (RelativeLayout) dialoga.findViewById(R.id.re_item3);

                        TextView tv_title = (TextView) dialoga.findViewById(R.id.tv_title);
                        TextView tv_item1 = (TextView) dialoga.findViewById(R.id.tv_item1);
                        TextView tv_item2 = (TextView) dialoga.findViewById(R.id.tv_item2);
                        TextView tv_item3 = (TextView) dialoga.findViewById(R.id.tv_item3);

                        tv_title.setText("????????????????????????????????????????????????");
                        tv_item1.setText("????????????");
                        tv_item2.setText("????????????");
                        tv_item3.setText("??????");

                        re_item1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialoga.dismiss();

                                //????????????


                            }
                        });

                        re_item2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialoga.dismiss();

                                //????????????
                                UploadAbnormalInfoWithType("????????????");
                            }
                        });

                        re_item3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialoga.dismiss();
                            }
                        });


                    }else {

                        //??????????????????
                        NewUpdateWorkState("01");
                    }


                }else {

                    //?????????
                    NewUpdateWorkState("02");
                    tongji("02", companyInfo.getCompanyId(), DemoHelper.getInstance().getCurrentUsernName());
                }

            }else if (Double.parseDouble(currentTime1) < Double.parseDouble(fristTimes[0])){

                //????????????

                double distance = DistanceUtil.getDistance(ll, new LatLng(mLat1, mLon1));

                //????????????500?????????????????????
                if (distance>500){

                    LayoutInflater inflaterDl = LayoutInflater.from(BaiDuQiyeLocationActivity.this);
                    RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_moddel_dialog, null);
                    final Dialog dialoga = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this, R.style.Dialog).create();
                    dialoga.show();
                    dialoga.getWindow().setContentView(layout);
                    dialoga.setCanceledOnTouchOutside(true);
                    RelativeLayout re_item1 = (RelativeLayout) dialoga.findViewById(R.id.re_item1);
                    RelativeLayout re_item2 = (RelativeLayout) dialoga.findViewById(R.id.re_item2);
                    RelativeLayout re_item3 = (RelativeLayout) dialoga.findViewById(R.id.re_item3);

                    TextView tv_title = (TextView) dialoga.findViewById(R.id.tv_title);
                    TextView tv_item1 = (TextView) dialoga.findViewById(R.id.tv_item1);
                    TextView tv_item2 = (TextView) dialoga.findViewById(R.id.tv_item2);
                    TextView tv_item3 = (TextView) dialoga.findViewById(R.id.tv_item3);

                    tv_title.setText("????????????????????????????????????????????????");
                    tv_item1.setText("????????????");
                    tv_item2.setText("????????????");
                    tv_item3.setText("??????");

                    re_item1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialoga.dismiss();

                            //????????????


                        }
                    });

                    re_item2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialoga.dismiss();

                            //????????????
                            UploadAbnormalInfoWithType("????????????");
                        }
                    });

                    re_item3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialoga.dismiss();
                        }
                    });


                }else {

                    //??????????????????
                    NewUpdateWorkState("01");
                }

            }else {

                //????????????
                NewUpdateWorkState("02");
                tongji("02", companyInfo.getCompanyId(), DemoHelper.getInstance().getCurrentUsernName());

            }

        }

    }


    //???????????????????????????
    private void UploadAbnormalInfoWithType(final String type){

        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(BaiDuQiyeLocationActivity.this, "?????????...");

        String url = FXConstant.URL_INSERTABNORMAL;

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                WeiboDialogUtils.closeDialog(mWeiboDialog);
                com.alibaba.fastjson.JSONObject object = JSON.parseObject(s);

                if (object.getString("code").equals("SUCCESS")){

                    //??????????????????
                    Toast.makeText(BaiDuQiyeLocationActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    finish();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                WeiboDialogUtils.closeDialog(mWeiboDialog);

            }


        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String currentTime = dateFormat.format(date);
                String timestamp = dateFormat.format(date).substring(0,8);
                com.alibaba.fastjson.JSONObject object = JSON.parseObject(comClockInfo);
                SharedPreferences mSharedPreferences = getSharedPreferences("sangu_dingwei_city", Context.MODE_PRIVATE);
                String location = mSharedPreferences.getString("location","0");

                params.put("companyid",companyInfo.getCompanyId());
                params.put("uid",DemoHelper.getInstance().getCurrentUsernName());
                params.put("timestamp",timestamp);
                params.put("site",location);
                params.put("reason","????????????");
                params.put("address",mLat1+"|"+mLon1);
                params.put("signtime",currentTime);

                params.put("timetype",type);

                return params;

            }
        };

        MySingleton.getInstance(BaiDuQiyeLocationActivity.this).addToRequestQueue(request);

    }




    //?????????????????????????????????
    //mornTime      ????????????
    //noonTime      ????????????
    //afternoonTime ????????????
    //nightTime     ????????????
    //add1 add2 add3 add4  ????????????4???????????????
    //  01 ????????????  02??????  03????????????????????????=02???04??????  05??????
    //0102????????????????????????   0202????????????????????????   0402??????????????????   0502??????????????????

    private void NewUpdateWorkState(final String state){

        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(BaiDuQiyeLocationActivity.this, "?????????...");

        String url = FXConstant.URL_UPDATEComClock;

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                WeiboDialogUtils.closeDialog(mWeiboDialog);

                com.alibaba.fastjson.JSONObject object = JSON.parseObject(s);

                if (object.getString("code").equals("SUCCESS")){

                    Toast.makeText(BaiDuQiyeLocationActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    finish();

                }else {


                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                WeiboDialogUtils.closeDialog(mWeiboDialog);

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String currentTime = dateFormat.format(date);
                String timestamp = dateFormat.format(date).substring(0,8);
                com.alibaba.fastjson.JSONObject object = JSON.parseObject(comClockInfo);


                params.put("companyId",companyInfo.getCompanyId());
                params.put("uId",DemoHelper.getInstance().getCurrentUsernName());
                params.put("timestamp",timestamp);

                //????????????????????????????????????
                if (state.equals("01")){

                    //??????????????????
                    if (object.getString("leaveState") != null && object.getString("leaveState").equals("01")) {
                        //?????????????????????????????????
                    }else {
                        //???????????????????????????
                        params.put("workState","01");
                    }

                    params.put("mornTime",currentTime);
                    params.put("add1",mLat1+"|"+mLon1);

                }else if (state.equals("0102")){
                    //??????????????????

                    params.put("afternoonTime",currentTime);
                    params.put("add3",mLat1+"|"+mLon1);

                    //??????????????????????????????????????????????????????
                    //???????????????????????????????????????????????????????????????
                    //??????????????????

                    if (!(object.getString("mornTime") != null && object.getString("mornTime").length()>0)){


                        if (object.getString("leaveState") != null && object.getString("leaveState").equals("01")) {
                            //?????????????????????????????????
                        }else {
                            //???????????????????????????
                            params.put("workState","01");
                        }

                        params.put("mornTime",currentTime);
                        params.put("add1",mLat1+"|"+mLon1);

                        params.put("noonTime",currentTime);
                        params.put("add2",mLat1+"|"+mLon1);

                    }

                }else if (state.equals("02")){
                    //?????? ????????????????????????????????????

                    params.put("mornTime",currentTime);
                    params.put("add1",mLat1+"|"+mLon1);

                    if (object.getString("leaveState") != null && object.getString("leaveState").equals("01")) {
                        //?????????????????????????????????
                    }else {
                        //???????????????????????????
                        params.put("workState","01");

                        //??????????????????
                        params.put("lateState","01");

                        //?????????????????????
                        params.put("lateTime",NewCalculateWithType("????????????")+"");

                    }


                }else if (state.equals("0202")){

                    params.put("afternoonTime",currentTime);
                    params.put("add3",mLat1+"|"+mLon1);

                    if (object.getString("leaveState") != null && object.getString("leaveState").equals("01")) {
                        //?????????????????????????????????
                    }else {
                        //???????????????????????????
                        params.put("workState","01");

                        //??????????????????
                        params.put("lateState","01");


                        if (object.getString("lateTime") != null && object.getString("lateTime").length()>0){

                            //?????????????????????
                            params.put("lateTime",Double.parseDouble(object.getString("lateTime"))+NewCalculateWithType("????????????")+"");

                        }else {

                            //?????????????????????
                            params.put("lateTime",NewCalculateWithType("????????????")+"");

                        }

                    }

                    if (!(object.getString("mornTime") != null && object.getString("mornTime").length()>0)){


                        if (object.getString("leaveState") != null && object.getString("leaveState").equals("01")) {
                            //?????????????????????????????????
                        }else {
                            //???????????????????????????
                            params.put("workState","01");
                        }

                        params.put("mornTime",currentTime);
                        params.put("add1",mLat1+"|"+mLon1);

                        params.put("noonTime",currentTime);
                        params.put("add2",mLat1+"|"+mLon1);

                    }

                }else if (state.equals("03")){



                }else if (state.equals("04")){

                    //?????????????????????????????? ????????????????????????????????? ?????????

                    //????????????????????????
                    //??????????????????????????????????????????????????????
                    //1?????????????????????????????? ?????????????????????????????????????????? 0402???????????????
                    //2???04??????????????? ??????????????????????????? ??????????????????????????????????????????
                    //3?????????????????????????????????????????????????????? ?????????????????????????????????

                    params.put("nightTime",currentTime);
                    params.put("add4",mLat1+"|"+mLon1);

                    if (object.getString("leaveState") != null && object.getString("leaveState").equals("01")) {
                        //?????????????????????????????????
                    }else {

                        //???????????? 00????????? 01??????  02??????
                        params.put("workState","02");

                        if (object.getString("workTime") != null && object.getString("workTime").length()>0){

                            params.put("workTime",Double.parseDouble(object.getString("workTime"))+NewCalculateWithType("????????????")+"");

                        }else {

                            params.put("workTime",NewCalculateWithType("????????????")+"");

                        }

                    }

                }else if (state.equals("0402")) {

                    //???????????????????????????  ???????????????
                    //???????????????????????????????????????????????????  ?????????????????????
                    params.put("noonTime",currentTime);
                    params.put("add2",mLat1+"|"+mLon1);

                    if (object.getString("leaveState") != null && object.getString("leaveState").equals("01")) {
                        //?????????????????????????????????
                    }else {

                        params.put("workTime",NewCalculateWithType("????????????")+"");

                    }

                }else if (state.equals("05")) {

                    //?????????????????? ??????????????????  ??????????????????
                    //?????????????????????  ???????????????????????????

                    params.put("nightTime",currentTime);
                    params.put("add4",mLat1+"|"+mLon1);

                    if (object.getString("leaveState") != null && object.getString("leaveState").equals("01")) {
                        //?????????????????????????????????
                    }else {

                        params.put("leaveEarly","01");
                        params.put("workState","02");

                        if (object.getString("leaveEarlyTime") != null && object.getString("leaveEarlyTime").length()>0){

                            params.put("leaveEarlyTime",Double.parseDouble(object.getString("leaveEarlyTime"))+NewCalculateWithType("????????????")+"");

                        }else {

                            params.put("leaveEarlyTime",NewCalculateWithType("????????????")+"");
                        }

                    }


                    if (object.getString("workTime") != null && object.getString("workTime").length()>0) {

                        params.put("workTime",Double.parseDouble(object.getString("workTime"))+NewCalculateWithType("????????????")+"");

                    }else {

                        params.put("workTime",NewCalculateWithType("????????????")+"");

                    }

                }else if (state.equals("0502")) {

                    //?????????????????? ??????????????????  ??????????????????
                    //????????????????????? ?????????????????????  ????????????????????????

                    //???????????????????????????
                    params.put("noonTime",currentTime);
                    params.put("add2",mLat1+"|"+mLon1);

                    if (object.getString("leaveState") != null && object.getString("leaveState").equals("01")) {
                        //?????????????????????????????????
                    }else {

                        params.put("leaveEarlyTime",NewCalculateWithType("????????????")+"");
                        params.put("workTime",NewCalculateWithType("????????????")+"");

                    }

                }


                return params;
            }
        };

        MySingleton.getInstance(BaiDuQiyeLocationActivity.this).addToRequestQueue(request);

    }


    //??????????????????
    private double NewCalculateWithType(String type){

        double allTime = 0;

        String currentTime = getcurrentTime();

        String current1 = currentTime.substring(8,10);
        String current2 = currentTime.substring(10,12);

        com.alibaba.fastjson.JSONObject object = JSON.parseObject(comClockInfo);

        String mornTime = object.getString("mornTime");
        String afternoonTime = object.getString("afternoonTime");
        String setSignaTime = object.getString("setSignaTime");//?????????????????????
        //?????????|?????????|?????????|?????????
        String[] signTimes = setSignaTime.split("\\|");


        if (type.equals("????????????") || type.equals("????????????")){

            //????????????????????????
            //????????????????????????arr[0]?????????????????????  arr1[0]????????? arr1[1]?????????

            String[] arr1;

            if (type.equals("????????????")){

                arr1 = signTimes[0].split("\\:");

            }else {

                arr1 = signTimes[3].split("\\:");
            }


            //arr1[0] ???   arr1[1]???  8:10

            if (current1.equals(arr1[0])){

                //????????????  ??????????????????
                allTime = Double.parseDouble(current2) - Double.parseDouble(arr1[1]);

            }else{

                //???????????????????????? ??????????????? ??????????????????
                if (Double.parseDouble(current2) > Double.parseDouble(arr1[1])){

                    //????????????  ????????????  ??????8???10?????????  9???20??????
                    //?????????????????????????????????  ????????? ?????????

                    double hour = Double.parseDouble(current1) - Double.parseDouble(arr1[0]);

                    double minutes = Double.parseDouble(current2) - Double.parseDouble(arr1[1]);

                    allTime = hour * 60 + minutes;


                }else {

                    double hour = Double.parseDouble(current1) - Double.parseDouble(arr1[0]) - 1;

                    double minutes = Double.parseDouble(current2) + 60 - Double.parseDouble(arr1[1]);

                    allTime = hour * 60 + minutes;

                }

            }

        }else if (type.equals("????????????")){


            //?????????????????????????????????
            //?????????????????? ?????????|?????????|?????????|?????????  arr0123

            //??????????????????????????????????????????  ???????????????????????????????????????????????????????????????????????????????????????

            String mornHour = mornTime.substring(8,10);
            String mornMin = mornTime.substring(10,12);

            //????????????????????????arr[2]  arr2[0]????????? arr2[1]?????????
            String[] arr2 = signTimes[2].split("\\:");

            //??????????????????????????????????????????????????????????????????????????????????????????

            if (Double.parseDouble(current1) < Double.parseDouble(arr2[0]) || (Double.parseDouble(current1) == Double.parseDouble(arr2[0]) && Double.parseDouble(current2) < Double.parseDouble(arr2[1]))){

                //????????? ?????????????????????
                //??????????????????????????????????????????????????????

                // (?????????????????????????????????????????????????????????)
                if (Double.parseDouble(current2) > Double.parseDouble(mornMin)){

                    double hour = Double.parseDouble(current1) - Double.parseDouble(mornHour);

                    double minutes = Double.parseDouble(current2) - Double.parseDouble(mornMin);

                    allTime = hour * 60 + minutes;

                }else {

                    double hour = Double.parseDouble(current1) - Double.parseDouble(mornHour) - 1;

                    double minutes = Double.parseDouble(current2) + 60 - Double.parseDouble(mornMin);

                    allTime = hour * 60 + minutes;

                }


            }else {

                // (?????????????????????????????????????????????????????????)
                if (Double.parseDouble(arr2[1]) > Double.parseDouble(mornMin)){

                    double hour = Double.parseDouble(arr2[0]) - Double.parseDouble(mornHour);

                    double minutes = Double.parseDouble(arr2[1]) - Double.parseDouble(mornMin);

                    allTime = hour * 60 + minutes;

                }else {

                    double hour = Double.parseDouble(arr2[0]) - Double.parseDouble(mornHour) - 1;

                    double minutes = Double.parseDouble(arr2[1]) + 60 - Double.parseDouble(mornMin);

                    allTime = hour * 60 + minutes;

                }

            }

        }else if (type.equals("????????????") || type.equals("????????????")){

            //????????????  ?????????????????????????????????????????????????????????????????????
            //????????????????????????arr[2]  arr2[0]????????? arr2[1]?????????
            String[] arr2;

            if (type.equals("????????????")) {

                arr2 = signTimes[2].split("\\:");

            }else
            {    //??????????????????
                arr2 = signTimes[1].split("\\:");
            }

            //???????????????  ??????????????????????????????????????????

            //??????????????????

            if (Double.parseDouble(arr2[1]) > Double.parseDouble(current2)){

                double hour = Double.parseDouble(arr2[0]) - Double.parseDouble(current1);

                double minutes = Double.parseDouble(arr2[1]) - Double.parseDouble(current2);

                allTime = hour * 60 + minutes;

            }else {

                double hour = Double.parseDouble(arr2[0]) - Double.parseDouble(current1) - 1;

                double minutes = Double.parseDouble(arr2[1]) + 60 - Double.parseDouble(current2);

                allTime = hour * 60 + minutes;
            }

        }else if (type.equals("????????????")){

            //??????????????????????????????????????????

            //???????????????????????????
            String mornHour ;
            //?????????????????????
            String mornMin ;

            if (companyInfo.getSignPattern().equals("00")){

                //???????????????????????????
                 mornHour = afternoonTime.substring(8,10);
                 mornMin = afternoonTime.substring(10,12);

            }else {

                 mornHour = mornTime.substring(8,10);
                 mornMin = mornTime.substring(10,12);

            }

            //????????????????????????arr[1]  arr2[0]????????? arr2[1]?????????
            String[] arr2 = signTimes[1].split("\\:");

            //??????????????????????????????????????????????????????????????????????????????????????????

            if (Double.parseDouble(current1) < Double.parseDouble(arr2[0]) || (Double.parseDouble(current1) == Double.parseDouble(arr2[0]) && Double.parseDouble(current2) < Double.parseDouble(arr2[1]))){

                //????????? ?????????????????????
                //??????????????????????????????????????????????????????

                // (?????????????????????????????????????????????????????????)
                if (Double.parseDouble(current2) > Double.parseDouble(mornMin)){

                    double hour = Double.parseDouble(current1) - Double.parseDouble(mornHour);

                    double minutes = Double.parseDouble(current2) - Double.parseDouble(mornMin);

                    allTime = hour * 60 + minutes;

                }else {

                    double hour = Double.parseDouble(current1) - Double.parseDouble(mornHour) - 1;

                    double minutes = Double.parseDouble(current2) + 60 - Double.parseDouble(mornMin);

                    allTime = hour * 60 + minutes;

                }


            }else {

                // (?????????????????????????????????????????????????????????)
                if (Double.parseDouble(arr2[1]) > Double.parseDouble(mornMin)){

                    double hour = Double.parseDouble(arr2[0]) - Double.parseDouble(mornHour);

                    double minutes = Double.parseDouble(arr2[1]) - Double.parseDouble(mornMin);

                    allTime = hour * 60 + minutes;

                }else {

                    double hour = Double.parseDouble(arr2[0]) - Double.parseDouble(mornHour) - 1;

                    double minutes = Double.parseDouble(arr2[1]) + 60 - Double.parseDouble(mornMin);

                    allTime = hour * 60 + minutes;

                }

            }

        }

        return allTime;

    }


    @Override
    public void updateUserList(List<MemberInfo> users,boolean hasMore) {
        boolean isFinishma = false;
        boolean isFinishse = false;
        for (int m = 0;m<users.size();m++){
            strId = users.get(m).getResv6();
            String id = users.get(m).getuLoginId();
            if ("00".equals(strId)){
                managerId = users.get(m).getuLoginId();
                isFinishma = true;
            }
            if (id.equals(currentId)){
                signState = users.get(m).getSignInfo_signState();
                signRemark = users.get(m).getSignInfo_remark();
                signTime = users.get(m).getSignInfo_signTime();
                memberInfo = users.get(m);
                comClockInfo = users.get(m).getComClockInfo();
                isFinishse = true;
            }
            if (isFinishma&&isFinishse){
                break;
            }
        }
        if (users.size() > 0) {
            for (int i = 0;i<users.size();i++){
                String lng = users.get(i).getResv1();
                String lat = users.get(i).getResv2();
                String loginId = users.get(i).getuLoginId();
                String name = users.get(i).getuName();
                String sex = TextUtils.isEmpty(users.get(i).getuSex())?"01":users.get(i).getuSex();
                strLat.add(lat);
                strLong.add(lng);
                strLoginId.add(loginId);
                strName.add(name);
                strSex.add(sex);
            }

            //??????????????????????????????
            com.alibaba.fastjson.JSONObject object = JSON.parseObject(comClockInfo);

            String type = object.getString("type");
            String mornTime = object.getString("mornTime");//????????????
            String noonTime = object.getString("noonTime");//????????????
            String afternoonTime = object.getString("afternoonTime");//????????????
            String nightTime = object.getString("nightTime");//????????????
            String signPattern = companyInfo.getSignPattern();//?????????????????????????????????

            btnDh.setText("??????");
            shBzhutai = "??????";


            if (type.equals("01")){

                //01?????????????????????  00???????????????????????????

                //??????????????????

                //??????????????????????????????  ????????????????????????

                if (mornTime != null && mornTime.length()>0){

                    btnDh.setText("????????????");
                    shBzhutai = "????????????";

                }

                if (signPattern.equals("00")){

                    //?????????????????????????????????????????????????????????????????????

                    //????????????????????????

                    if (noonTime != null && noonTime.length()>0){

                        btnDh.setText("??????");
                        shBzhutai = "??????";

                    }

                    if (afternoonTime != null && afternoonTime.length()>0){

                        btnDh.setText("????????????");
                        shBzhutai = "????????????";

                    }

                }

                if (nightTime != null && nightTime.length()>0){

                    btnDh.setText("?????????");
                    shBzhutai = "?????????";
                    btnDh.setBackgroundResource(R.drawable.fx_bg_btn_gray);
                    btnDh.setEnabled(false);

                }

            }else {

                btnDh.setText("?????????");
                shBzhutai = "?????????";
                btnDh.setBackgroundResource(R.drawable.fx_bg_btn_gray);
                btnDh.setEnabled(false);

            }


//            if (signTime.length()>0){
//                signTime = signTime.substring(0,8);
//            }
//            if (nowTime.equals(signTime)){
//                if (signState.equals("01")){
//                    shBzhutai = "????????????";
//                }else if (signState.equals("00")){
//                    shBzhutai = "?????????";
//                }
//            }else {
//                shBzhutai = "??????";
//            }
        }
      //  btnDh.setText(shBzhutai);
        btnXq.setText("??????");
//        if (shBzhutai.equals("?????????")) {
//            btnDh.setEnabled(false);
//            btnDh.setBackgroundResource(R.drawable.fx_bg_btn_gray);
//        }
        btnDh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shBzhutai.equals("??????")) {
                   // showDialog1(shBzhutai);

                    NewsSignJudge();

                } else if (shBzhutai.equals("????????????")) {
//                    if (isFenxiang) {
////                        showTishi("????????????");
////                    } else {
////                        showXbTishi();
////                    }

                    AfterWorkClick();

                }
            }
        });
        isLFinished = true;
        myMaphandler.postDelayed(mapThread, 100);
    }


    private void AfterWorkClick(){


        //  ???????????????????????????
        if (companyInfo.getSignShareType().equals("00")){

            //?????????00 ?????????????????????????????????

            //??????????????????????????????????????????

            if (companyInfo.getSignShareNeed().equals("01")){

                //????????????


            }else {

                //???????????????
                final LayoutInflater inflater1 = LayoutInflater.from(BaiDuQiyeLocationActivity.this);
                RelativeLayout layout1 = (RelativeLayout) inflater1.inflate(R.layout.dialog_alert, null);
                final Dialog collectionDialog = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this, R.style.Dialog).create();
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

                title_tv1.setText("???????????????????????????");

                btnCancel1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        collectionDialog.dismiss();

                        //????????? ????????????
                        WorkEndAboutInfo();

                    }
                });

                btnOK1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        collectionDialog.dismiss();

                        //??????


                    }
                });

            }

        }else {

            //??????00 ?????????????????????????????????

            //????????????
            WorkEndAboutInfo();

        }

    }


    //???????????????????????????
    private void WorkEndAboutInfo(){

        final LayoutInflater inflater1 = LayoutInflater.from(BaiDuQiyeLocationActivity.this);
        RelativeLayout layout1 = (RelativeLayout) inflater1.inflate(R.layout.dialog_alert, null);
        final Dialog collectionDialog = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this, R.style.Dialog).create();
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

        title_tv1.setText("???????????????????????????");

        btnCancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionDialog.dismiss();
            }
        });

        btnOK1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionDialog.dismiss();

                com.alibaba.fastjson.JSONObject object = JSON.parseObject(comClockInfo);

                String comLatitude = object.getString("latitude");//??????
                String comLongitude = object.getString("longitude");//??????

                final LatLng ll = JSONUtil.convertGPSToBaidu(new LatLng(Double.parseDouble(comLatitude), Double.parseDouble(comLongitude)));

                String setSignaTime = object.getString("setSignaTime");//?????????????????????

                //??????????????????
                String mornTime = object.getString("mornTime");//????????????
                String noonTime = object.getString("noonTime");//????????????
                String afternoonTime = object.getString("afternoonTime");//????????????
                String nightTime = object.getString("nightTime");//????????????

                //?????????|?????????|?????????|?????????
                String[] signTimes = setSignaTime.split("\\|");

                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String current =  dateFormat.format(date);
                String currentTime1 = dateFormat.format(date).substring(8,10);
                String currentTime2 = dateFormat.format(date).substring(10,12);

                String[] fristTimes = signTimes[0].split("\\:");//????????????
                String[] secondTimes = signTimes[2].split("\\:");//????????????
                String[] thridTimes = signTimes[3].split("\\:");//????????????
                String[] fourTimes = signTimes[1].split("\\:");//????????????


                //????????????????????????????????????????????????????????????????????????

                if ((object.getString("noonTime") != null && object.getString("noonTime").length() > 0 ) ||
                        companyInfo.getSignPattern().equals("01")){

                    //??????????????????

                    //??????????????????????????????????????????????????????
                    if (Double.parseDouble(currentTime1) > Double.parseDouble(fourTimes[0])){

                        //??????????????????

                        double distance = DistanceUtil.getDistance(ll, new LatLng(mLat1, mLon1));

                        //????????????500??????????????????
                        if (distance>500){

                            LayoutInflater inflaterDl = LayoutInflater.from(BaiDuQiyeLocationActivity.this);
                            RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_moddel_dialog, null);
                            final Dialog dialoga = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this, R.style.Dialog).create();
                            dialoga.show();
                            dialoga.getWindow().setContentView(layout);
                            dialoga.setCanceledOnTouchOutside(true);
                            RelativeLayout re_item1 = (RelativeLayout) dialoga.findViewById(R.id.re_item1);
                            RelativeLayout re_item2 = (RelativeLayout) dialoga.findViewById(R.id.re_item2);
                            RelativeLayout re_item3 = (RelativeLayout) dialoga.findViewById(R.id.re_item3);

                            TextView tv_title = (TextView) dialoga.findViewById(R.id.tv_title);
                            TextView tv_item1 = (TextView) dialoga.findViewById(R.id.tv_item1);
                            TextView tv_item2 = (TextView) dialoga.findViewById(R.id.tv_item2);
                            TextView tv_item3 = (TextView) dialoga.findViewById(R.id.tv_item3);

                            tv_title.setText("????????????????????????????????????????????????");
                            tv_item1.setText("????????????");
                            tv_item2.setText("????????????");
                            tv_item3.setText("??????");

                            re_item1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialoga.dismiss();


                                }
                            });

                            re_item2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialoga.dismiss();

                                    //????????????
                                    UploadAbnormalInfoWithType("????????????");
                                }
                            });

                            re_item3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialoga.dismiss();
                                }
                            });


                        }else {

                            //????????????????????????  04
                            NewUpdateWorkState("04");

                        }


                    }else if (Double.parseDouble(currentTime1) == Double.parseDouble(fourTimes[0])){

                        //????????? ?????????

                        if (Double.parseDouble(currentTime2) > Double.parseDouble(fourTimes[1]) || Double.parseDouble(currentTime2) == Double.parseDouble(fourTimes[1])){

                            //????????????

                            double distance = DistanceUtil.getDistance(ll, new LatLng(mLat1, mLon1));

                            //????????????500??????????????????
                            if (distance>500){

                                LayoutInflater inflaterDl = LayoutInflater.from(BaiDuQiyeLocationActivity.this);
                                RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_moddel_dialog, null);
                                final Dialog dialoga = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this, R.style.Dialog).create();
                                dialoga.show();
                                dialoga.getWindow().setContentView(layout);
                                dialoga.setCanceledOnTouchOutside(true);
                                RelativeLayout re_item1 = (RelativeLayout) dialoga.findViewById(R.id.re_item1);
                                RelativeLayout re_item2 = (RelativeLayout) dialoga.findViewById(R.id.re_item2);
                                RelativeLayout re_item3 = (RelativeLayout) dialoga.findViewById(R.id.re_item3);

                                TextView tv_title = (TextView) dialoga.findViewById(R.id.tv_title);
                                TextView tv_item1 = (TextView) dialoga.findViewById(R.id.tv_item1);
                                TextView tv_item2 = (TextView) dialoga.findViewById(R.id.tv_item2);
                                TextView tv_item3 = (TextView) dialoga.findViewById(R.id.tv_item3);

                                tv_title.setText("????????????????????????????????????????????????");
                                tv_item1.setText("????????????");
                                tv_item2.setText("????????????");
                                tv_item3.setText("??????");

                                re_item1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialoga.dismiss();


                                    }
                                });

                                re_item2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialoga.dismiss();

                                        //????????????
                                        UploadAbnormalInfoWithType("????????????");
                                    }
                                });

                                re_item3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialoga.dismiss();
                                    }
                                });


                            }else {

                                //????????????????????????  04
                                NewUpdateWorkState("04");

                            }

                        }else {

                            //??????
                            NewUpdateWorkState("05");
                        }


                    }else {

                        //?????????????????????
                        NewUpdateWorkState("05");

                    }


                }else {

                    //????????????????????????

                    //??????????????????????????????????????????????????????
                    if (Double.parseDouble(currentTime1) > Double.parseDouble(secondTimes[0])){

                        //??????????????????

                        double distance = DistanceUtil.getDistance(ll, new LatLng(mLat1, mLon1));

                        //????????????500??????????????????
                        if (distance>500){

                            LayoutInflater inflaterDl = LayoutInflater.from(BaiDuQiyeLocationActivity.this);
                            RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_moddel_dialog, null);
                            final Dialog dialoga = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this, R.style.Dialog).create();
                            dialoga.show();
                            dialoga.getWindow().setContentView(layout);
                            dialoga.setCanceledOnTouchOutside(true);
                            RelativeLayout re_item1 = (RelativeLayout) dialoga.findViewById(R.id.re_item1);
                            RelativeLayout re_item2 = (RelativeLayout) dialoga.findViewById(R.id.re_item2);
                            RelativeLayout re_item3 = (RelativeLayout) dialoga.findViewById(R.id.re_item3);

                            TextView tv_title = (TextView) dialoga.findViewById(R.id.tv_title);
                            TextView tv_item1 = (TextView) dialoga.findViewById(R.id.tv_item1);
                            TextView tv_item2 = (TextView) dialoga.findViewById(R.id.tv_item2);
                            TextView tv_item3 = (TextView) dialoga.findViewById(R.id.tv_item3);

                            tv_title.setText("????????????????????????????????????????????????");
                            tv_item1.setText("????????????");
                            tv_item2.setText("????????????");
                            tv_item3.setText("??????");

                            re_item1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialoga.dismiss();


                                }
                            });

                            re_item2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialoga.dismiss();

                                    //????????????
                                    UploadAbnormalInfoWithType("????????????");
                                }
                            });

                            re_item3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialoga.dismiss();
                                }
                            });


                        }else {

                            //????????????????????????  0402
                            NewUpdateWorkState("0402");

                        }


                    }else if (Double.parseDouble(currentTime1) == Double.parseDouble(secondTimes[0])){

                        //????????? ?????????

                        if (Double.parseDouble(currentTime2) > Double.parseDouble(secondTimes[1]) || Double.parseDouble(currentTime2) == Double.parseDouble(secondTimes[1])){

                            //????????????

                            double distance = DistanceUtil.getDistance(ll, new LatLng(mLat1, mLon1));

                            //????????????500??????????????????
                            if (distance>500){

                                LayoutInflater inflaterDl = LayoutInflater.from(BaiDuQiyeLocationActivity.this);
                                RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_moddel_dialog, null);
                                final Dialog dialoga = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this, R.style.Dialog).create();
                                dialoga.show();
                                dialoga.getWindow().setContentView(layout);
                                dialoga.setCanceledOnTouchOutside(true);
                                RelativeLayout re_item1 = (RelativeLayout) dialoga.findViewById(R.id.re_item1);
                                RelativeLayout re_item2 = (RelativeLayout) dialoga.findViewById(R.id.re_item2);
                                RelativeLayout re_item3 = (RelativeLayout) dialoga.findViewById(R.id.re_item3);

                                TextView tv_title = (TextView) dialoga.findViewById(R.id.tv_title);
                                TextView tv_item1 = (TextView) dialoga.findViewById(R.id.tv_item1);
                                TextView tv_item2 = (TextView) dialoga.findViewById(R.id.tv_item2);
                                TextView tv_item3 = (TextView) dialoga.findViewById(R.id.tv_item3);

                                tv_title.setText("????????????????????????????????????????????????");
                                tv_item1.setText("????????????");
                                tv_item2.setText("????????????");
                                tv_item3.setText("??????");

                                re_item1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialoga.dismiss();


                                    }
                                });

                                re_item2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialoga.dismiss();

                                        //????????????
                                        UploadAbnormalInfoWithType("????????????");
                                    }
                                });

                                re_item3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialoga.dismiss();
                                    }
                                });


                            }else {

                                //????????????????????????  0402
                                NewUpdateWorkState("0402");

                            }

                        }else {

                            //??????
                            NewUpdateWorkState("0502");
                        }


                    }else {

                        //?????????????????????
                        NewUpdateWorkState("0502");

                    }

                }

            }
        });



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

    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view ???????????????????????????????????????
            if (location == null || mMapView == null) {
                return;
            }
            mLat1 = location.getLatitude();
            mLon1 = location.getLongitude();
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // ?????????????????????????????????????????????????????????0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(15.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
            if (isFinished&&isLFinished) {
                myMaphandler.sendEmptyMessage(0);
            }
        }

    }

    Runnable mapThread = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < strLat.size(); i++) {
                View v = null;
                if (strSex.get(i).equals("01")) {
                    v = LayoutInflater.from(BaiDuQiyeLocationActivity.this).inflate(R.layout.item_map_user, null);
                } else {
                    v = LayoutInflater.from(BaiDuQiyeLocationActivity.this).inflate(R.layout.item_map_user1, null);
                }
                v.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                tvTitle = (TextView) v.findViewById(R.id.tv_name);
                tvTitle.setText(strName.get(i));
                double lat = 0, lng = 0;
                if (strLat.get(i) != null && !"".equals(strLat.get(i))) {
                    lat = Double.valueOf(strLat.get(i));
                }
                if (strLong.get(i) != null && !"".equals(strLong.get(i))) {
                    lng = Double.valueOf(strLong.get(i));
                }
                LatLng p = new LatLng(lat, lng);
                mMapView = new MapView(BaiDuQiyeLocationActivity.this,
                        new BaiduMapOptions().mapStatus(new MapStatus.Builder()
                                .target(p).build()));
                CoordinateConverter converter = new CoordinateConverter();
                converter.coord(p);
                converter.from(CoordinateConverter.CoordType.COMMON);
                LatLng convertLatLng = converter.convert();
                MarkerOptions mao = new MarkerOptions()
                        .position(convertLatLng)
                        .icon(BitmapDescriptorFactory
                                .fromView(v))
                        .zIndex(9)
                        .draggable(false);
                Marker mar = (Marker) mBaiduMap.addOverlay(mao);
                oa.add(mar);
//                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 16.0f);
//                mBaiduMap.animateMapStatus(u);
            }
            myMaphandler.removeCallbacks(mapThread);
        }
    };

    private void initV2() {
        tvAge = (TextView) vi2.findViewById(R.id.tv_nianling);
        tvmaj1 = (TextView) vi2.findViewById(R.id.tv_project_one);
        tv_distance = (TextView) vi2.findViewById(R.id.tv_distance);
        tvmaj2 = (TextView) vi2.findViewById(R.id.tv_project_two);
        tvmaj3 = (TextView) vi2.findViewById(R.id.tv_project_three);
        tvmaj4 = (TextView) vi2.findViewById(R.id.tv_project_four);
        tv_zy1_bao = (TextView) vi2.findViewById(R.id.tv_zy1_bao);
        tv_zy2_bao = (TextView) vi2.findViewById(R.id.tv_zy2_bao);
        tv_zy3_bao = (TextView) vi2.findViewById(R.id.tv_zy3_bao);
        tv_zy4_bao = (TextView) vi2.findViewById(R.id.tv_zy4_bao);
        tvName = (TextView) vi2.findViewById(R.id.tv_name);
        tvsign = (TextView) vi2.findViewById(R.id.tv_qianming);
        tv_titl = (TextView) vi2.findViewById(R.id.tv_titl);
        btnDh = (Button) vi2.findViewById(R.id.btn_daohang);
        btnXq = (Button) vi2.findViewById(R.id.btn_xiangqing);
        ivAvatar = (CircleImageView) vi2.findViewById(R.id.iv_head);
        ivSex = (ImageView) vi2.findViewById(R.id.iv_sex);
        iv_zy1_tupian = (TextView) vi2.findViewById(R.id.iv_zy1_tupian);
        iv_zy2_tupian = (TextView) vi2.findViewById(R.id.iv_zy2_tupian);
        iv_zy3_tupian = (TextView) vi2.findViewById(R.id.iv_zy3_tupian);
        iv_zy4_tupian = (TextView) vi2.findViewById(R.id.iv_zy4_tupian);
    }

    private void initV3() {
        tvAge3 = (TextView) vi3.findViewById(R.id.tv_nianling);
        tvmaj13 = (TextView) vi3.findViewById(R.id.tv_project_one);
        tv_distance3 = (TextView) vi3.findViewById(R.id.tv_distance);
        tvmaj23 = (TextView) vi3.findViewById(R.id.tv_project_two);
        tvmaj33 = (TextView) vi3.findViewById(R.id.tv_project_three);
        tvmaj43 = (TextView) vi3.findViewById(R.id.tv_project_four);
        tv_zy1_bao3 = (TextView) vi3.findViewById(R.id.tv_zy1_bao);
        tv_zy2_bao3 = (TextView) vi3.findViewById(R.id.tv_zy2_bao);
        tv_zy3_bao3 = (TextView) vi3.findViewById(R.id.tv_zy3_bao);
        tv_zy4_bao3 = (TextView) vi3.findViewById(R.id.tv_zy4_bao);
        tvName3 = (TextView) vi3.findViewById(R.id.tv_name);
        tvsign3 = (TextView) vi3.findViewById(R.id.tv_qianming);
        tv_titl3 = (TextView) vi3.findViewById(R.id.tv_titl);
        btnDh3 = (Button) vi3.findViewById(R.id.btn_daohang);
        btnXq3 = (Button) vi3.findViewById(R.id.btn_xiangqing);
        ivAvatar3 = (CircleImageView) vi3.findViewById(R.id.iv_head);
        ivSex3 = (ImageView) vi3.findViewById(R.id.iv_sex);
        iv_zy1_tupian3 = (TextView) vi3.findViewById(R.id.iv_zy1_tupian);
        iv_zy2_tupian3 = (TextView) vi3.findViewById(R.id.iv_zy2_tupian);
        iv_zy3_tupian3 = (TextView) vi3.findViewById(R.id.iv_zy3_tupian);
        iv_zy4_tupian3 = (TextView) vi3.findViewById(R.id.iv_zy4_tupian);
        tv_company_count3 = (TextView) vi3.findViewById(R.id.tv_company_count);
        tv_company3 = (TextView) vi3.findViewById(R.id.tv_company);
    }

    private boolean isWifiConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                isWifiConnected = true;
            } else {
                isWifiConnected = false;
            }
        }
        return isWifiConnected;
    }

    private String jisuanRemark() {
        String remark = "01";
        double lat = Double.parseDouble(qiyeLat);
        double lon = Double.parseDouble(qiyeLon);
        LatLng ll = JSONUtil.convertGPSToBaidu(new LatLng(lat, lon));
        double distance = DistanceUtil.getDistance(ll, new LatLng(mLat1, mLon1));
        String nowTime = getcurrentTime();
        nowTime = nowTime.substring(8, 10) + ":" + nowTime.substring(10, 12);
        double nowtime1 = Double.parseDouble(nowTime.substring(0, 2));
        double nowtime2 = Double.parseDouble(nowTime.substring(3, 5));
        if (btnDh.getText().toString().trim().equals("??????")) {
            if (shangbanTime != null) {
                double shbTime1 = Double.parseDouble(shangbanTime.substring(0, 2));
                double shbTime2 = Double.parseDouble(shangbanTime.substring(3, 5));
                if ((shbTime1 - nowtime1) * 60 + (shbTime2 - nowtime2) > 0) {
                    if (distance <= 500) {
                        remark = "01";
                    } else {
                        isdistance = true;
                        remark = "02";
                    }
                } else {
                    remark = "02";
                }
            } else {
                ToastUtils.showNOrmalToast(BaiDuQiyeLocationActivity.this.getApplicationContext(),"?????????????????????");

            }

        } else {
            double xbTime1 = Double.parseDouble(xiabanTime.substring(0, 2));
            double xbTime2 = Double.parseDouble(xiabanTime.substring(3, 5));
            if ((xbTime1 - nowtime1) * 60 + (xbTime2 - nowtime2) < 0) {
                remark = "01";
            } else {
                remark = "05";
            }
        }
        return remark;
    }

    private void showDialog1(final String str) {
        LayoutInflater inflaterDl = LayoutInflater.from(BaiDuQiyeLocationActivity.this);
        RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_moddel_dialog, null);
        final Dialog dialoga = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this).create();
        dialoga.show();
        dialoga.getWindow().setContentView(layout);
        dialoga.setCanceledOnTouchOutside(true);
        TextView tv_item1 = (TextView) dialoga.findViewById(R.id.tv_item1);
        TextView tv_item2 = (TextView) dialoga.findViewById(R.id.tv_item2);
        RelativeLayout re_item1 = (RelativeLayout) dialoga.findViewById(R.id.re_item1);
        final RelativeLayout re_item2 = (RelativeLayout) dialoga.findViewById(R.id.re_item2);
        final RelativeLayout re_item3 = (RelativeLayout) dialoga.findViewById(R.id.re_item3);
        tv_item1.setText("????????????");
        tv_item2.setText("????????????");
        re_item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFenxiang) {
                    dialoga.dismiss();
                    showTishi("????????????");
                } else {
                    dialoga.dismiss();
                    showdwShbTishi(str);
                }
            }
        });
        re_item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFenxiang) {
                    dialoga.dismiss();
                    showTishi("????????????");
                } else {
                    dialoga.dismiss();
                    showdshShbTishi();
                }
            }
        });
        re_item3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialoga.dismiss();
            }
        });
    }

    private void fenxiang(final String zhuangtai) {
        ScreenshotUtil.getBitmapByView(BaiDuQiyeLocationActivity.this, findViewById(R.id.ll1), "??????????????????", null,6,false,0,0);
        LayoutInflater inflater5 = LayoutInflater.from(BaiDuQiyeLocationActivity.this);
        final RelativeLayout layout5 = (RelativeLayout) inflater5.inflate(R.layout.dialog_fenxiang, null);
        final Dialog dialog = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this).create();
        dialog.show();
        Window window = dialog.getWindow();
        dialog.show();
        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        //??????????????????dialog??????????????????
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.getWindow().setContentView(layout5);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        TextView tv4 = (TextView) layout5.findViewById(R.id.tv4);
        TextView tv5 = (TextView) layout5.findViewById(R.id.tv5);
        RelativeLayout rl1 = (RelativeLayout) layout5.findViewById(R.id.rl1);
        RelativeLayout rl2 = (RelativeLayout) layout5.findViewById(R.id.rl2);
        RelativeLayout rl3 = (RelativeLayout) layout5.findViewById(R.id.rl3);
        RelativeLayout rl4 = (RelativeLayout) layout5.findViewById(R.id.rl4);
        RelativeLayout rl5 = (RelativeLayout) layout5.findViewById(R.id.rl5);
        RelativeLayout rl6 = (RelativeLayout) layout5.findViewById(R.id.rl6);
        rl6.setVisibility(View.INVISIBLE);
        rl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fxtoqqz(zhuangtai);
            }
        });
        rl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fxtowxm(zhuangtai);
            }
        });
        rl3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fxtowb(zhuangtai);
            }
        });
        rl4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fxtoqqf(zhuangtai);
            }
        });
        rl5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fxtowxf(zhuangtai);
            }
        });
    }

    private void fxtoqqf(final String zhuangtai) {
        QQ.ShareParams sp = new QQ.ShareParams();
        sp.setText(null);
        sp.setTitle(null);
        sp.setTitleUrl(null);
        sp.setImagePath(Environment.getExternalStorageDirectory()+"/zhengshier/fenxiang/mingpCut.png");
        Platform qq = ShareSDK.getPlatform(QQ.NAME);
// ????????????????????????????????????????????????????????????????????????????????????????????????????????????UI?????????
        qq.setPlatformActionListener(new PlatformActionListener() {
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                showQdDialog(zhuangtai);
            }

            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                showQdDialog(zhuangtai);
            }

            public void onCancel(Platform arg0, int arg1) {
                Toast.makeText(BaiDuQiyeLocationActivity.this, "????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        });
// ??????????????????
        qq.share(sp);
    }

    private void fxtoqqz(final String zhuangtai) {
        QZone.ShareParams sp = new QZone.ShareParams();
        sp.setText(null);
        sp.setTitle(null);
        sp.setTitleUrl(null);
        sp.setImagePath(Environment.getExternalStorageDirectory()+"/zhengshier/fenxiang/mingpCut.png");
        Platform qqz = ShareSDK.getPlatform(QZone.NAME);
// ????????????????????????????????????????????????????????????????????????????????????????????????????????????UI?????????
        qqz.setPlatformActionListener(new PlatformActionListener() {
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                showQdDialog(zhuangtai);
            }

            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                showQdDialog(zhuangtai);
            }

            public void onCancel(Platform arg0, int arg1) {
                Toast.makeText(BaiDuQiyeLocationActivity.this, "????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        });
// ??????????????????
        qqz.share(sp);
    }

    private void fxtowxm(final String zhuangtai) {
        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        sp.setShareType(Platform.SHARE_IMAGE);
        sp.setTitle("?????????app");
        sp.setImagePath(Environment.getExternalStorageDirectory()+"/zhengshier/fenxiang/mingpCut.png");
        Platform wx = ShareSDK.getPlatform(WechatMoments.NAME);
// ????????????????????????????????????????????????????????????????????????????????????????????????????????????UI?????????
        wx.setPlatformActionListener(new PlatformActionListener() {
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                showQdDialog(zhuangtai);
            }

            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                showQdDialog(zhuangtai);
            }

            public void onCancel(Platform arg0, int arg1) {
                Toast.makeText(BaiDuQiyeLocationActivity.this, "????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        });
// ??????????????????
        wx.share(sp);
    }

    private void fxtowxf(final String zhuangtai) {
        Wechat.ShareParams sp = new Wechat.ShareParams();
        sp.setShareType(Platform.SHARE_IMAGE);
        sp.setTitle("?????????app");
        sp.setImagePath(Environment.getExternalStorageDirectory()+"/zhengshier/fenxiang/mingpCut.png");
        Platform wx = ShareSDK.getPlatform(Wechat.NAME);
// ????????????????????????????????????????????????????????????????????????????????????????????????????????????UI?????????
        wx.setPlatformActionListener(new PlatformActionListener() {
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                showQdDialog(zhuangtai);
            }

            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                showQdDialog(zhuangtai);
            }

            public void onCancel(Platform arg0, int arg1) {
                Toast.makeText(BaiDuQiyeLocationActivity.this, "????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        });
// ??????????????????
        wx.share(sp);
    }

    private void fxtowb(final String zhuangtai) {
        SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
        sp.setShareType(Platform.SHARE_IMAGE);
        sp.setTitle("?????????app");
        sp.setImagePath(Environment.getExternalStorageDirectory()+"/zhengshier/fenxiang/mingpCut.png");
        Platform wb = ShareSDK.getPlatform(SinaWeibo.NAME);
// ????????????????????????????????????????????????????????????????????????????????????????????????????????????UI?????????
        wb.setPlatformActionListener(new PlatformActionListener() {
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                showQdDialog(zhuangtai);
            }

            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                showQdDialog(zhuangtai);
            }

            public void onCancel(Platform arg0, int arg1) {
                Toast.makeText(BaiDuQiyeLocationActivity.this, "????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        });
// ??????????????????
        wb.share(sp);
    }

    private void showQdDialog(final String zhuangtai) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(BaiDuQiyeLocationActivity.this);
        builder.setMessage("????????????????????????");
        builder.setTitle("????????????");
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
                if ("????????????".equals(zhuangtai)) {
                    showdwShbTishi("??????");
                } else if ("????????????".equals(zhuangtai)) {
                    showXbTishi();
                } else if ("????????????".equals(zhuangtai)) {
                    showdshShbTishi();
                }
            }
        });
        builder.show();
    }

    private void qiandao(final String remark) {
        final String qiyeId = DemoApplication.getInstance().getCurrentQiYeId();
        final String userId = DemoHelper.getInstance().getCurrentUsernName();
        String clockType = "01";//??????????????????
        if (btnDh.getText().toString().trim().equals("??????")) {
            clockType = "01";
        } else {
            clockType = "00";
        }
        String url = FXConstant.URL_YUGONG_QIANDAO;
        final String finalClockType = clockType;
        StringRequest qDrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("chen", "??????" + s);

                Toast.makeText(BaiDuQiyeLocationActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                if ("02".equals(remark)) {
                    tongji(remark, qiyeId, userId);
                } else if (shBzhutai.equals("????????????")&&"01".equals(signRemark)) {
                    tongji("06", qiyeId, userId);
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("??????????????????", volleyError + "");
                Toast.makeText(BaiDuQiyeLocationActivity.this, "??????????????????...", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("companyId", qiyeId);
                params.put("userId", userId);
                params.put("remark", remark);
                params.put("clockType", finalClockType);
                params.put("latitude", mLat1 + "");
                params.put("longitude", mLon1 + "");
                return params;
            }
        };
        MySingleton.getInstance(BaiDuQiyeLocationActivity.this).addToRequestQueue(qDrequest);
    }

    private void tongji(final String remark, final String qiyeId, final String userId) {
        String url = FXConstant.URL_QIYE_YUANGONGTONGJI;
        StringRequest qDrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                setResult(RESULT_OK);
              //  finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("qiandao,volleyError", volleyError.toString());
                Toast.makeText(BaiDuQiyeLocationActivity.this, "??????????????????...", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("companyId", qiyeId);
                params.put("userId", userId);
                if ("02".equals(remark)) {
                    params.put("lateTimes", "1");
                } else if ("06".equals(remark)) {
                    params.put("resv1", "1");
                }
                return params;
            }
        };
        MySingleton.getInstance(BaiDuQiyeLocationActivity.this).addToRequestQueue(qDrequest);
    }

    public String getcurrentTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(date);
    }

    private void initMapView() {
        mMapView.setLongClickable(true);
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        if (mLocClient != null) {
            mLocClient.stop();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // ??????????????????
        super.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        if (option.isOpenGps()) {
            option.setOpenGps(false);
        }
        if (mLocClient != null) {
            mLocClient.stop();
            mLocClient = null;
        }
        if (oa != null) {
            oa.clear();
            oa = null;
        }
        if (strLat != null) {
            strLat.clear();
            strLat = null;
        }
        if (strName != null) {
            strName.clear();
            strName = null;
        }
        if (strLoginId != null) {
            strLoginId.clear();
            strLoginId = null;
        }
        if (strLong != null) {
            strLong.clear();
            strLong = null;
        }
        if (strSex != null) {
            strSex.clear();
            strSex = null;
        }
    }

    public void back(View v) {
        setResult(RESULT_OK);
        finish();
    }
}
package com.sangu.apptongji.main.mapsearch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.permissionutil.PermissionListener;
import com.permissionutil.PermissionUtil;
import com.sangu.apptongji.DemoApplication;
import com.sangu.apptongji.R;
import com.sangu.apptongji.main.utils.JSONUtil;
import com.sangu.apptongji.main.utils.ToastUtils;
import com.sangu.apptongji.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import static com.sangu.apptongji.main.mapsearch.ViewUtils.goneView;
import static com.sangu.apptongji.main.mapsearch.ViewUtils.showView;

public class MapPickerActivity extends BaseActivity implements
        AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener {

    private static final String LOG_TAG = "MapPickerActivity";

    private ListView list;
    private TextView status;
    private ProgressBar loading;
    private View defineMyLocationButton;
    private RelativeLayout container;
    private LinearLayout ll_search;
    private Button btn_commit;

    //??????????????????
    private LocationService locationService;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    // ??????????????????????????????
    private LatLng mLoactionLatLng;
    private String mAddress;
    private String mStreet;
    private String mName;
    private String mCity;
    private String district;
    private String biaoshi;
    // ???????????????????????????
    private boolean isFirstLoc = true;
    private boolean isClickItem = false;
    // MapView???????????????????????????
    private Point mCenterPoint = null;
    // ????????????
    private GeoCoder mGeoCoder = null;
    // ????????????
    MapPickerAdapter mAdapter;
    ArrayList<PoiInfo> mInfoList;
    PoiInfo mCurentInfo;


    private ListView searchList;
    private View searchContainer;
    private TextView searchEmptyView;
    private TextView searchHintView;

    private boolean isSearchVisible = false;
    private MapPickerSearchAdapter searchAdapter;
    private List<LocationBean> searchDisplay;
    private EditText searchView;
    private MenuItem searchMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_activity_map_picker);
        PermissionUtil permissionUtil = new PermissionUtil(MapPickerActivity.this);
        permissionUtil.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                new PermissionListener() {
                    @Override
                    public void onGranted() {
                        //???????????????????????????
                    }
                    @Override
                    public void onDenied(List<String> deniedPermission) {
                        //Toast???????????????????????????
                        Toast.makeText(getApplicationContext(),"?????????????????????????????????",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    @Override
                    public void onShouldShowRationale(List<String> deniedPermission) {
                        //Toast???????????????????????????
                        Toast.makeText(getApplicationContext(),"?????????????????????????????????????????????????????????????????????",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
        initMap();
        findViewById(R.id.root).setBackgroundColor(Color.parseColor("#ffffff"));
        biaoshi = getIntent().getStringExtra("biaoshi");
        defineMyLocationButton = findViewById(R.id.define_my_location);
        defineMyLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnBack();
            }
        });
        btn_commit = (Button) findViewById(R.id.btn_commit);
        searchList = (ListView) findViewById(R.id.searchList);
        searchView = (EditText) findViewById(R.id.et_search);
        container = (RelativeLayout) findViewById(R.id.container);
        ll_search = (LinearLayout) findViewById(R.id.ll_search);
        searchContainer = findViewById(R.id.searchCont);
        searchContainer.setBackgroundColor(Color.parseColor("#ffffff"));
        searchEmptyView = (TextView) findViewById(R.id.empty);
        searchHintView = (TextView) findViewById(R.id.searchHint);
        searchEmptyView.setTextColor(Color.parseColor("#7A000000"));
        searchHintView.setTextColor(Color.parseColor("#7A000000"));
        searchHintView.setVisibility(View.GONE);
        searchEmptyView.setVisibility(View.GONE);
        ll_search.setFocusable(true);
        ll_search.setFocusableInTouchMode(true);
        ll_search.requestFocus();
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchView.getText().toString().trim().length() > 0) {
                    isSearchVisible = false;
                    showSearch();
                    searchPlaces(mCity,searchView.getText().toString().trim(), 0);
                }
            }
        });
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                if (cs.toString().trim().length() > 0) {
                    isSearchVisible = false;
                    showSearch();
                    searchPlaces(mCity, cs.toString(), 0);
                } else {
                    if (searchDisplay != null) {
                        searchDisplay.clear();
                        searchAdapter.notifyDataSetChanged();
                    }
                    showView(container, false);
                    goneView(searchContainer, false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                isClickItem = false;
                BitmapDescriptor mSelectIco = BitmapDescriptorFactory
                        .fromResource(R.drawable.picker_map_geo_icon);
                mBaiduMap.clear();
                LocationBean info = (LocationBean) searchAdapter.getItem(position);
                LatLng la = new LatLng(info.getLatitude(),info.getLongitude());
                // ????????????
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(la);
                mBaiduMap.animateMapStatus(u);
                // ???????????????
                OverlayOptions ooA = new MarkerOptions().position(la)
                        .icon(mSelectIco).anchor(0.5f, 0.5f);
                mBaiduMap.addOverlay(ooA);
                mLoactionLatLng = la;
                district = info.getDistrict();
                mName = info.getLocName();
                mStreet = info.getStreet();
                mCity = info.getCity();
                mAddress = mCity + district + mStreet;
                // ???????????????????????????
                mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption())
                        .location(la));
                loading.setVisibility(View.VISIBLE);
                if(isSearchVisible){
                    hideKeyBoard();
                    hideSearch();
                }
            }
        });
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isClickItem){
                    if (mInfoList == null || mInfoList.size() == 0) {
                        ToastUtils.showNOrmalToast(MapPickerActivity.this.getApplicationContext(), "?????????????????????????????????????????????");
                        return;
                    }
                    PoiInfo info = mInfoList.get(0);
                    mLoactionLatLng = info.location;
                    mCity = info.city;
                    mAddress = info.address;
                    mName = info.name;
                }
                if (mLoactionLatLng != null) {
                    double [] latlng = JSONUtil.bd09togcj02(mLoactionLatLng.longitude,mLoactionLatLng.latitude);
                    String lat = String.format("%.6f", latlng[1]);
                    String lon = String.format("%.6f", latlng[0]);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("latitude", lat);
                    returnIntent.putExtra("longitude", lon);
                    returnIntent.putExtra("city", mCity);
                    returnIntent.putExtra("street", mAddress);
                    returnIntent.putExtra("place", mName);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    private void initMap() {
        //ricky init baidumap begin
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mMapView.showZoomControls(false);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setOnMapTouchListener(touchListener);
        // ?????????POI????????????
        mInfoList = new ArrayList<PoiInfo>();
        // ???????????????MapView????????????????????????????????????????????????
        mCenterPoint = mBaiduMap.getMapStatus().targetScreen;
        mLoactionLatLng = mBaiduMap.getMapStatus().target;
        // ??????
        mBaiduMap.setMyLocationEnabled(true);
        // ????????????logo ZoomControl
        int count = mMapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ImageView || child instanceof ZoomControls) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        // ???????????????
        //mMapView.showScaleControl(false);
        // ????????????
        mGeoCoder = GeoCoder.newInstance();
        mGeoCoder.setOnGetGeoCodeResultListener(GeoListener);
        list = (ListView) findViewById(R.id.list);
        list.setOnScrollListener(this);
        list.setOnItemClickListener(this);
        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        loading = (ProgressBar) findViewById(R.id.loading);
        status = (TextView) findViewById(R.id.status);
        mAdapter = new MapPickerAdapter(MapPickerActivity.this, mInfoList);
        list.setAdapter(mAdapter);
    }

    private void showSearch() {
        if (isSearchVisible) {
            return;
        }
        isSearchVisible = true;

        searchDisplay = new ArrayList<>();
        searchAdapter = new MapPickerSearchAdapter(this, searchDisplay);

        searchList.setAdapter(searchAdapter);

        showView(searchHintView, false);
        goneView(searchEmptyView, false);
        goneView(container,false);
        showView(searchContainer);
    }

    private void hideSearch() {
        if (!isSearchVisible) {
            return;
        }
        isSearchVisible = false;

        if (searchDisplay != null) {
            searchDisplay.clear();
            searchDisplay = null;
        }
        searchAdapter = null;
        searchList.setAdapter(null);
        showView(container,false);
        goneView(searchContainer);

        if (searchMenu != null) {
            if (searchMenu.isActionViewExpanded()) {
                searchMenu.collapseActionView();
            }
        }
    }

    public void turnBack() {
        MyLocationData location = mBaiduMap.getLocationData();
        // ??????????????????
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(new LatLng(location.latitude, location.longitude));
        mBaiduMap.animateMapStatus(u);
        mBaiduMap.clear();
        // ???????????????????????????
        mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption())
                .location(new LatLng(location.latitude, location.longitude)));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        if (id == R.id.menu_send) {
            if (mLoactionLatLng != null) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("latitude", mLoactionLatLng.latitude);
                returnIntent.putExtra("longitude", mLoactionLatLng.longitude);
                returnIntent.putExtra("street", mAddress);
                returnIntent.putExtra("place", mName);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isSearchVisible) {
            hideSearch();
        }
        super.onBackPressed();
    }

    private void searchPlaces(String cityName, final String keyName,
                              int pageNum) {
        SearchPoiUtil.getPoiByPoiSearch(cityName,
                keyName, pageNum,
                new SearchPoiUtil.PoiSearchListener() {

                    @Override
                    public void onGetSucceed(List<LocationBean> locationList,
                                             PoiResult res) {
                        if (keyName.length() > 0) {
                            if (locationList.size() > 0) {
                                goneView(searchEmptyView);
                                goneView(searchHintView);
                            } else {
                                goneView(searchHintView);
                                showView(searchEmptyView);
                            }
                            if (searchDisplay == null) {
                                searchDisplay = new ArrayList<LocationBean>();
                            }
                            searchDisplay.clear();
                            searchDisplay.addAll(locationList);
                            searchAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onGetFailed() {
                        if (searchDisplay != null) {
                            searchDisplay.clear();
                            searchAdapter.notifyDataSetChanged();
                        }
                        goneView(searchHintView);
                        showView(searchEmptyView);
                        Toast.makeText(MapPickerActivity.this, "???????????????????????????",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void hideKeyBoard() {
//        searchView.clearFocus();
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        View focusedView = this.getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        isClickItem = true;
        // ?????????????????????position???item????????????
        mAdapter.setNotifyTip(position);
        mAdapter.notifyDataSetChanged();
        BitmapDescriptor mSelectIco = BitmapDescriptorFactory
                .fromResource(R.drawable.picker_map_geo_icon);
        mBaiduMap.clear();
        PoiInfo info = (PoiInfo) mAdapter.getItem(position);
        LatLng la = info.location;
        // ????????????
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(la);
        mBaiduMap.animateMapStatus(u);
        // ???????????????
        OverlayOptions ooA = new MarkerOptions().position(la)
                .icon(mSelectIco).anchor(0.5f, 0.5f);
        mBaiduMap.addOverlay(ooA);
        mLoactionLatLng = info.location;
        mAddress = info.address;
        mName = info.name;
        mStreet = info.address;
        mCity = info.city;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        switch (i) {
            case SCROLL_STATE_TOUCH_SCROLL:
                hideKeyBoard();
                break;
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {

    }

    /***
     * Stop location service
     */
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        locationService.stop(); // ??????????????????
        super.onStop();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // -----------location config ------------
        locationService = DemoApplication.getInstance().locationService;
        // ??????locationservice????????????????????????????????????1???location???????????????????????????????????????????????????activity?????????????????????????????????locationservice?????????
        locationService.registerListener(mListener);
        // ????????????
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());

    }

    @Override
    protected void onResume() {
        super.onResume();
        //???activity??????onResume?????????mMapView. onResume ()?????????????????????????????????
        locationService.start();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //???activity??????onDestroy?????????mMapView.onDestroy()?????????????????????????????????
        locationService.unregisterListener(mListener); // ???????????????
        locationService.stop();
        mMapView.onDestroy();
        mGeoCoder.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //???activity??????onPause?????????mMapView. onPause ()?????????????????????????????????
        locationService.stop();
        mMapView.onPause();
        hideSearch();
    }

    /*****
     * ???????????????????????????onReceiveLocation???????????????????????????????????????????????????????????????
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                //

                MyLocationData data = new MyLocationData.Builder()//
                        // .direction(mCurrentX)//
                        .accuracy(location.getRadius())//
                        .latitude(location.getLatitude())//
                        .longitude(location.getLongitude())//
                        .build();
                mBaiduMap.setMyLocationData(data);
                // ?????????????????????
                MyLocationConfiguration config = new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.NORMAL, true, null);
                mBaiduMap.setMyLocationConfigeration(config);
                district = location.getDistrict();
                mName = location.getStreet();
                mStreet = location.getStreet();
                mCity = location.getCity();
                mAddress = mCity + district + mStreet;
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mLoactionLatLng = currentLatLng;
                // ?????????????????????
                if (isFirstLoc) {
                    isFirstLoc = false;
                    // ??????????????????
                    MapStatusUpdate u = MapStatusUpdateFactory
                            .newLatLng(currentLatLng);
                    mBaiduMap.animateMapStatus(u);
                    mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption())
                            .location(currentLatLng));
                    return;
                }
            }

        }

    };
    // ?????????????????????
    OnGetGeoCoderResultListener GeoListener = new OnGetGeoCoderResultListener() {
        public void onGetGeoCodeResult(GeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // ?????????????????????
            }
            // ????????????????????????
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // ????????????????????????
                status.setText(R.string.picker_internalerror);
                status.setVisibility(View.VISIBLE);
            }
            // ??????????????????????????????
            else {
                status.setVisibility(View.GONE);
                // ??????????????????
                mLoactionLatLng = result.getLocation();
                district = result.getAddressDetail().district;
                mName = result.getAddressDetail().street;
                mStreet = result.getAddressDetail().street;
                mCity = result.getAddressDetail().city;
                mAddress = mCity + district + mStreet;
                mCurentInfo = new PoiInfo();
                mCurentInfo.address = result.getAddress();
                mCurentInfo.location = result.getLocation();
                mCurentInfo.name = "[??????]";
                mInfoList.clear();
                mInfoList.add(mCurentInfo);
                // ????????????????????????
                if (result.getPoiList() != null) {
                    mInfoList.addAll(result.getPoiList());
                }
                mAdapter.setNotifyTip(0);
                // ???????????????????????????
                mAdapter.notifyDataSetChanged();
                loading.setVisibility(View.GONE);
            }
        }
    };
    // ???????????????????????????
    BaiduMap.OnMapTouchListener touchListener = new BaiduMap.OnMapTouchListener() {
        @Override
        public void onTouch(MotionEvent event) {
            isClickItem = false;
            // TODO Auto-generated method stub
            if (event.getAction() == MotionEvent.ACTION_UP) {

                if (mCenterPoint == null) {
                    return;
                }
                // ????????????MapView???????????????????????????????????????
                LatLng currentLatLng;
                currentLatLng = mBaiduMap.getProjection().fromScreenLocation(
                        mCenterPoint);
                // ???????????????????????????
                mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption())
                        .location(currentLatLng));
                loading.setVisibility(View.VISIBLE);

            }
        }
    };
}

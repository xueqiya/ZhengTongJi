package com.sangu.apptongji.main.alluser.order.avtivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.sangu.apptongji.R;
import com.sangu.apptongji.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-11-25.
 */

public class PoiSearchActivity extends BaseActivity implements
        OnGetPoiSearchResultListener, OnGetSuggestionResultListener ,OnGetGeoCoderResultListener {
    private BaiduMap mBaiduMap = null;
    private String lat,lng,lat1,lng1;
    private PoiSearch mPoiSearch = null;
    private GeoCoder mSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    private List<String> suggest;
    private EditText editCity = null;
    private Button btn_commit;
    private AutoCompleteTextView keyWorldsView = null;
    private ArrayAdapter<String> sugAdapter = null;
    private int loadIndex = 0;
    private Handler myhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Intent intent = new Intent();
                    intent.putExtra("city",editCity.getText().toString().trim());
                    intent.putExtra("adress",keyWorldsView.getText().toString().trim());
                    intent.putExtra("lat1",lat1);
                    intent.putExtra("lng1",lng1);
                    setResult(RESULT_OK,intent);
                    finish();
                    break;
            }
        }
    };
    int searchType = 0;  // ????????????????????????????????????
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_poisearch);
        // ????????????????????????????????????????????????
        lat = this.getIntent().getStringExtra("lat");
        lng = this.getIntent().getStringExtra("lng");
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        // ????????????????????????????????????????????????????????????
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);

        editCity = (EditText) findViewById(R.id.city);
        btn_commit = (Button) findViewById(R.id.btn_commit);
        keyWorldsView = (AutoCompleteTextView) findViewById(R.id.searchkey);
        sugAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line);
        keyWorldsView.setAdapter(sugAdapter);
        keyWorldsView.setThreshold(1);
        mBaiduMap = ((SupportMapFragment) (getSupportFragmentManager()
                .findFragmentById(R.id.map))).getBaiduMap();
        LatLng cenpt = new LatLng(Double.valueOf(lat),Double.valueOf(lng));
        //??????????????????
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(18)
                .build();
        //??????MapStatusUpdate??????????????????????????????????????????????????????
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //??????????????????
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        /**
         * ??????????????????????????????????????????????????????
         */
        keyWorldsView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                if (cs.length() <= 0) {
                    return;
                }
                /**
                 * ??????????????????????????????????????????????????????onSuggestionResult()?????????
                 */
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(cs.toString()).city(editCity.getText().toString()));
            }
        });
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearch.geocode(new GeoCodeOption().city(
                        editCity.getText().toString()).address(keyWorldsView.getText().toString()));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void searchButtonProcess(View v) {
        searchType = 1;
        String citystr = editCity.getText().toString();
        String keystr = keyWorldsView.getText().toString();
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(citystr).keyword(keystr).pageNum(loadIndex));
    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(PoiSearchActivity.this, "???????????????", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();
//            switch( searchType ) {
//                case 2:
//                    showNearbyArea(center, radius);
//                    break;
//                case 3:
//                    showBound(searchbound);
//                    break;
//                default:
//                    break;
//            }
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
            // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            String strInfo = "???";
            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "????????????";
            Toast.makeText(PoiSearchActivity.this, strInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(PoiSearchActivity.this, "????????????????????????", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(PoiSearchActivity.this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    /**
     * ???????????????????????????????????????requestSuggestion?????????????????????
     * @param res
     */
    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }
        suggest = new ArrayList<String>();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null) {
                suggest.add(info.key);
            }
        }
        sugAdapter = new ArrayAdapter<String>(PoiSearchActivity.this, android.R.layout.simple_dropdown_item_1line, suggest);
        keyWorldsView.setAdapter(sugAdapter);
        sugAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(PoiSearchActivity.this, "???????????????????????????", Toast.LENGTH_LONG)
                    .show();
            return;
        }
//        mBaiduMap.clear();
//        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
//                .icon(BitmapDescriptorFactory
//                        .fromResource(R.drawable.icon_marka)));
//        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
//                .getLocation()));
//        String strInfo = String.format("?????????%f ?????????%f",
//                result.getLocation().latitude, result.getLocation().longitude);

//        Toast.makeText(PoiSearchActivity.this, strInfo, Toast.LENGTH_LONG).show();
        lat1 = String.valueOf(result.getLocation().latitude);
        lng1 = String.valueOf(result.getLocation().longitude);
        myhandler.sendEmptyMessage(0);
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

    }

    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(poi.uid));
            // }
            return true;
        }
    }

}

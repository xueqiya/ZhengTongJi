package com.sangu.apptongji.main.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.fanxin.easeui.utils.UserManager;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.sangu.apptongji.DemoApplication;
import com.sangu.apptongji.DemoHelper;
import com.sangu.apptongji.R;
import com.sangu.apptongji.db.DemoDBManager;
import com.sangu.apptongji.main.FXConstant;
import com.sangu.apptongji.main.alluser.entity.Userful;
import com.sangu.apptongji.main.alluser.order.avtivity.WJDLPaActivity;
import com.sangu.apptongji.main.fragment.MainActivity;
import com.sangu.apptongji.main.fragment.MainTwoActivity;
import com.sangu.apptongji.main.service.ContactsService;
import com.sangu.apptongji.main.utils.JSONParser;
import com.sangu.apptongji.main.utils.MySingleton;
import com.sangu.apptongji.ui.BaseActivity;
import com.xiaomi.mimc.MIMCUser;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Login screen
 */
public class LoginActivity extends BaseActivity{
    private static final String TAG = "LoginActivity";
    private EditText et_usertel=null;
    private EditText et_password=null;
    private TextView tv_wenti=null;
    private boolean autoLogin = false;
    private Button btn_login=null;
    private Button btn_qtlogin=null;
    LocationClient mLocClient=null;
    LocationClientOption option=null;
    private RelativeLayout rl_duanxin;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode=null;
    private String lng=null,lat=null,date=null, uLocation=null,city=null;
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view ???????????????????????????????????????
            if (location == null) {
                return;
            }
            lng = "" + location.getLongitude();
            lat = "" + location.getLatitude();
            String location1 = null;
            String province = location.getProvince();
            city = location.getCity();
            String district = location.getDistrict();
            String street = location.getStreet();
            if (city!=null&&!"null".equals(city)){
                location1 = city;
            }
            if (district!=null&&!"null".equals(district)){
                location1 = location1 + district;
            }
            if (street!=null&&!"null".equals(street)){
                location1 = location1 + street;
            }
            SharedPreferences mSharedPreferences = getSharedPreferences("sangu_dingwei_city", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("city",city);
            editor.putString("location",location1);
            editor.commit();
            if (province!=null&&!"null".equals(province)){
                uLocation = province;
            }
            if (city!=null&&!"null".equals(city)){
                uLocation = uLocation + city;
            }
            if (district!=null&&!"null".equals(district)){
                uLocation = uLocation + district;
            }
            if (street!=null&&!"null".equals(street)){
                uLocation = uLocation + street;
            }
            DemoApplication.getApp().saveCurrentLng(lng);
            DemoApplication.getApp().saveCurrentLat(lat);
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    protected void onDestroy() {
        if (mLocClient != null) {
            mLocClient.stop();
            mLocClient = null;
        }
        if (option !=null ){
            option.setOpenGps(false);
            option = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SDKInitializer.initialize(getApplicationContext());
        if (DemoHelper.getInstance().isLoggedIn(this)) {
            autoLogin = true;
            SharedPreferences sp = getSharedPreferences("sangu_denglu_info", Context.MODE_PRIVATE);
            String userId = sp.getString("userId","");
            String passWord = sp.getString("passWord","");
            String qiyeId = sp.getString("qiyeId","");
            if (userId!=null&&!"".equals(userId)) {
                loginInSever(userId, passWord,qiyeId);
            }else {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
            return;
        }
        setContentView(R.layout.fx_activity_login);
        RequestQueue queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        option = new LocationClientOption();
        option.setOpenGps(true); // ??????gps
        option.setCoorType("gcj02");
        option.setScanSpan(30000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        date = sDateFormat.format(curDate);
        rl_duanxin = (RelativeLayout) findViewById(R.id.rl_duanxin);
        et_usertel = (EditText) findViewById(R.id.et_usertel);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_qtlogin = (Button) findViewById(R.id.btn_qtlogin);
        tv_wenti = (TextView) findViewById(R.id.tv_wenti);
        // ?????????????????????
        TextChange textChange = new TextChange();
        et_usertel.addTextChangedListener(textChange);
        et_password.addTextChangedListener(textChange);
        // if user changed, clear the password
        et_usertel.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_password.setText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //TODO ???????????????????????????????????????
        if (DemoHelper.getInstance().getCurrentUsernName() != null) {
            et_usertel.setText(DemoHelper.getInstance().getCurrentUsernName());
        }
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String brand = android.os.Build.BRAND;
                final String model = android.os.Build.MODEL;
                String url = FXConstant.URL_UPDATE_TIME;
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Log.d("chen", "URL_UPDATE_TIME" + s);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        loadFriendlist(et_usertel.getText().toString(), et_password.getText().toString().trim());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        loadFriendlist(et_usertel.getText().toString(), et_password.getText().toString().trim());
                        Log.d("chen", "URL_UPDATE_TIME onErrorResponse" + volleyError.getMessage());
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("uLoginId",et_usertel.getText().toString());
                        params.put("loginTime",date);
                        if (uLocation!=null) {
                            params.put("uLocation", uLocation);
                        }
                        params.put("deviceType","android4.5?????????"+brand+model);
                        if (lng!=null) {
                            params.put("lng", lng);
                        }
                        if (lat!=null) {
                            params.put("lat", lat);
                        }
                        if (city!=null) {
                            params.put("region", city);
                        }
                        return params;
                    }
                };
                MySingleton.getInstance(LoginActivity.this).addToRequestQueue(request);
            }
        });
        btn_qtlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterOneActivity.class));
            }
        });
        tv_wenti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, WJDLPaActivity.class));
            }
        });
        rl_duanxin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,LoginWithDXActivity.class).putExtra("uLocation",uLocation)
                        .putExtra("lng",lng).putExtra("lat",lat).putExtra("city",city));
            }
        });
    }

    private void loadFriendlist(final String loginId,final String pass) {
        String url = FXConstant.URL_Get_UserInfo+loginId;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //Log.d("chen", "loadFriendlist" + s);
                try {
                    org.json.JSONObject jsonObject = new org.json.JSONObject(s);
                    String code = jsonObject.getString("code");
                    if ("???????????????".equals(code)){
                        Toast.makeText(getApplicationContext(),"??????????????????!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    org.json.JSONObject object = jsonObject.getJSONObject("userInfo");
                    DemoApplication app = DemoApplication.getApp();
                    Userful user = JSONParser.parseUser(object);
                    app.saveCurrentUser(user);
                    String qiyeId = user.getResv5();
                    String remark = user.getuNation();
                    String resv6 = user.getResv6();
                    String comAddress = user.getCompanyAdress();
                    String companyName = user.getCompany();
                    String locationState = user.getLocationState();
                    app.setCurrentQiYeRemark(remark);//?????????????????????
                    app.saveCurrentlocationState(locationState);
                    app.setCurrentCompanyName(companyName);
                    app.setCurrentcomAddress(comAddress);
                    app.setCurrentQiYeId(qiyeId);
                    app.setCurrentResv6(resv6);
                    loginInSever(loginId, pass,qiyeId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
               // Log.d("chen",""+volleyError.getMessage());
                Toast.makeText(getApplicationContext(),"??????????????????!",Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(LoginActivity.this).addToRequestQueue(request);
    }

    /**
     * ???????????????????????????????????????
     * @param
     */
    private void loginInSever(final String tel, final String password, final String qiyeId) {
        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(getString(R.string.Is_landing));
        if (!LoginActivity.this.isDestroyed()) {
            pd.show();
        }
        String url = FXConstant.URL_LOGIN;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    org.json.JSONObject object = new org.json.JSONObject(s);
                    String code = object.getString("code");
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    if(code.equals("SUCCESS")){
                        SharedPreferences sp = getSharedPreferences("sangu_denglu_info", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = sp.edit();
                        editor1.putBoolean("isLogedIn",true);
                        editor1.putString("userId",tel);
                        editor1.putString("passWord",password);
                        editor1.putString("qiyeId",qiyeId);
                        editor1.commit();
                        //finish();
                        loginHuanXin(tel, password);
                        loginMimcInfo(tel,password);
                    }else if (code.equals("1010")||code.equals("1011")) {
                        //pd.dismiss();
                        Toast.makeText(LoginActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                    }else if (code.equals("1009")) {
                        //pd.dismiss();
                        Toast.makeText(LoginActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show();
                    }else if (code.equals("???????????????????????????")){
                        Toast.makeText(LoginActivity.this, "?????????????????????,???????????????", Toast.LENGTH_SHORT).show();
                    }else {
                        //pd.dismiss();
                        Toast.makeText(LoginActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("uLoginId",tel);
                params.put("uPassword",password);
                return params;
            }
        };
        MySingleton.getInstance(LoginActivity.this).addToRequestQueue(request);
    }

    private void loginMimcInfo(String usertel,String password){

        MIMCUser user = UserManager.getInstance().newUser(usertel,LoginActivity.this);
        if (user != null) {

            user.login();

        }
    }

    private void loginHuanXin(String usertel,String password){
        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d(TAG, "EMClient.getInstance().onCancel");
            }
        });
        pd.setMessage(getString(R.string.Is_landing));
        if (pd!=null&&!pd.isShowing()) {
            pd.show();
        }
        // After logout???the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
        // close it before login to make sure DemoDB not overlap
        DemoDBManager.getInstance().closeDB();
        // reset current user name before login
        DemoHelper.getInstance().setCurrentUserName(usertel);
        final long start = System.currentTimeMillis();
        // ??????sdk?????????????????????????????????
        Log.d(TAG, "EMClient.getInstance().login");
        EMClient.getInstance().login(usertel, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "login: onSuccess");
                if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
                    pd.dismiss();
                }
                // ** ???????????????????????????logout?????????????????????????????????????????????
                // ** manually load all local groups and
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                // ?????????????????????nickname ????????????????????????ios?????????????????????????????????nick
//                if (!updatenick) {
//                    Log.e("LoginActivity", "update current user nick fail");
//                }
                startService(new Intent(LoginActivity.this, ContactsService.class));
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LoginActivity.this, "????????????", Toast.LENGTH_LONG).show();
                        if (pd!=null&&pd.isShowing()&&!LoginActivity.this.isDestroyed()){
                            pd.dismiss();
                        }
                        if (MainTwoActivity.instance!=null) {
                            MainTwoActivity.instance.finish();
                        }
                        // ???????????????
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "login: onProgress");
            }
            @Override
            public void onError(final int code, final String message) {
                Log.d(TAG, "login: onError: " + code);
                startService(new Intent(LoginActivity.this, ContactsService.class));
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LoginActivity.this, "????????????", Toast.LENGTH_LONG).show();
                        if (pd!=null&&pd.isShowing()){
                            pd.dismiss();
                        }
                        if (MainTwoActivity.instance!=null) {
                            MainTwoActivity.instance.finish();
                        }
                        // ???????????????
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocClient!=null){
            mLocClient.stop();
        }
    }

    // EditText?????????
    class TextChange implements TextWatcher {

        @Override
        public void afterTextChanged(Editable arg0) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before,
                                  int count) {

            boolean Sign2 = et_usertel.getText().length() >= 10;
            boolean Sign3 = et_password.getText().length() >= 3;

            if (Sign2 & Sign3) {
                btn_login.setEnabled(true);
            }
            // ???layout???????????????Button???text???????????????????????????????????????????????????????????????Button???????????????
            else {
                btn_login.setEnabled(false);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (autoLogin) {
            return;
        }
    }
}

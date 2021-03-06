package com.pintu.deutils.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pintu.deutils.adapter.GridItemsAdapter;
import com.pintu.deutils.bean.ItemBean;
import com.pintu.deutils.util.GameUtil;
import com.pintu.deutils.util.ImagesUtil;
import com.pintu.deutils.util.ScreenUtil;
import com.sangu.apptongji.DemoHelper;
import com.sangu.apptongji.R;
import com.sangu.apptongji.main.FXConstant;
import com.sangu.apptongji.main.utils.MySingleton;
import com.sangu.apptongji.main.utils.ScreenshotUtil;
import com.sangu.apptongji.main.utils.SoundPlayUtils;
import com.sangu.apptongji.ui.BaseActivity;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * ????????????????????????????????????
 *
 * @author xys
 */
public class PuzzleMain extends BaseActivity implements OnClickListener {

    // ??????????????????????????????????????????
    public static Bitmap mLastBitmap;
    // ?????????N*N??????
    public static int TYPE = 3;
//    // ????????????
//    public static int COUNT_INDEX = 0;
//    // ????????????
//    public static int TIMER_INDEX = 0;
    /**
     * UI??????Handler
     */
//    private Handler mHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 1:
//                    // ???????????????
//                    TIMER_INDEX++;
//                    mTvTimer.setText("" + TIMER_INDEX);
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
    // ???????????????
    private Bitmap mPicSelected;
    // PuzzlePanel
    private GridView mGvPuzzleMainDetail;
    private ImageView mImageView;
    // Button
    private Button mBtnBack;
    private Button mBtnImage;
    private Button mBtnRestart;
    //    // ????????????
//    private TextView mTvPuzzleMainCounts;
//    // ?????????
//    private TextView mTvTimer;
    // ??????????????????
    private List<Bitmap> mBitmapItemLists = new ArrayList<Bitmap>();
    // GridView?????????
    private GridItemsAdapter mAdapter;
    // Flag ?????????????????????
    private boolean mIsShowImg;
    //    // ????????????
//    private Timer mTimer;
//    /**
//     * ???????????????
//     */
//    private TimerTask mTimerTask;
    private String createTime,user_id,pintuUrl,dynamicSeq,gameRed,name;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.xpuzzle_puzzle_detail_main);
        RequestQueue queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        name = getIntent().getStringExtra("name");
        pintuUrl = getIntent().getStringExtra("pintuUrl");
        user_id = getIntent().getStringExtra("user_id");
        gameRed = getIntent().getStringExtra("gameRed");
        createTime = getIntent().getStringExtra("createTime");
        dynamicSeq = getIntent().getStringExtra("dynamicSeq");
        // ?????????????????????
        Bitmap picSelectedTemp;
        // ???????????????????????????????????????
        picSelectedTemp = getBitmapFromSharedPreferences();
        // ???????????????
        handlerImage(picSelectedTemp);
        // ?????????Views
        initViews();
        // ??????????????????
        generateGame();
        // GridView????????????
        mGvPuzzleMainDetail.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                // ?????????????????????
                if (GameUtil.isMoveable(position)) {
                    // ????????????Item??????????????????
                    GameUtil.swapItems(
                            GameUtil.mItemBeans.get(position),
                            GameUtil.mBlankItemBean);
                    // ??????????????????
                    recreateData();
                    // ??????GridView??????UI
                    mAdapter.notifyDataSetChanged();
                    // ????????????
//                    COUNT_INDEX++;
//                    mTvPuzzleMainCounts.setText("" + COUNT_INDEX);
                    // ??????????????????
                    if (GameUtil.isSuccess()) {
                        // ??????????????????????????????
                        recreateData();
                        mBitmapItemLists.remove(TYPE * TYPE - 1);
                        mBitmapItemLists.add(mLastBitmap);
                        // ??????GridView??????UI
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(PuzzleMain.this, "????????????!",
                                Toast.LENGTH_LONG).show();
                        mGvPuzzleMainDetail.setEnabled(false);
//                        mTimer.cancel();
//                        mTimerTask.cancel();
                        showFxDialog();
                    }
                }
            }
        });
        // ????????????????????????
        mBtnBack.setOnClickListener(this);
        // ??????????????????????????????
        mBtnImage.setOnClickListener(this);
        // ????????????????????????
        mBtnRestart.setOnClickListener(this);
    }

    private void showFxDialog() {
        LayoutInflater inflaterDl = LayoutInflater.from(PuzzleMain.this);
        RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_moddel_dialog, null);
        final Dialog dialog = new AlertDialog.Builder(PuzzleMain.this,R.style.Dialog).create();
        dialog.show();
        dialog.getWindow().setContentView(layout);
        dialog.setCanceledOnTouchOutside(true);
        RelativeLayout re_item1 = (RelativeLayout) dialog.findViewById(R.id.re_item1);
        RelativeLayout re_item2 = (RelativeLayout) dialog.findViewById(R.id.re_item2);
        RelativeLayout re_item3 = (RelativeLayout) dialog.findViewById(R.id.re_item3);
        RelativeLayout re_item5 = (RelativeLayout) dialog.findViewById(R.id.re_item5);
        TextView tv_item1 = (TextView) dialog.findViewById(R.id.tv_item1);
        TextView tv_item2 = (TextView) dialog.findViewById(R.id.tv_item2);
        TextView tv_item5 = (TextView) dialog.findViewById(R.id.tv_item5);
        re_item5.setVisibility(View.VISIBLE);
        tv_item1.setText("?????????QQ??????");
        tv_item2.setText("??????????????????");
        tv_item5.setText("???????????????");
        re_item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fenxiangtoqq();
            }
        });
        re_item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fenxiangtowx();
            }
        });
        re_item5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fenxiangtowb();
            }
        });
        re_item3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void fenxiangtoqq() {
        QZone.ShareParams sp = new QZone.ShareParams();
        sp.setText(null);
        sp.setTitle(null);
        sp.setTitleUrl(null);
        sp.setImagePath(Environment.getExternalStorageDirectory()+"/zhengshier/fenxiang/mingpCut.png");
        Platform qzone = ShareSDK.getPlatform(QZone.NAME);
// ????????????????????????????????????????????????????????????????????????????????????????????????????????????UI?????????
        qzone.setPlatformActionListener(new PlatformActionListener() {
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Toast.makeText(getApplicationContext(), "?????????QQ???????????????", Toast.LENGTH_SHORT).show();
            }

            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                Toast.makeText(getApplicationContext(), "???????????????QQ?????????", Toast.LENGTH_SHORT).show();
                updateTJzhuanfa(user_id,0);
                queryPtYue();
            }

            public void onCancel(Platform arg0, int arg1) {
                Toast.makeText(getApplicationContext(), "???????????????QQ?????????", Toast.LENGTH_SHORT).show();
            }
        });
// ??????????????????
        qzone.share(sp);
    }

    private void fenxiangtowx() {
        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        sp.setShareType(Platform.SHARE_IMAGE);
        sp.setTitle("?????????app");
        sp.setImagePath(Environment.getExternalStorageDirectory()+"/zhengshier/fenxiang/mingpCut.png");
        Platform wx = ShareSDK.getPlatform(WechatMoments.NAME);
// ????????????????????????????????????????????????????????????????????????????????????????????????????????????UI?????????
        wx.setPlatformActionListener(new PlatformActionListener() {
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Toast.makeText(getApplicationContext(), "?????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                Toast.makeText(getApplicationContext(), "?????????????????????????????????", Toast.LENGTH_SHORT).show();
                updateTJzhuanfa(user_id,1);
                queryPtYue();
            }
            public void onCancel(Platform arg0, int arg1) {
                Toast.makeText(getApplicationContext(), "?????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        });
// ??????????????????
        wx.share(sp);
    }

    private void fenxiangtowb() {
        SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
        sp.setShareType(Platform.SHARE_IMAGE);
        sp.setTitle("?????????app");
        sp.setImagePath(Environment.getExternalStorageDirectory()+"/zhengshier/fenxiang/mingpCut.png");
        Platform wb = ShareSDK.getPlatform(SinaWeibo.NAME);
// ????????????????????????????????????????????????????????????????????????????????????????????????????????????UI?????????
        wb.setPlatformActionListener(new PlatformActionListener() {
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Toast.makeText(getApplicationContext(), "????????????????????????", Toast.LENGTH_SHORT).show();
            }

            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                Toast.makeText(getApplicationContext(), "????????????????????????", Toast.LENGTH_SHORT).show();
                updateTJzhuanfa(user_id,2);
                queryPtYue();
            }

            public void onCancel(Platform arg0, int arg1) {
                Toast.makeText(getApplicationContext(), "????????????????????????", Toast.LENGTH_SHORT).show();
            }
        });
// ??????????????????
        wb.share(sp);
    }

    private void updateTJzhuanfa(final String loginId,final int type) {
        String url = FXConstant.URL_TONGJI_ZHUANFA;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e("Userdetailac,add","????????????????????????"+s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError!=null) {
                    Log.e("Userdetailac,add", "????????????????????????");
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("uLoginId",loginId);
                param.put("locationDynamics", "1");
                if (type==0) {
                    param.put("type", "qqLocationDynamic");
                }else if (type==1){
                    param.put("type", "weixinLocationDynamic");
                }else if (type==2){
                    param.put("type", "weiboLocationDynamic");
                }
                param.put("f_id",DemoHelper.getInstance().getCurrentUsernName());
                return param;
            }
        };
        MySingleton.getInstance(PuzzleMain.this).addToRequestQueue(request);
    }

    private void queryPtYue() {
        String url = FXConstant.URL_PUBLISHDETAIL_QUERY;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e("puzzmain,q",s);
                JSONObject object = JSON.parseObject(s);
                JSONArray array = object.getJSONArray("clist");
                JSONObject detail = array.getJSONObject(0);
                String redSum = detail.getString("redSum");
                if (redSum!=null&&!"".equals(redSum)&&!redSum.equalsIgnoreCase("null")&&Double.parseDouble(redSum)>0){
                    addHongbao();
                }else {
                    LayoutInflater inflater1 = LayoutInflater.from(PuzzleMain.this);
                    RelativeLayout layout1 = (RelativeLayout) inflater1.inflate(R.layout.dialog_alert, null);
                    final Dialog dialog1 = new AlertDialog.Builder(PuzzleMain.this,R.style.Dialog).create();
                    dialog1.show();
                    dialog1.getWindow().setContentView(layout1);
                    dialog1.setCanceledOnTouchOutside(true);
                    dialog1.setCancelable(true);
                    TextView title_tv1 = (TextView) layout1.findViewById(R.id.title_tv);
                    Button btnCancel1 = (Button) layout1.findViewById(R.id.btn_cancel);
                    final Button btnOK1 = (Button) layout1.findViewById(R.id.btn_ok);
                    TextView title = (TextView) layout1.findViewById(R.id.tv_title);
                    title.setText("????????????");
                    btnOK1.setText("??????");
                    btnCancel1.setText("??????");
                    title_tv1.setText("???????????????????????????????????????");
                    btnCancel1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog1.dismiss();
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                    btnOK1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog1.dismiss();
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("dynamicSeq",dynamicSeq);
                param.put("createTime",createTime);
                return param;
            }
        };
        MySingleton.getInstance(PuzzleMain.this).addToRequestQueue(request);
    }

    private void addHongbao() {
        if (name==null||"".equals(name)){
            name = user_id;
        }
        String url = FXConstant.URL_ADD_PINTUHB;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e("puzzmain,a",s);
                JSONObject object = JSON.parseObject(s);
                String code = object.getString("code");
                if ("success".equals(code)){
                    SoundPlayUtils.play(2);
                    LayoutInflater inflaterDl = LayoutInflater.from(PuzzleMain.this);
                    RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.dialog_hongbao, null);
                    final Dialog dialog = new AlertDialog.Builder(PuzzleMain.this,R.style.Dialog).create();
                    dialog.show();
                    dialog.getWindow().setContentView(layout);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                    TextView tv_title1 = (TextView) layout.findViewById(R.id.tv_title1);
                    TextView tv_title = (TextView) layout.findViewById(R.id.tv_title);
                    TextView tv_yue = (TextView) layout.findViewById(R.id.tv_yue);
                    TextPaint tp = tv_title.getPaint();
                    tp.setFakeBoldText(true);
                    tv_title.setText(gameRed + "???");
                    tv_title1.setText(name);
                    tv_yue.setText("????????? ????????????");
                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                }else {
                    LayoutInflater inflaterDl = LayoutInflater.from(PuzzleMain.this);
                    RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.dialog_ok, null);
                    final Dialog dialog2 = new AlertDialog.Builder(PuzzleMain.this,R.style.Dialog).create();
                    dialog2.show();
                    dialog2.getWindow().setContentView(layout);
                    dialog2.setCanceledOnTouchOutside(false);
                    dialog2.setCancelable(false);
                    TextView tv_title = (TextView) dialog2.findViewById(R.id.title_tv);
                    Button btn_ok = (Button) dialog2.findViewById(R.id.btn_ok);
                    tv_title.setText("???????????????????????????????????????????????????");
                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog2.dismiss();
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("user_id",user_id);
                param.put("mer_id", DemoHelper.getInstance().getCurrentUsernName());
                param.put("timestamp",createTime);
                Log.e("puzzmain,p",param.toString());
                return param;
            }
        };
        MySingleton.getInstance(PuzzleMain.this).addToRequestQueue(request);
    }

    private Bitmap getBitmapFromSharedPreferences(){
        Bitmap bitmap = null;
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("sangu_pintu", getApplicationContext().MODE_PRIVATE);
        //?????????:????????????????????????Bitmap
        String imageString = sharedPreferences.getString("image", "");
        //?????????:??????Base64?????????????????????ByteArrayInputStream
        byte[] byteArray = Base64.decode(imageString, Base64.DEFAULT);
        if (byteArray.length == 0) {
            return null;
        } else {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            //?????????:??????ByteArrayInputStream??????Bitmap
            bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
        }
        return bitmap;
    }

    /**
     * Button????????????
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // ????????????????????????
            case R.id.btn_puzzle_main_back:
                PuzzleMain.this.finish();
                break;
            // ??????????????????????????????
            case R.id.btn_puzzle_main_img:
                Animation animShow = AnimationUtils.loadAnimation(
                        PuzzleMain.this, R.anim.image_show_anim);
                Animation animHide = AnimationUtils.loadAnimation(
                        PuzzleMain.this, R.anim.image_hide_anim);
                if (mIsShowImg) {
                    mImageView.startAnimation(animHide);
                    mImageView.setVisibility(View.GONE);
                    mIsShowImg = false;
                } else {
                    mImageView.startAnimation(animShow);
                    mImageView.setVisibility(View.VISIBLE);
                    mIsShowImg = true;
                }
                break;
            // ????????????????????????
            case R.id.btn_puzzle_main_restart:
                cleanConfig();
                generateGame();
                recreateData();
                // ??????GridView??????UI
//                mTvPuzzleMainCounts.setText("" + COUNT_INDEX);
                mAdapter.notifyDataSetChanged();
                mGvPuzzleMainDetail.setEnabled(true);
                break;
            default:
                break;
        }
    }

    /**
     * ??????????????????
     */
    private void generateGame() {
        // ?????? ???????????????????????? ????????????
        new ImagesUtil().createInitBitmaps(
                TYPE, mPicSelected, PuzzleMain.this);
        // ??????????????????
        GameUtil.getPuzzleGenerator();
        // ??????Bitmap??????
        for (ItemBean temp : GameUtil.mItemBeans) {
            mBitmapItemLists.add(temp.getBitmap());
        }
        // ???????????????
        mAdapter = new GridItemsAdapter(this, mBitmapItemLists);
        mGvPuzzleMainDetail.setAdapter(mAdapter);
//        // ???????????????
//        mTimer = new Timer(true);
//        // ???????????????
//        mTimerTask = new TimerTask() {
//
//            @Override
//            public void run() {
//                Message msg = new Message();
//                msg.what = 1;
//                mHandler.sendMessage(msg);
//            }
//        };
//        // ???1000ms?????? ??????0s
//        mTimer.schedule(mTimerTask, 0, 1000);
    }

    /**
     * ?????????????????????View
     */
    private void addImgView() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(
                R.id.rl_puzzle_main_main_layout);
        mImageView = new ImageView(PuzzleMain.this);
        mImageView.setImageBitmap(mPicSelected);
        int x = (int) (mPicSelected.getWidth() * 0.9F);
        int y = (int) (mPicSelected.getHeight() * 0.9F);
        LayoutParams params = new LayoutParams(x, y);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mImageView.setLayoutParams(params);
        relativeLayout.addView(mImageView);
        mImageView.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanConfig();
    }

    /**
     * ???????????????
     */
    @Override
    protected void onStop() {
        super.onStop();
        // ????????????????????????
//        cleanConfig();
//        this.finish();
    }

    /**
     * ????????????????????????
     */
    private void cleanConfig() {
        // ????????????????????????
        GameUtil.mItemBeans.clear();
        // ???????????????
//        mTimer.cancel();
//        mTimerTask.cancel();
//        COUNT_INDEX = 0;
//        TIMER_INDEX = 0;
    }

    /**
     * ??????????????????
     */
    private void recreateData() {
        mBitmapItemLists.clear();
        for (ItemBean temp : GameUtil.mItemBeans) {
            mBitmapItemLists.add(temp.getBitmap());
        }
    }

    /**
     * ??????????????? ???????????????
     *
     * @param bitmap bitmap
     */
    private void handlerImage(Bitmap bitmap) {
        // ??????????????????????????????
        int screenWidth = ScreenUtil.getScreenSize(this).widthPixels;
        int screenHeigt = ScreenUtil.getScreenSize(this).heightPixels;
        mPicSelected = new ImagesUtil().resizeBitmap(
                screenWidth * 0.9f, screenWidth * 0.9f, bitmap);
    }

    /**
     * ?????????Views
     */
    private void initViews() {
        // Button
        mBtnBack = (Button) findViewById(R.id.btn_puzzle_main_back);
        mBtnImage = (Button) findViewById(R.id.btn_puzzle_main_img);
        mBtnRestart = (Button) findViewById(R.id.btn_puzzle_main_restart);
        // Flag ?????????????????????
        mIsShowImg = false;
        // GridView
        mGvPuzzleMainDetail = (GridView) findViewById(
                R.id.gv_puzzle_main_detail);
        // ?????????N*N??????
        mGvPuzzleMainDetail.setNumColumns(TYPE);
        LayoutParams gridParams = new LayoutParams(
                mPicSelected.getWidth(),
                mPicSelected.getHeight());
        // ??????????????????
        gridParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        gridParams.setMargins(0, ScreenshotUtil.dip2px(getApplicationContext(),90),0,0);
        // Grid??????
        mGvPuzzleMainDetail.setLayoutParams(gridParams);
        mGvPuzzleMainDetail.setHorizontalSpacing(0);
        mGvPuzzleMainDetail.setVerticalSpacing(0);
//        // TV??????
//        mTvPuzzleMainCounts = (TextView) findViewById(
//                R.id.tv_puzzle_main_counts);
//        mTvPuzzleMainCounts.setText("" + COUNT_INDEX);
//        // TV?????????
//        mTvTimer = (TextView) findViewById(R.id.tv_puzzle_main_time);
//        mTvTimer.setText("0???");
        // ?????????????????????View
        addImgView();
    }
}

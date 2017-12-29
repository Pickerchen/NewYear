package com.ipcamerasen5.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipcamerasen5.main.control.CameraViewUI;
import com.ipcamerasen5.main.message.MessageReceiver;
import com.ipcamerasen5.main.receiver.ReceiverCameraKey;
import com.ipcamerasen5.main.utils.PreferencesUtilsFactory;
import com.ipcamerasen5.main.utils.Utils;
import com.ipcamerasen5.main.utils.UtilsCameraStatus;
import com.ipcamerasen5.main.utils.UtilsLaunchApp;
import com.ipcamerasen5.main.widget.SlideView;
import com.ipcamerasen5.main1.R;
import com.ipcamerasen5.record.adapter.RecordItemDecoration;
import com.ipcamerasen5.record.common.CommonTools;
import com.ipcamerasen5.record.common.Constant;
import com.ipcamerasen5.record.db.IpCamDevice;
import com.ipcamerasen5.record.event.IpCamStopRecord;
import com.ipcamerasen5.record.event.IpcameraExit;
import com.ipcamerasen5.record.event.IpcameraHomePress;
import com.ipcamerasen5.record.event.MainRecyclerViewShow;
import com.ipcamerasen5.record.service.RecordService;
import com.ipcamerasen5.record.ui.view.RecordActivity;
import com.ipcamerasen5.record.ui.view.VideoTypeActivity;
import com.ipcamerasen5.tvrecyclerview.MainRecyclerViewAdapter;
import com.open.androidtvwidget.leanback.recycle.LinearLayoutManagerTV;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import glnk.io.OnDeviceStatusChangedListener;
import glnk.media.AViewRenderer;
import hsl.p2pipcam.entity.EntityDevice;
import nes.ltlib.LTSDKManager;
import nes.ltlib.utils.AppLog;
import nes.ltlib.utils.LogUtils;

import static android.view.View.INVISIBLE;
import static com.ipcamerasen5.main.MainApplication.activityName;
import static com.ipcamerasen5.main.nobuffer.CameraProviderHelper.ACTION_RECEIVER_CLOSE_SERVICE;
import static com.ipcamerasen5.record.ui.view.RecordActivity.currentUserId;
import static com.ipcamerasen5.record.ui.view.RecordActivity.position;
import static nes.ltlib.LTSDKManager.scaleType_1;

public class MainActivity extends Activity implements Handler.Callback, OnClickListener, OnDeviceStatusChangedListener {

    private RelativeLayout rl_LTCamera;
    private List<RelativeLayout> rl_LTCameras = new ArrayList<>();
    private List<Button> bt_LTCameras = new ArrayList<>();

    private RelativeLayout rl_LTCamera_41;
    private RelativeLayout rl_LTCamera_42;
    private RelativeLayout rl_LTCamera_43;
    private RelativeLayout rl_LTCamera_44;

    private static final String DEFAULT_POSITION = "default_position";
    private static final String DEFAULT_DID = "default_did";
    private float denisty;

    private LinearLayout ll_addView;
    private TextView tv_recording;
    private Resources mResources;
    private List<EntityDevice> mListCameraDevice = new ArrayList<>();
    private List<String> mListUIL = new ArrayList<String>();
    private Handler mHandler;


    private boolean mIsSwitchFourScreen;
    private RecyclerViewTV mRecyclerView;
    private View mrl_recyclerView;
    private View ll_four_gls;
    private int mPosition;
    private int startPosition;
    private ReceiverCameraKey mReceiverCameraKey;
    private BroadcastReceiver mReceiverFloatCamera = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //如果是悬浮窗关闭，摄像头继续播放
            if (action.equals(Constant.floatWinClose)) {
                LogUtils.e("onReceive", "action is" + action + " isBG is " + isBg);
            }
        }
    };
    private boolean isNoLauncherStart;
    public View mNoCameraView;
    public Button mAddButton;
    public Button mExitButton;
    private boolean isRefreshPosition;
    public CameraViewUI mCameraViewUI;

    private String TAG = MainActivity.class.getSimpleName();
    private boolean isBg = false;//是否在后台，默认不在

    //record
    public static boolean hasjump;


    private Button four_btn_selector1;
    private Button four_btn_selector2;
    private Button four_btn_selector3;
    private Button four_btn_selector4;

    private SlideView mSlideView;//左侧滑动的View，用来选择哪个被滑动


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (hasDestroy || hasCreate) {
            return;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ipcamera);


        denisty = getResources().getDisplayMetrics().density;
        init();
        initReceiver();
        initView();

        //加载新摄像头
        startNewCamera();
        initRecyclerView();
        hasCreate = true;
        // TODO: 2017/9/26 for test,please delete after tested
//        Intent intent = new Intent(MainActivity.this,RecordService.class);
//        startService(intent);
        LogUtils.e("MainActivity", "MainActivity_onCreate is finish");


        // TODO: 2017/12/5 过场动画暂停
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            postponeEnterTransition();
//        }
    }


    @Override
    protected void onStop() {
        LogUtils.e("mainActivity", "onStop life");
        super.onStop();
        MainApplication.isFront = false;

        for (int i = 0; i < rl_LTCameras.size(); i++) {
            if (rl_LTCameras.get(i).getChildCount() > 0) {
                rl_LTCameras.get(i).removeViewAt(0);
                if ((LTSDKManager.getInstance().gids.size() > i) && (LTSDKManager.getInstance().gids.get(i).startsWith("by0178"))) {
                    AViewRenderer mAViewRender = (AViewRenderer) LTSDKManager.getInstance().mLTDevices.get(i).getVideoRenderer();
                    Matrix matrix = mAViewRender.getMatrix();
                    matrix.reset();
                    matrix.postScale((float) 1, (float) 1, 0, 0);
                } else {
                    if (LTSDKManager.getInstance().gids.size() > i) {
                        AViewRenderer mAViewRender = (AViewRenderer) LTSDKManager.getInstance().mLTDevices.get(i).getVideoRenderer();
                        Matrix matrix = mAViewRender.getMatrix();
                        matrix.reset();
                        matrix.postScale((float) 1.5, (float) 1.77777, 0, 0);
                    }
                }
            }
        }

        if (mListCameraDevice != null && mListCameraDevice.size() != 0) {
            // TODO: 2017/6/12：此处出现mPosition大于size的bug
            if (mPosition > mListCameraDevice.size() - 1) {
                return;
            }
            PreferencesUtilsFactory.setLastTimeDID(mListCameraDevice.get(mPosition).DeviceID);
        }
    }

    @Override
    protected void onRestart() {
        LogUtils.e("mainActivity", "onRestart life");
        super.onRestart();
        mSlideView.setFocusable(true);
        if (null != mListCameraDevice && mListCameraDevice.size() == 0) {
            mAddButton.requestFocus();
        }
    }

    //lanchmode为singleInstance的情况下才会执行
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        LogUtils.e("mainActivity", "onNewIntent" + "pisition is " + intent.getIntExtra(DEFAULT_POSITION, 0));
    }


    public static boolean isFromLauncher = true;

    @Override
    protected void onResume() {
        LogUtils.e("mainActivity", "onResume life");
        super.onResume();
        MainApplication.isFront = true;
        activityName = MainActivity.class.getSimpleName();
        if (null != getIntent()) {
            if (isFromLauncher) {
                List<String> names = CommonTools.getDeviceName(MainActivity.this);
                if (names.size() > 0) {
                    for (int i = 0; i < mListCameraDevice.size(); i++) {
                        IpCamDevice mIpCamDevice = CommonTools.findDeviceFromDB(mListCameraDevice.get(i).DeviceID);
                        if (null != mIpCamDevice && names.size() > i) {
                            if (!names.get(i).equals(mIpCamDevice.getAliasName())) {
                                mIpCamDevice.setAliasName(names.get(i));
                                mIpCamDevice.save();
                            }
                        }
                    }
                }

                mPosition = getIntent().getIntExtra(DEFAULT_POSITION, 0);
                if (null != mListCameraDevice && mListCameraDevice.size() > mPosition) {
                    if (mListCameraDevice.get(mPosition).status.equals("2")) {
                        tv_recording.setVisibility(View.VISIBLE);
                    } else {
                        tv_recording.setVisibility(View.GONE);
                    }
                }
                LogUtils.e("mainActivity", "mposition is " + mPosition);

                boolean hasChange = false;
                if (null != adapter && null != mListCameraDevice && mListCameraDevice.size() > 0) {
                    for (int i = 0; i < mListCameraDevice.size(); i++) {
                        String did = mListCameraDevice.get(i).DeviceID;
                        if (!mListCameraDevice.get(i).videoPath.equals("Constant.imgPathByLauncher+did+File.separator+did+\".jpg\"")) {
                            hasChange = true;
                            mListCameraDevice.get(i).videoPath = Constant.imgPathByLauncher + did + File.separator + did + ".jpg";
                            // TODO: 2017/12/5 过场动画
//                            Drawable drawable = Drawable.createFromPath(Constant.imgPathByLauncher + did + File.separator + did + ".jpg");
//                            rl_LTCamera.setBackground(drawable);
                        }
                    }
                    if (hasChange) {
                        adapter.notifyDataSetChanged();
                    }
                }
                LogUtils.e("mainActivity", "mposition is " + mPosition);
            }
            LogUtils.e("mainActivity", "mposition is " + getIntent().getIntExtra(DEFAULT_POSITION, 0));
        }
        isBg = false;
        isFromLauncher = true;
        if (null != mSlideView) {
            mSlideView.requestFocus();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.e("mainActivity", "onPause life");
        //在后台
        isBg = true;
    }

    @Override
    protected void onDestroy() {
        isFirstShowMenu = false;
        hasDestroy = true;
        LogUtils.e("mainActivity", "onDestroy life");
        unReceiver();
        EventBus.getDefault().unregister(this);
        sendHomeBroadCast();
        // TODO: 2017/9/1 新摄像头remove处理
//        LTSDKManager.getInstance().releaseDevice();
        if (rl_LTCamera.getChildCount() > 0) {
            rl_LTCamera.removeViewAt(0);
        }
        for (int i = 0; i < rl_LTCameras.size(); i++) {
            if (rl_LTCameras.get(i).getChildCount() > 0) {
                rl_LTCameras.get(i).removeViewAt(0);
            }
        }
        super.onDestroy();
    }

    public static final boolean SINGLE_CAMERA_CLIENT_FLAG = true;

    //end 2017-05-17


    private void init() {
        EventBus.getDefault().register(this);
        mHandler = new Handler(this);
        //关闭launcher界面的悬浮
        Utils.closeCameraFloatWindow(this);
        mCameraViewUI = new CameraViewUI(this);
        Intent intent = getIntent();
        if (null != intent) {
            Bundle mBundle = intent.getExtras();
            if (mBundle != null) {
                isNoLauncherStart = true;
            }
            mPosition = intent.getIntExtra(DEFAULT_POSITION, 0);
            startPosition = mPosition;
        }
        //从launcher的悬浮视频启动
        if (!isNoLauncherStart) {
            String lastTimeDID = PreferencesUtilsFactory.getLastTimeDID();
            if (!TextUtils.isEmpty(lastTimeDID)) {
                mPosition = Utils.getLastTimePlayPosition(lastTimeDID);

            }
        }

        mResources = getBaseContext().getResources();
    }

    private void initView() {

        //四分屏UI
        ll_four_gls = findViewById(R.id.ll_four_gls);
        rl_LTCamera_41 = (RelativeLayout) findViewById(R.id.rl_LTCamera_41);
        rl_LTCamera_42 = (RelativeLayout) findViewById(R.id.rl_LTCamera_42);
        rl_LTCamera_43 = (RelativeLayout) findViewById(R.id.rl_LTCamera_43);
        rl_LTCamera_44 = (RelativeLayout) findViewById(R.id.rl_LTCamera_44);
        rl_LTCameras.add(rl_LTCamera_41);
        rl_LTCameras.add(rl_LTCamera_42);
        rl_LTCameras.add(rl_LTCamera_43);
        rl_LTCameras.add(rl_LTCamera_44);
        //全屏
        rl_LTCamera = (RelativeLayout) findViewById(R.id.rl_LTCamera);
        // TODO: 2017/12/6 过场动画
//        ViewCompat.setTransitionName(rl_LTCamera, "ltCamera");
//
//        rl_LTCamera.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//
//                rl_LTCamera.getViewTreeObserver().removeOnPreDrawListener(this);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    startPostponedEnterTransition();
//                }
//
//                return true;
//            }
//        });


        // TODO Auto-generated method stub
        ArrayList<ViewGroup> mListGLSFour = new ArrayList<ViewGroup>();
        tv_recording = (TextView) findViewById(R.id.main_single_recording);
        //无摄像头时的UI
        mNoCameraView = findViewById(R.id.ll_show_add_device);
        mAddButton = (Button) findViewById(R.id.btn_add_device);
        mExitButton = (Button) findViewById(R.id.btn_exit);

        /**
         * 四分屏时选中框
         */
        four_btn_selector1 = (Button) findViewById(R.id.btns_one);
        four_btn_selector2 = (Button) findViewById(R.id.btns_two);
        four_btn_selector3 = (Button) findViewById(R.id.btns_three);
        four_btn_selector4 = (Button) findViewById(R.id.btns_four);
        four_btn_selector1.setOnClickListener(this);
        four_btn_selector2.setOnClickListener(this);
        four_btn_selector3.setOnClickListener(this);
        four_btn_selector4.setOnClickListener(this);
        bt_LTCameras.add(four_btn_selector1);
        bt_LTCameras.add(four_btn_selector2);
        bt_LTCameras.add(four_btn_selector3);
        bt_LTCameras.add(four_btn_selector4);

        int size = mListGLSFour.size();
        initCameraDevice();
//        setGLSFourVisibility(View.GONE);
        ll_four_gls.setVisibility(View.GONE);


        ll_addView = (LinearLayout) findViewById(R.id.ll_addview);

        //左侧滑动View
        int dp = (int) getResources().getDimension(R.dimen.FHundred);
        int space_dp = ((1080) - dp) / 2;
        AppLog.e("space_dp is " + space_dp);
        mSlideView = (SlideView) findViewById(R.id.slide_view);
        RelativeLayout.LayoutParams mLayoutParams = (RelativeLayout.LayoutParams) mSlideView.getLayoutParams();

        mLayoutParams.setMargins(0, space_dp, 0, 0);
        mSlideView.setLayoutParams(mLayoutParams);
        mSlideView.setSlideNum(5);
        mSlideView.position = 0;
        mSlideView.setFocusable(true);
        showMenu();
    }


    //读取摄像头个数判断UI显示
    private void initCameraDevice() {
        List<IpCamDevice> mIpCamDevices = DataSupport.findAll(IpCamDevice.class);
        for (IpCamDevice ipCamDevice : mIpCamDevices) {
            EntityDevice entityDevice = new EntityDevice();
            entityDevice.DeviceID = ipCamDevice.getDid();
            entityDevice.DeviceName = ipCamDevice.getAliasName();
            mListCameraDevice.add(entityDevice);
        }

        //获取公共配置文件中的did数量，读取完之后通过eventbus的方式发送过去。
        if (null != adapter) {
            adapter.datas = mListCameraDevice;
            adapter.notifyDataSetChanged();
        }
        if (mIpCamDevices.size() != 0) {


            for (IpCamDevice mIpCamDevice : mIpCamDevices) {
                if (null == mIpCamDevice.getDid()) {
                    return;
                }


                for (EntityDevice mEntityDevice : mListCameraDevice) {
                    if (mIpCamDevice.getDid().equals(mEntityDevice.DeviceID)) {
                        if (null != mIpCamDevice.getLastJPGPath() && !mIpCamDevice.getLastJPGPath().equals("")) {
                            mEntityDevice.videoPath = mIpCamDevice.getLastJPGPath();
                        }
                        if (null != mIpCamDevice.getStatus() && !mIpCamDevice.getStatus().equals("")) {
                            mEntityDevice.status = mIpCamDevice.getStatus();
                        }
                        LogUtils.e("initCameraDevice", "path is " + mEntityDevice.videoPath);
                    }
                }
            }
            switchAddDevicesUI(INVISIBLE);
        }
    }

    private MainRecyclerViewAdapter adapter;

    private void initRecyclerView() {
        mRecyclerView = (RecyclerViewTV) findViewById(R.id.recyclerView);
//        mRecyclerView.setVisibility(View.VISIBLE);
        mrl_recyclerView = (View) findViewById(R.id.rl_recyclerView);
        mRecyclerView.setItemViewCacheSize(4);
        mRecyclerView.setItemAnimator(null);
        adapter = new MainRecyclerViewAdapter(MainActivity.this, mListCameraDevice);
        LinearLayoutManagerTV manager = new LinearLayoutManagerTV(MainActivity.this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new RecordItemDecoration(8));
        adapter.setOnItemClickListener(new MainRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // TODO: 2017/9/4 旧摄像头
//                mCameraViewControl.stopPlaySingleCamera(position);
//                currentUserId = mCameraViewControl.startPlaySingleCamera(position);
//                refreshCameraStatusUIByPosition(position);
//                //如果状态是2显示正在录制
//                if (mListCameraDevice.get(position).status.equals("2")) {
//                    tv_recording.setVisibility(View.VISIBLE);
//                } else {
//                    tv_recording.setVisibility(View.GONE);
//                }
//                sendMessageToRefreshUI(300);
                // TODO: 2017/9/4 LTSDK
                if (position < LTSDKManager.getInstance().mLTDevices.size()) {
                    mPosition = position;
                    if (rl_LTCamera.getChildCount() > 0) {
                        rl_LTCamera.removeViewAt(0);
                    }
                    if (null != LTSDKManager.getInstance().mLTDevices.get(position).getAView()) {
                        rl_LTCamera.addView(LTSDKManager.getInstance().mLTDevices.get(position).getAView());
                    }
                }
            }
        });
//        mrl_recyclerView.setVisibility(View.GONE);
//        mSlideView.setFocusable(true);
        mSlideView.requestFocus();
    }

    private void initReceiver() {
        mReceiverCameraKey = new ReceiverCameraKey();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ReceiverCameraKey.ACTION_IPCAMERA_KEY_FLOAT);
        registerReceiver(mReceiverCameraKey, mIntentFilter);

        IntentFilter mIntentFilter1 = new IntentFilter();
        mIntentFilter1.addAction(Constant.floatWinClose);
        registerReceiver(mReceiverFloatCamera, mIntentFilter1);
    }

    private void unReceiver() {
        if (null != mReceiverCameraKey) {
            unregisterReceiver(mReceiverCameraKey);
        }
        if (null != mReceiverFloatCamera) {
            unregisterReceiver(mReceiverFloatCamera);
        }
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        Integer i = (Integer) arg0.getId();
        if (i.equals(R.id.btn_exit)) {
            finish();

        } else if (i.equals(R.id.btn_add_device)) {
            UtilsLaunchApp.launchSecQreSettings(this);

        } else if (i.equals(R.id.btns_one)) {
            switchScreenSingleFour(false, 0);
            mPosition = 0;

        } else if (i.equals(R.id.btns_two)) {
            switchScreenSingleFour(false, 1);
            mPosition = 1;

        } else if (i.equals(R.id.btns_three)) {
            switchScreenSingleFour(false, 2);
            mPosition = 2;

        } else if (i.equals(R.id.btns_four)) {
            switchScreenSingleFour(false, 3);
            mPosition = 3;

        } else {
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent arg1) {
        // TODO Auto-generated method stub
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                LogUtils.e("onKeyDown", "-----------------mIsSwitchFourScreen = " + mIsSwitchFourScreen);
                boolean b = showMenuByKey();
                if (b) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                if (keyBackPress()) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (!isMenuShow() && !mIsSwitchFourScreen) {
                    switchLeftRightKey(UtilsCameraStatus.STATUS_SWITCH_LEFT);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (!isMenuShow() && !mIsSwitchFourScreen) {
                    switchLeftRightKey(UtilsCameraStatus.STATUS_SWITCH_RIGHT);
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, arg1);
    }

    private void switchLeftRightKey(int mode) {
        int count = mListCameraDevice.size();
        int nextPositionByStatus = UtilsCameraStatus.getNextPositionByStatus(count, mPosition, mode);
        mPosition = nextPositionByStatus;
    }

    private boolean keyBackPress() {
//        if (mRecyclerView.getVisibility() == View.VISIBLE) {
//            mrl_recyclerView.setVisibility(View.GONE);
//            mSlideView.setVisibility(View.GONE);
//            mRecyclerView.setVisibility(View.GONE);
//            if (ll_addView.getVisibility() == View.VISIBLE) {
//                ll_addView.setVisibility(View.GONE);
//                return true;
//            }
//        } else {
//            if (isMenuShow()) {
//                hideMenuShow();
//                return true;
//            }
//        }
//        return false;
        if (isMenuShow()) {
            hideMenuShow();
            return true;
        } else {
            //直接退出
            return false;
        }

    }


    private boolean isMenuShow() {
        if (ll_addView.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    private void hideMenuShow() {
        ll_addView.setVisibility(View.GONE);
        mrl_recyclerView.setVisibility(View.GONE);
        mSlideView.setVisibility(View.GONE);
    }

    private boolean showMenuByKey() {
        if (mIsSwitchFourScreen) {

        } else {
            showMenu();
        }
        return false;
    }

    private boolean isFirstShowMenu = true;
    private boolean hasDestroy = false;
    private boolean hasCreate = false;

    private void showMenu() {
        isFirstShowMenu = false;
        //多个cam的UI
        ll_addView.setVisibility(View.VISIBLE);
        mSlideView.setVisibility(View.VISIBLE);
        mSlideView.setFocusable(true);
        mSlideView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mSlideView.position) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        hideMenuShow();
                        switchScreenSingleFour(true);
                        break;
                    case 3:
                        if (rl_LTCamera.getChildCount() > 0) {
                            rl_LTCamera.removeViewAt(0);
                        }
                        Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                        intent.putExtra("userID", currentUserId);
                        intent.putExtra("position", mPosition);
                        LogUtils.e("startActivity", "currentUserID is " + currentUserId);
                        startActivityForResult(intent, 1);
                        hasjump = true;
                        isFromLauncher = false;
                        break;
                    case 4:
                        Intent intent_file = new Intent(MainActivity.this, VideoTypeActivity.class);
                        startActivity(intent_file);
                        isFromLauncher = false;
                        break;
                }
            }
        });
    }

    private void switchScreenSingleFour(boolean isFour) {
        switchScreenSingleFour(isFour, 5);
    }

    /**
     * 全屏和四分屏的切换
     *
     * @param isFour       true表示4分屏显示，false表示全屏显示
     * @param playPosition camera的下标， 表示全屏播放第几个camera
     */
    private void switchScreenSingleFour(boolean isFour, int playPosition) {
        LogUtils.e("switchScreenSingleFour", "isFour is " + isFour + " playPosition is " + playPosition + " device.size is" + LTSDKManager.getInstance().mLTDevices.size());
        // TODO: 2017/8/30 new
        if (!isFour) {
            for (int i = 0; i < rl_LTCameras.size(); i++) {
                if (rl_LTCameras.get(i).getChildCount() > 0) {
                    rl_LTCameras.get(i).removeViewAt(0);
                    LTSDKManager.getInstance().setVideoScale(denisty, LTSDKManager.scaleType_1);
//                    if (LTSDKManager.getInstance().gids.get(i).startsWith("by0178")) {
//                        AViewRenderer mAViewRender = (AViewRenderer) LTSDKManager.getInstance().mLTDevices.get(i).getVideoRenderer();
//                        Matrix matrix = mAViewRender.getMatrix();
//                        matrix.reset();
//                        matrix.postScale((float) 1, (float) 1, 0, 0);
//                    } else {
//                        AViewRenderer mAViewRender = (AViewRenderer) LTSDKManager.getInstance().mLTDevices.get(i).getVideoRenderer();
//                        Matrix matrix = mAViewRender.getMatrix();
//                        matrix.reset();
//                        matrix.postScale((float) 1.5, (float) 1.77777, 0, 0);
//                    }
                }
            }
            switch (playPosition) {
                case 0:
                    if (null != (LTSDKManager.getInstance().mLTDevices.get(0).getAView())) {
                        rl_LTCamera.addView(LTSDKManager.getInstance().mLTDevices.get(0).getAView());
                    }
                    break;
                case 1:
                    if (!(LTSDKManager.getInstance().mLTDevices.size() > 1)) {
                        rl_LTCamera.addView(LTSDKManager.getInstance().mLTDevices.get(mPosition).getAView());
                    }
                    if (LTSDKManager.getInstance().mLTDevices.size() > 1) {
                        if (null != LTSDKManager.getInstance().mLTDevices.get(1).getAView()) {
                            rl_LTCamera.addView(LTSDKManager.getInstance().mLTDevices.get(1).getAView());
                        }
                    }
                    break;
                case 2:
                    if (!(LTSDKManager.getInstance().mLTDevices.size() > 2)) {
                        rl_LTCamera.addView(LTSDKManager.getInstance().mLTDevices.get(mPosition).getAView());
                    }
                    if (LTSDKManager.getInstance().mLTDevices.size() > 2) {
                        if (null != LTSDKManager.getInstance().mLTDevices.get(2).getAView()) {
                            rl_LTCamera.addView(LTSDKManager.getInstance().mLTDevices.get(2).getAView());
                        }
                    }
                    break;
                case 3:
                    if (!(LTSDKManager.getInstance().mLTDevices.size() > 3)) {
                        rl_LTCamera.addView(LTSDKManager.getInstance().mLTDevices.get(mPosition).getAView());
                    }
                    if (LTSDKManager.getInstance().mLTDevices.size() > 3) {
                        if (null != LTSDKManager.getInstance().mLTDevices.get(3).getAView()) {
                            rl_LTCamera.addView(LTSDKManager.getInstance().mLTDevices.get(3).getAView());
                        }
                    }
                    break;
            }
            ll_addView.setVisibility(View.VISIBLE);
            mSlideView.setVisibility(View.VISIBLE);
            rl_LTCamera.setVisibility(View.VISIBLE);
            ll_four_gls.setVisibility(View.GONE);
        } else {
            if (LTSDKManager.getInstance().mLTDevices.size() == 0) {
                return;
            } else {
                if (rl_LTCamera.getChildCount() > 0) {
                    rl_LTCamera.removeViewAt(0);
                }
                LTSDKManager.getInstance().setVideoScale(denisty, LTSDKManager.scaleType_2);
                for (int i = 0; i < LTSDKManager.getInstance().mLTDevices.size(); i++) {
                    //1080P的摄像头
//                    if (LTSDKManager.getInstance().gids.get(i).startsWith("by0178")) {
//                        AViewRenderer mAViewRender = (AViewRenderer) LTSDKManager.getInstance().mLTDevices.get(i).getVideoRenderer();
//                        if (null != mAViewRender) {
//                            Matrix matrix = mAViewRender.getMatrix();
//                            matrix.reset();
//                            matrix.postScale((float) 0.5, (float) 0.5, 0, 0);
//                            LogUtils.e("switchScreenSingleFour", "i is " + i);
//                        }
//                    } else {
//                        AViewRenderer mAViewRender = (AViewRenderer) LTSDKManager.getInstance().mLTDevices.get(i).getVideoRenderer();
//                        if (null != mAViewRender) {
//                            Matrix matrix = mAViewRender.getMatrix();
//                            matrix.reset();
//                            matrix.postScale((float) 0.75, (float) 0.85, 0, 0);
//                            LogUtils.e("switchScreenSingleFour", "i is " + i);
//                        }
//                    }
                    if (null != LTSDKManager.getInstance().mLTDevices.get(i).getAView()) {
                        rl_LTCameras.get(i).addView(LTSDKManager.getInstance().mLTDevices.get(i).getAView());
                        bt_LTCameras.get(i).setText("");
                    }
                }
                for (int i = 0; i < bt_LTCameras.size(); i++) {
                    //如果未添加Aview则说明摄像头时离线的
                    if (rl_LTCameras.get(i).getChildCount() == 0) {
                        bt_LTCameras.get(i).setText(MainActivity.this.getResources().getString(R.string.status_off_line));
                    }
                    if (i >= LTSDKManager.getInstance().mLTDevices.size()) {
                        bt_LTCameras.get(i).setText("");
                    }
                }
                rl_LTCamera.setVisibility(View.VISIBLE);
                ll_four_gls.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 控制向导页面的弹出（在设备中无添加的摄像头时，显示向导页面， 反之则不显示）
     *
     * @param visibility
     */
    private void switchAddDevicesUI(int visibility) {
        mAddButton.setOnClickListener(this);
        mExitButton.setOnClickListener(this);
        mAddButton.requestFocus();
        mNoCameraView.setVisibility(visibility);
    }


    /**
     * 发送广播通知launcher播放
     */
    public void sendHomeBroadCast() {
        LogUtils.e("sendHomeBroadCast_application", "发出home键广播");
        Intent intent = new Intent();
        intent.setAction(ACTION_RECEIVER_CLOSE_SERVICE);
        intent.putExtra("activityName", activityName);
        sendBroadcast(intent);
    }


    @Override
    public boolean handleMessage(Message message) {
        int what = message.what;
        switch (what) {
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LTSDKManager.getInstance().mLTDevices.size() > mPosition && null != LTSDKManager.getInstance().mLTDevices.get(position).getAView()) {
            rl_LTCamera.addView(LTSDKManager.getInstance().mLTDevices.get(mPosition).getAView());
        }
        LogUtils.e("onActivityResult", "从录制界面返回");
    }

    /******************************事件总线********************************/

    /**
     * 更新mainActivity处deviceRecyclerView的状态
     *
     * @param device
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealwithRecording(IpCamDevice device) {
        LogUtils.e("dealwithRecording", "收到更新recyclerview的通知：" + device.getDid() + "status is " + device.getStatus());
        String did = device.getDid();
        int position = 0;
        for (int i = 0; i < mListCameraDevice.size(); i++) {
            if (mListCameraDevice.get(i).DeviceID.replaceAll("-", "").equals(did)) {
                position = i;
            }
        }
        if (device.getStatus().equals("2") && RecordService.hasStorage) {
            mListCameraDevice.get(position).status = "2";
            adapter.notifyItemChanged(position);
            LogUtils.e("dealwithRecording", "tv_recording will setVisibility");
            //如果当前全屏显示的摄像头和当前录制的摄像头一致则显示录制。
            if (position == mPosition) {
                tv_recording.setVisibility(View.VISIBLE);
            }
        } else if (device.getStatus().equals("1")) {
            //如果当前的设备已经是正在录制状态，不需要更改到在线录制
            if (!mListCameraDevice.get(position).status.equals("2")) {
                mListCameraDevice.get(position).status = "1";
            }
            adapter.notifyItemChanged(position);
        } else if (device.getStatus().equals("0")) {
            mListCameraDevice.get(position).status = "0";
            adapter.notifyItemChanged(position);
        }
    }

    @Subscribe
    public void receiverCameraKey(MessageReceiver messageReceiver) {
        showMenuByKey();
    }

    /**
     * 录制完成
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealwithRecordStopEvent(IpCamStopRecord event) {
        String did = event.getDid();
        int position = 0;
        for (int i = 0; i < mListCameraDevice.size(); i++) {
            if (mListCameraDevice.get(i).DeviceID.replaceAll("-", "").equals(did)) {
                position = i;
            }
        }
        LogUtils.e("dealwithRecordStopEvent", "收到录制完成的通知" + " position is " + position + " did is " + did);
        //更新数据为在线状态
        if (mListCameraDevice.get(position).status.equals("2")) {
            mListCameraDevice.get(position).status = "1";
        }
        adapter.notifyItemChanged(position);
        tv_recording.setVisibility(View.GONE);
        //更新摄像头截图
        if (null != event.getJpgPath() && !event.getJpgPath().equals("")) {
            mListCameraDevice.get(position).videoPath = event.getJpgPath();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showRecyclerView(MainRecyclerViewShow show) {
        if (show.show) {
            mrl_recyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mrl_recyclerView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    public void startNewCamera() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                if (LTSDKManager.getInstance().mLTDevices.size() > mPosition && null != LTSDKManager.getInstance().mLTDevices.get(mPosition).getAView()) {
//                    rl_LTCamera.addView(LTSDKManager.getInstance().mLTDevices.get(mPosition).getAView());
//                    LTSDKManager.getInstance().setVideoScale(LTSDKManager.scaleType_1);
//                }
                for (int i = 0; i < LTSDKManager.getInstance().mLTDevices.size(); i++) {
                    LTSDKManager.getInstance().setVideoScale(denisty, scaleType_1);
                }
                if (LTSDKManager.getInstance().mLTDevices.size() > mPosition && null != LTSDKManager.getInstance().mLTDevices.get(mPosition).getAView()) {
                    rl_LTCamera.addView(LTSDKManager.getInstance().mLTDevices.get(mPosition).getAView());
                }


//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        File mFile = new File(CommonTools.getAllStorages(MainActivity.this).get(0));
//                        LogUtils.e("startNewCamera","mFile is "+mFile.getAbsolutePath());
//                        CommonTools.createFolder(mFile,"LTCAMERA3");
//                        File mFile1 = new File(mFile.getAbsolutePath()+File.separator+"LTCAMERA3"+File.separator+"LT2.mp4");
//
//                        LTSDKManager.getInstance().startRecord("by0177e9c1",mFile1.getAbsolutePath());
//                    }
//                },1000);
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        LTSDKManager.getInstance().stopRecord("by0177e9c1");
//                    }
//                },10000);
            }
        }, 600);
    }


    @Override
    public void onChanged(final String s, int l) {
        LogUtils.e("onChanged", "s is " + s + " i is " + l);
        LTSDKManager.getInstance().createDevice(MainActivity.this);
        int position = LTSDKManager.getInstance().getPositionByGid(s);
        if (position == 0 && rl_LTCamera.getChildCount() == 0) {
            LogUtils.e("onChanged", "s is " + s + " rl_LTCamera will add");
            rl_LTCamera.addView(LTSDKManager.getInstance().mLTDevices.get(0).getAView());
            LTSDKManager.getInstance().mLTDevices.get(0).getGlnkPlayer().start();
        }
//        LogUtils.e("onChange","path is "+CommonTools.getAllStorages(MainActivity.this).get(0));
//            LTSDKManager.getInstance().startRecord(s, CommonTools.getAllStorages(MainActivity.this).get(0)+File.separator+"lt.mp4");
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    LTSDKManager.getInstance().stopRecord(s);
//                }
//            },10000);
    }

    @Override
    public void onPushSvrInfo(String s, String s1, int i) {
        LogUtils.e("onPushSvrInfo", "s is " + s + " s1 is " + s1 + " i is " + i);
    }

    @Override
    public void onDevFunInfo(String s, String s1) {

    }

    @Override
    public void onStAuthResult(String s) {

    }

    @Override
    public void onStDevList(String s, String s1) {

    }

    /**
     * home键按下时收到该事件，从MainApplication中发出
     *
     * @param ipcameraHomePress
     */
    @Subscribe
    public void getHomePressEvent(IpcameraHomePress ipcameraHomePress) {
        removeCameraView();
    }

    public void removeCameraView() {
        if (rl_LTCamera.getChildCount() > 0) {
            rl_LTCamera.removeViewAt(0);
        }
        for (int i = 0; i < rl_LTCameras.size(); i++) {
            if (rl_LTCameras.get(i).getChildCount() > 0) {
                rl_LTCameras.get(i).removeViewAt(0);
            }
        }
    }

    @Subscribe (threadMode =  ThreadMode.MAIN)
    public void dealwithExit(IpcameraExit event){
        LogUtils.e("dealwithExit","exit");
        finish();
    }

    // TODO: 2017/12/6 过场动画
//    @Override
//    public void finishAfterTransition() {
//
//        Intent data = new Intent();
//        data.putExtra("startPosition", startPosition);
//        data.putExtra("currentPosition", mPosition);
//        setResult(RESULT_OK, data);
//
//        super.finishAfterTransition();
//    }
}

package com.sen5.secure.launcher;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.ipcamera.main.control.CameraSourceControl;
import com.ipcamerasen5.main.MainActivity;
import com.ipcamerasen5.record.service.RecordService;
import com.open.androidtvwidget.bridge.RecyclerViewBridge;
import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.recycle.GridLayoutManagerTV;
import com.open.androidtvwidget.leanback.recycle.LinearLayoutManagerTV;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.open.androidtvwidget.view.MainUpView;
import com.sen5.launchertools.ToolsFragment;
import com.sen5.secure.launcher.base.BaseActivity;
import com.sen5.secure.launcher.base.LauncherApplication;
import com.sen5.secure.launcher.base.LauncherModel;
import com.sen5.secure.launcher.data.JsonCreator;
import com.sen5.secure.launcher.data.control.CameraControl;
import com.sen5.secure.launcher.data.control.VersionsControl;
import com.sen5.secure.launcher.data.entity.Constant;
import com.sen5.secure.launcher.data.entity.DeviceBean;
import com.sen5.secure.launcher.data.interf.ItemOnKeyListener;
import com.sen5.secure.launcher.data.interf.SecureCallback;
import com.sen5.secure.launcher.ui.adapter.AppRecycleViewPresenter;
import com.sen5.secure.launcher.ui.adapter.DevicePresenter;
import com.sen5.secure.launcher.ui.adapter.MoreDevPresenter;
import com.sen5.secure.launcher.ui.adapter.SceneAdapter;
import com.sen5.secure.launcher.ui.adapter.SensorPresenter;
import com.sen5.secure.launcher.utils.CutPicUtils;
import com.sen5.secure.launcher.utils.HolderDTV;
import com.sen5.secure.launcher.utils.MetricsUtils;
import com.sen5.secure.launcher.utils.Utils;
import com.sen5.secure.launcher.utils.UtilsRecyclerViewBridge;
import com.sen5.secure.launcher.utils.UtilsTimeFormat;
import com.sen5.secure.launcher.widget.CameraView;
import com.sen5.secure.launcher.widget.DividerGridItemDecoration;
import com.sen5.secure.launcher.widget.LTCameraView;
import com.sen5.secure.launcher.widget.ScaleAbleView;
import com.sen5.secure.launcher.widget.Sen5TextView;
import com.sen5.secure.launcher.widget.Sen5TimerTextView;
import com.sen5.secure.launcher.widget.TextClock;
import com.sen5.secure.launcher.workspace.AppInfo;
import com.sen5.smartlifebox.controlall.RemoteControl;
import com.sen5.smartlifebox.data.entity.DeviceData;
import com.sen5.smartlifebox.data.entity.DeviceStatusData;
import com.sen5.smartlifebox.data.entity.SceneData;
import com.sen5.smartlifebox.data.event.DeviceEvent;
import com.sen5.smartlifebox.data.event.RoomEvent;
import com.sen5.smartlifebox.data.event.SceneEvent;
import com.sen5.smartlifebox.data.p2p.P2PModel;
import com.sen5.smartlifebox.data.service.DeviceSDKService;
import com.sen5.smartlifebox.data.service.P2PConnectService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hsl.p2pipcam.nativecaller.DeviceSDK;
import nes.ltlib.LTSDKManager;
import nes.ltlib.data.CameraEntity;
import nes.ltlib.utils.AppLog;

// TODO: 2017/6/20 在launcher挂后重启 Camera的回调会丢失 需要 在一次挂后才能回来 可能是第一次挂进程没有完全退出 回调还是在原来的launcher上

/**
 * Created by zhoudao on 2017/5/18.
 */
@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class LauncherActivity extends BaseActivity implements LauncherModel.Callbacks, SecureCallback {
    private static final String TAG = LauncherActivity.class.getSimpleName();

    private HorizontalScrollView mHScrollView;
    private RecyclerViewTV mRecyclerScene;
    private RecyclerViewTV mRecyclerDevice;
    private RecyclerViewTV mRecyclerSensor;
    private RecyclerViewTV mRecyclerMoreDevice;
    private RecyclerViewTV mRecyclerApp;

    private FrameLayout mflContext;
    private LinearLayout mLlCamera;
    private RelativeLayout mLlDev, mLlMoreDev;

    private PopupWindow popupWindow;

    private MainUpView mainUpView1;
    private View mCurrentFocusView;
    private ViewGroup mFgmentViewGroup;
    private TextClock mClock;
    private Sen5TimerTextView mData;

    private LauncherModel mModel;
    private P2PModel p2PModel;

    private DevicePresenter deviceAdapter;
    private SensorPresenter sensorAdapter;
    private MoreDevPresenter moreDevPresenter;
    private AppRecycleViewPresenter appPresenter;
    private RecyclerViewBridge mRecyclerViewBridge;

    private LauncherApplication mApplication;
    private Sen5TextView mTvDev;
    private Sen5TextView mTvDevDes;
    private ImageView mIvDev;
    private ScaleAbleView mScaleRed;
    private ScaleAbleView mScaleYellow;
    private ScaleAbleView mScaleGreen;
    private ScaleAbleView mScaleBlue;
    private ScaleAbleView mScaleAdd;


    private VideoView mVvDTV;

    private FrameLayout mFLCamera;
    private GridLayoutManagerTV mDevlayoutManager;
    private GridLayoutManagerTV sensorlayoutManager;
    private GridLayoutManagerTV layoutManager;
    private GridLayoutManagerTV scenelayoutManager;

    private ArrayList<AppInfo> mApps = null; // All Apps

    private List<CameraEntity> cameraList;
    private SceneAdapter mSceneAdapter;


    public static final float DEFAULT_SCALE = 1.2f;
    public static final int KEYCODE_SEN5_SMARTHOME_SCENE_F1 = 5094;
    public static final int KEYCODE_SEN5_SMARTHOME_SCENE_F2 = 5095;
    public static final int KEYCODE_SEN5_SMARTHOME_SCENE_F3 = 5096;
    public static final int KEYCODE_SEN5_SMARTHOME_SCENE_F4 = 5097;

    public static final String IPCAMERA_PAKENAME = "com.ipcamerasen5.main";
    public static final String IPCAMERA_PAKENAME_NEW = "com.ipcamerasen5.main";

    private long destroyTime = 2000;


    private GridLayoutManagerTV mMoreDevlManager;
    private LinearLayoutManagerTV mApplayoutManager;
    /**
     * App列表左侧获取焦点标志
     */
    boolean isAppLeftFocus;
    /**
     * App列表右侧获取焦点标志
     */
    boolean isAppRightFocus;


    /**
     * popwindow显示的时间间隔
     */
    private long ppShowTime;


    private int[] screen;

    private Intent serviceIntent;

    private int[] mCameraDefImage = {R.drawable.bg_camera_default, R.drawable.bg_camera_default, R.drawable.bg_camera_default, R.drawable.bg_camera_default};
    private Intent mDevIntent;

    private boolean isBind;
    private int frameWidth;

    private Sen5TextView mTvDevTitle, mTvSceneTitle, mTvMoreDevTitle;


    private boolean isPause;
    /**
     * 是否初始化Camera
     */
    public static boolean isInitCamera;


    private VersionsControl versionsControl;


    private View mSceneChoseView;
    private int mStartingPosition;
    private int mCurrentPosition;


    @Override
    public int getLayoutId() {
        return R.layout.activity_launcher;
    }

    @Override
    public void initView() {

        versionsControl = new VersionsControl(1, this);
        versionsControl.onCreate(getIntent());

        //配置button主题，设置button的是否可见
        ToolsFragment fragment = (ToolsFragment) getFragmentManager().findFragmentById(R.id.fg_tools);
        fragment.setConfig("/etc/config_launcher");

        mHScrollView = (HorizontalScrollView) findViewById(R.id.hScrollView);
        mFgmentViewGroup = (ViewGroup) findViewById(R.id.fgment_tools);
        setFocusFgmentToolListener(mFgmentViewGroup);

        mainUpView1 = (MainUpView) findViewById(R.id.mainUpView1);
        mainUpView1.setEffectBridge(new RecyclerViewBridge());

        // 注意这里，需要使用 RecyclerViewBridge 的移动边框 Bridge.
        mRecyclerViewBridge = (RecyclerViewBridge) mainUpView1.getEffectBridge();
        mRecyclerViewBridge.setUpRectResource(R.drawable.video_cover_cursor);
        setDrawUpRectPaddingTwo(R.dimen.w_32, R.dimen.w_30);
        new UtilsRecyclerViewBridge(mRecyclerViewBridge, UtilsRecyclerViewBridge.isHideMoveUI);


        mflContext = (FrameLayout) findViewById(R.id.layout_context);
        mLlCamera = (LinearLayout) findViewById(R.id.ll_camera);

        mLlDev = (RelativeLayout) findViewById(R.id.ll_dev);
        mLlMoreDev = (RelativeLayout) findViewById(R.id.ll_more_dev);

        mScaleRed = (ScaleAbleView) findViewById(R.id.btn_scene_red);
        mScaleYellow = (ScaleAbleView) findViewById(R.id.btn_scene_yellow);
        mScaleGreen = (ScaleAbleView) findViewById(R.id.btn_scene_green);
        mScaleBlue = (ScaleAbleView) findViewById(R.id.btn_scene_blue);
        mScaleAdd = (ScaleAbleView) findViewById(R.id.btn_add_devices);

        mRecyclerScene = (RecyclerViewTV) findViewById(R.id.RecyclerViewScene);
        mRecyclerMoreDevice = (RecyclerViewTV) findViewById(R.id.RecyclerViewMoreDev);

        mVvDTV = (VideoView) findViewById(R.id.dtv);
        HolderDTV.initPlayShowVideo(new Handler(), this, mVvDTV);
        mVvDTV.setFocusable(false);


        mFLCamera = (FrameLayout) findViewById(R.id.fl_camera);


        mTvDevTitle = (Sen5TextView) findViewById(R.id.tv_dev_title);
        mTvSceneTitle = (Sen5TextView) findViewById(R.id.tv_scene_title);
        mTvMoreDevTitle = (Sen5TextView) findViewById(R.id.tv_more_dev_title);


        initCameraView();
        // TODO: 2017/12/6 过场动画
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            setExitSharedElementCallback(mCallback);
//        }

    }


    private void initCameraView() {


        cameraList = CameraSourceControl.getInstance().getCameraSource();
        CameraControl.getInstance().setType(CameraSourceControl.getInstance().getCameraType());


        CameraControl.getInstance().init(LauncherActivity.this, mLlCamera, onCameraFocusChangeListener, onCameraClickListener, onCameraKeyListener);
    }


    @Override
    public void initControl() {
        //壁纸
        versionsControl.WallpaperControl(mflContext);

        //摄像头设备服务
        mDevIntent = new Intent(this, DeviceSDKService.class);
        bindService(mDevIntent, mCameraConnection, Context.BIND_AUTO_CREATE);

        //安防设备服务
        serviceIntent = new Intent(this, P2PConnectService.class);

        startP2P();


        //设置场景监听
        setSceneListener();

        findViewById(R.id.img_settings).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                }

                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {

                    return true;
                }


                return false;
            }
        });

        findViewById(R.id.img_clean_memory).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                }

                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    return true;
                }


                return false;
            }
        });

        mScaleAdd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {

                    showMoreDevExpend(false);
//                mCurrentFocusView = view;
                    mRecyclerViewBridge.setUpRectResource(R.drawable.video_cover_cursor);
                    setDrawUpRectPaddingTwo(R.dimen.w_32, R.dimen.w_30);

                    mRecyclerViewBridge.setFocusView(view, DEFAULT_SCALE);
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }

                } else {
//                mCurrentFocusView = null;
                    mRecyclerViewBridge.setUnFocusView(view);
                }
            }
        });

        mScaleAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = JsonCreator.createAddDevice();
                p2PModel.sendData(str);

            }
        });

    }


    @Override
    public void initData() {
        mApplication = (LauncherApplication) getApplication();

        mModel = mApplication.setLauncher(this, this);
        mApps = mModel.getAllApps();

        screen = MetricsUtils.getScreenHight(this);

        initTimeAndDate();


        initDevice();
        initScene();
        initSensor();
        initMoreDevice();
        initRecycleViewApp();

        startRecordService((ArrayList<CameraEntity>) cameraList);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                /**
                 * 初始化camera
                 */
                CameraControl.getInstance().initCamera((ArrayList<CameraEntity>) cameraList);


                isInitCamera = true;


            }
        }, 500);


        CameraControl.getInstance().synchronousTime();

    }


    private void startP2P() {
        //开机启动P2P连接服务
        AppLog.e(TAG + "开启安防服务在Launcher");
        p2PModel = P2PModel.getInstance(this);

        String encryptKey = Utils.getQRInfo(this);
        serviceIntent.putExtra("EncryptKey", encryptKey);
        LauncherActivity.this.startService(serviceIntent);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        lastIsPlay = true;
        if (versionsControl != null)
            versionsControl.onCreate(getIntent());
    }

    /**
     * 开启录制全局服务
     */
    private void startRecordService(ArrayList<CameraEntity> cameraList) {


        if (cameraList.isEmpty()) {
            return;
        }

        // TODO: 2017/9/27 合入IPCamera改
        Intent intent = null;
        if (CameraSourceControl.getInstance().getCameraType(cameraList) == 1) {
            intent = new Intent();
            intent.setComponent(new ComponentName(IPCAMERA_PAKENAME, "com.ipcamerasen5.record.service.HSLRecordService"));
            AppLog.e(TAG + "服务 RecordService：开启老摄像头录制");


            if (isServiceWork(this, "com.ipcamerasen5.record.service.HSLRecordService", IPCAMERA_PAKENAME)) {
                return;
            }


            AppLog.e("是否存在新摄像头:" + isServiceWork(this, "com.ipcamerasen5.record.service.RecordService", "com.sen5.secure.launcher"));


        } else {
            intent = new Intent(this, RecordService.class);

            AppLog.e(TAG + "服务 RecordService：开启新摄像头录制");


            if (isServiceWork(this, "com.ipcamerasen5.record.service.RecordService", "com.sen5.secure.launcher")) {
                return;
            }

            AppLog.e("是否存在老摄像头:" + isServiceWork(this, "com.ipcamerasen5.record.service.HSLRecordService", IPCAMERA_PAKENAME));

        }

        this.startService(intent);


        //让IPCamera更新数据库
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    private View.OnClickListener onCameraClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String gid = (String) v.getTag();

            CameraView cameraView = null;
            LTCameraView transitionView = null;

            if (TextUtils.isEmpty(gid)) {
                return;
            }

            int position = 0;


            for (int i = 0; i < mLlCamera.getChildCount(); i++) {

                if (v instanceof CameraView) {

                    cameraView = (CameraView) mLlCamera.getChildAt(i);

                    if (TextUtils.isEmpty(cameraView.mDid)) {
                        continue;
                    }

                    if (cameraView.getmStatus() == CameraView.status.online) {
                        int status = CutPicUtils.getInstace().setSavePath(cameraView.mDid + ".jpg").setDid(cameraView.mDid).setUserId(cameraView.mUserid).cut();
                        AppLog.i("截图状态 UIStatus:" + status);
                    }


                    if (v == cameraView) {
                        position = i;
                    }

                    cameraView.stopPlayStream();

                } else if (v instanceof LTCameraView) {


                    LTCameraView ltCameraView = (LTCameraView) mLlCamera.getChildAt(i);


                    if (ltCameraView.getLtDevice() == null) {
                        continue;
                    }

                    if (((LTCameraView) v).getmUIStatus() == LTCameraView.UIStatus.online) {
                        int status = CutPicUtils.getInstace()
                                .setSavePath(((LTCameraView) v).getLtDevice().getGid() + ".jpg")
                                .setDid(((LTCameraView) v).getLtDevice().getGid())
                                .setBitmap(((LTCameraView) v).getmLastFrame())
                                .cut();

                        AppLog.i("startWindow 截图状态 LT UIStatus:" + status);
                    }
                    ltCameraView.removeCameraView();


                    if (v == ltCameraView) {
                        position = i;
                        transitionView = (LTCameraView) v;
                    }
                }

            }
            mStartingPosition = position;
            mCurrentPosition = position;

            isPause = true;
            ComponentName component;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            // TODO: 2017/9/27 合入IPCamera改

            if (CameraSourceControl.getInstance().getCameraType() == 1) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);

                component = new ComponentName(IPCAMERA_PAKENAME, "com.ipcamerasen5.main.MainActivity");

                intent.setComponent(component);

                intent.putExtra("default_did", gid);
                intent.putExtra("default_position", position);

                AppLog.e("进入老IPCamera" + "default_position:" + position);
                LauncherActivity.this.startActivity(intent);
                launcherControlDvb(false);

            } else {
                AppLog.e("进入新IPCamera" + "default_position:" + position);
                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                intent.putExtra("default_did", gid);
                intent.putExtra("default_position", position);
                launcherControlDvb(false);
//                if (android.os.Build.VERSION.SDK_INT > 20) {
//
//                    // TODO: 2017/12/6 过场动画
//                    AppLog.e("进入新IPCamera:过场动画");
//                    LauncherActivity.this.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LauncherActivity.this, transitionView.mLlCamera, "ltCamera").toBundle());
//
//
//                } else {
                LauncherActivity.this.startActivity(intent);

//                }


            }


//
        }
    };

    private View.OnFocusChangeListener onCameraFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            AppLog.d("onCameraFocusChangeListener::yjx :: hasFocus = " + hasFocus);
            if (hasFocus) {
                showMoreDevExpend(false);
            }

            if (v instanceof CameraView) {
                ((CameraView) v).setFocusFrame(hasFocus);
            } else if (v instanceof LTCameraView) {
                ((LTCameraView) v).setFocusFrame(hasFocus);
            }


        }
    };

    /***
     * 控制更多设备的展开
     * @param isExpend
     */
    private void showMoreDevExpend(boolean isExpend) {
        if (moreDevPresenter.isExpend() != isExpend) {

            mTvMoreDevTitle.setText(getString(R.string.more_devices_long));

            if (!isExpend) {
                mHScrollView.smoothScrollTo(0, 0);
                mTvMoreDevTitle.setText(getString(R.string.more_devices));

            }
            moreDevPresenter.showExpend(isExpend);
        }
    }

    private View.OnKeyListener onCameraKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (event.getAction() != KeyEvent.ACTION_DOWN) {
                return false;
            }

            if (v == mLlCamera.getChildAt(0))
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    return true;
                }

            if (v == mLlCamera.getChildAt(3))
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    return true;
                }


            return false;
        }
    };


    private void setFocusFgmentToolListener(ViewGroup viewGroup) {
        if (null != viewGroup) {

            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = (View) viewGroup.getChildAt(i);
                if (view instanceof ViewGroup) {

                }

                if (null != view) {
                    view.setOnFocusChangeListener(mOnFocusChangeListener);
                }
            }
        }

    }


    private void setSceneListener() {


        mScaleRed.setOnFocusChangeListener(mOnFocusChangeListener);
        mScaleYellow.setOnFocusChangeListener(mOnFocusChangeListener);
        mScaleGreen.setOnFocusChangeListener(mOnFocusChangeListener);
        mScaleBlue.setOnFocusChangeListener(mOnFocusChangeListener);
        mScaleRed.setOnClickListener(mOnScenClickListener);
        mScaleYellow.setOnClickListener(mOnScenClickListener);
        mScaleGreen.setOnClickListener(mOnScenClickListener);
        mScaleBlue.setOnClickListener(mOnScenClickListener);


    }

    private void setSelectScene(View view) {
        if (view != mSceneChoseView) {
            if (view instanceof ScaleAbleView) {
                view.setSelected(true);

            } else {
                setTopImage((Sen5TextView) view, true);

            }


            if (mSceneChoseView != null) {
                if (mSceneChoseView instanceof ScaleAbleView) {
                    mSceneChoseView.setSelected(false);

                } else {
                    setTopImage((Sen5TextView) mSceneChoseView, false);


                }
            }
            mSceneChoseView = view;
        }
    }

    private void setTopImage(Sen5TextView tv, boolean isSelect) {

        if (isSelect) {
            Drawable drawable = this.getResources().getDrawable(R.drawable.ic_scene_new_s);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv.setCompoundDrawables(null, drawable, null, null);
        } else {
            Drawable drawable = this.getResources().getDrawable(R.drawable.ic_scene_new_n);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv.setCompoundDrawables(null, drawable, null, null);
        }
    }


    private void initScene() {


        mRecyclerScene.setItemAnimator(null);
        mRecyclerScene.setOnItemListener(mDevOnRecyItemListener);
        mRecyclerScene.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                SceneData sceneData = mSceneAdapter.getList().get(position);
                if (sceneData == null) {
                    return;
                }

                setSelectScene(((LinearLayout) itemView).getChildAt(0));

                String scene = JsonCreator.createApplySceneJson(sceneData.getScene_id());
                p2PModel.sendData(scene);
            }
        });

        scenelayoutManager = new GridLayoutManagerTV(this, 2, GridLayoutManager.HORIZONTAL, false);
        scenelayoutManager.setAutoMeasureEnabled(true);
        mRecyclerScene.setLayoutManager(scenelayoutManager);
        mRecyclerScene.setFocusable(true);
//        mRecyclerScene.setSelectedItemAtCentered(true); // 设置item在中间移动. 默认为true
        mSceneAdapter = new SceneAdapter();
        mSceneAdapter.setList(Collections.synchronizedList(new ArrayList<SceneData>()));
        mRecyclerScene.setAdapter(mSceneAdapter);
        mRecyclerScene.addItemDecoration(new DividerGridItemDecoration(this));


    }


    private void initDevice() {
        mRecyclerDevice = (RecyclerViewTV) findViewById(R.id.RecyclerViewDevice);
        mRecyclerDevice.setItemAnimator(null);
        mRecyclerDevice.setOnItemListener(mDevOnRecyItemListener);
        mRecyclerDevice.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {

                DeviceData data = deviceAdapter.getList().get(position);

                if (data.getStatus() != null && !data.getStatus().isEmpty())
                    for (DeviceStatusData statusData : data.getStatus()) {
                        if (statusData.getId() == 1) {
                            byte[] params = new byte[1];
                            params[0] = (byte) (statusData.getParams()[0] == 0 ? 1 : 0);
                            p2PModel.sendData(JsonCreator.updateDeviceStatus(data.getDev_id(), statusData.getId(), params));
                            byte[] param = statusData.getParams();
                            param[0] = params[0];
                            statusData.setParams(param);

                            break;
                        } else if (statusData.getId() == Constant.ZB_ONLINE_AND_STATUS && data.getStatus().size() == 1) {
                            byte[] params = new byte[1];
                            if (statusData.getParams()[0] == 1) {
                                params[0] = (byte) (statusData.getParams()[1] == 0 ? 1 : 0);
                                p2PModel.sendData(JsonCreator.updateDeviceStatus(data.getDev_id(), statusData.getId(), params));

                                byte[] param = statusData.getParams();
                                param[1] = params[0];
                                statusData.setParams(param);

                            }

                            break;

                        }
                    }
            }
        });

        mDevlayoutManager = new GridLayoutManagerTV(this, 2, GridLayoutManager.HORIZONTAL, false);
        mDevlayoutManager.setAutoMeasureEnabled(true);
        mRecyclerDevice.setLayoutManager(mDevlayoutManager);
        mRecyclerDevice.setFocusable(true);
        mRecyclerDevice.setSelectedItemAtCentered(true); // 设置item在中间移动. 默认为true
        mRecyclerDevice.addItemDecoration(new DividerGridItemDecoration(this));
        deviceAdapter = new DevicePresenter();
        deviceAdapter.setHasStableIds(true);
        deviceAdapter.setList(Collections.synchronizedList(p2PModel.getmDevices()));

        mRecyclerDevice.setAdapter(deviceAdapter);


    }


    private void initSensor() {


        mRecyclerSensor = (RecyclerViewTV) findViewById(R.id.RecyclerViewSensor);
        mRecyclerSensor.setItemAnimator(new DefaultItemAnimator());
        mRecyclerSensor.addItemDecoration(new DividerGridItemDecoration(this));

        sensorlayoutManager = new GridLayoutManagerTV(this, 2, GridLayoutManager.HORIZONTAL, false);
        sensorlayoutManager.setAutoMeasureEnabled(true);
        mRecyclerSensor.setLayoutManager(sensorlayoutManager);
        mRecyclerSensor.setFocusable(false);
        mRecyclerSensor.setSelectedItemAtCentered(true); // 设置item在中间移动. 默认为true
        sensorAdapter = new SensorPresenter(this, p2PModel);

        sensorAdapter.setList(Collections.synchronizedList(p2PModel.getmSensor()));
        mRecyclerSensor.setAdapter(sensorAdapter);
    }


    private void initMoreDevice() {

        int[] img = {
                R.drawable.selector_door_sensor,
                R.drawable.selector_gas_sensor,
                R.drawable.selector_leak_sensor,
                R.drawable.selector_light_sensor,

                R.drawable.selector_motion_sensor,
                R.drawable.selector_outlet_sensor,
                R.drawable.selector_shock_sensor,
                R.drawable.selector_siren_sensor,
                R.drawable.selector_co_sensor,
                R.drawable.selector_smoke_sensor,
                R.drawable.selector_tem_sensor,
                R.drawable.selector_termostat_sensor};

        String[] name = {
                this.getResources().getString(R.string.door_sensor),
                this.getResources().getString(R.string.pop_Combustible_gas),
                this.getResources().getString(R.string.pop_leak),
                this.getResources().getString(R.string.light_device),

                this.getResources().getString(R.string.Motion),
                this.getResources().getString(R.string.Outlet),
                this.getResources().getString(R.string.shock_sensor),
                this.getResources().getString(R.string.Siren),
                this.getResources().getString(R.string.Co),
                this.getResources().getString(R.string.Smoke),
                this.getResources().getString(R.string.Tem_Humid),
                this.getResources().getString(R.string.Thermostat)};

        String[] des = {
                this.getResources().getString(R.string.door_sensor_des),
                this.getResources().getString(R.string.pop_Gombustible_gas_des),
                this.getResources().getString(R.string.pop_leak_des),
                this.getResources().getString(R.string.light_device_des),

                this.getResources().getString(R.string.Motion_des),
                this.getResources().getString(R.string.Outlet_des),
                this.getResources().getString(R.string.shock_sensor_des),
                this.getResources().getString(R.string.Siren_des),
                this.getResources().getString(R.string.Co_des),
                this.getResources().getString(R.string.Smoke_des),
                this.getResources().getString(R.string.Tem_Humid_des),
                this.getResources().getString(R.string.Thermostat_des)};

        int[] realImg = {R.drawable.door_sensor, R.drawable.combusible_gas, R.drawable.leak, R.drawable.light,
                R.drawable.motion, R.drawable.outlet, R.drawable.shock, R.drawable.siren, R.drawable.co, R.drawable.smoke, R.drawable.temp_humid, R.drawable.thermostat};


        ArrayList<DeviceBean> list = new ArrayList<>();

        DeviceBean bean = new DeviceBean();
        list.add(bean);

        for (int i = 0; i < img.length; i++) {
            bean = new DeviceBean();
            bean.setDevImg(img[i]);
            bean.setDevName(name[i]);
            bean.setDevDes(des[i]);
            bean.setDevRealImg(realImg[i]);
            list.add(bean);
        }


        if (list.isEmpty()) {
            mLlMoreDev.setVisibility(View.GONE);
            return;
        }

        mLlMoreDev.setVisibility(View.VISIBLE);


        mRecyclerMoreDevice.setItemAnimator(null);
        mRecyclerMoreDevice.setOnItemListener(mDevOnRecyItemListener);


        mMoreDevlManager = new GridLayoutManagerTV(this, 2, GridLayoutManager.HORIZONTAL, false);
        mMoreDevlManager.setAutoMeasureEnabled(true);

        mRecyclerMoreDevice.setSelectedItemAtCentered(true); // 设置item在中间移动. 默认为true
        moreDevPresenter = new MoreDevPresenter(this);

        mRecyclerMoreDevice.addItemDecoration(new DividerGridItemDecoration(this));
        mRecyclerMoreDevice.setLayoutManager(mMoreDevlManager);
        mRecyclerMoreDevice.setFocusable(true);
        moreDevPresenter.setHasStableIds(true);
        moreDevPresenter.setList(list);
        mRecyclerMoreDevice.setAdapter(moreDevPresenter);


        moreDevPresenter.setKeyListener(new ItemOnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event, int position) {


                int size = moreDevPresenter.getItemCount();

                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                }


                if (position == (size == 1 ? 0 : size - 2) && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//                    mHScrollView.smoothScrollTo(0, 0);
//                    mScaleGreen.requestFocus();
                    return true;

                } else if (position != 0 && position == size - 1 && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//                    mHScrollView.smoothScrollTo(0, 0);
//                    if (mApplayoutManager.findFirstVisibleItemPosition() == 0) {
//                        mApplayoutManager.findViewByPosition(0).requestFocus();
//                    } else {
//                        mApplayoutManager.scrollToPosition(0);
//                        isAppLeftFocus = true;
//                    }

                    return true;
                }


                return false;
            }
        });
    }


    private void initTimeAndDate() {
        mClock = (TextClock) findViewById(R.id.txt_clock);
        mClock.setTimeChangeListener(new TextClock.TimeChangeListener() {
            @Override
            public void onTimeChanged(boolean is24Formate) {
                String sData = UtilsTimeFormat.getTimeDataForSystemFormat(LauncherActivity.this);
                String sWeek = UtilsTimeFormat.getTimeWeek(LauncherActivity.this);
                if (null != mData) {

                    mData.setText(sData + "\n" + sWeek);
                }
            }
        });
        mData = (Sen5TimerTextView) findViewById(R.id.txt_data);

        //后续需要在时间刷新中调用
        String sData = UtilsTimeFormat.getTimeDataForSystemFormat(this);
        String sWeek = UtilsTimeFormat.getTimeWeek(this);
        mData.setText(sData + "\n" + sWeek);
    }


    private void initRecycleViewApp() {
        mRecyclerApp = (RecyclerViewTV) findViewById(R.id.RecyclerViewApp);
        mRecyclerApp.setItemAnimator(new DefaultItemAnimator());
        mRecyclerApp.setOnItemListener(mDevOnRecyItemListener);
        mRecyclerApp.setOnItemClickListener(mOnRecyItemClickListener);
        mRecyclerApp.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isAppLeftFocus) {
                    mApplayoutManager.findViewByPosition(0).requestFocus();
                    isAppLeftFocus = false;
                }
                if (isAppRightFocus) {
                    mApplayoutManager.findViewByPosition(appPresenter.getItemCount() - 1).requestFocus();
                    isAppRightFocus = false;
                }
            }
        });

        appPresenter = new AppRecycleViewPresenter(mApps);
        GeneralAdapter mGeneralAdapter = new GeneralAdapter(appPresenter);

        mApplayoutManager = new LinearLayoutManagerTV(this);
        mApplayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mApplayoutManager.setAutoMeasureEnabled(true);
        mRecyclerApp.setLayoutManager(mApplayoutManager);
        mRecyclerApp.setFocusable(false);
        mRecyclerApp.setSelectedItemAtCentered(true); // 设置item在中间移动. 默认为true
        mRecyclerApp.setAdapter(mGeneralAdapter);

        appPresenter.setKeyListener(new ItemOnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event, int position) {

                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                }

                if (position == 0 && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
//                    int size = moreDevPresenter.getItemCount();
//                    mMoreDevlManager.findViewByPosition(size - 1).requestFocus();
                    return true;
                } else if (position == appPresenter.getItemCount() - 1 && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//                    findViewById(R.id.img_clean_memory).requestFocus();
                    return true;
                }


                return false;
            }
        });
    }


    private RecyclerViewTV.OnItemListener mDevOnRecyItemListener = new RecyclerViewTV.OnItemListener() {
        @Override
        public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
            AppLog.e("-----------------position dev pre = " + position);
            setBottomTextFocus(itemView, false);
            mRecyclerViewBridge.setUnFocusView(itemView);
            mRecyclerViewBridge.setUnFocusView(parent.getChildAt(parent.getLastVisiblePosition()));

        }

        @Override
        public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
            AppLog.e("-----------------position  dev current = " + position);

            int[] location = new int[2];
            itemView.getLocationOnScreen(location);

            if (parent == mRecyclerScene) {
                mRecyclerScene.bringToFront();
            }


            setDrawUpRectPadding(R.dimen.w_17, R.dimen.w_65);
            setBottomTextFocus(itemView, true);
            mRecyclerViewBridge.setFocusView(itemView, DEFAULT_SCALE);

            if (parent == mRecyclerMoreDevice) {

                if (position == 0) {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    showMoreDevExpend(true);
                } else {
                    showExplainPp(itemView);

                }

            } else {
                showMoreDevExpend(false);
                if (popupWindow != null && popupWindow.isShowing())
                    popupWindow.dismiss();
            }

            mCurrentFocusView = itemView;

        }

        @Override
        public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
            AppLog.e("-----------------position dev revise = " + position);
            mRecyclerViewBridge.setFocusView(itemView, DEFAULT_SCALE);


        }
    };


    private RecyclerViewTV.OnItemClickListener mOnRecyItemClickListener = new RecyclerViewTV.OnItemClickListener() {

        @Override
        public void onItemClick(RecyclerViewTV parent, View itemView, int position) {

            try {
                Object ob = itemView.getTag();
                if (null != ob && ob instanceof AppInfo) {
                    AppInfo info = (AppInfo) itemView.getTag();
                    if (null != info && !TextUtils.isEmpty(info.componentName.getPackageName())) {
                        mApplication.launchApp(info.componentName.getPackageName(), true);
                        launcherControlDvb(false);
                        return;
                    }
                }

            } catch (Exception e) {
            }
            //  Toast.makeText(LauncherActivity.this,"aa" + position, Toast.LENGTH_SHORT).show();
        }

    };


    private View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View view, boolean focus) {
            if (focus) {

                showMoreDevExpend(false);

                view.bringToFront();
//                mCurrentFocusView = view;
                mRecyclerViewBridge.setUpRectResource(R.drawable.video_cover_cursor);
                setDrawUpRectPaddingTwo(R.dimen.w_32, R.dimen.w_30);

                mRecyclerViewBridge.setFocusView(view, DEFAULT_SCALE);
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }

            } else {
//                mCurrentFocusView = null;
                mRecyclerViewBridge.setUnFocusView(view);
            }
        }
    };

    private View.OnClickListener mOnScenClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SceneData data = (SceneData) v.getTag();
            if (data == null) {
                return;
            }
            setSelectScene(v);
            String scene = JsonCreator.createApplySceneJson(data.getScene_id());
            p2PModel.sendData(scene);
        }
    };


    private void setScenceData() {

        mScaleRed.setText("");
        mScaleGreen.setText("");
        mScaleYellow.setText("");
        mScaleBlue.setText("");


        if (p2PModel.getScenes() == null || p2PModel.getScenes().isEmpty()) {

            return;
        }

        int size = p2PModel.getScenes().size();

        for (int i = 0; i < (size < 4 ? size : 4); i++) {
            SceneData data = p2PModel.getScenes().get(i);
            switch (i) {
                case 0:
                    mScaleRed.setText(data.getScene_name());
                    mScaleRed.setTag(data);
                    break;
                case 1:
                    mScaleGreen.setText(data.getScene_name());
                    mScaleGreen.setTag(data);

                    break;
                case 2:
                    mScaleYellow.setText(data.getScene_name());
                    mScaleYellow.setTag(data);

                    break;
                case 3:
                    mScaleBlue.setText(data.getScene_name());
                    mScaleBlue.setTag(data);

                    break;
            }
        }


        List<SceneData> moreScene = new ArrayList<>();


        if (size > 4) {
            for (int i = 4; i < size; i++) {
                moreScene.add(p2PModel.getScenes().get(i));
            }

        } else {
            mSceneAdapter.getList().clear();
            mSceneAdapter.notifyDataSetChanged();
            mRecyclerScene.setVisibility(View.GONE);
            return;
        }


        if (moreScene.isEmpty()) {
            if (mRecyclerScene != null)
                mRecyclerScene.setVisibility(View.GONE);
            return;
        }


        mRecyclerScene.setVisibility(View.VISIBLE);
        mSceneAdapter.getList().clear();
        mSceneAdapter.addList((ArrayList<SceneData>) moreScene);
    }


    private void setBottomTextFocus(View vg, boolean selected) {
        if (null != vg && vg instanceof ViewGroup) {
            int count = ((ViewGroup) vg).getChildCount();
            if (count > 1) {
                View v = ((ViewGroup) vg).getChildAt(1);
                if (v instanceof TextView) {
                    ((TextView) v).setSelected(selected);
                }
            }
        }
    }

    private void setDrawUpRectPaddingTwo(int dimenIdH, int dimenV) {
        float density = getResources().getDisplayMetrics().density;
        RectF receF = new RectF(getResources().getDimension(dimenIdH) * density, getResources().getDimension(dimenV) * density,
                getResources().getDimension(dimenIdH) * density, getResources().getDimension(dimenV) * density);
        mRecyclerViewBridge.setDrawUpRectPadding(receF);
    }

    private void setDrawUpRectPadding(int dimenIdOther, int dimenBottom) {
        float density = getResources().getDisplayMetrics().density;
        RectF receF = new RectF(getResources().getDimension(dimenIdOther) * density, getResources().getDimension(dimenIdOther) * density,
                getResources().getDimension(dimenIdOther) * density, -getResources().getDimension(dimenBottom) * density);
        mRecyclerViewBridge.setDrawUpRectPadding(receF);
    }

    private void showExplainPp(View parent) {

        if (System.currentTimeMillis() - ppShowTime > 300) {


            if (popupWindow == null) {
                View view = LayoutInflater.from(this).inflate(R.layout.view_pop_explain, null);
                popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mTvDev = (Sen5TextView) view.findViewById(R.id.tv_dev_name);
                mTvDevDes = (Sen5TextView) view.findViewById(R.id.tv_dev_des);
                mIvDev = (ImageView) view.findViewById(R.id.iv_dev);
            }

            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }


            DeviceBean bean = (DeviceBean) parent.getTag();

            if (bean != null) {
                mTvDev.setText(bean.getDevName());
                mTvDevDes.setText(bean.getDevDes());
                mIvDev.setImageResource(bean.getDevRealImg());
            }


            int[] location = new int[2];
            parent.getLocationOnScreen(location);
            int position = mRecyclerMoreDevice.getChildLayoutPosition(parent);
            int right = (int) (screen[0] - (location[0] + this.getResources().getDimension(R.dimen.dimen_94)));

            if (right < this.getResources().getDimension(R.dimen.dimen_300)) {
                mHScrollView.scrollBy((int) this.getResources().getDimension(R.dimen.dimen_300) - right + 40, 0);
            }

            location = new int[2];
            parent.getLocationOnScreen(location);

            if (position % 2 == 0) {
                popupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, location[0] + parent.getWidth() + 20,
                        location[1] - 10);
            } else {
                popupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, location[0] + parent.getWidth() + 20, (int) (location[1] - (this.getResources().getDimension(R.dimen.dimen_198) - this.getResources().getDimension(R.dimen.dimen_94) - 10)));
            }

            ppShowTime = System.currentTimeMillis();

        } else {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }

        }


    }


    @Override
    public void bindAppsAdded(ArrayList<AppInfo> apps) {
        AppLog.i(TAG + "bindAppsAdded :" + apps.size());
        appPresenter.notifyDataSetChanged();

    }

    @Override
    public void bindAppsUpdated(ArrayList<AppInfo> apps) {

        AppLog.i(TAG + "bindAppsUpdated :" + apps.size());


        appPresenter.notifyDataSetChanged();
    }

    @Override
    public void bindAppsRemoved(ArrayList<AppInfo> apps, boolean permanent) {

        AppLog.i(TAG + "bindAppsRemoved :" + apps.size());

        appPresenter.notifyDataSetChanged();
    }

    @Override
    public void bindPackagesUpdated() {

        AppLog.i(TAG + "bindPackagesUpdated :");

        appPresenter.notifyDataSetChanged();
    }

    @Override
    public void titleReceiveData(Intent intent) {

    }

    @Override
    public void changeHDMIMode() {

    }

    @Override
    public void onHomeKey() {

    }

    @Override
    public void CameraChange(final int type, final String ipcId) {


        List<CameraEntity> newList = null;
        try {
            newList = CameraSourceControl.getInstance().initCameraSource();
        } catch (Exception e) {
            e.printStackTrace();

            newList = new ArrayList<>();
        }

        //先开启服务
        startRecordService((ArrayList<CameraEntity>) newList);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //转发Camera被修改的广播给IPCamera
                Intent intent = new Intent();
                intent.setAction("com.sen5.launcher.camerachange");
                intent.putExtra("type", type);
                intent.putExtra("CameraDID", ipcId);
                LauncherActivity.this.sendBroadcast(intent);
                AppLog.e(TAG + "cameraChange");
            }
        }).start();

        //让IPCamera更新数据库
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (type == 0 && !TextUtils.isEmpty(ipcId)) {

            if (!ipcId.startsWith("SLIFE") && CameraControl.getInstance().getType() == 1) {

                mLlCamera.removeAllViews();
                CameraControl.getInstance().setType(0);
                CameraControl.getInstance().init(LauncherActivity.this, mLlCamera, onCameraFocusChangeListener, onCameraClickListener, onCameraKeyListener);

            } else if (ipcId.startsWith("SLIFE") && CameraControl.getInstance().getType() == 0) {

                mLlCamera.removeAllViews();
                CameraControl.getInstance().setType(1);
                CameraControl.getInstance().init(LauncherActivity.this, mLlCamera, onCameraFocusChangeListener, onCameraClickListener, onCameraKeyListener);

            }


        }


        switch (type) {
            //0：是增加摄像头
            case 0:


                for (CameraEntity entity : newList) {
                    if (entity.getDeviceID().equals(ipcId)) {
                        AppLog.i("添加摄像头" + "gid:" + ipcId);
                        CameraControl.getInstance().addCamera(ipcId);

                        break;
                    }

                }
                break;
            //删除摄像头
            case 1:


                CameraControl.getInstance().deleteCamera(ipcId);


                break;


        }


    }

    @Override
    public void CameraClose(String activity) {
        mApplication.activityName = activity;


        CameraControl.getInstance().playAll();
    }

    @Override
    public void startWindow(String mac) {

        int type = 0;

        for (int i = 0; i < mLlCamera.getChildCount(); i++) {

            final View view = mLlCamera.getChildAt(i);


            if (view instanceof LTCameraView) {

                if (((LTCameraView) view).getLtDevice() == null) {
                    ((LTCameraView) view).removeCameraView();
                    type = 0;
                    continue;
                }

                if (((LTCameraView) view).getmUIStatus() == LTCameraView.UIStatus.online) {

//                    Observable.create(new ObservableOnSubscribe<String>() {
//                        @Override
//                        public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {

                    int status = CutPicUtils.getInstace()
                            .setSavePath(((LTCameraView) view).getLtDevice().getGid() + ".jpg")
                            .setDid(((LTCameraView) view).getLtDevice().getGid())
                            .setBitmap(((LTCameraView) view).getmLastFrame())
                            .cut();

//                            e.onNext(status + "");
//                            e.onComplete();
//
//                        }
//                    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableObserver<String>() {
//                        @Override
//                        public void onNext(@NonNull String s) {
//                            AppLog.i("startWindow 截图状态 LT UIStatus:" + s);
//
//                        }
//
//                        @Override
//                        public void onError(@NonNull Throwable e) {
//
//                        }
//
//                        @Override
//                        public void onComplete() {
//
//                        }
//                    });
//
//
                }

//                ((LTCameraView) view).showLastFrame();
                ((LTCameraView) view).removeCameraView();
                type = 0;
//                }

            } else if (view instanceof CameraView) {

                type = 1;

                if (TextUtils.isEmpty(((CameraView) view).mDid)) {
                    continue;
                }

                if (((CameraView) view).getmStatus() == CameraView.status.online) {

                    int status = CutPicUtils.getInstace().setSavePath(((CameraView) view).mDid + ".jpg").setDid(((CameraView) view).mDid).setUserId(((CameraView) view).mUserid).cut();
                    AppLog.i("startWindow 截图状态 UIStatus:" + status);

                }
            }

        }

        AppLog.i("LtDevices" + LTSDKManager.mLTDevices.size());


        if (!TextUtils.isEmpty(mac)) {
            Intent intent = new Intent("com.sen5.process.camera.sensor");
            intent.putExtra("receiver_mac", mac);
            LauncherActivity.this.sendBroadcast(intent);
            AppLog.e(TAG + "startWindow" + "sensor触发广播已发  mac =" + mac);
        }


        String activityName = Utils.getActivityName(this);

        AppLog.e(TAG + "packName" + activityName);
        if (activityName.contains("com.ipcamerasen5")) {
            return;
        }


        Intent intent1 = new Intent();
        intent1.setAction("com.ipcamera.floatviewservice");
        intent1.putExtra("flag_show_view", true);
        intent1.putExtra("receiver_mac", mac);
        intent1.putExtra("type", type);
        intent1.setPackage("com.sen5.secure.launcher");
        LauncherActivity.this.startService(intent1);


    }


    @Override
    protected void onDestroy() {


        versionsControl.onDestroy();

        try {
            DeviceSDK.unInitialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppLog.i("注销Camera SDK");

        if (serviceIntent != null) {
            stopService(serviceIntent);
        }

        if (mDevIntent != null && isBind) {
            unbindService(mCameraConnection);
            stopService(mDevIntent);

        }


        LTSDKManager.getInstance().releaseSDK();

        super.onDestroy();

        System.exit(0);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DeviceEvent event) {
        switch (event.flag) {
            case DeviceEvent.DEV_LIST:

                showDevTitle();

                if (p2PModel.getmDevices().isEmpty()) {
                    mRecyclerDevice.setVisibility(View.GONE);
                } else {
                    mRecyclerDevice.setVisibility(View.VISIBLE);
                    deviceAdapter.notifyDataSetChanged();

                }

                if (p2PModel.getmSensor().isEmpty()) {
                    mRecyclerSensor.setVisibility(View.GONE);
                } else {
                    mRecyclerSensor.setVisibility(View.VISIBLE);
                    sensorAdapter.notifyDataSetChanged();
                }

                break;
            case DeviceEvent.DEV_UPDATE:
                updateStatus(event.getDeviceData());
                break;

            case DeviceEvent.DEV_ADD:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }

                showDevTitle();


                DeviceData data = event.getDeviceData();
                if (data.getMode() == 1) {
                    deviceAdapter.notifyDataSetChanged();
                    mRecyclerDevice.setVisibility(View.VISIBLE);
                } else if (data.getMode() == 0) {
                    sensorAdapter.notifyDataSetChanged();
                    mRecyclerSensor.setVisibility(View.VISIBLE);
                }
                break;


            case DeviceEvent.DEV_REMOVE:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }

                if (event.getDeviceData().getMode() == 1) {

                    if (!deviceAdapter.getList().isEmpty()) {
                        deviceAdapter.notifyDataSetChanged();
                        mRecyclerDevice.setVisibility(View.VISIBLE);

                    } else {
                        mRecyclerDevice.setVisibility(View.GONE);
                    }
                } else if (event.getDeviceData().getMode() == 0) {


                    if (!sensorAdapter.getList().isEmpty()) {
                        sensorAdapter.notifyDataSetChanged();
                        mRecyclerSensor.setVisibility(View.VISIBLE);

                    } else {
                        mRecyclerSensor.setVisibility(View.GONE);
                    }
                }

                showDevTitle();

                break;

            case DeviceEvent.DEV_MODE:

                deviceAdapter.notifyDataSetChanged();

                sensorAdapter.notifyDataSetChanged();
                break;

            case DeviceEvent.DEV_EDIT:

                updateStatus(event.getDeviceData());

                break;

        }

    }

    private void showDevTitle() {
        if (p2PModel.getmDevices().isEmpty() && p2PModel.getmSensor().isEmpty()) {
            mLlDev.setVisibility(View.GONE);
        } else {
            mLlDev.setVisibility(View.VISIBLE);

            if (p2PModel.getmDevices().size() > 2 || p2PModel.getmSensor().size() > 2 || (p2PModel.getmDevices().size() > 0 && p2PModel.getmSensor().size() > 0)) {
                mTvDevTitle.setText(getString(R.string.devices_long));
            } else {
                mTvDevTitle.setText(getString(R.string.devices));
            }

        }
    }

    // TODO: 2017/6/14 设备状态切换的时候，并且焦点正在当前设备上移动 会导致缩放动画无法继续，造成item无法回缩 所以解决方式 是将全局刷新改成局部
    // TODO: BUG: 那么增加和删除是否也会? 待确定
    private void updateStatus(DeviceData data) {


        if (data.getMode() == 1) {

            if (deviceAdapter == null) {
                return;
            }

            for (int i = 0; i < deviceAdapter.getList().size(); i++) {
                if (data.getDev_id() == deviceAdapter.getList().get(i).getDev_id()) {
                    deviceAdapter.notifyItemChanged(i);
                    return;
                }
            }

//            deviceAdapter.notifyDataSetChanged();

        } else if (data.getMode() == 0) {

            if (sensorAdapter == null) {
                return;
            }

            sensorAdapter.notifyDataSetChanged();
        }


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SceneEvent event) {
        switch (event.getFlag()) {
            case SceneEvent.LIST_SCENE:
            case SceneEvent.DELETE_SCENE:
            case SceneEvent.NEW_SCENE:
            case SceneEvent.EDIT_SCENE:
                setScenceData();
                break;

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RoomEvent event) {
        switch (event.getFlag()) {

            case RoomEvent.DELETE_ROOM:
            case RoomEvent.NEW_ROOM:
            case RoomEvent.EDIT_ROOM:
            case RoomEvent.LIST_ROOM:

                deviceAdapter.notifyDataSetChanged();
                sensorAdapter.notifyDataSetChanged();
                break;

        }
    }


    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     *                    com.ipcamerasen5.record.service.RecordService
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName, String packgeName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            String mPackgeName = myList.get(i).service.getPackageName().toString();
            if (mName.equals(serviceName) && mPackgeName.equals(packgeName)) {
                isWork = true;
                break;
            }
        }
        AppLog.d("isServiceWork" + "iswork is " + isWork);
        return isWork;
    }


    ServiceConnection mCameraConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBind = true;
            AppLog.i("isBind:" + isBind + "");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
            AppLog.i("isBind:" + isBind + "");
        }
    };

    @Override
    protected void onPause() {
        super.onPause();


        AppLog.i("isPause:" + isPause + "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLog.i(TAG + "onResume");
        isPause = false;
        launcherControlDvb(true);

    }

    @Override
    protected void onRestart() {
        super.onRestart();


        AppLog.i(TAG + "onRestart");
    }

    @Override
    public void smartHomeKeyCode(int keycode) {
        SceneData data;

//        Log.i("~~~~~~~~~~~~~~~~~~~~~","keycode：" + keycode);

        switch (keycode) {


            case KEYCODE_SEN5_SMARTHOME_SCENE_F1:
                data = (SceneData) mScaleRed.getTag();
                if (data != null) {
                    String scene = JsonCreator.createApplySceneJson(data.getScene_id());
                    p2PModel.sendData(scene);
                    setSelectScene(mScaleRed);
                }

                break;
            case KEYCODE_SEN5_SMARTHOME_SCENE_F2:

                data = (SceneData) mScaleGreen.getTag();
                if (data != null) {
                    String scene = JsonCreator.createApplySceneJson(data.getScene_id());
                    p2PModel.sendData(scene);
                    setSelectScene(mScaleGreen);
                }

                break;
            case KEYCODE_SEN5_SMARTHOME_SCENE_F3:

                data = (SceneData) mScaleYellow.getTag();
                if (data != null) {
                    String scene = JsonCreator.createApplySceneJson(data.getScene_id());
                    p2PModel.sendData(scene);
                    setSelectScene(mScaleYellow);

                }

                break;
            case KEYCODE_SEN5_SMARTHOME_SCENE_F4:
                data = (SceneData) mScaleBlue.getTag();
                if (data != null) {
                    String scene = JsonCreator.createApplySceneJson(data.getScene_id());
                    p2PModel.sendData(scene);
                    setSelectScene(mScaleBlue);
                }

                break;
        }


        RemoteControl.smarthomeKey(this, keycode);

    }

    boolean lastIsPlay = true;

    /**
     * Launcher控制DVB的播放
     *
     * @param start
     */
    public void launcherControlDvb(boolean start) {

        if (lastIsPlay != start) {
            Intent intent = new Intent();
            intent.setAction("com.nes.dvb.play");
            intent.setPackage("com.amlogic.DVBPlayer");
            intent.putExtra("play", start);
            this.sendBroadcast(intent);
            lastIsPlay = start;
        }


    }


    StringBuffer backdoor = new StringBuffer();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_9:
                backdoor.append("9");
                break;
            case KeyEvent.KEYCODE_5:
                backdoor.append("5");
                break;

            case KeyEvent.KEYCODE_8:
                backdoor.append("8");
                break;

            case KeyEvent.KEYCODE_7:
                backdoor.append("7");
                if (backdoor.toString().contains("5987")) {
                    TestLTActivity.launch(this);
                } else {
                    backdoor = new StringBuffer();
                }
                break;
            case KeyEvent.KEYCODE_BACK:

                if (versionsControl.getCode() == 1) {
                    Intent home = new Intent(Intent.ACTION_MAIN);
                    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    home.addCategory(Intent.CATEGORY_HOME);
                    startActivity(home);
                    return true;
                } else {
                    return true;
                }


        }


        return super.onKeyDown(keyCode, event);
    }


// TODO: 2017/12/6 过场动画
//    private SharedElementCallback mCallback = new SharedElementCallback() {
//        @Override
//        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
//            if (mStartingPosition != mCurrentPosition) {
//                // If startingPosition != currentPosition the user must have swiped to a
//                // different page in the DetailsActivity. We must update the shared element
//                // so that the correct one falls into place.
//                names.clear();
//                names.add("ltCamera");
//                sharedElements.clear();
//                sharedElements.put("ltCamera", mLlCamera.getChildAt(mCurrentPosition));
//            }
//        }
//    };

    // TODO: 2017/12/6 过场动画
//    @Override
//    public void onActivityReenter(int resultCode, Intent data) {
//
//        Bundle bundle = new Bundle(data.getExtras());
//        mStartingPosition = bundle.getInt("startPosition");
//        mCurrentPosition = bundle.getInt("currentPosition");
//
//
//        Log.e("startPosition:", mStartingPosition + "");
//        Log.e("currentPosition:", mCurrentPosition + "");
//
//
//        super.onActivityReenter(resultCode, data);
//    }


}

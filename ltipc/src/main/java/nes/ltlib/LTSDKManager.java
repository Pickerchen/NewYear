package nes.ltlib;

import android.app.Application;
import android.content.Context;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import glnk.client.DataSourceListener2;
import glnk.client.GlnkChannel;
import glnk.client.GlnkClient;
import glnk.client.OnLanSearchListener;
import glnk.client.indep.LanSearchIndep;
import glnk.io.OnDeviceStatusChangedListener;
import glnk.media.AView;
import glnk.media.AViewRenderer;
import glnk.media.GlnkDataSource;
import glnk.media.GlnkPlayer;
import glnk.media.VideoRenderer;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import nes.ltlib.data.CameraEntity;
import nes.ltlib.data.CommonCode;
import nes.ltlib.data.LTDevice;
import nes.ltlib.data.TLV_V_WifiConfigRequest;
import nes.ltlib.interf.CameraSearchListener;
import nes.ltlib.interf.CameraWifiListener;
import nes.ltlib.interf.DataChannelListener;
import nes.ltlib.interf.SaxParse;
import nes.ltlib.utils.AppLog;
import nes.ltlib.utils.FileUtils;
import nes.ltlib.utils.GidGlnkDataSourceListener;
import nes.ltlib.utils.HttpUtils;
import nes.ltlib.utils.MySaxHandle;

/**
 * Created by chenqianghua on 2017/8/28.
 */

public class LTSDKManager implements VideoRenderer.OnVideoSizeChangedListener, OnLanSearchListener, Handler.Callback {


    private GlnkClient gclient;

    public static List<LTDevice> getmLTDevices() {
        return mLTDevices;
    }

    public static List<LTDevice> mLTDevices = new ArrayList<>();
    private static LTSDKManager sLTSDKManager;

    private String TAG = this.getClass().getSimpleName();


    private DataChannelListener dataChannelListener;
    private CameraSearchListener searchListener;
    private CameraWifiListener wifilistener;
    private LanSearchIndep lanSearcher;
    private Application application;

    private ArrayList<CameraEntity> soureEntities;
    private ArrayList<CameraEntity> cameraEntities;


    private GlnkChannel mChannel;


    private Handler mHandler;


    /**
     * 0:默认模式 1：搜索模式 2:改名字
     */
    private int ControlMode;


    private final int SEARCH_NORMAL = 0;
    private final int SEARCH_CAMERA = 1;
    private final int SEARCH_RENAME = 2;
    private final int SEARCH_TIME = 3;


    private boolean isDisconnted = false;

    private final int CONTROL_RENAME = 1111;

    private final int SEARCH_FINISH = 1112;

    //第一步：从设备获取设备搜索到的WiFi列表
    private final int TLV_T_WIFISEARCH_REQ = 433;
    private final int TLV_T_WIFISEARCH_RSP = 434;

    //第二步：设置设备WiFi密码
    private final int TLV_T_WIFICONFIG_REQ = 435;
    private final int TLV_T_WIFICONFIG_RSP = 436;


    private final int LT_SEARCH_TIME_OUT = 1113;

    public List<String> gids = new ArrayList<>();

    public static LTSDKManager getInstance() {

        if (null == sLTSDKManager) {
            synchronized (LTSDKManager.class) {
                if (null == sLTSDKManager) {
                    sLTSDKManager = new LTSDKManager();
                }
            }
        }
        return sLTSDKManager;
    }

    /**
     * 初始化浪涛sdk
     *
     * @param application
     */
    public void initLT(Application application) {
        this.application = application;
        gclient = GlnkClient.getInstance();
        gclient.init(application, "langtao", "20141101", "1234567890", 1, 1);    //初始化，这些参数写死即可
        gclient.setStatusAutoUpdate(true); //设备状态自动更新，填写true即可
        gclient.setSdkLan(true);
        gclient.setAppKey("任意值"); //这个	key由浪涛研发分配，唯一，用于验证sdk合法性
        gclient.start();

        mHandler = new Handler(this);

        mDisposables = new CompositeDisposable();

    }

    /**
     * 添加一个摄像头实例
     *
     * @param gid
     */
    public static void addGid(String gid) {
        GlnkClient.getInstance().addGID(gid);
    }

    public static void deleteGid(String gid) {
        GlnkClient.getInstance().removeGid(gid);

    }


    /**
     * 设置设备状态回调
     */
    public void setDeviceStatusChangedListener(OnDeviceStatusChangedListener changedListener) {
        gclient.setOnDeviceStatusChangedListener(changedListener);
    }

    /**
     * 设置搜索摄像头监听
     *
     * @param searchListener
     */
    public void setSearchListener(CameraSearchListener searchListener) {
        this.searchListener = searchListener;
    }

    /**
     * 设置wifi监听
     *
     * @param listener
     */
    public void setWifiListener(CameraWifiListener listener) {
        this.wifilistener = listener;
    }


    /**
     * DataChannel监听
     *
     * @param dataChannelListener
     */
    public void setDataChannelListener(DataChannelListener dataChannelListener) {
        this.dataChannelListener = dataChannelListener;
    }

    /**
     * 创建摄像头·1实例
     *
     * @param gid
     * @param mContext
     * @param sizeChangedListener
     * @param dataSourceListener
     * @return
     */
    private static GlnkPlayer player = null;

    public LTDevice createDevice(String gid, Context mContext) {

        Log.d("createDevice", "create:  gid :" + gid + " mLTDevices.size is " + mLTDevices.size() + " gids.size is " + gids.size());

        AView mAView = new AView(mContext);
//        mAView.setBackgroundColor(mContext.getResources().getColor(R.color.bg_black_ap_30));
        VideoRenderer mVideoRenderer = new AViewRenderer(mContext, mAView);
        mVideoRenderer.setOnVideoSizeChangedListener(this);
        GlnkDataSource source = new GlnkDataSource(GlnkClient.getInstance());
        source.setMetaData(gid, "admin", "admin", 0, 1, 2);// 第一位：通道号  第二位:0高清 1：标清 第三位：0视频流 1：音频流 2：音视频流
        source.setGlnkDataSourceListener(new GidGlnkDataSourceListener(gid));
        source.setTalkVolue(0.5);
        player = new GlnkPlayer();
        player.prepare();
        player.setDataSource(source);
        player.setDisplay(mVideoRenderer);

        LTDevice mLTDevice = getDeviceByGid(gid);
        if (mLTDevice == null) {
            mLTDevice = new LTDevice();
            mLTDevices.add(mLTDevice);
        }
        if (!gids.contains(gid)) {
            gids.add(gid);
        }
        mLTDevice.setGlnkPlayer(player);
        mLTDevice.setAView(mAView);
        mLTDevice.setGid(gid);
        mLTDevice.setVideoRenderer(mVideoRenderer);

        mLTDevice.setGlnkDataSource(source);
        return mLTDevice;
    }

    /**
     * 开始进行播放
     * @param
     */
//    public static void startPlay(){
//        player.start();
//    }

    /**
     * 释放摄像头实例,注意该操作必须放在主线程中去操作
     *
     * @param device
     */
    public void releaseDevice(LTDevice device) {
        Log.i("LTSDK", "释放实例");
        GlnkPlayer mGlnkPlayer = device.getGlnkPlayer();
        if (null != device.getGlnkPlayer()) {
            device.getGlnkPlayer().stop();
            device.getGlnkPlayer().release();
            device.setPlayNull();
            device.setAViewNull();
        }
//        if (mLTDevices.contains(device)) {
//            Log.i("LTSDK", "释放已存在的Device");
//            mLTDevices.remove(device);
//        } else {
//            for (int i = 0; i < mLTDevices.size(); i++) {
//                if (mLTDevices.get(i).getGid().equals(device.getGid())) {
//                    mLTDevices.remove(device);
//                }
//            }
//        }
    }


    public void releaseSDK() {
        GlnkClient.getInstance().release();
    }


    public void disposableAll() {
        if (mDisposables != null)
            mDisposables.clear();
    }

    public void disposable(Disposable d) {
        if (mDisposables != null)
            mDisposables.delete(d);
    }


    /**
     * 搜索摄像头
     */
    public void searchCamera(int mode) {

        ControlMode = mode;
        startSearch();
    }


    public void startSearch() {

        stopSearch();

        soureEntities = new ArrayList<>();
        cameraEntities = new ArrayList<>();
        lanSearcher = new LanSearchIndep(application.getApplicationContext(), this);
        int status = lanSearcher.start();
        AppLog.i("search status:" + status);
        mHandler.sendEmptyMessageDelayed(LT_SEARCH_TIME_OUT, 180000);
    }


    /**
     * 停止搜索
     */
    public void stopSearch() {
        if (lanSearcher == null)
            return;

        // 关闭退出搜索
        lanSearcher.release();
        lanSearcher = null;
    }


    private CompositeDisposable mDisposables;

    public void reName(final CameraEntity entity, final boolean isComplete) {

        //修改名称观察者
        DisposableObserver disObs = new DisposableObserver<CameraEntity>() {


            @Override
            public void onNext(@NonNull CameraEntity s) {

                Log.i("LTSDK", "保存名字");
                if (!TextUtils.isEmpty(s.getDeviceName()))
                    cameraEntities.add(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.i("LTSDK", "error：" + e.getMessage());

                if (isComplete && searchListener != null) {
                    Log.i("LTSDK", "异常完成");
                    searchListener.onSearchFinish(cameraEntities);
                }
            }

            @Override
            public void onComplete() {

                if (isComplete && searchListener != null) {
                    Log.i("LTSDK", "完成");
                    searchListener.onSearchFinish(cameraEntities);
                }
            }
        };


        //增加到取消队列中
        mDisposables.add(disObs);


        Observable.create(new ObservableOnSubscribe<CameraEntity>() {
            @Override
            public void subscribe(final ObservableEmitter e) throws Exception {


                Log.i("LTSDK", "reName ip:" + entity.getIP());

                String url;

                if (entity.getDeviceID().contains("zz")) {
                    url = "http://" + entity.getIP() + "/web/cgi-bin/hi3510/param.cgi?cmd=getserverinfo&-encode&cmd=getnetinfo";

                    Map<String, String> head = new HashMap<String, String>();


                    String auth = Base64.encodeToString("admin:admin".getBytes(), Base64.NO_WRAP);
                    Log.i("LTSDK", "auth:" + auth);

                    head.put("Authorization", "Basic " + auth);


                    String result = HttpUtils.get(url, head);

                    if (TextUtils.isEmpty(result)) {
                        return;
                    }

                    name = SpiltName(result);

                    entity.setDeviceName(name);

                    e.onNext(entity);
                    e.onComplete();

                } else {
                    //获取摄像头信息
                    url = "http://" + entity.getIP() + "/cgi/image_get?Channel=1&Group=OSDInfo";


                    Map<String, String> head = new HashMap<String, String>();


                    String auth = Base64.encodeToString("admin:admin".getBytes(), Base64.NO_WRAP);
                    Log.i("LTSDK", "auth:" + auth);

                    head.put("Authorization", "Basic YWRtaW46");


                    String result = HttpUtils.get(url, head);

                    Log.i("LTSDK", "result:" + result);


                    if (TextUtils.isEmpty(result)) {
                        e.onNext(entity);
                        e.onComplete();
                        return;
                    }

                    //将获取到的XML数据保存到文件
                    File file = null;
                    file = FileUtils.createFile(application.getApplicationContext(), "cgi.xml");

                    if (file.exists()) {
                        file.delete();
                        file = FileUtils.createFile(application.getApplicationContext(), "cgi.xml");
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(result.getBytes());
                    fileOutputStream.flush();
                    fileOutputStream.close();


                    //解析XML文件
                    SAXParserFactory mSAXParserFactory = SAXParserFactory.newInstance();

                    SAXParser mSAXParser = mSAXParserFactory.newSAXParser();
                    mSAXParser.parse(file, new MySaxHandle(new SaxParse() {
                        @Override
                        public void characters(char[] ch, int start, int length) {


                            String name1 = new String(ch);
                            String name = name1.substring(start, length);

                            Log.i("LTSDK", "解析名字  name1：" + name1 + " name:" + name);


                            if (name.equals("HD-IPC")) {
                                //修改名字
                                startReName(entity, "", new DisposableObserver<String>() {
                                    @Override
                                    public void onNext(@NonNull String s) {

                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                            } else {
                                entity.setDeviceName(name);
                            }

                            e.onNext(entity);
                            e.onComplete();

                        }
                    }));
                }

            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.single()).subscribe(disObs);


    }


    private String SpiltName(String txt) {
        String[] str = txt.split(";");

        String nameValue = str[4];
        if (nameValue.contains("ipcname")) {
            String name = nameValue.split("=")[1];
            return name.substring(1, name.length() - 1);
        }

        return "";
    }


    private CameraEntity mEntityReName;
    private String name;
    private DisposableObserver observer;

    public void reCameraName(final CameraEntity entity, final String name, DisposableObserver<String> observer) {

        this.mEntityReName = entity;
        this.name = name;
        this.observer = observer;


        ControlMode = SEARCH_RENAME;
        startSearch();
    }


    public void setFrameRate(final String gid, final int streamType, final int frameRate, DisposableObserver<String> Observer) {

        mDisposables.add(Observer);

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String url = "";
                if (streamType == 0) {
                    url = "http://" + getIPbyGid(gid) + "/cgi/major_stream_set?Channel=1&Group=StreamInfo&FrameRate=" + frameRate;

                } else {
                    url = "http://" + getIPbyGid(gid) + "/cgi/minor_stream_set?Channel=1&Group=StreamInfo&FrameRate=" + frameRate;

                }


                Map<String, String> head = new HashMap<String, String>();
                head.put("Authorization", "Basic YWRtaW46");

                String result = HttpUtils.get(url, head);

                e.onNext(result);
                e.onComplete();


            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.single()).subscribe(Observer);
    }

    public void setTimeZone(final String gid) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {

                String timeUrl = null;

                Map<String, String> head = new HashMap<String, String>();


                if (gid.contains("zz")) {

                    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
                    Date date = new Date(System.currentTimeMillis());
                    String time = format.format(date);

                    String timeZone = getTimeZone();

                    AppLog.i("时间戳:" + System.currentTimeMillis());


                    String auth = Base64.encodeToString("admin:admin".getBytes(), Base64.NO_WRAP);
                    head.put("Authorization", "Basic " + auth);

                    timeUrl = "http://" + getIPbyGid(gid) + "/cgi-bin/hi3510/setservertime.cgi?cururl=document.URL&-time=" + time +
                            "&-timezone=" + timeZone + "&-dstmode=on";

                } else {

                    //设置摄像头时间
                    timeUrl = "http://" + getIPbyGid(gid) + "/cgi/sys_set?Group=TimeInfo&TimeMethod=0&CameraTime=" + (System.currentTimeMillis() / 1000)
                            + "&TimeZone=" + TimeZone.getDefault().getRawOffset() / 60000;
                    head.put("Authorization", "Basic YWRtaW46");
                }


                String timeResult = HttpUtils.get(timeUrl, head);


                e.onNext(timeResult);
                e.onComplete();

            }
        }).subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread()).subscribe(new DefaultObserver<String>() {
            @Override
            public void onNext(@NonNull String s) {

                if (TextUtils.isEmpty(s)) {
                    Log.i("LTSDK", "设置时间失败");
                } else {
                    Log.i("LTSDK", "设置时间成功");
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.i("LTSDK", "设置时间 error：" + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i("LTSDK", "设置时间 完成");
            }
        });
    }


    private void startReName(final CameraEntity entity, final String name, DisposableObserver<String> observer) {

        mDisposables.add(observer);

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {

                String reName = name;
                Log.i("LTSDK", "重命名 name :" + name);
                if (TextUtils.isEmpty(reName)) {
                    reName = "WIFICAMERA";
                }

                String url = null;
                Map<String, String> head = new HashMap<String, String>();

                //兼容新摇头机 gid是zz开头
                if (entity.getDeviceID().contains("zz")) {
                    url = "http://" + entity.getIP() + "/web/cgi-bin/hi3510/param.cgi?cmd=setosdattr&-region=1&-ipcname=" + reName + "&-encode=&cmd=setservername&-ipcname=" + reName;

                    //账号密码现在是固定 以后会需要给出入口统一修改
                    String auth = Base64.encodeToString("admin:admin".getBytes(), Base64.NO_WRAP);

                    head.put("Authorization", "Basic " + auth);
                } else {
                    url = "http://" + entity.getIP() + "/cgi/image_set?Channel=1&Group=OSDInfo&TextOSDStatus=1&TextOSDTitle=" + reName + "&TextOSDX=2&TextOSDY=2";

                    head.put("Authorization", "Basic YWRtaW46");

                }

                String result = HttpUtils.get(url, head);


                if (!"".equals(result)) {
                    entity.setDeviceName(reName);
                }

                e.onNext(result);
                e.onComplete();

            }
        }).subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);

    }


    private String mGid;
    private String mSsid;
    private String mPwd;

    public void startConnect(String gid, String ssid, String pwd) {

        Log.i("LTSDK", "startConnect");

        if (TextUtils.isEmpty(gid) || TextUtils.isEmpty(ssid) || TextUtils.isEmpty(pwd)) {
            return;
        }
        this.mGid = gid;
        this.mSsid = ssid;
        this.mPwd = pwd;

        if (mChannel != null) {
            stopConnect();
        }

        isDisconnted = false;
        mChannel = new GlnkChannel(new MyGlnkDataSource());
        mChannel.setMetaData(gid, " ", " ", 0, 3, 0);
        mChannel.setAuthMode(0);
        mChannel.start();
    }

    // 关闭退出,只能主线程执行，不能开其他 线程执行
    private void stopConnect() {
        mHandler.removeMessages(CommonCode.VIDEO_KEEP_LIVE);
        if (mChannel != null) {
            mChannel.stop();
            mChannel.release();
            mChannel = null;
        }
    }

    /**
     * 通过gid获取设备IP
     *
     * @param gid
     * @return
     */
    private String getIPbyGid(String gid) {
        String ip = "";

        for (CameraEntity entity : soureEntities) {
            if (gid.equals(entity.getDeviceID())) {
                ip = entity.getIP();
                break;
            }
        }


        return ip;
    }


    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case CONTROL_RENAME:

                if (soureEntities != null && !soureEntities.isEmpty()) {
                    for (CameraEntity cameraEntity : soureEntities) {
                        if (mEntityReName.getDeviceID().equals(cameraEntity.getDeviceID())) {
                            mEntityReName.setIP(cameraEntity.getIP());
                            break;
                        }
                    }

                }

                startReName(mEntityReName, name, observer);

                break;

            case SEARCH_FINISH:
                //普通搜索
                if (ControlMode == SEARCH_CAMERA) {

                    if (soureEntities.isEmpty()) {
                        if (searchListener != null) {
                            searchListener.onSearchFinish(cameraEntities);
                        }
                    } else {

                        for (int i = 0; i < soureEntities.size(); i++) {
                            reName(soureEntities.get(i), i == soureEntities.size() - 1 ? true : false);
                        }
                    }

                    //改名字
                } else if (ControlMode == SEARCH_RENAME) {
                    mHandler.sendEmptyMessage(CONTROL_RENAME);

                } else if (ControlMode == SEARCH_TIME) {
                    if (searchListener != null) {
                        searchListener.onSearchFinish(soureEntities);
                    }
                }


                ControlMode = SEARCH_NORMAL;

                stopSearch();
                break;


            case CommonCode.VIDEO_KEEP_LIVE:        //20秒发一次心跳包保持长连接
            {
//                    if (mChannel != null) {
//                        mChannel.keepliveReq();
//                        this.sendEmptyMessageDelayed(CommonCode.VIDEO_KEEP_LIVE, 1000 * 20);
//                    }
            }
            break;
            case CommonCode.VIDEO_ONCONNECTED:
                byte[] b = null;
                try {
                    b = getByte(mGid, mSsid, mPwd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (b != null) {
                    int ret = mChannel.sendData(TLV_T_WIFICONFIG_REQ, b);
                    if (ret == 0) {
                        Log.i("LTSDK", "发送成功");
                    } else {
                        Log.i("LTSDK", "发送失败");

                        if (wifilistener != null) {
                            wifilistener.setWifiFailure();
                        }
                    }
                }
//                    this.sendEmptyMessageDelayed(CommonCode.VIDEO_KEEP_LIVE, 1000 * 20); //20秒发一次心跳包保持长连接
                break;

            case CommonCode.VIDEO_IOCTRL:            //设备返回信息
                Bundle bundle = msg.getData();
                byte[] data = bundle.getByteArray("data");
                int type = bundle.getInt("type");
                if (type == TLV_T_WIFICONFIG_RSP)    //配置WiFi热点成功返回
                {
                    Log.i("LTSDK", "配置wifi成功");
                    if (wifilistener != null) {
                        wifilistener.setWifiSuccess();
                    }
                }
                break;

            case LT_SEARCH_TIME_OUT:
                if (searchListener != null) {
                    searchListener.onSearchFinish(cameraEntities);
                }
                break;


        }

        return false;
    }


    class MyGlnkDataSource extends DataSourceListener2 {
        @Override
        public void onDisconnected(int errcode) {
            super.onDisconnected(errcode);
            //回调函数不能做阻塞动作，所有数据都要通过Handler等方式抛到主线程执行。
            isDisconnted = true;
        }

        @Override
        public void onConnected(int mode, String ip, int port) {
            super.onConnected(mode, ip, port);
            //回调函数不能做阻塞动作，所有数据都要通过Handler等方式抛到主线程执行。

            mHandler.sendEmptyMessage(CommonCode.VIDEO_ONCONNECTED);
        }

        @Override
        public void onAuthorized(int result) {
            //回调函数不能做阻塞动作，所有数据都要通过Handler等方式抛到主线程执行。

        }

        @Override
        public void onIOCtrl(int type, byte[] data) {
            //回调函数不能做阻塞动作，所有数据都要通过Handler等方式抛到主线程执行。

            Log.i("LTSDK", "onIOCtrl");

            Bundle bundle = new Bundle();
            bundle.putByteArray("data", data);
            bundle.putInt("type", type);
            Message msg = new Message();
            msg.setData(bundle);
            msg.what = CommonCode.VIDEO_IOCTRL;
            mHandler.sendMessage(msg);
        }

        // 下面两个回调，不使用，请忽略
        @Override
        public void onPermision(int i) {

        }

        public void onRecvDevRecVersion(int i, int i1) {

        }
    }

    /**
     * ap配置所需要的数据格式
     *
     * @param name_   此变量为gid
     * @param ssid_
     * @param passwd_
     * @return
     * @throws IOException
     */
    public byte[] getByte(String name_, String ssid_, String passwd_)
            throws IOException {
        TLV_V_WifiConfigRequest req = new TLV_V_WifiConfigRequest();
        byte[] temp = name_.getBytes();
        int len = temp.length >= 31 ? 31 : temp.length;
        for (int i = 0; i < len; i++) {
            req.name[i] = temp[i];
        }
        req.name[len] = '\0';

        temp = ssid_.getBytes(); // len = temp.length >= 31 ? 31 : temp.length;
        len = temp.length >= 127 ? 127 : temp.length; // change 31 -> 127 //
        for (int i = 0; i < len; i++) {
            req.ssid[i] = temp[i];
        }
        req.ssid[len] = '\0';
        temp = passwd_.getBytes();
        len = temp.length >= 31 ? 31 : temp.length;
        for (int i = 0; i < len; i++) {
            req.password[i] = temp[i];
        }
        req.password[len] = '\0';
        return req.serialize();
    }


    @Override
    public void onVideoSizeChanged(VideoRenderer videoRenderer, int i, int i1) {

//        Log.e("onVideoSizeChanged","i is "+i+" i1 is "+i1);
//        //副码流
//        if (i == 640){
//            AViewRenderer mAViewRenderer = (AViewRenderer) videoRenderer;
//            Matrix mMatrix = mAViewRenderer.getMatrix();
//            mMatrix.reset();
//            mMatrix.setScale((float)3,(float)3,0,0);
//        }
//        else if (i== 1280){
//            AViewRenderer mAViewRenderer = (AViewRenderer) videoRenderer;
//            Matrix mMatrix = mAViewRenderer.getMatrix();
//            mMatrix.reset();
//            mMatrix.setScale((float)(1.5),(float)(1.7777),0,0);
//        }
//        else if (i == 1920){
//            //盒子分辨率为1920*1080,如果是全屏播放则不需要进行处理
//            //四分屏时做处理
//        }

    }


    @Override
    public void onSearched(String s, String s1) {

    }

    @Override
    public void onSearched2(String s, String s1) {
        AppLog.e("搜索onSearched2   s:" + s + "  s1:" + s1);

        if (mHandler.hasMessages(LT_SEARCH_TIME_OUT)) {
            mHandler.removeMessages(LT_SEARCH_TIME_OUT);
        }

        CameraEntity entity = new CameraEntity();
        entity.setIP(s1.split(":")[0]);
        entity.setDeviceID(s);

        soureEntities.add(entity);

    }

    @Override
    public void onSearchFinish() {

        mHandler.sendEmptyMessage(SEARCH_FINISH);

    }


    /******************IPCAMERA********************/
    /**
     * 添加一个摄像头到sdk中用作状态回调
     */
    public void addGid() {
        if (null != gids && gids.size() > 0) {
//            LogUtils.e("addGid","add gid is "+gids.get(0));
            for (int i = 0; i < gids.size(); i++) {
                GlnkClient.getInstance().addGID(gids.get(i));
            }
        }
    }

    /**
     * 从secsetting扫描到一个新摄像头并添加
     */
    public void addOneDevice(String gid) {


        LTDevice mLTDevice = getDeviceByGid(gid);
        if (mLTDevice == null) {
            mLTDevice = new LTDevice();
            mLTDevice.setGid(gid);
            mLTDevices.add(mLTDevice);
        }
        //添加到sdk中，在onchanged回调中创建摄像头实例
        // TODO: 2017/10/11 防止gid重复增加
        int position = gids.indexOf(gid);
        if (position == -1) {
            gids.add(gid);
            GlnkClient.getInstance().addGID(gid);

        }
    }


    public void deleteOneDevice(String gid) {
        int position = 0;
        boolean deleteSure = false;
        for (int i = 0; i < mLTDevices.size(); i++) {
            if (mLTDevices.get(i).getGid().equals(gid)) {
                position = i;
                deleteSure = true;
            }
        }
        if (deleteSure) {
            mLTDevices.remove(position);
        }
    }

    /**
     * 创建摄像头·1实例
     *
     * @param mContext
     * @return
     */
    public void createDevice(Context mContext) {
        for (int i = 0; i < gids.size(); i++) {
            //建两个空对象，等onchange返回1的时候再去创建对象。
//            AView mAView = new AView(mContext);
//            VideoRenderer mVideoRenderer = new AViewRenderer(mContext, mAView);
//            mVideoRenderer.setOnVideoSizeChangedListener(new DataSourceListener(gids.get(i)));
//            GlnkDataSource source = new GlnkDataSource(GlnkClient.getInstance());
//            source.setMetaData(gids.get(i), "admin", "admin", 0, 0, 2);//通道号、主副码流、音视频流:0,0,2:第二个0代表主码流，设置为1时代表副码流
//            source.setGlnkDataSourceListener(new DataSourceListener(gids.get(i)));
//            source.setTalkVolue(0.5);
//            GlnkPlayer player = new GlnkPlayer();
//            player.prepare();
//            player.setDataSource(source);
//            player.setDisplay(mVideoRenderer);
//            mLTDevice.setVideoRenderer(mVideoRenderer);
//            mLTDevice.setGid(gids.get(i));
//            mLTDevice.setGlnkDataSource(source);

            LTDevice mLTDevice = getDeviceByGid(gids.get(i));
            if (mLTDevice == null) {
                mLTDevice = new LTDevice();
                mLTDevices.add(mLTDevice);
            }
            mLTDevice.setGid(gids.get(i));

        }
//        LogUtils.e("createDevice","mLTDevice.size is "+mLTDevices.size());
    }

    /**
     * 掉线之后重新创建实例
     *
     * @param mContext
     */
    public void createDevice_one(Context mContext, String gid) {
        //如果已经添加过该gid的实例则直接return
        int position = gids.indexOf(gid);
//        LogUtils.e("onCreateDevice_one","did is "+gid+" time is "+time+"glnkPlayer is "+mLTDevices.get(position).getGlnkPlayer());
        if (mLTDevices.size() > position && null != mLTDevices.get(position).getGlnkPlayer()) {
//            LogUtils.e("createDevice_one","gid is "+gid);
            return;
        }


        createDevice(gid, mContext);

    }


    public LTDevice getDeviceByGid(String gid) {
        LTDevice ltDevice = null;

        if (mLTDevices != null && !mLTDevices.isEmpty()) {
            for (LTDevice device : mLTDevices) {
                if (gid.equals(device.getGid())) {
                    return device;
                }
            }
        }

        return ltDevice;
    }


    /**
     * 收到在线状态之后打开数据流
     *
     * @param gid
     */
    public void startPlay(String gid) {
        int position = gids.indexOf(gid);
//        LogUtils.e("startPlay","gid is "+gid);
        if (mLTDevices.size() > position && position >= 0) {
            mLTDevices.get(position).getGlnkPlayer().start();
        }
    }


    /**
     * 通过gid获取position
     *
     * @param gid
     */
    public int getPositionByGid(String gid) {
        int position = 100;
        for (int i = 0; i < gids.size(); i++) {
            if (gids.get(i).equals(gid)) {
                position = i;
            }
        }
        return position;
    }

    /**
     * 开始进行播放
     * @param
     */
//    public static void startPlay(){
//        player.start();
//    }

    /**
     * 释放摄像头实例,注意该操作必须放在主线程中去操作
     */
    public void releaseDevice(String gid) {
//        int position = gids.indexOf(gid);
//        if (position >= 0 && mLTDevices.size() > position) {
//            LTDevice device = mLTDevices.get(position);
//            if (null != device.getGlnkPlayer()) {
//                device.getGlnkPlayer().stop();
//                device.getGlnkPlayer().release();
//                device.setPlayNull();
//                device.setAViewNull();
//            }
//        }
    }


    /**
     * 开始录制操作
     *
     * @param gid
     * @param filePath
     */
    public void startRecord(String gid, String filePath) {
//        LogUtils.e("LTstartRecord","gid is "+gid+" filePath is "+filePath);
        int position = 100;
        for (int i = 0; i < mLTDevices.size(); i++) {
            if (null != mLTDevices.get(i).getGlnkPlayer()) {
                if (mLTDevices.get(i).getGid().equals(gid)) {
                    position = i;
                }
            }
        }
        if (position >= 0 && position < gids.size()) {
//            LogUtils.e("LTstartRecord","filePath is "+filePath);
            GlnkDataSource mSource = mLTDevices.get(position).getGlnkDataSource();
            int start = mSource.startRecordVideo(GlnkDataSource.REC_MP4, filePath);
//            LogUtils.e("LTstartRecord","start is "+start);
        }
    }

    /**
     * 停止录制
     *
     * @param gid
     */
    public void stopRecord(String gid) {
//        LogUtils.e("LTStopRecord","gid is "+gid);
        int position = 0;
        for (int i = 0; i < mLTDevices.size(); i++) {
            if (mLTDevices.get(i).getGid().equals(gid)) {
                position = i;
                break;
            }
        }
        GlnkDataSource mSource = mLTDevices.get(position).getGlnkDataSource();
//        LogUtils.e("LTStopRecord","gid is "+gid);
        mSource.stopRecordVideo();
    }

    /**
     *
     */
    public void setMatrix_launcher() {
        for (int i = 0; i < mLTDevices.size(); i++) {
            AViewRenderer mAViewRenderer = (AViewRenderer) mLTDevices.get(i).getVideoRenderer();
            Matrix mMatrix = mAViewRenderer.getMatrix();
            mMatrix.reset();
//            mMatrix.postScale((float) 1920/640,(float) 1080/480);
        }
    }


    public static final int scaleType_1 = 1;
    public static final int scaleType_2 = 2;
//
//    public void setVideoScale(float denisty, int type) {
//        switch (type) {
//            case scaleType_1:
//                for (int i = 0; i < mLTDevices.size(); i++) {
//                    AViewRenderer mAViewRenderer = (AViewRenderer) mLTDevices.get(i).getVideoRenderer();
//                    if (null != mAViewRenderer) {
//                        Matrix mMatrix = mAViewRenderer.getMatrix();
//                        mMatrix.reset();
//                        if (denisty == 1.5) {
//                            mMatrix.setScale((float) 3, (float) 3, 0, 0);
//                        } else {
//                            mMatrix.setScale((float) 2.95, (float) 2.95, 0, 0);
//                        }
//                    }
//                }
//                break;
//            case scaleType_2:
//                for (int i = 0; i < mLTDevices.size(); i++) {
//                    AViewRenderer mAViewRenderer = (AViewRenderer) mLTDevices.get(i).getVideoRenderer();
//                    if (null != mAViewRenderer) {
//                        Matrix mMatrix = mAViewRenderer.getMatrix();
//                        mMatrix.reset();
//                        mMatrix.setScale((float) 1.5, (float) 1.5, 0, 0);
//                    }
//                }
//                break;
//        }
//    }


//    public void setVideoScale(float denisty, int type) {
//        switch (type) {
//            case scaleType_1:
//                for (int i = 0; i < mLTDevices.size(); i++) {
//                    AViewRenderer mAViewRenderer = (AViewRenderer) mLTDevices.get(i).getVideoRenderer();
//                    if (null != mAViewRenderer) {
//                        Matrix mMatrix = mAViewRenderer.getMatrix();
//                        mMatrix.reset();
//                        if (denisty == 1.5) {
//                            mMatrix.setScale((float) 3, (float) 3, 0, 0);
//                        } else {
//                            mMatrix.setScale((float) 2.95, (float) 2.95, 0, 0);
//                        }
//                    }
//                }
//                break;
//            case scaleType_2:
//                for (int i = 0; i < mLTDevices.size(); i++) {
//                    AViewRenderer mAViewRenderer = (AViewRenderer) mLTDevices.get(i).getVideoRenderer();
//                    if (null != mAViewRenderer) {
//                        Matrix mMatrix = mAViewRenderer.getMatrix();
//                        mMatrix.reset();
//                        mMatrix.setScale((float) 1.5, (float) 1.5, 0, 0);
//                    }
//                }
//                break;
//        }
//    }



    public void setVideoScale(float denisty, int type) {
        switch (type) {
            case scaleType_1:
                for (int i = 0; i < mLTDevices.size(); i++) {
                    AViewRenderer mAViewRenderer = (AViewRenderer) mLTDevices.get(i).getVideoRenderer();
                    if (null != mAViewRenderer) {
                        Matrix mMatrix = mAViewRenderer.getMatrix();
                        if (mLTDevices.get(i).getGid().startsWith("zz")) {
                            mMatrix.setScale( 1920f / 640f,1080f / 360f);
                        } else {
                            mMatrix.setScale( 1920f / 640f,1080f / 480f);
                        }
                    }
                }
                break;
            case scaleType_2:
                for (int i = 0; i < mLTDevices.size(); i++) {
                    AViewRenderer mAViewRenderer = (AViewRenderer) mLTDevices.get(i).getVideoRenderer();
                    if (null != mAViewRenderer) {
                        Matrix mMatrix = mAViewRenderer.getMatrix();
                        if (mLTDevices.get(i).getGid().startsWith("zz")) {
                            mMatrix.setScale((960.0f / 640.0f),(float) (540.0 / 360.0));
                        } else {
                            mMatrix.setScale( 960f / 640f,540f / 480f);
                        }
                    }
                }
                break;
        }
    }



    /******************IPCAMERA********************/

    /**
     * 新摇头机
     */
    public void setWifi(final String ip, final String wifiname, final String pwd) {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {

                String url = "http://" + ip + "/cgi-bin/setwifiattr.cgi?cmd=setwifiattr.cgi&-enable=1&-ssid=" + wifiname + "&-wktype=3&-key=" + pwd;

                Map<String, String> map = new HashMap<String, String>();
                String auth = Base64.encodeToString("admin:admin".getBytes(), Base64.NO_WRAP);
                map.put("Authorization", "Basic " + auth);

                String result = HttpUtils.get(url, map);

                e.onNext(result);
                e.onComplete();

            }
        }).subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableObserver<String>() {
            @Override
            public void onNext(@NonNull String o) {

                if (!TextUtils.isEmpty(o)) {
                    if (wifilistener != null) {
                        wifilistener.setWifiSuccess();
                    }
                } else {
                    if (wifilistener != null) {
                        wifilistener.setWifiFailure();
                    }
                }

            }

            @Override
            public void onError(@NonNull Throwable e) {
                if (wifilistener != null) {
                    wifilistener.setWifiFailure();
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }


    private String getTimeZone() {
        int time = TimeZone.getDefault().getRawOffset();

        int offsetMinutes = time / 60000;
        char sign = '+';
        if (offsetMinutes < 0) {
            sign = '-';
            offsetMinutes = -offsetMinutes;
        }

        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("STD:").append(sign).append(":");

        appendNumber(stringBuffer, 2, offsetMinutes / 60);
        stringBuffer.append(":");
        appendNumber(stringBuffer, 2, offsetMinutes % 60);

        return stringBuffer.toString();

    }

    private void appendNumber(StringBuilder builder, int count, int value) {
        String string = Integer.toString(value);
        for (int i = 0; i < count - string.length(); i++) {
            builder.append('0');
        }
        builder.append(string);
    }
}




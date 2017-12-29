package com.sen5.smartlifebox.data.p2p;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.SecQreSettingApplication;
import com.sen5.smartlifebox.common.utils.DID;
import com.sen5.smartlifebox.common.utils.NetUtils;
import com.sen5.smartlifebox.common.utils.Utils;

import nes.ltlib.utils.AppLog;

import com.sen5.smartlifebox.common.wrapper.FastJsonTools;
import com.sen5.smartlifebox.data.DeviceJudge;
import com.sen5.smartlifebox.data.IniFileOperate;
import com.sen5.smartlifebox.data.JsonCreator;
import com.sen5.smartlifebox.data.entity.Constant;
import com.sen5.smartlifebox.data.entity.ContactEntity;
import com.sen5.smartlifebox.data.entity.DeviceData;
import com.sen5.smartlifebox.data.entity.DeviceStatusData;
import com.sen5.smartlifebox.data.entity.MemberEntity;
import com.sen5.smartlifebox.data.entity.ModeData;
import com.sen5.smartlifebox.data.entity.RoomData;
import com.sen5.smartlifebox.data.entity.SceneData;
import com.sen5.smartlifebox.data.event.DeviceEvent;
import com.sen5.smartlifebox.data.event.MembersEvent;
import com.sen5.smartlifebox.data.event.RoomEvent;
import com.sen5.smartlifebox.data.event.SceneEvent;
import com.sen5.smartlifebox.socket.OnSocketClientListener;
import com.sen5.smartlifebox.socket.SocketClient;
import com.sen5.smartlifebox.ui.other.EmergencyContactFragment;
import com.sen5.smartlifebox.ui.other.NotificationSystemChooseFragment;
import com.sen5.smartlifebox.widget.RightSideToast;
import com.sen5.smartlifebox.widget.ScanView;
import com.sen5.smartlifebox.widget.WaterWaveView;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nes.ltlib.data.CameraEntity;

import static com.sen5.smartlifebox.common.utils.Utils.getCameraList;
import static com.sen5.smartlifebox.common.wrapper.FastJsonTools.getList;


public class P2PModel {

    private static volatile P2PModel mP2PModel;

    private Context mContext;
    //    private P2PClient mP2PClient;
    private SocketClient mSocketClient;
    private Handler mHandler;
    private String mEncryptKey = "";

    private EventBus mEventBus = EventBus.getDefault();
    private Dialog dialog;
    private ScanView scanView;
    private WaterWaveView waterWaveView;

    ExecutorService mThreadPool;

    private P2PModel(Context context) {
        mContext = context;
        mHandler = new Handler();
        mThreadPool = Executors.newCachedThreadPool();
//        mP2PClient = new P2PClient();
//        mP2PClient.setOnP2PClientListener(mOnP2PClientListener);
        createSocket();

    }

    public static P2PModel getInstance(Context context) {
        if (mP2PModel == null) {
            synchronized (P2PModel.class) {
                if (mP2PModel == null) {
                    mP2PModel = new P2PModel(context);
                    return mP2PModel;
                }
            }
        }
        return mP2PModel;
    }

    public void createSocket() {
        mSocketClient = new SocketClient();
        mSocketClient.setOnSocketClientListener(mOnSocketClientListener);
    }


    /**
     * 获取加密的key
     *
     * @return
     */
    public String getEncryptKey() {
        return mEncryptKey;
    }

    /**
     * 设置加密的key
     *
     * @param encryptKey
     * @return 解密后的DID
     */
    private String setEncryptKey(String encryptKey) {
        //SecqLogUtil.e("encryptKey = " + encryptKey);
        mEncryptKey = encryptKey;
        String decodeDID = DID.getDID(mEncryptKey);
        return decodeDID;
    }

    /**
     * 连接到设备
     *
     * @return
     */
    public void connectDevice(String encryptKey) {
        //将当前连接的Home持久化到sp中，用于记录当前连接的Home
        //SecqLogUtil.e("解密前did：" + encryptKey);
        String did = setEncryptKey(encryptKey);
        //SecqLogUtil.e("解密后did：" + did);
//        mP2PClient.setKey(did);
//        if (!mP2PClient.isInitP2P()) {
//            mP2PClient.initP2P();
//        }
//
//        mP2PClient.connectDevice();
        mSocketClient.connectSocket();
        //SecqLogUtil.e("连接Home");
    }

    /**
     * 断开当前连接，但不注销初始化
     */
    public void disConnectDevice() {
        mEncryptKey = "";
//        mP2PClient.disconnectDevice();
        mSocketClient.disConnect();
        //SecqLogUtil.e("断开Home连接");
    }

    /**
     * 应用退出时调用
     */
    public void destroyP2P() {
//        mP2PClient.delInitP2P();
        mSocketClient.disConnect();
        //SecqLogUtil.e("销毁P2P连接");
    }

    /**
     * 发送数据
     *
     * @param data json格式的数据
     * @return < 0:对应错误状态 ；>=0:发送字节数
     */
    public int sendData(final String data) {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                new CustomToast(mContext, "发送:" + data);
//            }
//        });
        AppLog.e("发送:" + data);


        if (mThreadPool != null)
            mThreadPool.execute(new secureRunnable(data));

//        return mP2PClient.sendData(data);
        return 0;
    }

    /**
     * P2P是否已经连接
     *
     * @return
     */
    public boolean isP2PConnect() {
//        return mP2PClient.checkConnectStatus() >= 0;
        return false;
    }

    private OnSocketClientListener mOnSocketClientListener = new OnSocketClientListener() {
        @Override
        public void onSocketConnFail() {
            //SecqLogUtil.e("连接Home失败");
            //每隔3s重连一次，直到连接成功
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
//                    if (!mEncryptKey.equals("")) {
                    //SecqLogUtil.e("P2P连接失败后重连");
                    //这里是P2P的一个bug，如果第一次连接失败，那么重连的时候会得不到IP，得主动调用一下PPCS_NetworkDetect()
                    //去检测一下网络才能获取到IP，然后才能连接成功
//                        st_PPCS_NetInfo st_ppcs_netInfo = mP2PClient.checkNetStatus();
//                        //SecqLogUtil.e("WAN IP = " + st_ppcs_netInfo.getMyWanIP());
//                        //SecqLogUtil.e("LAN IP = " + st_ppcs_netInfo.getMyLanIP());
//                        mP2PClient.connectDevice();
                    mSocketClient.connectSocket();
//                    }
                }
            }, 5000);

        }

        @Override
        public void onSocketConnSuccess() {
            //SecqLogUtil.i("连接Home成功");
            //发送身份验证
            String did = Utils.getQRInfo(mContext);
            String data = JsonCreator.createIdentityJson(did);
//            String data = JsonCreator.createIdentityJson("SLIFE000625PSVBLBZRGCP");
            sendData(data);
        }

        @Override
        public void onSocketRecvData(final String str) {//执行在子线程
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    new CustomToast(mContext, "接收：" + str);
//                }
//            });
            //SecqLogUtil.e("接收：" + str);
            handleData(str);
        }

        @Override
        public void onPlay(int whichCamera, int encrypt, byte[] pAVData,
                           int nFrameSize, int frameCount) {

        }

        @Override
        public void onSocketDisconnect() {
            //每隔3s重连一次，直到连接成功。
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!mEncryptKey.equals("")) {
                        //SecqLogUtil.e("-------------SocketClient disConnect to connect");
                        //SecqLogUtil.e("P2P连接异常断开后重连");
//                        mP2PClient.connectDevice();
                        mSocketClient.connectSocket();
                    }
                }
            }, 5000);
        }
    };
//    private OnP2PClientListener mOnP2PClientListener = new OnP2PClientListener() {
//        @Override
//        public void onP2PConnFail() {
//            //SecqLogUtil.e("连接Home失败");
//            //每隔3s重连一次，直到连接成功
//            final Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    if (!mEncryptKey.equals("")) {
//                        //SecqLogUtil.e("P2P连接失败后重连");
//                        //这里是P2P的一个bug，如果第一次连接失败，那么重连的时候会得不到IP，得主动调用一下PPCS_NetworkDetect()
//                        //去检测一下网络才能获取到IP，然后才能连接成功
//                        st_PPCS_NetInfo st_ppcs_netInfo = mP2PClient.checkNetStatus();
//                        //SecqLogUtil.e("WAN IP = " + st_ppcs_netInfo.getMyWanIP());
//                        //SecqLogUtil.e("LAN IP = " + st_ppcs_netInfo.getMyLanIP());
//
//                        mP2PClient.connectDevice();
//                    }
//                }
//            }, 5000);
//
//        }
//
//        @Override
//        public void onP2PConnSuccess() {
//            //SecqLogUtil.i("连接Home成功");
//            //发送身份验证
//            String did = Utils.getQRInfo(mContext);
//            String data = JsonCreator.createIdentityJson(did);
////            String data = JsonCreator.createIdentityJson("SLIFE000625PSVBLBZRGCP");
//            sendData(data);
//        }
//
//        @Override
//        public void onP2PRecvData(final String str) {//执行在子线程
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    TestToast.showShort(mContext, "接收：" + str);
//                }
//            });
//            //SecqLogUtil.e("接收：" + str);
//            handleData(str);
//        }
//
//        @Override
//        public void onPlay(int whichCamera, int encrypt, byte[] pAVData,
//                           int nFrameSize, int frameCount) {
//
//        }
//
//        @Override
//        public void onP2PDisconnect() {
//            //每隔3s重连一次，直到连接成功。
//            final Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    if (!mEncryptKey.equals("")) {
//                        //SecqLogUtil.e("P2P连接异常断开后重连");
//                        mP2PClient.connectDevice();
//                    }
//                }
//            }, 5000);
//        }
//    };

    /**
     * 处理服务端推送的数据
     *
     * @param jsonStr 服务端返回的json数据
     */
    private void handleData(String jsonStr) {
        //SecqLogUtil.e(jsonStr);
        JSONObject jsonObject = null;

        try {
            jsonObject = JSONObject.parseObject(jsonStr);

        } catch (Exception e) {
            e.printStackTrace();

        }

        if (jsonObject == null) {
            return;
        }
        int msg_type = jsonObject.getIntValue(Constant.MSG_TYPE);
        Log.d("", "------------------接收 msg_type = " + msg_type + " jsonObject = " + jsonObject);
        switch (msg_type) {
            /*******************身份验证*****************************/
            case Constant.MSG_IDENTITY_LEGAL_RESPOND: {//身份合法
//                mEventBus.post(new MembersEvent(MembersEvent.IDENTITY_LEGAL));

                //获取房间列表
                String roomlistData = JsonCreator.createRoomsJson();
                sendData(roomlistData);

                //获取设备列表
                String deviceListData = JsonCreator.createListDeviceJson();
                sendData(deviceListData);


                //获取用户列表
                String memberData = JsonCreator.createListUserJson();
                sendData(memberData);
                //获取模式列表
                String modeListJson = JsonCreator.createListModeJson();
                sendData(modeListJson);

                //获取场景列表
                String sceneListJson = JsonCreator.createListSceneJson();
                sendData(sceneListJson);


            }
            break;
            /*********************Scenes*****************************/
            case Constant.MSG_LIST_SCENE_RESPOND://场景列表
                String sceneJsonStr = jsonObject.getString("scenes");
                mScenes.clear();


                mScenes.addAll(getList(sceneJsonStr, SceneData.class));


                Iterator list = mScenes.iterator();
                while (list.hasNext()) {
                    switch (((SceneData) list.next()).getScene_id()) {
                        case 1001:
                        case 1002:
                        case 1003:
                            list.remove();
                            break;
                    }
                }


                mEventBus.post(new SceneEvent(SceneEvent.LIST_SCENE));
                break;
            case Constant.MSG_NEW_SCENE_RESPOND: //新建场景
                SceneData newSceneData = FastJsonTools.getItem(jsonObject.getString("scene_info"),
                        SceneData.class);
                addScene(newSceneData);


                mEventBus.post(new SceneEvent(SceneEvent.NEW_SCENE));

//                mEventBus.post(new SceneEvent(SceneEvent.NEW_SCENE));
                break;

            case Constant.MSG_DELETE_SCENE_RESPOND: //删除场景
                int sceneId = jsonObject.getIntValue("scene_id");
                deleteScene(sceneId);

                mEventBus.post(new SceneEvent(SceneEvent.DELETE_SCENE));
                break;
            case Constant.MSG_EDIT_SCENE_RESPOND: //编辑场景

                SceneData editSceneData = FastJsonTools.getItem(jsonObject.getString("scene_info"),
                        SceneData.class);
                replaceScene(editSceneData);

                mEventBus.post(new SceneEvent(SceneEvent.EDIT_SCENE));
                break;

            case Constant.MSG_APPLY_SCENE_RESPOND:


                int Id = jsonObject.getIntValue("scene_id");

                //SecqLogUtil.e("------------------apply_scene--------------" + Id);


                SceneEvent sceneEvent = new SceneEvent(SceneEvent.APPLY_SCENE);
                sceneEvent.setSceneId(Id);
                mEventBus.post(sceneEvent);
                break;

            /*********************Members***************************/
            case Constant.MSG_LIST_USER_RESPOND: {//获取用户列表回复
                mMembers.clear();
                String str = jsonObject.getString("users");
                if (!TextUtils.isEmpty(str)) {
                    mMembers.addAll(getList(str, MemberEntity.class));
                }
                getmCurMember();
                mEventBus.post(new MembersEvent(MembersEvent.LIST_USER));
            }
            break;

            case Constant.MSG_ADD_USER_RESPOND: {//添加用户成功回复
                MemberEntity member = FastJsonTools.getItem(jsonStr, MemberEntity.class);
                mMembers.add(member);
                mCurMember++;
                setmCurMember(mCurMember);
                mEventBus.post(new MembersEvent(MembersEvent.ADD_USER));
            }
            break;

            case Constant.MSG_DELETE_USER_RESPOND: {//删除用户成功回复
                String id = jsonObject.getString("identity_id");
                Iterator iterator = mMembers.iterator();
                while (iterator.hasNext()) {
                    MemberEntity member = (MemberEntity) iterator.next();
                    if (member.getIdentity_id().equals(id)) {
                        iterator.remove();
                    }
                }
                mEventBus.post(new MembersEvent(MembersEvent.DELETE_USER));
            }

            break;

            case Constant.MSG_RENAME_USER_RESPOND: {//重命名成功回复
                MemberEntity member = FastJsonTools.getItem(jsonStr, MemberEntity.class);
                for (MemberEntity memberEntity : mMembers) {
                    if (memberEntity.equals(member)) {
                        memberEntity.setIdentity_name(member.getIdentity_name());
                        break;
                    }
                }
                mEventBus.post(new MembersEvent(MembersEvent.RENAME_USER));
            }
            break;

            case Constant.MSG_REQUEST_ADD_USER: {//底层Manager发来添加用户请求
                String id = jsonObject.getString("identity_id");
                MembersEvent event = new MembersEvent(MembersEvent.REQUEST_ADD_USER);
                event.setAddUserId(id);
                mEventBus.post(event);
            }
            break;

            /***************************设备********************************/
            case Constant.MSG_LIST_DEVICE_RESPOND: {//设备列表
                JSONArray deviceJsonArray = jsonObject.getJSONArray("devices");
                String devicesJsonStr;
                if (deviceJsonArray != null) {
                    devicesJsonStr = deviceJsonArray.toString();
                    mAllDevices.clear();
                    mAllDevices.addAll(getList(devicesJsonStr, DeviceData.class));


                    //请求设备状态
                    String deviceStatussJson = JsonCreator.createRequestAllDeviceStatusJson();
                    sendData(deviceStatussJson);
                }


//                for (int i = 0; i < mAllDevices.size(); i++) {
//                    DeviceData deviceData = mAllDevices.get(i);
//                    String dev_type = deviceData.getDev_type();
//                    if(isCamera(dev_type)){
//                        int camera_id = deviceData.getDev_id();
//                        //SecqLogUtil.d("--------------camera_id = " + camera_id);
//                    }
//                    //jyc
//                }
            }
            break;

            case Constant.MSG_ADD_DEVICE_RESPOND: {//新增设备
                DeviceData device = FastJsonTools.getItem(jsonStr, DeviceData.class);
                if (device != null && null == getDeviceById(device.getDev_id()) && device.getDev_id() != 0) {//防止重复添加

                    if (device.getDev_type() != null && device.getDev_type().equals(Constant.ZHA_ACTION_SECURE_RC)) {
                        return;
                    }


                    if (device.getDev_type().equals(Constant.ZHA_SENSOR_THERMOSTAT)) {
                        device.setMode(0);
                    }

                    if (device.getMode() == 1) {

                        mDevices.add(device);
                    } else if (device.getMode() == 0) {
                        mSensor.add(device);
                    }

                    DeviceEvent event = new DeviceEvent(DeviceEvent.DEV_ADD);
                    event.setDeviceData(device);
                    EventBus.getDefault().post(event);
                } else {

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
//                            new CustomToast(mContext, mContext.getString(R.string.string_add_dev));
                            showAddDevDialog();
                        }
                    });
                }


            }
            break;

            case Constant.MSG_DELETE_DEVICE_RESPOND: {//删除设备
                int dev_id = jsonObject.getIntValue("dev_id");
                DeviceData device = getDeviceById(dev_id);
                int count = -1;
                if (device != null) {

                    if (device.getMode() == 1) {
                        count = mDevices.indexOf(device);
                        mDevices.remove(device);
                    } else if (device.getMode() == 0) {
                        count = mSensor.indexOf(device);
                        mSensor.remove(device);
                    }


                    DeviceEvent event = new DeviceEvent(DeviceEvent.DEV_REMOVE);
                    event.setDeviceData(device);
                    event.setPosition(count);
                    EventBus.getDefault().post(event);
                }
            }
            break;

            case Constant.MSG_EDIT_DEVICE_RESPOND: {//编辑设备
                DeviceData device = FastJsonTools.getItem(jsonStr, DeviceData.class);
                //SecqLogUtil.e("-----------jsonStr 123456= " + jsonStr + " ok = " + device.getCamera_id());

                //编辑设备中修改了房间
                if (device.getRoom_id() == DeviceData.NO_ROOM_ID) {
                    for (RoomData room : mRooms) {
                        for (int i = 0; i < room.getDev_list().size(); i++) {
                            if (room.getDev_list().get(i).equals(device.getDev_id())) {
                                room.getDev_list().remove(i);
                                break;
                            }
                        }
                    }
                } else {
                    room:
                    for (RoomData room : mRooms) {
                        if (room.getRoom_id() == device.getRoom_id()) {
                            for (Integer devId : room.getDev_list()) {
                                if (devId.equals(device.getDev_id())) {
                                    continue room;
                                }
                            }

                            room.getDev_list().add(device.getDev_id());
                        } else {
                            for (int i = 0; i < room.getDev_list().size(); i++) {
                                if (room.getDev_list().get(i).equals(device.getDev_id())) {
                                    room.getDev_list().remove(i);
                                    break;
                                }
                            }
                        }

                    }
                }


                go:
                for (int i = 0; i < mModes.size(); i++) {
                    if (mModes.get(i).getSec_mode() == ModeData.MODE_STAY) {
                        if (device.getSec_stay() == 1) {

                            for (Integer dev : mModes.get(i).getDev_list()) {
                                if (dev.equals(device.getDev_id())) {
                                    continue go;
                                }
                            }

                            mModes.get(i).getDev_list().add(device.getDev_id());


                        } else if (device.getSec_stay() == 0) {
                            for (int n = 0; n < mModes.get(i).getDev_list().size(); n++) {
                                if (mModes.get(i).getDev_list().get(n).equals(device.getDev_id())) {
                                    mModes.get(i).getDev_list().remove(n);
                                    continue go;
                                }
                            }

                        }


                    } else if (mModes.get(i).getSec_mode() == ModeData.MODE_DISARM) {

                        if (device.getSec_disarm() == 1) {

                            for (Integer dev : mModes.get(i).getDev_list()) {
                                if (dev.equals(device.getDev_id())) {
                                    continue go;
                                }
                            }

                            mModes.get(i).getDev_list().add(device.getDev_id());


                        } else if (device.getSec_disarm() == 0) {
                            for (int n = 0; n < mModes.get(i).getDev_list().size(); n++) {
                                if (mModes.get(i).getDev_list().get(n).equals(device.getDev_id())) {
                                    mModes.get(i).getDev_list().remove(n);
                                    continue go;
                                }
                            }

                        }

                    } else if (mModes.get(i).getSec_mode() == ModeData.MODE_AWAY) {

                        if (device.getSec_away() == 1) {

                            for (Integer dev : mModes.get(i).getDev_list()) {
                                if (dev.equals(device.getDev_id())) {
                                    continue go;
                                }
                            }

                            mModes.get(i).getDev_list().add(device.getDev_id());


                        } else if (device.getSec_away() == 0) {
                            for (int n = 0; n < mModes.get(i).getDev_list().size(); n++) {
                                if (mModes.get(i).getDev_list().get(n).equals(device.getDev_id())) {
                                    mModes.get(i).getDev_list().remove(n);
                                    continue go;
                                }
                            }

                        }

                    }
                }


                DeviceData checkedDevice = getDeviceById(device.getDev_id());
                if (checkedDevice != null) {
                    checkedDevice.setName(device.getName());
                    checkedDevice.setSec_away(device.getSec_away());
                    checkedDevice.setSec_stay(device.getSec_stay());
                    checkedDevice.setSec_disarm(device.getSec_disarm());
                    String i = device.getCamera_id();
                    //SecqLogUtil.e("-----------device = id = " + i);
                    checkedDevice.setCamera_id(device.getCamera_id());
                    DeviceEvent event = new DeviceEvent(DeviceEvent.DEV_EDIT);
                    event.setDeviceData(checkedDevice);
                    EventBus.getDefault().post(event);
                }

            }
            break;

            case Constant.MSG_REQUEST_ALL_DEVICE_STATUS_RESPOND: {//所有设备状态列表

                iterationDevAndSensor();

                String statusJsonArrayStr = jsonObject.getString("status_list");
                List<DeviceData> devices = getList(statusJsonArrayStr, DeviceData.class);
                for (DeviceData device : devices) {
//                    boolean isDelete = false;
//
//                    for (DeviceStatusData statusData : device.getStatus()) {
//
//                        if (device.getMode() == 1 && statusData.getId() == 1) {
//                            isDelete = true;
//                        } else if (device.getMode() == 0 && statusData.getId() == 64) {
//                            isDelete = true;
//
//                        }
//                    }
//
//                    if (isDelete)
//                        for (DeviceStatusData statusData : device.getStatus()) {
//                            if (statusData.getId() == Constant.ZB_ONLINE_AND_STATUS) {
//                                device.getStatus().remove(statusData);
//                                break;
//                            }
//                        }
                    int deviceId = device.getDev_id();
                    DeviceData checkedDevice = getDeviceById(deviceId);
                    if (checkedDevice != null) {
                        checkedDevice.setStatus(device.getStatus());

//                        String dev_type = checkedDevice.getDev_type();
//                        //SecqLogUtil.e("-----------dev_type = " + dev_type );
//
//                        List<DeviceStatusData> aa = device.getStatus();
//                        if(null != aa && aa.size() > 0){
//                            //SecqLogUtil.e("-----------dev_type device.getStatus() = " + new String(aa.get(0).getParams()));
//                        }else{
//                            //SecqLogUtil.e("-----------dev_type device.getStatus() = none");
//                        }
                    }
                }

                DeviceEvent event = new DeviceEvent(DeviceEvent.DEV_LIST);
                mEventBus.post(event);
            }
            break;

            case Constant.MSG_REPORT_DEVICE_STATUS_RESPOND: {//单个设备状态上报

                DeviceData device = FastJsonTools.getItem(jsonStr, DeviceData.class);
//                { "msg_type": 106, "dev_id": 4, "ipc_did": 5, "status": [ { "id": 2, "params": "AQAAAA==" } ] }
//                Log.d("AA", "------device jsonStr = " + jsonStr);
                if (device != null) {
//                    Log.d("AA", "------device jsonStr = " + jsonStr + "  device = " + device.toString());
                    if (device.getDev_type() != null && !device.getDev_type().equals(Constant.ZHA_ACTION_SECURE_RC)) {
                        new RightSideToast(mContext, mContext.getString(R.string.add_security_control_sensor));
                        return;
                    }


                    int deviceId = device.getDev_id();
                    DeviceData checkedDevice = getDeviceById(deviceId);


//                    //更新设备状态
                    if (checkedDevice != null) {
                        List<DeviceStatusData> deviceStatuss = device.getStatus();
                        List<DeviceStatusData> checkedDeviceStatuss = checkedDevice.getStatus();
                        if (checkedDeviceStatuss == null) {//原先没有状态，直接设置状态
                            checkedDevice.setStatus(deviceStatuss);
                        } else {//原先有状态，更新状态


                            // boolean isDelete = false;

                            out:
                            for (DeviceStatusData deviceStatus : deviceStatuss) {

                                for (DeviceStatusData checkedDeviceStatus : checkedDeviceStatuss) {

//                                    if (checkedDevice.getMode() == 1 && checkedDeviceStatus.getId() == 1) {
//                                        isDelete = true;
//                                    } else if (checkedDevice.getMode() == 0 && checkedDeviceStatus.getId() == 64) {
//                                        isDelete = true;
//                                    }

                                    if (checkedDeviceStatus.equals(deviceStatus)) {
                                        checkedDeviceStatus.setTime(deviceStatus.getTime());
                                        checkedDeviceStatus.setParams(deviceStatus.getParams());

                                        continue out;
                                    }
                                }
                                checkedDeviceStatuss.add(deviceStatus);
                            }


//                            if (isDelete)
//                                for (DeviceStatusData checkedDeviceStatus : checkedDeviceStatuss) {
//                                    if (checkedDeviceStatus.getId() == Constant.ZB_ONLINE_AND_STATUS) {
//                                        checkedDeviceStatuss.remove(checkedDeviceStatus);
//                                        break;
//                                    }
//                                }
                        }

                        DeviceEvent event = new DeviceEvent(DeviceEvent.DEV_UPDATE);
                        event.setDeviceData(checkedDevice);
                        mEventBus.post(event);
                    }

                    //唤醒box
                    if (SecQreSettingApplication.isSleep)
                        if (checkedDevice.getStatus() != null && !checkedDevice.getStatus().isEmpty()) {
                            for (DeviceStatusData statusData : checkedDevice.getStatus()) {
                                if (statusData.getId() == Constant.STATUS_ID_BASE_SENSOR) {
                                    int onff = DeviceJudge.statusToint(statusData.getId(), statusData.getParams());

                                    if (onff == 1) {

                                        if (mCurMode != null)
                                            for (int i : mCurMode.getDev_list()) {
                                                if (i == checkedDevice.getDev_id()) {
                                                    wakeupBox();
                                                    break;
                                                }
                                            }

                                    }

                                }
                            }
                        }

                    sendAlertInfo(deviceId, device.getCamera_id());

                }
                //SecqLogUtil.d("单个设备状态改变");


            }
            break;


            /*****************模式操作******************/
            case Constant.MSG_LIST_MODE_RESPOND: {//列出模式
                int curModeId = jsonObject.getIntValue("cur_sec_mode");
                String modeArrayStr = jsonObject.getString("modes");
                if (modeArrayStr != null) {
                    mModes.clear();
                    mModes.addAll(FastJsonTools.getList(modeArrayStr, ModeData.class));
                }

                //检测当前模式下是否有警报
                if (mCurMode != null) {
                    mCurMode = getModeById(curModeId);

                    DeviceEvent event = new DeviceEvent(DeviceEvent.DEV_MODE);
                    mEventBus.post(event);


                } else {
                    mCurMode = getModeById(curModeId);
                }
            }
            break;

            case Constant.MSG_EDIT_MODE_RESPOND: {//编辑模式
                ModeData modeData = FastJsonTools.getItem(jsonStr, ModeData.class);
                replaceMode(modeData);
                //获取模式列表
                String modeListJson = JsonCreator.createListModeJson();
                sendData(modeListJson);
            }
            break;

            case Constant.MSG_APPLY_MODE_RESPOND: {//应用模式
                int curModeId = jsonObject.getIntValue("cur_sec_mode");
                mCurMode = getModeById(curModeId);
                if (mCurMode != null) {
                    DeviceEvent event = new DeviceEvent(DeviceEvent.DEV_MODE);
                    mEventBus.post(event);
                }
            }
            break;

            case Constant.MSG_LIST_ROOM_RESPOND:
                String rooms = jsonObject.getString("rooms");

                if (rooms != null) {
                    mRooms.clear();
                    mRooms.addAll(FastJsonTools.getList(rooms, RoomData.class));
                }

                mEventBus.post(new RoomEvent(RoomEvent.LIST_ROOM));
                break;

            case Constant.MSG_NEW_ROOM_RESPOND:
                String roomNew = jsonObject.getString("rooms");

                if (roomNew != null) {
                    mRooms.clear();
                    mRooms.addAll(FastJsonTools.getList(roomNew, RoomData.class));
                }

                mEventBus.post(new RoomEvent(RoomEvent.NEW_ROOM));

                break;

            case Constant.MSG_DELETE_ROOM_RESPOND:
                String roomDelete = jsonObject.getString("rooms");

                if (roomDelete != null) {
                    mRooms.clear();
                    mRooms.addAll(FastJsonTools.getList(roomDelete, RoomData.class));
                }
                mEventBus.post(new RoomEvent(RoomEvent.DELETE_ROOM));

                break;


            case Constant.MSG_EDIT_ROOM_RESPOND:
                String roomEdit = jsonObject.getString("rooms");

                if (roomEdit != null) {
                    mRooms.clear();
                    mRooms.addAll(FastJsonTools.getList(roomEdit, RoomData.class));
                }
                mEventBus.post(new RoomEvent(RoomEvent.EDIT_ROOM));

                break;

        }
    }

    /**
     * 判断设备是否是摄像头
     *
     * @param devType
     * @return
     */

    public static boolean isCamera(String devType) {
        if (devType.equals(Constant.DEV_IP_CAMERA)) {
            return true;
        }
        return false;
    }


    private String getCameraShowDID(int camera_id) {
        String mCameraShowDID = "";
        if (camera_id != 0) {
            DeviceData cameraDeviceData = getDeviceById(camera_id);
            if (null != cameraDeviceData) {

                String dev_type = cameraDeviceData.getDev_type();
                //SecqLogUtil.d("-----------dev_type = " + dev_type);

                List<DeviceStatusData> aa = cameraDeviceData.getStatus();
                if (null != aa && aa.size() > 0) {
                    mCameraShowDID = new String(aa.get(0).getParams());
                    //SecqLogUtil.d("-----------dev_type device.getStatus() = " + mCameraShowDID);
                } else {
                    //SecqLogUtil.d("-----------dev_type device.getStatus() = none");
                }
            }
//                cameraDeviceData.getStatus().get(0).
        }
        return mCameraShowDID;
    }

    /**
     * 检测并发送警报信息
     *
     * @param deviceId 检测是设备id
     */
    private void sendAlertInfo(int deviceId, String camera_id) {
        AppLog.d("检测并发送警报信息");
        //1.判断设备是否处于警报状态
        if (isDeviceAlert(deviceId)) {
            DeviceData checkedDevice = getDeviceById(deviceId);
            final String info = checkedDevice.getName() + " " + mContext.getString(R.string.sensor_trigger);

            AppLog.e("------------camera_id = " + camera_id);

//            final String cameraShowDID = getCameraShowDID(camera_id);
            final String cameraShowDID = camera_id;
            //2.吐司提示警报信息
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    boolean isShowNotificationToast = NotificationSystemChooseFragment.getIsShowNotificationToast(mContext);
                    if (isShowNotificationToast) {
                        showFloatCameraView(cameraShowDID);
                        new RightSideToast(mContext, info);
                    }
                }
            });

            //3.发送短信给紧急联系人
            if (NetUtils.isFastNetwork(mContext)) {
                if (EmergencyContactFragment.contacts == null) {
                    EmergencyContactFragment.contacts = IniFileOperate.readContactIni();
                }
                SmsManager smsManager = SmsManager.getDefault();
                for (ContactEntity contact : EmergencyContactFragment.contacts) {
                    if (contact.getEmergencyFlag() == 1) {
                        smsManager.sendTextMessage(contact.getNumber(), null, info, null, null);
                    }
                }
            } else {
                AppLog.d("没有4g模块不发送短信");
            }

        }

    }

    private static final String RECEIVER_MAC = "receiver_mac";
    public static final String FLAG_SHOW_FLOAT_VIEW = "flag_show_view";
    private static final String RECEIVER_POSITION = "receiver_position";
    public static final String ACTION_IPCAMERA_SERVICE_FLOAT = "com.ipcamera.floatviewservice";

    private void showFloatCameraView(String did) {
        if (TextUtils.isEmpty(did)) {
            return;
        }
        String[] strs = new String[]{"SLIFE000870HTRKW", "SLIFE000253LLLPU"};
        List<CameraEntity> cameraList = getCameraList();
        int size = cameraList.size();

        for (int i = 0; i < size; i++) {
            String deviceID = cameraList.get(i).getDeviceID();
            if (did.equals(deviceID)) {
                Log.e("", "-------------307 msg_--type ------deviceID start= " + deviceID);
                startServiceFloat(did);
            }
            Log.e("", "-------------307 msg_--type ------deviceID = " + deviceID);
        }
    }


    private void startServiceFloat(String mac) {
        Intent intent1 = new Intent();
        intent1.setAction(ACTION_IPCAMERA_SERVICE_FLOAT);
        intent1.putExtra(RECEIVER_MAC, mac);
        intent1.putExtra(FLAG_SHOW_FLOAT_VIEW, true);
        intent1.setPackage("com.ipcamera.main");
//        intent1.setPackage("com.ipcamera.floatservice");
//        mContext.startActivityAsUser(intent1, UserHandle.OWNER);
//        if (Utils.isVersionAbove6()) {
        sendLauncherFloatWindowReceiver(mac);
//        } else {
//            mContext.startService(intent1);
//        }
    }

    public static final String ACTION_IPCAMERA_KEY_FLOAT = "com.sen5.process.camera.key";
    public static final String ACTION_RECEIVER_CLOSE_SERVICE = "com.sen5.process.camera.close.float";
    public static final String ACTION_SMARTHOME_KEY_CODE = "com.sen5.process.smarthome.key";


    private void sendLauncherFloatWindowReceiver(String mac) {
        Intent intent = new Intent(ACTION_IPCAMERA_KEY_FLOAT);
        intent.putExtra(RECEIVER_MAC, mac);
        mContext.sendBroadcast(intent);
    }


    /**
     * 唤醒box
     */
    private void wakeupBox() {

        translationvirtualKey(26);
        //SecqLogUtil.e("发送唤醒广播");


    }

    public static void translationvirtualKey(int code) {
        long mDownTime = SystemClock.uptimeMillis();
        KeyEvent evDown = new KeyEvent(mDownTime, mDownTime,
                KeyEvent.ACTION_DOWN, code, 0, 0,
                KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0
                | KeyEvent.FLAG_FROM_SYSTEM
                | KeyEvent.FLAG_VIRTUAL_HARD_KEY,
                InputDevice.SOURCE_KEYBOARD);
        InputManager.getInstance().injectInputEvent(evDown,
                InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);

        long mUpTime = SystemClock.uptimeMillis();
        KeyEvent evUp = new KeyEvent(mUpTime, mUpTime, KeyEvent.ACTION_UP,
                code, 0, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0
                | KeyEvent.FLAG_FROM_SYSTEM
                | KeyEvent.FLAG_VIRTUAL_HARD_KEY,
                InputDevice.SOURCE_KEYBOARD);
        InputManager.getInstance().injectInputEvent(evUp,
                InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
    }

    /**
     * 判断是否报警，只有设备处于设防模式并且被触发才报警
     *
     * @param deviceId
     * @return
     */
    private boolean isDeviceAlert(int deviceId) {
        boolean flag = false;
        DeviceData device = mP2PModel.getDeviceById(deviceId);
        //1.判断设备是否处于设防模式
        if (device != null
                && mCurMode != null
                && device.getStatus() != null
                && mCurMode.getDev_list().contains(deviceId)) {
            //SecqLogUtil.d(deviceId + " ：设备处于设防模式");
            //2.判断设备是否处于警报状态
            for (DeviceStatusData deviceStatus : device.getStatus()) {
                // TODO: 2016/10/8 临时适配zwave设备
                int statusId = deviceStatus.getId();
                if (((statusId == Constant.STATUS_ID_ON_OFF
                        && device.getMode() == Constant.MODE_SENSOR))
                        || Constant.ZB_STATUS_ID_BASIC == statusId
                        || statusId == Constant.STATUS_ID_FEIBIT_SENSOR
                        || statusId == Constant.STATUS_ID_DOOR_SENSOR
                        || statusId == Constant.STATUS_ID_HOME_SECURITY) {

                    int status = deviceStatus.getParams()[0];
                    int status_one = deviceStatus.getParams()[1];
                    AppLog.d(deviceStatus.getParams()[0]);
                    AppLog.d(deviceStatus.getParams()[1]);
//                    AppLog.d(deviceStatus.getParams()[1]);
//                    AppLog.d(deviceStatus.getParams()[2]);
//                    AppLog.d(deviceStatus.getParams()[3]);

                    AppLog.d("状态参数：" + status);
                    if (status == DeviceStatusData.SENSOR_STATUS_DANGER
                            || status == 7
                            || status == 8) {
                        flag = true;
                        AppLog.d(deviceId + " 0发出警报");
                    }

                    if (status_one == DeviceStatusData.SENSOR_STATUS_DANGER
                            || status_one == 7
                            || status_one == 8) {
                        flag = true;
                        AppLog.d(deviceId + " 1发出警报");
                    }
//
//                    try {
//                        if (devType.equals(Constant.ZHA_ACTION_SECURE_RC)) {
//                            //安防遥控器 "action_ids":[],"status_ids":[2]
//
//                            DeviceStatusData deviceStatus = deviceStatuss.get(0);
//                            byte[] params = deviceStatus.getParams();
//                            if (params != null) {
//                                int status = params[0];
//
//                            }
//
//                        } else if (devType.equals(Constant.ZHA_ACTION_EMERGENCY_BUTTON)) {
//                            //紧急按钮 "action_ids":[],"status_ids":[2]
//
//                        } else if (devType.equals(Constant.ZHA_ACTION_ALERTOR)) {
//                            //报警器  "action_ids":[1],"status_ids":[1]
//
//                        } else if (devType.equals(Constant.ZHA_SENSOR)) {
//                            //ZHA感器  "action_ids":[],"status_ids":[2]
//
//                        } else if (devType.equals(Constant.ZHA_SENSOR_INFRARED)) {
//                            //红外传感器 "action_ids":[],"status_ids":[2]
//                            DeviceStatusData deviceStatus = deviceStatuss.get(0);
//                            byte[] params = deviceStatus.getParams();
//                            if (params != null) {
//                                int status = params[0];
//
//                            }
//
//                        } else if (devType.equals(Constant.ZHA_SENSOR_DOOR)) {
//                            //门磁传感器 "action_ids":[],"status_ids":[2]
//                            DeviceStatusData deviceStatus = deviceStatuss.get(0);
//                            byte[] params = deviceStatus.getParams();
//                            if (params != null) {
//                                int status = params[0];
//
//                            }
//
//                        } else if (devType.equals(Constant.ZHA_SENSOR_SMOKE)) {
//                            //烟雾传感器 "action_ids":[],"status_ids":[2]
//                            DeviceStatusData deviceStatus = deviceStatuss.get(0);
//                            byte[] params = deviceStatus.getParams();
//                            if (params != null) {
//                                int status = params[0];
//
//                            }
//
//                        } else if (devType.equals(Constant.ZHA_SENSOR_COMBUSTIBLE_GAS)) {
//                            //易燃气体传感器 "action_ids":[],"status_ids":[2]
//                            DeviceStatusData deviceStatus = deviceStatuss.get(0);
//                            byte[] params = deviceStatus.getParams();
//                            if (params != null) {
//                                int status = params[0];
//
//                            }
//
//                        } else if (devType.equals(Constant.ZHA_SENSOR_CO)) {
//                            //Co传感器 "action_ids":[],"status_ids":[2]
//                            DeviceStatusData deviceStatus = deviceStatuss.get(0);
//                            byte[] params = deviceStatus.getParams();
//                            if (params != null) {
//                                int status = params[0];
//                            }
//
//                        } else if (devType.equals(Constant.ZHA_SENSOR_SHOCK)) {
//                            //震动传感器 "action_ids":[],"status_ids":[2]
//                            DeviceStatusData deviceStatus = deviceStatuss.get(0);
//                            byte[] params = deviceStatus.getParams();
//                            if (params != null) {
//                                int status = params[0];
//                            }
//
//                        } else if (devType.equals(Constant.ZHA_SENSOR_WATER)) {
//                            //水侵传感器 "action_ids":[],"status_ids":[2]
//                            DeviceStatusData deviceStatus = deviceStatuss.get(0);
//                            byte[] params = deviceStatus.getParams();
//                            if (params != null) {
//                                int status = params[0];
//                            }
//
//                        } else if (devType.equals(Constant.ZHA_SENSOR_HUMITURE)) {
//                            //温湿度传感器  "action_ids":[],"status_ids":[3,4] 【3:温度；4：湿度】
//                            for (DeviceStatusData deviceStatus : deviceStatuss) {
//                                byte[] params = deviceStatus.getParams();
//                                if (params != null) {
//                                    int statusId = deviceStatus.getId();
//                                    if (statusId == Constant.STATUS_ID_FEIBIT_SENSOR_TEMPERATURE) {//温度
//                                        int temperature_int = params[0];
//                                        int temperature_dec = params[1];
//                                        double temperature = temperature_int + temperature_dec / 100.0;
//
//                                        int status;
//                                        if (temperature > 40 && temperature < 0) {
//                                            status = DeviceStatusData.SENSOR_STATUS_DANGER;
//                                        } else {
//                                            status = DeviceStatusData.SENSOR_STATUS_SAFETY;
//                                        }
//                                        drawableId = DeviceTypeJudge.getStatusDrawable(devType, status);
//
//                                    } else if (statusId == Constant.STATUS_ID_FEIBIT_SENSOR_HUMIDITY) {//湿度
//
//                                    }
//                                }
//                            }
//
//                        } else if (devType.equals(Constant.SENSOR_BOX_ALERTOR)) {
//                            //盒子自带报警器
//
//                        } else if (devType.equals(Constant.ZWAVE_SENSOR_INFRARED)) {
//                            //红外传感器 "action_ids":[],"status_ids":[9]
//                            DeviceStatusData deviceStatus = deviceStatuss.get(0);
//                            byte[] params = deviceStatus.getParams();
//                            if (params != null) {
//                                int status = params[0];
//                                drawableId = DeviceTypeJudge.getStatusDrawable(devType, status);
//                            }
//
//                        } else if (devType.equals(Constant.ZWAVE_SENSOR_DOOR)) {
//                            //门磁传感器 "action_ids":[],"status_ids":[7]
//                            DeviceStatusData deviceStatus = deviceStatuss.get(0);
//                            byte[] params = deviceStatus.getParams();
//                            if (params != null) {
//                                int status = params[0];
//                                drawableId = DeviceTypeJudge.getStatusDrawable(devType, status);
//                            }
//
//                        } else if (devType.equals(Constant.ZWAVE_SENSOR_SMOKE)) {
//                            //烟雾传感器 "action_ids":[],"status_ids":[13]
//
//                        } else if (devType.equals(Constant.ZWAVE_SENSOR_COMBUSTIBLE_GAS)) {
//                            //易燃气体传感器 "action_ids":[],"status_ids":[14]
//
//                        } else if (devType.equals(Constant.ZWAVE_SENSOR_CO)) {
//                            //Co传感器 "action_ids":[],"status_ids":[12]
//
//                        } else if (devType.equals(Constant.ZWAVE_SENSOR_WATER)) {
//                            //水侵传感器 "action_ids":[],"status_ids":[11]
//
//                        } else if (devType.equals(Constant.ZWAVE_SENSOR_HUMITURE)) {
//                            //温湿度传感器  "action_ids":[],"status_ids":[3,10] 【3：温度；10：湿度】
//
//                        }
//
//                    } catch (IndexOutOfBoundsException e) {
//                        //SecqLogUtil.e("数组越界");
//                        e.printStackTrace();
//                    }

                    break;
                }
            }
        }
        //SecqLogUtil.d("状态改变未触发报警");
        return flag;
    }

    //所有设备，包括普通设备、Camera、盒子报警器
    public final List<DeviceData> mAllDevices = new ArrayList<>();
    //家庭成员列表
//    private List<MemberEntity> mMembers = new ArrayList<>();
    //模式列表
    private final List<ModeData> mModes = new ArrayList<>();

    //场景列表
    private final List<SceneData> mScenes = new ArrayList<>();

    //房间
    public static final List<RoomData> mRooms = new ArrayList<>();

    public int getmCurMember() {
        SharedPreferences preferences = mContext.getSharedPreferences("memberSize", Context.MODE_PRIVATE);
        mCurMember = preferences.getInt("memberSize", 0);
        return mCurMember;
    }

    public void setmCurMember(int mCurMember) {

        SharedPreferences preferences = mContext.getSharedPreferences("memberSize", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("memberSize", mCurMember);
        editor.commit();

        this.mCurMember = mCurMember;
    }

    private int mCurMember;

    private ModeData mCurMode;

    /*************************
     * Members操作
     **********************************/
    public List<MemberEntity> getMembers() {
        return mMembers;
    }

    /*************************
     * Device操作
     **********************************/
    public DeviceData getDeviceById(int devId) {

        for (DeviceData data : mDevices) {
            if (data.getDev_id() == devId) {
                return data;
            }
        }

        for (DeviceData data : mSensor) {
            if (data.getDev_id() == devId) {
                return data;
            }
        }
        return null;
    }

    public ArrayList<DeviceData> getmDevices() {
        return mDevices;
    }

    private ArrayList<DeviceData> mDevices = new ArrayList<>();

    private List<MemberEntity> mMembers = new ArrayList<>();

    public ArrayList<DeviceData> getmSensor() {
        return mSensor;
    }

    private ArrayList<DeviceData> mSensor = new ArrayList<>();

    private void iterationDevAndSensor() {
        synchronized (this) {

            mSensor.clear();
            mDevices.clear();
            if (mAllDevices.isEmpty()) {
                return;
            }

            for (DeviceData data : mAllDevices) {

                if (data.getDev_type().equals(Constant.ZHA_SENSOR_THERMOSTAT)) {
                    data.setMode(0);
                }


                if (data.getMode() == 0) {
                    if (!data.getDev_type().equals(Constant.ZHA_ACTION_SECURE_RC)) {
                        mSensor.add(data);
                    }
                } else if (data.getMode() == 1) {

                    mDevices.add(data);

                }
            }
        }

    }


    /********************************
     * 模式操作
     *****************************/
    public List<ModeData> getModeDatas() {
        return mModes;
    }

    public ModeData getCurMode() {
        return mCurMode;
    }

    public boolean replaceMode(ModeData modeData) {
        for (int i = 0; i < mModes.size(); i++) {
            if (modeData.equals(mModes.get(i))) {
                mModes.set(i, modeData);
                return true;
            }
        }
        return false;
    }

    public ModeData getModeById(int mode) {
        for (ModeData modeData : mModes) {
            if (modeData.getSec_mode() == mode) {
                return modeData;
            }
        }
        return null;
    }

    /********************************
     * Scene操作
     *****************************/
    public List<SceneData> getScenes() {
        return mScenes;
    }

    public void addScene(SceneData sceneData) {
        mScenes.add(sceneData);
    }

    public void deleteScene(int sceneId) {
        Iterator iterator = mScenes.iterator();
        while (iterator.hasNext()) {
            SceneData sceneData = (SceneData) iterator.next();
            if (sceneData.getScene_id() == sceneId) {
                iterator.remove();
            }
        }
    }

    public boolean replaceScene(SceneData scene) {
        int sceneId = scene.getScene_id();
        for (int i = 0; i < mScenes.size(); i++) {
            SceneData scene1 = mScenes.get(i);
            if (scene1.getScene_id() == sceneId) {
                mScenes.set(i, scene);
                return true;
            }
        }

        return false;
    }

    private void showAddDevDialog() {

        if (mContext == null) {
            return;
        }

        if (dialog == null) {

            dialog = new Dialog(mContext, R.style.AddDevicesDialog);
            View view = LayoutInflater.from(SecQreSettingApplication.mContext).inflate(R.layout.dialog_scan, null);

            waterWaveView = (WaterWaveView) view.findViewById(R.id.waterWaveView);
            scanView = (ScanView) view.findViewById(R.id.scanView);
            scanView.setListener(new ScanView.timeListener() {
                @Override
                public void onTimeFinish() {
                    dialog.dismiss();
                }
            });

            Window dialogWindow = dialog.getWindow();

            WindowManager m = ((Activity) mContext).getWindowManager();
            Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
            WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            p.height = d.getHeight(); // 高度设置为屏幕的0.6
            p.width = (int) (d.getWidth() * 0.25); // 宽度设置为屏幕的0.65
            dialogWindow.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

            dialogWindow.setAttributes(p);


            dialog.setContentView(view);
        } else {
            scanView.reset();
            waterWaveView.resetWave();
        }


        dialog.show();
    }


    private class secureRunnable implements Runnable {

        private String data;

        public secureRunnable(String data) {
            this.data = data;
        }


        @Override
        public void run() {
            mSocketClient.sendData(data);
        }
    }

}
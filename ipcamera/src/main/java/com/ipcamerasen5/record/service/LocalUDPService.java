package com.ipcamerasen5.record.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ipcamerasen5.record.db.IpCamDevice;
import com.ipcamerasen5.record.event.AddIpcameraEvent;
import com.ipcamerasen5.record.event.DeleteIpcameraEvent;
import com.ipcamerasen5.record.event.IpCamMoveEvent;
import com.ipcamerasen5.record.event.IpcameraExit;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import nes.ltlib.LTSDKManager;
import nes.ltlib.utils.LogUtils;

/**
 * 监听本地udp广播
 * Created by chenqianghua on 2017/9/11.
 */

public class LocalUDPService extends Service {

    private boolean isFirstTime;
    private int num = 0;
    private List<IpCamMoveEvent> moves = new ArrayList<>();
    private List<Timer> timers = new ArrayList<>();
    private List<IpCamDevice> mIpCamDevices = new ArrayList<>();
    //    private
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("LocalUDPService", "handlemessge what is " + msg.what);
            switch (msg.what) {
                case 0:
                    //开始录制
                    moves.get(0).setStatus(0);
                    EventBus.getDefault().post(moves.get(0));
                    if (moves.get(0).isRecording()) {
                        moves.get(0).setStop(false);
                    }
                    moves.get(0).setRecording(true);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (moves.get(0).isStop()) {
                                Log.d("LocalUDPService", "position is " + 0);
                                //停止录制
                                moves.get(0).setStatus(1);
                                EventBus.getDefault().post(moves.get(0));
                                //录制结束，更新状态
                                moves.get(0).setRecording(false);
                            }
                        }
                    }, 70000);
                    moves.get(0).setStop(true);
                    break;
                case 1:
                    if (moves.get(1).isRecording()) {
                        moves.get(1).setStop(false);
                    }
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (moves.get(1).isStop()) {
                                Log.d("LocalUDPService", "position is " + 1);
                                EventBus.getDefault().post(moves.get(1));
                            }
                        }
                    }, 70000);
                    moves.get(1).setStop(true);
                    break;
                case 2:
                    if (moves.get(2).isRecording()) {
                        moves.get(2).setStop(false);
                    }
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (moves.get(2).isStop()) {
                                Log.d("LocalUDPService", "position is " + 2);
                                EventBus.getDefault().post(moves.get(2));
                            }
                        }
                    }, 70000);
                    moves.get(2).setStop(true);
                    break;
                case 3:
                    if (moves.get(3).isRecording()) {
                        moves.get(3).setStop(false);
                    }
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (moves.get(3).isStop()) {
                                Log.d("LocalUDPService", "position is " + 3);
                                EventBus.getDefault().post(moves.get(3));
                            }
                        }
                    }, 70000);
                    moves.get(3).setStop(true);
                    break;
            }
        }
    };

    public Handler mHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            switch (msg.what){
//                case 0:
//                    Log.d("LocalUDPService","handle2 is "+0);
//                    EventBus.getDefault().post(moves.get(0));
//                    break;
//            }
            Log.d("LocalUDPService", "handle2 is " + msg.what);
            EventBus.getDefault().post(moves.get(msg.what));
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("LocalUDPService", "onCreate");
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {


        if (mIpCamDevices.size() > 0) {
            mIpCamDevices.clear();
        }

        if (moves.size() > 0) {
            moves.clear();
        }
        if (timers.size() > 0) {
            moves.clear();
        }
        mIpCamDevices.addAll(IpCamDevice.findAll(IpCamDevice.class));
        for (int i = 0; i < mIpCamDevices.size(); i++) {
            IpCamMoveEvent ipcameMoveEvent = new IpCamMoveEvent(mIpCamDevices.get(i).getDid());
            moves.add(ipcameMoveEvent);
            Timer mTimer = new Timer();
            timers.add(mTimer);
        }
        Thread mThread = new Thread() {
            @Override
            public void run() {
                DatagramSocket dgSocket = null;
                while (true) {
                    Log.d("LocalUDPService", "onStartCommand");
                    try {
                        if (dgSocket == null) {
                            dgSocket = new DatagramSocket(null);
                            dgSocket.setReuseAddress(true);
                            dgSocket.bind(new InetSocketAddress(8765));
                        }
                        byte[] content = new byte[1024];
                        Log.d("LocalUDPService", "content.size is " + content.length);
                        DatagramPacket dgPacker = new DatagramPacket(content, content.length);
                        dgSocket.receive(dgPacker);
                        Log.d("LocalUDPService", "dgPacker is " + dgPacker.getAddress() + "port is " + dgPacker.getPort() + " add is " + dgPacker.getSocketAddress());
                        if (dgPacker.getData()[3] == 2) {
                            Log.d("LocalUDPService", "data[3] is 2");

                            // TODO: 2017/9/13 timer
                            String str = new String(dgPacker.getData(), 0, dgPacker.getLength());
                            String str_alarm_id = str.substring(4, 14);
                            Log.d("LocalUDPService", "str_alarm_id is " + str_alarm_id);
                            for (int i = 0; i < LTSDKManager.getInstance().mLTDevices.size(); i++) {
                                Log.d("LocalUDPService", "did is " + LTSDKManager.getInstance().mLTDevices.get(i).getGid());
                                if (LTSDKManager.getInstance().mLTDevices.get(i).getGid().equals(str_alarm_id)) {
                                    Log.d("LocalUDPService", "position is " + i);
                                    int position = i;
                                    if (position < moves.size()) {
                                        removeTimer(position);
                                        setTimer(position);
                                        moves.get(position).setStatus(0);
                                        Message message = new Message();
                                        message.what = position;
                                        mHandler2.sendMessage(message);
                                    }
                                }
                            }
                        }
                    } catch (SocketException e) {
                        e.printStackTrace();
                        Log.d("LocalUDPService", e.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("LocalUDPService", e.getMessage());
                    }
                }
            }
        };
        mThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 开始进行timer
     *
     * @param position
     */
    public void setTimer(final int position) {
        Log.d("LocalUDPService", "setTimer is " + position);
        if (timers.size() > position) {
            Timer mTimer = timers.get(position);
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //发送停止录制的message
                    moves.get(position).setStatus(1);
                    Message message = new Message();
                    message.what = position;
                    mHandler2.sendMessage(message);
                }
            }, 65000);
        }
    }

    /**
     * 移除之前的timer并增加新的timer
     *
     * @param position
     */
    public void removeTimer(int position) {
        Log.d("LocalUDPService", "removeTimer is " + position);
        if (timers.size() > position) {
            timers.get(position).cancel();
            timers.remove(position);
        }
        timers.add(position, new Timer());
    }

    @Subscribe
    public void dealwithAddCameraEvent(AddIpcameraEvent event) {
        IpCamMoveEvent ipcameMoveEvent = new IpCamMoveEvent(event.getDid());
        moves.add(ipcameMoveEvent);
        Timer mTimer = new Timer();
        timers.add(mTimer);
    }

    @Subscribe
    public void dealwithDeleteCameraEvent(DeleteIpcameraEvent event) {
        int position = -1;
        for (int i = 0; i < moves.size(); i++) {
            if (Objects.equals(moves.get(i).getDid(), event.getDid())) {
                position = i;
            }
        }
        if (position != -1) {
            moves.remove(position);
            timers.get(position).cancel();
            timers.remove(position);
        }
    }

    @Override
    public void onDestroy() {
        Log.e("localUdpService","ondestroy");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (null != timers){
            for (int i =0; i<timers.size(); i++){
                timers.get(i).cancel();
            }
        }
        timers.clear();
        timers = null;
        moves.clear();
        moves = null;
        mIpCamDevices.clear();
        mIpCamDevices = null;
    }

    @Subscribe(threadMode =  ThreadMode.MAIN)
    public void dealwithExit(IpcameraExit event){
        LogUtils.e("dealwithExit","exit");
        stopSelf();
    }

}

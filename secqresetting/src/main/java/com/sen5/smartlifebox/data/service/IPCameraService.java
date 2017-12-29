/**
 * 文件名：IPCameraService.java
 * 版权：Copyright 2002-2007 Sen5 Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：wanglin
 * 修改时间：2016年7月26日
 * 修改单号：
 * 修改内容：
 */

package com.sen5.smartlifebox.data.service;


import android.os.Handler;
import android.os.Message;

import nes.ltlib.utils.AppLog;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteOrder;


/**
 * 提供IPCamera访问服务
 *
 * @author wanglin
 */
public class IPCameraService {

    public static final int PORT = 20387;
    public static final int SEND_SUCCESS = 1;
    public static final int SEND_FAIL = 0;
    public boolean isRunning = true;
    private CallBack mCallBack;
    private Handler mHandler;
    private ServerSocket mServerSocket;
    private Socket mSocket;

    public IPCameraService(CallBack callBack) {
        mCallBack = callBack;
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == SEND_SUCCESS){
                    mCallBack.onSuccess();
                }else if(msg.what == SEND_FAIL){
                    mCallBack.onFail();
                }

            }
        };
    }

    public void start() {
        AppLog.e("IPCamera服务启动");
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                     mServerSocket = new ServerSocket(PORT);

                    while (isRunning) {
                        // 等待请求,此方法会一直阻塞,直到获得请求才往下走
                        mSocket = mServerSocket.accept();

                        AppLog.e("收到IPCamera的请求");
                        handleData(mSocket);
                    }
                } catch (Exception e) {
                    AppLog.e("服务器异常: " + e.getMessage());
                }
            }
        }).start();
    }

    public void stop() {
        AppLog.e("IPCamera服务停止");
        isRunning = false;
        try {
            if(mSocket != null){
                mSocket.close();
            }
            mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            AppLog.e("关闭Socket服务异常");
        }
    }

    private void handleData(Socket socket) {

        DataInputStream input = null;
        DataOutputStream out = null;

        try {
            // 读取客户端数据
            input = new DataInputStream(socket.getInputStream());
            byte[] cmd = new byte[4];
            input.read(cmd, 0, cmd.length);
            //当前平台使用的是小端模式，所以以小端模式将byte[]转成int
            int i = bytesToIntLittle(cmd, 0);
            AppLog.e("当前平台字节排序：" + ByteOrder.nativeOrder());
            AppLog.e("cmd = " + i);

            if (i == 1) {// 向客户端回复信息
                out = new DataOutputStream(socket.getOutputStream());

                //先发送一个命令通知IPCamera准备接收数据：cmd(4bytes) + datalen(4).
                byte[] respondCmd = getRespondCmd();
                out.write(respondCmd);
                out.flush();

                Thread.sleep(2000);

                //再发送wifi信息数据：ssid(32bytes) + key(32) + authtype(4) + enable(4) + other(64)
                out.write(getWifiInfo());
                out.flush();
                AppLog.e("发送wifi info完成");

                mHandler.sendEmptyMessage(SEND_SUCCESS);
            } else {
                AppLog.e("cmd != " + 1);
            }
        } catch (Exception e) {
            AppLog.e("服务器 run 异常: " + e.getMessage());
            mHandler.sendEmptyMessage(SEND_FAIL);
        } finally {
            if (socket != null) {
                try {
                    if (input != null) {
                    }
                    if (out != null) {
                        out.close();
                    }
                    socket.close();
                } catch (Exception e) {
                    socket = null;
                    AppLog.e("服务端 finally 异常:" + e.getMessage());
                }
            }
        }
    }

    /**
     * 以大端模式将int转成byte[]
     */
    public static byte[] intToBytesBig(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * 以小端模式将int转成byte[]
     *
     * @param value
     * @return
     */
    public static byte[] intToBytesLittle(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * 以大端模式将byte[]转成int
     */
    public static int bytesToIntBig(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset] & 0xFF) << 24)
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8)
                | (src[offset + 3] & 0xFF));
        return value;
    }

    /**
     * 以小端模式将byte[]转成int
     */
    public static int bytesToIntLittle(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }


    private byte[] getWifiInfo() {
        byte[] wifiInfo = new byte[136];
        byte[] ssidBytes = wifiName.getBytes();
        byte[] passwordBytes = wifiPassword.getBytes();
        byte[] authtypeBytes = intToBytesLittle(authType);
        byte[] enableBytes = intToBytesLittle(1);
        byte[] otherBytes = "".getBytes();

        System.arraycopy(ssidBytes, 0, wifiInfo, 0, ssidBytes.length);
        System.arraycopy(passwordBytes, 0, wifiInfo, 32, passwordBytes.length);
        System.arraycopy(authtypeBytes, 0, wifiInfo, 64, authtypeBytes.length);
        System.arraycopy(enableBytes, 0, wifiInfo, 68, enableBytes.length);
        System.arraycopy(otherBytes, 0, wifiInfo, 72, otherBytes.length);

        return wifiInfo;
    }

    private byte[] getRespondCmd() {
        byte[] respondCmdbytes = new byte[8];
        long cmd = 2;
        long length = 0;
        byte[] cmdBytes = intToBytesLittle((int) cmd);//使用小端存储
        byte[] lengthBytes = intToBytesLittle((int) length);

        System.arraycopy(cmdBytes, 0, respondCmdbytes, 0, cmdBytes.length);
        System.arraycopy(lengthBytes, 0, respondCmdbytes, 4, lengthBytes.length);

        return respondCmdbytes;
    }

    private String wifiName;
    private String wifiPassword;
    private int authType = 3;//默认是3：WPA2_PSK

    public void setAuthType(int authType) {
        this.authType = authType;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public void setWifiPassword(String wifiPassword) {
        this.wifiPassword = wifiPassword;
    }

    public interface CallBack {
        void onSuccess();

        void onFail();
    }
}



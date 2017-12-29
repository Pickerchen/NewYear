package com.sen5.secure.launcher.data.socket;

import com.eric.xlee.lib.utils.LogUtil;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import nes.ltlib.utils.AppLog;

/**
 * Created by jiangyicheng on 2017/3/7.
 */

public class SocketClient {
    private static final String HOST = "127.0.0.1";
    //	private static final String HOST = "localhost";
    private static final int PORT = 9999;
    private Socket mSocket = null;

    private InputStream mInputStream = null;
    private OutputStream mOS = null;
    private OnSocketClientListener mOnSocketClientListener;
    /**
     * 数据包头长度
     */
    private static final int PACKAGE_HEAD_LEN = 2;
    /**
     * 数据长度的长度，为2个字节
     */
    private static final int PACKAGE_LENGTH_LEN = 4;
    /**
     * 数据包尾长度
     */
    private static final int PACKAGE_END_LEN = 2;

    /**
     * 包头
     */
    private static final String PACKAGE_HEAD = "##";
    /**
     * 包尾
     */
    private static final String PACKAGE_END = "!!";
    /**
     * socket 服务端异常断开 上报的数据
     */
    private static final String SOCKET_DISCONNECT_DATA = "0000";
    private boolean mIsWhile;
//    public BufferedReader mBufferedReader;
//    public PrintWriter mPrintWriter;

    public SocketClient() {


    }

    public void connectSocket() {
        new Thread() {

            @Override
            public void run() {
                try {
                    AppLog.e("-------------SocketClient connectSocket");
                    mSocket = new Socket(HOST, PORT);
                    mInputStream = mSocket.getInputStream();
//            mBufferedReader = new BufferedReader(new mInputStreamReader(mIS));

                    mOS = mSocket.getOutputStream();
//            mPrintWriter = new PrintWriter(mOS);
//            mPrintWriter = new PrintWriter(new BufferedWriter(
//                    new OutputStreamWriter(mOS)),true);

                    if (null != mOnSocketClientListener) {
                        mOnSocketClientListener.onSocketConnSuccess();
                    }
                    readData();
                } catch (IOException e) {
                    e.printStackTrace();
                    AppLog.e("-------------SocketClient connectSocket fail");
                    if (null != mOnSocketClientListener) {
                        mOnSocketClientListener.onSocketConnFail();
                    }
                }
            }
        }.start();
    }

    public void setOnSocketClientListener(OnSocketClientListener onSocketClientListener) {
        this.mOnSocketClientListener = onSocketClientListener;
    }

    public void readData() {
        mIsWhile = true;
        new Thread(new RunnableReadData()).start();
    }

    public int sendData(String strData) {
        int i = -1;
        byte[] bytes = buildPackageData(strData);
        try {
            if (mOS != null) {
                mOS.write(bytes);
                mOS.flush();
                i = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return i;
    }

    public void closeThread() {
        mIsWhile = false;
    }

    private String mMsg;

    private class RunnableReadData implements Runnable {


        @Override
        public void run() {
            do {
                if (null != mSocket && mSocket.isConnected()) {
                    if (null != mInputStream) {
                        readJXData();

                    } else {
                        disConnect();
                    }
                } else {
                    disConnect();

                }

            } while (mIsWhile);
        }
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public void readJXData() {
        AppLog.e("-------------SocketClient readJXData");


        byte[] buffer = new byte[PACKAGE_HEAD_LEN];
        int len = PACKAGE_HEAD_LEN;
        String contentEnd = "";
        try {
            if (mInputStream.read(buffer, 0, len) != 0) {

                String content = new String(buffer);
                AppLog.e("--------------content0 = " + content);

                if (PACKAGE_HEAD.equals(content)) {
                    buffer = new byte[PACKAGE_LENGTH_LEN];
                    if (mInputStream.read(buffer, 0, PACKAGE_LENGTH_LEN) != 0) {
                        content = new String(buffer);
                        //									int validLen = (mOtherRecvData[0]&0xFF)<<8 | (mOtherRecvData[1]&0xFF);
//                        int i = (buffer[0] & 0xFF) << 8 | (buffer[1] & 0xFF);

                        int i = (buffer[0] & 0xFF) << 24 | (buffer[1] & 0xFF) << 16 | (buffer[2] & 0xFF) << 8 | (buffer[3] & 0xFF);
                        AppLog.e("--------content1 = " + content + "  i = " + i + "  buffer[0] = " + buffer[0] + "  buffer[1] = " + buffer[1]);

                        // TODO: 2017/7/12 防止 接收数据错误 造成OOM
                        if (i > 2 * 1024 * 1024) {
                            disConnect();
                            return;
                        }

                        buffer = new byte[i];
                        len = i;
                        if (mInputStream.read(buffer, 0, len) != 0) {
                            content = new String(buffer);

                            buffer = new byte[PACKAGE_END_LEN];
                            len = PACKAGE_END_LEN;
                            if (mInputStream.read(buffer, 0, len) != 0) {
                                contentEnd = new String(buffer);
                                AppLog.e("--------------contentEnd = " + contentEnd);
                            }
                            if (PACKAGE_END.equals(contentEnd)) {
                                if (null != mOnSocketClientListener) {
                                    mOnSocketClientListener.onSocketRecvData(content);
                                }

                            } else {

                            }
                        }
                    }
                } else {
                    String content123 = bytesToHexString(buffer);
                    AppLog.e("--------------content123 = " + content123);
                    if (SOCKET_DISCONNECT_DATA.equals(content123)) {
                        disConnect();
                        return;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disConnect() {
        AppLog.e("-------------SocketClient disConnect");
        mIsWhile = false;
        shutdownIO();
        closeIO(mOS);
        closeIO(mInputStream);
        closeSocket();
        if (null != mOnSocketClientListener) {
            mOnSocketClientListener.onSocketDisconnect();
        }
    }

    public void shutdownIO() {
        if (null != mSocket) {
            try {
                mSocket.shutdownOutput();
                mSocket.shutdownInput();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void closeSocket() {
        if (null != mSocket) {
            try {
                mSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public void sleep(Thread thread, long time) {
        try {
            thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void closeIO(Closeable closeable) {

        try {
            if (null != closeable) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 组建数据包，暂时不考虑分包的现象
     *
     * @param validData
     */
    public static byte[] buildPackageData(String validData) {

        // 有效数据长度
        int dataLen = validData.getBytes().length;
        int packetLen = PACKAGE_HEAD_LEN + PACKAGE_LENGTH_LEN + dataLen + PACKAGE_END_LEN;
        byte[] packet = new byte[packetLen];
        Arrays.fill(packet, (byte) 0);
        // 包头
        System.arraycopy(PACKAGE_HEAD.getBytes(), 0, packet, 0, PACKAGE_HEAD_LEN);
        // 数据长度(大端传输)
//        packet[2] = (byte) (dataLen >>> 8);
//        packet[3] = (byte) dataLen;
//        // 有效数据
//        System.arraycopy(validData.getBytes(), 0, packet, 4, dataLen);
//        // 包尾
//        System.arraycopy(PACKAGE_END.getBytes(), 0, packet, 4 + dataLen, PACKAGE_END_LEN);

        packet[2] = (byte) (dataLen >>> 24);
        packet[3] = (byte) (dataLen >>> 16);
        packet[4] = (byte) (dataLen >>> 8);
        packet[5] = (byte) dataLen;
        // 有效数据
        System.arraycopy(validData.getBytes(), 0, packet, 6, dataLen);
        // 包尾
        System.arraycopy(PACKAGE_END.getBytes(), 0, packet, 6 + dataLen, PACKAGE_END_LEN);
        return packet;
    }


    public interface OnSocketClientListener {

        void onSocketConnFail();

        void onSocketConnSuccess();

        /** 连接完成 */
//		public void onConnFinish(int sessionHandle);

        /**
         * 服务器返回有效数据
         * <strong>在子线程里，不可直接刷新UI</strong>
         *
         * @param strInfo 读取到的内容
         */
        void onSocketRecvData(String strInfo);

        /**
         * 播放
         */
        void onPlay(int whichCamera, int encrypt, byte[] pAVData, int nFrameSize, int frameCount);

        /**
         * 设备断开了，需要刷新状态
         */
        void onSocketDisconnect();

    }
}

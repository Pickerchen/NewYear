package com.sen5.smartlifebox.socket;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by ZHOUDAO on 2017/7/20.
 */

public class ZigBeeSocket {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8001;
    private InputStream mInputStream = null;
    private Socket mSocket;

    private InputStream inputStream;

    private OutputStream outputStream;

    private boolean mIsWhile;
    private OnSocketClientListener mOnSocketClientListener;


    /**
     * 数据包头长度
     */
    private static final int PACKAGE_HEAD_LEN = 2;
    /**
     * 数据长度的长度，为16个字节
     */
    private static final int PACKAGE_LENGTH_LEN = 16;
    /**
     * 数据包尾长度
     */
    private static final int PACKAGE_END_LEN = 2;

    /**
     * 包头
     */
    private static final int PACKAGE_HEAD = 0xBB;
    /**
     * 包尾
     */
    private static final String PACKAGE_END = "!!";


    public ZigBeeSocket() {

    }

    public void connectSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    mSocket = new Socket(HOST, PORT);

                    inputStream = mSocket.getInputStream();

                    outputStream = mSocket.getOutputStream();


                    if (mOnSocketClientListener != null) {
                        isConnect = true;
                        mOnSocketClientListener.onSocketConnSuccess();
                    }


                    readData();

                } catch (IOException e) {
                    e.printStackTrace();

                    if (mOnSocketClientListener != null) {
                        mOnSocketClientListener.onSocketConnFail();
                    }
                }


            }
        }).start();
    }

    private void readData() {
        mIsWhile = true;
        do {
            if (null != mSocket && mSocket.isConnected()) {
                if (null != inputStream) {


                    byte[] buffer = new byte[1];


                    try {
                        if (inputStream.read(buffer, 0, 1) != 0) {

                            if (0xBA == (buffer[0] & 0xFF)) {

                                if (listener != null) {
                                    listener.getData("");
                                }

                            }


                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (mOnSocketClientListener != null) {
                            mOnSocketClientListener.onSocketConnFail();
                        }
                    }


                } else {
                    disConnect();
                }
            } else {
                disConnect();

            }

        } while (mIsWhile);
    }


    public void disConnect() {
        mIsWhile = false;
        shutdownIO();
        closeSocket();
        closeIO(inputStream);
        closeIO(outputStream);
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


    public void closeIO(Closeable closeable) {

        try {
            if (null != closeable) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setOnSocketClientListener(OnSocketClientListener onSocketClientListener) {
        this.mOnSocketClientListener = onSocketClientListener;
    }


    public void sendData(String data) {
        try {
            if (outputStream != null) {
                outputStream.write(intToBytes2(data));
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void Restore() {
        try {
            if (outputStream != null) {
                outputStream.write(0xBC);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public byte[] intToBytes2(String hexString) {


        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length+2];

        d[0] = (byte) 0xBB;
        d[1] = (byte) length;

        for (int i = 2; i < length+2; i++) {
            int pos = (i-2) * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }

        return d;
    }


    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


    public interface DataListener {
        void getData(String str);
    }

    public void setListener(DataListener listener) {
        this.listener = listener;
    }

    private DataListener listener;

    private boolean isConnect;

    private boolean isConnect() {
        return isConnect;
    }

}

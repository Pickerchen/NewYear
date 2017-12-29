package com.sen5.smartlifebox.socket;

/**
 * Created by jiangyicheng on 2017/3/7.
 */

public class SocketModel {
    private static SocketModel mSocketModel;
    public SocketClient mSocketClient;

    private SocketModel() {
        mSocketClient = new SocketClient();
        mSocketClient.setOnSocketClientListener(new MyOnSocketClientListener());
    }

    public static SocketModel getInstance() {
        if (null == mSocketModel) {
            synchronized (SocketModel.class) {
                if (null == mSocketModel) {
                    mSocketModel = new SocketModel();
                    return mSocketModel;
                }
            }

        }
        return mSocketModel;
    }

    private class MyOnSocketClientListener implements OnSocketClientListener{

        @Override
        public void onSocketConnFail() {

        }

        @Override
        public void onSocketConnSuccess() {

        }

        @Override
        public void onSocketRecvData(String strInfo) {

        }

        @Override
        public void onPlay(int whichCamera, int encrypt, byte[] pAVData, int nFrameSize, int frameCount) {

        }

        @Override
        public void onSocketDisconnect() {

        }
    }

}

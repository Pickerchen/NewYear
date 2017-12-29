package com.sen5.smartlifebox.socket;

/**
 * Created by ZHOUDAO on 2017/7/20.
 */

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
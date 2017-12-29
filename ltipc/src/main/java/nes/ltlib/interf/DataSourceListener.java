package nes.ltlib.interf;

import android.graphics.Matrix;
import android.util.Log;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import glnk.media.AViewRenderer;
import glnk.media.GlnkDataSourceListener;
import glnk.media.VideoRenderer;
import nes.ltlib.event.DataSourceListenerEvent;

/**
 * Created by chenqianghua on 2017/9/7.
 */

public class DataSourceListener implements VideoRenderer.OnVideoSizeChangedListener , GlnkDataSourceListener {

    private String gid;

    public DataSourceListener(String did) {
        this.gid = did;
        EventBus.getDefault().register(this);
    }

    //------------------------------------ltSDK回调-------------------------------------//

    @Override
    public void onTalkingResp(int i) {

    }

    @Override
    public void onIOCtrl(int i, byte[] bytes) {

    }

    @Override
    public void onIOCtrlByManu(byte[] bytes) {

    }

    @Override
    public void onRemoteFileResp(int i, int i1, int i2) {

    }

    @Override
    public void onRemoteFileEOF() {

    }

    @Override
    public void onConnecting() {
       Log.e("onConnecting"," onConnecting is coming");
    }

    @Override
    public void onConnected(int i, String s, int i1) {
       Log.e("dealwithGidStaus"," i is "+i+" s is "+s+" i1 is "+i1);
        DataSourceListenerEvent mDataSourceListenerEvent = new DataSourceListenerEvent(gid,0);
        EventBus.getDefault().post(mDataSourceListenerEvent);
    }

    /**
     * 可以进行录制的标志， 认证通过之后才可以进行录制
     * @param i
     */
    @Override
    public void onAuthorized(int i) {
       Log.e("onAuthorized","onAuthorized is  coming i is "+i);
    }

    @Override
    public void onPermision(int i) {

    }

    @Override
    public void onModeChanged(int i, String s, int i1) {
       Log.e("onModeChanged"," i is "+i+" s is "+s);
    }

    @Override
    public void onDisconnected(int i) {
       Log.e("dealwithGidStaus"," i is "+i+" did is "+gid);
        DataSourceListenerEvent mDataSourceListenerEvent = new DataSourceListenerEvent(gid,1);
        EventBus.getDefault().post(mDataSourceListenerEvent);
    }

    @Override
    public void onDataRate(int i) {
//       Log.e("onDataRate"," i is "+i);

    }

    @Override
    public void onReConnecting() {

    }

    @Override
    public void onEndOfFileCtrl(int i) {

    }

    @Override
    public void onLocalFileOpenResp(int i, int i1) {

    }

    @Override
    public void onLocalFilePlayingStamp(int i) {

    }

    @Override
    public void onLocalFileEOF() {

    }

    @Override
    public void onOpenVideoProcess(int i) {

    }

    @Override
    public void onVideoFrameRate(int i) {

    }

    @Override
    public void onAppVideoFrameRate(int i) {

    }

    /**
     * 改变视频显示的比例（浪涛sdk存在两个比例：1280*720、640*480）
     * @param videoRenderer
     * @param i
     * @param i1
     */
    @Override
    public void onVideoSizeChanged(VideoRenderer videoRenderer, int i, int i1) {
       Log.e("onVideoSizeChanged","i is "+i+" i1 is "+i1);
        //副码流
        if (i == 640){
            AViewRenderer mAViewRenderer = (AViewRenderer) videoRenderer;
            Matrix mMatrix = mAViewRenderer.getMatrix();
            mMatrix.reset();
            mMatrix.setScale((float)3,(float)3,0,0);
        }
        else if (i== 1280){
            AViewRenderer mAViewRenderer = (AViewRenderer) videoRenderer;
            Matrix mMatrix = mAViewRenderer.getMatrix();
            mMatrix.reset();
            mMatrix.setScale((float)(1.5),(float)(1.7777),0,0);
        }
        else if (i == 1920){
            //盒子分辨率为1920*1080,如果是全屏播放则不需要进行处理
            //四分屏时做处理
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealWithEvent(Object object){
       Log.e("dealWithEvent","dealwithEvent is coming");
    }
}

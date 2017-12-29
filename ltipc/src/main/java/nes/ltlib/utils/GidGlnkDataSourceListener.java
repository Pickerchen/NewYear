package nes.ltlib.utils;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import glnk.media.GlnkDataSourceListener;
import nes.ltlib.event.DataSourceListenerEvent;
import nes.ltlib.event.LTCameraEvent;

/**
 * Created by ZHOUDAO on 2017/9/8.
 */

public class GidGlnkDataSourceListener implements GlnkDataSourceListener {

    public String gid;


    public GidGlnkDataSourceListener(String gid) {
        this.gid = gid;
       EventBus.getDefault().register(this);
    }

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
        Log.i("LTSDK", "onConnecting ----  gid:" + gid);
        EventBus.getDefault().post(new LTCameraEvent(LTCameraEvent.connecting, gid));
    }

    @Override
    public void onConnected(int i, String s, int i1) {
        EventBus.getDefault().post(new LTCameraEvent(LTCameraEvent.connected, gid));
        DataSourceListenerEvent mDataSourceListenerEvent = new DataSourceListenerEvent(gid,0);
        EventBus.getDefault().post(mDataSourceListenerEvent);

        Log.i("LTSDK", "onConnected i" + i + "s:" + s + " i1:" + i1 + " gid:" + gid);
    }

    @Override
    public void onAuthorized(int i) {

    }

    @Override
    public void onPermision(int i) {

    }

    @Override
    public void onModeChanged(int i, String s, int i1) {

    }

    @Override
    public void onDisconnected(int i) {
        Log.i("LTSDK", "onDisconnected   " + i + "  gid:" + gid);

        LTCameraEvent event = new LTCameraEvent(LTCameraEvent.disconnect, gid);
        event.setError(i);

        EventBus.getDefault().post(event);

        DataSourceListenerEvent mDataSourceListenerEvent = new DataSourceListenerEvent(gid,1);
        EventBus.getDefault().post(mDataSourceListenerEvent);
    }

    @Override
    public void onDataRate(int i) {

    }

    @Override
    public void onReConnecting() {
        EventBus.getDefault().post(new LTCameraEvent(LTCameraEvent.reconnecting, gid));

        Log.i("LTSDK", "onReConnecting ----  gid:" + gid);
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

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Object event) {

    }
}

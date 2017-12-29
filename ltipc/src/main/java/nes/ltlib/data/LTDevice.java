package nes.ltlib.data;

import glnk.media.AView;
import glnk.media.GlnkDataSource;
import glnk.media.GlnkPlayer;
import glnk.media.VideoRenderer;

/**
 * Created by chenqianghua on 2017/8/28.
 */

public class LTDevice {
    private String gid;
    private AView mAView;
    private GlnkPlayer mGlnkPlayer;
    private GlnkDataSource mGlnkDataSource;
    private VideoRenderer mVideoRenderer;

    public VideoRenderer getVideoRenderer() {
        return mVideoRenderer;
    }

    public void setVideoRenderer(VideoRenderer videoRenderer) {
        mVideoRenderer = videoRenderer;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public GlnkDataSource getGlnkDataSource() {
        return mGlnkDataSource;
    }

    public void setGlnkDataSource(GlnkDataSource glnkDataSource) {
        mGlnkDataSource = glnkDataSource;
    }

    public LTDevice(GlnkPlayer glnkPlayer, AView AView) {
        mGlnkPlayer = glnkPlayer;
        mAView = AView;
    }


    public LTDevice() {
    }

    public AView getAView() {
        return mAView;
    }

    public void setAView(AView AView) {
        mAView = AView;
    }

    public GlnkPlayer getGlnkPlayer() {
        return mGlnkPlayer;
    }

    public void setGlnkPlayer(GlnkPlayer glnkPlayer) {
        mGlnkPlayer = glnkPlayer;
    }

    public void setPlayNull(){
        if (null != mGlnkPlayer){
            mGlnkPlayer = null;
        }
    }

    public void setAViewNull(){
        if (null != mAView){
            mAView = null;
        }
    }

}

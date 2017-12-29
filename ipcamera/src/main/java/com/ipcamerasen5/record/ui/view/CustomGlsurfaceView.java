package com.ipcamerasen5.record.ui.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by chenqianghua on 2017/4/6.
 * 用来后台隐藏播放视频的控件
 */

public class CustomGlsurfaceView extends GLSurfaceView{
    private long userId;
    private String did;
    public CustomGlsurfaceView(Context context) {
        super(context);
    }

    public CustomGlsurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public void setPause(){
        this.onPause();
    }
    public void setResume(){
        this.onResume();
    }
}

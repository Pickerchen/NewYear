package com.ipcamerasen5.record.ui.view;

/**
 * RecordActivityUI逻辑
 * Created by chenqianghua on 2017/4/7.
 */

public interface IRecordActivity {
    void addGl();//放置glsurfaceView
    void setSwitchListener();//设置switchView的监听
    void setLayoutSelected(boolean flag);//设置布局选中状态,改变ll_switch的颜色
    void initRecyclerView();//初始化recyclerView
    void setSwitchStatus(int status);//设置switch开关的状态
    void removeAview();//移除LT摄像头View
}

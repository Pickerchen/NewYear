package com.sen5.secure.launcher.utils;


import com.open.androidtvwidget.bridge.RecyclerViewBridge;

/**
 * Created by jiangyicheng on 2016/12/21.
 */

public class UtilsRecyclerViewBridge {
    private RecyclerViewBridge mRecyclerViewBridge;
    public static boolean isHideMoveUI = true;
    public UtilsRecyclerViewBridge(RecyclerViewBridge recyclerViewBridge, boolean isShow){
        mRecyclerViewBridge = recyclerViewBridge;
        mRecyclerViewBridge.setVisibleWidget(isShow);
    }

    public void setRoundView(){

    }
}

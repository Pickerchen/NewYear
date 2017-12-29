package com.nes.ipc;

import android.app.Application;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import glnk.client.GlnkClient;
import glnk.client.LanSearcher;
import glnk.client.OnLanSearchListener;
import glnk.io.GlnkService;
import glnk.io.OnDeviceStatusChangedListener;
import glnk.rt.MyRuntime;

/**
 * Created by ZHOUDAO on 2017/8/16.
 */

public class CameraManager implements  OnLanSearchListener {


    private static CameraManager manager;

    private EventBus bus;

    private CameraSearchListener searchListener;
    private LanSearcher lanSearcher;

    private List<CameraEntity> camearList = new ArrayList<>();

    public static CameraManager getInstance() {
        synchronized (CameraManager.class) {
            if (manager == null) {
                manager = new CameraManager();
            }
        }

        return manager;
    }


    public void initSDK(Application context) {
        if (!MyRuntime.supported()) {
            Toast.makeText(context, "暂不支持的手机", Toast.LENGTH_SHORT).show();
            return;
        }


        GlnkClient gClient = GlnkClient.getInstance();
        gClient.init(context, "langtao", "20141101", "1234567890", 1, 1);    //初始化，这些参数写死即可
        gClient.setStatusAutoUpdate(true); //设备状态自动更新，填写true即可
        gClient.setAppKey("2z7zdId4U8oDWMY5+679qbjSvoJGGrPRv9g4"); //这个	key由浪涛研发分配，唯一，用于验证sdk合法性
        gClient.start();


    }

    public void destorySDK() {
        GlnkClient.getInstance().release();
    }


    public void searchCamera(CameraSearchListener listener) {
        searchListener = listener;
        lanSearcher = new LanSearcher(GlnkClient.getInstance(), this);
        lanSearcher.start();
    }

    public void stopSearch() {
        // 关闭退出搜索
        lanSearcher.release();
        lanSearcher = null;
    }


    public void setOnDeviceStatusChangedListener(OnDeviceStatusChangedListener l) {
        GlnkService.getInstance().setOnDeviceStatusChangedListener(l);
    }


    @Override
    public void onSearched(String gid, String name) {

        CameraEntity entity = new CameraEntity();
        entity.setDeviceID(gid);
        entity.setDeviceName(name);

        camearList.add(entity);
    }

    @Override
    public void onSearched2(String s, String s1) {

    }

    @Override
    public void onSearchFinish() {
        searchListener.onSearchFinish(camearList);
    }


}

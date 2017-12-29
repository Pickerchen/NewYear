/*
 * Copyright (c) belongs to sen5.
 */

package com.sen5.smartlifebox.ui.camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ipcamera.main.control.CameraSourceControl;
import com.sen5.smartlifebox.SecQreSettingApplication;
import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseFragment;
import com.sen5.smartlifebox.common.utils.Utils;
import nes.ltlib.utils.AppLog;
import com.sen5.smartlifebox.common.wrapper.FastJsonTools;
import com.sen5.smartlifebox.data.event.CameraEvent;
import com.sen5.smartlifebox.data.event.SwitchFragmentEvent;
import com.sen5.smartlifebox.widget.SenAlertDialog;
import com.sen5.smartlifebox.widget.WheelView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


import hsl.p2pipcam.nativecaller.DeviceSDK;
import nes.ltlib.data.CameraEntity;
import nes.ltlib.interf.CameraSearchListener;
import nes.ltlib.LTSDKManager;

/**
 * Created by wanglin on 2016/10/25.
 */
public class FindLANCamerasFragment extends BaseFragment implements CameraSearchListener {

    WheelView wheelView;

    private static final int SEARCH_AGAIN = 1;
    private static final int SEARCH_LTIPC = 3;

    private static final int SEARCH_COMPLETE = 2;
    private static final int GET_CAMERANAME = 4;

    private View contentView;
    private MyHandler mHandler;
    private List<CameraEntity> mCameras = new ArrayList<>();
    private int flag;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_main, container, false);
            initView();
            EventBus.getDefault().register(this);
            ctrlRespond();
            initData();
        }
        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        wheelView.requestFocus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        LTSDKManager.getInstance().disposableAll();
        stopSearchLtIPC();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        DeviceSDK.unInitSearchDevice();
        EventBus.getDefault().unregister(this);
    }

    public FindLANCamerasFragment() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CameraEvent evnet) {
        switch (evnet.getFlag()) {
            case CameraEvent.ADD_CAMERA:
                int size = mCameras.size();
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        if (SecQreSettingApplication.getCameras().contains(mCameras.get(i))) {
                            //刷新布局
                            View view = wheelView.getItem(i + 1);
                            ImageView ivCamera = (ImageView) view.findViewById(R.id.iv_camera);
                            ivCamera.setImageResource(R.drawable.ic_camera_selected);
                        }
                    }
                }
                break;
        }
    }

    private void ctrlRespond() {
        wheelView.setmOnItemClickListener(new WheelView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position > 0) {
                    SwitchFragmentEvent event = new SwitchFragmentEvent(SwitchFragmentEvent.ADD_CANCEL_FRAGMENT);
                    event.setCamera(mCameras.get(position - 1));
                    EventBus.getDefault().post(event);
                }
            }
        });

        //初始化LTIpc搜索监听
        LTSDKManager.getInstance().setSearchListener(this);

    }

    private void initView() {
        wheelView = (WheelView) contentView.findViewById(R.id.wheel_view);

    }


    private void initData() {
        mHandler = new MyHandler(this);
        //往WheelView中添加条目
        View titleView = getActivity().getLayoutInflater().inflate(R.layout.view_title_add_camera, null, false);
        titleView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dip2px(getResources(), 60)));
        TextView tvTitle1 = (TextView) titleView.findViewById(R.id.tv_title);
        tvTitle1.setText(getString(R.string.available_cameras));
        wheelView.addItem(titleView);

        // TODO: 2017/11/20 调试CPU正式版需要取消注释
        if (CameraSourceControl.getInstance().getCameraSource().isEmpty()) {
            searchDevice();
        } else {
            if (CameraSourceControl.getInstance().getCameraType() == 1) {
                searchDevice();
            } else {
                searchLTIpc();
            }
        }
        // TODO: 2017/11/20 调试CPU正式版需要注释
//        searchLTIpc();
        showProgressDialog(getString(R.string.scan_lan_cameras));
    }

    private void searchDevice() {
        //        DeviceSDK.initialize(serviceContent);
        DeviceSDK.initSearchDevice();//初始化搜索sdk
        DeviceSDK.setSearchCallback(FindLANCamerasFragment.this);
        DeviceSDK.searchDevice();
        //防止一次扫描不能将局域网设备扫描出来
        mHandler.sendEmptyMessageDelayed(SEARCH_AGAIN, 2000);
    }


    /**
     * 搜索LTIpc
     */
    private void searchLTIpc() {
        stopSearchLtIPC();

        LTSDKManager.getInstance().searchCamera(1);
        AppLog.i("搜索LTIpc");
    }


    private void stopSearchLtIPC() {
        LTSDKManager.getInstance().stopSearch();
    }


    //搜索到设备后jni的回调。
    public void CallBack_SearchDevice(String DeviceInfo) {
        AppLog.e(DeviceInfo);
        CameraEntity camera = FastJsonTools.getItem(DeviceInfo, CameraEntity.class);
        //去掉did中间的“-”
        camera.setDeviceID(camera.getDeviceID().replaceAll("-", ""));
        if (!mCameras.contains(camera)) {
            mCameras.add(camera);
        }
    }


    @Override
    public void onSearchFinish(ArrayList<CameraEntity> list) {


        if (!this.isVisible()) {
            return;
        }

        for (CameraEntity entity : list) {
            if (!mCameras.contains(entity)) {
                mCameras.add(entity);
            }
        }

        mHandler.sendEmptyMessage(SEARCH_COMPLETE);
    }

    private class MyHandler extends Handler {

        private FindLANCamerasFragment mFragment;

        public MyHandler(FindLANCamerasFragment fragment) {
            WeakReference<FindLANCamerasFragment> reference = new WeakReference<>(fragment);
            mFragment = reference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SEARCH_AGAIN:
                    AppLog.d("扫描第二次");
                    DeviceSDK.searchDevice();

                    sendEmptyMessageDelayed(SEARCH_LTIPC, 5000);


                    break;
                case SEARCH_LTIPC:

                    if (!mFragment.isVisible()) {
                        return;
                    }

                    if (mCameras.size() > 0 || CameraSourceControl.getInstance().getCameraType() == 1) {
                        sendEmptyMessage(SEARCH_COMPLETE);
                    } else {
                        searchLTIpc();
                    }


                    break;

                case SEARCH_COMPLETE:
                    if (!mFragment.isVisible()) {
                        return;
                    }

                    stopSearchLtIPC();


                    mFragment.dismissProgressDialog();
                    if (mFragment.mCameras.size() == 0) {
                        AlertDialog dialog = new SenAlertDialog.Builder(mFragment.getActivity())
                                .setMessage(mFragment.getString(R.string.no_camera_hint))
                                .setPositiveButton(mFragment.getString(R.string.confirm),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).create();
                        dialog.show();
                        setTextSize(dialog);
//                        if(dialog instanceof SenAlertDialog){
//
//                            ((SenAlertDialog)dialog).setTextSize();
//                        }
                    } else {
                        for (CameraEntity camera : mFragment.mCameras) {
                            View view = mFragment.getActivity().getLayoutInflater()
                                    .inflate(R.layout.item_camera, mFragment.wheelView, false);

                            ImageView ivCamera = (ImageView) view.findViewById(R.id.iv_camera);
                            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
                            tvName.setText(camera.getDeviceName());
                            if (SecQreSettingApplication.getCameras().contains(camera)) {
                                ivCamera.setImageResource(R.drawable.ic_camera_selected);
                            }
                            mFragment.wheelView.addItem(view);
                        }
                    }

                    break;
                case GET_CAMERANAME:

                    break;
            }
        }

        public void setTextSize(AlertDialog dialog) {
            setDialogText(dialog.getWindow().getDecorView());
        }

        public void setDialogText(View v) {
            if (v instanceof ViewGroup) {
                ViewGroup parent = (ViewGroup) v;
                int count = parent.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = parent.getChildAt(i);
                    setDialogText(child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTextSize(v.getResources().getDimension(R.dimen.text_size_normal));
            }
        }

    }


}

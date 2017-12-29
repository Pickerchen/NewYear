/*
 * Copyright (c) belongs to sen5.
 */

package com.sen5.smartlifebox.ui.camera;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.SecQreSettingApplication;
import com.sen5.smartlifebox.base.BaseFragment;
import com.sen5.smartlifebox.common.utils.TestToast;
import com.sen5.smartlifebox.data.event.SwitchFragmentEvent;
import com.sen5.smartlifebox.widget.WheelView;

import org.greenrobot.eventbus.EventBus;

import nes.ltlib.data.CameraEntity;


public class EditCameraFragment extends BaseFragment {
    private View contentView;

    WheelView wheelView;
    private CameraEntity mCamera;

    public EditCameraFragment() {
        // Required empty public constructor
    }

    public static EditCameraFragment newInstance(CameraEntity camera) {
        EditCameraFragment fragment = new EditCameraFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("camera", camera);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCamera = getArguments().getParcelable("camera");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_main, container, false);
            initView();
            initData();
        }
        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        wheelView.requestFocus();
    }


    private void initView() {
        wheelView = (WheelView) contentView.findViewById(R.id.wheel_view);

    }


    private void initData() {
        wheelView.addItem(getString(R.string.set_wifi));
        wheelView.addItem(mContext.getString(R.string.rename));
        wheelView.addItem(mContext.getString(R.string.delete));

        wheelView.setmOnItemClickListener(new WheelView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                TestToast.showShort(mContext, "点击：" + position);
                if (position == 0) {


                    SecQreSettingApplication.deviceId = mCamera.getDeviceID();

                    EventBus.getDefault().post(new SwitchFragmentEvent(
                            SwitchFragmentEvent.ADD_WIFI_CAMERA_FRAGMENT));


                } else if (position == 1) {
                    Intent intent = new Intent(mContext, EditCameraNameActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("camera", mCamera);
                    intent.putExtras(bundle);
                    startActivityAnim(intent);

                } else if (position == 2) {
                    SwitchFragmentEvent event = new SwitchFragmentEvent(SwitchFragmentEvent.DELETE_CAMERA_FRAGMENT);
                    event.setCamera(mCamera);
                    EventBus.getDefault().post(event);
                }
            }
        });

    }


}

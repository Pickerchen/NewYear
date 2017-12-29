/*
 * Copyright (c) belongs to sen5.
 */

package com.sen5.smartlifebox.ui.camera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sen5.smartlifebox.SecQreSettingApplication;
import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseFragment;
import com.sen5.smartlifebox.common.Constant;
import com.sen5.smartlifebox.common.utils.TestToast;
import com.sen5.smartlifebox.data.event.CameraEvent;
import com.sen5.smartlifebox.data.event.SwitchFragmentEvent;
import com.sen5.smartlifebox.widget.WheelView;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import nes.ltlib.data.CameraEntity;


/**
 * Created by wanglin on 2016/10/25.
 */
public class AddCancelFragment extends BaseFragment {

    WheelView wheelView;
    private View contentView;

    private CameraEntity mCamera;

    public AddCancelFragment() {
    }

    public static AddCancelFragment newInstance(CameraEntity camera) {

        Bundle args = new Bundle();
        args.putParcelable("camera", camera);
        AddCancelFragment fragment = new AddCancelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCamera = getArguments().getParcelable("camera");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        wheelView.addItem(mContext.getString(R.string.add));
        wheelView.addItem(mContext.getString(R.string.cancel));

        wheelView.setmOnItemClickListener(new WheelView.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {
                TestToast.showShort(mContext, "点击：" + position);
                if (position == 0) {
                    if (TextUtils.isEmpty(mCamera.getDeviceID())) {//CameraId为空不能添加
                        Toast.makeText(mContext, mContext.getString(R.string.camera_invalid),
                                Toast.LENGTH_SHORT).show();
                        return;
                    } else if (SecQreSettingApplication.getCameras().contains(mCamera)) {//防止重复添加
                        Toast.makeText(mContext, mContext.getString(R.string.camera_already_add),
                                Toast.LENGTH_SHORT).show();
                        return;
                    } else if (SecQreSettingApplication.getCameras().size() >= 4) {//最多只能添加4个Camera
                        Toast.makeText(mContext, mContext.getString(R.string.add_camera_upper_limit),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(mCamera.getDeviceName())) {
                        mCamera.setDeviceName(mContext.getString(R.string.camera_unnamed));
                    }

                    //添加Camera
                    writeCameraIni(mCamera);
                    SecQreSettingApplication.getCameras().add(mCamera);

                    CameraEvent event = new CameraEvent(CameraEvent.ADD_CAMERA);
                    event.setCameraDID(mCamera.getDeviceID());
                    EventBus.getDefault().post(event);


                    getActivity().getSupportFragmentManager().popBackStackImmediate(
                            SwitchFragmentEvent.FIND_LAN_CAMERA_FRAGMENT, 0);
                } else if (position == 1) {
                    //回到EditMemberFragment
                    getActivity().getSupportFragmentManager().popBackStackImmediate(
                            SwitchFragmentEvent.FIND_LAN_CAMERA_FRAGMENT, 0);
                }
            }
        });
    }

    /**
     * 向camera.ini文件中写入camera内容
     *
     * @param camera
     */
    private void writeCameraIni(CameraEntity camera) {
        File file = new File(Constant.CAMERA_INI);
        BufferedWriter bw = null;
        StringBuffer sb = new StringBuffer();

        try {
            bw = new BufferedWriter(new FileWriter(file, true));//true表示不覆盖原来内容
            sb.append("##");
            sb.append(camera.getDeviceID());
            sb.append("#");
            sb.append(camera.getDeviceName());
            sb.append("##");
            sb.append("\n");
            bw.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

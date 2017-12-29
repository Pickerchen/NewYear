/*
 * Copyright (c) belongs to sen5.
 */

package com.sen5.smartlifebox.ui.camera;


import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseFragment;
import com.sen5.smartlifebox.common.utils.TestToast;
import com.sen5.smartlifebox.data.event.CameraEvent;
import com.sen5.smartlifebox.data.event.SwitchFragmentEvent;
import com.sen5.smartlifebox.data.p2p.P2PModel;
import com.sen5.smartlifebox.widget.WheelView;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import nes.ltlib.data.CameraEntity;
import nes.ltlib.utils.AppLog;


public class DeleteCameraFragment extends BaseFragment {
    private View contentView;

    WheelView wheelView;


    P2PModel mP2PModel;
    private CameraEntity mCamera;

    public DeleteCameraFragment() {
        // Required empty public constructor
    }

    public static DeleteCameraFragment newInstance(CameraEntity camera){
        DeleteCameraFragment fragment = new DeleteCameraFragment();
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

    private void initView(){
        wheelView = (WheelView) contentView.findViewById(R.id.wheel_view);

    }

    private void initData() {
        mP2PModel = P2PModel.getInstance(getActivity());

        wheelView.addItem(mContext.getString(R.string.yes));
        wheelView.addItem(mContext.getString(R.string.no));

        wheelView.setmOnItemClickListener(new WheelView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                TestToast.showShort(mContext, "点击：" + position);
                if (position == 0) {
                    //删除Camera
                    deleteCamera();
                    CameraEvent event = new CameraEvent(CameraEvent.DELETE_CAMERA);
                    event.setCameraDID(mCamera.getDeviceID());
                    EventBus.getDefault().post(event);

                    getActivity().getSupportFragmentManager().popBackStackImmediate(
                            SwitchFragmentEvent.EDIT_CAMERA_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                } else if (position == 1) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate(
                            SwitchFragmentEvent.DELETE_CAMERA_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        wheelView.requestFocus();
    }

    private void deleteCamera() {
        //读取文件行数，一行代表一个camera，每行格式为：##DID#NAME##
        File file = new File("/data/smarthome/camera.ini");
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String content = line.substring(2, line.length() - 2);
                String[] strs = content.split("#");
                if (!strs[0].equals(mCamera.getDeviceID())) {
                    sb.append(line).append("\n");
                }

            }
        } catch (IOException e){
            e.printStackTrace();
        }finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(sb.toString());
            AppLog.e("Camera.ini:删除摄像头:"+sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
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

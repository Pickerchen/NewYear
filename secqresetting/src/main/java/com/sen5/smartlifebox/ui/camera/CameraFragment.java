package com.sen5.smartlifebox.ui.camera;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sen5.smartlifebox.SecQreSettingApplication;
import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseFragment;
import com.sen5.smartlifebox.common.Constant;
import com.sen5.smartlifebox.common.utils.TestToast;
import com.sen5.smartlifebox.common.utils.Utils;

import nes.ltlib.utils.AppLog;

import com.sen5.smartlifebox.data.event.CameraEvent;
import com.sen5.smartlifebox.data.event.SwitchFragmentEvent;
import com.sen5.smartlifebox.widget.WheelView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nes.ltlib.data.CameraEntity;

import static com.sen5.smartlifebox.SecQreSettingApplication.isSupportWIFI;


public class CameraFragment extends BaseFragment {

    public static final int INDEX_ADD_WIFI_CAMERA = 1;
    public static final int INDEX_ADD_LAN_CAMERAS = 2;
    private static final String serviceContent = "ADHOAFAJPFMPCNNCBIHOBAFHDMNFGJJCHDAGFHCFEAIHOLKFDHADCL" +
            "PBCJLLMMKBEIJCLDHGPJMLEMCDMGMMNOEIIGLHENDLEDCIHNBOMKKFFMCBBH";


    WheelView wheelView;

    private View contentView;
    private WifiManager mWifiManager;

    private CameraEntity mCurEditCamera;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        TestToast.showShort(mContext, "CameraFragment onAttach");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TestToast.showShort(mContext, "CameraFragment onCreateView");
        if (contentView == null) {
            //这里的inflate跟Activity中的inflate不同
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

    }

    @Override
    public void onDetach() {
        super.onDetach();
        SecQreSettingApplication.getCameras().clear();
        EventBus.getDefault().unregister(this);
    }

    public CameraFragment() {
        // Required empty public constructor
    }

    private void ctrlRespond() {
        wheelView.setmOnItemClickListener(new WheelView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                TestToast.showShort(mContext, "点击：" + position);
                AppLog.e("position " + position);


                if (isSupportWIFI)
                    if (position == INDEX_ADD_WIFI_CAMERA) {
                        EventBus.getDefault().post(new SwitchFragmentEvent(
                                SwitchFragmentEvent.ADD_WIFI_CAMERA_FRAGMENT));
                    }

                if (position == (isSupportWIFI ? INDEX_ADD_LAN_CAMERAS : 1)) {
                    EventBus.getDefault().post(new SwitchFragmentEvent(
                            SwitchFragmentEvent.FIND_LAN_CAMERA_FRAGMENT));
                } else if (position > (isSupportWIFI ? 3 : 2)) {
                    SwitchFragmentEvent event = new SwitchFragmentEvent(SwitchFragmentEvent.EDIT_CAMERA_FRAGMENT);
                    if (SecQreSettingApplication.getCameras().size() > 0) {
                        mCurEditCamera = SecQreSettingApplication.getCameras().get(position - (isSupportWIFI ? 4 : 3));
                        event.setCamera(mCurEditCamera);
                        EventBus.getDefault().post(event);
                    }
                }

            }
        });
    }


    private void initView() {
        wheelView = (WheelView) contentView.findViewById(R.id.wheel_view);

    }


    private void initData() {
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        wheelView.addItem(createTitleView(getString(R.string.add_new_ip_camera)));

        List<String> items = new ArrayList<>();
        if (isSupportWIFI)
            items.add(getString(R.string.set_wifi_camera));
        items.add(getString(R.string.find_lan_cameras));
        wheelView.addItem(items);
        wheelView.addItem(createTitleView(getString(R.string.added_ip_cameras)));

        //读取已添加的Camera
        readCameraIni();


    }

    private View createTitleView(String title) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.view_title_add_camera, null, false);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dip2px(getResources(), 60)));
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvTitle.setText(title);

        return view;
    }

    private void readCameraIni() {
        SecQreSettingApplication.getCameras().clear();

        File file = new File(Constant.CAMERA_INI);
        if (!file.exists()) {
            AppLog.e("camera.ini 文件不存在");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!file.canWrite()) {
            AppLog.e("不能写camera.ini文件");
        }

        if (file.exists()) {
            //读取文件行数，一行代表一个camera，每行格式为：##DID#NAME#IP##
            FileReader fr = null;
            BufferedReader br = null;
            try {
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                String line;
                while ((line = br.readLine()) != null) {
                    String content = line.substring(2, line.length() - 2);
                    String[] strs = content.split("#");
                    CameraEntity camera = new CameraEntity();
                    SecQreSettingApplication.getCameras().add(camera);
                    camera.setDeviceID(strs[0]);
                    camera.setDeviceName(strs[1]);
                    if (strs.length > 2) {
                        camera.setIP(strs[2]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {
                AppLog.e("Camera.ini文件格式异常");
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                    if (fr != null) {
                        fr.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        wheelView.removeItems((isSupportWIFI ? 4 : 3), wheelView.getItemCount() - (isSupportWIFI ? 4 : 3));
        for (CameraEntity camera : SecQreSettingApplication.getCameras()) {
            View view = getActivity().getLayoutInflater()
                    .inflate(R.layout.item_camera, wheelView, false);

            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            tvName.setText(camera.getDeviceName());
            wheelView.addItem(view);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CameraEvent event) {
        switch (event.getFlag()) {
            case CameraEvent.ADD_CAMERA:
                //刷新Camera
                readCameraIni();
                AppLog.i("刷新Camera列表");

                //通知底层Camera信息发生改变
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //所有的 Camera编辑事件都要等这个接口返回200的时候 才能进行真正的上层数据操作
                        notifyCameraChange();
                    }
                }).start();
                //通知launcher
                Intent intent = new Intent("com.sen5.smartlife.camerachange");
                intent.putExtra("CameraDID", event.getCameraDID());
                intent.putExtra("type", 0);
                mContext.sendBroadcast(intent);
                break;

            case CameraEvent.DELETE_CAMERA:

                //刷新Camera
                readCameraIni();
                AppLog.i("刷新Camera列表");

                //通知底层Camera信息发生改变
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //所有的 Camera编辑事件都要等这个接口返回200的时候 才能进行真正的上层数据操作
                        notifyCameraChange();
                    }
                }).start();
                //通知launcher
                intent = new Intent("com.sen5.smartlife.camerachange");
                intent.putExtra("CameraDID", event.getCameraDID());
                intent.putExtra("type", 1);
                mContext.sendBroadcast(intent);
                break;

            case CameraEvent.CAMERA_RENAME:
                //刷新Camera
                readCameraIni();
                AppLog.i("刷新Camera列表");

                //通知底层Camera信息发生改变
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //所有的 Camera编辑事件都要等这个接口返回200的时候 才能进行真正的上层数据操作
                        notifyCameraChange();
                    }
                }).start();
                //通知launcher
                intent = new Intent("com.sen5.smartlife.camerachange");
                intent.putExtra("CameraDID", event.getCameraDID());
                intent.putExtra("type", 2);
                mContext.sendBroadcast(intent);
                break;
        }
    }


    private void notifyCameraChange() {
        //请求这个Url，去通知hal层，hal层处理成功回复200
        AppLog.i("通知hal层，camera改变");
        URL url;
        HttpURLConnection conn;
        try {
            url = new URL("http://127.0.0.1:20186/1");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                AppLog.i("hal层处理完Camera改变事件");
            } else {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

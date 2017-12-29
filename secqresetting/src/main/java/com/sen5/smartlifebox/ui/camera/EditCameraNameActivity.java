/*
 * Copyright (c) belongs to sen5.
 */

package com.sen5.smartlifebox.ui.camera;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseActivity;

import nes.ltlib.utils.AppLog;

import com.sen5.smartlifebox.data.JsonCreator;
import com.sen5.smartlifebox.data.db.CameraProviderFControl;
import com.sen5.smartlifebox.data.event.BackFragmentEvent;
import com.sen5.smartlifebox.data.event.CameraEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import hsl.p2pipcam.nativecaller.DeviceSDK;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import nes.ltlib.data.CameraEntity;
import nes.ltlib.LTSDKManager;

/**
 * Created by wanglin at 2016/10/19
 */
public class EditCameraNameActivity extends BaseActivity {
    private static final String serviceContent = "ADHOAFAJPFMPCNNCBIHOBAFHDMNFGJJCHDAGFHCFEAIHOLKFDHADCL" +
            "PBCJLLMMKBEIJCLDHGPJMLEMCDMGMMNOEIIGLHENDLEDCIHNBOMKKFFMCBBH";

    EditText etName;
    TextView tvName;

    private CameraEntity mCamera;
    private long mUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_member_name);
        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DeviceSDK.destoryDevice(mUserId);
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        etName = (EditText) findViewById(R.id.et_name);
        tvName = (TextView) findViewById(R.id.tv_name);
    }


    private void initData() {
        tvName.setText(getString(R.string.enter_camera_name));
        Bundle bundle = getIntent().getExtras();
        mCamera = bundle.getParcelable("camera");
        etName.setText(mCamera.getDeviceName());
        etName.setSelection(mCamera.getDeviceName().length());

        etName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode()
                        && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {

                    String name = etName.getText().toString();

                    String regEx = "^[a-z0-9A-Z\u4e00-\u9fa5]+$";
//                    Pattern.matches(regEx, name);
//                    Pattern p = Pattern.compile(regEx);
//                    Matcher matcher = p.matcher(name);
//                    matcher.matches();

                    if (name.equals("")) {
                        Toast.makeText(EditCameraNameActivity.this, getString(R.string.name_not_empty),
                                Toast.LENGTH_SHORT).show();
                    } else if (!name.matches(regEx)) {
                        AppLog.i("名字不能包含特殊字符");
                        Toast.makeText(EditCameraNameActivity.this, getString(R.string.name_not_special_character),
                                Toast.LENGTH_SHORT).show();
                    } else if (name.length() > 64) {
                        Toast.makeText(EditCameraNameActivity.this, getString(R.string.name_exceed_length),
                                Toast.LENGTH_SHORT).show();
                    } else if (name.equals(mCamera.getDeviceName())) {
                        finish();
                    } else {
                        showProgressDialog(getString(R.string.modify_camera_name));

                        //编辑摄像头本身的名字
//                        mUserId = DeviceSDK.createDevice("admin", "", "", 0, mCamera.getDeviceID(), 1);
//                        DeviceSDK.openDevice(mUserId);

                        if (!mCamera.getDeviceID().startsWith("SLIFE")) {

                            renameLtCamera(mCamera, name);

                        } else {

                            CameraProviderFControl cameraProviderFControl = new CameraProviderFControl(EditCameraNameActivity.this);
                            int status = cameraProviderFControl.getCameraStatusByDID(mCamera.getDeviceID());
                            if (status == 100) {
                                CameraEvent event = new CameraEvent(CameraEvent.CAMERA_ONLINE);
                                mUserId = cameraProviderFControl.getCameraUserIdByDID(mCamera.getDeviceID());
                                event.setUserId(mUserId);
                                EventBus.getDefault().post(event);
                            }
                        }

                    }
                }
                return false;

            }
        });
    }

    private void renameCamera(String name) {
        File file = new File("/data/smarthome/camera.ini");
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String content = line.substring(2, line.length() - 2);
                String[] strs = content.split("#");
                if (strs[0].equals(mCamera.getDeviceID())) {
                    String str = "##" + strs[0] + "#" + name + (strs.length > 2 ? "#" + strs[2] + "##" : "##");
                    sb.append(str).append("\n");
                } else {
                    sb.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            AppLog.e("Camera.ini文件格式异常");
        } finally {
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

            AppLog.e("Camera.ini：修改姓名数据:" + sb.toString());

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CameraEvent event) {
        long userId = event.getUserId();
        if (userId == mUserId) {
            switch (event.getFlag()) {
                case CameraEvent.CAMERA_ONLINE:
                    DeviceSDK.setDeviceParam(mUserId, 0x2702, JsonCreator.createRenameCameraJson(
                            etName.getText().toString()));
                    break;
                case CameraEvent.CAMERA_RENAME_SUCCESS: {
                    renameCamera(etName.getText().toString());
                    dismissProgressDialog();

                    EventBus.getDefault().post(new CameraEvent(CameraEvent.CAMERA_RENAME));
                    EventBus.getDefault().post(new BackFragmentEvent(BackFragmentEvent.IP_CAMERA_FRAGMENT));

                    finish();

                }
                break;
            }
        }
    }

    private boolean isSuccess;

    private void renameLtCamera(final CameraEntity entity, final String name) {


        LTSDKManager.getInstance().reCameraName(entity, name, new DisposableObserver<String>() {
            @Override
            public void onNext(@NonNull String s) {
                if (!"".equals(s)) {
                    isSuccess = true;
                    renameCamera(name);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

                dismissProgressDialog();
            }

            @Override
            public void onComplete() {

                if (isSuccess) {
                    dismissProgressDialog();

                    EventBus.getDefault().post(new CameraEvent(CameraEvent.CAMERA_RENAME));
                    EventBus.getDefault().post(new BackFragmentEvent(BackFragmentEvent.IP_CAMERA_FRAGMENT));
                    finish();
                }

            }
        });


    }


}

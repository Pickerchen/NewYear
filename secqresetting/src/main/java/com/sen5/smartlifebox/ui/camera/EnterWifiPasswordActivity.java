package com.sen5.smartlifebox.ui.camera;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.SecQreSettingApplication;
import com.sen5.smartlifebox.base.BaseActivity;
import com.sen5.smartlifebox.common.utils.HSmartLinkUtils;
import com.sen5.smartlifebox.common.utils.PreferencesUtils;
import com.sen5.smartlifebox.common.utils.TestToast;

import nes.ltlib.interf.CameraSearchListener;
import nes.ltlib.utils.AppLog;

import com.sen5.smartlifebox.data.event.BackFragmentEvent;
import com.sen5.smartlifebox.ui.main.MainActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import nes.ltlib.LTSDKManager;
import nes.ltlib.data.CameraEntity;
import nes.ltlib.interf.CameraWifiListener;

import static com.sen5.smartlifebox.SecQreSettingApplication.isSupportWIFI;


public class EnterWifiPasswordActivity extends BaseActivity implements CameraWifiListener {
    private static final String SP_HIDE_PASSWORD = "hide_password";

    TextView tvName;
    EditText etPassword;
    CheckBox cbHidePassword;
    private String mWifiName;
    private String wifiPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_password);
        initView();

        initData();
    }


    private void initView() {
        etPassword = (EditText) findViewById(R.id.et_password);
        tvName = (TextView) findViewById(R.id.tv_name);
        cbHidePassword = (CheckBox) findViewById(R.id.cb_hide_password);
    }

    private void initData() {
        mWifiName = getIntent().getStringExtra("WifiName");
//        tvName.setText(String.format("%s " + mWifiName, getString(R.string.enter_wifi_password)));
        tvName.setText(getString(R.string.enter_wifi_password_new) + " " + mWifiName);
        String savePassword = PreferencesUtils.getString(this, mWifiName);
        boolean hidePassword = PreferencesUtils.getBoolean(this, SP_HIDE_PASSWORD, false);
        if (savePassword != null) {
            etPassword.setText(savePassword);
//            cbHidePassword.setChecked(true);
//            etPassword.setInputType(InputType.TYPE_CLASS_TEXT
//                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setSelection(savePassword.length());
        }

        if (hidePassword) {
            cbHidePassword.setChecked(true);
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            cbHidePassword.setChecked(false);
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }

        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode()
                        && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {
                    wifiPassword = etPassword.getText().toString();
                    if (wifiPassword.length() > 0) {
                        //记住wifi密码
                        PreferencesUtils.putString(EnterWifiPasswordActivity.this, mWifiName, wifiPassword);


                        //如果支持wifi配置 跳转到Camera连接页面
                        if (isSupportWIFI) {
                            Intent intent = new Intent(EnterWifiPasswordActivity.this, ConnectCameraActivity.class);
                            intent.putExtra("WifiName", mWifiName);
                            intent.putExtra("WifiPassword", wifiPassword);
                            startActivityAnim(intent);
                        } else {

                            //如果不支持wifi配置 设置wifi功能
                            if (isLTCamera()) {
                                setLTCamera(wifiPassword);
                            } else {

                                setOldCamera(wifiPassword);
                            }
                        }


                    } else {
                        Toast.makeText(EnterWifiPasswordActivity.this, getString(R.string.password_empty),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                return false;

            }
        });

        cbHidePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                TestToast.showShort(EnterWifiPasswordActivity.this, "check: " + isChecked);
                if (isChecked) {
                    //必须加上InputType.TYPE_CLASS_TEXT才起作用
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    PreferencesUtils.putBoolean(EnterWifiPasswordActivity.this, SP_HIDE_PASSWORD, true);
                } else {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    PreferencesUtils.putBoolean(EnterWifiPasswordActivity.this, SP_HIDE_PASSWORD, false);
                }
            }
        });
    }


    private boolean isLTCamera() {
        List<CameraEntity> cameraEntities = SecQreSettingApplication.getCameras();

        if (!cameraEntities.isEmpty()) {
            CameraEntity entity = cameraEntities.get(0);
            if (entity.getDeviceID().startsWith("SLIFE")) {
                return false;
            } else {
                return true;
            }
        }

        return false;
    }


    private void setLTCamera(String pwd) {
        showProgressDialog(getString(R.string.setting_wifi_camera));
        LTSDKManager.getInstance().setWifiListener(this);

        LTSDKManager.getInstance().startConnect(SecQreSettingApplication.deviceId, mWifiName, pwd);


    }


    private void setOldCamera(String wifiPassword) {
        showProgressDialog(getString(R.string.setting_wifi_camera));

        HSmartLinkUtils.getInstance().setCameraWifi(SecQreSettingApplication.deviceId, mWifiName, wifiPassword, this);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        HSmartLinkUtils.unInitSearch();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //记住wifi密码
        PreferencesUtils.putString(this, mWifiName, etPassword.getText().toString());
    }

    @Override
    public void setWifiSuccess() {


        Toast.makeText(this, getString(R.string.setting_wifi_success), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissProgressDialog();
                AppLog.i(getString(R.string.setting_wifi_success));
                BackFragmentEvent event = new BackFragmentEvent(BackFragmentEvent.IP_CAMERA_FRAGMENT);
                EventBus.getDefault().post(event);
                Intent intent = new Intent(EnterWifiPasswordActivity.this, MainActivity.class);
                EnterWifiPasswordActivity.this.startActivity(intent);
            }
        }, 3000);


    }

    @Override
    public void setWifiFailure() {

        dismissProgressDialog();
        Toast.makeText(this, R.string.setting_wifi_failure, Toast.LENGTH_SHORT).show();

        AppLog.i("设置wifi失败");
    }


}

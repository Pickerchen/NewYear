package com.sen5.smartlifebox.ui.main;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.sen5.smartlifebox.SecQreSettingApplication;
import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseActivity;
import nes.ltlib.utils.AppLog;
import com.sen5.smartlifebox.data.entity.ContactEntity;
import com.sen5.smartlifebox.data.entity.MemberEntity;
import com.sen5.smartlifebox.data.event.BackFragmentEvent;
import com.sen5.smartlifebox.data.event.SwitchFragmentEvent;
import com.sen5.smartlifebox.data.p2p.P2PModel;

import com.sen5.smartlifebox.ui.camera.AddCancelFragment;
import com.sen5.smartlifebox.ui.camera.AddWifiCameraFragment;
import com.sen5.smartlifebox.ui.camera.AllWifiCameraFragment;
import com.sen5.smartlifebox.ui.camera.CameraFragment;
import com.sen5.smartlifebox.ui.camera.DeleteCameraFragment;
import com.sen5.smartlifebox.ui.camera.EditCameraFragment;
import com.sen5.smartlifebox.ui.camera.FindLANCamerasFragment;
import com.sen5.smartlifebox.ui.camera.NewSetWifiCameraFragment;
import com.sen5.smartlifebox.ui.members.DeleteMemberFragment;
import com.sen5.smartlifebox.ui.members.EditMemberFragment;
import com.sen5.smartlifebox.ui.members.MembersFragment;
import com.sen5.smartlifebox.ui.other.AffirmDeleteFragment;
import com.sen5.smartlifebox.ui.other.EditContactFragment;
import com.sen5.smartlifebox.ui.other.EmergencyContactFragment;
import com.sen5.smartlifebox.ui.other.ManageContactFragment;
import com.sen5.smartlifebox.ui.other.NotificationSystemChooseFragment;
import com.sen5.smartlifebox.ui.other.OtherFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import nes.ltlib.data.CameraEntity;

import static com.sen5.smartlifebox.SecQreSettingApplication.mDefaultWifiEnable;

public class MainActivity extends BaseActivity {

    ImageView ivHead;
    TextView tvName;
    TextView tvParentName;


    P2PModel mP2PModel;

    private FragmentManager mFragmentManager;
    private CameraEntity mCurCamera;
    private ContactEntity mCurContact;
    private MemberEntity mCurMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDefaultWifiEnable = ((WifiManager) getSystemService(WIFI_SERVICE)).isWifiEnabled();
        AppLog.e("--------------mDefaultWifiEnable = " + mDefaultWifiEnable);
        initView();
//        startService(new Intent(this, DeviceSDKService.class));
        EventBus.getDefault().register(this);
        registerHomeReceiver();
        initData();

//        { "msg_type": 106, "dev_id": 4, "ipc_did": 5, "status": [ { "id": 2, "params": "AQAAAA==" } ] }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
//                AppLog.e("-----------------mDefaultWifiEnable = " + mDefaultWifiEnable);
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在onResume中退栈是因为Activity的退栈操作必须在onSaveInstanceState()之前执行
        if (mBackFragmentEvent != null) {
            getSupportFragmentManager().popBackStackImmediate(mBackFragmentEvent.getFlag(), 0);
            if (mBackFragmentEvent.isSwitchFindLANCameraFragment()) {
                EventBus.getDefault().post(new SwitchFragmentEvent(
                        SwitchFragmentEvent.FIND_LAN_CAMERA_FRAGMENT));
            }
            mBackFragmentEvent = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //停止IP Camera SDK服务
//        stopService(new Intent(this, DeviceSDKService.class));
        unRegisterHomeReceiver();
        //还原wifi，插网线时这里可能会导致wifi开启，从而插了网线还会去连接wifi，原因是插网线时拿到的默认wifi状态为开
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(SecQreSettingApplication.isDefaultWifiEnable());
        AppLog.i("AddWifiCameraFragment:还原wifi");


//        Process.killProcess(Process.myPid());
//        P2PModel.getInstance(this).destroyP2P();
    }

    @Override
    protected void resetWifi() {
        super.resetWifi();
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(SecQreSettingApplication.isDefaultWifiEnable());
    }

    private void initView() {

        ivHead = (ImageView) findViewById(R.id.iv_head);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvParentName = (TextView) findViewById(R.id.tv_parent_name);




    }


    private void initData() {

        mP2PModel = P2PModel.getInstance(this);

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .add(R.id.space_fragment, new MainFragment())
                .commit();
        mFragmentManager.addOnBackStackChangedListener(mOnBackStackChangedListener);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SwitchFragmentEvent event) {
        switch (event.getFlag()) {
            /**********************Camera******************************/
            case SwitchFragmentEvent.IP_CAMERA_FRAGMENT: {
                CameraFragment ipCameraFragment = new CameraFragment();

                mFragmentManager.beginTransaction()
                        .replace(R.id.space_fragment, ipCameraFragment)
                        .addToBackStack(SwitchFragmentEvent.IP_CAMERA_FRAGMENT)//将这个事务添加到Activity管理的back stack中，当执行pop stack时，这个事务就会被反转
                        .commitAllowingStateLoss();
            }
            break;

            case SwitchFragmentEvent.SET_NEW_WIFI_CAMERA: {

                mFragmentManager.beginTransaction()
                        .replace(R.id.space_fragment, NewSetWifiCameraFragment.newInstance(), "SET_NEW_WIFI_CAMERA")
                        .addToBackStack(SwitchFragmentEvent.SET_NEW_WIFI_CAMERA)
                        .commit();
            }
            break;

            case SwitchFragmentEvent.ADD_WIFI_CAMERA_FRAGMENT: {
                AddWifiCameraFragment wifiCameraFragment = new AddWifiCameraFragment();

                mFragmentManager.beginTransaction()
                        .replace(R.id.space_fragment, wifiCameraFragment, "ADD_WIFI_CAMERA_FRAGMENT")
                        .addToBackStack(SwitchFragmentEvent.ADD_WIFI_CAMERA_FRAGMENT)
                        .commit();
            }
            break;


            case SwitchFragmentEvent.ALL_WIFI_CAMERA_FRAGMENT: {
                AddWifiCameraFragment addWifiCameraFragment = (AddWifiCameraFragment) mFragmentManager
                        .findFragmentByTag("ADD_WIFI_CAMERA_FRAGMENT");
                AllWifiCameraFragment allWifiCameraFragment = AllWifiCameraFragment.newInstance(
                        addWifiCameraFragment.getScanResults());

                mFragmentManager.beginTransaction()
                        .replace(R.id.space_fragment, allWifiCameraFragment)
                        .addToBackStack(SwitchFragmentEvent.ALL_WIFI_CAMERA_FRAGMENT)
                        .commit();
            }
            break;

            case SwitchFragmentEvent.FIND_LAN_CAMERA_FRAGMENT: {
                FindLANCamerasFragment fragment = new FindLANCamerasFragment();
                mFragmentManager.beginTransaction()
                        .replace(R.id.space_fragment, fragment)
                        .addToBackStack(SwitchFragmentEvent.FIND_LAN_CAMERA_FRAGMENT)
                        .commitAllowingStateLoss();
            }
            break;
            case SwitchFragmentEvent.ADD_CANCEL_FRAGMENT: {
                mCurCamera = event.getCamera();
                AddCancelFragment fragment = AddCancelFragment.newInstance(mCurCamera);
                mFragmentManager.beginTransaction()
                        .replace(R.id.space_fragment, fragment)
                        .addToBackStack(SwitchFragmentEvent.ADD_CANCEL_FRAGMENT)
                        .commit();
            }
            break;

            case SwitchFragmentEvent.EDIT_CAMERA_FRAGMENT: {
                mCurCamera = event.getCamera();
                EditCameraFragment membersFragment = EditCameraFragment.newInstance(mCurCamera);

                mFragmentManager.beginTransaction()
                        .replace(R.id.space_fragment, membersFragment)
                        .addToBackStack(SwitchFragmentEvent.EDIT_CAMERA_FRAGMENT)
                        .commit();


            }
            break;

            case SwitchFragmentEvent.DELETE_CAMERA_FRAGMENT: {
                mCurCamera = event.getCamera();
                DeleteCameraFragment fragment = DeleteCameraFragment.newInstance(mCurCamera);

                mFragmentManager.beginTransaction()
                        .replace(R.id.space_fragment, fragment)
                        .addToBackStack(SwitchFragmentEvent.DELETE_CAMERA_FRAGMENT)
                        .commit();
            }
            break;

            /**********************Member*****************************/
            case SwitchFragmentEvent.MEMBERS_FRAGMENT: {
                MembersFragment membersFragment = new MembersFragment();
                mFragmentManager.beginTransaction()
                        .replace(R.id.space_fragment, membersFragment)
                        .addToBackStack(SwitchFragmentEvent.MEMBERS_FRAGMENT)
                        .commit();
            }
            break;

            case SwitchFragmentEvent.EDIT_MEMBER_FRAGMENT: {
                mCurMember = event.getMember();
                EditMemberFragment editMemberFragment = EditMemberFragment.newInstance(mCurMember);

                mFragmentManager.beginTransaction()
                        .replace(R.id.space_fragment, editMemberFragment)
                        .addToBackStack(SwitchFragmentEvent.EDIT_MEMBER_FRAGMENT)
                        .commit();

            }

            break;

            case SwitchFragmentEvent.DELETE_MEMBER_FRAGMENT: {
                mCurMember = event.getMember();
                DeleteMemberFragment yesNoFragment = DeleteMemberFragment.newInstance(mCurMember);

                mFragmentManager.beginTransaction()
                        .replace(R.id.space_fragment, yesNoFragment)
                        .addToBackStack(SwitchFragmentEvent.DELETE_MEMBER_FRAGMENT)
                        .commit();
            }

            break;

            /***********************Other***************************/
            case SwitchFragmentEvent.OTHER_FRAGMENT: {
                OtherFragment fragment = new OtherFragment();

                mFragmentManager.beginTransaction()
                        .replace(R.id.space_fragment, fragment)
                        .addToBackStack(SwitchFragmentEvent.OTHER_FRAGMENT)
                        .commit();
            }
            break;
            case SwitchFragmentEvent.NOTIFICATION_SYSTEM_CHOOSE:
                NotificationSystemChooseFragment notificationSystemChooseFragment = new NotificationSystemChooseFragment();
                mFragmentManager.beginTransaction().replace(R.id.space_fragment, notificationSystemChooseFragment)
                        .addToBackStack(SwitchFragmentEvent.NOTIFICATION_SYSTEM_CHOOSE).commit();
                break;

            case SwitchFragmentEvent.EMERGENCY_CONTACT_FRAGMENT: {
                EmergencyContactFragment fragment = new EmergencyContactFragment();

                mFragmentManager.beginTransaction()
                        .replace(R.id.space_fragment, fragment)
                        .addToBackStack(SwitchFragmentEvent.EMERGENCY_CONTACT_FRAGMENT)
                        .commit();
            }

            break;

            case SwitchFragmentEvent.MANAGE_CONTACT_FRAGMENT: {
                ManageContactFragment fragment = new ManageContactFragment();

                mFragmentManager.beginTransaction()
                        .replace(R.id.space_fragment, fragment)
                        .addToBackStack(SwitchFragmentEvent.MANAGE_CONTACT_FRAGMENT)
                        .commit();
            }

            break;

            case SwitchFragmentEvent.AFFIREM_DELETE_FRAGMENT: {
                mCurContact = event.getContact();
                AffirmDeleteFragment fragment = AffirmDeleteFragment.newInstance(mCurContact);

                mFragmentManager.beginTransaction()
                        .replace(R.id.space_fragment, fragment)
                        .addToBackStack(SwitchFragmentEvent.AFFIREM_DELETE_FRAGMENT)
                        .commit();
            }

            break;

            case SwitchFragmentEvent.EDIT_CONTACT_FRAGMENT: {
                mCurContact = event.getContact();
                EditContactFragment fragment = EditContactFragment.newInstance(mCurContact);

                mFragmentManager.beginTransaction()
                        .replace(R.id.space_fragment, fragment)
                        .addToBackStack(SwitchFragmentEvent.EDIT_CONTACT_FRAGMENT)
                        .commit();
            }

            break;
        }
    }

    private BackFragmentEvent mBackFragmentEvent;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BackFragmentEvent event) {
        mBackFragmentEvent = event;
    }

    private FragmentManager.OnBackStackChangedListener mOnBackStackChangedListener =
            new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    int count = mFragmentManager.getBackStackEntryCount();
                    if (count == 0) {
                        tvName.setText(getString(R.string.secQre));
                        ivHead.setImageResource(R.drawable.ic_secqre);
                    } else {
                        FragmentManager.BackStackEntry entry = mFragmentManager.getBackStackEntryAt(count - 1);
                        AppLog.e("entry 名：" + entry.getName());
                        switch (entry.getName()) {
                            /**********************Camera**********************/
                            case SwitchFragmentEvent.IP_CAMERA_FRAGMENT:
                                tvParentName.setText("");
                                tvName.setText(getString(R.string.ip_cameras));
                                ivHead.setImageResource(R.drawable.ic_camera);
                                break;

                            case SwitchFragmentEvent.ADD_WIFI_CAMERA_FRAGMENT:
                                tvParentName.setText(getString(R.string.ip_cameras));
                                tvName.setText(getString(R.string.set_wifi_camera));
                                ivHead.setImageResource(R.drawable.ic_camera);
                                break;

                            case SwitchFragmentEvent.ALL_WIFI_CAMERA_FRAGMENT:

                                break;

                            case SwitchFragmentEvent.FIND_LAN_CAMERA_FRAGMENT:
                                tvName.setText(getString(R.string.find_lan_cameras));
                                tvParentName.setText(getString(R.string.ip_cameras));
                                ivHead.setImageResource(R.drawable.ic_camera);
                                break;

                            case SwitchFragmentEvent.ADD_CANCEL_FRAGMENT:
                                tvName.setText(mCurCamera.getDeviceName());
                                tvParentName.setText(getString(R.string.find_lan_cameras));
                                ivHead.setImageResource(R.drawable.ic_camera);
                                break;

                            case SwitchFragmentEvent.EDIT_CAMERA_FRAGMENT:
                                tvName.setText(mCurCamera.getDeviceName());
                                tvParentName.setText(getString(R.string.ip_cameras));
                                ivHead.setImageResource(R.drawable.ic_camera);
                                break;

                            /********************Members***************************/
                            case SwitchFragmentEvent.MEMBERS_FRAGMENT:
                                tvName.setText(getString(R.string.members));
                                ivHead.setImageResource(R.drawable.ic_members);
                                break;

                            case SwitchFragmentEvent.EDIT_MEMBER_FRAGMENT:
                                tvName.setText(mCurMember.getIdentity_name());
                                ivHead.setImageResource(R.drawable.ic_members);
                                break;

                            case SwitchFragmentEvent.DELETE_MEMBER_FRAGMENT:

                                break;

                            /*********************Other**************************/
                            case SwitchFragmentEvent.OTHER_FRAGMENT:
                                tvParentName.setText("");
                                tvName.setText(getString(R.string.other));
                                ivHead.setImageResource(R.drawable.ic_security);
                                break;

                            case SwitchFragmentEvent.EMERGENCY_CONTACT_FRAGMENT:
                                tvName.setText(getString(R.string.emergency_contancts));
                                tvParentName.setText(getString(R.string.other));
                                ivHead.setImageResource(R.drawable.ic_contacts);

                                break;
                            case SwitchFragmentEvent.NOTIFICATION_SYSTEM_CHOOSE:
                                tvName.setText(getString(R.string.notification_system));
                                tvParentName.setText(getString(R.string.other));
                                ivHead.setImageResource(R.drawable.ic_contacts);

                                break;

                            case SwitchFragmentEvent.MANAGE_CONTACT_FRAGMENT:
                                tvName.setText(getString(R.string.contact_management));
                                tvParentName.setText(getString(R.string.emergency_contancts));
                                ivHead.setImageResource(R.drawable.ic_contacts);
                                break;

                            case SwitchFragmentEvent.AFFIREM_DELETE_FRAGMENT:
                                tvName.setText(mCurContact.getName());
                                tvParentName.setText(getString(R.string.emergency_contancts));
                                ivHead.setImageResource(R.drawable.ic_contacts);
                                break;

                            case SwitchFragmentEvent.EDIT_CONTACT_FRAGMENT:
                                tvName.setText(mCurContact.getName());
                                tvParentName.setText(getString(R.string.emergency_contancts));
                                ivHead.setImageResource(R.drawable.ic_contacts);
                                break;
                        }
                    }
                }
            };


}

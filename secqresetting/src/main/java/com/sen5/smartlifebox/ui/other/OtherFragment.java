package com.sen5.smartlifebox.ui.other;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseActivity;
import com.sen5.smartlifebox.base.BaseFragment;
import com.sen5.smartlifebox.common.utils.Utils;
import com.sen5.smartlifebox.data.event.SwitchFragmentEvent;
import com.sen5.smartlifebox.socket.OnSocketClientListener;
import com.sen5.smartlifebox.socket.ZigBeeSocket;
import com.sen5.smartlifebox.widget.WheelView;

import org.greenrobot.eventbus.EventBus;



/**
 * Created by wanglin on 2017/2/8.
 */
public class OtherFragment extends BaseFragment {

    public static final int INDEX_NOTIFICATION_SYSTEM = 0;
    public static final int INDEX_EMERGENCY_CONTACTS = 1;

    private View contentView;
    WheelView wheelView;
    public TextView mTvNumber;

    private ZigBeeSocket socket;
    private int mode;

    public OtherFragment() {
        // Required empty public constructor
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

    @Override
    public void onResume() {
        super.onResume();
        String notificationSystemSwitchStr = getNotificationSystemSwitchStr(mContext);
        if (null != mTvNumber) {
            mTvNumber.setText(notificationSystemSwitchStr);
        }
    }

    private void addNotificationSystem() {
        //和 other中的Emergency Contacts 的 selection子项 共用一个view
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.item_contact, wheelView, false);

        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        ImageView ivDot = (ImageView) view.findViewById(R.id.iv_dot);
        TextView tvLabel = (TextView) view.findViewById(R.id.tv_label);
        mTvNumber = (TextView) view.findViewById(R.id.tv_number);

        tvName.setText(getString(R.string.notification_system));

        String notificationSystemSwitchStr = getNotificationSystemSwitchStr(mContext);
        mTvNumber.setText(notificationSystemSwitchStr);

        ivDot.setVisibility(View.INVISIBLE);
        tvLabel.setVisibility(View.INVISIBLE);

        wheelView.addItem(view);


//        List<String> items = new ArrayList<>();
//        items.add(getString(R.string.string_Configure));
//        items.add(getString(R.string.string_restore));
//
//        wheelView.addItem(items);
    }


    private String getNotificationSystemSwitchStr(Context context) {
        boolean isShowNotificationToast = NotificationSystemChooseFragment.getIsShowNotificationToast(mContext);
        String str = "";
        if (isShowNotificationToast) {
            str = context.getString(R.string.on);
        } else {
            str = context.getString(R.string.off);
        }
        return str;
    }



    private void initView() {
        wheelView = (WheelView) contentView.findViewById(R.id.wheel_view);
    }


    private void initData() {
//        wheelView.addItem(getString(R.string.notification_system));
        addNotificationSystem();
        if (hasLTEmode()) {

            wheelView.addItem(getString(R.string.emergency_contancts));
        }

        wheelView.setmOnItemClickListener(new WheelView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                switch (position) {
                    case INDEX_NOTIFICATION_SYSTEM:
                        EventBus.getDefault().post(new SwitchFragmentEvent(
                                SwitchFragmentEvent.NOTIFICATION_SYSTEM_CHOOSE));

                        break;

                    case INDEX_EMERGENCY_CONTACTS:
                        if (hasLTEmode()) {
                            EventBus.getDefault().post(new SwitchFragmentEvent(
                                    SwitchFragmentEvent.EMERGENCY_CONTACT_FRAGMENT));
                        } else {
                            mode = 0;
                            socket();
                        }

                        break;

                    case 2:
                        mode = 1;
                        socket();
                        break;

                }
            }
        });
    }

    private static final String ZIG_BEE = SystemProperties.get("ro.sen5.house.module", "null");
    private static final String ZIG_BEE_NEW = SystemProperties.get("ro.sen5.has.modemtype", "null");
    private static final String HOUSE_VALUE_LTE = "lte";
    private static final String HOUSE_VALUE_4G = "4g";

    //    ro.sen5.house.module=zigbee&gsm ?? ro.sen5.house.module=zigbee&lte ???4G??
//ro.sen5.has.modemtype=4g
    private boolean hasLTEmode() {
        String[] split = ZIG_BEE.split("&");
        for (int i = 0; i < split.length; i++) {
            if (HOUSE_VALUE_LTE.equalsIgnoreCase(split[i])) {
                return true;
            } else {

            }
        }
        if (ZIG_BEE_NEW.contains(HOUSE_VALUE_4G)) {
            return true;
        }
        return false;
    }

    private void socket() {
        if (socket == null) {

            socket = new ZigBeeSocket();

            socket.setOnSocketClientListener(new OnSocketClientListener() {
                @Override
                public void onSocketConnFail() {

                }

                @Override
                public void onSocketConnSuccess() {
                    if (mode == 0) {

                        showZigBeeDialog();
                    } else if (mode == 1) {
                        socket.Restore();

                    }

                }

                @Override
                public void onSocketRecvData(String strInfo) {

                }

                @Override
                public void onPlay(int whichCamera, int encrypt, byte[] pAVData, int nFrameSize, int frameCount) {

                }

                @Override
                public void onSocketDisconnect() {

                }
            });

            socket.setListener(new ZigBeeSocket.DataListener() {
                @Override
                public void getData(String str) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                            getActivity().onBackPressed();

                        }
                    });
                }
            });

            socket.connectSocket();
        } else {
            if (mode == 0) {

                showZigBeeDialog();
            } else if (mode == 1) {
                socket.Restore();

            }
        }

    }


    private void showZigBeeDialog() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_zigbee, null);
                final EditText edt = (EditText) view.findViewById(R.id.et_name);
                builder.setTitle(R.string.string_code).setView(view).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String str = edt.getText().toString().trim();
                        if (str.length() == 12 || str.length() == 16 || str.length() == 24 || str.length() == 32) {
                            socket.sendData(str);
                        } else {
                            Toast.makeText(getActivity(), "秘钥为12，16，24，32位", Toast.LENGTH_SHORT).show();
                        }
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });
    }

    @Override
    public void onDestroy() {

        if (socket != null)
            socket.disConnect();
        super.onDestroy();
    }
}

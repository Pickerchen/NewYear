package com.sen5.secure.launcher.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sen5.secure.launcher.R;
import com.sen5.secure.launcher.base.BaseViewHolderAdapter;
import com.sen5.secure.launcher.base.LauncherApplication;
import com.sen5.secure.launcher.data.DeviceJudge;
import com.sen5.secure.launcher.data.entity.Constant;
import com.sen5.secure.launcher.data.interf.ItemOnKeyListener;
import com.sen5.secure.launcher.widget.Sen5TextView;
import com.sen5.smartlifebox.data.entity.DeviceData;
import com.sen5.smartlifebox.data.entity.DeviceStatusData;
import com.sen5.smartlifebox.data.entity.RoomData;
import com.sen5.smartlifebox.data.p2p.P2PModel;

/**
 * Created by ZHOUDAO on 2017/5/23.
 */

public class DevicePresenter extends BaseViewHolderAdapter<DeviceData> {


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_list, parent, false);
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        try {
            if (list.isEmpty()) {
                return;
            }

            if (position > list.size() - 1) {
                return;
            }

            DeviceData data = list.get(position);

            if (data == null) {
                return;
            }

            if (keyListener != null) {
                viewHolder.itemView.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        return keyListener.onKey(v, keyCode, event, position);
                    }
                });
            }


            ((ViewHolder) viewHolder).mTvRoomName.setText("");
            data.setRoom_id(DeviceData.NO_ROOM_ID);

            if (viewHolder instanceof ViewHolder) {


                if (!P2PModel.mRooms.isEmpty()) {


                    for (RoomData room : P2PModel.mRooms) {


                        for (int i : room.getDev_list()) {
                            if (i == data.getDev_id()) {
                                data.setRoom_id(room.getRoom_id());
                            }
                        }

                        if (room.getRoom_id() == data.getRoom_id()) {
                            if (room.getRoom_name().equals("_")) {

                                continue;
                            }
                            ((ViewHolder) viewHolder).mTvRoomName.setText(room.getRoom_name());
                            continue;
                        }
                    }

                }

                switch (DeviceJudge.getTypeName(data.getDev_type())) {
                    case R.string.light_device:
                        setActionDevUi((ViewHolder) viewHolder, R.drawable.ic_light_close, R.drawable.selector_bg_blue, R.string.Offline);

                        if (data.getStatus() != null)
                            for (DeviceStatusData statusData : data.getStatus()) {

                                if (statusData.getId() == 1) {

                                    if (statusData.getParams()[0] == 0) {//正常状态

                                        setActionDevUi((ViewHolder) viewHolder, R.drawable.ic_light_close, R.drawable.selector_bg_blue, R.string.off);

                                    } else if (statusData.getParams()[0] == 1) {//被触发

                                        setActionDevUi((ViewHolder) viewHolder, R.drawable.ic_light_open, R.drawable.selector_bg_light, R.string.on);
                                    }
                                } else if (statusData.getId() == Constant.ZB_ONLINE_AND_STATUS && data.getStatus().size() == 1) {
                                    if (statusData.getParams()[0] == 1) {
                                        if (statusData.getParams()[1] == 0) {//正常状态

                                            setActionDevUi((ViewHolder) viewHolder, R.drawable.ic_light_close, R.drawable.selector_bg_blue, R.string.off);

                                        } else if (statusData.getParams()[1] == 1) {//被触发

                                            setActionDevUi((ViewHolder) viewHolder, R.drawable.ic_light_open, R.drawable.selector_bg_light, R.string.on);
                                        }
                                    } else {

                                        setActionDevUi((ViewHolder) viewHolder, R.drawable.ic_light_close, R.drawable.selector_bg_blue, R.string.Offline);

                                    }
                                }
                            }

                        ((ViewHolder) viewHolder).mTvDevName.setText(data.getName());
                        ((ViewHolder) viewHolder).mTvDevName.setHint(DeviceJudge.getTypeName(data.getDev_type()));
                        break;

                    case R.string.outlet_device:

                        setActionDevUi((ViewHolder) viewHolder, R.drawable.ic_outlet_close, R.drawable.selector_bg_blue, R.string.Offline);

                        if (data.getStatus()!=null)
                        for (DeviceStatusData statusData : data.getStatus()) {
                            if (statusData.getId() == 1) {
                                if (statusData.getParams()[0] == 0) {//正常状态

                                    setActionDevUi((ViewHolder) viewHolder, R.drawable.ic_outlet_close, R.drawable.selector_bg_blue, R.string.off);
                                } else if (statusData.getParams()[0] == 1) {//被触发

                                    setActionDevUi((ViewHolder) viewHolder, R.drawable.ic_outlet_open, R.drawable.selector_bg_outlet, R.string.on);
                                }

                            } else if (statusData.getId() == Constant.ZB_ONLINE_AND_STATUS && data.getStatus().size() == 1) {

                                if (statusData.getParams()[0] == 1) {
                                    if (statusData.getParams()[1] == 0) {//正常状态

                                        setActionDevUi((ViewHolder) viewHolder, R.drawable.ic_outlet_close, R.drawable.selector_bg_blue, R.string.off);

                                    } else if (statusData.getParams()[1] == 1) {//被触发
                                        setActionDevUi((ViewHolder) viewHolder, R.drawable.ic_outlet_open, R.drawable.selector_bg_outlet, R.string.on);

                                    }
                                } else {

                                    setActionDevUi((ViewHolder) viewHolder, R.drawable.ic_outlet_close, R.drawable.selector_bg_blue, R.string.Offline);
                                }
                            }
                        }
                        ((ViewHolder) viewHolder).mTvDevName.setText(data.getName());
                        ((ViewHolder) viewHolder).mTvDevName.setHint(DeviceJudge.getTypeName(data.getDev_type()));
                        break;
                    case R.string.relay:

                        setActionDevUi((ViewHolder) viewHolder, R.drawable.ic_relay_close, R.drawable.selector_bg_blue, R.string.Offline);

                        if (data.getStatus()!=null)
                        for (DeviceStatusData statusData : data.getStatus()) {
                            if (statusData.getId() == 1) {

                                if (statusData.getParams()[0] == 0) {//正常状态

                                    setActionDevUi((ViewHolder) viewHolder, R.drawable.ic_relay_close, R.drawable.selector_bg_blue, R.string.off);
                                } else if (statusData.getParams()[0] == 1) {//被触发

                                    setActionDevUi((ViewHolder) viewHolder, R.drawable.ic_relay_open, R.drawable.selector_bg_orange, R.string.on);

                                }
                            } else if (statusData.getId() == Constant.ZB_ONLINE_AND_STATUS && data.getStatus().size() == 1) {

                                if (statusData.getParams()[0] == 1) {
                                    if (statusData.getParams()[1] == 0) {//正常状态


                                        setActionDevUi((ViewHolder) viewHolder, R.drawable.ic_relay_close, R.drawable.selector_bg_blue, R.string.off);


                                    } else if (statusData.getParams()[1] == 1) {//被触发


                                        setActionDevUi((ViewHolder) viewHolder, R.drawable.ic_relay_open, R.drawable.selector_bg_orange, R.string.on);

                                    }
                                } else {

                                    setActionDevUi((ViewHolder) viewHolder, R.drawable.ic_relay_close, R.drawable.selector_bg_blue, R.string.Offline);

                                }
                            }
                        }

                        ((ViewHolder) viewHolder).mTvDevName.setText(data.getName());
                        ((ViewHolder) viewHolder).mTvDevName.setHint(DeviceJudge.getTypeName(data.getDev_type()));
                        break;

                }

            }


        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setActionDevUi(ViewHolder viewHolder, int devImg, int devbg, int status) {

        viewHolder.mIvDevStatus.setImageResource(devImg);
        viewHolder.mLlRoot.setBackgroundResource(devbg);
        viewHolder.mTvDevStatus.setText(LauncherApplication.mContext.getResources().getString(status));
    }


    private class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvDevStatus, mIvLowPower;
        private Sen5TextView mTvDevStatus, mTvRoomName;
        private LinearLayout mLlRoot;

        private Sen5TextView mTvDevName;

        public ViewHolder(View view) {
            super(view);
            mLlRoot = (LinearLayout) view.findViewById(R.id.ll_root);
            mIvDevStatus = (ImageView) view.findViewById(R.id.iv_device_status);
            mTvRoomName = (Sen5TextView) view.findViewById(R.id.tv_room_name);
            mTvDevStatus = (Sen5TextView) view.findViewById(R.id.tv_device_status);
            mIvLowPower = (ImageView) view.findViewById(R.id.iv_low_power);
            mTvDevName = (Sen5TextView) view.findViewById(R.id.tv_dev_name);
        }
    }

    public void setKeyListener(ItemOnKeyListener keyListener) {
        this.keyListener = keyListener;
    }

    private ItemOnKeyListener keyListener;
}

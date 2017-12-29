package com.sen5.secure.launcher.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sen5.secure.launcher.R;
import com.sen5.secure.launcher.base.BaseViewHolderAdapter;
import com.sen5.secure.launcher.data.DeviceJudge;
import com.sen5.secure.launcher.data.entity.Constant;
import com.sen5.secure.launcher.widget.Sen5TextView;
import com.sen5.smartlifebox.data.entity.DeviceData;
import com.sen5.smartlifebox.data.entity.DeviceStatusData;
import com.sen5.smartlifebox.data.entity.ModeData;
import com.sen5.smartlifebox.data.entity.RoomData;
import com.sen5.smartlifebox.data.p2p.P2PModel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ZHOUDAO on 2017/5/23.
 */

public class SensorPresenter extends BaseViewHolderAdapter<DeviceData> {

    private P2PModel mModel;
    private Context mContext;

    public SensorPresenter(Context mContext, P2PModel model) {

        this.mModel = model;
        this.mContext = mContext;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sensor_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {


        try {
            if (viewHolder instanceof ViewHolder) {


                if (position > list.size() - 1) {
                    return;
                }


                DeviceData deviceData = list.get(position);


                ModeData mCurMode = mModel.getCurMode();

                ViewHolder holder = (ViewHolder) viewHolder;
                holder.mLlSensor.setVisibility(View.VISIBLE);
                holder.mLlTemp.setVisibility(View.GONE);
                holder.mTvRoomName.setText("");
                holder.mTvTempRoomName.setText("");
                deviceData.setRoom_id(DeviceData.NO_ROOM_ID);
                holder.mIvLowPower.setVisibility(View.GONE);
                holder.mIvDevStatus.setImageResource(R.drawable.ic_unknown_unknown);
                holder.mTvTempStatus.setText("");
                holder.mTvDevStatus.setText("");

                if (!P2PModel.mRooms.isEmpty()) {

                    for (RoomData room : P2PModel.mRooms) {


                        for (int i : room.getDev_list()) {
                            if (i == deviceData.getDev_id()) {
                                deviceData.setRoom_id(room.getRoom_id());
                            }
                        }


                        if (room.getRoom_id() == deviceData.getRoom_id()) {
                            if (room.getRoom_name().equals("_")) {

                                continue;
                            }
                            holder.mTvRoomName.setText(room.getRoom_name());
                            holder.mTvTempRoomName.setText(room.getRoom_name());
                            continue;
                        }
                    }

                }


                switch (DeviceJudge.getTypeName(deviceData.getDev_type())) {
                    case R.string.co_sensor:


                        holder.mTvTempDevName.setText(deviceData.getName());
                        holder.mTvDevName.setText(deviceData.getName());
                        holder.mTvTempDevName.setHint(DeviceJudge.getTypeName(deviceData.getDev_type()));
                        holder.mTvDevName.setHint(DeviceJudge.getTypeName(deviceData.getDev_type()));

                        if (deviceData.getStatus() != null && !deviceData.getStatus().isEmpty()) {


                            for (DeviceStatusData statusData : deviceData.getStatus()) {
                                if (statusData.getId() == Constant.STATUS_ID_BASE_SENSOR) {
                                    int onff = DeviceJudge.statusToint(statusData.getId(), statusData.getParams());


                                    if (statusData.getParams()[3] == 1) {
                                        holder.mIvLowPower.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.mIvLowPower.setVisibility(View.GONE);

                                    }

                                    if (statusData.getParams()[2] == 1) {

                                        setDevStatusUi(holder, mContext.getString(R.string.Tampered), mContext.getResources().getColor(R.color.red), R.drawable.ic_co_sensor_warning, 3);

                                        return;
                                    }


                                    if (onff == 1)//触发状态
                                    {
                                        if (mCurMode != null)
                                            for (int i : mCurMode.getDev_list()) {
                                                if (i == deviceData.getDev_id()) {

                                                    setDevStatusUi(holder, mContext.getString(R.string.warning), mContext.getResources().getColor(R.color.red), R.drawable.ic_co_sensor_warning, 1);
                                                    return;
                                                }
                                            }
                                        setDevStatusUi(holder, mContext.getString(R.string.warning), mContext.getResources().getColor(R.color.red), R.drawable.ic_co_sensor_warning, 1);

                                    } else {
                                        holder.mTvDevStatus.setText(R.string.Normal);
                                        holder.mTvDevStatus.setTextColor(mContext.getResources().getColor(R.color.white));
                                        holder.mIvDevStatus.setImageResource(R.drawable.ic_co_sensor_working);
                                    }


                                } else if (statusData.getId() == Constant.ZB_ONLINE_AND_STATUS && deviceData.getStatus().size() == 1) {

                                    if (statusData.getParams()[0] == 1) {

                                        if (statusData.getParams()[1] == 1)//触发状态
                                        {

                                            if (mCurMode != null)
                                                for (int i : mCurMode.getDev_list()) {
                                                    if (i == deviceData.getDev_id()) {

                                                        setDevStatusUi(holder, mContext.getString(R.string.warning), mContext.getResources().getColor(R.color.red), R.drawable.ic_co_sensor_warning, 1);


                                                        return;
                                                    }
                                                }

                                            setDevStatusUi(holder, mContext.getString(R.string.warning),
                                                    mContext.getResources().getColor(R.color.red),
                                                    R.drawable.ic_co_sensor_warning,
                                                    0);


                                        } else {


                                            setDevStatusUi(holder, mContext.getString(R.string.Normal),
                                                    mContext.getResources().getColor(R.color.white),
                                                    R.drawable.ic_co_sensor_working,
                                                    0);

                                        }
                                    } else {

                                        setDevStatusUi(holder, mContext.getString(R.string.unknown),
                                                mContext.getResources().getColor(R.color.white),
                                                R.drawable.ic_co_sensor_unknown,
                                                2);

                                    }


                                } else if (statusData.getId() == Constant.ZB_STATUS_ID_LOW_VOLTAGE) {
                                    if (statusData.getParams()[0] == 1) {
                                        holder.mIvLowPower.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.mIvLowPower.setVisibility(View.GONE);

                                    }

                                }
                            }
                        } else {

                            setDevStatusUi(holder, mContext.getString(R.string.unknown),
                                    mContext.getResources().getColor(R.color.white),
                                    R.drawable.ic_co_sensor_unknown,
                                    2);

                        }

                        break;

                    case R.string.shock_sensor:

                        holder.mTvTempDevName.setText(deviceData.getName());
                        holder.mTvDevName.setText(deviceData.getName());
                        holder.mTvTempDevName.setHint(DeviceJudge.getTypeName(deviceData.getDev_type()));
                        holder.mTvDevName.setHint(DeviceJudge.getTypeName(deviceData.getDev_type()));

                        if (deviceData.getStatus() != null && !deviceData.getStatus().isEmpty()) {

                            for (DeviceStatusData statusData : deviceData.getStatus()) {
                                if (statusData.getId() == Constant.STATUS_ID_BASE_SENSOR) {
                                    int onff = DeviceJudge.statusToint(statusData.getId(), statusData.getParams());


                                    if (statusData.getParams()[3] == 1) {
                                        holder.mIvLowPower.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.mIvLowPower.setVisibility(View.GONE);

                                    }

                                    if (statusData.getParams()[2] == 1) {

                                        setDevStatusUi(holder, mContext.getString(R.string.Tampered), mContext.getResources().getColor(R.color.red), R.drawable.ic_shock_sensor_warning, 3);
                                        return;
                                    }


                                    if (onff == 1)//触发状态
                                    {

                                        if (mCurMode != null)
                                            for (int i : mCurMode.getDev_list()) {
                                                if (i == deviceData.getDev_id()) {

                                                    setDevStatusUi(holder, mContext.getString(R.string.warning),
                                                            mContext.getResources().getColor(R.color.red),
                                                            R.drawable.ic_shock_sensor_warning,
                                                            1);

                                                    return;
                                                }
                                            }

                                        setDevStatusUi(holder, mContext.getString(R.string.Shock),
                                                mContext.getResources().getColor(R.color.red),
                                                R.drawable.ic_shock_sensor_warning,
                                                0);

                                    } else {


                                        setDevStatusUi(holder, mContext.getString(R.string.Normal),
                                                mContext.getResources().getColor(R.color.white),
                                                R.drawable.ic_shock_sensor_working,
                                                0);

                                    }


                                } else if (statusData.getId() == Constant.ZB_ONLINE_AND_STATUS && deviceData.getStatus().size() == 1) {


                                    if (statusData.getParams()[0] == 1) {

                                        if (statusData.getParams()[1] == 1)//触发状态
                                        {
                                            if (mCurMode != null)
                                                for (int i : mCurMode.getDev_list()) {
                                                    if (i == deviceData.getDev_id()) {

                                                        setDevStatusUi(holder, mContext.getString(R.string.warning),
                                                                mContext.getResources().getColor(R.color.red),
                                                                R.drawable.ic_shock_sensor_warning,
                                                                1);

                                                        break;
                                                    }
                                                }

                                            setDevStatusUi(holder, mContext.getString(R.string.Shock),
                                                    mContext.getResources().getColor(R.color.red),
                                                    R.drawable.ic_shock_sensor_warning,
                                                    0);

                                        } else {

                                            setDevStatusUi(holder, mContext.getString(R.string.Normal),
                                                    mContext.getResources().getColor(R.color.white),
                                                    R.drawable.ic_shock_sensor_working,
                                                    0);
                                        }

                                    } else {
                                        setDevStatusUi(holder, mContext.getString(R.string.unknown),
                                                mContext.getResources().getColor(R.color.white),
                                                R.drawable.ic_shock_sensor_unknown,
                                                2);
                                    }

                                } else if (statusData.getId() == Constant.ZB_STATUS_ID_LOW_VOLTAGE) {
                                    if (statusData.getParams()[0] == 1) {
                                        holder.mIvLowPower.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.mIvLowPower.setVisibility(View.GONE);

                                    }

                                }
                            }
                        } else {

                            setDevStatusUi(holder, mContext.getString(R.string.unknown),
                                    mContext.getResources().getColor(R.color.white),
                                    R.drawable.ic_shock_sensor_unknown,
                                    2);
                        }

                        break;

                    case R.string.door_sensor:

                        holder.mTvTempDevName.setText(deviceData.getName());
                        holder.mTvDevName.setText(deviceData.getName());
                        holder.mTvTempDevName.setHint(DeviceJudge.getTypeName(deviceData.getDev_type()));
                        holder.mTvDevName.setHint(DeviceJudge.getTypeName(deviceData.getDev_type()));

                        if (deviceData.getStatus() != null && !deviceData.getStatus().isEmpty()) {

                            for (DeviceStatusData statusData : deviceData.getStatus()) {
                                if (statusData.getId() == Constant.STATUS_ID_BASE_SENSOR) {
                                    int onff = DeviceJudge.statusToint(statusData.getId(), statusData.getParams());

                                    if (statusData.getParams()[3] == 1) {
                                        holder.mIvLowPower.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.mIvLowPower.setVisibility(View.GONE);

                                    }

                                    if (statusData.getParams()[2] == 1) {

                                        setDevStatusUi(holder, mContext.getString(R.string.Tampered), mContext.getResources().getColor(R.color.red), R.drawable.ic_door_sensor_warning, 3);
                                        return;
                                    }

                                    if (onff == 1)//触发状态
                                    {


                                        if (mCurMode != null)
                                            for (int i : mCurMode.getDev_list()) {
                                                if (i == deviceData.getDev_id()) {

                                                    setDevStatusUi(holder, mContext.getString(R.string.warning), mContext.getResources().getColor(R.color.red), R.drawable.ic_door_sensor_warning, 1);

                                                    return;
                                                }
                                            }
                                        setDevStatusUi(holder, mContext.getString(R.string.open), mContext.getResources().getColor(R.color.red), R.drawable.ic_door_sensor_warning, 1);
                                    } else {

                                        setDevStatusUi(holder, mContext.getString(R.string.close), mContext.getResources().getColor(R.color.white), R.drawable.ic_door_sensor_working, 0);
                                    }


                                } else if (statusData.getId() == Constant.ZB_ONLINE_AND_STATUS && deviceData.getStatus().size() == 1) {

                                    if (statusData.getParams()[0] == 1) {

                                        if (statusData.getParams()[1] == 1)//触发状态
                                        {

                                            if (mCurMode != null)
                                                for (int i : mCurMode.getDev_list()) {
                                                    if (i == deviceData.getDev_id()) {

                                                        setDevStatusUi(holder, mContext.getString(R.string.warning), mContext.getResources().getColor(R.color.red), R.drawable.ic_door_sensor_warning, 1);
                                                        return;
                                                    }
                                                }


                                            setDevStatusUi(holder, mContext.getString(R.string.open), mContext.getResources().getColor(R.color.red), R.drawable.ic_door_sensor_warning, 0);

                                        } else {

                                            setDevStatusUi(holder, mContext.getString(R.string.close), mContext.getResources().getColor(R.color.white), R.drawable.ic_door_sensor_working, 0);

                                        }
                                    } else {

                                        setDevStatusUi(holder, mContext.getString(R.string.unknown), mContext.getResources().getColor(R.color.white), R.drawable.ic_door_sensor_unknown, 2);

                                    }


                                } else if (statusData.getId() == Constant.ZB_STATUS_ID_LOW_VOLTAGE) {
                                    if (statusData.getParams()[0] == 1) {
                                        holder.mIvLowPower.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.mIvLowPower.setVisibility(View.GONE);

                                    }

                                }
                            }
                        } else {

                            setDevStatusUi(holder, mContext.getString(R.string.unknown), mContext.getResources().getColor(R.color.white), R.drawable.ic_door_sensor_unknown, 2);

                        }

                        break;


                    case R.string.gas_sensor:

                        holder.mTvTempDevName.setText(deviceData.getName());
                        holder.mTvDevName.setText(deviceData.getName());
                        holder.mTvTempDevName.setHint(DeviceJudge.getTypeName(deviceData.getDev_type()));
                        holder.mTvDevName.setHint(DeviceJudge.getTypeName(deviceData.getDev_type()));

                        if (deviceData.getStatus() != null && !deviceData.getStatus().isEmpty()) {

                            for (DeviceStatusData statusData : deviceData.getStatus()) {
                                if (statusData.getId() == Constant.STATUS_ID_BASE_SENSOR) {
                                    int onff = DeviceJudge.statusToint(statusData.getId(), statusData.getParams());


                                    if (statusData.getParams()[3] == 1) {
                                        holder.mIvLowPower.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.mIvLowPower.setVisibility(View.GONE);

                                    }

                                    if (statusData.getParams()[2] == 1) {


                                        setDevStatusUi(holder, mContext.getString(R.string.Tampered), mContext.getResources().getColor(R.color.red), R.drawable.ic_gas_sensor_warning, 3);

                                        return;
                                    }

                                    if (onff == 1)//触发状态
                                    {


                                        if (mCurMode != null)
                                            for (int i : mCurMode.getDev_list()) {
                                                if (i == deviceData.getDev_id()) {

                                                    setDevStatusUi(holder, mContext.getString(R.string.warning),
                                                            mContext.getResources().getColor(R.color.red),
                                                            R.drawable.ic_gas_sensor_warning,
                                                            1);

                                                    return;
                                                }
                                            }

                                        setDevStatusUi(holder, mContext.getString(R.string.warning),
                                                mContext.getResources().getColor(R.color.red),
                                                R.drawable.ic_gas_sensor_warning,
                                                0);


                                    } else {


                                        setDevStatusUi(holder, mContext.getString(R.string.Normal),
                                                mContext.getResources().getColor(R.color.white),
                                                R.drawable.ic_gas_sensor_working,
                                                0);

                                    }

                                } else if (statusData.getId() == Constant.ZB_ONLINE_AND_STATUS && deviceData.getStatus().size() == 1) {

                                    if (statusData.getParams()[0] == 1) {

                                        if (statusData.getParams()[1] == 1)//触发状态
                                        {

                                            if (mCurMode != null)
                                                for (int i : mCurMode.getDev_list()) {
                                                    if (i == deviceData.getDev_id()) {

                                                        setDevStatusUi(holder, mContext.getString(R.string.warning),
                                                                mContext.getResources().getColor(R.color.red),
                                                                R.drawable.ic_gas_sensor_warning,
                                                                1);

                                                        return;
                                                    }
                                                }

                                            setDevStatusUi(holder, mContext.getString(R.string.warning),
                                                    mContext.getResources().getColor(R.color.red),
                                                    R.drawable.ic_gas_sensor_warning,
                                                    0);

                                        } else {

                                            setDevStatusUi(holder, mContext.getString(R.string.Normal),
                                                    mContext.getResources().getColor(R.color.white),
                                                    R.drawable.ic_gas_sensor_working,
                                                    0);

                                        }
                                    } else {

                                        setDevStatusUi(holder, mContext.getString(R.string.unknown),
                                                mContext.getResources().getColor(R.color.white),
                                                R.drawable.ic_gas_sensor_unknown,
                                                2);

                                    }


                                } else if (statusData.getId() == Constant.ZB_STATUS_ID_LOW_VOLTAGE) {
                                    if (statusData.getParams()[0] == 1) {
                                        holder.mIvLowPower.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.mIvLowPower.setVisibility(View.GONE);

                                    }

                                }
                            }
                        } else {

                            setDevStatusUi(holder, mContext.getString(R.string.unknown),
                                    mContext.getResources().getColor(R.color.white),
                                    R.drawable.ic_gas_sensor_unknown,
                                    2);

                        }

                        break;


                    case R.string.motion_sensor:


                        holder.mTvTempDevName.setText(deviceData.getName());
                        holder.mTvDevName.setText(deviceData.getName());
                        holder.mTvTempDevName.setHint(DeviceJudge.getTypeName(deviceData.getDev_type()));
                        holder.mTvDevName.setHint(DeviceJudge.getTypeName(deviceData.getDev_type()));

                        if (deviceData.getStatus() != null && !deviceData.getStatus().isEmpty()) {

                            for (DeviceStatusData statusData : deviceData.getStatus()) {
                                if (statusData.getId() == Constant.STATUS_ID_BASE_SENSOR) {
                                    int onff = DeviceJudge.statusToint(statusData.getId(), statusData.getParams());

                                    if (statusData.getParams()[3] == 1) {
                                        holder.mIvLowPower.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.mIvLowPower.setVisibility(View.GONE);

                                    }

                                    if (statusData.getParams()[2] == 1) {

                                        setDevStatusUi(holder, mContext.getString(R.string.Tampered), mContext.getResources().getColor(R.color.red), R.drawable.ic_motion_sensor_warning, 3);

                                        return;
                                    }

                                    boolean is24 = DateFormat.is24HourFormat(mContext);
                                    SimpleDateFormat formatter;
                                    if (is24) {
                                        formatter = new SimpleDateFormat("HH:mm");

                                    } else {
                                        formatter = new SimpleDateFormat("hh:mm");

                                    }

                                    Date curDate = new Date(statusData.getTime() * 1000);
                                    String str = formatter.format(curDate);
                                    StringBuffer sb = new StringBuffer();
                                    sb.append(str).append("\t").append(mContext.getResources().getString(R.string.motion));

                                    if (onff == 1)//触发状态
                                    {

                                        if (mCurMode != null)
                                            for (int i : mCurMode.getDev_list()) {
                                                if (i == deviceData.getDev_id()) {

                                                    setDevStatusUi(holder, sb.toString(), mContext.getResources().getColor(R.color.red), R.drawable.ic_motion_sensor_warning, 1);
                                                    return;
                                                }
                                            }

                                        setDevStatusUi(holder, sb.toString(), mContext.getResources().getColor(R.color.red), R.drawable.ic_motion_sensor_warning, 0);


                                    } else {

                                        setDevStatusUi(holder, mContext.getString(R.string.Normal), mContext.getResources().getColor(R.color.white), R.drawable.ic_motion_sensor_working, 0);
                                    }


                                } else if (statusData.getId() == Constant.ZB_ONLINE_AND_STATUS && deviceData.getStatus().size() == 1) {
                                    boolean is24 = DateFormat.is24HourFormat(mContext);
                                    SimpleDateFormat formatter;
                                    if (is24) {
                                        formatter = new SimpleDateFormat("HH:mm");

                                    } else {
                                        formatter = new SimpleDateFormat("hh:mm");

                                    }
                                    Date curDate = new Date(statusData.getTime() * 1000);
                                    String str = formatter.format(curDate);
                                    StringBuffer sb = new StringBuffer();
                                    sb.append(str).append("\t").append(mContext.getResources().getString(R.string.motion));

                                    if (statusData.getParams()[0] == 1) {

                                        if (statusData.getParams()[1] == 1)//触发状态
                                        {


                                            if (mCurMode != null)
                                                for (int i : mCurMode.getDev_list()) {
                                                    if (i == deviceData.getDev_id()) {

                                                        setDevStatusUi(holder, sb.toString(),
                                                                mContext.getResources().getColor(R.color.red),
                                                                R.drawable.ic_motion_sensor_warning,
                                                                1);

                                                        return;
                                                    }
                                                }


                                            setDevStatusUi(holder, sb.toString(),
                                                    mContext.getResources().getColor(R.color.red),
                                                    R.drawable.ic_motion_sensor_warning,
                                                    0);

                                        } else {

                                            setDevStatusUi(holder, mContext.getString(R.string.Normal),
                                                    mContext.getResources().getColor(R.color.white),
                                                    R.drawable.ic_motion_sensor_working,
                                                    0);
                                        }
                                    } else {

                                        setDevStatusUi(holder, mContext.getString(R.string.unknown),
                                                mContext.getResources().getColor(R.color.white),
                                                R.drawable.ic_motion_sensor_unknown,
                                                2);
                                    }


                                } else if (statusData.getId() == Constant.ZB_STATUS_ID_LOW_VOLTAGE) {
                                    if (statusData.getParams()[0] == 1) {
                                        holder.mIvLowPower.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.mIvLowPower.setVisibility(View.GONE);

                                    }

                                }
                            }
                        } else {

                            setDevStatusUi(holder, mContext.getString(R.string.unknown),
                                    mContext.getResources().getColor(R.color.white),
                                    R.drawable.ic_motion_sensor_unknown,
                                    2);
                        }

                        break;

                    case R.string.smoke_sensor:

                        holder.mTvTempDevName.setText(deviceData.getName());
                        holder.mTvDevName.setText(deviceData.getName());
                        holder.mTvTempDevName.setHint(DeviceJudge.getTypeName(deviceData.getDev_type()));
                        holder.mTvDevName.setHint(DeviceJudge.getTypeName(deviceData.getDev_type()));

                        if (deviceData.getStatus() != null && !deviceData.getStatus().isEmpty()) {

                            for (DeviceStatusData statusData : deviceData.getStatus()) {
                                if (statusData.getId() == Constant.STATUS_ID_BASE_SENSOR) {
                                    int onff = DeviceJudge.statusToint(statusData.getId(), statusData.getParams());

                                    if (statusData.getParams()[3] == 1) {
                                        holder.mIvLowPower.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.mIvLowPower.setVisibility(View.GONE);

                                    }

                                    if (statusData.getParams()[2] == 1) {

                                        setDevStatusUi(holder, mContext.getString(R.string.Tampered), mContext.getResources().getColor(R.color.red), R.drawable.ic_smoke_sensor_warning, 3);

                                        return;
                                    }

                                    if (onff == 1)//触发状态
                                    {


                                        if (mCurMode != null)
                                            for (int i : mCurMode.getDev_list()) {
                                                if (i == deviceData.getDev_id()) {

                                                    setDevStatusUi(holder, mContext.getString(R.string.warning), mContext.getResources().getColor(R.color.red), R.drawable.ic_smoke_sensor_warning, 1);

                                                    return;
                                                }
                                            }

                                        setDevStatusUi(holder, mContext.getString(R.string.warning),
                                                mContext.getResources().getColor(R.color.red),
                                                R.drawable.ic_smoke_sensor_warning, 0);

                                    } else {

                                        setDevStatusUi(holder, mContext.getString(R.string.Normal),
                                                mContext.getResources().getColor(R.color.white),
                                                R.drawable.ic_smoke_sensor_working, 0);
                                    }


                                } else if (statusData.getId() == Constant.ZB_ONLINE_AND_STATUS && deviceData.getStatus().size() == 1) {

                                    if (statusData.getParams()[0] == 1) {
                                        if (statusData.getParams()[1] == 1)//触发状态
                                        {

                                            if (mCurMode != null)
                                                for (int i : mCurMode.getDev_list()) {
                                                    if (i == deviceData.getDev_id()) {

                                                        setDevStatusUi(holder, mContext.getString(R.string.warning), mContext.getResources().getColor(R.color.red), R.drawable.ic_smoke_sensor_warning, 1);

                                                        return;
                                                    }
                                                }

                                            setDevStatusUi(holder, mContext.getString(R.string.warning),
                                                    mContext.getResources().getColor(R.color.red),
                                                    R.drawable.ic_smoke_sensor_warning, 0);

                                        } else {
                                            setDevStatusUi(holder, mContext.getString(R.string.Normal),
                                                    mContext.getResources().getColor(R.color.white),
                                                    R.drawable.ic_smoke_sensor_working, 0);
                                        }
                                    } else {

                                        setDevStatusUi(holder, mContext.getString(R.string.unknown),
                                                mContext.getResources().getColor(R.color.white),
                                                R.drawable.ic_smoke_sensor_unknown, 2);

                                    }


                                } else if (statusData.getId() == Constant.ZB_STATUS_ID_LOW_VOLTAGE) {
                                    if (statusData.getParams()[0] == 1) {
                                        holder.mIvLowPower.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.mIvLowPower.setVisibility(View.GONE);

                                    }

                                }
                            }
                        } else {

                            setDevStatusUi(holder, mContext.getString(R.string.unknown),
                                    mContext.getResources().getColor(R.color.white),
                                    R.drawable.ic_smoke_sensor_unknown, 2);
                        }

                        break;

                    case R.string.leak_sensor:

                        holder.mTvTempDevName.setText(deviceData.getName());
                        holder.mTvDevName.setText(deviceData.getName());
                        holder.mTvTempDevName.setHint(DeviceJudge.getTypeName(deviceData.getDev_type()));
                        holder.mTvDevName.setHint(DeviceJudge.getTypeName(deviceData.getDev_type()));

                        if (deviceData.getStatus() != null && !deviceData.getStatus().isEmpty()) {

                            for (DeviceStatusData statusData : deviceData.getStatus()) {
                                if (statusData.getId() == Constant.STATUS_ID_BASE_SENSOR) {
                                    int onff = DeviceJudge.statusToint(statusData.getId(), statusData.getParams());

                                    if (statusData.getParams()[3] == 1) {
                                        holder.mIvLowPower.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.mIvLowPower.setVisibility(View.GONE);

                                    }

                                    if (statusData.getParams()[2] == 1) {

                                        setDevStatusUi(holder, mContext.getString(R.string.Tampered), mContext.getResources().getColor(R.color.red), R.drawable.ic_leak_sensor_warning, 3);

                                        return;
                                    }

                                    if (onff == 1)//触发状态
                                    {

                                        if (mCurMode != null)
                                            for (int i : mCurMode.getDev_list()) {
                                                if (i == deviceData.getDev_id()) {

                                                    setDevStatusUi(holder, mContext.getString(R.string.warning)
                                                            , mContext.getResources().getColor(R.color.red)
                                                            , R.drawable.ic_leak_sensor_warning, 1);

                                                    return;
                                                }
                                            }


                                        setDevStatusUi(holder, mContext.getString(R.string.warning)
                                                , mContext.getResources().getColor(R.color.red)
                                                , R.drawable.ic_leak_sensor_warning, 0);


                                    } else {

                                        setDevStatusUi(holder, mContext.getString(R.string.Normal)
                                                , mContext.getResources().getColor(R.color.white)
                                                , R.drawable.ic_leak_sensor_working, 0);

                                    }


                                } else if (statusData.getId() == Constant.ZB_ONLINE_AND_STATUS && deviceData.getStatus().size() == 1) {

                                    if (statusData.getParams()[0] == 1) {
                                        if (statusData.getParams()[1] == 1)//触发状态
                                        {

                                            if (mCurMode != null)
                                                for (int i : mCurMode.getDev_list()) {
                                                    if (i == deviceData.getDev_id()) {

                                                        setDevStatusUi(holder, mContext.getString(R.string.warning)
                                                                , mContext.getResources().getColor(R.color.red)
                                                                , R.drawable.ic_leak_sensor_warning, 1);

                                                        return;
                                                    }
                                                }

                                            setDevStatusUi(holder, mContext.getString(R.string.warning)
                                                    , mContext.getResources().getColor(R.color.red)
                                                    , R.drawable.ic_leak_sensor_warning, 0);

                                        } else {

                                            setDevStatusUi(holder, mContext.getString(R.string.Normal)
                                                    , mContext.getResources().getColor(R.color.white)
                                                    , R.drawable.ic_leak_sensor_working, 0);
                                        }

                                    } else {

                                        setDevStatusUi(holder, mContext.getString(R.string.unknown)
                                                , mContext.getResources().getColor(R.color.white)
                                                , R.drawable.ic_leak_sensor_unknown, 2);

                                    }

                                } else if (statusData.getId() == Constant.ZB_STATUS_ID_LOW_VOLTAGE) {
                                    if (statusData.getParams()[0] == 1) {
                                        holder.mIvLowPower.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.mIvLowPower.setVisibility(View.GONE);

                                    }

                                }
                            }
                        } else {

                            setDevStatusUi(holder, mContext.getString(R.string.unknown)
                                    , mContext.getResources().getColor(R.color.white)
                                    , R.drawable.ic_leak_sensor_unknown, 2);
                        }

                        break;


                    case R.string.thermostat_sensor:

                        holder.mTvTempDevName.setText(deviceData.getName());
                        holder.mTvDevName.setText(deviceData.getName());
                        holder.mTvTempDevName.setHint(DeviceJudge.getTypeName(deviceData.getDev_type()));
                        holder.mTvDevName.setHint(DeviceJudge.getTypeName(deviceData.getDev_type()));
                        holder.mLlSensor.setVisibility(View.GONE);
                        holder.mLlTemp.setVisibility(View.VISIBLE);
                        holder.mTvTempStatus.setText("_ _℃");
                        if (deviceData.getStatus() != null && !deviceData.getStatus().isEmpty()) {

                            for (DeviceStatusData statusData : deviceData.getStatus()) {


                                if (statusData.getId() == Constant.ZB_STATUS_ID_SYSTEM_MODE) {
                                    if (statusData.getParams()[0] == 3) {
                                        Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_cooling);
                                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                        holder.mTvHum.setCompoundDrawables(null, null, drawable, null);
                                    } else if (statusData.getParams()[0] == 4) {
                                        Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_heating);
                                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                        holder.mTvHum.setCompoundDrawables(null, null, drawable, null);
                                    } else if (statusData.getParams()[0] == 7) {
                                        Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_ventilate);
                                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                        holder.mTvHum.setCompoundDrawables(null, null, drawable, null);
                                    } else if (statusData.getParams()[0] == 5) {
                                    } else if (statusData.getParams()[0] == 0) {
                                        holder.mTvTempStatus.setText(R.string.string_standby);
                                    }
                                }

                                if (statusData.getId() == Constant.ZB_ONLINE_AND_STATUS && deviceData.getStatus().size() == 1) {
                                    if (statusData.getParams()[0] == 0) {
                                        holder.mTvTempStatus.setText(R.string.Offline);

                                    } else if (statusData.getParams()[0] == 1) {
//                                        holder.mTvTempStatus.setText(R.string.Online);
                                    }
                                }


                                if (statusData.getId() == Constant.ZB_STATUS_ID_SETUP_TEMPERATURE) {
                                    holder.mTvTemp.setText(statusData.getParams()[0] + "℃");

//                                    if (deviceData.getStatus().size() == 1)
//                                        holder.mTvTempStatus.setText(mContext.getResources().getString(R.string.on));
                                }


                                if (statusData.getId() == Constant.ZB_STATUS_ID_LOCAL_TEMPERATURE) {
                                    holder.mTvTempStatus.setText(mContext.getString(R.string.string_current)+" " + statusData.getParams()[0] + "℃");

                                }


                            }
                        } else {
                            holder.mTvTempStatus.setText(R.string.unknown);
                            holder.mTvTempStatus.setTextColor(mContext.getResources().getColor(R.color.white));

                            holder.mTvDevStatus.setText(R.string.unknown);
                            holder.mTvTemp.setText("_℃");
                        }

                        break;


                    case R.string.humiture_sensor:

                        holder.mTvTempDevName.setText(deviceData.getName());
                        holder.mTvDevName.setText(deviceData.getName());
                        holder.mTvTempDevName.setHint(DeviceJudge.getTypeName(deviceData.getDev_type()));
                        holder.mTvDevName.setHint(DeviceJudge.getTypeName(deviceData.getDev_type()));
                        holder.mTvTemp.setText("_℃");
                        holder.mTvHum.setText("_%");
                        holder.mLlSensor.setVisibility(View.GONE);
                        holder.mLlTemp.setVisibility(View.VISIBLE);
                        holder.mTvHum.setCompoundDrawables(null, null, null, null);


                        if (deviceData.getStatus() != null && !deviceData.getStatus().isEmpty()) {


                            for (DeviceStatusData statusData : deviceData.getStatus()) {
                                if (statusData.getId() == Constant.ZB_STATUS_ID_TEMPERATURE || statusData.getId() == Constant.STATUS_ID_FEIBIT_SENSOR_TEMPERATURE) {

                                    holder.mTvTemp.setText(statusData.getParams()[0] + "℃");
                                }

                                if (statusData.getId() == Constant.ZB_STATUS_ID_HUMIDITY || statusData.getId() == Constant.STATUS_ID_FEIBIT_SENSOR_HUMIDITY) {

                                    holder.mTvHum.setText(statusData.getParams()[0] + "%");
                                }


                                holder.mTvTempStatus.setText(R.string.Online);

                                if (statusData.getId() == Constant.ZB_STATUS_ID_LOW_VOLTAGE) {
                                    if (statusData.getParams()[0] == 1) {
                                        holder.mIvLowPower.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.mIvLowPower.setVisibility(View.GONE);

                                    }

                                }
                            }

                        } else {

                            holder.mTvDevStatus.setText(R.string.unknown);

                        }

                        break;
                    case R.string.security_control_sensor:
                        holder.mTvDevStatus.setText(R.string.on);
                        holder.mTvDevStatus.setTextColor(mContext.getResources().getColor(R.color.white));
                        holder.mIvDevStatus.setImageResource(R.drawable.ic_remote_sensor_working);
                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mLlTemp, mLlSensor;
        private ImageView mIvDevStatus, mIvLowPower;
        private Sen5TextView mTvDevStatus, mTvRoomName;
        private Sen5TextView mTvTempStatus, mTvTemp, mTvHum, mTvTempRoomName;
        private Sen5TextView mTvTempDevName;
        private Sen5TextView mTvDevName;


        public ViewHolder(View view) {
            super(view);
            mIvDevStatus = (ImageView) view.findViewById(R.id.iv_device_status);
            mTvRoomName = (Sen5TextView) view.findViewById(R.id.tv_room_name);
            mTvDevStatus = (Sen5TextView) view.findViewById(R.id.tv_device_status);
            mTvTempStatus = (Sen5TextView) view.findViewById(R.id.tv_temp_status);
            mTvTemp = (Sen5TextView) view.findViewById(R.id.tv_temp);
            mTvHum = (Sen5TextView) view.findViewById(R.id.tv_hum);
            mTvTempRoomName = (Sen5TextView) view.findViewById(R.id.tv_temp_room_name);
            mLlTemp = (LinearLayout) view.findViewById(R.id.ll_root_temp);
            mLlSensor = (LinearLayout) view.findViewById(R.id.ll_root);
            mIvLowPower = (ImageView) view.findViewById(R.id.iv_low_power);
            mTvDevName = (Sen5TextView) view.findViewById(R.id.tv_dev_name);
            mTvTempDevName = (Sen5TextView) view.findViewById(R.id.tv_temp_dev_name);
        }
    }


    /**
     * 设置UI
     *
     * @param holder
     * @param devImg
     * @param mode   0：正常 1：报警 2：离线 3：被拆卸
     */
    private void setDevStatusUi(ViewHolder holder, String status, int color, int devImg, int mode) {

        holder.mTvDevStatus.setText(status);
        holder.mTvDevStatus.setTextColor(color);
        holder.mIvDevStatus.setImageResource(devImg);

    }


}

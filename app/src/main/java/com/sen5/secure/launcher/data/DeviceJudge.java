package com.sen5.secure.launcher.data;

import android.content.Context;

import com.sen5.secure.launcher.R;
import com.sen5.secure.launcher.base.LauncherApplication;
import com.sen5.secure.launcher.data.entity.Constant;


public class DeviceJudge {

    /**
     * 获取设备类型名称
     *
     * @param devType
     * @return
     */
    public static int getTypeName(String devType) {

        if (devType.equals(Constant.ZLL_ACTION_LIGHT_1)
                || devType.equals(Constant.ZLL_ACTION_LIGHT_2)
                || devType.equals(Constant.ZHA_ZLL_ACTION_LIGHT)
                || devType.equals(Constant.ZHA_ZLL_ACTION_LIGHT1)

//                || (Constant.ZHA_ACTION_RELAY).equals(devType)
                || (Constant.ZHA_ZLL_ACTION_DIMMABLE_LIGHT).equals(devType)
                || (Constant.ZHA_ZLL_ACTION_COLOUR_DIMMABLE_LIGHT).equals(devType)
                || (Constant.ZHA_ZLL_ACTION_COLOUR_TEMPERATURE_LIGHT).equals(devType)
                || (Constant.ZHA_ZLL_ACTION_EXTENDED_COLOUR_LIGHT).equals(devType)
                ) {
            return R.string.light_device;

        }else if (devType.equals(Constant.ZHA_ACTION_RELAY)){

            return R.string.relay;

        } else if (devType.equals(Constant.ZHA_ACTION_OUTLET_1)
                || devType.equals(Constant.ZHA_ACTION_OUTLET_2)
                || devType.equals(Constant.ZHA_ACTION_OUTLET_EU)
                || devType.equals(Constant.ZWAVE_ACTION_OUTLET)
                || (Constant.ZHA_ACTION_LIGHT_SWITCH).equals(devType)
                || (Constant.ZHA_ACTION_DIMMER_SWITCH).equals(devType)
                || (Constant.ZHA_ACTION_COLOUR_DIMMER_SWITCH).equals(devType)
                || (Constant.ZHA_ACTION_ON_OFF_PLUG_IN_UNIT).equals(devType)
                || (Constant.ZHA_ACTION_DIMMABLE_PLUG_IN_UNIT).equals(devType)
                ) {
            return R.string.outlet_device;
        } else if (devType.equals(Constant.ZHA_SENSOR_INFRARED)
                || devType.equals(Constant.ZWAVE_SENSOR_INFRARED)) {
            return R.string.motion_sensor;

        } else if (devType.equals(Constant.ZHA_SENSOR_DOOR)
                || devType.equals(Constant.ZWAVE_SENSOR_DOOR)) {
            return R.string.door_sensor;

        } else if (devType.equals(Constant.ZHA_SENSOR)) {
            return R.string.zha_sensor;

        } else if (devType.equals(Constant.ZHA_SENSOR_SMOKE)
                || devType.equals(Constant.ZWAVE_SENSOR_SMOKE)) {
            return R.string.smoke_sensor;

        } else if (devType.equals(Constant.ZHA_SENSOR_COMBUSTIBLE_GAS)
                || devType.equals(Constant.ZWAVE_SENSOR_COMBUSTIBLE_GAS)) {
            return R.string.gas_sensor;

        } else if (devType.equals(Constant.ZHA_SENSOR_CO)
                || devType.equals(Constant.ZWAVE_SENSOR_CO)) {
            return R.string.co_sensor;

        } else if (devType.equals(Constant.ZHA_SENSOR_SHOCK)) {
            return R.string.shock_sensor;

        } else if (devType.equals(Constant.ZHA_SENSOR_WATER)
                || devType.equals(Constant.ZWAVE_SENSOR_WATER)) {
            return R.string.leak_sensor;

        } else if (devType.equals(Constant.ZHA_ACTION_SECURE_RC)) {
            return R.string.security_control_sensor;

        } else if (devType.equals(Constant.ZHA_ACTION_EMERGENCY_BUTTON)) {
            return R.string.emergency_sensor;

        } else if (devType.equals(Constant.ZHA_ACTION_ALERTOR)) {
            return R.string.alertor_sensor;

        } else if (devType.equals(Constant.ZHA_SENSOR_HUMITURE)
                || devType.equals(Constant.ZWAVE_SENSOR_HUMITURE)) {
            return R.string.humiture_sensor;

        } else if (devType.equals(Constant.ZHA_SENSOR_THERMOSTAT)) {
            return R.string.thermostat_sensor;
        }
        return R.string.unknown_device;
    }

    /**
     * 将设备状态转换成字符语言
     *
     * @param statusId
     * @param params
     * @return
     */
    public static String statusToString(int statusId, byte[] params) {
        Context context = LauncherApplication.mContext;
        //设备没有状态时，认定是首次添加
        String action = context.getString(R.string.unknown);
        if (params == null || params.length < 1) {
            return action;
        }

        try {
            if (statusId == Constant.STATUS_ID_UNKNOWN) {
                action = context.getString(R.string.unknown);
            } else if (statusId == Constant.STATUS_ID_ON_OFF) {
                if (params[0] == 0) {//正常状态
                    action = context.getString(R.string.off);
                } else if (params[0] == 1) {//被触发
                    action = context.getString(R.string.on);
                }

            } else if (statusId == Constant.STATUS_ID_FEIBIT_SENSOR) {
                if (params[0] == 0) {//正常状态
                    action = context.getString(R.string.sensor_get_right);
                } else if (params[0] == 1) {//被触发
                    action = context.getString(R.string.sensor_trigger);
                }
            } else if (statusId == Constant.STATUS_ID_FEIBIT_SENSOR_TEMPERATURE) {

            } else if (statusId == Constant.STATUS_ID_FEIBIT_SENSOR_HUMIDITY) {

            } else if (statusId == Constant.STATUS_ID_DEVICE_GROUP) {

            } else if (statusId == Constant.STATUS_ID_DEVICE_DID) {

            } else if (statusId == Constant.STATUS_ID_DOOR_SENSOR) {
                if (params[0] == 0) {//正常状态
                    action = context.getString(R.string.door_close);
                } else if (params[0] == 1) {//被触发
                    action = context.getString(R.string.door_open);
                }

            } else if (statusId == Constant.STATUS_ID_LUMINANCE) {

            } else if (statusId == Constant.STATUS_ID_HOME_SECURITY) {

            } else if (statusId == Constant.STATUS_ID_ZWAVE_HUMIDITY) {

            } else if (statusId == Constant.STATUS_ID_WATER_SENSOR) {

            } else if (statusId == Constant.STATUS_ID_CO_SENSOR) {

            } else if (statusId == Constant.STATUS_ID_SMOKE_SENSOR) {

            } else if (statusId == Constant.STATUS_ID_COMBUSTIBLE_GAS_SENSOR) {

            } else if (statusId == Constant.ZB_ONLINE_AND_STATUS) {
                if (params[0] == 1) {

                    if (params[1] == 0) {//正常状态
                        action = context.getString(R.string.off);
                    } else if (params[1] == 1) {//被触发
                        action = context.getString(R.string.on);
                    }

                }

            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return action;
    }

    /**
     * 将设备状态转换成
     *
     * @param statusId
     * @param params
     * @return
     */
    public static int statusToint(int statusId, byte[] params) {
        Context context = LauncherApplication.mContext;
        //设备没有状态时，认定是首次添加
        int action = 0;
        if (params == null || params.length < 1) {
            return action;
        }

        try {
            if (statusId == Constant.STATUS_ID_UNKNOWN) {
                action = 0;
            } else if (statusId == Constant.STATUS_ID_ON_OFF) {
                if (params[0] == 0) {//正常状态
                    action = 0;
                } else if (params[0] == 1) {//被触发
                    action = 1;
                }

            } else if (statusId == Constant.STATUS_ID_FEIBIT_SENSOR) {
                if (params[0] == 0) {//正常状态
                    action = 0;
                } else if (params[0] == 1) {//被触发
                    action = 1;
                }
            } else if (statusId == Constant.STATUS_ID_FEIBIT_SENSOR_TEMPERATURE) {

            } else if (statusId == Constant.STATUS_ID_FEIBIT_SENSOR_HUMIDITY) {

            } else if (statusId == Constant.STATUS_ID_DEVICE_GROUP) {

            } else if (statusId == Constant.STATUS_ID_DEVICE_DID) {

            } else if (statusId == Constant.STATUS_ID_DOOR_SENSOR) {
                if (params[0] == 0) {//正常状态
                    action = 0;
                } else if (params[0] == 1) {//被触发
                    action = 1;
                }

            } else if (statusId == Constant.STATUS_ID_LUMINANCE) {

            } else if (statusId == Constant.STATUS_ID_HOME_SECURITY) {

            } else if (statusId == Constant.STATUS_ID_ZWAVE_HUMIDITY) {

            } else if (statusId == Constant.STATUS_ID_WATER_SENSOR) {

            } else if (statusId == Constant.STATUS_ID_CO_SENSOR) {

                if (params[0] == 0) {//正常状态
                    action = 0;
                } else if (params[0] == 1) {//被触发
                    action = 1;
                }

            } else if (statusId == Constant.STATUS_ID_SMOKE_SENSOR) {

            } else if (statusId == Constant.STATUS_ID_COMBUSTIBLE_GAS_SENSOR) {

            } else if (statusId == Constant.STATUS_ID_BASE_SENSOR) {
                if (params[0] == 1 || params[1] == 1) {
                    action = 1;
                } else {
                    action = 0;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return action;
    }

    public static String getDevAndRoom(String devType) {
        Context context = LauncherApplication.mContext;
        return getTypeName(devType) + "-" + context.getString(R.string.def_room);
    }
}

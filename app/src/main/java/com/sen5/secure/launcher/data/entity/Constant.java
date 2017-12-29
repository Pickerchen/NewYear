package com.sen5.secure.launcher.data.entity;

/**
 * Created by wanglin on 2016/7/29.
 */

public class Constant {

    public static final String WIFI_INFO = "wifi_info";
    public static final String CAMERA_INI = "/data/smarthome/camera.ini";


    /**********************Devices类型***************************/
    //Zigbee设备 ZHA：全拼ZigBee Home Automation ；ZLL:Zigbee Light Link；都是zigbee智能家居的标准
    public static final String ZLL_ACTION_LIGHT_1                         = "AC05E02100000";//ZLL灯
    public static final String ZLL_ACTION_LIGHT_2                         = "AC05E02200000";//ZLL灯
    public static final String ZHA_ZLL_ACTION_LIGHT                       = "A010402200000";//ZLL/ZHA灯
    public static final String ZHA_ZLL_ACTION_LIGHT1                      = "A010402100000";//ZLL/ZHA灯

    public static final String ZHA_ACTION_OUTLET_1                        = "A010400090000";//ZHA开关
    public static final String ZHA_ACTION_OUTLET_2                        = "A010400020000";//ZHA开关
    public static final String ZHA_ACTION_OUTLET_EU                       = "A010400510000";//ZHA开关——欧标
    public static final String ZHA_ACTION_RELAY                           = "A010401000000";//ZHA继电器
    public static final String ZHA_ACTION_SECURE_RC                       = "A010404020115";//ZHA安防遥控器
    public static final String ZHA_ACTION_EMERGENCY_BUTTON                = "A01040402002C";//ZHA紧急按钮
    public static final String ZHA_ACTION_ALERTOR                         = "A010404030225";//ZHA报警器


    public static final String ZHA_ZLL_ACTION_DIMMABLE_LIGHT              = "A010401010000";//ZLL灯
    public static final String ZHA_ZLL_ACTION_COLOUR_DIMMABLE_LIGHT       = "A010401020000";//ZLL灯
    public static final String ZHA_ZLL_ACTION_COLOUR_TEMPERATURE_LIGHT    = "A0104010C0000";//ZLL灯
    public static final String ZHA_ZLL_ACTION_EXTENDED_COLOUR_LIGHT       = "A0104010D0000";//ZLL灯




    public static final String ZHA_ACTION_LIGHT_SWITCH                    = "A010401030000";//ZHA开关
    public static final String ZHA_ACTION_DIMMER_SWITCH 				  = "A010401040000";//ZHA开关
    public static final String ZHA_ACTION_COLOUR_DIMMER_SWITCH            = "A010401050000";//ZHA开关
    public static final String ZHA_ACTION_ON_OFF_PLUG_IN_UNIT 			  = "A0104010A0000";//ZHA开关
    public static final String ZHA_ACTION_DIMMABLE_PLUG_IN_UNIT           = "A0104010B0000";//ZHA开关



    public static final String ZHA_SENSOR                                 = "A010404020000";//ZHA传感器
    public static final String ZHA_SENSOR_DOOR                            = "A010404020015";//ZHA门磁传感器
    public static final String ZHA_SENSOR_INFRARED                        = "A01040402000D";//ZHA红外传感器
    public static final String ZHA_SENSOR_SMOKE                           = "A010404020028";//ZHA烟雾传感器
    public static final String ZHA_SENSOR_COMBUSTIBLE_GAS                 = "A01040402002B";//ZHA易燃气体传感器
    public static final String ZHA_SENSOR_CO                              = "A010404028001";//ZHA一氧化碳传感器
    public static final String ZHA_SENSOR_SHOCK                           = "A01040402002D";//ZHA震动传感器
    public static final String ZHA_SENSOR_WATER                           = "A01040402002A";//ZHA水浸传感器
    public static final String ZHA_SENSOR_HUMITURE                        = "A010403020000";//ZHA温湿度传感器
    public static final String SENSOR_BOX_ALERTOR                         = "B000000000001";//盒子自带报警器
    public static final String ZHA_SENSOR_THERMOSTAT                      = "A010403010000";//恒温器

    //ZWave设备
    public static final String ZWAVE_ACTION_OUTLET                        = "CP04100100000";//插座

    public static final String ZWAVE_SENSOR_DOOR  	                      = "CA04070106070";//门磁传感器
    public static final String ZWAVE_SENSOR_CO  	                      = "CA04070102000";//一氧化碳传感器
    public static final String ZWAVE_SENSOR_WATER                         = "CA04070105070";//水浸传感器
    public static final String ZWAVE_SENSOR_SMOKE	                      = "CA04200101000";//烟雾传感器
    public static final String ZWAVE_SENSOR_INFRARED  	                  = "CA04070107000";//红外传感器
    public static final String ZWAVE_SENSOR_COMBUSTIBLE_GAS               = "CA04070112000";//气体传感器
    public static final String ZWAVE_SENSOR_HUMITURE	                  = "CS04210101050";//温湿度传感器
    public static final String ZWAVE_MULTI_DEVICE	                      = "CM04070100000";//多功能设备

    //摄像头
    public static final String DEV_IP_CAMERA	                          = "B000000000002";//摄像头


    /************************************设备状态ID*******************************************/
    public static final int STATUS_ID_UNKNOWN                           = 0;//未知状态
    public static final int STATUS_ID_ON_OFF                            = 1;//开/关状态
    public static final int STATUS_ID_FEIBIT_SENSOR                     = 2;//feibit传感器状态
    public static final int STATUS_ID_FEIBIT_SENSOR_TEMPERATURE         = 3;//feibit传感器温度状态
    public static final int STATUS_ID_FEIBIT_SENSOR_HUMIDITY            = 4;//feibit传感器湿度状态
    public static final int STATUS_ID_DEVICE_GROUP                      = 5;//device_group
    public static final int STATUS_ID_DEVICE_DID                        = 6;//摄像头的did
    public static final int STATUS_ID_DOOR_SENSOR                       = 7;//门磁传感器状态
    public static final int STATUS_ID_LUMINANCE                         = 8;//亮度状态
    public static final int STATUS_ID_HOME_SECURITY                     = 9;//家庭安全状态
    public static final int STATUS_ID_ZWAVE_HUMIDITY                    = 10;//zwave传感器湿度状态
    public static final int STATUS_ID_WATER_SENSOR                      = 11;//水浸状态
    public static final int STATUS_ID_CO_SENSOR                         = 12;//Co传感器状态
    public static final int STATUS_ID_SMOKE_SENSOR                      = 13;//烟雾状态
    public static final int STATUS_ID_COMBUSTIBLE_GAS_SENSOR            = 14;//易燃气体状态
    public static final int STATUS_ID_LIGHT_BRIGHTNESS                  = 15;//灯的亮度
    public static final int STATUS_ID_LIGHT_HUES                        = 16;//灯的色相
    public static final int STATUS_ID_LIGHT_SATURATION                  = 17;//灯饱和度
    public static final int STATUS_ID_BASE_SENSOR                       = 64;//基础传感器状态
    public static final int ZB_STATUS_ID_TEMPERATURE                    = 61;//温度值
    public static final int ZB_STATUS_ID_HUMIDITY                       = 62;//湿度值
    public static final int ZB_STATUS_ID_LOW_VOLTAGE                    = 53;//设备电量过低
    public static final int ZB_STATUS_ID_LOCAL_TEMPERATURE              = 56;//当前温度
    public static final int ZB_STATUS_ID_SETUP_TEMPERATURE              = 57;//目标温度
    public static final int ZB_STATUS_ID_SYSTEM_MODE                    = 58;//系统模式
    public static final int ZB_ONLINE_AND_STATUS                        = 67;//ZB senser on line and UIStatus


    /******************************p2p通信*******************************/
    public static final String MSG_TYPE = "msg_type";
    // 超级用户->盒子端msg_type

    //1、设备操作
    public static final int MSG_LIST_DEVICE                           = 101;
    public static final int MSG_EDIT_DEVICE                           = 102;
    public static final int MSG_ADD_DEVICE 			                  = 103;
    public static final int MSG_DELETE_DEVICE                         = 104;
    public static final int MSG_CONTROL_DEVICE 			              = 105;
    public static final int MSG_REQUEST_SINGLE_DEVICE_STATUS          = 106;
    public static final int MSG_REQUEST_ALL_DEVICE_STATUS             = 107;

    //2、房间操作
    public static final int MSG_LIST_ROOM                             = 201;
    public static final int MSG_NEW_ROOM                              = 202;
    public static final int MSG_DELETE_ROOM                           = 203;
    public static final int MSG_EDIT_ROOM                             = 204;
    //3、场景操作
    public static final int MSG_LIST_SCENE                            = 301;
    public static final int MSG_NEW_SCENE                             = 302;
    public static final int MSG_DELETE_SCENE                          = 303;
    public static final int MSG_EDIT_SCENE                            = 304;
    public static final int MSG_APPLY_SCENE                           = 305;
    //4、模式
    public static final int MSG_LIST_MODE                             = 401;
    public static final int MSG_EDIT_MODE                             = 402;
    public static final int MSG_APPLY_MODE                            = 403;
    //5、收藏
    public static final int MSG_EDIT_FAVORITE                         = 501;
    public static final int MSG_LIST_FAVORITE                         = 502;
    //6、成员
    public static final int MSG_IDENTITY_VERIFY                       = 601; //验证身份
    public static final int MSG_LIST_USER                             = 602;
    public static final int MSG_ADD_USER                              = 603;
    public static final int MSG_DELETE_USER                           = 604;
    public static final int MSG_RENAME_USER                           = 608;
    //日志
    public static final int MSG_REQUEST_ALL_DEVICE_LOG                = 801;
    public static final int MSG_REQUEST_SINGLE_DEVICE_LOG             = 802;
    public static final int MSG_REQUEST_ALERTOR_LOG                   = 803;



    //盒子端-->手机端msg_type
    public static final int MSG_DATABASE_VERSION_RESPOND	          = 1;
    //心跳包
    public static final int MSG_HEARTBEAT_PACKETS_RESPOND             = 3;

    //1、设备
    public static final int MSG_LIST_DEVICE_RESPOND                   = 101;
    public static final int MSG_EDIT_DEVICE_RESPOND	                  = 102;
    public static final int MSG_ADD_DEVICE_RESPOND                    = 103;
    public static final int MSG_DELETE_DEVICE_RESPOND                 = 104;
    public static final int MSG_CONTROL_DEVICE_RESPOND                = 105;
    public static final int MSG_REPORT_DEVICE_STATUS_RESPOND          = 106;
    public static final int MSG_REQUEST_ALL_DEVICE_STATUS_RESPOND     = 107;
    //2、房间
    public static final int MSG_LIST_ROOM_RESPOND                     = 201;
    public static final int MSG_NEW_ROOM_RESPOND                      = 202;
    public static final int MSG_DELETE_ROOM_RESPOND		              = 203;
    public static final int MSG_EDIT_ROOM_RESPOND                     = 204;
    //3、场景
    public static final int MSG_LIST_SCENE_RESPOND                    = 301;
    public static final int MSG_NEW_SCENE_RESPOND                     = 302;
    public static final int MSG_DELETE_SCENE_RESPOND                  = 303;
    public static final int MSG_EDIT_SCENE_RESPOND                    = 304;
    public static final int MSG_APPLY_SCENE_RESPOND                   = 305;
    //4、模式
    public static final int MSG_LIST_MODE_RESPOND                     = 401;
    public static final int MSG_EDIT_MODE_RESPOND                     = 402;
    public static final int MSG_APPLY_MODE_RESPOND                    = 403;
    //5、收藏
    public static final int MSG_EDIT_FAVORITE_RESPOND                 = 501;
    public static final int MSG_LIST_FAVORITE_RESPOND                 = 502;
    //6、成员
    public static final int MSG_IDENTITY                              = 601; //验证身份
    public static final int MSG_LIST_USER_RESPOND                     = 602;
    public static final int MSG_ADD_USER_RESPOND                      = 603;
    public static final int MSG_DELETE_USER_RESPOND                   = 604;
    public static final int MSG_REQUEST_ADD_USER                      = 605;
    public static final int MSG_IDENTITY_LEGAL_RESPOND                = 606;
    public static final int MSG_RENAME_USER_RESPOND                   = 608;

    public static final int MSG_IDENTITY_NOT_LEGAL_RESPOND            = 701;

    //日志
    public static final int MSG_REQUEST_ALL_DEVICE_LOG_RESPOND        = 801;
    public static final int MSG_REQUEST_SINGLE_DEVICE_LOG_RESPOND     = 802;
    public static final int MSG_REQUEST_ALERTOR_LOG_RESPOND           = 803;

}

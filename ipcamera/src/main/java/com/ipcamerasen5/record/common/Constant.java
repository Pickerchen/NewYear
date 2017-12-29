package com.ipcamerasen5.record.common;

import android.os.Environment;

import java.io.File;

/**
 * Created by chenqianghua on 2017/4/5.
 */

public class Constant {

    //imgpathByLauncher
    public static String imgPathByLauncher = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "IPCamera"+File.separator+"lastFrame"+File.separator;
    //path Constant
    public static  String  commonPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "IPCamera";
    public static  String  recordPath = commonPath+File.separator+"record";
    public static  String  thumpPath = commonPath+File.separator+"thump";//视频缩略图地址
    public static final String cameraDeviceFilePath = "/data/smarthome/camera.ini";//存放did的文件路径

    //sensor触发开启录制的广播：广播由secQre setting发出
    public static String sensorBroadCast = "com.sen5.process.camera.key";
    public static final String sensorBroadCast_change = "com.sen5.process.camera.sensor";
    public static String tenSecondVideoBroadCast = "com.sen5.process.camera.video";
    public static final String homeAction = "com.amlogic.dvbplayer.homekey";
    public static final String cameraChange = "com.sen5.launcher.camerachange";
//    public static final String cameraChange = "com.sen5.smartlife.camerachange";
    public static final String floatWinClose = "com.sen5.process.camera.close.float";

    //设备状态
    public static final int connecting = 0;//正在连接
    public static final int conncted = 100;//设备在线
    public static final int connect_error = 101;//设备连接失败
    public static final int moreThanMax = 2;//超过最大可连接用户数量
    public static final int userId_error = 3;//id错误
    public static final int did_error = 5;//did不可使用
    public static final int offline = 9;//设备不在线
    public static final int timeout = 10;//连接超时
    public static final int disconnected = 11;//断开连接
    public static final int judgeAccount = 12;//校验账号

    //视频录制类型
    public static final int type_clock = 0;//闹钟录制
    public static final int type_sensor = 2;//触发录制
    public static final int type_round = 1;//24小时录制
    public static final int type_move = 3;//移动侦测录制

    //设备状态
    public static final int  status_offLine = 0;//掉线状态status_offLine
    public static final int status_recording = 2;//录制状态status_recording
    public static final int status_onLine = 1;//在线状态status_onLine

    //shareprefrence
    public static String timeGap = "TimeGap";//时差
    public static String isFirst = "isFirst";//数据库是否第一次初始化
    public static String shouldOpenRecordService = "false";//是否需要开启录制服务
    public static String isFirst_true = "yes";
    public static String isFirst_false = "no";
    public static String lastStorage = "lastStorage";//上次退出前录制的盘符路径
    public static String lastImagePath = "lastImagePathFirst";//摄像头一上次截图
    public static String lastImagePath2 = "lastImagePathSecond";
    public static String lastImagePath3 = "lastImagePathThird";
    public static String lastImagePath4 = "lastImagePathFourth";
    //闹钟广播action
    public static final String ALARM_ACTION = "com.sen5.record.alarm";

    //设备的状态
    public static final String Normal = "Normal";
    public static final String OffLine = "OffLine";
    public static final String Recording = "Recording";

    //共享数据字段
    public static final String DB_FIELD_MODE = "mode";//0：用来播放的id>>>1:用来录制的id

    //packgeName
    public static final String PACKGENAME = "com.ipcamerasen5.main";

}

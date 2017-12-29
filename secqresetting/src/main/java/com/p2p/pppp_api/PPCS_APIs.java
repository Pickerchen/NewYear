package com.p2p.pppp_api;

public class PPCS_APIs {
    public static int ms_verAPI = 0;

    public static final int ERROR_PPCS_SUCCESSFUL = 0;

    public static final int ERROR_PPCS_NOT_INITIALIZED = -1;
    public static final int ERROR_PPCS_ALREADY_INITIALIZED = -2;
    public static final int ERROR_PPCS_TIME_OUT = -3;
    public static final int ERROR_PPCS_INVALID_ID = -4;
    public static final int ERROR_PPCS_INVALID_PARAMETER = -5;
    public static final int ERROR_PPCS_DEVICE_NOT_ONLINE = -6;
    public static final int ERROR_PPCS_FAIL_TO_RESOLVE_NAME = -7;
    public static final int ERROR_PPCS_INVALID_PREFIX = -8;
    public static final int ERROR_PPCS_ID_OUT_OF_DATE = -9;
    public static final int ERROR_PPCS_NO_RELAY_SERVER_AVAILABLE = -10;
    public static final int ERROR_PPCS_INVALID_SESSION_HANDLE = -11;
    public static final int ERROR_PPCS_SESSION_CLOSED_REMOTE = -12;
    public static final int ERROR_PPCS_SESSION_CLOSED_TIMEOUT = -13;
    public static final int ERROR_PPCS_SESSION_CLOSED_CALLED = -14;
    public static final int ERROR_PPCS_REMOTE_SITE_BUFFER_FULL = -15;
    public static final int ERROR_PPCS_USER_LISTEN_BREAK = -16;
    public static final int ERROR_PPCS_MAX_SESSION = -17;
    public static final int ERROR_PPCS_UDP_PORT_BIND_FAILED = -18;
    public static final int ERROR_PPCS_USER_CONNECT_BREAK = -19;
    public static final int ERROR_PPCS_SESSION_CLOSED_INSUFFICIENT_MEMORY = -20;
    public static final int ERROR_PPCS_INVALID_APILICENSE = -21;

    public static final int ER_ANDROID_NULL = -5000;

    /**
     * 获取api版本
     * @return
     */
    public native static int PPCS_GetAPIVersion();

    /**
     * 通过设备名字获取设备的DID
     * @param DeviceName
     * @param DID
     * @param DIDBufSize
     * @return
     */
    public native static int PPCS_QueryDID(String DeviceName, String DID, int DIDBufSize);

    /**
     * 初始化PPCS session module
     * @param Parameter
     * @return
     */
    public native static int PPCS_Initialize(byte[] Parameter);

    /**
     * 释放所有被PPCS session module使用的资源
     * @return
     */
    public native static int PPCS_DeInitialize();

    /**
     * 检测网络相关的信息
     * @param NetInfo
     * @param UDP_Port
     * @return
     */
    public native static int PPCS_NetworkDetect(st_PPCS_NetInfo NetInfo, int UDP_Port);

    public native static int PPCS_NetworkDetectByServer(st_PPCS_NetInfo NetInfo, int UDP_Port, String ServerString);

    /**
     * 是否允许设备执行转播服务
     * @param bOnOff
     * @return
     */
    public native static int PPCS_Share_Bandwidth(byte bOnOff);

    /**
     * 等待一个客户端的连接
     * @param MyID
     * @param TimeOut_sec
     * @param UDP_Port
     * @param bEnableInternet
     * @param APILicense
     * @return
     */
    public native static int PPCS_Listen(String MyID, int TimeOut_sec, int UDP_Port, byte bEnableInternet, String APILicense);

    /**
     * 断开PPCS_Listen
     * @return
     */
    public native static int PPCS_Listen_Break();

    /**
     * 检查设备的登陆状态
     * @param bLoginStatus 设备登陆状态：0：没有登录到服务器； 1：成功登陆到服务器
     * @return
     */
    public native static int PPCS_LoginStatus_Check(byte[] bLoginStatus);

    /**
     * 连接到目标设备
     * @param TargetID
     * @param bEnableLanSearch
     * @param UDP_Port
     * @return
     */
    public native static int PPCS_Connect(String TargetID, byte bEnableLanSearch, int UDP_Port);

    public native static int PPCS_ConnectByServer(String TargetID, byte bEnableLanSearch, int UDP_Port, String ServerString);

    /**
     * 断开PPCS_Connect连接
     * @return
     */
    public native static int PPCS_Connect_Break();

    /**
     * 检测session信息
     * @param SessionHandle
     * @param SInfo 接收session信息的结构体
     * @return
     */
    public native static int PPCS_Check(int SessionHandle, st_PPCS_Session SInfo);

    public native static int PPCS_Close(int SessionHandle);

    public native static int PPCS_ForceClose(int SessionHandle);

    public native static int PPCS_Write(int SessionHandle, byte Channel, byte[] DataBuf, int DataSizeToWrite);

    public native static int PPCS_Read(int SessionHandle, byte Channel, byte[] DataBuf, int[] DataSize, int TimeOut_ms);

    public native static int PPCS_Check_Buffer(int SessionHandle, byte Channel, int[] WriteSize, int[] ReadSize);

    static {
        try {
            System.loadLibrary("PPCS_API");
            ms_verAPI = PPCS_GetAPIVersion();
        } catch (UnsatisfiedLinkError ule) {
            System.out.println("loadLibrary PPCS_API lib," + ule.getMessage());
        }
    }
}

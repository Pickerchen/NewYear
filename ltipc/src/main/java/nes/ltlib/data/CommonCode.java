package nes.ltlib.data;

public class CommonCode {
	public static final int VIDEO_SIZE_CHANGE 	= 15;	//窗口大小改变
	public static final int VIDEO_ONCONNECTING 	= 16;	//正在连接
	public static final int VIDEO_ONCONNECTED 	= 17;	//已经连接上设备，准备发送登录指令
	public static final int VIDEO_ONAUTH		= 18;	//登录设备返回结果
	public static final int VIDEO_ONDATARATE 	= 19;	//手机跟设备交互实时码流大小
	public static final int VIDEO_ONDISCONN 	= 20;	//手机跟设备连接断开
	public static final int VIDEO_MODE_CHG 		= 21;	//连接方式转换，可能由转发跟直连(p2p),切换,这个只是提示，不用做任何操作
	public static final int VIDEO_RECONNING 	= 22;	//手机跟设备重连中
	public static final int VIDEO_IOCTRL	 	= 23;	//私有协议返回
	public static final int VIDEO_IOCTRL_MANU	= 24;	//透明通道数据返回
	public static final int VIDEO_KEEP_LIVE		= 25;	//透明通道保持心跳包，防止超时连接断开
	
	//设备端SD卡录像回放,根据文件列表方式回放
	public static final int PB_FILE_ITEM_RSP1	= 25;	//文件列表返回
	public static final int PB_FILE_SEARCH_RSP1	= 26;	//文件列表数量返回，由设备统计的数量
	public static final int PB_FILE_PLAY_RSP1	= 27;	//开始播放某个文件，播放结果返回
	public static final int PB_FILE_PLAY_END1	= 28;	//设备端回调当前文件播放结束，需要手动进行下一个文件播放
	
	//设备端SD卡录像回放,根据时间段方式回放
	public static final int PB_FILE_ITEM_RSP2	= 45;	//时间段列表返回
	public static final int PB_FILE_SEARCH_RSP2	= 46;	//时间段列表数量返回，由设备统计的数量
	public static final int PB_FILE_PLAY_RSP2	= 47;	//开始播放某个时间点录像
	public static final int PB_FILE_PLAY_CUT2	= 48;	//设备端回调当前播放中断，需要手动请求传入最近播放时间戳进行下一次播放
	
}

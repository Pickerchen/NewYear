package com.sen5.smartlifebox.data.p2p;

import com.p2p.pppp_api.AVFrameHead;
import com.p2p.pppp_api.AVIOCtrlHead;
import com.p2p.pppp_api.AVStreamIOHead;
import com.p2p.pppp_api.AVStreamIO_Proto;
import com.p2p.pppp_api.PPCS_APIs;
import com.p2p.pppp_api.st_PPCS_NetInfo;
import com.p2p.pppp_api.st_PPCS_Session;
import com.sen5.smartlifebox.common.utils.Utils;
import nes.ltlib.utils.AppLog;

import java.util.Arrays;

public class P2PClient {
	
	public static final int P2P_NOT_CONNECT = -1;
	public static final int P2P_CONNECTING = 0;
	public static final int P2P_CONNECTED = 1;
	/** Buffer 64*1024; */
	private static final int MAX_SIZE_BUF = 65536;
	/** 数据包头长度 */
	private static final int PACKAGE_HEAD_LEN = 2;
	/** 数据长度的长度，为2个字节 */
	private static final int PACKAGE_LENGTH_LEN = 4;
	/** 数据包尾长度 */
	private static final int PACKAGE_END_LEN = 2;
	/** 包头 */
	private static final String PACKAGE_HEAD = "##";
	/** 包尾 */
	private static final String PACKAGE_END = "!!";
	
	/** 读取数据超时设置 */
	private static final int TIME_OUT = 300000;
	/** 读取数据的Channel ID，PPCS提供8个Channel，0~7，要与服务端的write channel对应 */
	private static final byte CHANNEL_READ = 2;
	/** 发送数据的Channel ID，PPCS提供8个Channel，0~7，要与服务端的read channel对应 */
	private static final byte CHANNEL_WRITE = 1;
	/** 告知服务器的参数 */
	private static final String P2P_PARAM_DEFAULT = "BIHLBDBIKAJKHJJJAPGJBMFNDGJOGPJIHLEPFKCIFCIBKPKFDFBPHHPBDCLNMMOAAFJOLBDHLNNAFGCBJFNNIIAAIMPMEBCKEPGDDKFJMOOO";
	private static final String P2P_PARAM_SLIFE = "ADHOAFAJPFMPCNNCBIHOBAFHDMNFGJJCHDAGFHCFEAIHOLKFDHADCLPBCJLLMMKBEIJCLDHGPJMLEMCDMGMMNOEIIGLHENDLEDCIHNBOMKKFFMCBBH";
	/** 设备的UID，唯一的 */
	private String mDID = "";
	private ReadDataThread mReadDataThread = null;
	/** 初始化P2P的结果 */
	private int mInitRet = PPCS_APIs.ERROR_PPCS_ALREADY_INITIALIZED;
	/** 客户端的回话句柄 【结果】 大于等于0：成功；小于0：失败； */
	private int mSessionHandle = -1;
	private int mConnStatus = P2P_NOT_CONNECT;
	private boolean mPermitRead = false;
	private boolean isInitP2P = false;

	private OnP2PClientListener mP2PClientListener;
	private st_PPCS_Session mPPCS_Session;
	private st_PPCS_NetInfo mSt_ppcs_netInfo;

	public P2PClient() {
		this("");
	}
	
	public P2PClient(String uid) {
		mPPCS_Session = new st_PPCS_Session();
		mSt_ppcs_netInfo = new st_PPCS_NetInfo();
		setKey(uid);
	}

	/**
	 * 初始化，初始化后需要调用{@link ListenClientThread}
	 * @param uidType 连接对象的Id的类型
	 * @return 初始化的结果
	 */
	public int initP2P() {
		mConnStatus = P2P_NOT_CONNECT;

		if(mDID.contains("SLIFE")){
			mInitRet = PPCS_APIs.PPCS_Initialize(P2P_PARAM_SLIFE.getBytes());
		}else {
			mInitRet = PPCS_APIs.PPCS_Initialize(P2P_PARAM_DEFAULT.getBytes());
		}
		isInitP2P = true;
		AppLog.d("Initialize Result == " + mInitRet);
		return mInitRet;
	}

	/**
	 * 释放所有初始化的资源
	 */
	public void delInitP2P() {
		disconnectDevice();
		PPCS_APIs.PPCS_DeInitialize();
		isInitP2P = false;
	}


	/**
	 * 设置监听事件
	 * @param listener
	 */
	public void setOnP2PClientListener(OnP2PClientListener listener) {
		
		mP2PClientListener = listener;
	}
	
	/**
	 * 设置Key：ID + license
	 * @param did 格式：SLIFE-000000-XYYEJ
	 * @param license 格式：GELMHM
	 */
	public void setKey(String did) {
		mDID = did;
	}

	public boolean isInitP2P(){
		return isInitP2P;
	}

	/**
	 * 设置连接状态
	 * @param status
	 * @deprecated 最好不要在外部设置此值
	 */
	public void setConnectStatus(int status) {
		mConnStatus = status;
	}
	
	/**
	 * 获取连接状态
	 * @return
	 */
	public int getConnectStatus() {
		return mConnStatus;
	}
	
	/**
	 * 获取P2P初始化的结果
	 * @return
	 */
	public int getInitP2PResult() {
		
		return mInitRet;
	}
	
	/**
	 * 获取当前的SessionHandle
	 * @return
	 */
	public int getSessionHandle() {
		
		return mSessionHandle;
	}
	
	/**
	 * 获取连接方式
	 * @return
	 */
	public int getConnectMode() {
		
		if(mSessionHandle >= 0) {
			return mPPCS_Session.getMode();			
		} else {
			return -1;
		}
	}
	
	public String getDID(){
		return mDID;
	}

	/**
	 * 检测网络信息，包括LANIP、WANIP等
	 * @return
	 */
	public st_PPCS_NetInfo checkNetStatus(){
		PPCS_APIs.PPCS_NetworkDetect(mSt_ppcs_netInfo, 0);
		return  mSt_ppcs_netInfo;
	}

	/**
	 * 连接设备
	 */
	public void connectDevice() {
		if(null == mDID || mDID.equals("")) {
			AppLog.e("DeviceUID == null");
			return;
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mConnStatus = P2P_CONNECTING;
				AppLog.e("connectting... == " + mDID);
				
				//连接设备，得到对话句柄
				mSessionHandle = PPCS_APIs.PPCS_Connect(mDID, (byte)1, 0);
				AppLog.e("connectting... == " + mSessionHandle);
				
				if(mSessionHandle >= 0) {//连接成功
					AppLog.d("client connect successful sessionhandle == " + mSessionHandle);
					mConnStatus = P2P_CONNECTED;
					if(mP2PClientListener != null){
						mP2PClientListener.onP2PConnSuccess();
					}
					
					//连接成功后就调用readData()启动一个线程，并在线程里面开启一个无限循环，用来监听服务端的推送给自己的信息
					readData();
					
					int nCheckRet = PPCS_APIs.PPCS_Check(mSessionHandle, mPPCS_Session);
					AppLog.d("Client Check Result == " + nCheckRet);
					if(nCheckRet == PPCS_APIs.ERROR_PPCS_SUCCESSFUL) {
						AppLog.d("Communication mode == " + (mPPCS_Session.getMode() == 0 ? "P2P" : "Relay"));
					}
				} else {//连接失败
					AppLog.e("client connect failure sessionhandle == " + mSessionHandle);
					mConnStatus = P2P_NOT_CONNECT;
					
					if(null != mP2PClientListener) {
						mP2PClientListener.onP2PConnFail();
					}
				}
			}
		}, "ConnectDev").start();
	}
	
	/**
	 * 检测连接的状态
	 * <p>只有在大于等于0的情况下才是连接状态</p>
	 * @return
	 */
	public int checkConnectStatus() {
		
		int nRet = PPCS_APIs.ERROR_PPCS_INVALID_SESSION_HANDLE;
		if(mSessionHandle >= 0) {
			nRet = PPCS_APIs.PPCS_Check(mSessionHandle, mPPCS_Session);
			mConnStatus = P2P_CONNECTING;
			AppLog.d("Connect Check Status == " + nRet + "::" + (mPPCS_Session.getMode() == 0 ? "P2P" : "Relay"));
			if(nRet < 0) {
				disconnectException();
			}else{
				mConnStatus = P2P_CONNECTED;
			}
//			int nRet1 = sendData(buildPackageData("test"));
//			AppLog.d("Connect Write Status == " + nRet1);
		}
		return nRet;
	}

	/**
	 * 断开连接设备，断掉读取的线程
	 */
	public int disconnectDevice() {
		mConnStatus = P2P_NOT_CONNECT;
		int nRet = PPCS_APIs.ER_ANDROID_NULL;
		if(mSessionHandle >= 0) {
			// 停掉视频
			sendIOCtrl(mSessionHandle, AVStreamIO_Proto.IOCTRL_TYPE_AUDIO_STOP, null, 0);
			sendIOCtrl(mSessionHandle, AVStreamIO_Proto.IOCTRL_TYPE_VIDEO_STOP, null, 0);

			nRet = PPCS_APIs.PPCS_Close(mSessionHandle);
			mSessionHandle = -1;
		}
		PPCS_APIs.PPCS_Connect_Break();
		disReadThread();
		disRecvAVDataThread();
		return nRet;
	}

	/**
	 * 异常断开
	 */
	private void disconnectException() {
		mConnStatus = P2P_NOT_CONNECT;
		disReadThread();
		disRecvAVDataThread();
		mSessionHandle = -1;
		if(null != mP2PClientListener) {
			mP2PClientListener.onP2PDisconnect();
		}
	}

	/**
	 * 组建数据包，暂时不考虑分包的现象
	 * @param validData
	 */
	private byte[] buildPackageData(String validData) {
		
		// 有效数据长度
		int dataLen = validData.getBytes().length;
		int packetLen = PACKAGE_HEAD_LEN + PACKAGE_LENGTH_LEN + dataLen + PACKAGE_END_LEN;
		byte[] packet = new byte[packetLen];
		Arrays.fill(packet, (byte)0);
//		// 包头
		System.arraycopy(PACKAGE_HEAD.getBytes(), 0, packet, 0, PACKAGE_HEAD_LEN);
//		// 数据长度(大端传输)
//		packet[2] = (byte)(dataLen >>> 8);
//		packet[3] = (byte)dataLen;
//		// 有效数据
//		System.arraycopy(validData.getBytes(), 0, packet, 4, dataLen);
//		// 包尾
//		System.arraycopy(PACKAGE_END.getBytes(), 0, packet, 4 + dataLen, PACKAGE_END_LEN);

		packet[2] = (byte)(dataLen >>> 24);
        packet[3] = (byte)(dataLen >>> 16);
        packet[4] = (byte)(dataLen >>> 8);
        packet[5] = (byte)dataLen;
        // 有效数据
        System.arraycopy(validData.getBytes(), 0, packet, 6, dataLen);
        // 包尾
        System.arraycopy(PACKAGE_END.getBytes(), 0, packet, 6 + dataLen, PACKAGE_END_LEN);
		return packet;
	}
	
	/**
	 * 发送数据，注意：会启 动一个Thread去出传送数据
	 * @param data 
	 * @param handleSessions 不填写，默认为连接的device
	 * @return < 0 :对应错误状态 ；>=0 : 发送字节数
	 */
		
	public int sendData(String data, int... handleSessions) {
		byte[] packet = buildPackageData(data);

		int handleSession = mSessionHandle;
		if(null != handleSessions && handleSessions.length > 0) {
			handleSession = handleSessions[0];
		}
		int size = packet.length;
		AppLog.d("Send Data == " + packet.toString() + "  Size == " + size);
		String str = new String(packet);
		AppLog.d("Send Data == " + str + "  Size == " + size);
		return PPCS_APIs.PPCS_Write(handleSession, CHANNEL_WRITE, packet, size);
	}
	
	/**
	 * 读取数据
	 */
	private void readData() {
		
		if(null == mReadDataThread) {
			mPermitRead = true;
			mReadDataThread = new ReadDataThread();
			mReadDataThread.setName(String.format("ReadData-%s", mSessionHandle));
			mReadDataThread.start();		
		} else {
			AppLog.e("can not start thread again!!!");
		}
	}
	
	/**
	 * 断掉读取线程
	 */
	private void disReadThread() {
		
		mPermitRead = false;
//		if(mReadDataThread != null && mReadDataThread.isAlive()){
//			try { 
//				mReadDataThread.join();
//			} catch (InterruptedException e) { 
//				AppLog.e("Stop ReadThread error == " + e.getMessage());
//			}			
//		}
		mReadDataThread = null;
		AppLog.d("P2P disReadThread");
	}
	
	/**
	 * 读取线程，没有采用PPCS_Check_Buffer验证缓存
	 */
	private class ReadDataThread extends Thread {

		/** 其他数据 */
		private  byte[] mOtherRecvData = null;
		/** 有效数据 */
		private byte[] mValidRecvData = null;
		private int[] mRecvSize = new int[1];
		
		/**
		 * 设置接收数据的信息
		 * @param isValid	接收的数据是否是有效数据
		 * @param len	长度
		 */
		private void setRecvInfo(boolean isValid, int len) {
			
			mRecvSize[0] = len;
			if(isValid) {
				mValidRecvData = new byte[len];
			} else {
				mOtherRecvData = new byte[len];
			}
		}
		
		@Override
		public void run() {
			
			do {
				AppLog.d("开始接收数据，Read mSessionHandle == " + mSessionHandle);
				
				// --------读取包头，即从流中读取PACKAGE_HEAD_LEN个长度的字节--------
				setRecvInfo(false, PACKAGE_HEAD_LEN);
				int readRet = PPCS_APIs.PPCS_Read(mSessionHandle, CHANNEL_READ, mOtherRecvData, mRecvSize, TIME_OUT);
				AppLog.d("Read Head Result == " + readRet);
				if (readRet == PPCS_APIs.ERROR_PPCS_SESSION_CLOSED_TIMEOUT) {
					AppLog.e("ReadDataThread: Session TimeOUT!");
					disconnectException();
					break;

				} else if (readRet == PPCS_APIs.ERROR_PPCS_SESSION_CLOSED_REMOTE) {
					AppLog.e("ReadDataThread: Session Remote Close!");
					disconnectException();
					break;

				} else if (readRet == PPCS_APIs.ERROR_PPCS_SESSION_CLOSED_CALLED) {
					AppLog.e("ReadDataThread: myself called PPCS_Close!");
					disconnectException();
					break;
					
				} else if (readRet == PPCS_APIs.ERROR_PPCS_INVALID_SESSION_HANDLE) {
					AppLog.e("ReadDataThread: invalid session handle!");
					disconnectException();
					break;
					
				} else if (readRet == PPCS_APIs.ERROR_PPCS_NOT_INITIALIZED) {
					AppLog.e("ReadDataThread: not initialzed!");
					disconnectException();
					break;
				}
				
				if(readRet >= 0) {
					AppLog.d("Read Head Data == " + new String(mOtherRecvData));
				}

				if(readRet >= 0 && PACKAGE_HEAD.equals(new String(mOtherRecvData))) {
					// 包头验证成功，开始有效数据读取
					// --------读取数据长度的长度--------
					setRecvInfo(false, PACKAGE_LENGTH_LEN);
					readRet = PPCS_APIs.PPCS_Read(mSessionHandle, CHANNEL_READ, mOtherRecvData, mRecvSize, TIME_OUT);
					AppLog.d("Read Lenght Result == " + readRet);
					
					// 计算有效数据长度
//					int validLen = (mOtherRecvData[0]&0xFF)<<8 | (mOtherRecvData[1]&0xFF);
					int validLen = (mOtherRecvData[0] & 0xFF) << 24 | (mOtherRecvData[1] & 0xFF)<<16|(mOtherRecvData[2] & 0xFF) << 8 | (mOtherRecvData[3] & 0xFF);
					if(readRet >= 0) {
						AppLog.d("Read Lenght Data == " + validLen);
					}
					
					if(readRet >= 0 && validLen > 0) {
						// --------读取有效数据--------
						setRecvInfo(true, validLen);
						readRet = PPCS_APIs.PPCS_Read(mSessionHandle, CHANNEL_READ, mValidRecvData, mRecvSize, TIME_OUT);
						if(readRet >= 0) {
							// --------读取包尾--------
							setRecvInfo(false, PACKAGE_END_LEN);
							readRet = PPCS_APIs.PPCS_Read(mSessionHandle, CHANNEL_READ, mOtherRecvData, mRecvSize, TIME_OUT);
							
							if(readRet >= 0 && PACKAGE_END.equals(new String(mOtherRecvData))) {
								//包尾验证成功，数据符合格式，开始处理数据
								AppLog.d("Valid Lenght == " + validLen + "::ValidRecvData == " + new String(mValidRecvData));
								if(null != mP2PClientListener) {
									mP2PClientListener.onP2PRecvData(new String(mValidRecvData));
								}
							}
						}
					} else {
						sleep(50);
						continue;
					}
				} else {
					if(readRet >= 0) {
						// 读取剩余的数据，并扔掉，要在读取正常的情况下
						setRecvInfo(false, MAX_SIZE_BUF);
						PPCS_APIs.PPCS_Read(mSessionHandle, CHANNEL_READ, mOtherRecvData, mRecvSize, TIME_OUT);
					}
					sleep(50);
					continue;
				}
				sleep(50);
			} while(mPermitRead);
		}
		
		private void sleep(int time) {
			
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				AppLog.e("sleep exception!!");
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// START MJPEG
	public static final int CODE_INFO_CONNECTING	=1;
	public static final int CODE_INFO_CONNECT_FAIL	=2;
	public static final int CODE_INFO_PPCS_CHECK_OK	=3;
	public static final int CODE_INFO_AV_ONLINENUM	=4;
	/** 读取视频数据的Channel ID，PPCS提供8个Channel，0~7，要与服务端的write video channel对应 */
	private static final byte CHANNEL_DATA	=3;
	/** 读取IO控制数据的Channel ID，PPCS提供8个Channel，0~7，要与服务端的write IOCtrl channel对应 */
	private static final byte CHANNEL_IOCTRL	=4;
	volatile boolean mVideoRunning = false;
	private RecvAVDataThread mRecvAVDataThread = null;
	
	public boolean isVideoRunning() {
		return mVideoRunning;
	}
	
	/**
	 * 开始视频
	 * @return
	 */
	public int startVideo() {
		
		if(mRecvAVDataThread == null) {
			mVideoRunning = true;
			mRecvAVDataThread = new RecvAVDataThread();
			mRecvAVDataThread.setName("RecvAVData");
			mRecvAVDataThread.start();
		}
		
		int nRet = PPCS_APIs.ER_ANDROID_NULL;
		if (mSessionHandle < 0) {
			return nRet;			
		}
		nRet = sendIOCtrl(mSessionHandle,
				AVStreamIO_Proto.IOCTRL_TYPE_VIDEO_START, null, 0);
		return nRet;
	}

	/**
	 * 结束视频
	 * @return
	 */
	public int stopVideo() {
		
		disRecvAVDataThread();
		int nRet = PPCS_APIs.ER_ANDROID_NULL;
		if (mSessionHandle < 0) {
			return nRet;			
		}
		nRet = sendIOCtrl(mSessionHandle, AVStreamIO_Proto.IOCTRL_TYPE_VIDEO_STOP, null, 0);
		return nRet;
	}
	
	/**
	 * 设置分辨率
	 * @return
	 */
	public int setResolution(byte[] b) {
		
		int nRet = PPCS_APIs.ER_ANDROID_NULL;
		if (mSessionHandle < 0) {
			return nRet;
		}
		nRet = sendIOCtrl(mSessionHandle, AVStreamIO_Proto.IOCTRL_TYPE_VIDEO_SET_RESOLUTION, b, 1);
		return nRet;
	}
	
	/**
	 * 切换摄像头
	 * @param b
	 * @return
	 */
	public int switchCamera(byte[] b) {
		
		int nRet = PPCS_APIs.ER_ANDROID_NULL;
		if (mSessionHandle < 0) {
			return nRet;
		}
		nRet = sendIOCtrl(mSessionHandle, AVStreamIO_Proto.IOCTRL_TYPE_VIDEO_SELECT_ANALOG_CAMERA, b, 1);
		return nRet;
	}
	
	/**
	 * 发送IOCtrl指令
	 * @param sessionHandle
	 * @param nIOCtrlType
	 * @param pIOData
	 * @param nIODataSize
	 * @return
	 */
	public int sendIOCtrl(int sessionHandle, int nIOCtrlType, byte[] pIOData, int nIODataSize) {
		
		if(nIODataSize < 0 || nIOCtrlType < 0) {
			return PPCS_APIs.ER_ANDROID_NULL;
		}

		int nSize = AVStreamIOHead.LEN_HEAD + AVIOCtrlHead.LEN_HEAD + nIODataSize;
		byte[] packet = new byte[nSize];
		Arrays.fill(packet, (byte)0);
		byte[] byt = Utils.intToByteArray_Little(AVIOCtrlHead.LEN_HEAD + nIODataSize);
		System.arraycopy(byt, 0, packet, 0, 4);
		packet[3] = (byte) AVStreamIO_Proto.SIO_TYPE_IOCTRL;
		
		packet[4] = (byte)nIOCtrlType;
		packet[5] = (byte)(nIOCtrlType >>> 8);
		
		if(pIOData != null){
			packet[6] = (byte)nIODataSize;
			packet[7] = (byte)(nIODataSize >>> 8);			
			System.arraycopy(pIOData, 0, packet, 8, nIODataSize);
		}
		return PPCS_APIs.PPCS_Write(sessionHandle, CHANNEL_IOCTRL, packet, nSize);
	}
	
	class RecvAVDataThread extends Thread {
		
		private long mStartTime = 0l;
		private int mFrameNum = 0;
		private int mFrameCount = 0;
		public static final int MAX_FRAMEBUF = 1600000;

		private byte[] pAVData = new byte[MAX_SIZE_BUF];
		private int[] nRecvSize = new int[1];
		private int nCurStreamIOType = 0;
		private AVStreamIOHead pStreamIOHead = new AVStreamIOHead();
		private AVFrameHead stFrameHead = new AVFrameHead();
		
		@Override
		public void run() {
			super.run();
			long tick1 = System.currentTimeMillis(), tick2 = 0L;
			int nOnlineNum=1;
			mStartTime = System.currentTimeMillis();
			do{
				nRecvSize[0] = AVStreamIOHead.LEN_HEAD;
				pAVData = new byte[nRecvSize[0]];
				int nRet = PPCS_APIs.PPCS_Read(mSessionHandle, CHANNEL_DATA, pAVData, nRecvSize, TIME_OUT);
				AppLog.d("RecvAVData  Result == " + nRet);
				if(nRet == PPCS_APIs.ERROR_PPCS_SESSION_CLOSED_TIMEOUT) {
					AppLog.e("ThreadRecvIOCtrl: Session TimeOUT!");
					disRecvAVDataThread();
					break;

				}else if(nRet == PPCS_APIs.ERROR_PPCS_SESSION_CLOSED_REMOTE) {
					AppLog.e("ThreadRecvIOCtrl: Session Remote Close!");
					disRecvAVDataThread();
					break;

				}else if(nRet == PPCS_APIs.ERROR_PPCS_SESSION_CLOSED_CALLED) {
					AppLog.e("ThreadRecvIOCtrl: myself called PPCS_Close!");
					disRecvAVDataThread();
					break;
					
				} else if (nRet == PPCS_APIs.ERROR_PPCS_INVALID_SESSION_HANDLE) {
					AppLog.e("ReadDataThread: invalid session handle!");
					disRecvAVDataThread();
					break;
					
				} else if (nRet == PPCS_APIs.ERROR_PPCS_NOT_INITIALIZED) {
					AppLog.e("ReadDataThread: not initialzed!");
					disRecvAVDataThread();
					break;
				}
				if(nRet >= 0 && nRecvSize[0] > 0){
					pStreamIOHead.setData(pAVData);
					nCurStreamIOType = pStreamIOHead.getStreamIOType();
					nRecvSize[0] = pStreamIOHead.getDataSize();
					pAVData = new byte[nRecvSize[0]];
					nRet = PPCS_APIs.PPCS_Read(mSessionHandle, CHANNEL_DATA, pAVData, nRecvSize, TIME_OUT);
					if(nRet == PPCS_APIs.ERROR_PPCS_SESSION_CLOSED_TIMEOUT) {
						AppLog.e("ThreadRecvIOCtrl: Session TimeOUT!");
						disRecvAVDataThread();
						break;

					}else if(nRet == PPCS_APIs.ERROR_PPCS_SESSION_CLOSED_REMOTE) {
						AppLog.e("ThreadRecvIOCtrl: Session Remote Close!");
						disRecvAVDataThread();
						break;

					}else if(nRet == PPCS_APIs.ERROR_PPCS_SESSION_CLOSED_CALLED) {
						AppLog.e("ThreadRecvIOCtrl: myself called PPCS_Close!");
						disRecvAVDataThread();
						break;
						
					} else if (nRet == PPCS_APIs.ERROR_PPCS_INVALID_SESSION_HANDLE) {
						AppLog.e("ReadDataThread: invalid session handle!");
						disRecvAVDataThread();
						break;
						
					} else if (nRet == PPCS_APIs.ERROR_PPCS_NOT_INITIALIZED) {
						AppLog.e("ReadDataThread: not initialzed!");
						disRecvAVDataThread();
						break;
					}
					
					if(nRet >= 0 && nRecvSize[0] > 0) {
						tick2 = System.currentTimeMillis();
						if((tick2 - tick1) > 1000){
							tick1 = tick2;
							stFrameHead.setData(pAVData);
							nOnlineNum = stFrameHead.getOnlineNum();
//							updateAVListenerInfo(CODE_INFO_AV_ONLINENUM, nOnlineNum, null);
							AppLog.d("CODE_INFO_AV_ONLINENUM :: " + nOnlineNum);
						}
						
						if(nRecvSize[0] >= MAX_SIZE_BUF) {
							AppLog.d("====nRecvSize>64*1024, nCurStreamIOType=" + nCurStreamIOType);
						}
						if(nCurStreamIOType == AVStreamIO_Proto.SIO_TYPE_VIDEO) {
							handleVideoData(mSessionHandle, pAVData, nRecvSize[0]);
						}
					}
				}
			}while(mVideoRunning);
			System.out.println("---ThreadRecvAVData is exit.");
		}
		
		private void handleVideoData(int handleSession, byte[] pAVData, int nAVDataSize) {
			
			stFrameHead.setData(pAVData);
			switch(stFrameHead.getCodecID()) {
				case AVStreamIO_Proto.CODECID_V_MJPEG:
					AppLog.d("CODECID_V_MJPEG");
					int nFrameSize = stFrameHead.getDataSize();
					int whichCamera = stFrameHead.getCameraFlag();
					int encrypt = stFrameHead.getEncryptFlag();
					AppLog.d("FrameSize == " + nFrameSize + "::AVDataSize == " + nAVDataSize);
					AppLog.d("Which Camera == " + whichCamera + "::isEncrypt == " + encrypt);
					try {
						mFrameNum++;
						System.arraycopy(pAVData, AVFrameHead.LEN_HEAD, pAVData, 0, nAVDataSize - AVFrameHead.LEN_HEAD);
//						Bitmap bmp = BitmapFactory.decodeStream(new ByteArrayInputStream(pAVData));
						if(null != mP2PClientListener && mVideoRunning) {
							mP2PClientListener.onPlay(whichCamera, encrypt, pAVData, nFrameSize, mFrameCount);
						}		
					} catch(ArrayIndexOutOfBoundsException e) {
						AppLog.e("ArrayIndexOutOfBoundsException!!!!!!!!");
					}
					// 显示每秒的帧数
					if ((System.currentTimeMillis() - mStartTime) >= 1000) {
						mStartTime = System.currentTimeMillis();
						mFrameCount = mFrameNum;
						mFrameNum = 0;
					}
					sleep(50);
					break;
				default:;
			}
		}
		
		private void sleep(int time) {
			
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				AppLog.e("sleep exception!!");
			}
		}
	}
	
	private void disRecvAVDataThread() {
		
		mVideoRunning = false;
		mRecvAVDataThread = null;
	}
	
	// END MJPEG
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	public interface OnP2PClientListener {
		
		void onP2PConnFail();
		void onP2PConnSuccess();
		
		/** 连接完成 */
//		public void onConnFinish(int sessionHandle);
		/**
		 * 服务器返回有效数据
		 *  <strong>在子线程里，不可直接刷新UI</strong> 
		 * @param strInfo 读取到的内容
		 */
		void onP2PRecvData(String strInfo);
		/** 播放 */
		void onPlay(int whichCamera, int encrypt, byte[] pAVData, int nFrameSize, int frameCount);
		/** 设备断开了，需要刷新状态 */
		void onP2PDisconnect();
		
	}

}






package com.sen5.smartlifebox.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sen5.smartlifebox.common.Constant;
import nes.ltlib.utils.AppLog;
import com.sen5.smartlifebox.controlall.LongToothControl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.SystemProperties;
import android.sen5.Sen5ServiceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Surface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import nes.ltlib.data.CameraEntity;

public class Utils {

	public static boolean isVersionAbove6(){
//		AppLog.e("---------Build.VERSION.SDK_INT = " + Build.VERSION.SDK_INT);
		return  Build.VERSION.SDK_INT >= 23;
	}


	/**
	 * 判断是否存在有线网口
	 * @param context
	 * @return
	 */
	public static boolean hasEthernet(Context context) {
		
		Sen5ServiceManager sen5ServiceManager = (Sen5ServiceManager) context.getSystemService("sen5_service");
        boolean isEthernet = false;
        if (null != sen5ServiceManager) {
            isEthernet = sen5ServiceManager.hasEthernet();
        }
        return isEthernet;
	}
	
	/**
	 * 判断是否以太网连接
	 * @param context
	 * @return
	 */
	@SuppressLint("InlinedApi")
	public static boolean isEthernetConn(Context context) {
		
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connectivity
					.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
			
			return info.isConnected();			
		} catch(NullPointerException e) {
			
			return false;
		}
	}
	
	/**
	 * 判断wifi是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnect(Context context) {
		ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (null != mWifi && mWifi.isConnected()) {
			return true;
		}
		return false;
	}
		
	
	/**
	 * value为true代表可以调出小视频窗口，false则不能
	 * @param value
	 */
	public static void setDVBStart(boolean value) {
		SystemProperties.set("sys.sen5.dvb.onekey.start", String.valueOf(value));
	}
	
	/**
	 * Unix时间戳
	 * @param datetime
	 * @return
	 */
	public static long dateToMillion(String datetime) {
		long milliseconds = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = sdf.parse(datetime);
			milliseconds = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return milliseconds;
	}
	
	/**
	 * 设置系统时间
	 * @param date
	 */
	public static void setSystemDate(Context context, Date date) {
	       
        long when = date.getTime();

        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager)context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }
	
	/**
	 * 取消键盘
	 * @param context
	 * @param view
	 */
	public static void cancelKeyBoard(Context context, View view) {
		
		if (view != null) {
			InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);  
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}	
	}
	
	/**
	 * 显示键盘
	 * @param edit
	 */
	public static void DisplayKeyBoard(final EditText edit) {
	
		edit.setFocusable(true);
		edit.setFocusableInTouchMode(true);
		edit.requestFocus();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager)edit.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(edit, InputMethodManager.RESULT_SHOWN);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}	
		}, 100);
		
	}
	
	/**
	 * 获取总的内存大小 
	 * @param context
	 * @return
	 */
    public static long getTotalMemory(Context context) { 
        
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE); 
        MemoryInfo mi = new MemoryInfo(); 
        am.getMemoryInfo(mi); 
        return mi.totalMem / (1024 * 1024);
    }
	
	/**
	 * 获取可用内存大小 
	 * @param context
	 * @return
	 */
    public static long getAvailMemory(Context context) { 
        
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE); 
        MemoryInfo mi = new MemoryInfo(); 
        am.getMemoryInfo(mi); 
        //return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化 
        return mi.availMem / (1024 * 1024); 
    }
    
    /**
	 * 获取已用内存大小 
	 * @param context
	 * @return
	 */
    public static long getUsedMemory(Context context) { 
        
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE); 
        MemoryInfo mi = new MemoryInfo(); 
        am.getMemoryInfo(mi); 
        //return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化 
        return (mi.totalMem - mi.availMem) / (1024 * 1024); 
    }
    
    /**
     * 已用内存占的比例
     * @param context
     * @return
     */
    public static int getAvailMemProportion(Context context) {
    	
    	ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    	MemoryInfo mi = new MemoryInfo();
    	am.getMemoryInfo(mi);
    	int nProportion = (int)((mi.totalMem - mi.availMem)  * 100/ mi.totalMem);
    	return nProportion;
    }

    public static int dpToPx(Resources res, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
	}
	
	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Resources res, float pxValue) {
		final float scale = res.getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Resources res, float dpValue) {
		final float scale = res.getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	public static int getInt(String strIndex) {
		int index = 0;
		try {
			index = Integer.parseInt(strIndex);
		} catch(NumberFormatException e) {
			
		}
		return index;
	}
	
	public static String getString(String str) {
		
		if(null == str) {
			return "";
		}
		return str;
	}
	
	/**
	 * 获取APK版本号（只取前面的三位）
	 * @param context
	 * @return
	 */
	public static String getAppVersion(Context context) {
		
		String versionName = "";
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);

			// 当前应用的版本名称
			versionName = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		// 版本号处理
		if(null != versionName || !"".equals(versionName)) {
			String[] arr = versionName.split("[.]");
			versionName = String.format("%s.%s.%s", arr[0], arr[1], arr[2]);
		}
		return versionName;
	}
	
	/**
	 * 获取版本号
	 * @param context
	 * @return
	 */
	public static int getAppVersionCode(Context context) {
		
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * 获取当前屏幕旋转角度
	 * 
	 * @param activity
	 * @return 0表示是竖屏; 90表示是左横屏; 180表示是反向竖屏; 270表示是右横屏
	 */
	public static int getDisplayRotation(Activity activity) {
		if(activity == null)
			return 0;
		
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		switch (rotation) {
		case Surface.ROTATION_0:
			return 0;
		case Surface.ROTATION_90:
			return 90;
		case Surface.ROTATION_180:
			return 180;
		case Surface.ROTATION_270:
			return 270;
		}
		return 0;
	}
	
	/**
	 * 二维码生成
	 * @param str
	 * @param size
	 * @return
	 * @throws WriterException
	 */
	public static Bitmap createQRCode(String str, int size)throws WriterException {
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		BitMatrix matrix = new MultiFormatWriter().encode(str,
				BarcodeFormat.QR_CODE, size, size);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[] pixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					// 0xff000000 black
					pixels[y * width + x] = 0xff000000;
				}
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
	
	/**
	 * 生成带logo的二维码
	 * @param qrInfo  	二维码中包含的文本信息
	 * @param logo		logo图片
	 * @param qrSize	QR的大小
	 * @param logoSize	Logo的大小
	 * @param format	 编码格式（例如BarcodeFormat.QR_CODE）
	 * @return
	 * @throws WriterException
	 */
	public static Bitmap createLogoQRCode(String qrInfo, Bitmap logo, int qrSize, int logoSize, BarcodeFormat format)
			throws WriterException {
		Matrix m = new Matrix();
		float sx = (float) 2 * logoSize / logo.getWidth();
		float sy = (float) 2 * logoSize / logo.getHeight();
		// 设置缩放信息
		m.setScale(sx, sy);
		// 将logo图片按martix设置的信息缩放
		logo = Bitmap.createBitmap(logo, 0, 0, logo.getWidth(),
				logo.getHeight(), m, false);
		MultiFormatWriter writer = new MultiFormatWriter();
		Hashtable<EncodeHintType, ErrorCorrectionLevel> hst = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
		// 设置字符编码
//		hst.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		// 提高容错率，要不然有可能不能被识别
		hst.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		// 生成二维码矩阵信息
		BitMatrix matrix = writer.encode(qrInfo, format, qrSize, qrSize, hst);
		// 矩阵高度
		int width = matrix.getWidth();
		// 矩阵宽度
		int height = matrix.getHeight();
		int halfW = width / 2;
		int halfH = height / 2;
		// 定义数组长度为矩阵高度*矩阵宽度，用于记录矩阵中像素信息
		int[] pixels = new int[width * height];
		// 从行开始迭代矩阵
		for (int y = 0; y < height; y++) {
			// 迭代列
			for (int x = 0; x < width; x++) {
				// 该位置用于存放图片信息
				if (x > halfW - logoSize && x < halfW + logoSize
						&& y > halfH - logoSize
						&& y < halfH + logoSize) {
					// 记录图片每个像素信息
					pixels[y * width + x] = logo.getPixel(x - halfW
							+ logoSize, y - halfH + logoSize);
				} else {
					{// 如果有黑块点，记录信息
					if (matrix.get(x, y)) 
						// 记录黑块信息
						pixels[y * width + x] = 0xff000000;
					}
				}

			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		// 通过像素数组生成bitmap
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
	
	/**
	 * 得到默认IP Address
	 * 
	 * @param context
	 * @return
	 */
	public static String getDefaultIpAddresses(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		LinkProperties prop = cm.getActiveLinkProperties();
		return formatIpAddresses(prop);
	}

	/**
	 * 得到IP地址字符串，包括IPv6地址
	 * 
	 * @param prop
	 * @return
	 */
	private static String formatIpAddresses(LinkProperties prop) {
		if (prop == null) {
			return null;			
		}
		Iterator<InetAddress> iter = prop.getAddresses().iterator();
		// If there are no entries, return null
		if (!iter.hasNext()) {
			return null;			
		}
		// Concatenate all available addresses, comma separated,include IPv6
		String addresses = "";
		while (iter.hasNext()) {
			addresses += iter.next().getHostAddress();
			if (iter.hasNext())
				addresses += ", ";
		}
		return addresses;
	}

	public static String getIPv4Address(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		LinkProperties prop = cm.getActiveLinkProperties();
		String addresses = "";
		if (null != prop) {
			Iterator<InetAddress> iter = prop.getAllAddresses().iterator();
			if (!iter.hasNext()) {
				return addresses;
			}
			while (iter.hasNext()) {
				InetAddress temp = iter.next();
				if (temp instanceof Inet4Address) {
					addresses = temp.getHostAddress();
					return addresses;
				}
			}
		}
		return addresses;
	}

	public static String getIPv6Address(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		LinkProperties prop = cm.getActiveLinkProperties();
		String addresses = "";
		if (null != prop) {
			Iterator<InetAddress> iter = prop.getAllAddresses().iterator();
			if (!iter.hasNext()) {
				return addresses;
			}
			while (iter.hasNext()) {
				InetAddress temp = iter.next();
				if (temp instanceof Inet6Address) {
					addresses = temp.getHostAddress();
					return addresses;
				}
			}
		}
		return addresses;
	}
	
	/**
	 * 是否有外部存储器
	 * @return 存储的路径
	 */
	public static ArrayList<String> getUSBStoragePath() {
		
		ArrayList<String> filePath = new ArrayList<String>();
		File[] files = new File("/storage/external_storage").listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.getPath().startsWith("/storage/external_storage/sd") && !
					(file.getPath().contains("/storage/external_storage/sdcard"))) {
					filePath.add(file.getAbsolutePath());
				}
			}
		}
		return filePath;
	}
	
	public static byte[] hexStr2ByteArray(String hexString) {
        try {
            hexString = hexString.toLowerCase();
            final byte[] byteArray = new byte[hexString.length() / 2];
            int k = 0;
            for (int i = 0; i < byteArray.length; i++) {
                byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
                byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
                byteArray[i] = (byte) (high << 4 | low);
                k += 2;
            }
            return byteArray;
        } catch (Exception e) {
            Log.e("factoryTest", "read key error:" + e);
            return "hex2Bytes error".getBytes();
        }

    }
	
	public static final int HOURS_1 = 60 * 60000;
	/**
	 * 获取时区，专给HISI用的
	 * @param tz
	 * @return
	 */
	public static int getSecrityTimeZone(TimeZone tz) {
    	
    	int time = 0;
    	final long date = Calendar.getInstance().getTimeInMillis();
        final int offset = tz.getOffset(date);
        final int p = Math.abs(offset);
        
        time = (p / HOURS_1) * 4 + ((p / 60000) % 60) / 15;
        if (offset < 0) {
        	time = -time;
        }
        Log.d("TimeZoneUtils", "SecrityTime == " + time);
    	return time;
    }

	public static String getQRInfoToPair(Context context) {
		Sen5ServiceManager serviceManager = (Sen5ServiceManager) context.getSystemService("sen5_service");
		serviceManager.writeSysfs("sys/class/unifykeys/name", "DID");
		String strDID = serviceManager.readSysfs("/sys/class/unifykeys/read");

		String longToothID = LongToothControl.getLongToothServerId();
		if(!TextUtils.isEmpty(longToothID)){
			return longToothID;
		}else if (null != strDID) {
			return new String(Utils.hexStr2ByteArray(strDID));
		} else {
			return null;
		}
	}

	public static String getQRInfo(Context context) {
		Sen5ServiceManager serviceManager = (Sen5ServiceManager) context.getSystemService("sen5_service");
		serviceManager.writeSysfs("sys/class/unifykeys/name", "DID");
		String strDID = serviceManager.readSysfs("/sys/class/unifykeys/read");

		boolean hasTL = LongToothControl.hasLongToothServer();
		if(hasTL){
			String eMac = Utils.getMacAddress();
			return eMac;
		}else if (null != strDID) {
			return new String(Utils.hexStr2ByteArray(strDID));
		} else {
			return null;
		}
	}

	/**
	 * 整形转 字节
	 *
	 * @param value
	 * @return
	 */
	public static final byte[] intToByteArray_Little(int value) {
		return new byte[]{
				(byte) value,
				(byte) (value >>> 8),
				(byte) (value >>> 16),
				(byte) (value >>> 24)
		};
	}

	/**
	 * 过滤5g wifi
	 * @param results
	 */
	public static List<ScanResult> filter5GWifi(List<ScanResult> results) {
		Iterator iterator = results.iterator();
		while (iterator.hasNext()) {
			ScanResult result = (ScanResult) iterator.next();
			String frequency = String.valueOf(result.frequency);
			if (frequency.startsWith("5")) {
				iterator.remove();
			}
		}

		return results;
	}

	public static List<CameraEntity> getCameraList(){
		List<CameraEntity> list = new ArrayList<CameraEntity>();
		File file = new File(Constant.CAMERA_INI);
		if (!file.exists()) {
			AppLog.e("camera.ini 文件不存在");
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!file.canWrite()) {
			AppLog.e("不能写camera.ini文件");
		}

		if (file.exists()) {
			//读取文件行数，一行代表一个camera，每行格式为：##DID#NAME##
			FileReader fr = null;
			BufferedReader br = null;
			try {
				fr = new FileReader(file);
				br = new BufferedReader(fr);
				String line;
				while ((line = br.readLine()) != null) {
					String content = line.substring(2, line.length() - 2);
					String[] strs = content.split("#");
					Log.e("AA", "---------getCameraList--strs[0] = " + strs[0]);
					CameraEntity camera = new CameraEntity();
					list.add(camera);
					camera.setDeviceID(strs[0]);
					camera.setDeviceName(strs[1]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IndexOutOfBoundsException e) {
				AppLog.e("Camera.ini文件格式异常");
			} finally {
				try {
					if (br != null) {
						br.close();
					}
					if (fr != null) {
						fr.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public static String loadFileAsString(String filePath)
			throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
		}
		reader.close();
		return fileData.toString();
	}

	/*
	 * Get the STB MacAddress
	 */
	public static String getMacAddress() {
		try {
			return loadFileAsString("/sys/class/net/eth0/address")
					.toUpperCase().substring(0, 17);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}

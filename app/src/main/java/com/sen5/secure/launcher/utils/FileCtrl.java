package com.sen5.secure.launcher.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 文件操作类
 * @author Matt.Xie
 */
public class FileCtrl {

	/**
	 * 读取文件
	 * @param filePath 文件路径
	 * @return 以字符串的形式返回文件内容
	 */
	public static String readTxtFile(String filePath) {

		try {
			String encoding = "utf-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) {
				StringBuilder sb = new StringBuilder();
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);
				BufferedReader buff = new BufferedReader(read);
				String strLine = null;
				while ((strLine = buff.readLine()) != null) {
					sb.append(strLine);
				}
				read.close();
				return sb.toString();
			} else {
				return null;
			}

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 读取文件的最后修改时间
	 * @param filePath 文件路径
	 * @return 最后修改时间
	 */
	public static long getFileLastModifiedTime(String filePath) {
		String path = filePath.toString();
		File f = new File(path);
		Calendar cal = Calendar.getInstance();
		long time = f.lastModified();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		cal.setTimeInMillis(time);
		// formatter.format(cal.getTime())的输出修改时间2015-01-19 10:32:38
		long dateTime=0;
		Date d2 = null;
		try {
			d2 = formatter.parse(formatter.format(cal.getTime()));
			dateTime = d2.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateTime;
	}
}

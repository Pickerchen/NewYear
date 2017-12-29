package com.sen5.secure.launcher.data.parserxml;

public class ItemLaunchAppData {

	/**
	 * 按键的代码
	 */
	private int keyCode = 0;
	/**
	 * 按键的名称
	 */
	private String keyName = "";
	/**
	 * 包名
	 */
	private String packageName = "";
	/**
	 * 启动的类名
	 */
	private String className = "";
	public int getKeyCode() {
		return keyCode;
	}
	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}
	public String getKeyName() {
		return keyName;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	
}

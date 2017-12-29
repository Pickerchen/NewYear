package com.sen5.launchertools;

import android.content.Context;
import android.sen5.Sen5ServiceManager;
/**
 *	@author JesseYao 
 */
public class CustomizationUtil {
	
	private static Sen5ServiceManager serviceManager = null;
	
	/**
	 * get Customer Name
	 * 
	 * @param context
	 * @return
	 */
	public static String getProductName(Context context) {

		String productName = "";
		if(null == serviceManager){
			serviceManager = (Sen5ServiceManager) context.getSystemService("sen5_service");
		}
		if (null != serviceManager) {
			productName = serviceManager.getProductName();
		}
		return productName;
	}
	
	public static boolean isProductName(Context context, String productName){
		if(null == serviceManager){
			serviceManager = (Sen5ServiceManager) context.getSystemService("sen5_service");
		}
		if (null != serviceManager) {
			String name = serviceManager.getProductName();
			if(name.equals(productName)){
				return true;
			}else {
				return false;
			}
		}
		return false;
	}
	
	public static boolean isContainsCustomerTag(Context context, String tag){
		if(null == serviceManager){
			serviceManager = (Sen5ServiceManager) context.getSystemService("sen5_service");
		}
		if (null != serviceManager) {
			String productName = serviceManager.getProductName();
			if(productName.contains(tag)){
				return true;
			}else {
				return false;
			}
		}
		return false;
	}
	
	public static boolean isCustomerName(Context context, String customerName){
		if(null == serviceManager){
			serviceManager = (Sen5ServiceManager) context.getSystemService("sen5_service");
		}
		if (null != serviceManager) {
			String name = serviceManager.getCustomerName();
			if(name.equals(customerName)){
				return true;
			}else {
				return false;
			}
		}
		return false;
	} 
	
	/**
	 * product name
	 */
	public static final String SN6B5A_EU064 = "SN6B5A_EU064";
	public static final String SN6B5C_EU009 = "SN6B5C_EU009";
	public static final String SN6B5E_SA016 = "SN6B5E_SA016";
	
	/**
	 * customer name
	 */
	public static final String EU001 = "EU001";
	public static final String UHD200 = "UHD200";
	public static final String EU012 = "EU012";
	public static final String SA016 = "SA016";
	public static final String EU095 = "EU095";
	public static final String EU012_A = "EU012_A";
    public static final String AF008 = "AF008";
    public static final String SF001 = "SF001";
}

package com.ipcamera.main.utils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class UtilsDIDSP {	
	
	public static final String S_DID_ALARM_REQUESTCODE = "alarm_requestcode";
	
	public static void setSharePreInt(List<String> listDID){
		if(listDID == null){
			return;
		}
		int size = listDID.size();
		
		int maxDIDValue = getMaxDIDValue();
		
		for (int i = 0; i < size; i++) {
			String didKey = listDID.get(i);
			boolean containsKey = MySharePreDID.containsKey(didKey);
			
			if(containsKey){
				
			}else{
				maxDIDValue += 1;
				MySharePreDID.setSharePreInt(didKey, maxDIDValue);
//				maxDIDValue = getMaxDIDValue(listDID);
			}
		}
	}
	
//	public static int getMaxDIDValue(List<String> listDID){
//		if(listDID == null){
//			return 0;
//		}
//		int size = listDID.size();
//		int saveMaxValue = 0;
//		
//		for (int i = 0; i < size; i++) {
//			String didKey = listDID.get(i);
//			int sharePreInt = MySharePreDID.getSharePreInt(didKey, 0);
//			saveMaxValue = Math.max(saveMaxValue, sharePreInt);
//		}
//		
//		return saveMaxValue;
//	}
	
	public static int getMaxDIDValue(){
		Map<String, Integer> all = (Map<String, Integer>)MySharePreDID.getAll();
		int saveMaxValue = 0;
		for (Entry<String, Integer> iterable_element : all.entrySet()) {
			Integer value = iterable_element.getValue();
			
			saveMaxValue = Math.max(saveMaxValue, value);
		}
		return saveMaxValue;
		
	}
	
	public static void setSharePreAdd(){
		MySharePreDID.addSharePreInt(S_DID_ALARM_REQUESTCODE);
	}
	
}

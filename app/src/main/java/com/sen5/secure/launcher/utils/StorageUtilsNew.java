package com.sen5.secure.launcher.utils;



import android.os.Environment;
import android.os.StatFs;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.storage.VolumeInfo;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


/**
 * Created by jyc on 2016/7/25.
 */
public class StorageUtilsNew {
	
    private static final String SD = "sd";
    private static final String SDCARD = "sdcard";
    private static final String USB_905 = "ud";
    private static final String USB = "usb";
    public static final String EXTSD = "extsd";

    /**
     * android 6.0 sdk>=23的时候使用
     * @param storageManager
     * @param spath
     * @return
     */
    public static boolean isSDCard(StorageManager storageManager, String spath){
    	//DLog.e("---------------absolutePath 1= " + spath);
    	boolean isSDCard = false;
    	if(null != storageManager){
    		List<VolumeInfo> volumes = storageManager.getVolumes();
//    				storageManager.getVolumes();
    		if(null != volumes){
    			int size = volumes.size();
    			for (int i = 0; i < size; i++) {
    				
    				VolumeInfo volumeInfo = volumes.get(i);
    				File file = volumeInfo.getPath();
    				if(null == file){
    					continue;
    				}
    				String absolutePath = file.getAbsolutePath();
    				//DLog.e("---------------absolutePath = " + absolutePath);
    				if(!TextUtils.isEmpty(absolutePath) && !TextUtils.isEmpty(spath) && absolutePath.equals(spath)){
    					if(null != volumeInfo){
    						DiskInfo disk = volumeInfo.getDisk();
    						isSDCard = disk.isSd();
    					}
    				}
 
    			}
    		}
    		
    	}
    	return isSDCard;
    }
    
    /**
     * android 6.0 sdk>=23的时候使用
     * @param storageManager
     * @param spath
     * @return
     */
    public static boolean isUSB(StorageManager storageManager, String spath){
    	//DLog.e("---------------absolutePath 1= " + spath);
    	boolean isUSB = false;
    	if(null != storageManager){
    		List<VolumeInfo> volumes = storageManager.getVolumes();
//    		= storageManager.getVolumes();
    		if(null != volumes){
    			int size = volumes.size();
    			for (int i = 0; i < size; i++) {
    				
    				VolumeInfo volumeInfo = volumes.get(i);
    				File file = volumeInfo.getPath();
    				if(null == file){
    					continue;
    				}
    				String absolutePath = file.getAbsolutePath();
    				//DLog.e("---------------absolutePath = " + absolutePath);
    				if(!TextUtils.isEmpty(absolutePath) && !TextUtils.isEmpty(spath) && absolutePath.equals(spath)){
    					if(null != volumeInfo){
    						DiskInfo disk = volumeInfo.getDisk();
    						isUSB = disk.isUsb();
    					}
    				}
    				
    			}
    		}
    		
    	}
    	return isUSB;
    }

    
    

    public static boolean getSDCradStatus(StorageManager storageManager, int platform) {
        StorageVolume[] volumeList = storageManager.getVolumeList();
        boolean hasActivateSD = false;
        String resultText = "";
        for (StorageVolume storageVolume : volumeList) {
        	
            boolean isSD = false;
            		
            if(ConstantUtils.SDK_6_0){
            	isSD = isSDCard(storageManager, storageVolume.getPath());
            }else{
            	
            	isSD = isSDVolume(platform, storageVolume);
            }
            
            
            if (isSD && isDeviceMounted(storageManager, storageVolume)) {
                resultText += storageVolume.getPath() + "  可用空间/总空间    "
                        + measureStorage(storageVolume) + "\n";
                hasActivateSD = true;
            }
        }
     
        return true;
    }
    
    

    public static String getUSBStatus(StorageManager storageManager, int platform, String filePath) {
        StorageVolume[] volumeList = storageManager.getVolumeList();
        String pathVideo = "";
        boolean hasActivateUsb = false;
        String resultText = "";
        for (StorageVolume storageVolume : volumeList) {
        	
            boolean isUsb = false;
            if(ConstantUtils.SDK_6_0){
            	isUsb = isUSB(storageManager, storageVolume.getPath());
            }else{
            	isUsb = isUsbVolume(platform, storageVolume);
            }
            		
            
            if (isUsb && isDeviceMounted(storageManager, storageVolume)) {
                resultText += storageVolume.getPath() + "  可用空间/总空间    "
                        + measureStorage(storageVolume) + "\n";
                File sen5file = new File(storageVolume.getPath() +File.separator +filePath);
                if (sen5file.exists()) {
                	pathVideo = sen5file.getAbsolutePath();
                } else {
                    //DLog.i("read usbinfo 文件不存在");
//                    pathVideo = "android.resource://" + "com.amlogic.DVBPlayer"
//            				+ File.separator + R.raw.test_1;
                }

                hasActivateUsb = true;
            }
        }
        if(TextUtils.isEmpty(pathVideo)){
//    	  pathVideo = "android.resource://" + "com.amlogic.DVBPlayer"
//  				+ File.separator + R.raw.test_1;
        }
        
        return pathVideo;
    }

    private static boolean isSDVolume(int platform, StorageVolume volume) {
        boolean isAmlogic = true;

        if (isAmlogic) {
            return isAmlogicSD(volume);
        } else {
            return isAllWinnerSD(volume);
        }
    }

    private static boolean isUsbVolume(int platform, StorageVolume volume) {
        boolean isAmlogic = true;
        //DLog.e("isAmlogic:" + isAmlogic + " platform:" + platform + "     ");
        if (isAmlogic) {
            return isAmlogicUsb(volume);
        } else {
            return isAllWinnerUsb(volume);
        }
    }

    private static boolean isAllWinnerUsb(StorageVolume volume) {
        //DLog.i("");
        if (volume != null && volume.getPath().contains(USB)) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isAllWinnerSD(StorageVolume volume) {
        //DLog.i("");
        if (volume != null && volume.getPath().contains(EXTSD)) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isAmlogicUsb(StorageVolume volume) {
//    	 if(config.SDK_6_0){
//         	return true;
//         }
    	
        //DLog.i("--------volume.getPath() = " + volume.getPath());
        String path = volume.getPath();
        boolean hasSD = volume != null && path.contains(SD);
        boolean noHasSDCard = !path.contains(SDCARD);
        boolean hasSD_905 = path.contains(USB_905);
        
        if ((hasSD && noHasSDCard) || (hasSD_905 && noHasSDCard)) {
    		return true;
    	} else {
    		
    	}
        
        if(path.contains("emulated") || path.contains("self")
        		||path.contains("external_storage")){
        	
        }else{
        	
        	if (ConstantUtils.SDK_6_0) {
        		return true;
        	} else {
        		return false;
        	}
        }
       
        return false;
       
    }

    private static boolean isAmlogicSD(StorageVolume volume) {
        //DLog.i("");
        
//        if(config.SDK){
//        	return true;
//        }else{
        	
        	if (volume != null && volume.getPath().contains(SDCARD)) {
        		return true;
        	} else {
        		return false;
        	}
//        }
    }

    private static String measureStorage(StorageVolume mVolume) {

        long mTotalSize = 0;// 总大小
        long mAvailSize = 0;// 可用控件

        // 获取路径
        final String path = mVolume != null ? mVolume.getPath() : Environment
                .getDataDirectory().getPath();

        // 若有分区
        if (hasMultiplePartition(path)) {
            try {
                File dev = new File(path);
                // 获取分区目录名称
                String[] devList = dev.list();
                // 累加每一个分区的大小
                for (String devpart : devList) {
                    StatFs fs = new StatFs(path + "/" + devpart);
                    long availableBlocks = fs.getAvailableBlocks();
                    long blockCount = fs.getBlockCount();
                    long size = fs.getBlockSize();
                    mTotalSize += blockCount * size;
                    mAvailSize += availableBlocks * size;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            try {
                StatFs fs = new StatFs(path);
                long availableBlocks = fs.getAvailableBlocks();
                long blockCount = fs.getBlockCount();
                long size = fs.getBlockSize();

                mTotalSize = blockCount * size;
                mAvailSize = availableBlocks * size;
            } catch (IllegalArgumentException e) {

            } catch (Exception e) {

            }

        }
        return Long.toString(mAvailSize / 1024 / 1024) + "MB/"
                + Long.toString(mTotalSize / 1024 / 1024) + "MB";
    }

    // 判断U盘是否有分区
    private static boolean hasMultiplePartition(String devicePath) {
        try {
            File file = new File(devicePath);
            String major_minor[] = new String[2];
            String[] list = file.list();
            if (list.length <= 10) {
                for (int j = 0; j < list.length; j++) {
                    major_minor = list[j].split("_");
                    if (major_minor == null || major_minor.length != 2) {
                        return false;
                    }
                    /*
                     * try{ Integer.valueOf(major_minor[0]);
					 * Integer.valueOf(major_minor[1]); } catch
					 * (NumberFormatException e){ return false; }
					 */
                }
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    private static boolean isDeviceMounted(StorageManager storageManager, StorageVolume volume) {
        String state = volume != null ? storageManager.getVolumeState(volume
                .getPath()) : null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            return false;
        }
    }

}

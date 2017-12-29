package com.ipcamerasen5.record.common;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.widget.Toast;

import com.ipcamerasen5.main.MainApplication;
import com.ipcamerasen5.main1.R;
import com.ipcamerasen5.record.db.Clock;
import com.ipcamerasen5.record.db.IpCamDevice;
import com.ipcamerasen5.record.db.VideoForMemory;
import com.ipcamerasen5.record.entity.ClockTempObject;
import com.ipcamerasen5.record.entity.StorageInfo;
import com.litesuits.common.io.FileUtils;

import org.litepal.crud.DataSupport;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import nes.ltlib.utils.AppLog;
import nes.ltlib.utils.LogUtils;

import static android.provider.MediaStore.Video.Thumbnails.MINI_KIND;
import static com.ipcamerasen5.record.ui.view.RecordSettingActivity.did;
import static org.litepal.crud.DataSupport.findAll;

/**
 * Created by chenqianghua on 2017/4/5.
 */

public class CommonTools {

    private static final String  DEFAULT_PATH_TITLE = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "IPCamera" + File.separator;
    public static String capturePath = DEFAULT_PATH_TITLE + "image";
    public static final String captureDefaultUIPath = DEFAULT_PATH_TITLE + "default";
    public static final String Default_Record_Path = DEFAULT_PATH_TITLE + "video";
    public static final String CAMERA_DEVICE_FILE_PATH = "/data/smarthome/camera.ini";


    private static Toast mToast;
    public static Context mContext;
    private static boolean flag = false;
    public static void showToast(String content){
        if (flag) {
            if (mToast == null) {
                mToast = Toast.makeText(mContext, content, Toast.LENGTH_LONG);
            } else {
                mToast.setText(content);
            }
            mToast.show();
        }
    }

    public static boolean createFolder(File file,String name){
        String filePath = file.getAbsolutePath()+File.separator+name;
        File folder = new File(filePath);
        boolean isOk = true;
        if (!folder.exists()){
            isOk = folder.mkdirs();
        }
        LogUtils.e("createFolder","isok is "+isOk+" filepaht is"+filePath);
        return isOk;
    }

    public static boolean createCommonFolder(String path){
        boolean isOk = true;
        File folder = new File(path);
        if (!folder.exists()){
            isOk = folder.mkdirs();
        }
        LogUtils.e("createCommonFolder","path is "+path+" isok is"+isOk);
        return isOk;
    }


    /**
     * 通过安防系统的指定文件读取盒子中已经添加过的摄像头did
     * @return
     */
    public static List<String> initStartCameraDevice(){
        List<String> list = new ArrayList<String>();
        File file = new File(CAMERA_DEVICE_FILE_PATH);
        if(file.exists()){
            try {
                List<String> readLines = FileUtils.readLines(file);
                if(null != readLines){
                    int size = readLines.size();
                    for (int i = 0; i < size; i++) {
                        String[] split = readLines.get(i).replaceAll("##", "").replaceAll("-", "").split("#");
                        if(null != split){
                            if(split.length > 0){
                                list.add(split[0]);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                LogUtils.e("initStartCameraDevice","---------------------e = " + e.getMessage());
                e.printStackTrace();
            }
        }
        return list;
    }
    /**
     * 获取所有设备名称
     * @param context
     * @return
     */
    public static List<String> getDeviceName(Context context){
        List<String> list = new ArrayList<String>();
        File file = new File(CAMERA_DEVICE_FILE_PATH);
        if(file.exists()){
            try {
                List<String> readLines = FileUtils.readLines(file);
                if(null != readLines){
                    int size = readLines.size();
                    for (int i = 0; i < size; i++) {
                        String[] split = readLines.get(i).replaceAll("##", "").replaceAll("-", "").split("#");
                        if(null != split){
                            if(split.length > 0){
                                list.add(split[1]);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                LogUtils.e("getDeviceName","---------------------e = " + e.getMessage());
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 将content保存至SharedPreferences
     * @param key
     * @param content
     */
    public static void savePreference(String key, String content) {
        if (null == mContext){
            return;
        }
        SharedPreferences.Editor sharedata = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        sharedata.putString(key, content);
        sharedata.commit();
    }

    /**
     * 从SharedPreferences中取出内容
     *
     * @param key
     * @return
     */
    public static String getPreference(String key) {
        if (null == mContext){
            return "";
        }
        SharedPreferences sharedata = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedata.getString(key, "");
    }

    /**
     * 切割十秒视频文件并进行保存
     * @param filePath：待切割的视频源
     * @param ipCamDevice:IpCamDevice对象用来查询十秒小视频的存储路径
     * @return
     */
    public static boolean cutVideo(String filePath, IpCamDevice ipCamDevice){
        VideoDecoder mVideoDecoder = new VideoDecoder();
        File file = new File(filePath);
        String fileName = file.getName();
        LogUtils.e("cutVideo","fileName is "+fileName);
        String mSecondPath = ipCamDevice.getSecondPath();
        String savePath = mSecondPath+File.separator+fileName;
        LogUtils.e("savePath","savePath is "+savePath);
        boolean isSuccess = mVideoDecoder.decodeVideo(filePath,1000000,10000000,savePath);
        LogUtils.e("isSuccess","isSuccess is "+isSuccess);
        return isSuccess;
    }

    /**
     * 获取视频文件的缩略图
     * @param filePath
     * @return
     */

    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        try {
            bitmap = ThumbnailUtils.createVideoThumbnail(filePath,MINI_KIND);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 480, 320,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        } catch (Exception e) {
            if (null != e.getMessage()){
                LogUtils.e("getVideoThumbnail","Exception is "+e.getMessage().toString());
            }
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将bitmap保存为图片
     * @param bm
     * @param fileName
     * @throws IOException
     */
    public static void saveFile(Bitmap bm, String fileName){
//        String path = Constant.thumpPath;
//        File dirFile = new File(path);
//        if(!dirFile.exists()){
//            dirFile.mkdir();
//        }
//        File myCaptureFile = new File(fileName);
        BufferedOutputStream bos = null;
        File myCaptureFile = new File(fileName);
        LogUtils.e("saveFile","exist is "+myCaptureFile.exists());
        try {
            bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            LogUtils.e("saveFile", "exception is " + e.getMessage());
            try {
                if (null != bos){
                    bos.close();
                    bos = null;
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     *
     * @param filePath
     * @return
     */
    public static Bitmap getVideoThumbnail(String filePath, Handler handler) {
        Bitmap bitmap = null;
        try {
            bitmap = ThumbnailUtils.createVideoThumbnail(filePath,MINI_KIND);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 240, 160,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        } catch (Exception e) {
            if (null != e.getMessage()){
                LogUtils.e("getVideoThumbnail","Exception is "+e.getMessage().toString());
            }
            e.printStackTrace();
        }
        handler.sendEmptyMessage(100);
        return bitmap;
    }

    //int数组转字符串
    public static String intArrayToString(int[] source){
        String returnString = "";
        if (source != null){
            for(int i =0; i<source.length; i++){
                if (source[i] > 0){
                    returnString += source[i];
                }
                else {
                    returnString += 0;
                }
            }
        }
        return returnString;
    }
    //闹钟周期String转int数组并转换成对应的星期组成字符串
    public static String timesToWeeks(String times,Context mContext){
        boolean flag = false;
        boolean flag_1 = false;
        boolean flag_2 = false;
        boolean flag_3 = false;
        boolean flag_4 = false;
        boolean flag_5 = false;
        boolean flag_6 = false;
        boolean flag_7 = false;
        String returnString = "";
        int one = Integer.parseInt(String.valueOf(times.charAt(0)));
        int two = Integer.parseInt(String.valueOf(times.charAt(1)));
        int thr = Integer.parseInt(String.valueOf(times.charAt(2)));
        int four = Integer.parseInt(String.valueOf(times.charAt(3)));
        int five = Integer.parseInt(String.valueOf(times.charAt(4)));
        int six = Integer.parseInt(String.valueOf(times.charAt(5)));
        int seven = Integer.parseInt(String.valueOf(times.charAt(6)));
        int eight = Integer.parseInt(String.valueOf(times.charAt(7)));
        if (one > 0){
            returnString+=mContext.getString(R.string.monday)+" ";
            flag = true;
            flag_1 = true;
        }
        if (two > 0){
            if (flag){
                returnString += mContext.getString(R.string.two)+" ";
            }
            else {
                returnString += mContext.getString(R.string.tuesday)+" ";
            }
            flag = true;
            flag_2 = true;
        }
        if (thr > 0){
            if (flag){
                returnString += mContext.getString(R.string.thr)+" ";
            }
            else {
                returnString += mContext.getString(R.string.wednesday) + " ";
            }
            flag = true;
            flag_3 = true;
        }
        if (four > 0){
            if (flag){
                returnString += mContext.getString(R.string.fou)+" ";
            }
            else {
                returnString += mContext.getString(R.string.thursday)+" ";
            }
            flag = true;
            flag_4 = true;
        }
        if (five >0){
            if (flag){
                returnString += mContext.getString(R.string.five)+" ";
            }
            else {
                returnString += mContext.getString(R.string.friday)+" ";
            }
            flag = true;
            flag_5 = true;
        }
        if (six > 0){
            if (flag){
                returnString += mContext.getString(R.string.six)+" ";
            }
            else {
                returnString += mContext.getString(R.string.Saturday)+" ";
            }
            flag = true;
            flag_6 = true;
        }
        if (seven > 0){
            if (flag){
                returnString += mContext.getString(R.string.seven);
            }
            else {
                returnString += mContext.getString(R.string.sunday)+" ";
            }
            flag = true;
            flag_7 = true;
        }
        if (eight >0){//每天
            returnString  = mContext.getString(R.string.everyday);
        }
        //如果把所有的天数都选择了就显示每天
        if (flag_1 && flag_2 && flag_3 && flag_4 && flag_5 && flag_6 && flag_7){
            returnString = mContext.getString(R.string.everyday);
        }
        return  returnString;
    }

    public static boolean isVisBottom(RecyclerView recyclerView){
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        LogUtils.e("onScrollChange","最后可见position is"+lastVisibleItemPosition+"当前看到个数是："+visibleItemCount+"当前所有子项 "+totalItemCount);
        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        if(visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1){
            return true;
        }else {
            return false;
        }
    }

    //通过开始时间和结束时间计算录制的时间
    //通过开始时间和结束时间计算录制的时间
    public static int computeDuration(String startTime,String endTime){
        String[] startTimes = startTime.split(":");
        String[] endTimes = endTime.split(":");
        int duration = 0;
        if (Integer.parseInt(endTimes[0]) < Integer.parseInt(startTimes[0])){
            int startHour =  Integer.parseInt(startTimes[0]);
            int startMinutes = Integer.parseInt(startTimes[1]);
            int endHour = Integer.parseInt(endTimes[0]);
            int endMinutes = Integer.parseInt(endTimes[1]);
            int durationHour = endHour - startHour;
            int durationMinutes = endMinutes - startMinutes;
            duration = (durationHour*3600*1000+durationMinutes*60*1000);
        }
        else {
            int startHour =  Integer.parseInt(startTimes[0]);
            int startMinutes = Integer.parseInt(startTimes[1]);
            int endHour = Integer.parseInt(endTimes[0]);
            int endMinutes = Integer.parseInt(endTimes[1]);
            int durationHour = endHour - startHour;
            int durationMinutes = endMinutes - startMinutes;
            duration = durationMinutes*60*1000+durationHour*60*60*1000;
        }
        LogUtils.e("computeDuration","duration is "+duration);
        return duration;
    }

    /**
     * 将录制周期以int
     * @param times
     * @return
     */
    public static List<String> formatTimes(String times){
        List<String> times_String = new ArrayList<>();
        int valid_num = 0;
        for (int i =0; i<8; i++){
            String time = times.charAt(i)+"";
            if (!time.equals("0")){
                times_String.add(time);
            }
        }
        return times_String;
    }

    /**
     * 获取内部存储路径的容量大小
     * @param mContext
     */
    public static boolean getSDCardMemory(Context mContext,String sdcard){
        File sdcard_filedir = new File(sdcard);//得到sdcard的目录作为一个文件对象
        long usableSpace = sdcard_filedir.getUsableSpace();//获取文件目录对象剩余空间
        long totalSpace = sdcard_filedir.getTotalSpace();
        //将一个long类型的文件大小格式化成用户可以看懂的M，G字符串
        String usableSpace_str = Formatter.formatFileSize(mContext, usableSpace);
        String totalSpace_str = Formatter.formatFileSize(mContext, totalSpace);
        LogUtils.e("getSDCardMemory","usableSpace is "+usableSpace_str+"totalSpace is "+totalSpace_str);
        if(usableSpace < 1024 * 1024 * 1024){//判断剩余空间是否小于1G
            return  false;
        }
        else {
            return true;
        }
    }

    /**
     * 获取内部存储路径的容量大小
     */
    public static long getSDCardMemory(String sdcard){
        File sdcard_filedir = new File(sdcard);//得到sdcard的目录作为一个文件对象
        long usableSpace = sdcard_filedir.getUsableSpace();//获取文件目录对象剩余空间
        //转换成M
        int returnSize = (int) (usableSpace/1000000);
        return returnSize;
    }

    /**
     * 获取设备的存储设备，通过反射获取。
     * @param context
     * @return
     */
    public static List listAvaliableStorage(Context context) {
        ArrayList storagges = new ArrayList();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
//            Class<?>[] paramClasses = {};
//            Method getVolumeList = StorageManager.class.getMethod("getVolumeList", paramClasses);
//            getVolumeList.setAccessible(true);
            Method getVolumeList = StorageManager.class.getMethod("getVolumeList");
            Object[] params = {};
            Object[] invokes = (Object[]) getVolumeList.invoke(storageManager, params);
            LogUtils.e("listAvaliableStorage","invokes.size is "+invokes.length);
            if (invokes != null) {
                StorageInfo info = null;
                for (int i = 0; i < invokes.length; i++) {
                    Object obj = invokes[i];
                    Method getPath = obj.getClass().getMethod("getPath", new Class[0]);
                    String path = (String) getPath.invoke(obj, new Object[0]);
                    LogUtils.e("listAvaliableStorage","path is "+path);
                    info = new StorageInfo(path);
                    File file = new File(info.path);
                    if ((file.exists()) && (file.isDirectory()) && (file.canWrite())) {
                        Method isRemovable = obj.getClass().getMethod("isRemovable", new Class[0]);
                        String state = null;
                        try {
                            Method getVolumeState = StorageManager.class.getMethod("getVolumeState", String.class);
                            state = (String) getVolumeState.invoke(storageManager, info.path);
                            info.state = state;
                            LogUtils.e("listAvaliableStorage","state is "+state);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (info.isMounted()) {
                            //是否可插拔，可插拔的意味着是外接的存储设备
                            info.isRemoveable = ((Boolean) isRemovable.invoke(obj, new Object[0])).booleanValue();
                            LogUtils.e("listAvaliableStorage","isRemoveable is "+info.isRemoveable);
                            storagges.add(info);
                        }
                    }
                }
            }
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        storagges.trimToSize();
        return storagges;
    }

    /**
     * 通过反射获取所有的存储设备
     * @param mContext
     * @return
     */
    public static List<String> getAllStorages(Context mContext){
        List<String> paths = new ArrayList<>();
        try {
            StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
            Method getvolumlists = mStorageManager.getClass().getMethod("getVolumeList");
            Object[] storages = (Object[]) getvolumlists.invoke(mStorageManager);
            for (Object storage : storages){
                Method mGetPath = storage.getClass().getMethod("getPath");
                String path = (String) mGetPath.invoke(storage);
                LogUtils.e("getAllStorages","path is "+path);
                File mFile = new File(path);
                LogUtils.e("getAllStorages","exists is "+mFile.exists()+" canWrite is "+mFile.canWrite()+" isDirectory is "+mFile.isDirectory());
                if (mFile.exists() && mFile.isDirectory() && mFile.canWrite()){
                    LogUtils.e("getAllStorages","exists is "+mFile.exists()+"canwrite is "+mFile.canWrite()+"isDirectory is "+mFile.isDirectory());
                    Method isRemoveable = storage.getClass().getMethod("isRemovable");
                    boolean flag = (boolean) isRemoveable.invoke(storage);
                    Method getState = storage.getClass().getMethod("getState");
                    String state = (String) getState.invoke(storage);
                    if (flag && Environment.MEDIA_MOUNTED.equals(state)) {//如果是可移动的设备,加入到移动设备列表中
                        paths.add(path);
                    }
                    LogUtils.e("getAllStorages","isRemoveable is "+flag);
                }
                Method getStatus = mStorageManager.getClass().getMethod("getVolumeState",String.class);
                String status = (String) getStatus.invoke(mStorageManager,path);
                LogUtils.e("getAllStorages",status);
            }
        }
        catch (Exception e){
            LogUtils.e("exception",e.getMessage());
        }
        LogUtils.e("getAllStorages","paths.size is "+paths.size());
        return paths;
    }

    /**
     * 将对象序列化到本地
     */
    public static void serializableObject(){
        FileOutputStream fos = null;
        ObjectOutputStream obs = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        List<IpCamDevice> ipcams = new ArrayList<>();
        List<IpCamDevice> mIpCamDevices = new ArrayList<>();
        mIpCamDevices = findAll(IpCamDevice.class);
        LogUtils.e("createFile","size is "+mIpCamDevices.size());
        File serializable = new File(Constant.commonPath+File.separator+"serializable");
        File mFile = new File(serializable,"camDevice.dat");
        if (!serializable.exists()){
            serializable.mkdirs();
            try {
                mFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try{
            fis = new FileInputStream(mFile);
            ois = new ObjectInputStream(fis);
            ipcams = (List<IpCamDevice>) ois.readObject();
            LogUtils.e("createFile","ipcams.size is "+ipcams.size());
        }
        catch (IOException e){
            LogUtils.e("createFile",e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (fis != null){
                    fis.close();
                }
                if (ois != null){
                    ois.close();
                }
            }
            catch (Exception e){
                LogUtils.e("createFile",e.getMessage());
            }
        }
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     *            com.ipcamerasen5.record.service.RecordService
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName, String packgeName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            String mPackgeName = myList.get(i).service.getPackageName().toString();
            if (mName.equals(serviceName) && mPackgeName.equals(packgeName)) {
                isWork = true;
                break;
            }
        }
        AppLog.d("isServiceWork" + "iswork is " + isWork);
        return isWork;
    }


    /**
     * sensor触发录制的不用进行判断，十秒+三分钟直接忽略。只判断闹钟和24小时录制的视频
     * 判断能否进行录制，若不能则将之前录制的视频删除
     * @param duration :秒值
     */
    public static void makeSureRecordNomal(Context context,float duration) {
        LogUtils.e("makeSureRecordNomal","duration is "+duration);
        try {
            List<VideoForMemory> videos = new ArrayList<>();
            List<VideoForMemory> videos_db = new ArrayList<>();
            VideoForMemory video_1 = new VideoForMemory();
            float video_1_size = 0;
            List<VideoForMemory> video_2 = new ArrayList<>();
            float video_2_size = 0;
            List<VideoForMemory> video_5 = new ArrayList<>();
            float video_5_size = 0;
            List<VideoForMemory> video_10 = new ArrayList<>();
            float video_10_size = 0;
            float useMemory = (float) (duration / 36 * 1.4);
//       LogUtils.e("makeSureRecordNomal","float is "+duration/36);
            if (CommonTools.getAllStorages(MainApplication.getWholeContext()).size() <= 0){
                return;
            }
            String filePath = CommonTools.getAllStorages(MainApplication.getWholeContext()).get(0);
            LogUtils.e("makeSureRecordNomal", "floatsize is " + getSDCardMemory(filePath) + "useMemory is " + useMemory);
            //还剩容量，单位为M
            float allMemory = getSDCardMemory(filePath);
            if ((allMemory - useMemory) > 1024) {
                //如果现有容量减去将要使用的大于1G，则继续进行录制
                return;
            } else {
                //删除视频，留出容量进行录制
                videos_db.addAll(findAll(VideoForMemory.class));
                LogUtils.e("makeSureRecordNomal", "容量大小不足1G"+" size is " + videos_db.size() );
                for (int i = 0; i < videos_db.size(); i++ ){
                    File mFile = null;
                    if (null != videos_db.get(i).getFilePath() && !"".equals(videos_db.get(i).getFilePath())){
                        mFile = new File(videos_db.get(i).getFilePath());
                    }
                    if (null != mFile && mFile.exists()){
                        //如果视频文件存在，则将其添加到videos集合
                        videos.add(videos_db.get(i));
                    }
                }
                LogUtils.e("makeSureRecordNomal", "容量大小不足1G,添加所有数据库数据" + "size is  " + videos.size());

                //未存储直接退出
                if (null == videos || videos.size() == 0) {
                    return;
                }
                LogUtils.e("makeSureRecordNomal", "video.size >= 1");
                if (videos.size() >= 1) {
                    video_1 = videos.get(0);
                    video_1_size = video_1.getFileSize();
                    LogUtils.e("makeSureRecordNomal", "video1_1.size is " + video_1_size);
                }
                LogUtils.e("makeSureRecordNomal", "video.size >= 2");
                if (videos.size() >= 2) {
                    video_2.add(videos.get(0));
                    video_2.add(videos.get(1));
                    for (int i = 0; i < video_2.size(); i++) {
                        video_2_size += video_2.get(i).getFileSize();
                    }
                    LogUtils.e("makeSureRecordNomal", "video1_1.size is " + video_2_size);
                }
                LogUtils.e("makeSureRecordNomal", "video.size >= 5");
                if (videos.size() >= 5) {
                    for (int i = 0; i < videos.size(); i++) {
                        if (i < 5) {
                            video_5.add(videos.get(i));
                        } else {
                            break;
                        }
                    }
                    for (int i = 0; i < video_5.size(); i++) {
                        video_5_size += video_5.get(i).getFileSize();
                    }
                    LogUtils.e("makeSureRecordNomal", "video1_1.size is " + video_5_size);
                }
                LogUtils.e("makeSureRecordNomal", "video.size >= 5");
                if (videos.size() >= 10) {
                    for (int i = 0; i < videos.size(); i++) {
                        if (i < 10) {
                            video_10.add(videos.get(i));
                        } else {
                            break;
                        }
                    }
                    for (int i = 0; i < video_10.size(); i++) {
                        video_10_size += video_10.get(i).getFileSize();
                    }
                    LogUtils.e("makeSureRecordNomal", "video1_1.size is " + video_10_size);
                }
                if (null != video_1 && video_1.getFileSize() > useMemory) {
                    LogUtils.e("makeSureRecordNomal", "filePath is  " + video_1.getFilePath());
                    if (null != video_1.getFilePath()) {
                        File mFile = new File(video_1.getFilePath());
                        if (null != mFile && mFile.exists()) {
                            mFile.delete();
                        }
                    }
                    //删除数据库中的记录
                    video_1.deleteFromDB();
                    video_1.delete();
                } else if (null != video_2 && video_2_size > useMemory) {
                    LogUtils.e("makeSureRecordNomal", "filePath is  " + video_1.getFilePath());
                    for (int i = 0; i < video_2.size(); i++) {
                        LogUtils.e("makeSureRecordNomal", "filePath is  " + video_2.get(i).getFilePath());
                        if (null != video_2.get(i).getFilePath()) {
                            File mFile = new File(video_2.get(i).getFilePath());
                            if (null != mFile && mFile.exists()) {
                                mFile.delete();
                            }
                        }
                        video_2.get(i).deleteFromDB();
                        video_2.get(i).delete();
                    }
                } else if (null != video_5 && video_5_size > useMemory) {
                    for (int i = 0; i < video_5.size(); i++) {
                        LogUtils.e("makeSureRecordNomal", "filePath is  " + video_5.get(i).getFilePath());
                        if (null != video_5.get(i).getFilePath()) {
                            File mFile = new File(video_5.get(i).getFilePath());
                            if (null != mFile && mFile.exists()) {
                                mFile.delete();
                            }
                        }
                        video_5.get(i).deleteFromDB();
                        video_5.get(i).delete();
                    }
                } else if (null != video_10 && video_10_size >= useMemory) {
                    LogUtils.e("makeSureRecordNomal", "filePath is  " + video_1.getFilePath());
                    for (int i = 0; i < video_10.size(); i++) {
                        LogUtils.e("makeSureRecordNomal", "filePath is  " + video_10.get(i).getFilePath());
                        if (null != video_10.get(i).getFilePath()) {
                            File mFile = new File(video_10.get(i).getFilePath());
                            if (null != mFile && mFile.exists()) {
                                mFile.delete();
                            }
                        }
                        video_10.get(i).deleteFromDB();
                        video_10.get(i).delete();
                    }
                }
            }
        }
        catch (Exception e){
            LogUtils.e("makeSureRecordNomal","e.getMessage is "+e.getMessage());
        }
    }

    /**
     *
     * @return
     * @param did :根据did返回当前设备
     */
    public static IpCamDevice findDeviceFromDB(String did){
        List<IpCamDevice> mIpCamDevices = DataSupport.findAll(IpCamDevice.class);
        AppLog.e("findDeviceFromeDB mIPcameDevices.size is " + mIpCamDevices.size());
        IpCamDevice mIpCamDevice = null;
        if (null != mIpCamDevices && mIpCamDevices.size() != 0){
            for (int i = 0; i<mIpCamDevices.size(); i++){
                if ((null != mIpCamDevices.get(i).getDid()) &&(mIpCamDevices.get(i).getDid().equals(did))){
                    mIpCamDevice = mIpCamDevices.get(i);
                }
            }
        }
        return mIpCamDevice;
    }

    public static void deleteOneDeviceFromDB(String did){
        List<IpCamDevice> mIpCamDevices = DataSupport.findAll(IpCamDevice.class);
        AppLog.e("deleteOneDeviceFromeDB did is " + did+" mIpcameras.size is " + mIpCamDevices.size());
        IpCamDevice mIpCamDevice = null;
        if (mIpCamDevices.size() != 0){
            for (int i =0 ;i < mIpCamDevices.size();i++){
                if ((null != mIpCamDevices.get(i).getDid()) &&(mIpCamDevices.get(i).getDid().equals(did))){
                    mIpCamDevice = mIpCamDevices.get(i);
                }
            }
        }
        if (null != mIpCamDevice){
            AppLog.e("deleteOneDeviceFromDB delete");
            mIpCamDevice.delete();
        }
    }

    /**
     * 当前时间到开始闹钟时间过去了多久
     * @param currentTime
     * @param otherTime
     * @return
     */
    public static long computerDuration(int currentTime,int otherTime){
        long duration = 0;
        int currentHour = currentTime/100;
        int currentMin = currentTime%100;
        int noRoundHour = otherTime/100;
        int noRoundMin = otherTime%100;
//        if (noRoundMin > currentMin){
//            duration = 3600000 * (currentHour - noRoundHour) - 60000 * ((60 - (noRoundMin - currentMin)));
//        }
//        else {
//            duration = 3600000 * (currentHour - noRoundHour) + 60000 * (currentMin - noRoundMin);
//        }
        int durationTime = currentTime - otherTime;
        int hour = durationTime/100;
        int min = durationTime%100;
        duration = 3600000 * hour + 60000 * (min);
        LogUtils.e("computerDuration","duration is "+duration);
        return duration;
    }

    /**
     * 通过clock转换成
     * @param mClocks
     * @return
     */
    public static List<ClockTempObject> getAllClock(List<Clock> mClocks){
        List<ClockTempObject> mClockTempObjects = new ArrayList<>();
        for (Clock mClock : mClocks) {
            if (mClock.getDuration() <= 0) {
                continue;
            }
            String time = mClock.getStartTime();
            time = time.replace(":", "");
            String time2 = mClock.getTimes();//02045678:大于零表示当天有该闹钟:周一到周日中哪些需要录制
            List<String> cycle = CommonTools.formatTimes(time2);
            if (cycle.contains("8")) {
                String roundTime = 8 + time;//81703:代表每天下午5点零三开始录制。八代表everyDay
                int duration = mClock.getDuration();
                String did = mClock.getDid();
                ClockTempObject mClockTempObject = new ClockTempObject();
                mClockTempObject.setDuration(duration);
                mClockTempObject.setDid(did);
                mClockTempObject.setHasStartRecord(false);
                mClockTempObject.setStartTime(roundTime);
                mClockTempObjects.add(mClockTempObject);
            } else {
                for (int i = 0; i < cycle.size(); i++) {
                    String roundTime = cycle.get(i) + time;
                    int duration = mClock.getDuration();
                    String did = mClock.getDid();
                    ClockTempObject mClockTempObject = new ClockTempObject();
                    mClockTempObject.setDuration(duration);
                    mClockTempObject.setDid(did);
                    mClockTempObject.setHasStartRecord(false);
                    mClockTempObject.setStartTime(roundTime);
                    mClockTempObjects.add(mClockTempObject);
                }
            }
        }
        return  mClockTempObjects;
    }

    /**
     * U盘路径改变时
     * @param filePath
     */
    public static void initFile(String filePath) {
        List<IpCamDevice> devices = new ArrayList<>();
        Constant.commonPath = filePath + File.separator + "IPCamera";
        Constant.recordPath = Constant.commonPath + File.separator + "record";//record视频文件
        Constant.thumpPath = Constant.commonPath + File.separator + "thump";//视频缩略图文件
        CommonTools.createCommonFolder(Constant.recordPath);
        CommonTools.createCommonFolder(Constant.thumpPath);
        devices = DataSupport.findAll(IpCamDevice.class);
        if (devices.size() != 0) {
            for (IpCamDevice device : devices) {
                LogUtils.e("createFile", did);
                File file = new File(Constant.recordPath);
                String did = device.getDid();
                boolean isOk = CommonTools.createFolder(file, did);
                if (isOk) {
                    File file_did = new File(Constant.recordPath + File.separator + did);
                    boolean isOk2 = CommonTools.createFolder(file_did, "sensor");
                    if (isOk2) {
                        device.setSensorPath(file_did.getAbsolutePath() + File.separator + "sensor");
                    }
                    boolean isOk3 = CommonTools.createFolder(file_did, "second");
                    if (isOk3) {
                        device.setSecondPath(file_did.getAbsolutePath() + File.separator + "second");
                    }
                    boolean isOk4 = CommonTools.createFolder(file_did, "clock");
                    if (isOk4) {
                        device.setClockPath(file_did + File.separator + "clock");
                    }
                    boolean isok5 = CommonTools.createFolder(file_did, "round");
                    if (isok5) {
                        device.setRoundPath(file_did + File.separator + "round");
                    }
                    boolean isok6 = CommonTools.createFolder(new File(Constant.thumpPath), did);
                    if (isok6) {
                        device.setThumpPath(Constant.thumpPath + File.separator + did);
                    }
                }
                device.save();
            }
        }
    }

    /**
     * 确保每个文件夹都被创建了,不在则重新创建
     */
    public static void makeSureFile() {
        String filePath = getPreference(Constant.lastStorage);
        List<IpCamDevice> devices = new ArrayList<>();
        Constant.commonPath = filePath + File.separator + "IPCamera";
        File mFile_CommonPath = new File(Constant.commonPath);
        Constant.recordPath = Constant.commonPath + File.separator + "record";//record视频文件
        File mFile_recordPath = new File(Constant.recordPath);
        Constant.thumpPath = Constant.commonPath + File.separator + "thump";//视频缩略图文件
        File mFile_thumpPath = new File(Constant.thumpPath);
        if (!mFile_CommonPath.exists()){
            CommonTools.createCommonFolder(Constant.commonPath);
        }
        if (!mFile_recordPath.exists()){
            CommonTools.createCommonFolder(Constant.recordPath);
        }
        if (!mFile_thumpPath.exists()){
            CommonTools.createCommonFolder(Constant.thumpPath);
        }
        devices = DataSupport.findAll(IpCamDevice.class);
        if (devices.size() != 0) {
            for (IpCamDevice device : devices) {
                LogUtils.e("createFile", did);
                File file = new File(Constant.recordPath);
                String did = device.getDid();
                boolean isOk = CommonTools.createFolder(file, did);
                if (isOk) {
                    File file_did = new File(Constant.recordPath + File.separator + did);
                    boolean isOk2 = CommonTools.createFolder(file_did, "sensor");
                    if (isOk2) {
                        device.setSensorPath(file_did.getAbsolutePath() + File.separator + "sensor");
                    }
                    boolean isOk3 = CommonTools.createFolder(file_did, "second");
                    if (isOk3) {
                        device.setSecondPath(file_did.getAbsolutePath() + File.separator + "second");
                    }
                    boolean isOk4 = CommonTools.createFolder(file_did, "clock");
                    if (isOk4) {
                        device.setClockPath(file_did + File.separator + "clock");
                    }
                    boolean isok5 = CommonTools.createFolder(file_did, "round");
                    if (isok5) {
                        device.setRoundPath(file_did + File.separator + "round");
                    }
                    boolean isok6 = CommonTools.createFolder(new File(Constant.thumpPath), did);
                    if (isok6) {
                        device.setThumpPath(Constant.thumpPath + File.separator + did);
                    }
                    boolean isok7 = CommonTools.createFolder(file_did,"dynamic");
                    if (isok7){
                        device.setDynamicPath(file_did+File.separator+"dynamic");
                    }
                }
                device.save();
            }
        }
    }


    /**
     * 创建所有的摄像头对应的文件夹
     */
    public static void createFile() {
        boolean storageCanUse;
        List<String> gids_db = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> gids = CommonTools.initStartCameraDevice();
        AppLog.e("createFile"+ " gids.size is " + gids.size());
        List<IpCamDevice> devices = new ArrayList<>();
        List<String> paths = getAllStorages(MainApplication.getWholeContext());
//        if (paths.size() == 0){
//            return;
//        }
        if (paths.size() > 0){
            //判断u盘是否可用
//            storageCanUse =  CommonTools.getSDCardMemory(mContext,paths.get(0));
//            if (!storageCanUse){
//                // TODO: 2017/9/4 提示该存储器所剩太小
////                mCustomDialog_storage_tip.show();
//                return;
//            }
            //如果只存在一个移动磁盘
            storageCanUse = true;
            if (storageCanUse) {
                String lastPath = CommonTools.getPreference(Constant.lastStorage);
                CommonTools.savePreference(Constant.lastStorage, paths.get(0));
                Constant.commonPath = paths.get(0) + File.separator + "IPCamera";
                Constant.recordPath = Constant.commonPath + File.separator + "record";//record视频文件
                Constant.thumpPath = Constant.commonPath + File.separator + "thump";//视频缩略图文件
                CommonTools.createCommonFolder(Constant.recordPath);
                CommonTools.createCommonFolder(Constant.thumpPath);
                names = CommonTools.getDeviceName(mContext);
                LogUtils.e("createFile","names.size is "+names.size());
                devices = DataSupport.findAll(IpCamDevice.class);
                for (IpCamDevice mIpCamDevice : devices) {
                    gids_db.add(mIpCamDevice.getDid());
                    LogUtils.e("createFile","record_type is "+mIpCamDevice.getRecordType());
                    //如果U盘路径发生改变
                    if (null != lastPath && (!lastPath.equals("")) && !lastPath.equals(paths.get(0))){
                        initFile(paths.get(0));
                    }
                }
                if (gids.size() != 0) {
                    for (String did : gids) {
                        //如果数据库中不包含该gid则进行创建
                        if (!gids_db.contains(did)) {
                            LogUtils.e("createFile", did);
                            IpCamDevice device = new IpCamDevice();
                            device.setDid(did);
                            device.setIsStop("2");//默认录制按钮打开
                            device.setSecondIsOpen("2");//默认sensor触发十秒钟打开
                            int position = gids.indexOf(did);
                            device.setAliasName(names.get(position));
                            File file = new File(Constant.recordPath);
                            boolean isOk = CommonTools.createFolder(file, did);
                            if (isOk) {
                                File file_did = new File(Constant.recordPath + File.separator + did);
                                boolean isOk2 = CommonTools.createFolder(file_did, "sensor");
                                if (isOk2) {
                                    device.setSensorPath(file_did.getAbsolutePath() + File.separator + "sensor");
                                }
                                boolean isOk3 = CommonTools.createFolder(file_did, "second");
                                if (isOk3) {
                                    device.setSecondPath(file_did.getAbsolutePath() + File.separator + "second");
                                }
                                boolean isOk4 = CommonTools.createFolder(file_did, "clock");
                                if (isOk4) {
                                    device.setClockPath(file_did + File.separator + "clock");
                                }
                                boolean isok5 = CommonTools.createFolder(file_did, "round");
                                if (isok5) {
                                    device.setRoundPath(file_did + File.separator + "round");
                                }
                                boolean isok6 = CommonTools.createFolder(new File(Constant.thumpPath), did);
                                if (isok6) {
                                    device.setThumpPath(Constant.thumpPath + File.separator + did);
                                }
                                boolean isok7 = CommonTools.createFolder(file_did, "dynamic");
                                if (isok7) {
                                    device.setDynamicPath(file_did + File.separator + "dynamic");
                                }
                                device.setStatus("0");
                                device.setIsStop("2");
                                device.setSecondIsOpen("2");
                                device.save();//存储(新增一行)
                            }
                        }
                    }
                }
                CommonTools.savePreference( Constant.isFirst, Constant.isFirst_false);
                CommonTools.savePreference(Constant.lastStorage, paths.get(0));
            }
        }
        else {
            names = CommonTools.getDeviceName(mContext);
            devices = DataSupport.findAll(IpCamDevice.class);
            for (IpCamDevice mIpCamDevice : devices) {
                gids_db.add(mIpCamDevice.getDid());
                LogUtils.e("createFile","record_type is "+mIpCamDevice.getRecordType());
            }
            if (gids.size() != 0) {
                for (String did : gids) {
                    //如果数据库中不包含该gid则进行创建
                    if (!gids_db.contains(did)) {
                        LogUtils.e("createFile", did);
                        IpCamDevice device = new IpCamDevice();
                        device.setDid(did);
                        int position = gids.indexOf(did);
                        device.setAliasName(names.get(position));
                        device.setStatus("0");
                        device.setIsStop("2");
                        device.setSecondIsOpen("2");
                        device.save();//存储(新增一行)
                    }
                }
            }
        }
    }
}

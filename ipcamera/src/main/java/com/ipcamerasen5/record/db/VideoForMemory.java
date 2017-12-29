package com.ipcamerasen5.record.db;

import com.ipcamerasen5.record.common.Constant;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import nes.ltlib.utils.LogUtils;

/**
 * 记录每个视频文件
 * 排序删除
 * Created by chenqianghua on 2017/6/30.
 */


public class VideoForMemory extends DataSupport{
    private String filePath;
    private int record_type;
    private String fileName;//通过fileName去标识删除数据库中的记录
    private float fileSize;//单位size(M)

    public int getRecord_type() {
        return record_type;
    }

    public void setRecord_type(int record_type) {
        this.record_type = record_type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public float getFileSize() {
        return fileSize;
    }

    public void setFileSize(float fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    //删除数据库中的存储记录
    public  void deleteFromDB() {
        try {
            LogUtils.e("deleteFromDB", "type is " + record_type + " fileName is " + fileName);
            switch (record_type) {
                case Constant.type_clock:
                    List<ClockPath> mClockPaths = new ArrayList<>();
                    mClockPaths.addAll(DataSupport.where("fileName = ?", fileName).find(ClockPath.class));
                    if (null != mClockPaths && mClockPaths.size() != 0) {
                        LogUtils.e("deleteFromDB", "fileName is " + fileName);
                        mClockPaths.get(0).delete();
                    }
                    break;
                case Constant.type_round:
                    List<RoundPath> mRoundPaths = new ArrayList<>();
                    mRoundPaths.addAll(DataSupport.where("fileName = ?", fileName).find(RoundPath.class));
                    if (null != mRoundPaths && mRoundPaths.size() != 0) {
                        LogUtils.e("deleteFromDB", "fileName is " + fileName);
                        mRoundPaths.get(0).delete();
                    }
                    break;
            }
        }
        catch (NullPointerException e){
            LogUtils.e("deleteFromeDB","e.getMessage is "+ e.getMessage());
        }
    }

}

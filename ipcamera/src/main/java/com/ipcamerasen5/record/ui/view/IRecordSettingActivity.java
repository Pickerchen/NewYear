package com.ipcamerasen5.record.ui.view;

/**
 * Created by chenqianghua on 2017/4/18.
 */

public interface IRecordSettingActivity {
    void timedIsSelected(int position);//闹钟录制被选中
    void timedIsnotSelected(int position);//选中其他的录制方式
    void changeRecordingType(int position);//更改recording type的title显示
    void setDeviceImage(String jpgPath);//设置最近视频缩略图
    void setinClude_clockView(int type, String... times);//设置闹钟的配置
    void setDeviceName(String name);//设置设备的名字
    void setDeviceStatus(String type);//设置设备的状态
    void setLastRepeatInfo(String content);//设置上次设置的闹钟重复信息

}

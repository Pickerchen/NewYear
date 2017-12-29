package com.ipcamerasen5.record.callback;

/**
 * Created by chenqianghua on 2017/4/24.
 */

public interface DialogCallback {
    void repeatCallback(int[] repeats);
    void chooseStorageCallback(String paths);//选择存储路径
    void makeSureCallback(String path);//确定更换存储
}

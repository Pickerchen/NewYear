package com.sen5.secure.launcher.utils;


import com.sen5.secure.launcher.data.entity.WorkspaceData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangyicheng on 2016/12/20.
 */

public class UtilsControlApp {
    private static ArrayList<String> mHidAppList = new ArrayList<String>();
    private static final String GOOGLECOUNT1 = "com.google.android.gsf.login";
    private static final String GOOGLECOUNT2 = "com.google.android.apps.plus";
    private static final String GOOGLE = "com.google.android.googlequicksearchbox";
    private static final String GOOGLESETTING = "com.google.android.gms";

    public static ArrayList<String> getHideApp(){
        mHidAppList.add(GOOGLECOUNT1);//Google+
        mHidAppList.add(GOOGLECOUNT2);//Google+

        mHidAppList.add(GOOGLE); //Google
        mHidAppList.add(GOOGLESETTING);//Google setting
        List<WorkspaceData> hideAppByXml = UtilXml.getDataByXml("HideApps.config");
        if(null != hideAppByXml){
            int size = hideAppByXml.size();
            for (int i = 0; i < size; i++) {
                WorkspaceData entityHideApp = hideAppByXml.get(i);
                mHidAppList.add(entityHideApp.getPackageName());
            }
        }
        return mHidAppList;
    }
}

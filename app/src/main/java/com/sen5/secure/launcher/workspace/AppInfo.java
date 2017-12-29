package com.sen5.secure.launcher.workspace;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;

import java.util.HashMap;

/**
 * App信息
 * 
 * @author Matt.Xie
 */
public class AppInfo {

    public Intent intent;
    public ComponentName componentName;
    public Bitmap iconBitmap;
    public boolean isAPP = true;
    /** App名称 */
    public CharSequence title;

    public int position = 0;// app 在Launcher中的位置

    public AppInfo(String packageName, String className) {
        componentName = new ComponentName(packageName, className);
    }

    public AppInfo() {

    }

    public AppInfo(PackageManager packageManager, ResolveInfo info, IconCache iconCache,
                   HashMap<Object, CharSequence> labelCache) {
        // ComponentName
        componentName = new ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name);
        // title
        title = info.loadLabel(packageManager);

        // this.drawable = info.loadIcon(packageManager);
        // drawable = info.activityInfo.loadIcon(packageManager);
        // intent
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        this.intent = intent;
        // icon
        iconCache.getTitleAndIcon(this, info, labelCache);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AppInfo && null != this.componentName && null != ((AppInfo) o).componentName) {
            return ((AppInfo) o).componentName.getPackageName().equals(this.componentName.getPackageName());
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        return "AppInfo [componentName=" + componentName + ", title=" + title + ", position=" + position + "]";
    }
}

package com.sen5.secure.launcher.workspace;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.List;

public class AllAppsList {

    /** The list off all apps. */
    public ArrayList<AppInfo> mListApps = new ArrayList<AppInfo>();
    /** The list of apps that have been added since the last notify() call. */
    public ArrayList<AppInfo> added = new ArrayList<AppInfo>();
    /** The list of apps that have been removed since the last notify() call. */
    public ArrayList<AppInfo> removed = new ArrayList<AppInfo>();
    /** The list of apps that have been modified since the last notify() call. */
    public ArrayList<AppInfo> modified = new ArrayList<AppInfo>();

    private IconCache mIconCache;

    public AllAppsList(Context context, IconCache iconCache) {
        mIconCache = iconCache;
    }

    /**
     * 添加app
     */
    public void addPackage(Context context, String packageName) {
        final List<ResolveInfo> matches = findActivitiesForPackage(context, packageName);

        if (matches.size() > 0) {
            for (ResolveInfo info : matches) {
                // add(new AppInfo(context, info));
                add(new AppInfo(context.getPackageManager(), info, mIconCache, null));
            }
        }
    }

    /**
     * Remove the apps for the given apk identified by packageName.
     */
    public void removePackage(String packageName) {
        final List<AppInfo> data = mListApps;
        for (int i = data.size() - 1; i >= 0; i--) {
            AppInfo info = data.get(i);
            final ComponentName component = info.intent.getComponent();
            if (packageName.equals(component.getPackageName())) {
                removed.add(info);
                data.remove(i);
            }
        }
        // This is more aggressive than it needs to be.
        mIconCache.flush();
    }

    /**
     * Add and remove icons for this package which has been updated.
     */
    public void updatePackage(Context context, String packageName) {
        final List<ResolveInfo> matches = findActivitiesForPackage(context, packageName);
        final List<AppInfo> listAppMatches = new ArrayList<AppInfo>();
        for (int i = 0; i < matches.size(); i++) {
            // AppInfo appInfo = new AppInfo(context, matches.get(i));
            AppInfo appInfo = new AppInfo(context.getPackageManager(), matches.get(i), mIconCache, null);
            listAppMatches.add(appInfo);
        }
        if (matches.size() > 0) {
            // Find disabled/removed activities and remove them from data and
            // add them
            // to the removed list.
            for (int i = mListApps.size() - 1; i >= 0; i--) {
                final AppInfo applicationInfo = mListApps.get(i);
                final ComponentName component = applicationInfo.intent.getComponent();
                if (packageName.equals(component.getPackageName())) {
                    if (!findActivity(listAppMatches, component)) {
                        removed.add(applicationInfo);
                        mIconCache.remove(component);
                        mListApps.remove(i);
                    }
                }
            }

            // Find enabled activities and add them to the adapter
            // Also updates existing activities with new labels/icons
            int count = matches.size();
            for (int i = 0; i < count; i++) {
                final ResolveInfo info = matches.get(i);
                AppInfo applicationInfo = findApplicationInfoLocked(info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name);
                if (applicationInfo == null) {
                    add(new AppInfo(context.getPackageManager(), info, mIconCache, null));
                } else {
                    mIconCache.remove(applicationInfo.componentName);
                    mIconCache.getTitleAndIcon(applicationInfo, info, null);
                    modified.add(applicationInfo);
                }
            }
        } else {
            // Remove all data for this package.
            for (int i = mListApps.size() - 1; i >= 0; i--) {
                final AppInfo applicationInfo = mListApps.get(i);
                final ComponentName component = applicationInfo.intent.getComponent();
                if (packageName.equals(component.getPackageName())) {
                    removed.add(applicationInfo);
                    mIconCache.remove(component);
                    mListApps.remove(i);
                }
            }
        }
    }

    /**
     * 将app添加到列表中,并进入排队 list to broadcast when notify() is called.
     * 
     * 如果应用程序已经在列表中,不添加它。
     */
    public void add(AppInfo info) {
        if (findActivity(mListApps, info.componentName)) {
            return;
        }
        mListApps.add(info);
        added.add(info);
    }

    /**
     * Returns whether <em>apps</em> contains <em>component</em>.
     */
    private static boolean findActivity(List<AppInfo> apps, ComponentName component) {
        final String className = component.getClassName();
        for (AppInfo info : apps) {
            if (info.componentName.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Query the package manager for MAIN/LAUNCHER activities in the supplied
     * package.
     */
    private static List<ResolveInfo> findActivitiesForPackage(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setPackage(packageName);

        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
        return apps != null ? apps : new ArrayList<ResolveInfo>();
    }

    /**
     * Find an ApplicationInfo object for the given packageName and className.
     */
    private AppInfo findApplicationInfoLocked(String packageName, String className) {
        for (AppInfo info : mListApps) {
            final ComponentName component = info.intent.getComponent();
            if (packageName.equals(component.getPackageName()) && className.equals(component.getClassName())) {
                return info;
            }
        }
        return null;
    }

}

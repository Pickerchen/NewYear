package com.sen5.secure.launcher.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.eric.xlee.lib.utils.LogUtil;
import com.sen5.secure.launcher.data.entity.WorkspaceData;

import java.util.ArrayList;
import java.util.List;

import nes.ltlib.utils.AppLog;

/**
 * 
 * @author XIE
 * 
 */
public class WorkspaceDataService {
    private static final String TAG = WorkspaceDataService.class.getSimpleName();

    private volatile static WorkspaceDataService sWorkspaceDataService;
    private DBOpenHelper mDBOpenHelper = null;
    private SQLiteDatabase mSQLiteDB = null;

    public static WorkspaceDataService getInstance(Context context) {
        if (null == sWorkspaceDataService) {
            synchronized (WorkspaceDataService.class) {
                if (null == sWorkspaceDataService) {
                    sWorkspaceDataService = new WorkspaceDataService(context.getApplicationContext());
                }
            }
        }
        return sWorkspaceDataService;
    }

    private WorkspaceDataService(Context context) {
        mDBOpenHelper = new DBOpenHelper(context);
        // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
        // mFactory);
        mSQLiteDB = mDBOpenHelper.getWritableDatabase();
    }

    /**
     * 判断数据库是否已打开
     * 
     * @return 判断结果
     */
    public boolean isOpen() {
        return mSQLiteDB.isOpen();
    }

    /**
     * 按照字段判断数据是否存在
     * 
     * @param strField
     * @param strContent
     * @return
     */
    public boolean judge(String strField, String strContent) {
        try {
            Cursor cursor = mSQLiteDB.rawQuery("SELECT * FROM WorkspaceData WHERE " + strField + "=?",
                    new String[] { strContent });
            if (cursor.getCount() > 0) {

                return true;
            } else {

                return false;
            }
        } finally {

        }
    }

    /**
     * 保存一条数据
     * 
     * @param workspaceData
     * @return
     */
    public boolean setWorkspaceData(WorkspaceData workspaceData) {
        mSQLiteDB.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put("PackageName", workspaceData.getPackageName());
            cv.put("ClassName", workspaceData.getClassName());
            cv.put("IsAdded", workspaceData.getIsAdded());
            cv.put("AppOrder", workspaceData.getAppOrder());
            cv.put("Remark", workspaceData.getRemark());
            if (judge("PackageName", workspaceData.getPackageName())) {
                if (judge("ClassName", workspaceData.getPackageName())) {
                    mSQLiteDB.update("WorkspaceData", cv, "PackageName=?",
                            new String[] { workspaceData.getPackageName() });
                } else {
                    mSQLiteDB.insert("WorkspaceData", null, cv);
                    AppLog.i(TAG+ "mSQLiteDB.insert() PackageName == " + workspaceData.getPackageName());
                }
            } else {
                mSQLiteDB.insert("WorkspaceData", null, cv);
                AppLog.i(TAG+ "mSQLiteDB.insert() PackageName == " + workspaceData.getPackageName());
            }
            mSQLiteDB.setTransactionSuccessful();
            AppLog.i(TAG+ "保存成功  PackageName == " + workspaceData.getPackageName());
            return true;

        } catch (Exception e) {
            AppLog.i(TAG+ "保存失败:" + e.toString());
            return false;

        } finally {
            mSQLiteDB.endTransaction();
        }

    }

    /**
     * 更新数据
     * 
     * @param strPackage
     * @param strClass
     * @param strAppOrder
     * @return
     */
    public boolean updateWorkspaceData(String strPackage, String strClass, String strAppOrder) {

        mSQLiteDB.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put("AppOrder", strAppOrder);

            if (judge("PackageName", strPackage)) {
                if (judge("ClassName", strClass)) {
                    mSQLiteDB.update("WorkspaceData", cv, "PackageName=? AND ClassName=?", new String[] { strPackage,
                            strClass });
                } else {
                    AppLog.e(TAG+ "保存失败:数据不存在");
                    return false;
                }
            } else {
                AppLog.e(TAG+ "保存失败:数据不存在");
                return false;
            }
            mSQLiteDB.setTransactionSuccessful();
            AppLog.i(TAG+ "保存成功");
            return true;

        } catch (Exception e) {
            AppLog.e(TAG+ "保存失败:" + e.toString());
            return false;

        } finally {
            mSQLiteDB.endTransaction();
        }

    }

    /**
     * 返回一条数据
     * 
     * @param strSql
     *            SQL查询语句
     * @return 返回WorkspaceData
     */
    public WorkspaceData getWorkspaceData(String strSql) {

        WorkspaceData workspaceData = new WorkspaceData();
        mSQLiteDB.beginTransaction();
        Cursor cursor = mSQLiteDB.rawQuery(strSql, null);
        try {
            if (cursor.moveToFirst()) {

                setWorkspaceData(workspaceData, cursor);
            }
            mSQLiteDB.setTransactionSuccessful();

        } finally {
            cursor.close();
            mSQLiteDB.endTransaction();
        }
        return workspaceData;
    }

    /**
     * 返回n条数据
     * 
     * @param strSql
     *            SQL查询语句
     * @return 返回WorkspaceData的集合
     */
    public List<WorkspaceData> getListWorkspaceData(String strSql) {
        List<WorkspaceData> listWorkspaceData = new ArrayList<WorkspaceData>();
        mSQLiteDB.beginTransaction();
        Cursor cursor = mSQLiteDB.rawQuery(strSql, null);
        try {
            while (cursor.moveToNext()) {
                WorkspaceData workspaceData = new WorkspaceData();
                setWorkspaceData(workspaceData, cursor);
                listWorkspaceData.add(workspaceData);
            }
            mSQLiteDB.setTransactionSuccessful();
        } finally {
            cursor.close();
            mSQLiteDB.endTransaction();
        }
        return listWorkspaceData;
    }

    /**
     * 根据IsLoadedGames字段需求返回n条数据
     * 
     * @param strIsLoadedGames
     */
    public List<WorkspaceData> getListLoadedData(String strIsLoadedGames) {
        List<WorkspaceData> listWorkspaceData = null;
        String strSql = String.format("SELECT * FROM WorkspaceData WHERE IsAdded=%s",
                strIsLoadedGames);
//        String strSql = String.format("SELECT * FROM WorkspaceData WHERE IsAdded=%s ORDER BY AppOrder",
//                strIsLoadedGames);
        listWorkspaceData = new ArrayList<WorkspaceData>(getListWorkspaceData(strSql));
        return listWorkspaceData;
    }

    /**
     * 按包号删除一条数据
     * 
     * @param strPackageName
     *            包号
     * @return 是否删除成功
     */
    public boolean delDataByPackage(String strPackageName) {
        mSQLiteDB.beginTransaction();
        try {
            mSQLiteDB.delete("WorkspaceData", "PackageName=?", new String[] { strPackageName });
            mSQLiteDB.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            mSQLiteDB.endTransaction();
        }
    }

    /**
     * 按包号和类名删除一条数据
     * 
     * @param strPackageName
     *            包号
     * @return 是否删除成功
     */
    public boolean delDataByPackageAndClass(String strPackageName, String strClassName) {

        mSQLiteDB.beginTransaction();
        try {
            mSQLiteDB.delete("WorkspaceData", "PackageName=? AND ClassName=?", new String[] { strPackageName,
                    strClassName });
            mSQLiteDB.setTransactionSuccessful();
            return true;
        } catch (Exception e) {

            return false;
        } finally {

            mSQLiteDB.endTransaction();
        }
    }

    public boolean delListWorkspaceData(List<String> listPackageName) {

        mSQLiteDB.beginTransaction();
        try {
            for (String strPackageName : listPackageName) {
                mSQLiteDB.delete("WorkspaceData", "PackageName=?", new String[] { strPackageName });
            }
            mSQLiteDB.setTransactionSuccessful();
            return true;
        } catch (Exception e) {

            return false;
        } finally {

            mSQLiteDB.endTransaction();
        }
    }

    /**
     * 删除全部数据
     * 
     * @return 是否删除成功
     */
    public boolean delAllWorkspaceData() {

        mSQLiteDB.beginTransaction();
        try {
            mSQLiteDB.delete("WorkspaceData", null, null);
            mSQLiteDB.setTransactionSuccessful();
            return true;
        } catch (Exception e) {

            return false;
        } finally {
            mSQLiteDB.endTransaction();
        }
    }

    /**
     * 删除全部AddedApp的数据
     * 
     * @return
     */
    public boolean delAllAddedAppsWorkspaceData() {
        mSQLiteDB.beginTransaction();
        try {
            mSQLiteDB.delete("WorkspaceData", "IsAdded=?", new String[] { "1" });
            mSQLiteDB.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {

            mSQLiteDB.endTransaction();
        }
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        if (isOpen()) {
            mSQLiteDB.close();
        }
    }

    /**
     * 设置WorkspaceData的数据
     * 
     * @param workspaceData
     * @param cursor
     *            游标
     */
    private void setWorkspaceData(WorkspaceData workspaceData, Cursor cursor) {
        workspaceData.setAutoID(cursor.getString(cursor.getColumnIndex("AutoID")));
        workspaceData.setPackageName(cursor.getString(cursor.getColumnIndex("PackageName")));
        workspaceData.setClassName(cursor.getString(cursor.getColumnIndex("ClassName")));
        workspaceData.setIsAdded(cursor.getString(cursor.getColumnIndex("IsAdded")));
        workspaceData.setAppOrder(cursor.getString(cursor.getColumnIndex("AppOrder")));
        workspaceData.setRemark(cursor.getString(cursor.getColumnIndex("Remark")));
    }
}

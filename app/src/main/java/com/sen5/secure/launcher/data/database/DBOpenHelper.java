package com.sen5.secure.launcher.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DB_NAME = "Sen5Launcher.db";
    private static final String TABLE_WORSPACE_DATA = "WorkspaceData";

    /**
     * 自带的构造方法
     * 
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
        // 必须通过super调用父类当中的构造函数
        super(context, name, factory, version);
    }

    /**
     * 为了每次构造时不用传入dbName和版本号，新定义一个构造方法
     * 
     * @param context
     */
    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    /**
     * 版本变更时
     * 
     * @param context
     * @param version
     */
    public DBOpenHelper(Context context, int version) {
        this(context, DB_NAME, null, version);
    }

    /**
     * 当数据库创建的时候调用
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_WORSPACE_DATA + " (AutoID INTEGER PRIMARY KEY AUTOINCREMENT," + // 自动编号
                "PackageName nvarchar(50)," + // 包名
                "ClassName nvarchar(50)," + // Class名称
                "IsAdded bit," + // 是否已在OTT中加载
                "AppOrder smallint," + // app在OTT的排列位置
                "Remark nvarchar(50)) "); // 备注
    }

    /**
     * 版本更新时调用
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_WORSPACE_DATA);
        onCreate(db);
    }
}

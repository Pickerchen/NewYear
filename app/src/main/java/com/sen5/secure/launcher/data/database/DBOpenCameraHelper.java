package com.sen5.secure.launcher.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenCameraHelper extends SQLiteOpenHelper {
	
	private static final int VERSION = 2;
	private static final String DB_NAME = "Sen5LauncherCamera.db";
	/**
	 * 数据库 表名
	 */
	public static final String DB_TABLE_CAMERA_NAME = "cameraData";
	
	/**
	 * 数据库字段，表示camera的did
	 */
	public static final String DB_FIELD_DID = "DID";
	
	/**
	 * 数据库字段，表示camera的当前状态
	 */
	public static final String DB_FIELD_STATUS = "status";
	
	/**
	 * 数据库字段，表示camera 使用用户id（DeviceSDK.createDevice 返回的句柄）
	 */
	public static final String DB_FIELD_USERID = "userId";
	
	/**
	 * 数据库字段，表示该camera 使用用户id的作用， 0表示用于play；1表示用于record
	 */
	public static final String DB_FIELD_MODE = "mode";
	
	/**
	 * 自带的构造方法
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public DBOpenCameraHelper(Context context, String name, CursorFactory factory, int version) {  
		//必须通过super调用父类当中的构造函数  
		super(context, name, factory, version);  
	} 

	/**
	 * 为了每次构造时不用传入dbName和版本号，新定义一个构造方法 
	 * @param context
	 */
	public DBOpenCameraHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 版本变更时 
	 * @param context
	 * @param version
	 */
	public DBOpenCameraHelper(Context context, int version) {  
		this(context, DB_NAME, null, version);  
	} 

	/**
	 * 当数据库创建的时候调用
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE  "+ DB_TABLE_CAMERA_NAME +" (AutoID INTEGER PRIMARY KEY AUTOINCREMENT," +	// 自动编号
										DB_FIELD_DID + " nvarchar(50)," +							// 包名
										DB_FIELD_STATUS + " int," +								// Class名称
										DB_FIELD_MODE + " int," +								// Class名称
										DB_FIELD_USERID + " long," +										// 是否已在OTT中加载
										"test smallint," +									// app在OTT的排列位置
										"Remark nvarchar(50)) "); 								// 备注
	}
	

	
	/**
	 * 版本更新时调用
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		//db.execSQL("ALTER TABLE person ADD amount Integer(12) NULL");
		for (int i = oldVersion; i <= newVersion; i++) {
			switch (i) {
			case VERSION:
				upgradeToVersionAddMode(db);
				break;

			default:
				break;
			}
		}
	}
	private void upgradeToVersionAddMode(SQLiteDatabase db){

        // cameraData 表新增1个字段
        String sql1 = "ALTER TABLE "+ DB_TABLE_CAMERA_NAME +" ADD COLUMN mode INT";

        db.execSQL(sql1);

    }
}

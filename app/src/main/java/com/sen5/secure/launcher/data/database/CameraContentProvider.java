package com.sen5.secure.launcher.data.database;
 
import com.eric.xlee.lib.utils.LogUtil;

import android.content.ContentProvider;  
import android.content.ContentUris;  
import android.content.ContentValues;  
import android.content.UriMatcher;  
import android.database.Cursor;  
import android.database.sqlite.SQLiteDatabase;  
import android.net.Uri;

import nes.ltlib.utils.AppLog;

public class CameraContentProvider extends ContentProvider {  
  
    //这里的AUTHORITY就是我们在AndroidManifest.xml中配置的authorities，这里的authorities可以随便写
    private static final String AUTHORITY = "com.example.cameraProvider";
   //匹配成功后的匹配码
    private static final int MATCH_ALL_CODE = 100;
    private static final int MATCH_ONE_CODE = 101;
    private static UriMatcher uriMatcher;
    private SQLiteDatabase db;
    private DBOpenCameraHelper openHelper;
    private Cursor cursor = null;
    //数据改变后指定通知的Uri  
//    "content://com.example.cameraProvider/camera"
    private static final Uri NOTIFY_URI = Uri.parse("content://" + AUTHORITY + "/camera"); 
//    private static final String CAMERA_DATA = "cameraData";
     //在静态代码块中添加要匹配的 Uri
   static {
        //匹配不成功返回NO_MATCH(-1)
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        /** 
         * uriMatcher.addURI(authority, path, code); 其中 
         * authority：主机名(用于唯一标示一个ContentProvider,这个需要和清单文件中的authorities属性相同) 
         * path:路径路径(可以用来表示我们要操作的数据，路径的构建应根据业务而定) 
         * code:返回值(用于匹配uri的时候，作为匹配成功的返回值) 
         */
        uriMatcher.addURI(AUTHORITY, "camera", MATCH_ALL_CODE);// 匹配记录集合  
        uriMatcher.addURI(AUTHORITY, "camera_one", MATCH_ONE_CODE);// 匹配单条记录  
    }  
  
    @Override  
    public boolean onCreate() {
    	openHelper = new DBOpenCameraHelper(getContext());
        db = openHelper.getWritableDatabase();
        return false;  
    }  
  
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {  
    	int count = -1;
        switch (uriMatcher.match(uri)) {
        case MATCH_ALL_CODE:
        	count = db.delete(DBOpenCameraHelper.DB_TABLE_CAMERA_NAME, null, null);  
            if(count>0){  
                notifyDataChanged();
                return count;  
            }  
            break;  
            
        case MATCH_ONE_CODE:
            // 这里可以做删除单条数据的操作。  
        	 count = db.delete(DBOpenCameraHelper.DB_TABLE_CAMERA_NAME, selection, selectionArgs);
        	if(count > 0){
        		notifyDataChanged();
        	}
            break;  
        default:  
            throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());  
        }  
        return 0;  
    }  
  
    @Override
    public String getType(Uri uri) {  
        return null;  
    }  
 
    @Override  
    public Uri insert(Uri uri, ContentValues values) {  
  
        int match=uriMatcher.match(uri);  
        if(match != MATCH_ALL_CODE){
            throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());  
        }
          
        long rawId = db.insert(DBOpenCameraHelper.DB_TABLE_CAMERA_NAME, null, values);  
        Uri insertUri = ContentUris.withAppendedId(uri, rawId);  
        if(rawId>0){
            notifyDataChanged();  
            return insertUri;  
        }  
        return null;  
          
    }  
  
    @Override  
    public Cursor query(Uri uri, String[] projection, String selection,  
            String[] selectionArgs, String sortOrder) {  
        switch (uriMatcher.match(uri)) {  
        /** 
         * 如果匹配成功，就根据条件查询数据并将查询出的cursor返回 
         */  
        case MATCH_ALL_CODE:  
            cursor = db.query(DBOpenCameraHelper.DB_TABLE_CAMERA_NAME, projection, selection, selectionArgs, null, null, sortOrder);  
            break;
        case MATCH_ONE_CODE:  
        	cursor = db.query(DBOpenCameraHelper.DB_TABLE_CAMERA_NAME, projection, selection, selectionArgs, null, null, sortOrder);  
            // 根据条件查询一条数据。。。。  
            break;  
        default:  
            throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());  
        }  
  
        return cursor;  
    }  
  
    @Override  
    public int update(Uri uri, ContentValues values, String selection,  
            String[] selectionArgs) {  
  
    	AppLog.d("----------did matcher id = " + uriMatcher.match(uri));
    	String did = null;
    	int mode = -1;
    	int count = 0;
        switch (uriMatcher.match(uri)) {  
        case MATCH_ONE_CODE:
//            long age = ContentUris.parseId(uri);
        	did = (String)values.get("DID");
        	mode = (int)values.get("mode");
            AppLog.e("----------did  = " + did);
            selection = DBOpenCameraHelper.DB_FIELD_DID + " = ? and " + DBOpenCameraHelper.DB_FIELD_MODE + " = ?";
            selectionArgs = new String[] { String.valueOf(did), String.valueOf(mode)};  
            count = db.update(DBOpenCameraHelper.DB_TABLE_CAMERA_NAME, values, selection,selectionArgs);  
            if(count>0){
                notifyDataChanged();  
            }  
            break;  
        case MATCH_ALL_CODE:  
            // 如果有需求的话，可以对整个表进行操作  
        	did = (String)values.get("DID");
        	AppLog.d("----------did  = " + did);
            selection = DBOpenCameraHelper.DB_FIELD_DID + " = ?";
            selectionArgs = new String[] { String.valueOf(did)};  
            count = db.update(DBOpenCameraHelper.DB_TABLE_CAMERA_NAME, values, selection,selectionArgs);  
            if(count>0){
                notifyDataChanged();  
            }  
            break;  
        default:  
            throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());  
        }  
        return 0;  
    }
      
    //通知指定URI数据已改变    
    private void notifyDataChanged() {    
        getContext().getContentResolver().notifyChange(NOTIFY_URI, null);           
    }  
      
}  


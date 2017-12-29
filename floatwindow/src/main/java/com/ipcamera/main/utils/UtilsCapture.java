package com.ipcamera.main.utils;

import java.io.File;
import android.text.TextUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import com.ipcamera.main.smarthome.CameraControlNew;

/**
 * Created by jiangyicheng on 2016/11/2.
 */

public class UtilsCapture {
    public static Drawable getLoadingDefaultPic(Context context, String did){
        File file = new File(CameraControlNew.captureDefaultUIPath);
        Drawable drawable = null;
        if(file.exists()){
            File[] files = file.listFiles();
            if(files != null){
                int len = files.length;

                for (int i = 0; i < len; i++) {
                    String str = files[i].getAbsolutePath();
                    if(TextUtils.isEmpty(did)){
                    	break;
                    }
                    if(str.contains(did)){
                        Bitmap bitmap = ImageTools.getBitmapForPath(str,null);
                        if(bitmap != null){
                            drawable = ImageTools.bitmapToDrawable(context, bitmap);
                            return drawable;
                        }
                    }

                }
                for (int i = 0; i < len; i++) {

                    Bitmap bitmap = ImageTools.getBitmapForPath(files[i].getAbsolutePath(),null);
                    if(bitmap != null){
                        drawable = ImageTools.bitmapToDrawable(context, bitmap);
                        break;
                    }
                }
            }
        }
        return drawable;
    }
}

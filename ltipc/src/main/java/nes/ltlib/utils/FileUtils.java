package nes.ltlib.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ZHOUDAO on 2017/7/20.
 */

public class FileUtils {


    public static File createFile(Context context, String fileName) {
        String path = context.getExternalCacheDir().getAbsolutePath()+ File.separator + "xml";
//        String storageState = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(storageState)) {
//            path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "video";
//        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File File = new File(file, fileName);

        return File;
    }





    /**
     * 把文件从一个地方复制到另一个地方
     */
    public static boolean mCopyFile(File fromFile, File toFile) {
        try {
            FileInputStream fosfrom = new FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024 * 1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return true;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Log.i("复制文件异常", e.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.i("复制文件异常", e.toString());
        } finally {
            return false;
        }
    }

}

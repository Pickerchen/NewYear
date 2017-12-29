package com.sen5.secure.launcher.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import hsl.p2pipcam.nativecaller.NativeCaller;
import nes.ltlib.utils.AppLog;

/**
 * Created by ZHOUDAO on 2017/9/5.
 */

public class CutPicUtils {

    private Bitmap bitmap;
    private String path;
    private String did;

    private long userId;

    private static CutPicUtils cutPicUtils;

    public static CutPicUtils getInstace() {
        cutPicUtils = new CutPicUtils();
        return cutPicUtils;
    }


    public CutPicUtils setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        return this;
    }

    public CutPicUtils setSavePath(String path) {
        this.path = path;
        return this;
    }

    public CutPicUtils setUserId(long userID) {
        this.userId = userID;
        return this;
    }

    public CutPicUtils setDid(String did) {
        this.did = did;
        return this;
    }

    public int cut() {
        int status = 0;
        createCommonFolder(commonPath + did);
        File file = createSaveFolder(path);
        FileOutputStream fos = null;
        if (bitmap != null) {

            try {
//                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file.getPath()));
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int option = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, option, baos);
                while (baos.toByteArray().length / 1024 > 100 && option > 6) {
                    baos.reset();
                    option -= 6;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, option, baos);
                }

                fos = new FileOutputStream(file);
                fos.write(baos.toByteArray());

                baos.flush();
                baos.close();

                status = 1;
            } catch (IOException e) {
                e.printStackTrace();
                status = 0;
            } finally {
                if (bitmap != null)
                    bitmap.recycle();
                bitmap = null;
                if (fos != null)
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        } else {
            status = NativeCaller.CapturePicture(userId, file.getPath());

        }


        return status;
    }


    public boolean createCommonFolder(String path) {
        boolean isOk = true;
        File folder = new File(path);
        if (!folder.exists()) {
            isOk = folder.mkdirs();
        }
        AppLog.e("createCommonFolder: path is " + path + " isok is" + isOk);
        return isOk;
    }


    public File createSaveFolder(String path) {
        boolean isOk = true;
        File folder = new File(commonPath + did, path);
        if (folder.exists()) {
            folder.delete();
            folder = new File(commonPath + did, path);
        }

        AppLog.e("createCommonFolder: path is " + path + " isok is" + isOk);
        return folder;
    }

    public String commonPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "IPCamera" + File.separator + "lastFrame" + File.separator;
}

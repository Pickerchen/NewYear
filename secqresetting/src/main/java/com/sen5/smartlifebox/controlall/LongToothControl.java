package com.sen5.smartlifebox.controlall;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by jiangyicheng on 2017/3/22.
 */

public class LongToothControl {
    public static final String LONG_TOOTH_CONFIG = "/data/smarthome/lt_id.cfg";
    public static String getLongToothServerId(){
        String longToothS = "";
        File mFile = new File(LONG_TOOTH_CONFIG);
        if(mFile.exists() && mFile.isFile()){

            try {
                FileReader fr = new FileReader(mFile);
                BufferedReader br = new BufferedReader(fr);
                String str = br.readLine();
                if(!TextUtils.isEmpty(str)){
                    String[] line = str.split(":");
                    if(line.length > 1){
                        longToothS = line[1];
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return longToothS;
    }

    public static boolean hasLongToothServer(){
        boolean hasLT = false;
        File mFile = new File(LONG_TOOTH_CONFIG);
        if(mFile.exists() && mFile.isFile()){
            hasLT = true;
        }
        return hasLT;
    }




}

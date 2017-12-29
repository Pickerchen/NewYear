package com.sen5.smartlifebox.data;

import com.sen5.smartlifebox.common.Constant;
import nes.ltlib.utils.AppLog;
import com.sen5.smartlifebox.data.entity.ContactEntity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglin on 2017/2/8.
 */
public class IniFileOperate {

    /**
     * 修改contact.ini文件
     * @param contacts
     */
    public static void alterContactIni(List<ContactEntity> contacts){
        File file = new File(Constant.CONTACT_INI);
        BufferedWriter bw = null;
        StringBuffer sb = new StringBuffer();

        try {
            bw = new BufferedWriter(new FileWriter(file, false));//true表示在原来内容后面追加,false：覆盖原来内容
            for (ContactEntity contact : contacts) {
                sb.append("##");
                sb.append(contact.getEmergencyFlag());
                sb.append("#");
                sb.append(contact.getNumber());
                sb.append("#");
                sb.append(contact.getName());
                sb.append("##");
                sb.append("\n");
            }
            bw.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读contact.ini文件
     */
    public static List<ContactEntity> readContactIni(){
        List<ContactEntity> contacts = new ArrayList<>();

        File file = new File(Constant.CONTACT_INI);
        if (!file.exists()) {
            AppLog.e("contact.ini 文件不存在");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!file.canWrite()) {
            AppLog.e("contact.ini文件");
        }

        if (file.exists()) {
            //读取文件行数，一行代表一个contact，每行格式为：##0#电话号码#联系人名字##
            FileReader fr = null;
            BufferedReader br = null;
            try {
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                String line;
                while ((line = br.readLine()) != null) {
                    String content = line.substring(2, line.length() - 2);
                    String[] strs = content.split("#");

                    ContactEntity contact = new ContactEntity();
                    contacts.add(contact);
                    contact.setEmergencyFlag(Integer.valueOf(strs[0]));
                    contact.setNumber(strs[1]);
                    contact.setName(strs[2]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {
                AppLog.e("Camera.ini文件格式异常");
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                    if (fr != null) {
                        fr.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return contacts;
    }
}

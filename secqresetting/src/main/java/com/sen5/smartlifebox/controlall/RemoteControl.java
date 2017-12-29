package com.sen5.smartlifebox.controlall;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import nes.ltlib.utils.AppLog;
import com.sen5.smartlifebox.data.JsonCreator;
import com.sen5.smartlifebox.data.entity.ModeData;
import com.sen5.smartlifebox.data.entity.SceneData;
import com.sen5.smartlifebox.data.p2p.P2PModel;

import java.util.List;

/**
 * Created by jiangyicheng on 2017/3/23.
 */

public class RemoteControl {
//    public static final int KEYCODE_SEN5_SMARTHOME_ARM = 5087;
//    public static final int KEYCODE_SEN5_SMARTHOME_DISARM = 5088;
//    public static final int KEYCODE_SEN5_SMARTHOME_ARM_STAY = 5089;
//    public static final int KEYCODE_SEN5_SMARTHOME_SCENE_F1 = 5090;
//    public static final int KEYCODE_SEN5_SMARTHOME_SCENE_F2 = 5091;
//    public static final int KEYCODE_SEN5_SMARTHOME_SCENE_F3 = 5092;
//    public static final int KEYCODE_SEN5_SMARTHOME_SCENE_F4 = 5093;
//    public static final int KEYCODE_SEN5_SMARTHOME_SCENE_F5 = 5094;
//    public static final int KEYCODE_SEN5_SMARTHOME_SCENE_F6 = 5095;
//    private static final int KEYCODE_SMARTHOME_RED = KeyEvent.KEYCODE_SEN5_SMARTHOME_SCENE_F1;
//    private static final int KEYCODE_SMARTHOME_GREEN = KeyEvent.KEYCODE_SEN5_SMARTHOME_SCENE_F2;
//    private static final int KEYCODE_SMARTHOME_YEELOW = KeyEvent.KEYCODE_SEN5_SMARTHOME_SCENE_F3;
//    private static final int KEYCODE_SMARTHOME_BLUE = KeyEvent.KEYCODE_SEN5_SMARTHOME_SCENE_F4;
//
//    private static final int KEYCODE_SMARTHOME_ARM_STAY =  KeyEvent.KEYCODE_SEN5_SMARTHOME_ARM_STAY;
//    private static final int KEYCODE_SMARTHOME_ARM =  KeyEvent.KEYCODE_SEN5_SMARTHOME_ARM;
//    private static final int KEYCODE_SMARTHOME_DISARM =  KeyEvent.KEYCODE_SEN5_SMARTHOME_DISARM;

    public static final String KEYCODE_SEN5_SMARTHOME_ARM = "KEYCODE_SEN5_SMARTHOME_ARM";
    public static final String KEYCODE_SEN5_SMARTHOME_DISARM = "KEYCODE_SEN5_SMARTHOME_DISARM";
    public static final String KEYCODE_SEN5_SMARTHOME_ARM_STAY = "KEYCODE_SEN5_SMARTHOME_ARM_STAY";
    public static final String KEYCODE_SEN5_SMARTHOME_SCENE_F1 = "KEYCODE_SEN5_SMARTHOME_SCENE_F1";
    public static final String KEYCODE_SEN5_SMARTHOME_SCENE_F2 = "KEYCODE_SEN5_SMARTHOME_SCENE_F2";
    public static final String KEYCODE_SEN5_SMARTHOME_SCENE_F3 = "KEYCODE_SEN5_SMARTHOME_SCENE_F3";
    public static final String KEYCODE_SEN5_SMARTHOME_SCENE_F4 = "KEYCODE_SEN5_SMARTHOME_SCENE_F4";


    private static final int KEYCODE_SMARTHOME_RED = 0;
    private static final int KEYCODE_SMARTHOME_GREEN = 1;
    private static final int KEYCODE_SMARTHOME_YEELOW = 2;
    private static final int KEYCODE_SMARTHOME_BLUE = 3;

    private static final int KEYCODE_SMARTHOME_ARM_STAY =  4;
    private static final int KEYCODE_SMARTHOME_ARM =  5;
    private static final int KEYCODE_SMARTHOME_DISARM =  6;

    public static void smarthomeKey(Context context, int keyCode){

        String keyCodeName = KeyEvent.keyCodeToString(keyCode);

        if(!TextUtils.isEmpty(keyCodeName)){
            if(KEYCODE_SEN5_SMARTHOME_ARM.equals(keyCodeName)){
                keyCode = KEYCODE_SMARTHOME_ARM;
            }
            else if(KEYCODE_SEN5_SMARTHOME_DISARM.equals(keyCodeName)){
                keyCode = KEYCODE_SMARTHOME_DISARM;
            }
            else if(KEYCODE_SEN5_SMARTHOME_ARM_STAY.equals(keyCodeName)){
                keyCode = KEYCODE_SMARTHOME_ARM_STAY;
            }
            else if(KEYCODE_SEN5_SMARTHOME_SCENE_F1.equals(keyCodeName)){
                keyCode = KEYCODE_SMARTHOME_RED;
            }
            else if(KEYCODE_SEN5_SMARTHOME_SCENE_F2.equals(keyCodeName)){
                keyCode = KEYCODE_SMARTHOME_GREEN;
            }
            else if(KEYCODE_SEN5_SMARTHOME_SCENE_F3.equals(keyCodeName)){
                keyCode = KEYCODE_SMARTHOME_YEELOW;
            }
            else if(KEYCODE_SEN5_SMARTHOME_SCENE_F4.equals(keyCodeName)){
                keyCode = KEYCODE_SMARTHOME_BLUE;
            }
        }


        triggerScences(context, keyCode);
        triggerSecurityMode(context, keyCode);
    }

    public static void triggerScences(Context context, int keyCode){
        P2PModel mP2PModel = P2PModel.getInstance(context);

        switch(keyCode){
            case KEYCODE_SMARTHOME_RED:
                sendScenesData(mP2PModel, 0);
            break;
            case KEYCODE_SMARTHOME_GREEN:
                sendScenesData(mP2PModel, 1);
            break;
            case KEYCODE_SMARTHOME_YEELOW:
                sendScenesData(mP2PModel, 2);
            break;
            case KEYCODE_SMARTHOME_BLUE:
                sendScenesData(mP2PModel, 3);
            break;
            default:
            break;
        }

    }

    public static void triggerSecurityMode(Context context, int keyCode){
        P2PModel mP2PModel = P2PModel.getInstance(context);
        Log.d("TAG", "--------------smarthome triggerSecurityMode keyCode= " + keyCode);
        switch(keyCode){
            case KEYCODE_SMARTHOME_ARM:
                applyMode(mP2PModel, mP2PModel.getCurMode(), ModeData.MODE_AWAY);
            break;
            case KEYCODE_SMARTHOME_ARM_STAY:
                applyMode(mP2PModel, mP2PModel.getCurMode(), ModeData.MODE_STAY);
            break;
            case KEYCODE_SMARTHOME_DISARM:
                applyMode(mP2PModel, mP2PModel.getCurMode(), ModeData.MODE_DISARM);
            break;
            default:
            break;
        }
    }

    private static void sendScenesData(P2PModel mP2PModel, int position){
        List<SceneData> scenes = mP2PModel.getScenes();
        int size = scenes.size();
        Log.e("TAG", "--------------smarthome sendScenesData = " + size + "  position = " + position);
        if(size > position){
            int id = scenes.get(position).getScene_id();
            String str = JsonCreator.createApplySceneJson(id);
            mP2PModel.sendData(str);
        }
    }

    /**
     * 应用指定模式
     *
     * @param mode
     */
    private static void applyMode(P2PModel mP2PModel, ModeData mCurMode, int mode) {

        if (mCurMode != null && mCurMode.getSec_mode() == mode) {
            AppLog.i("已处于该模式");
            return;
        }

        String data = JsonCreator.createApplyModeJson(mode);
        mP2PModel.sendData(data);
    }
}

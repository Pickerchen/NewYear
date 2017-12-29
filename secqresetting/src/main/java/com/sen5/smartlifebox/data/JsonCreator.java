package com.sen5.smartlifebox.data;


import com.alibaba.fastjson.JSON;
import com.sen5.smartlifebox.data.entity.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务器请求json生成类
 */
public class JsonCreator {

    /**
     * 发送身份验证
     * @param id
     * @return
     */
    public static String createIdentityJson(String id) {
        Map map = new HashMap();
        map.put(Constant.MSG_TYPE, Constant.MSG_IDENTITY);
        map.put("identity_id", id);

        return JSON.toJSONString(map);
    }

    /**
     * 获取用户列表
     * @return
     */
    public static String createListUserJson() {
        Map map = new HashMap();
        map.put(Constant.MSG_TYPE, Constant.MSG_LIST_USER);

        return JSON.toJSONString(map);
    }

    /**
     * 添加用户
     * @param id
     * @param name
     * @return
     */
    public static String createAddUserJson(String id, String name) {
        Map map = new HashMap();
        map.put(Constant.MSG_TYPE, Constant.MSG_ADD_USER);
        map.put("identity_id", id);
        map.put("identity_name", name);

        return JSON.toJSONString(map);
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    public static String createDeleteUserJson(String id) {
        Map map = new HashMap();
        map.put(Constant.MSG_TYPE, Constant.MSG_DELETE_USER);
        map.put("identity_id", id);

        return JSON.toJSONString(map);
    }

    /**
     * 重命名用户
     * @param id
     * @param name
     * @return
     */
    public static String createRenameUserJson(String id, String name) {
        Map map = new HashMap();
        map.put(Constant.MSG_TYPE, Constant.MSG_RENAME_USER);
        map.put("identity_id", id);
        map.put("identity_name", name);

        return JSON.toJSONString(map);
    }

    /**
     * 用于重命名摄像头
     * @param name
     * @return
     */
    public static String createRenameCameraJson(String name){
        Map map = new HashMap();
        map.put("alias", name);

        return JSON.toJSONString(map);
    }

    /**********************************
     * 设防模式操作
     *************************/
    public static String createListModeJson() {
        Map map = new HashMap();
        map.put(Constant.MSG_TYPE, Constant.MSG_LIST_MODE);

        return JSON.toJSONString(map);
    }

    /**
     * 应用场景
     *
     * @param cur_sec_mode 要应用的设防模式id
     * @return
     */
    public static String createApplyModeJson(int cur_sec_mode) {
        Map map = new HashMap();
        map.put(Constant.MSG_TYPE, Constant.MSG_APPLY_MODE);
        map.put("cur_sec_mode", cur_sec_mode);

        return JSON.toJSONString(map);
    }



    /************************设备操作*******************************/

    /**
     * 请求所有设备列表
     *
     * @return
     */
    public static String createListDeviceJson() {
        Map map = new HashMap();
        map.put(Constant.MSG_TYPE, Constant.MSG_LIST_DEVICE);

        return JSON.toJSONString(map);
    }

    /**
     * 请求所有设备状态
     *
     * @return
     */
    public static String createRequestAllDeviceStatusJson() {
        Map map = new HashMap();
        map.put(Constant.MSG_TYPE, Constant.MSG_REQUEST_ALL_DEVICE_STATUS);
        return JSON.toJSONString(map);
    }

    /*************************Scenes****************************/
    /**
     * 获取场景列表
     * @return
     */
    public static String createListSceneJson() {
        Map map = new HashMap();
        map.put(Constant.MSG_TYPE, Constant.MSG_LIST_SCENE);

        return JSON.toJSONString(map);
    }

    /**
     * 应用场景
     *
     * @param sceneId
     * @return
     */
    public static String createApplySceneJson(int sceneId) {
        Map map = new HashMap();
        map.put(Constant.MSG_TYPE, Constant.MSG_APPLY_SCENE);
        map.put("scene_id", sceneId);

        return JSON.toJSONString(map);
    }

    public static String createRoomsJson() {
        Map map = new HashMap();
        map.put(Constant.MSG_TYPE, Constant.MSG_LIST_ROOM_RESPOND);
        return JSON.toJSONString(map);
    }

    public static String updateDeviceStatus(int devId, int actionId, byte[] params) {
        try {
            Map map = new HashMap();
            map.put(Constant.MSG_TYPE, Constant.MSG_CONTROL_DEVICE_RESPOND);
            map.put("dev_id", devId);
            map.put("action_id", actionId);
            map.put("action_params", params);

            return JSON.toJSONString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String sendVideoPath(int msg_type,String video_path){
        try {
            Map map = new HashMap();
            map.put(Constant.MSG_TYPE, msg_type);
            map.put("video_path", video_path);
            return JSON.toJSONString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String createAddDevice(){
        Map map = new HashMap();
        map.put(Constant.MSG_TYPE, Constant.MSG_ADD_DEVICE);
        return JSON.toJSONString(map);
    }

}

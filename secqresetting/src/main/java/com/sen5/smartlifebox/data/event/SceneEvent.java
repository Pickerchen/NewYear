package com.sen5.smartlifebox.data.event;

/**
 * Created by wanglin on 2016/6/15.
 */

public class SceneEvent {

    int flag;

    public int getSceneId() {
        return SceneId;
    }

    public void setSceneId(int sceneId) {
        SceneId = sceneId;
    }

    private int SceneId;
    public static final int LIST_SCENE = 0;
    public static final int NEW_SCENE = 1;
    public static final int DELETE_SCENE = 2;
    public static final int EDIT_SCENE = 3;
    public static final int APPLY_SCENE = 4;

    public SceneEvent(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }
}

package nes.ltlib.event;

/**
 * Created by ZHOUDAO on 2017/9/8.
 */

public class LTCameraEvent {

    public static final int connected = 100;
    public static final int connecting = 0;
    public static final int disconnect = 11;
    public static final int reconnecting = 1314;


    private int status;

    public LTCameraEvent(int status, String gid) {
        this.status = status;
        this.gid = gid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    private String gid;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    private int error;
}

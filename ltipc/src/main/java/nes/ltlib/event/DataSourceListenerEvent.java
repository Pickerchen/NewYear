package nes.ltlib.event;

/**
 * Created by chenqianghua on 2017/9/8.
 * 监听单个设备状态的event事件
 */

public class DataSourceListenerEvent {
    private String did;
    private int status;//0:connected 1:disconnected


    public DataSourceListenerEvent(String did, int status) {
        this.did = did;
        this.status = status;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

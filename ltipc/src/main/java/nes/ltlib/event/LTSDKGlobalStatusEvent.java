package nes.ltlib.event;

/**
 * Created by chenqianghua on 2017/10/12.
 * 由launcher注册LTSDK全局监听并分发，RecordService接收
 */

public class LTSDKGlobalStatusEvent {
    private String gid;
    private int status;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LTSDKGlobalStatusEvent(String gid, int status) {
        this.gid = gid;
        this.status = status;
    }
}

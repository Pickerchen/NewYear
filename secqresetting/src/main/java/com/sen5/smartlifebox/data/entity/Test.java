package com.sen5.smartlifebox.data.entity;

import java.util.List;

/**
 * Created by jiangyicheng on 2017/3/17.
 */

public class Test {

    /**
     * msg_type : 106
     * dev_id : 4
     * ipc_did : 5
     * status : [{"id":2,"params":"AQAAAA=="}]
     */

    private int msg_type;
    private int dev_id;
    private int ipc_did;
    private List<StatusBean> status;

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public int getDev_id() {
        return dev_id;
    }

    public void setDev_id(int dev_id) {
        this.dev_id = dev_id;
    }

    public int getIpc_did() {
        return ipc_did;
    }

    public void setIpc_did(int ipc_did) {
        this.ipc_did = ipc_did;
    }

    public List<StatusBean> getStatus() {
        return status;
    }

    public void setStatus(List<StatusBean> status) {
        this.status = status;
    }

    public static class StatusBean {
        /**
         * id : 2
         * params : AQAAAA==
         */

        private int id;
        private String params;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getParams() {
            return params;
        }

        public void setParams(String params) {
            this.params = params;
        }
    }
}

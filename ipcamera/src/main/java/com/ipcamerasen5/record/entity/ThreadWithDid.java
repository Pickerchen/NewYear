package com.ipcamerasen5.record.entity;

/**
 * Created by chenqianghua on 2017/6/20.
 */

public class ThreadWithDid {
    private String did;
    private Thread mThread;
    public Runnable mRunnable;

    public ThreadWithDid(String did, Thread thread,Runnable runnable) {
        this.did = did;
        this.mThread = thread;
        this.mRunnable = runnable;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public Thread getThread() {
        return mThread;
    }

    public void setThread(Thread thread) {
        mThread = thread;
    }
    public void stopThread(){
        if (null != mThread){
            mThread.interrupt();
            mThread = null;
        }
    }
}

package com.sen5.smartlifebox.common.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import nes.ltlib.utils.AppLog;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WifiSearcher {

    private static final int WIFI_SEARCH_TIMEOUT = 15; //扫描WIFI的超时时间

    private Context mContext;
    private WifiManager mWifiManager;
    private WiFiScanReceiver mWifiReceiver;
    private Lock mLock;
    private Condition mCondition;
    private SearchWifiListener mSearchWifiListener;
    private boolean mIsWifiScanCompleted = false;

    private Handler mHandler;

    public static enum ErrorType {
        SEARCH_WIFI_TIMEOUT, //扫描WIFI超时（一直搜不到结果）
        NO_WIFI_FOUND,       //扫描WIFI结束，没有找到任何WIFI信号
    }

    //扫描结果通过该接口返回给Caller
    public interface SearchWifiListener {
        public void onSearchWifiFailed(ErrorType errorType);

        public void onSearchWifiSuccess(List<ScanResult> scanResults);
    }

    public WifiSearcher(Context context, SearchWifiListener listener) {

        mContext = context;
        mSearchWifiListener = listener;

        mLock = new ReentrantLock();
        mCondition = mLock.newCondition();
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

        mWifiReceiver = new WiFiScanReceiver();
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        mSearchWifiListener.onSearchWifiFailed(ErrorType.SEARCH_WIFI_TIMEOUT);
                        break;

                    case 1:
                        mSearchWifiListener.onSearchWifiSuccess(mWifiManager.getScanResults());
                        break;
                }
            }
        };
    }

    public void startSearch() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                //注册接收WIFI扫描结果的监听类对象
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//                intentFilter.setPriority(2147483647);
                mContext.registerReceiver(mWifiReceiver, intentFilter);

                //开启wifi之前，需要先关闭WIFI热点
                if (WifiApAdmin.isWifiApEnabled(mWifiManager)) {
                    WifiApAdmin.closeWifiAp(mContext);
                }

                //如果WIFI没有打开，则打开WIFI
                if (!mWifiManager.isWifiEnabled()) {
                    mWifiManager.setWifiEnabled(true);
                    AppLog.i("Thread WifiSearcher ：没有打开wifi，而打开wifi");
                }

                mLock.lock(); //得到锁
                try {
                    mIsWifiScanCompleted = false;
                    mCondition.await(WIFI_SEARCH_TIMEOUT, TimeUnit.SECONDS);   //阻塞线程
                    if (!mIsWifiScanCompleted) {
                        //扫描超时了，检测一下是否有扫描结果，如果有则认为扫描成功，否则认为扫描超时
                        //频繁开关并扫描wifi，后续会接收不到SCAN_RESULTS_AVAILABLE_ACTION广播？
                        List<ScanResult> scanResults = mWifiManager.getScanResults();
                        if(scanResults.isEmpty()){
                            mHandler.sendEmptyMessage(0);
                            AppLog.i("wifi扫描超时");
                        }else {
                            mHandler.sendEmptyMessage(1);
                            AppLog.i("wifi扫描超时，但有扫描结果");
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    mLock.unlock();//释放锁
                }

                //删除注册的监听类对象
                mContext.unregisterReceiver(mWifiReceiver);
                AppLog.i("Thread WifiSearcher ：广播被注销");
            }
        }).start();
    }

    /**
     * 停止搜索
     */
    public void stopSearch(){
        mLock.lock();
        mIsWifiScanCompleted = true;
        mCondition.signalAll();
        mLock.unlock();
    }

    //系统WIFI扫描结果消息的接收者
    protected class WiFiScanReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            AppLog.e("Wifi Aciton = " + intent.getAction());
            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        //当注册WifiManager.WIFI_STATE_CHANGED_ACTION广播时，会立马接收到一个带有当前wifi状态的
                        //WifiManager.WIFI_STATE_CHANGED_ACTION广播，所以当打开wifi，然后立马注册WifiManager.WIFI_STATE_CHANGED_ACTION广播
                        //时，会接先后收到WIFI_STATE_DISABLED、WIFI_STATE_ENABLED两种状态的WifiManager.WIFI_STATE_CHANGED_ACTION广播
                        //因为wifi打开始需要时间的

                        AppLog.i("WifiSearcher：wifi被打开");
                        //开始扫描
                        mWifiManager.startScan();
                        AppLog.i("WifiSearcher：扫描wifi");
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        AppLog.i("WifiSearcher：wifi被关闭");
                        break;
                }
            } else if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                AppLog.i("WifiSearcher：扫描完成");
                //提取扫描结果
                List<ScanResult> scanResults = mWifiManager.getScanResults();

                //检测扫描结果
                if (scanResults.isEmpty()) {
                    mSearchWifiListener.onSearchWifiFailed(ErrorType.NO_WIFI_FOUND);
                } else {
                    mSearchWifiListener.onSearchWifiSuccess(scanResults);
                }

                mLock.lock();
                mIsWifiScanCompleted = true;
                mCondition.signalAll(); //唤醒等待该锁的所有线程
                mLock.unlock();
            }
        }
    }
}
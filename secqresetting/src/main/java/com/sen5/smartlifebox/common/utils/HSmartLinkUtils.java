package com.sen5.smartlifebox.common.utils;

import android.text.TextUtils;

import nes.ltlib.utils.AppLog;
import com.sen5.smartlifebox.common.wrapper.FastJsonTools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import hsl.p2pipcam.nativecaller.DeviceSDK;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import nes.ltlib.data.CameraEntity;
import nes.ltlib.interf.CameraWifiListener;
import nes.ltlib.utils.HttpUtils;

/**
 * Created by ZHOUDAO on 2017/9/18.
 */

public class HSmartLinkUtils {

    private final String TAG = HSmartLinkUtils.class.getSimpleName();

    private static HSmartLinkUtils hSmartLinkUtils;

    public static HSmartLinkUtils getInstance() {
        if (hSmartLinkUtils == null) {
            hSmartLinkUtils = new HSmartLinkUtils();

        }
        return hSmartLinkUtils;
    }

    private List<CameraEntity> mCameras = new ArrayList<>();

    private String did;
    private String ssid;
    private String pwd;


    private Observable setWifiCGI() {


        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {

                CameraEntity entity = getEntityById(did);
                if (entity == null) {
                    e.onError(new NullPointerException());
                    return;
                }

                String url = "http://" + entity.getIP() + ":" + entity.getPort() + "/set_wifi.cgi?enable=1&ssid=" + ssid + "&encrypt=1&authtype=4&keyformat=1&channel=1&mode=0&wpa_psk=" + pwd + "&loginuse=admin&loginpas=";

                AppLog.e("url:" + url);


//                Map<String, String> head = new HashMap<String, String>();
//                head.put("Authorization", "Basic YWRtaW46");

                String result = HttpUtils.get(url, null);

                e.onNext(result);
                e.onComplete();

            }
        });
    }

    public void setCameraWifi(String did, String ssid, String pwd, final CameraWifiListener listener) {
        this.did = did;
        this.ssid = ssid;
        this.pwd = pwd;

        DeviceSDK.initSearchDevice();//初始化搜索sdk
        DeviceSDK.setSearchCallback(HSmartLinkUtils.this);

        searchDevice(0);

        searchDevice(2);

        setWifiCGI().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).delaySubscription(7, TimeUnit.SECONDS).subscribe(new DisposableObserver<String>() {
            @Override
            public void onNext(@NonNull String o) {

                AppLog.i("result:" + o);

                if (!TextUtils.isEmpty(o) && o.contains("ok") && listener != null) {
                    AppLog.i("设置老摄像头Wifi成功");
                    listener.setWifiSuccess();
                } else {
                    AppLog.i("设置老摄像头Wifi失败");
                    listener.setWifiFailure();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                if (listener != null) {
                    AppLog.i("设置老摄像头Wifi失败");
                    listener.setWifiFailure();
                }
            }

            @Override
            public void onComplete() {

            }
        });


    }

    private void searchDevice(int delay) {
        Observable.create(new ObservableOnSubscribe<CameraEntity>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<CameraEntity> e) throws Exception {

                DeviceSDK.searchDevice();

                e.onComplete();

            }
        }).delaySubscription(delay, TimeUnit.SECONDS).subscribe(new DisposableObserver<CameraEntity>() {
            @Override
            public void onNext(@NonNull CameraEntity cameraEntity) {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }


    //搜索到设备后jni的回调。
    public void CallBack_SearchDevice(String DeviceInfo) {

//        AppLog.e(TAG + DeviceInfo);
        CameraEntity camera = FastJsonTools.getItem(DeviceInfo, CameraEntity.class);
        //去掉did中间的“-”
        camera.setDeviceID(camera.getDeviceID().replaceAll("-", ""));
        if (!mCameras.contains(camera)) {
            mCameras.add(camera);
        }
    }


    private CameraEntity getEntityById(String did) {

        CameraEntity entity = null;

        for (CameraEntity cameraEntity : mCameras) {

            if (cameraEntity.getDeviceID().equals(did)) {
                entity = cameraEntity;
            }

        }

        return entity;
    }

    public static void unInitSearch() {
        if (hSmartLinkUtils != null)
            DeviceSDK.unInitSearchDevice();

    }

}

package com.sen5.secure.launcher.utils;


import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.VideoView;

import com.eric.xlee.lib.utils.LogUtil;

import nes.ltlib.utils.AppLog;


public class HolderDTV {
    //	public static final boolean IS_SHOW_NEED = SystemProperties.getBoolean("ro.sen5.startshowtime", true);
    public static final boolean IS_SHOW_NEED = SystemProperties.getBoolean("persist.sys.sen5.startshowtime", true);

    public static void initPlayShowVideo(final Handler mHandler, final Context context, final VideoView mVideoview_main) {
        AppLog.e("DTV:------HolderDTV.IS_SHOW_NEED:" + HolderDTV.IS_SHOW_NEED);
        if (HolderDTV.IS_SHOW_NEED) {
            Timer mTimer = new Timer();
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            HolderDTV.startPlayVideo(mVideoview_main,
                                    context);
                        }
                    });
                }
            }, 5000, 5000);
        }
    }


    private static void startPlayVideo(final VideoView videoView, Context context) {
        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        String usbStatus = StorageUtilsNew.getUSBStatus(mStorageManager, 0, "showtime/showtime_video.mp4");
//		AppLog.i("--DTV:---------------start showtime video = " + usbStatus + "  videoView.isPlaying() = " + videoView.isPlaying());
        if (TextUtils.isEmpty(usbStatus)) {
//            AppLog.i("--DTV:-------Empty--------");
            return;
        }
        if (videoView.isPlaying()) {
//			videoView.stopPlayback();
            return;
        }

        videoView.setVisibility(View.VISIBLE);
        Uri uri = Uri.parse(usbStatus);
        videoView.setVideoURI(uri);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.start();
                mp.setLooping(true);

            }
        });

        videoView.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                // TODO Auto-generated method stub
                AppLog.e("-----------------start setOnErrorListener = ");
                videoView.setVisibility(View.GONE);
                return false;
            }
        });

        videoView.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer arg0) {
                // TODO Auto-generated method stub
//				DLog.e("-----------------start setOnCompletionListener = ");
            }
        });
    }
}

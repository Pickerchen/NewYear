package com.ipcamerasen5.record;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.widget.ImageView;

import com.ipcamerasen5.main1.R;

/**
 * Created by ZHOUDAO on 2017/12/4.
 */

public class TestView extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setTransitionName("ltCamera");
        }


        imageView.setImageResource(R.drawable.no_camera_ui_bg);
        ViewCompat.setTransitionName(imageView,"ltCamera");

        setContentView(imageView);


    }
}

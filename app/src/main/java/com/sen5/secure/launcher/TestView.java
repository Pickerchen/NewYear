package com.sen5.secure.launcher;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


/**
 * Created by ZHOUDAO on 2017/12/4.
 */

public class TestView extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ImageView imageView = new ImageView(this);
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            imageView.setTransitionName("ltCamera");
//        }


//        imageView.setImageResource(R.drawable.no_camera_ui_bg);
//        ViewCompat.setTransitionName(imageView,"ltCamera");

        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setBackgroundColor(Color.BLUE);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            relativeLayout.setTransitionName("ltCamera");
        }
        setContentView(relativeLayout);


    }
}

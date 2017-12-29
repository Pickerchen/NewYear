package com.sen5.secure.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;


/**
 * Created by ZHOUDAO on 2017/12/4.
 */

public class TestView1 extends AppCompatActivity {


    private ImageView mIvTest;
    private RelativeLayout mRlayoutContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mIvTest = (ImageView) findViewById(R.id.iv_test);

        mRlayoutContainer = (RelativeLayout) findViewById(R.id.rlayout_container);
        mIvTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestView1.this, TestView.class);

                TestView1.this.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(TestView1.this, mRlayoutContainer, "ltCamera").toBundle());
            }
        });

    }
}

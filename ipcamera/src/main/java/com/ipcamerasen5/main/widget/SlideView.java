package com.ipcamerasen5.main.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;

import com.ipcamerasen5.main1.R;
import com.ipcamerasen5.record.event.MainRecyclerViewShow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import nes.ltlib.utils.AppLog;

/**
 * Created by chenqianghua on 2017/8/16.
 */

public class SlideView extends View {

    private int num;
    public int position;//当前位置
    private int slide_leght = 120;

    public SlideView(Context context) {
        super(context);
    }

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置上下滚动轨道数目
     */
    @Subscribe
    public void setSlideNum(int sum){
            num = sum;
        EventBus.getDefault().register(this);
//       slide_leght = (int) (getResources().getDisplayMetrics().density * 80);
        slide_leght = (int) getResources().getDimension(R.dimen.eighty);
        AppLog.e("slide_leght is " + slide_leght);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_UP:
                if (position == 0){
                    break;
                }
                float startF =  (float)(position)*slide_leght;
                float endF = (float) ((position-1)*slide_leght);
                ObjectAnimator anim = ObjectAnimator.ofFloat(this, "translationY", startF,endF);
                anim.setDuration(300);
                anim.start();
                position--;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (position == (num-1)){
                    break;
                }
                float startF1 = (float) (position*slide_leght);
                float endF1 = (float) ((position+1)*slide_leght);
                ObjectAnimator anim2 = ObjectAnimator.ofFloat(this, "translationY", startF1,endF1);
                anim2.setDuration(300);
                anim2.start();
                position++;
                break;
        }
        if (position == 0){
            MainRecyclerViewShow mMainRecyclerViewShow = new MainRecyclerViewShow(true);
            EventBus.getDefault().post(mMainRecyclerViewShow);
        }
        else{
            MainRecyclerViewShow mMainRecyclerViewShow = new MainRecyclerViewShow(false);
            EventBus.getDefault().post(mMainRecyclerViewShow);
        }
        return super.onKeyDown(keyCode, event);
    }
}

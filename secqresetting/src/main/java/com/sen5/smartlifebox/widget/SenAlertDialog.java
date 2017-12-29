package com.sen5.smartlifebox.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sen5.smartlifebox.R;

/**
 * Created by jiangyicheng on 2017/4/6.
 */

public class SenAlertDialog extends AlertDialog {
    protected SenAlertDialog(Context context) {
        super(context);
    }

    protected SenAlertDialog(Context context, @StyleRes int theme) {
        super(context, theme);
    }

    protected SenAlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void show() {
        super.show();


    }

    @Override
    public void create() {
        super.create();
    }

    public void setTextSize(){
        setDialogText(getWindow().getDecorView());
    }

    public void setDialogText(View v){
        if(v instanceof ViewGroup){
            ViewGroup parent=(ViewGroup)v;
            int count=parent.getChildCount();
            for(int i=0;i<count;i++){
                View child=parent.getChildAt(i);
                setDialogText(child);
            }
        }else if(v instanceof TextView){
            ((TextView)v).setTextSize(getContext().getResources().getDimension(R.dimen.text_size_normal));
        }
    }

    public static class MyBuilder extends AlertDialog.Builder{

        public MyBuilder(Context context) {
            super(context);
        }

        public MyBuilder(Context context, int theme) {
            super(context, theme);
        }
    }

}

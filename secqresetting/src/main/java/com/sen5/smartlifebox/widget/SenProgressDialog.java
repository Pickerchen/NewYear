package com.sen5.smartlifebox.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by jiangyicheng on 2017/4/6.
 */

public class SenProgressDialog extends ProgressDialog{
    public SenProgressDialog(Context context) {
        super(context);
    }

    public SenProgressDialog(Context context, int theme) {
        super(context, theme);
        View v = getWindow().getDecorView();
        setDialogText(v);
    }

    private void setDialogText(View v){
        if(v instanceof ViewGroup){
            ViewGroup parent=(ViewGroup)v;
            int count=parent.getChildCount();
            for(int i=0;i<count;i++){
                View child=parent.getChildAt(i);
                setDialogText(child);
            }
        }else if(v instanceof TextView){
            ((TextView)v).setTextSize(40);
        }
    }
}

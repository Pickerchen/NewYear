package com.sen5.smartlifebox.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.SecQreSettingApplication;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by wanglin on 2016/9/1.
 */

public abstract class BaseFragment extends Fragment {

    protected Context mContext;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        RefWatcher refWatcher = SecQreSettingApplication.getRefWatcher();
        refWatcher.watch(getActivity());
    }

    protected void showProgressDialog(String msg){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(mContext);
        }
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
        setDialogText(mProgressDialog.getWindow().getDecorView());
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
            ((TextView)v).setTextSize(getActivity().getResources().getDimension(R.dimen.text_size_normal));
        }
    }

    protected void dismissProgressDialog(){
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }

    public void startActivityAnim(Intent intent) {
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.activity_slide_right_in,
                R.anim.activity_slide_remain);
    }

    public void startActivityForResultAnim(Intent intent, int requestCode) {

        startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.activity_slide_right_in,
                R.anim.activity_slide_remain);
    }
}

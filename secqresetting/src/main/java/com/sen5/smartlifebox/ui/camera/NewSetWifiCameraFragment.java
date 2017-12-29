package com.sen5.smartlifebox.ui.camera;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseFragment;
import com.sen5.smartlifebox.common.utils.Utils;

import nes.ltlib.utils.AppLog;

import com.sen5.smartlifebox.widget.WheelView;

import java.util.ArrayList;

import nes.ltlib.data.CameraEntity;
import nes.ltlib.interf.CameraSearchListener;
import nes.ltlib.LTSDKManager;

/**
 * Created by ZHOUDAO on 2017/9/6.
 */

public class NewSetWifiCameraFragment extends BaseFragment implements CameraSearchListener, Handler.Callback {

    public static NewSetWifiCameraFragment newInstance() {

        NewSetWifiCameraFragment fragment = new NewSetWifiCameraFragment();

        return fragment;
    }


    WheelView wheelView;

    private ArrayList<CameraEntity> cameraEntities;
    private Handler mHandler;

    private static final int SEARCH_COMPLETE = 1001;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String gid;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_set_wifi_fragment, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        initData();
    }

    private void initView() {

        wheelView = (WheelView) getView().findViewById(R.id.wheel_view);

        LTSDKManager.getInstance().setSearchListener(this);
        searchLTIpc();

        showProgressDialog("Searching Camera");
    }

    private void initData() {
        wheelView.addItem(createTitleView(getString(R.string.select_camera)));


        mHandler = new Handler(this);


        wheelView.setmOnItemClickListener(new WheelView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position > 0) {


                }
            }
        });

    }

    private View createTitleView(String title) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.view_title_add_camera, null, false);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dip2px(getResources(), 60)));
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvTitle.setText(title);

        return view;
    }

    /**
     * 搜索LTIpc
     */
    private void searchLTIpc() {
        LTSDKManager.getInstance().searchCamera(1);
        AppLog.i("搜索LTIpc");
    }


    private void stopSearchLtIPC() {
//        LTSDKManager.getInstance().stopSearch();
    }

    @Override
    public void onSearchFinish(ArrayList<CameraEntity> list) {
        cameraEntities = new ArrayList<>();
        cameraEntities.addAll(list);

        mHandler.sendEmptyMessage(SEARCH_COMPLETE);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case SEARCH_COMPLETE:
                dismissProgressDialog();

                for (CameraEntity entity : cameraEntities) {

                    View view = getActivity().getLayoutInflater()
                            .inflate(R.layout.item_camera, wheelView, false);

                    TextView tvName = (TextView) view.findViewById(R.id.tv_name);
                    tvName.setText(entity.getDeviceName());
                    wheelView.addItem(view);

                }


                break;
        }


        return false;
    }
}

package com.sen5.smartlifebox.ui.camera;


import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseFragment;
import com.sen5.smartlifebox.common.utils.TestToast;
import com.sen5.smartlifebox.common.utils.Utils;
import com.sen5.smartlifebox.widget.WheelView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class AllWifiCameraFragment extends BaseFragment {


    WheelView wheelView;
    private View contentView;

    private List<ScanResult> mScanResults;

    public AllWifiCameraFragment() {
        // Required empty public constructor
    }

    public static AllWifiCameraFragment newInstance(List<ScanResult> scanResults){
        AllWifiCameraFragment fragment = new AllWifiCameraFragment();
        Bundle arg = new Bundle();
        arg.putParcelableArrayList("mScanResults", (ArrayList<? extends Parcelable>) scanResults);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mScanResults = getArguments().getParcelableArrayList("mScanResults");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (contentView == null) {
            //这里的inflate跟Activity中的inflate不同
            contentView = inflater.inflate(R.layout.fragment_main, container, false);
            initView();
            initData();
        }
        return contentView;
    }

    private void initView(){
        wheelView = (WheelView) contentView.findViewById(R.id.wheel_view);

    }

    private void initData() {
        if (mScanResults == null) {
            return;
        }

        //将wifi按照信号强度排序
        Collections.sort(mScanResults, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return rhs.level - lhs.level;
            }
        });
        for (int i = 0; i < mScanResults.size(); i++) {
            ScanResult scanResult = mScanResults.get(i);
            View view = getActivity().getLayoutInflater().inflate(R.layout.item_wifi_name, null, false);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    Utils.dip2px(getResources(), 60)));
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_name);
            ImageView tvLevel = (ImageView) view.findViewById(R.id.iv_level);
            tvTitle.setText(scanResult.SSID);
            String capabilities = scanResult.capabilities.trim();
            if(capabilities.equals("") || capabilities.equals("[ESS]")){
                //wifi没有加密
                tvLevel.setImageResource(R.drawable.level_list_wifi);
            }else{
                //wifi被加密
                tvLevel.setImageResource(R.drawable.level_list_wifi_secure);
            }
            tvLevel.setImageLevel(Math.abs(scanResult.level));
            wheelView.addItem(view);

//            AppLog.e(mScanResults.get(i).SSID  + " : " + mScanResults.get(i).level);
        }

        wheelView.setmOnItemClickListener(new WheelView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                TestToast.showShort(mContext, "点击：" + position);
                String wifiName = mScanResults.get(position).SSID;
                Intent intent = new Intent(mContext, EnterWifiPasswordActivity.class);
                intent.putExtra("WifiName", wifiName);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        wheelView.requestFocus();
    }

    private List<ScanResult> searchWifi(){
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> list = wifiManager.getScanResults();

        return list;
    }

}

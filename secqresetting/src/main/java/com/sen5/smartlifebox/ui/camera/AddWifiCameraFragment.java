package com.sen5.smartlifebox.ui.camera;


import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseFragment;
import com.sen5.smartlifebox.common.utils.TestToast;
import com.sen5.smartlifebox.common.utils.Utils;
import com.sen5.smartlifebox.common.utils.WifiSearcher;
import nes.ltlib.utils.AppLog;
import com.sen5.smartlifebox.data.event.SwitchFragmentEvent;
import com.sen5.smartlifebox.widget.WheelView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;




public class AddWifiCameraFragment extends BaseFragment {

    WheelView wheelView;

    private View contentView;

    private WifiManager mWifiManager;
    private List<String> mWifiNames;
    private List<ScanResult> mScanResults;
    private WifiSearcher mWifiSearcher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TestToast.showShort(mContext, "AddWifiCameraFragment onCreateView");
        if (contentView == null) {
            //这里的inflate跟Activity中的inflate不同
            AppLog.i("AddWifiCameraFragment contentView == null ");
            contentView = inflater.inflate(R.layout.fragment_main, container, false);
            initView();
            initData();
        }
        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        wheelView.requestFocus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TestToast.showShort(mContext, "AddWifiCameraFragment onDestroy");
        //停止wifi搜索
        mWifiSearcher.stopSearch();
    }

    public AddWifiCameraFragment() {
        // Required empty public constructor
    }

    private void initView(){
        wheelView = (WheelView) contentView.findViewById(R.id.wheel_view);

    }


    private void initData() {
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mWifiNames = new ArrayList<>();
        showProgressDialog(getString(R.string.scan_wifi));

        //往WheelView中添加条目
        View titleView = LayoutInflater.from(mContext).inflate(R.layout.view_title_add_camera, null, false);
        titleView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dip2px(getResources(), 60)));
        TextView tvTitle1 = (TextView) titleView.findViewById(R.id.tv_title);
        tvTitle1.setText(getString(R.string.available_networks));
        wheelView.addItem(titleView);
        wheelView.addItem(getString(R.string.add_new_network));
        wheelView.setmOnItemClickListener(new WheelView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                TestToast.showShort(mContext, "点击：" + position);
                if (position == 0) {
                    return;
                }

                int size = wheelView.getItemCount();
                if (position == size - 2) {
                    //see all
                    EventBus.getDefault().post(new SwitchFragmentEvent(SwitchFragmentEvent.ALL_WIFI_CAMERA_FRAGMENT));
                } else if (position == size - 1) {
                    //add new network
                    Intent intent = new Intent(mContext, EnterWifiNameActivity.class);
                    startActivityAnim(intent);

                } else {
                    //wifi name
                    if (mWifiNames != null) {
                        String wifiName = mWifiNames.get(position - 1);
                        Intent intent = new Intent(mContext, EnterWifiPasswordActivity.class);
                        intent.putExtra("WifiName", wifiName);
                        startActivityAnim(intent);
                    }
                }

            }
        });

        //打开wifi，并扫描
        mWifiSearcher = new WifiSearcher(mContext, mSearchWifiListener);
        mWifiSearcher.startSearch();
    }


    WifiSearcher.SearchWifiListener mSearchWifiListener = new WifiSearcher.SearchWifiListener() {
        @Override
        public void onSearchWifiFailed(WifiSearcher.ErrorType errorType) {
            if (errorType == WifiSearcher.ErrorType.SEARCH_WIFI_TIMEOUT) {
                if (AddWifiCameraFragment.this.isAdded()) {
                    Toast.makeText(mContext, getString(R.string.search_wifi_timeout), Toast.LENGTH_LONG).show();
                }
            } else if (errorType == WifiSearcher.ErrorType.NO_WIFI_FOUND) {
                if (AddWifiCameraFragment.this.isAdded()) {
                    Toast.makeText(mContext, getString(R.string.no_search_wifi), Toast.LENGTH_LONG).show();
                }
                AppLog.e("没有扫描到wifi");
            }
            dismissProgressDialog();
        }

        @Override
        public void onSearchWifiSuccess(List<ScanResult> scanResults) {
            AppLog.e("扫描到Wifi的数量：" + scanResults.size());
            //过滤5g wifi
            AddWifiCameraFragment.this.mScanResults = Utils.filter5GWifi(scanResults);
            //将wifi按照信号强度排序
            Collections.sort(scanResults, new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult lhs, ScanResult rhs) {
                    return rhs.level - lhs.level;
                }
            });
            for (int i = 0; i < scanResults.size(); i++) {
                if (i > 2) {
                    break;
                }
                ScanResult scanResult = scanResults.get(i);
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_wifi_name, null, false);
                view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        Utils.dip2px(getResources(), 60)));
                TextView tvTitle = (TextView) view.findViewById(R.id.tv_name);
                ImageView tvLevel = (ImageView) view.findViewById(R.id.iv_level);
                tvTitle.setText(scanResult.SSID);

                String capabilities = scanResult.capabilities.trim();
                if (capabilities.equals("") || capabilities.equals("[ESS]")) {
                    //wifi没有加密
                    tvLevel.setImageResource(R.drawable.level_list_wifi);
                    AppLog.e("wifi没有加密");
                } else {
                    //wifi被加密
                    tvLevel.setImageResource(R.drawable.level_list_wifi_secure);
//                    AppLog.e("wifi被加密");
                }
                tvLevel.setImageLevel(Math.abs(scanResult.level));

                wheelView.addItem(view, wheelView.getItemCount() - 1);

                mWifiNames.add(scanResults.get(i).SSID);
//                AppLog.e(mScanResults.get(i).SSID + " : " + mScanResults.get(i).level);
            }

            wheelView.addItem(getString(R.string.see_all), wheelView.getItemCount() - 1);

            dismissProgressDialog();
        }
    };

    public List<ScanResult> getScanResults() {
        return mScanResults;
    }
}




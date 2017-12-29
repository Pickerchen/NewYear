package com.sen5.smartlifebox.ui.main;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseFragment;
import com.sen5.smartlifebox.common.utils.NetUtils;
import com.sen5.smartlifebox.common.utils.TestToast;
import com.sen5.smartlifebox.data.event.SwitchFragmentEvent;
import com.sen5.smartlifebox.ui.pairhome.PairHomeActivity;
import com.sen5.smartlifebox.widget.WheelView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends BaseFragment {

    WheelView wheelView;

    public static final int INDEX_PAIR_HOME = 0;
    public static final int INDEX_IP_CAMERAS = 1;
    public static final int INDEX_MEMBERS = 2;
    public static final int INDEX_OTHERS = 3;
    private View contentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TestToast.showShort(mContext, "MainFragment onCreateView");
        if (contentView == null) {//通过这个判断可以防止重新创建ContentView
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
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.pair_home));
        items.add(getString(R.string.ip_cameras));
        items.add(getString(R.string.members));
        items.add(getString(R.string.other));
        wheelView.addItem(items);

        wheelView.setmOnItemClickListener(new WheelView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                TestToast.showShort(mContext, "点击：" + position);

                switch (position) {
                    case INDEX_PAIR_HOME:
                        if(NetUtils.isConnected(mContext)){
                            Intent pairHomeIntent = new Intent(mContext, PairHomeActivity.class);
                            startActivityAnim(pairHomeIntent);
                        }else{
                            Toast.makeText(mContext, mContext.getString(R.string.please_connect_network),
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case INDEX_IP_CAMERAS:
                        EventBus.getDefault().post(new SwitchFragmentEvent(SwitchFragmentEvent.IP_CAMERA_FRAGMENT));
                        break;

                    case INDEX_MEMBERS:
                        if(NetUtils.isConnected(mContext)){
                            EventBus.getDefault().post(new SwitchFragmentEvent(SwitchFragmentEvent.MEMBERS_FRAGMENT));
                        }else{
                            Toast.makeText(mContext, mContext.getString(R.string.please_connect_network),
                                    Toast.LENGTH_SHORT).show();
                        }

                    break;

                    case INDEX_OTHERS:{
                        EventBus.getDefault().post(new SwitchFragmentEvent(SwitchFragmentEvent.OTHER_FRAGMENT));
                    }

                    break;
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        TestToast.showShort(mContext, "MainFragment onStart");
        wheelView.requestFocus();
    }
}

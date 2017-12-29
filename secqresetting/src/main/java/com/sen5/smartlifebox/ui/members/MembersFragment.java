package com.sen5.smartlifebox.ui.members;


import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseFragment;
import com.sen5.smartlifebox.common.utils.TestToast;
import nes.ltlib.utils.AppLog;
import com.sen5.smartlifebox.data.entity.MemberEntity;
import com.sen5.smartlifebox.data.event.MembersEvent;
import com.sen5.smartlifebox.data.event.SwitchFragmentEvent;
import com.sen5.smartlifebox.data.p2p.P2PModel;

import com.sen5.smartlifebox.widget.WheelView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;



public class MembersFragment extends BaseFragment {

    private View contentView;

    WheelView wheelView;

    P2PModel mP2PModel;

    private List<MemberEntity> mMembers;

    public MembersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        TestToast.showShort(mContext, "MembersFragment onAttach");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        wheelView.requestFocus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (contentView == null) {
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

        mP2PModel = P2PModel.getInstance(getActivity());

        wheelView.setmOnItemClickListener(new WheelView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                TestToast.showShort(mContext, "点击：" + position);

                SwitchFragmentEvent event = new SwitchFragmentEvent(SwitchFragmentEvent.EDIT_MEMBER_FRAGMENT);
                event.setMember(mMembers.get(position));
                EventBus.getDefault().post(event);
            }
        });

        mMembers = mP2PModel.getMembers();
        showMembersLayout();

        //        //获取成员列表o
//        String data = JsonCreator.createListUserJson();
//        mP2PModel.sendData(data);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MembersEvent event) {
        int flag = event.getFlag();
        switch (flag) {
//            case MembersEvent.LIST_USER:
//                AppLog.i("获取用户列表成功");
//                mMembers = mP2PModel.getMembers();
//                showMembersLayout();
//                break;

            case MembersEvent.DELETE_USER:
                AppLog.i("删除用户成功");
                showMembersLayout();
                //回到MemberFragment
                getActivity().getSupportFragmentManager().popBackStackImmediate(
                        SwitchFragmentEvent.EDIT_MEMBER_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                break;

            case MembersEvent.RENAME_USER:
                AppLog.i("重命名用户成功");
                showMembersLayout();
                getActivity().getSupportFragmentManager().popBackStackImmediate(
                        SwitchFragmentEvent.EDIT_MEMBER_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                break;

        }
    }

    private void showMembersLayout() {
        wheelView.removeAllItem();
        for (MemberEntity member : mMembers) {
            wheelView.addItem(member.getIdentity_name());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        TestToast.showShort(mContext, "MembersFragment onDetach");
        EventBus.getDefault().unregister(this);
    }


}

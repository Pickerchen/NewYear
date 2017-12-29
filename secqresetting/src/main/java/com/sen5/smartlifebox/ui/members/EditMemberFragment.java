package com.sen5.smartlifebox.ui.members;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseFragment;
import com.sen5.smartlifebox.common.utils.TestToast;
import com.sen5.smartlifebox.data.entity.MemberEntity;
import com.sen5.smartlifebox.data.event.SwitchFragmentEvent;
import com.sen5.smartlifebox.widget.WheelView;

import org.greenrobot.eventbus.EventBus;



public class EditMemberFragment extends BaseFragment {
    private View contentView;

    WheelView wheelView;

    public EditMemberFragment() {
        // Required empty public constructor
    }

    public static EditMemberFragment newInstance(MemberEntity member) {
        EditMemberFragment fragment = new EditMemberFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("member", member);
        fragment.setArguments(bundle);
        return fragment;
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

    @Override
    public void onStart() {
        super.onStart();
        wheelView.requestFocus();
    }

    private void initView() {
        wheelView = (WheelView) contentView.findViewById(R.id.wheel_view);

    }

    private void initData() {
        final MemberEntity member = getArguments().getParcelable("member");
        wheelView.addItem(mContext.getString(R.string.rename));
        wheelView.addItem(mContext.getString(R.string.delete));

        wheelView.setmOnItemClickListener(new WheelView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                TestToast.showShort(mContext, "点击：" + position);
                if (position == 0) {
                    Intent intent = new Intent(mContext, EditMemberNameActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("member", member);
                    intent.putExtras(bundle);
                    startActivityAnim(intent);

                } else if (position == 1) {
                    SwitchFragmentEvent event = new SwitchFragmentEvent(SwitchFragmentEvent.DELETE_MEMBER_FRAGMENT);
                    event.setMember(member);
                    EventBus.getDefault().post(event);
                }
            }
        });

    }

}

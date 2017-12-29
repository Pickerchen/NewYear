package com.sen5.smartlifebox.ui.members;


import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseFragment;
import com.sen5.smartlifebox.common.utils.TestToast;
import com.sen5.smartlifebox.data.JsonCreator;
import com.sen5.smartlifebox.data.entity.MemberEntity;
import com.sen5.smartlifebox.data.event.SwitchFragmentEvent;
import com.sen5.smartlifebox.data.p2p.P2PModel;

import com.sen5.smartlifebox.widget.WheelView;



public class DeleteMemberFragment extends BaseFragment {
    private View contentView;

    WheelView wheelView;

    P2PModel mP2PModel;
    private MemberEntity member;

    public DeleteMemberFragment() {
        // Required empty public constructor
    }

    public static DeleteMemberFragment newInstance(MemberEntity member) {
        DeleteMemberFragment fragment = new DeleteMemberFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("member", member);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            member = getArguments().getParcelable("member");
        }
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


    private void initView() {
        wheelView = (WheelView) contentView.findViewById(R.id.wheel_view);
    }

    private void initData() {

        mP2PModel = P2PModel.getInstance(getActivity());


        wheelView.addItem(mContext.getString(R.string.yes));
        wheelView.addItem(mContext.getString(R.string.no));

        wheelView.setmOnItemClickListener(new WheelView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                TestToast.showShort(mContext, "点击：" + position);
                if (position == 0) {
                    String data = JsonCreator.createDeleteUserJson(member.getIdentity_id());
                    mP2PModel.sendData(data);

                } else if (position == 1) {
                    //回到EditMemberFragment
                    getActivity().getSupportFragmentManager().popBackStackImmediate(SwitchFragmentEvent.DELETE_MEMBER_FRAGMENT,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        wheelView.requestFocus();
    }
}

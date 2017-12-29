package com.sen5.smartlifebox.ui.other;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseFragment;
import com.sen5.smartlifebox.data.IniFileOperate;
import com.sen5.smartlifebox.data.entity.ContactEntity;
import com.sen5.smartlifebox.data.event.SwitchFragmentEvent;
import com.sen5.smartlifebox.widget.WheelView;


/**
 * Created by wanglin on 2017/2/8.
 */
public class AffirmDeleteFragment extends BaseFragment {

    public static final int INDEX_CONFIRM = 0;
    public static final int INDEX_CANCEL = 1;

    private View contentView;
    WheelView wheelView;

    public AffirmDeleteFragment() {
        // Required empty public constructor
    }

    public static AffirmDeleteFragment newInstance(ContactEntity contact){
        AffirmDeleteFragment fragment = new AffirmDeleteFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("ContactEntity", contact);
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

    private void initView(){
        wheelView = (WheelView) contentView.findViewById(R.id.wheel_view);

    }

    private void initData() {
        final ContactEntity contact = getArguments().getParcelable("ContactEntity");

        wheelView.addItem(mContext.getString(R.string.confirm));
        wheelView.addItem(mContext.getString(R.string.cancel));

        wheelView.setmOnItemClickListener(new WheelView.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {

                switch (position) {
                    case INDEX_CONFIRM:
                        EmergencyContactFragment.contacts.remove(contact);
                        IniFileOperate.alterContactIni(EmergencyContactFragment.contacts);
                        getActivity().getSupportFragmentManager().popBackStackImmediate(
                                SwitchFragmentEvent.MANAGE_CONTACT_FRAGMENT, 0);
                        break;
                    case INDEX_CANCEL:
                        getActivity().getSupportFragmentManager().popBackStackImmediate(
                                SwitchFragmentEvent.EDIT_CONTACT_FRAGMENT, 0);
                        break;

                }
            }
        });
    }
}

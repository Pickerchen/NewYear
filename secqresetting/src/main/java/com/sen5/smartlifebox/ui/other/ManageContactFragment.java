package com.sen5.smartlifebox.ui.other;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseFragment;
import com.sen5.smartlifebox.data.entity.ContactEntity;
import com.sen5.smartlifebox.data.event.SwitchFragmentEvent;
import com.sen5.smartlifebox.widget.WheelView;

import org.greenrobot.eventbus.EventBus;



import static com.sen5.smartlifebox.ui.other.EmergencyContactFragment.contacts;

/**
 * Created by wanglin on 2017/2/8.
 */
public class ManageContactFragment extends BaseFragment {


    private View contentView;
    WheelView wheelView;

    public ManageContactFragment() {
        // Required empty public constructor
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
        wheelView.removeAllItem();
        for (ContactEntity contact : EmergencyContactFragment.contacts) {
            View view = getActivity().getLayoutInflater()
                    .inflate(R.layout.item_contact, wheelView, false);

            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            ImageView ivDot = (ImageView) view.findViewById(R.id.iv_dot);
            TextView tvLabel = (TextView) view.findViewById(R.id.tv_label);
            TextView tvNumber = (TextView) view.findViewById(R.id.tv_number);

            tvName.setText(contact.getName());
            tvNumber.setText(contact.getNumber());
            if (contact.getEmergencyFlag() == 1) {
                tvLabel.setVisibility(View.VISIBLE);
            } else {
                tvLabel.setVisibility(View.INVISIBLE);
            }
            wheelView.addItem(view);
        }

        wheelView.requestFocus();
    }

    private void initView() {
        wheelView = (WheelView) contentView.findViewById(R.id.wheel_view);
    }

    private void initData() {
        wheelView.setmOnItemClickListener(new WheelView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                SwitchFragmentEvent event = new SwitchFragmentEvent(
                        SwitchFragmentEvent.EDIT_CONTACT_FRAGMENT);
                event.setContact(contacts.get(position));
                EventBus.getDefault().post(event);
            }
        });
    }

}

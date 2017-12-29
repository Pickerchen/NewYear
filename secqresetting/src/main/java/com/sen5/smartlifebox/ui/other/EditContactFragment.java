package com.sen5.smartlifebox.ui.other;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseFragment;
import com.sen5.smartlifebox.data.entity.ContactEntity;
import com.sen5.smartlifebox.data.event.SwitchFragmentEvent;
import com.sen5.smartlifebox.widget.WheelView;

import org.greenrobot.eventbus.EventBus;



/**
 * Created by wanglin on 2017/2/8.
 */
public class EditContactFragment extends BaseFragment {
    public static final int INDEX_EDIT_NUMBER = 0;
    public static final int INDEX_EDIT_NAME = 1;
    public static final int INDEX_DELETE = 2;

    private View contentView;
    WheelView wheelView;

    public EditContactFragment() {
        // Required empty public constructor
    }

    public static EditContactFragment newInstance(ContactEntity contact){
        EditContactFragment fragment = new EditContactFragment();
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

        wheelView.addItem(getString(R.string.edit_number));
        wheelView.addItem(getString(R.string.edit_name));
        wheelView.addItem(getString(R.string.delete));

        wheelView.setmOnItemClickListener(new WheelView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                switch (position) {
                    case INDEX_EDIT_NUMBER: {
                        Intent intent = new Intent(getActivity(), EditContactNumberActivity.class);
                        intent.putExtra("From", 1);
                        intent.putExtra("ContactEntity", contact);
                        startActivityAnim(intent);
                    }
                    break;

                    case INDEX_EDIT_NAME: {
                        Intent intent = new Intent(getActivity(), EditContactNameActivity.class);
                        intent.putExtra("From", 1);
                        intent.putExtra("ContactEntity", contact);
                        startActivityAnim(intent);
                    }
                    break;

                    case INDEX_DELETE:
                        SwitchFragmentEvent event = new SwitchFragmentEvent(
                                SwitchFragmentEvent.AFFIREM_DELETE_FRAGMENT);
                        event.setContact(contact);
                        EventBus.getDefault().post(event);
                        break;

                }
            }
        });
    }
}

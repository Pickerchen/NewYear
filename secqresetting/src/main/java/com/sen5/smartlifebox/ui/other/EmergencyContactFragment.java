package com.sen5.smartlifebox.ui.other;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseFragment;
import com.sen5.smartlifebox.common.utils.Utils;
import com.sen5.smartlifebox.data.IniFileOperate;
import com.sen5.smartlifebox.data.entity.ContactEntity;
import com.sen5.smartlifebox.data.event.SwitchFragmentEvent;
import com.sen5.smartlifebox.widget.WheelView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;



/**
 * Created by wanglin on 2017/2/8.
 */
public class EmergencyContactFragment extends BaseFragment {

    public static final int INDEX_ADD_NEW_CONTACT = 0;
    public static final int INDEX_CONTACT_MANAGEMENT = 1;

    public static List<ContactEntity> contacts;//存储已经添加的联系人

    private View contentView;
    WheelView wheelView;

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
        //显示联系人
        wheelView.removeItems(3, wheelView.getItemCount() - 3);
        for (ContactEntity contact : contacts) {
            //和 other中的notification system项 共用一个view
            View view = getActivity().getLayoutInflater()
                    .inflate(R.layout.item_contact, wheelView, false);

            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            ImageView ivDot = (ImageView) view.findViewById(R.id.iv_dot);
            TextView tvLabel = (TextView) view.findViewById(R.id.tv_label);
            TextView tvNumber = (TextView) view.findViewById(R.id.tv_number);

            tvName.setText(contact.getName());
            tvNumber.setText(contact.getNumber());
            if (contact.getEmergencyFlag() == 1) {
                ivDot.setVisibility(View.VISIBLE);
                tvLabel.setVisibility(View.VISIBLE);
            } else {
                ivDot.setVisibility(View.INVISIBLE);
                tvLabel.setVisibility(View.INVISIBLE);
            }
            wheelView.addItem(view);
        }

        wheelView.requestFocus();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        contacts.clear();
        contacts = null;
    }

    public EmergencyContactFragment() {
        // Required empty public constructor
    }


    private void initView(){
        wheelView = (WheelView) contentView.findViewById(R.id.wheel_view);
    }


    private void initData() {
        //读取联系人
        contacts = IniFileOperate.readContactIni();

        wheelView.addItem(getString(R.string.add_new_contact));
        wheelView.addItem(getString(R.string.contact_management));
        wheelView.addItem(createTitleView(getString(R.string.emergency_contact_selection)));

        wheelView.setmOnItemClickListener(new WheelView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                if (position == INDEX_ADD_NEW_CONTACT) {
                    Intent intent = new Intent(getActivity(), EditContactNumberActivity.class);
                    intent.putExtra("From", 0);
                    startActivityAnim(intent);
                } else if (position == INDEX_CONTACT_MANAGEMENT) {
                    EventBus.getDefault().post(new SwitchFragmentEvent(
                            SwitchFragmentEvent.MANAGE_CONTACT_FRAGMENT));
                } else if (position > 2) {
                    //设置、取消紧急联系人
                    ContactEntity contact = contacts.get(position - 3);
                    View ivDot = wheelView.getItem(position).findViewById(R.id.iv_dot);
                    View tvLabel = wheelView.getItem(position).findViewById(R.id.tv_label);

                    if (contact.getEmergencyFlag() == 0) {
                        contact.setEmergencyFlag(1);
                        ivDot.setVisibility(View.VISIBLE);
                        tvLabel.setVisibility(View.VISIBLE);
                    } else {
                        contact.setEmergencyFlag(0);
                        ivDot.setVisibility(View.INVISIBLE);
                        tvLabel.setVisibility(View.INVISIBLE);
                    }
                    IniFileOperate.alterContactIni(contacts);
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
}

package com.sen5.smartlifebox.ui.other;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.ChooseBaseFragment;
import com.sen5.smartlifebox.common.utils.PreferencesUtils;
import com.sen5.smartlifebox.data.event.SwitchFragmentEvent;

/**
 * Created by jiangyicheng on 2017/2/21.
 */

public class NotificationSystemChooseFragment extends ChooseBaseFragment{
    private static final String SP_NOTIFICATION_SWITCH = "sp_notifi_switch";
    private static final boolean DEFAULT_NOTIFICATION_SWITCH = true;

    @Override
    protected void initView() {
        boolean isShowNotificationToast = getIsShowNotificationToast(mContext);
        mYesStr = mContext.getString(R.string.on);
        mNoStr = mContext.getString(R.string.off);

        for (int i = 0; i < 2; i++) {

            View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_choose_switch, wheelView, false);
            View dot = inflate.findViewById(R.id.iv_dot);
            TextView name = (TextView)inflate.findViewById(R.id.tv_name);


            switch (i){
                case 0:
                    name.setText(mYesStr);
                    if(isShowNotificationToast){
                        dot.setVisibility(View.VISIBLE);
                    }else{
                        dot.setVisibility(View.INVISIBLE);
                    }

                    break;
                case 1:
                    name.setText(mNoStr);
                    if(!isShowNotificationToast){
                        dot.setVisibility(View.VISIBLE);
                    }else{
                        dot.setVisibility(View.INVISIBLE);
                    }

                    break;
            }
            wheelView.addItem(inflate);
        }
//        super.initView();
    }

    @Override
    protected void initData() {

        super.initData();

    }

    @Override
    protected void clickYes() {
        getActivity().getSupportFragmentManager().popBackStack(SwitchFragmentEvent.NOTIFICATION_SYSTEM_CHOOSE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        PreferencesUtils.putBoolean(mContext, SP_NOTIFICATION_SWITCH, true);
//        P2PModel.getInstance(mContext).setShowNotificationToast(true);
    }

    @Override
    protected void clickNo() {
        getActivity().getSupportFragmentManager().popBackStack(SwitchFragmentEvent.NOTIFICATION_SYSTEM_CHOOSE,  FragmentManager.POP_BACK_STACK_INCLUSIVE);
        PreferencesUtils.putBoolean(mContext, SP_NOTIFICATION_SWITCH, false);
//        P2PModel.getInstance(mContext).setShowNotificationToast(false);
    }

    public static boolean getIsShowNotificationToast(Context context){
        return  PreferencesUtils.getBoolean(context, SP_NOTIFICATION_SWITCH, DEFAULT_NOTIFICATION_SWITCH);
    };



}

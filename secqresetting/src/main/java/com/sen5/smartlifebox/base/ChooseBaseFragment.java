package com.sen5.smartlifebox.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.widget.WheelView;



/**
 * Created by jiangyicheng on 2017/2/21.
 */

public abstract class ChooseBaseFragment extends BaseFragment{
    public static final int INDEX_YES = 0;
    public static final int INDEX_NO = 1;

    protected int mYesId = -1;
    protected int mNoId = -1;

    private static final String NO_NE = "no-ne";
    protected String mYesStr = NO_NE;
    protected String mNoStr = NO_NE;

    protected WheelView wheelView;

    private View contentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_main, container, false);
            wheelView = (WheelView) contentView.findViewById(R.id.wheel_view);
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

    protected void initView(){

        if(mYesId != -1){
            wheelView.addItem(mContext.getString(mYesId));
        }else if(!NO_NE.equals(mYesStr)){
            wheelView.addItem(mYesStr);
        }else{
            wheelView.addItem(mContext.getString(R.string.yes));
        }

        if(mNoId != -1){
            wheelView.addItem(mContext.getString(mNoId));
        }else if(!NO_NE.equals(mNoStr)){
            wheelView.addItem(mNoStr);
        }else{
            wheelView.addItem(mContext.getString(R.string.no));
        }
    }

    protected void initData() {


        wheelView.setmOnItemClickListener(new WheelView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                switch(position){
                    case INDEX_YES:
                        clickYes();
                        break;
                    case INDEX_NO:
                        clickNo();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    protected abstract void clickYes();
    protected abstract void clickNo();

}

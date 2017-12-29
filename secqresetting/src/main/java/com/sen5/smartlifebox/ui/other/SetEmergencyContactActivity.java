package com.sen5.smartlifebox.ui.other;

import android.content.Intent;
import android.os.Bundle;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseActivity;
import com.sen5.smartlifebox.data.IniFileOperate;
import com.sen5.smartlifebox.data.entity.ContactEntity;
import com.sen5.smartlifebox.ui.main.MainActivity;
import com.sen5.smartlifebox.widget.WheelView;



public class SetEmergencyContactActivity extends BaseActivity {

    public static final int INDEX_YES = 0;
    public static final int INDEX_NO = 1;

    WheelView wheelView;
    private String mContactNumber;
    private String mContactName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_emergency_contact);
        initView();
        initData();
    }

    @Override
    public void onStart() {
        super.onStart();
        wheelView.requestFocus();
    }

    private void initView() {
        wheelView = (WheelView) findViewById(R.id.wheel_view);
    }

    private void initData() {
        mContactNumber = getIntent().getStringExtra("ContactNumber");
        mContactName = getIntent().getStringExtra("ContactName");

        wheelView.addItem(getString(R.string.yes));
        wheelView.addItem(getString(R.string.no));

        wheelView.setmOnItemClickListener(new WheelView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                ContactEntity contact = new ContactEntity();
                contact.setName(mContactName);
                contact.setNumber(mContactNumber);

                switch (position) {
                    case INDEX_YES:
                        contact.setEmergencyFlag(1);
                        break;

                    case INDEX_NO:
                        contact.setEmergencyFlag(0);
                        break;

                }
                EmergencyContactFragment.contacts.add(contact);
                //将联系人写入文件
                IniFileOperate.alterContactIni(EmergencyContactFragment.contacts);

                Intent intent = new Intent(SetEmergencyContactActivity.this, MainActivity.class);
                startActivityAnim(intent);
            }
        });
    }


}

package com.sen5.smartlifebox.ui.other;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseActivity;
import com.sen5.smartlifebox.data.IniFileOperate;
import com.sen5.smartlifebox.data.entity.ContactEntity;
import com.sen5.smartlifebox.data.event.BackFragmentEvent;

import org.greenrobot.eventbus.EventBus;


public class EditContactNameActivity extends BaseActivity implements TextView.OnEditorActionListener{

    EditText etName;

    private String mNumber;

    private int mFrom; //0：来自添加；1：来自编辑
    private ContactEntity mContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact_name);
        initView();

        initData();
    }

    private void initView(){
        etName = (EditText) findViewById(R.id.et_name);
        etName.setOnEditorActionListener(this);
    }

    private void initData() {
        mFrom = getIntent().getIntExtra("From", 0);
        mNumber = getIntent().getStringExtra("ContactNumber");
        if(mFrom == 1){
            mContact = getIntent().getParcelableExtra("ContactEntity");
            String name = mContact.getName();
            etName.setText(name);
            etName.setSelection(name.length());
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

        if (actionId == EditorInfo.IME_ACTION_SEND
                || actionId == EditorInfo.IME_ACTION_DONE
                || (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode()
                && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {
            String name = etName.getText().toString();

            String regEx = "^[a-z0-9A-Z\u4e00-\u9fa5]+$";
            if (name.equals("")) {
                Toast.makeText(EditContactNameActivity.this, getString(R.string.name_not_empty),
                        Toast.LENGTH_SHORT).show();
            } else if (!name.matches(regEx)) {
                Toast.makeText(EditContactNameActivity.this, getString(R.string.name_not_special_character),
                        Toast.LENGTH_SHORT).show();
            } else if (name.length() > 64) {
                Toast.makeText(EditContactNameActivity.this, getString(R.string.name_exceed_length),
                        Toast.LENGTH_SHORT).show();
            }else {
                if(mFrom == 0){
                    Intent intent = new Intent(EditContactNameActivity.this, SetEmergencyContactActivity.class);
                    intent.putExtra("ContactNumber", mNumber);
                    intent.putExtra("ContactName", name);
                    startActivityAnim(intent);
                }else {
                    if(name.equals(mContact.getName())){
                        finish();
                        return false;
                    }
                    for (ContactEntity contact : EmergencyContactFragment.contacts) {
                        if(contact.equals(mContact)){
                            contact.setName(name);
                            IniFileOperate.alterContactIni(EmergencyContactFragment.contacts);
                            EventBus.getDefault().post(new BackFragmentEvent(BackFragmentEvent.MANAGE_CONTACT_FRAGMENT));
                            finish();
                            break;
                        }
                    }
                }
            }

        }
        return false;
    }
}

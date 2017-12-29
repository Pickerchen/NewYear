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



public class EditContactNumberActivity extends BaseActivity implements TextView.OnEditorActionListener{

    EditText etName;
    private int mFrom; //0：来自添加；1：来自编辑
    private ContactEntity mContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact_number);
        initView();

        initData();
    }

    private void initData() {
        mFrom = getIntent().getIntExtra("From", 0);
        if(mFrom == 1){
            mContact = getIntent().getParcelableExtra("ContactEntity");
            String number = mContact.getNumber();
            etName.setText(number);
            etName.setSelection(number.length());
        }
    }


    private void initView(){
        etName = (EditText) findViewById(R.id.et_name);
        etName.setOnEditorActionListener(this);
    }


    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

        if (actionId == EditorInfo.IME_ACTION_SEND
                || actionId == EditorInfo.IME_ACTION_DONE
                || (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode()
                && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {

            String number = etName.getText().toString();

            String regEx = "^[0-9]+$";
            if (number.equals("")) {
                Toast.makeText(EditContactNumberActivity.this, getString(R.string.number_not_empty),
                        Toast.LENGTH_SHORT).show();
            } else if (!number.matches(regEx)) {
                Toast.makeText(EditContactNumberActivity.this, getString(R.string.number_only),
                        Toast.LENGTH_SHORT).show();
            }else {
                if(mFrom == 0){
                    for (ContactEntity contact : EmergencyContactFragment.contacts) {
                        if(number.equals(contact.getNumber())){
                            Toast.makeText(EditContactNumberActivity.this, getString(R.string.number_already_exist),
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                    Intent intent = new Intent(EditContactNumberActivity.this, EditContactNameActivity.class);
                    intent.putExtra("ContactNumber", number);
                    intent.putExtra("From", 0);
                    startActivityAnim(intent);
                }else {
                    for (ContactEntity contact : EmergencyContactFragment.contacts) {
                        if(!contact.equals(mContact) && number.equals(contact.getNumber())){
                            Toast.makeText(EditContactNumberActivity.this, getString(R.string.number_already_exist),
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                    if(number.equals(mContact.getNumber())){
                        finish();
                        return false;
                    }
                    for (ContactEntity contact : EmergencyContactFragment.contacts) {
                        if(contact.equals(mContact)){
                            contact.setNumber(number);
                            IniFileOperate.alterContactIni(EmergencyContactFragment.contacts);
                            EventBus.getDefault().post(new BackFragmentEvent(
                                    BackFragmentEvent.MANAGE_CONTACT_FRAGMENT));
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

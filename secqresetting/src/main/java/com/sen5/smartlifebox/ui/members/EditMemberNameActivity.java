package com.sen5.smartlifebox.ui.members;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseActivity;
import com.sen5.smartlifebox.data.JsonCreator;
import com.sen5.smartlifebox.data.entity.MemberEntity;
import com.sen5.smartlifebox.data.p2p.P2PModel;



/**
 * Created by wanglin at 2016/10/19
 */
public class EditMemberNameActivity extends BaseActivity {

    EditText etName;
    TextView tvName;

    P2PModel mP2PModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_member_name);
        initView();


        initData();
    }

    private void initView(){
        etName = (EditText) findViewById(R.id.et_name);
        tvName = (TextView) findViewById(R.id.tv_name);
    }

    private void initData() {


        mP2PModel = P2PModel.getInstance(this);

        tvName.setText(getString(R.string.enter_member_name));
        Bundle bundle = getIntent().getExtras();
        final MemberEntity member = bundle.getParcelable("member");
        etName.setText(member.getIdentity_name());
        etName.setSelection(member.getIdentity_name().length());

        etName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode()
                        && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {

                    String name = etName.getText().toString();
                    if (name != null && !name.equals("")) {

                        if (!hasName(name)){
                            String data = JsonCreator.createRenameUserJson(member.getIdentity_id(), name);
                            mP2PModel.sendData(data);

                            finish();
                        }else {
                            Toast.makeText(EditMemberNameActivity.this, R.string.tip_rename,
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditMemberNameActivity.this, getString(R.string.name_not_empty),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                return false;

            }
        });
    }



    public boolean hasName(String name){

        for (MemberEntity entity:mP2PModel.getMembers()){
            if (name.equals(entity.getIdentity_name())){
                return true;
            }
        }
        return false;
    }
}

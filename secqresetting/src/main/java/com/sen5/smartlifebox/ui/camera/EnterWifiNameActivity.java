package com.sen5.smartlifebox.ui.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.sen5.smartlifebox.R;


public class EnterWifiNameActivity extends Activity implements TextView.OnEditorActionListener {


    TextView tvName;
    EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_name);
        initView();
        initData();
    }

    private void initView(){
        etName = (EditText) findViewById(R.id.et_name);
        tvName = (TextView) findViewById(R.id.tv_name);
    }

    private void initData() {
        etName.setOnEditorActionListener(this);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

        if (actionId == EditorInfo.IME_ACTION_SEND
                || actionId == EditorInfo.IME_ACTION_DONE
                || (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode()
                && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {
            String wifiName = etName.getText().toString();
            Intent intent = new Intent(EnterWifiNameActivity.this, EnterWifiPasswordActivity.class);
            intent.putExtra("WifiName", wifiName);
            startActivity(intent);
        }
        return false;
    }

}

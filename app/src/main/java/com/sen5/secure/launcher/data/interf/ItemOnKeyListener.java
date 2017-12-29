package com.sen5.secure.launcher.data.interf;

import android.view.KeyEvent;
import android.view.View;

/**
 * Created by ZHOUDAO on 2017/6/2.
 */

public interface ItemOnKeyListener {

    boolean onKey(View v, int keyCode, KeyEvent event, int position);

}

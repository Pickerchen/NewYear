package com.sen5.secure.launcher.utils;

import android.widget.Toast;

public class ToastMaster {

	private static Toast mToast = null;

	private ToastMaster() {

	}

	public static void setToast(Toast toast) {
		if (null != mToast) {
			mToast.cancel();
		}
		mToast = toast;
	}

	public static void cancelToast() {
		if (null != mToast) {
			mToast.cancel();
		}
		mToast = null;
	}

}

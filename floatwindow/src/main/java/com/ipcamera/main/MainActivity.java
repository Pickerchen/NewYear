package com.ipcamera.main;

import java.util.ArrayList;
import java.util.List;

import com.ipcamera.main.service.FingerPrintViewService;
import com.ipcamera.main.utils.DLog;
import com.ipcamera.main.utils.MySharePre;
import com.ipcamera.main.utils.MySharePreDID;
import com.ipcamera.main.utils.UtilsDIDSP;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.os.UserHandle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements OnClickListener{

	private LinearLayout ll_addView;
	private LinearLayout ll_addview_two;
	private Resources mResources;
	private Intent intent;
	private ServiceConnection mServiceConnection;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		RecordDatabase  mRecordDatabase = new RecordDatabase(this);
//		mRecordDatabase.removeFromStatus(RecordDatabaseItem.STATUS_ING, 0, 3);
		init();
		initView();
		MySharePre.initMySharePre(this);
		MySharePreDID.initMySharePre(this);
		testSaveDID();
	}
	
	private void testSaveDID(){
		List<String> listDID = new ArrayList<String>();
		int size = 3;
		for (int i = 0; i < size; i++) {
			listDID.add("aaa+" + i);
		}
		UtilsDIDSP.setSharePreInt(listDID);
	}

	private void init() {
		mResources = getBaseContext().getResources();
		
	}

	private void initView() {
		// TODO Auto-generated method stub
		Button button1 = (Button)findViewById(R.id.button1);
		button1.setTag(1010);
		ll_addView = (LinearLayout)findViewById(R.id.ll_addview);
		ll_addview_two = (LinearLayout)findViewById(R.id.ll_addview_two);
		
//		ll_addView.findFocus();
		button1.setOnClickListener(this);
		addMyView();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch ((Integer)arg0.getTag()) {
		case 1010:
			DLog.e("----------------click");
//			Intent intent = new Intent();
//			intent.setPackage("com.ipcamera.main");
//			intent.setAction("com.ipcamera.floatviewservice");
//			intent.putExtra("flag_show_view", true);
			
//			intent.setClass(this, FingerPrintViewService.class);
//			startServiceAsUser(intent, UserHandle.OWNER);
//			startService(intent);
			
			
			intent = new Intent();
			intent.setPackage("com.ipcamera.main");
			intent.setAction("com.ipcamera.floatviewservicetest");
			intent.putExtra("flag_show_view", true);
			
//			intent.setClass(this, FingerPrintViewService.class);
			startServiceAsUser(intent, UserHandle.OWNER);
//			startService(intent);
//			mServiceConnection = new ServiceConnection() {
//				
//				@Override
//				public void onServiceDisconnected(ComponentName name) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void onServiceConnected(ComponentName name, IBinder service) {
//					// TODO Auto-generated method stub
//					
//				}
//			};
//			bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(null != mServiceConnection){
			
			unbindService(mServiceConnection);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent arg1) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_DOWN:
			
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			
			break;
		case KeyEvent.KEYCODE_0:
			Intent intent1 = new Intent();
			intent1.setAction(FingerPrintViewService.ACTION_IPCAMERA_KEY_FLOAT);
			
//			intent.setPackage("com.sen5.klauncher");
			sendBroadcast(intent1);
			
			Intent intent = new Intent();
			intent.setAction("com.ipcamera.floatviewservice");
			intent.putExtra(FingerPrintViewService.FLAG_SHOW_FLOAT_VIEW, true);
//			intent.setClass(this, FingerPrintViewService.class);
			startService(intent);
			break;
		case KeyEvent.KEYCODE_1:
			List<String> listDID = new ArrayList<String>();
			int size = 5;
			for (int i = 0; i < size; i++) {
				listDID.add("bbb+" + i);
			}
			UtilsDIDSP.setSharePreInt(listDID);
			break;
		case KeyEvent.KEYCODE_2:
			List<String> listDID1 = new ArrayList<String>();
			int size1 = 7;
			for (int i = 0; i < size1; i++) {
				listDID1.add("bbb+" + i);
			}
			
			int size2 = 5;
			for (int i = 0; i < size2; i++) {
				listDID1.add("aaa+" + i);
			}
			UtilsDIDSP.setSharePreInt(listDID1);
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, arg1);
	}
	
	public void addMyView(){}

}

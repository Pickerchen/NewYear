package com.sen5.launchertools.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sen5.launchertools.R;
import com.sen5.launchertools.widget.SlideRemoveListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JesseYao
 * @version 2016 2016年12月13日 下午7:52:07 ClassName：NotificationPopupWindow.java
 *          Description：
 */
public class NotificationPopupWindow extends PopupWindow
        implements OnClickListener, OnItemClickListener, SlideRemoveListView.OnRemoveListener {

    private NotificationReceiver nReceiver;
    private Button mStart, mStop, mNowList, mbtnCLearAll;
    private TextView mTvNoNoti;
    private SlideRemoveListView mListView;
    private List<NTBean> mNTList;
    private List<NTBean> mTempList = new ArrayList<NTBean>();
    //private List<String> mPackageList;
    private InfoListAdapter mAdapter;
    private Context mContext = null;
    private ContentResolver mCR;
    private Config mConfig;
    private NewNoticeCallback mCallback;


    public NotificationPopupWindow(Context context, View view, int width, int height,
                                   boolean focusable) {
        super(view, width, height, focusable);
        mContext = context;
        initPermission();
        initView(view);
        initData();
    }

    private void initPermission() {
        mCR = mContext.getContentResolver();
        mConfig = getConfig();
        if (!isEnabled()) { //打开接收通知的权限
            saveEnabledServices();
        } else {
            Log.d(TAG, mContext.getString(R.string.already_has_permission));
        }
    }

    private void initView(View view) {
        mStart = (Button) view.findViewById(R.id.start_receive_notification);
        mStop = (Button) view.findViewById(R.id.stop_receive_notification);
        mListView = (SlideRemoveListView) view.findViewById(R.id.lv_notification);
        mListView.setNextFocusUpId(R.id.clear_all_notifications);
        mNowList = (Button) view.findViewById(R.id.current_notification);
        mbtnCLearAll = (Button) view.findViewById(R.id.clear_all_notifications);
        mbtnCLearAll.setNextFocusDownId(R.id.lv_notification);
        mTvNoNoti = (TextView) view.findViewById(R.id.tv_no_notification);

        mNowList.setOnClickListener(this);
        mStop.setOnClickListener(this);
        mStart.setOnClickListener(this);
        mbtnCLearAll.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnRemoveListener(this);
    }

    private void initData() {
        nReceiver = new NotificationReceiver();
//        mPackageList = new ArrayList<String>();
        mNTList = new ArrayList<NTBean>();
        mAdapter = new InfoListAdapter(mContext, mNTList);
        mListView.setAdapter(mAdapter);
    }

    public void registerNotificationReceiver() {
        if (null != nReceiver) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(UPDATE);
            filter.addAction(REMOVE);
            mContext.registerReceiver(nReceiver, filter, NotificationService.BROADCAST_PERMISSION_NOTIFICATION_UPDATE, null);
        }
    }

    public void unregisterNotificationReciver() {
        if (null != nReceiver) {
            mContext.unregisterReceiver(nReceiver);
        }
    }

    private static Config getNotificationListenerConfig() {
        final Config c = new Config();
        c.tag = TAG;
        c.setting = Settings.Secure.ENABLED_NOTIFICATION_LISTENERS;
        c.intentAction = NotificationListenerService.SERVICE_INTERFACE;
        c.permission = android.Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE;
        c.noun = "notification listener";
        c.warningDialogTitle = R.string.notification_listener_security_warning_title;
        c.warningDialogSummary = R.string.notification_listener_security_warning_summary;
        c.emptyText = R.string.no_notification_listeners;
        return c;
    }

    private Config getConfig() {
        return CONFIG;
    }

    private void saveEnabledServices() {
        StringBuilder sb = new StringBuilder();
        sb.append(new ComponentName(mContext.getPackageName(),//"com.android.launcher",
                "com.sen5.launchertools.notification.NotificationService").flattenToString());
        Settings.Secure.putString(mCR,
                mConfig.setting,
                sb != null ? sb.toString() : "");
    }

    @Override
    public void onClick(View v) {
        if (v == mStart) {
            if (!isEnabled()) { //打开接收通知的权限
//                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
//                mContext.startActivity(intent);
                saveEnabledServices();
            } else {
                Toast.makeText(mContext, R.string.already_has_permission,
                        Toast.LENGTH_LONG).show();
            }
        }
        if (v == mStop) {
            Intent s = new Intent(mContext, NotificationService.class);
            mContext.stopService(s);
        }

        if (v == mNowList) {
            Intent i2 = new Intent(NotificationService.COMMAND);
            i2.putExtra(NotificationService.COMMAND_EXTRA,
                    NotificationService.GET_LIST);
            mContext.sendBroadcast(i2);
            v.setEnabled(false);
        }
        if (v == mbtnCLearAll) {
            Intent i3 = new Intent(NotificationService.COMMAND);
            i3.putExtra(NotificationService.COMMAND_EXTRA,
                    NotificationService.CANCEL_ALL);
            mContext.sendBroadcast(i3);
            mTempList.clear();
            for (NTBean bean : mNTList) {
                if (bean.clearable == false) {
                    mTempList.add(bean);
                }
            }
            mNTList.clear();
            mNTList.addAll(mTempList);
            mAdapter.notifyDataSetChanged(mTvNoNoti);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> view, View arg1, int id,
                            long arg3) {
        try {
            mContext.startActivity(mNTList.get(id).intent.getIntent());
        } catch (Exception e) {
            Log.e(TAG, "----no penddingIntent or lunch failed!------");
        }
    }

    private boolean isEnabled() {
        String pkgName = mContext.getPackageName();
        final String flat = Settings.Secure.getString(mContext.getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean removeItem(SlideRemoveListView.RemoveDirection direction, int position) {
        mNTList.remove(position);
        mAdapter.notifyDataSetChanged(mTvNoNoti);
        return true;
    }

    @Override
    public boolean isRemovable(int position) {
        if (mNTList.get(position).clearable) {
            return true;
        }
        return false;
    }

    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = mNTList.size() + "：" + intent.getStringExtra(EVENT);
            NTBean bean = new NTBean();
            bean.info = temp;

            Bundle budle = intent.getExtras();
            bean.title = budle.getString(Notification.EXTRA_TITLE);
            bean.text = budle.getString(Notification.EXTRA_TEXT);
            bean.subText = budle.getString(Notification.EXTRA_SUB_TEXT);
            bean.largeIcon = budle.getParcelable(Notification.EXTRA_LARGE_ICON);
            // Icon icon = budle.getParcelable(Notification.EXTRA_SMALL_ICON);
            // bean.smallIcon = icon;
            bean.packageName = budle.getString(PACKAGE_NAME);
            bean.intent = budle.getParcelable(PENDING_INTENT);
            bean.notificationId = budle.getInt(NOTIFICATION_ID);
            bean.clearable = budle.getBoolean(CLEARABLE);
            bean.viewS = budle.getParcelable(VIEW_S);
            bean.viewL = budle.getParcelable(View_L);
            Log.d(TAG, "packageName = " + bean.packageName +
                    " intent = " + intent +
                    " title = " + bean.title +
                    " text = " + bean.text +
                    " subText = " + bean.text +
                    " viewS = " + bean.viewS);
            if (intent.getAction().equals(UPDATE)) {
                removeRepeats(bean);
                if (bean.viewS != null) {
//                    mPackageList.add(bean.packageName);
                    mNTList.add(bean);
                    mCallback.newNoticeReceived();
                }
            } else if (intent.getAction().equals(REMOVE)) {
                removeRepeats(bean);
            }

            Log.d(TAG, "receive:" + temp + "\n" + budle);
            mAdapter.notifyDataSetChanged(mTvNoNoti);
        }

    }

    private void removeRepeats(NTBean bean) {
        int index = mNTList.indexOf(bean);
        if (index != -1) {
            mNTList.remove(index);
        }
    }

    public static class ViewHolder {
        public TextView title, notiTitel, notiText, notiSubText;
        public ImageView smallIcon, largeIcon;
        public ViewGroup contentView, bigContentView;
        public PendingIntent intent;
        public String packageName;
        public int id;
        public boolean clearable;
    }

    protected static class Config {
        String tag;
        String setting;
        String intentAction;
        String permission;
        String noun;
        int warningDialogTitle;
        int warningDialogSummary;
        int emptyText;
    }

    public void setNewNoticeCallback(NewNoticeCallback callback) {
        mCallback = callback;
    }

    public interface NewNoticeCallback {
        void newNoticeReceived();
    }

    private static final Config CONFIG = getNotificationListenerConfig();
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    public static final String UPDATE = "com.sen5.Launcher.NOTIFICATION_UPDATE";
    public static final String REMOVE = "com.sen5.Launcher.NOTIFICATION_REMOVE";
    public static final String EVENT = "notification_event";
    public static final String PACKAGE_NAME = "package_name";
    public static final String PENDING_INTENT = "pending_intent";
    // private static final String ICON_S = "small_icon";
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String CLEARABLE = "canclable";
    public static final String VIEW_S = "small_content_view";
    public static final String View_L = "large_content_view";

    private static final String TAG = NotificationPopupWindow.class.getSimpleName();

}

package com.sen5.smartlifebox.ui.pairhome;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.sen5.smartlifebox.R;
import com.sen5.smartlifebox.base.BaseActivity;
import com.sen5.smartlifebox.common.utils.ImageTools;
import com.sen5.smartlifebox.common.utils.Utils;
import nes.ltlib.utils.AppLog;
import com.sen5.smartlifebox.data.JsonCreator;
import com.sen5.smartlifebox.data.entity.MemberEntity;
import com.sen5.smartlifebox.data.event.MembersEvent;
import com.sen5.smartlifebox.data.p2p.P2PModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by wanglin on 2016/8/25.
 */

public class PairHomeActivity extends BaseActivity {


    ImageView imgQR;

    P2PModel mP2PModel;
    private String userName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_home);
        initView();
        EventBus.getDefault().register(this);
        initData();
    }


    private void initView() {
        imgQR = (ImageView) findViewById(R.id.img_qr);
    }

    private void initData() {
        mP2PModel = P2PModel.getInstance(this);

//        imgQR = (ImageView)findViewById(R.id.img_qr);
        int qrSize = getResources().getDimensionPixelSize(R.dimen.qr_size);
        int logoSize = getResources().getDimensionPixelSize(R.dimen.logo_size);
        AppLog.d("QR size == " + qrSize);
        try {
            // 普通的二维码
//			Bitmap bmpQR = Utils.createQRCode(Utils.getIPv4Address(this), size);
            String qrInfo = Utils.getQRInfoToPair(this);
            if (null != qrInfo) {
                Bitmap bmpLogo = ImageTools.fromResGetBmp(this, R.drawable.ic_secqre_h);
                // 带logo的二维码
                Bitmap bmpLogoQR = Utils.createLogoQRCode(qrInfo, bmpLogo, qrSize, logoSize, BarcodeFormat.QR_CODE);
                imgQR.setImageBitmap(bmpLogoQR);
                AppLog.e("生成二维码成功 ");
            } else {
                imgQR.setImageResource(R.drawable.ic_qr_error);
            }
        } catch (WriterException e) {
            // TODO Auto-generated catch block
            imgQR.setImageResource(R.drawable.ic_qr_error);
            AppLog.e("WriterException error == " + e.toString());
        } catch (Exception e) {
            imgQR.setImageResource(R.drawable.ic_qr_error);
            AppLog.e("Exception error == " + e.toString());
        }
    }

    private AlertDialog dialog;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MembersEvent event) {
        int flag = event.getFlag();
        switch (flag) {
            case MembersEvent.REQUEST_ADD_USER:
                AppLog.i("收到添加用户请求");
                if (null != dialog && dialog.isShowing()) {
                    AppLog.i("收到添加用户请求, 请求框处于弹出状态");
                    return;
                }

                int n = 1;
                userName = getString(R.string.member) + "-" + n;

                while (hasName(userName)) {
                    n++;
                    userName = getString(R.string.member) + "-" + n;
                }

//                go:
//                for (MemberEntity entity : mP2PModel.getMembers()) {
//                    if (userName.equals(entity.getIdentity_name())) {
//                        mP2PModel.setmCurMember(mP2PModel.getmCurMember() + 1);
//                        userName = getString(R.string.member) + "-" + (mP2PModel.getmCurMember() + 1);
//                        continue go;
//                    }
//                }


                final String addUserId = event.getAddUserId();

                dialog = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.hints))
                        .setMessage(userName + " " + getString(R.string.apply_to_join))
                        .setPositiveButton(getString(R.string.refuse), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(getString(R.string.allow), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //发送添加成员请求
                                String data = JsonCreator.createAddUserJson(addUserId, userName);
                                mP2PModel.sendData(data);
                                dialog.dismiss();
                                showProgressDialog(getString(R.string.add_member_ing));

                            }
                        }).create();
                dialog.show();
                dialog.getButton(Dialog.BUTTON_POSITIVE).setAllCaps(false);
                dialog.getButton(Dialog.BUTTON_NEGATIVE).setAllCaps(false);
                break;

            case MembersEvent.ADD_USER:
                AppLog.i("添加用户成功");
                dismissProgressDialog();
                Toast.makeText(this, getString(R.string.add_member_success), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private boolean hasName(String name) {
        for (MemberEntity memberEntity : mP2PModel.getMembers()) {
            if (memberEntity.getIdentity_name().equals(name)) {
                return true;
            }
        }

        return false;
    }
}

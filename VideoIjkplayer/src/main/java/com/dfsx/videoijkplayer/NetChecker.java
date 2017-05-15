package com.dfsx.videoijkplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by liuwb on 2016/10/18.
 */
public class NetChecker {

    public static final String SP_SET_NET_PLAY = "com.dfsx.videoijkplayer.NetNotes_SET_NET_PLAY";

    private CheckCallBack checkCallBack;
    private Context context;

    private boolean setAnyNetIsOk;//设置任何网络都行

    /**
     * 是否需把设置的数据持久化
     */
    private boolean isSaveSettingToLocal = false;

    public NetChecker(Context context, CheckCallBack checkCallBack) {
        this.checkCallBack = checkCallBack;
        this.context = context;
    }


    public void checkNet() {
        if (getSettingsNetPlay(context)) {
            checkCallBack.callBack(true);
            return;
        }
        int netConnectedStatus = getConnectivityStatus(context);
        if (netConnectedStatus == 1) {
            checkCallBack.callBack(true);
        } else {
            String notesText = netConnectedStatus == 0 ? context.getResources().getString(R.string.note_not_net) :
                    context.getResources().getString(R.string.note_network);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("提示");
            builder.setMessage(notesText);
            if (netConnectedStatus == 0) {
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        checkCallBack.callBack(false);
                    }
                });
            } else {
                builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        checkCallBack.callBack(true);
                        saveSetNetPlay(context);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        checkCallBack.callBack(false);
                    }
                });
            }
            AlertDialog adig = builder.create();
            adig.show();
        }
    }

    private void saveSetNetPlay(Context context) {
        if (!isSaveSettingToLocal) {
            setAnyNetIsOk = true;
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(SP_SET_NET_PLAY, 0);
        sp.edit().putBoolean(SP_SET_NET_PLAY, true).commit();
    }

    private boolean getSettingsNetPlay(Context context) {
        if (!isSaveSettingToLocal) {
            return setAnyNetIsOk;
        }
        SharedPreferences sp = context.getSharedPreferences(SP_SET_NET_PLAY, 0);
        return sp.getBoolean(SP_SET_NET_PLAY, false);
    }

    public interface CheckCallBack {
        void callBack(boolean isCouldPlay);
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return 1;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return 2;
        }
        return 0;
    }
}

package com.delong.iweixun.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.delong.factory.Factory;
import com.delong.factory.data.helper.AccountHelper;
import com.delong.factory.persistence.Account;
import com.igexin.sdk.PushConsts;

public class MessageReceiver extends BroadcastReceiver {
    private static String TAG = MessageReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null)
            return;
        Bundle bundle = intent.getExtras();

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_CLIENTID:
                onClientInit(bundle.getString("clientId"));
                Log.e(TAG,"GET_CLIENTID"+bundle.toString());
                break;
            case PushConsts.GET_MSG_DATA:
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    String message = new String(payload);
                    Log.e(TAG,"GET_MSG_DATA>>>>"+message);
                }
                break;
            default:
                Log.e(TAG,"OTHER>>>>"+bundle.toString());
                break;
        }
    }

    private void onClientInit(String cid) {
        Account.setPushId(cid);
        if (Account.isLogin()){
            AccountHelper.bindPush(null);
        }
    }


    private void onMessageArrived(String message) {
        Factory.dispatchPush(message);
    }
}

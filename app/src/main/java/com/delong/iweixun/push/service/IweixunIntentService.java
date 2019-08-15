package com.delong.iweixun.push.service;

import android.content.Context;
import android.util.Log;
import com.delong.factory.Factory;
import com.delong.factory.data.helper.AccountHelper;
import com.delong.factory.persistence.Account;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;

import java.io.UnsupportedEncodingException;

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class IweixunIntentService extends GTIntentService {

    @Override
    public void onReceiveServicePid(Context context, int i) {
        Log.e(TAG, "onReceiveServicePid -> " + "i >>>>>>>>>>>>>" + i);
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientId >>>>>>>>>>>>>>" + clientid);
        onClientInit(clientid);
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage gtTransmitMessage) {
        try {
            String msg = new String(gtTransmitMessage.getPayload(),"utf-8");
            onMessageArrived(msg);
            Log.e(TAG, "onReceiveMessageData -> " + "gtTransmitMessage >>>>>>>>>>>>>>" + new String(gtTransmitMessage.getPayload(),"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean b) {
        Log.e(TAG, "onReceiveOnlineState -> " + "b >>>>>>>>>>>>>>"+b);
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage gtCmdMessage) {
        Log.e(TAG, "onReceiveCommandResult -> " + "GTCmdMessage>>>>>>>>>>>>>>"+gtCmdMessage.toString());
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {
        Log.e(TAG, "onNotificationMessageArrived -> " + "gtNotificationMessage>>>>>>>>>>>>>>"+gtNotificationMessage.getContent());
    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage gtNotificationMessage) {
        Log.e(TAG, "onNotificationMessageClicked -> " + "GTNotificationMessage>>>>>>>>>>>>>>"+gtNotificationMessage.getContent());
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
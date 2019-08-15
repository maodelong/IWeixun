package com.delong.iweixun;

import com.delong.common.app.MyApplication;
import com.delong.factory.Factory;
import com.delong.iweixun.push.service.IweixunIntentService;
import com.delong.iweixun.push.service.IweixunPushService;
import com.igexin.sdk.PushManager;

public class App extends MyApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Factory.setup();
        PushManager.getInstance().initialize(getApplicationContext(), IweixunPushService.class);
        PushManager.getInstance().registerPushIntentService(this, IweixunIntentService.class);
    }
}

package com.delong.common.app;

import android.app.Application;
import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.widget.Toast;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.qiujuer.genius.ui.Ui;

import java.io.File;

public class MyApplication extends android.app.Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Application getInstance() {
        return instance;
    }

    public static File getCahceDirFile() {
        return instance.getCacheDir();
    }

    public static File getAudioTmpFile(boolean isTmp) {
        File dir = new File(getCahceDirFile(), "audio");
        dir.mkdirs();
        File[] files = dir.listFiles();
        if (files != null && files.length > 0)
            for (File file : files) {
                file.delete();
            }

        File path = new File(getCahceDirFile(), isTmp ? "tmp.mp3" : SystemClock.uptimeMillis() + ".mp3");
        return path.getAbsoluteFile();
    }


    public static File getPortraitTmpFile() {
        File dir = new File(getCahceDirFile(), "portarit");
        dir.mkdirs();
        File[] files = dir.listFiles();
        if (files != null && files.length > 0)
            for (File file : files) {
                file.delete();
            }

        File path = new File(dir, SystemClock.uptimeMillis() + ".jpg");
        return path;
    }


    public static void showToast(final String msg) {
        Run.onUiSync(new Action() {
            @Override

        public void call() {
                Toast.makeText(instance, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void showToast(@StringRes int msgId) {
        showToast(instance.getString(msgId));
    }
}

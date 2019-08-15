package com.delong.factory.utils;

import com.delong.common.app.MyApplication;
import com.delong.factory.net.NetWork;
import com.delong.factory.net.UploaderHelper;
import com.delong.utils.HashUtil;
import com.delong.utils.StreamUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
public class FileCache<Holder> {
    private CacheListener listener;
    private File baseDir;
    private String ext;
    private SoftReference<Holder> holderSoftReference;

    public FileCache(CacheListener listener, String baseDir, String ext) {
        this.listener = listener;
        this.baseDir = new File(MyApplication.getCahceDirFile(), baseDir);
        this.ext = ext;
    }

    public void download(Holder holder, String path) {

        final File cacheFile = buildCacheFile(path);
        if (cacheFile.exists() && cacheFile.length() > 0) {
            listener.onDownloadSucceed(holder,cacheFile);
            return;
        }

        holderSoftReference = new SoftReference<>(holder);
        OkHttpClient client = NetWork.getOkHttpClient();
        Request request = new Request.Builder()
                .url(path)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback(holder,cacheFile));

    }


    class Callback implements okhttp3.Callback {
        private final SoftReference<Holder> holderSoftReference;
        private final File file;

        public Callback(Holder holder, File file) {
            this.holderSoftReference = new SoftReference<>(holder);
            this.file = file;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            Holder holder = holderSoftReference.get();
            if (holder != null && holder == getLastHolderAndClear()) {
                FileCache.this.listener.onDownloadFailed(holder);
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            InputStream inputStream = response.body().byteStream();
            if (inputStream != null && StreamUtil.copy(inputStream, file)) {
                Holder holder = holderSoftReference.get();
                if (holder != null && holder == getLastHolderAndClear()) {
                    FileCache.this.listener.onDownloadSucceed(holder, file);
                }
            } else {
                onFailure(call, null);
            }


        }
    }

    private Holder getLastHolderAndClear() {
        if (holderSoftReference == null)
            return null;
        else {
            Holder holder = holderSoftReference.get();
            holderSoftReference.clear();
            return holder;
        }
    }

    private File buildCacheFile(String path) {
        String key = HashUtil.getMD5String(path);
        return new File(baseDir, key + "." + ext);
    }

    public interface CacheListener<Holder> {
        void onDownloadSucceed(Holder holder, File file);

        void onDownloadFailed(Holder holder);
    }
}

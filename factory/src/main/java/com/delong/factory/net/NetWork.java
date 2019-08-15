package com.delong.factory.net;

import android.text.TextUtils;
import android.util.Log;

import com.delong.common.Common;
import com.delong.factory.Factory;
import com.delong.factory.persistence.Account;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetWork implements Common.Constance {
    private static NetWork instance;
    private static OkHttpClient mClient;

    static {
        instance = new NetWork();
    }

    private NetWork() {
    }


    public static Retrofit getRetrofit() {
        instance.mClient = getOkHttpClient();
        return new Retrofit.Builder().
                client(mClient).
                baseUrl(BASE_URL).
                addConverterFactory(GsonConverterFactory.create(Factory.getGson())).
                build();
    }

    public static RemoteService remote() {
        return NetWork.getRetrofit().create(RemoteService.class);
    }

    public static OkHttpClient getOkHttpClient() {
        if (instance.mClient != null)
            return instance.mClient;

        //日志显示级别
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
        //新建log拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.e("delong", "OkHttp====Message:" + message);
            }
        });
        loggingInterceptor.setLevel(level);
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient
                .Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request.Builder builder = request.newBuilder();
                if (!TextUtils.isEmpty(Account.getToken())) {
                    builder.addHeader("token", Account.getToken());
                }
                builder.addHeader("Content-Typle", "application/json");
                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        });
        //OkHttp进行添加拦截器loggingInterceptor
        httpClientBuilder.addInterceptor(loggingInterceptor);

        return httpClientBuilder.build();
    }
}

package com.delong.factory.data;

import android.support.annotation.StringRes;

public interface DataSource {


    interface Callback<T> extends SuccessCallback<T> ,FailedCallback{

    }

    interface SuccessCallback<T> {
             void onDataLoaded(T t);
    }

    interface FailedCallback {
        void onDataNotAvailable(@StringRes int strId);
    }

    void dispose();
}

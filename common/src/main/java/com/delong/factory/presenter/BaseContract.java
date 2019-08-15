package com.delong.factory.presenter;

import android.support.annotation.StringRes;

import com.delong.common.widget.recycler.RecyclerAdapter;

public interface BaseContract{
    interface View <T>{
        void showError(@StringRes int strId);
        void showLoading();
        void setPresenter(T t);
    }

    interface  Presenter{
        void star();
        void destroy();
    }

    interface RecyclerView<T extends Presenter ,ViewModel > extends View<T> {
        RecyclerAdapter<ViewModel> getAdapter();
        void onAdapterDataChange();
    }

}

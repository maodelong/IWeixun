package com.delong.common.app;

import android.content.Context;

import com.delong.factory.presenter.BaseContract;

public  abstract class PresenterFragment<Presenter extends BaseContract.Presenter> extends Fragment implements BaseContract.View<Presenter> {
   protected Presenter mPresenter;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initPresenter();
    }

    protected abstract Presenter initPresenter();

    @Override
    public void showError(int strId) {
        if(mPlaceHolderView!=null){
            mPlaceHolderView.triggerError(strId);
        }else {
            MyApplication.showToast(strId);
        }
    }

    @Override
    public void showLoading() {
        if(mPlaceHolderView!=null){
            mPlaceHolderView.triggerLoading();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
           this.mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter!=null)
            mPresenter.destroy();
    }
}

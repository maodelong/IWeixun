package com.delong.factory.presenter;

public class BasePresenter<T extends BaseContract.View> implements BaseContract.Presenter {
    private T mView;

    public BasePresenter(T mView) {
        setmView(mView);
    }

    @Override
    public void star() {
        T view = mView;
        mView.showLoading();
    }

    public T getmView() {
        return mView;
    }

    protected void setmView(T mView) {
        this.mView = mView;
        mView.setPresenter(this);
    }

    @Override
    public void destroy() {
        T view = mView;
        mView = null;
        if (view != null) {
            view.setPresenter(null);
        }
    }
}

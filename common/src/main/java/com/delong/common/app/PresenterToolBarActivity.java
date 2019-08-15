package com.delong.common.app;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.delong.common.R;
import com.delong.factory.presenter.BaseContract;

public abstract class PresenterToolBarActivity<Presenter extends BaseContract.Presenter> extends ToolBarActivity
        implements BaseContract.View<Presenter> {
    protected Toolbar mToolbar;
    protected Presenter mPresenter;

    @Override
    protected void initBefore() {
        super.initBefore();
        initPresenter();
    }

    @Override
    public void showLoading() {
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerLoading();
        }
    }

    @Override
    public void showError(int strId) {
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerError(strId);
        } else {
            MyApplication.showToast(strId);
        }
    }

    protected void hideLoading() {
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerOk();
        }

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.mPresenter = presenter;
    }

    protected abstract Presenter initPresenter();

    @Override
    protected void initWidget() {
        super.initWidget();
        iniToolbar((Toolbar) findViewById(R.id.toolbar));
    }

    public void iniToolbar(Toolbar toolbar) {
        mToolbar = toolbar;
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        initTitleNeedBack();
    }

    protected void initTitleNeedBack() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter!=null)
        mPresenter.destroy();
    }
}

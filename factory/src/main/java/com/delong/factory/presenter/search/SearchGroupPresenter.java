package com.delong.factory.presenter.search;

import com.delong.factory.presenter.BasePresenter;

public class SearchGroupPresenter extends BasePresenter<SearchContract.GroupView> implements SearchContract.Presenter {
    public SearchGroupPresenter(SearchContract.GroupView mView) {
        super(mView);
    }

    @Override
    public void search(String content) {

    }
}

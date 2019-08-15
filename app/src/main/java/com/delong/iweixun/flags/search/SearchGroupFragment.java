package com.delong.iweixun.flags.search;

import com.delong.common.app.PresenterFragment;
import com.delong.factory.model.card.GroupCard;
import com.delong.factory.presenter.search.SearchContract;
import com.delong.factory.presenter.search.SearchGroupPresenter;
import com.delong.iweixun.R;
import com.delong.iweixun.activities.SearchActivity;
import java.util.List;

public class SearchGroupFragment extends PresenterFragment<SearchContract.Presenter> implements
        SearchActivity.SearchFragment,SearchContract.GroupView {


    public SearchGroupFragment() {
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_group;
    }


    @Override
    public void search(String content) {

    }

    @Override
    protected SearchContract.Presenter initPresenter() {
        return new SearchGroupPresenter(this);
    }

    @Override
    public void onSearchDone(List<GroupCard> userCards) {

    }
}

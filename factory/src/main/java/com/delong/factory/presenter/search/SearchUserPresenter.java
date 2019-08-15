package com.delong.factory.presenter.search;

import com.delong.factory.presenter.BasePresenter;
import com.delong.factory.data.DataSource;
import com.delong.factory.data.helper.UserHelper;
import com.delong.factory.model.card.UserCard;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import java.util.List;
import retrofit2.Call;

public class SearchUserPresenter extends BasePresenter<SearchContract.UserView>
        implements SearchContract.Presenter,
        DataSource.Callback<List<UserCard>>{
    private Call searchCall;

    public SearchUserPresenter(SearchContract.UserView mView) {
        super(mView);
    }

    @Override
    public void search(String content) {
        star();
        Call call = searchCall;
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
        searchCall = UserHelper.search(content, this);
    }


    @Override
    public void onDataLoaded(final List<UserCard> userCards) {
        final SearchContract.UserView view = getmView();
        if (view == null)
            return;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.onSearchDone(userCards);
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int strId) {
        final SearchContract.UserView view = getmView();
        if (view == null)
            return;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.showError(strId);
            }
        });
    }
}

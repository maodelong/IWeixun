package com.delong.factory.presenter.contact;

import com.delong.factory.presenter.BasePresenter;
import com.delong.factory.data.DataSource;
import com.delong.factory.data.helper.UserHelper;
import com.delong.factory.model.card.UserCard;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

public class FollowPresenter extends BasePresenter<FollowContract.View> implements FollowContract.Presenter, DataSource.Callback<UserCard> {


    public FollowPresenter(FollowContract.View mView) {
        super(mView);
    }

    @Override
    public void followUser(String id) {
        star();
        UserHelper.userFollow(id,this);
    }

    @Override
    public void onDataLoaded(final UserCard userCard) {
        final FollowContract.View view = getmView();
        if (view!=null){
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.onFollowSuccess(userCard);
                }
            });
        }
    }

    @Override
    public void onDataNotAvailable(final int strId) {
        final FollowContract.View view = getmView();
        if (view!=null){
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.showError(strId);
                }
            });
        }
    }
}

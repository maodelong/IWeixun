package com.delong.factory.presenter.contact;

import com.delong.factory.Factory;
import com.delong.factory.data.helper.UserHelper;
import com.delong.factory.model.db.User;
import com.delong.factory.persistence.Account;
import com.delong.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

@SuppressWarnings({"RedundantConditionalExpression", "ConstantConditions"})
public class PersonalPresenter extends BasePresenter<PersonalContract.View> implements
        PersonalContract.Presenter {
    private User user;
    private String userId;

    public PersonalPresenter(PersonalContract.View mView) {
        super(mView);
    }

    @Override
    public void star() {
        super.star();
        userId = getmView().getUserId();
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                final PersonalContract.View view = getmView();
                if (view != null) {
                    User user = UserHelper.searchFirstForNet(userId);
                    onLoaded(view, user);
                }
            }
        });
    }

    private void onLoaded(final PersonalContract.View view, final User user) {
        this.user = user;
        final boolean isSelf = user.getId().equalsIgnoreCase(Account.getUserId()) ? true : false;
        final boolean isFollow = isSelf || user.isFollow();
        final boolean allowSayHello = !isSelf && isFollow;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.allowSayHello(allowSayHello);
                view.setFollow(isFollow);
                view.onLoadDone(user);
            }
        });
    }

    @Override
    public User getUserPersonal() {
        return user;
    }
}

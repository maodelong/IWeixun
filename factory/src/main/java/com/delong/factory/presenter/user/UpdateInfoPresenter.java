package com.delong.factory.presenter.user;

import android.text.TextUtils;

import com.delong.factory.presenter.BasePresenter;
import com.delong.factory.Factory;
import com.delong.factory.R;
import com.delong.factory.data.DataSource;
import com.delong.factory.data.helper.UserHelper;
import com.delong.factory.model.api.user.UserUpdateModel;
import com.delong.factory.model.card.UserCard;
import com.delong.factory.model.db.User;
import com.delong.factory.net.UploaderHelper;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

public class UpdateInfoPresenter extends BasePresenter<UpdateInfoContract.view>
        implements UpdateInfoContract.presenter,
        DataSource.Callback<UserCard> {
    public UpdateInfoPresenter(UpdateInfoContract.view mView) {
        super(mView);
    }


    @Override
    public void update(final String photoFilePath,final String des,final boolean isMan) {
        star();
        final UpdateInfoContract.view view = getmView();
        if (view == null)
            return;
        if (TextUtils.isEmpty(photoFilePath) || TextUtils.isEmpty(des)) {
            view.showError(R.string.data_account_update_invalid_parameter);
        } else {
            Factory.runOnAsync(new Runnable() {
                @Override
                public void run() {
                    String netUrl = UploaderHelper.uploadPortrait(photoFilePath);
                    if (TextUtils.isEmpty(netUrl)){
                        view.showError(R.string.data_upload_error);
                    }else {
                        UserUpdateModel model = new UserUpdateModel("",netUrl,des,
                                isMan? User.SEX_MAN: User.SEX_WOMAN);
                        UserHelper.update(model,UpdateInfoPresenter.this);
                    }
                }
            });

        }
    }

    @Override
    public void onDataLoaded(UserCard userCard) {
        final UpdateInfoContract.view view = getmView();
        if (view == null)
            return;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                getmView().updateSuccess();
            }
        });

    }

    @Override
    public void onDataNotAvailable(final int strId) {
        final UpdateInfoContract.view view = getmView();
        if (view == null)
            return;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                getmView().showError(strId);
            }
        });

    }
}

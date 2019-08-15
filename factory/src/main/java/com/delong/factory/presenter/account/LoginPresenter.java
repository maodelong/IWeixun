package com.delong.factory.presenter.account;

import com.delong.common.Common;
import com.delong.factory.presenter.BasePresenter;
import com.delong.factory.R;
import com.delong.factory.data.DataSource;
import com.delong.factory.data.helper.AccountHelper;
import com.delong.factory.model.api.account.LoginModel;
import com.delong.factory.model.db.User;
import com.delong.factory.persistence.Account;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import java.util.regex.Pattern;

public   class LoginPresenter extends BasePresenter<LoginContract.View> implements
                                              LoginContract.Presenter, DataSource.Callback<User> {
    public LoginPresenter(LoginContract.View mView) {
        super(mView);
    }

    @Override
    public void login(String phone, String password) {
         star();
        LoginContract.View view = getmView();
        if (view==null)
            return;
        if (!checkMobile(phone)) {
            view.showError(R.string.data_account_register_invalid_parameter_mobile);
        }else  if(password.length()<6){
            view.showError(R.string.data_account_register_invalid_parameter_password);
        }else {
            LoginModel model = new LoginModel(phone,password,Account.getPushId());
            AccountHelper.login(model, this);
        }
    }

    @Override
    public boolean checkMobile(String phone) {
         return Pattern.matches(Common.Constance.REGEX_MOBILE,phone);
    }

    @Override
    public void onDataLoaded(User user) {
        final LoginContract.View view = getmView();
        if (view==null)
            return;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.loginSuccess();
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int strId) {
        final LoginContract.View view = getmView();
        if (view==null)
            return;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.showError(strId);
            }
        });

    }
}

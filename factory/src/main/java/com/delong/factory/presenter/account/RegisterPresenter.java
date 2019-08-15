package com.delong.factory.presenter.account;

import android.support.annotation.StringRes;
import android.text.TextUtils;
import com.delong.common.Common;
import com.delong.factory.presenter.BasePresenter;
import com.delong.factory.R;
import com.delong.factory.data.DataSource;
import com.delong.factory.data.helper.AccountHelper;
import com.delong.factory.model.api.account.RegisterModel;
import com.delong.factory.model.db.User;
import com.delong.factory.persistence.Account;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import java.util.regex.Pattern;

public class RegisterPresenter extends BasePresenter<RegisterContract.View> implements
        RegisterContract.Presenter, DataSource.Callback<User> {

    public RegisterPresenter(RegisterContract.View mView) {
        super(mView);
    }

    @Override
    public void register(String phone, String name, String password) {
        star();
        RegisterContract.View view = getmView();
        if (view==null)
            return;
        if (!checkMobile(phone)){
               view.showError(R.string.data_account_register_invalid_parameter_mobile);
        }else if(name.length()<2){
               view.showError(R.string.data_account_register_invalid_parameter_name);
        }else  if(password.length()<6){
              view.showError(R.string.data_account_register_invalid_parameter_password);
        }else {
            RegisterModel model = new RegisterModel(phone,name,password, Account.getPushId());
            AccountHelper.register(model,this);
        }
    }

    @Override
    public boolean checkMobile(String phone) {
       return !TextUtils.isEmpty(phone)
               && Pattern.matches(Common.Constance.REGEX_MOBILE,phone);
    }

    @Override
    public void onDataLoaded(User user) {
        final RegisterContract.View view = getmView();
        if (view==null)
            return;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.registerSuccess();
            }
        });
    }

    @Override
    public void onDataNotAvailable(@StringRes final int strId) {
        final RegisterContract.View view = getmView();
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

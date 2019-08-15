package com.delong.factory.presenter.account;

import com.delong.factory.presenter.BaseContract;

public interface LoginContract {

    interface View extends BaseContract.View<LoginContract.Presenter>  {
        void loginSuccess();

    }

    interface Presenter extends BaseContract.Presenter{
        void login(String phone, String password);

        boolean checkMobile(String phone);

    }

}

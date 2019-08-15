package com.delong.factory.presenter.account;

import com.delong.factory.presenter.BaseContract;

public interface RegisterContract {

    interface View extends BaseContract.View<RegisterContract.Presenter> {
        void registerSuccess();
    }

    interface Presenter extends  BaseContract.Presenter{
        void register(String phone ,String name ,String password);

        boolean checkMobile(String phone);

    }

}

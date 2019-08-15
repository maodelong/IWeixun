package com.delong.factory.presenter.user;

import com.delong.factory.presenter.BaseContract;

public interface UpdateInfoContract{

    interface presenter extends BaseContract.Presenter{
       void update(String photoFilePath ,String des,boolean isMan);
    }

    interface view extends BaseContract.View<presenter>{
        void updateSuccess();
    }

}

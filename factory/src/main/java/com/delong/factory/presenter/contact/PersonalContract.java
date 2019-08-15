package com.delong.factory.presenter.contact;

import com.delong.factory.model.db.User;
import com.delong.factory.presenter.BaseContract;

public interface PersonalContract {
    interface Presenter extends BaseContract.Presenter {
        User getUserPersonal();
    }

    interface View extends BaseContract.View<Presenter> {
          String getUserId();
          void onLoadDone(User user);
          void setFollow(boolean isFollow);
          void allowSayHello(boolean isAllow);
    }

}

package com.delong.factory.presenter.contact;

import com.delong.factory.presenter.BaseContract;
import com.delong.factory.model.card.UserCard;

/**
 * 处理添加好友View与presenter的接口契约
 */
public interface FollowContract {

    interface Presenter extends BaseContract.Presenter {
        void followUser(String id);
    }

    interface View extends  BaseContract.View<Presenter>{
       void onFollowSuccess(UserCard userCard);
    }

}

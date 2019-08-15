package com.delong.factory.presenter.search;

import com.delong.factory.presenter.BaseContract;
import com.delong.factory.model.card.GroupCard;
import com.delong.factory.model.card.UserCard;

import java.util.List;

public interface SearchContract{

    interface Presenter extends BaseContract.Presenter {
        void search(String content);
    }


    interface  UserView extends  BaseContract.View<Presenter>{
        void onSearchDone(List<UserCard> userCards);
        void refresh(UserCard UserCard);
    }

    interface  GroupView extends  BaseContract.View<Presenter>{
        void onSearchDone(List<GroupCard> userCards);
    }

}

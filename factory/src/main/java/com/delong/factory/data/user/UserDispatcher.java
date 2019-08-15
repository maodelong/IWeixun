package com.delong.factory.data.user;

import android.text.TextUtils;

import com.delong.factory.data.helper.DBHelper;
import com.delong.factory.model.card.UserCard;
import com.delong.factory.model.db.User;
import com.delong.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
public class UserDispatcher implements UserCenter{
    private static UserDispatcher instance;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private UserDispatcher() {
    }

    public static  UserDispatcher instance() {
        if (instance==null){
            synchronized (UserDispatcher.class){
                if (instance==null)
                   instance = new UserDispatcher();
            }
        }
        return instance;
    }

    @Override
    public void dispatch(UserCard... cards) {
        if (cards==null||cards.length==0)
            return;
        executor.execute(new UserCardHandler(cards));
    }



    private class  UserCardHandler implements Runnable{
        private final UserCard[] cards;

        public UserCardHandler(UserCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<User> users = new ArrayList<>();
            for (UserCard card : cards) {
                if (card==null|| TextUtils.isEmpty(card.getId()))
                    continue;
                users.add(card.build());
            }
            //进行数据库存储，并分发通知，异步操作
            DBHelper.save(User.class, CollectionUtil.toArray(users,User.class));
        }
    }
}

package com.delong.factory.data.helper;

import com.delong.factory.Factory;
import com.delong.factory.R;
import com.delong.factory.data.DataSource;
import com.delong.factory.model.api.RspModel;
import com.delong.factory.model.api.user.UserUpdateModel;
import com.delong.factory.model.card.UserCard;
import com.delong.factory.model.db.User;
import com.delong.factory.model.db.User_Table;
import com.delong.factory.net.NetWork;
import com.delong.utils.CollectionUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings({"unused"})
public class UserHelper {

    public static void update(final UserUpdateModel model, final DataSource.Callback<UserCard> callback) {
        Call<RspModel<UserCard>> call = NetWork.remote().userUpdate(model);
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    UserCard userCard = rspModel.getResult();
                    //使用用户中心分发处理存储到本地数据库并通知
                    Factory.getUserCenter().dispatch(userCard);
                    callback.onDataLoaded(userCard);
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    public static Call search(final String name, final DataSource.Callback<List<UserCard>> callback) {
        Call<RspModel<List<UserCard>>> call = NetWork.remote().searchUser(name);
        call.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<UserCard> userCardS = rspModel.getResult();
                    callback.onDataLoaded(userCardS);
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        return call;
    }

    public static void userFollow(final String followId, final DataSource.Callback<UserCard> callback) {
        Call<RspModel<UserCard>> call = NetWork.remote().userFollow(followId);
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    UserCard userCard = rspModel.getResult();
                    //使用用户中心分发处理存储到本地数据库并通知
                    Factory.getUserCenter().dispatch(userCard);
                    callback.onDataLoaded(userCard);
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    //刷新联系人的操作，不需要callback，
    // 最直接存储到本地数据库，并通过观察者通知界面
    //界面更新的时候进行对比，然后差异更新
    public static void refreshContact() {
        Call<RspModel<List<UserCard>>> call = NetWork.remote().userContacts();
        call.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<UserCard> cards = rspModel.getResult();
                    if (cards == null || cards.size() == 0)
                        return;
                    Factory.getUserCenter().dispatch(CollectionUtil.toArray(cards, UserCard.class));
                } else {
                    Factory.decodeRspCode(rspModel, null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                //nothing
            }
        });
    }

    public static User findFromLocal(String userId) {
        return SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(userId))
                .querySingle();
    }

    public static User findFromNet(String userId) {
        Call<RspModel<UserCard>> call = NetWork.remote().userFind(userId);
        UserCard card;
        try {
            Response<RspModel<UserCard>> response = call.execute();
            card = response.body().getResult();
            if (card != null) {
                User user = card.build();
                //本地数据库存储并通知
                Factory.getUserCenter().dispatch(card);
                return user;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User searchFirstForLocal(String userId) {
        User user = findFromLocal(userId);
        if (user == null) {
            return findFromNet(userId);
        }
        return user;
    }

    public static User searchFirstForNet(String userId) {
        User user = findFromNet(userId);
        if (user == null) {
            return findFromLocal(userId);
        }
        return user;
    }
}
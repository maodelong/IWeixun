package com.delong.factory.data.helper;

import android.text.TextUtils;
import com.delong.factory.Factory;
import com.delong.factory.R;
import com.delong.factory.data.DataSource;
import com.delong.factory.model.api.RspModel;
import com.delong.factory.model.api.account.AccountRsModel;
import com.delong.factory.model.api.account.LoginModel;
import com.delong.factory.model.api.account.RegisterModel;
import com.delong.factory.model.card.UserCard;
import com.delong.factory.model.db.User;
import com.delong.factory.net.NetWork;
import com.delong.factory.net.RemoteService;
import com.delong.factory.persistence.Account;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountHelper {

    public static void register(RegisterModel model, final DataSource.Callback<User> callback) {
        RemoteService service = NetWork.remote();
        Call<RspModel<AccountRsModel>> call = service.accountRegister(model);
        call.enqueue(new AccountRspCallback(callback));
    }


    public static void login(LoginModel model, final DataSource.Callback<User> callback) {
        RemoteService service = NetWork.remote();
        Call<RspModel<AccountRsModel>> call = service.accountLogin(model);
        call.enqueue(new AccountRspCallback(callback));
    }

    public static void bindPush(final DataSource.Callback<User> callback) {
        String pushId = Account.getPushId();
        if (TextUtils.isEmpty(pushId))
            return;
        RemoteService service = NetWork.remote();
        Call<RspModel<AccountRsModel>> call = service.accountBind(pushId);
        call.enqueue(new AccountRspCallback(callback));
    }


    private static class AccountRspCallback implements  Callback<RspModel<AccountRsModel>>{
        final DataSource.Callback<User> callback;
        private AccountRspCallback(DataSource.Callback<User> callback) {
            this.callback = callback;
        }

        @Override
        public void onResponse(Call<RspModel<AccountRsModel>> call, Response<RspModel<AccountRsModel>> response) {
            RspModel<AccountRsModel> rspModel = response.body();
            if (rspModel.success()) {
                AccountRsModel accountRsModel = rspModel.getResult();
                final UserCard card = accountRsModel.getUserCard();
                //本地数据库存储并通知
                DBHelper.save(User.class,card.build());
                Account.login(accountRsModel);
                if (accountRsModel.isBind()) {
                    Account.setIsBind(true);
                    if (callback!=null)
                    callback.onDataLoaded(card.build());
                } else {
                    bindPush(callback);
                }
            } else {
                Factory.decodeRspCode(rspModel, callback);
            }
        }

        @Override
        public void onFailure(Call<RspModel<AccountRsModel>> call, Throwable t) {
            t.printStackTrace();
            if (callback!=null)
            callback.onDataNotAvailable(R.string.data_network_error);
        }
    }

}

package com.delong.factory.data.user;

import android.support.annotation.NonNull;

import com.delong.factory.data.BaseRepository;
import com.delong.factory.data.DataSource;
import com.delong.factory.model.db.User;
import com.delong.factory.model.db.User_Table;
import com.delong.factory.persistence.Account;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
public class ContactRepository extends BaseRepository<User> implements ContactDataSource {
    private DataSource.SuccessCallback callback;
    private final List<User> users = new LinkedList<>();

    @Override
    public void load(DataSource.SuccessCallback<List<User>> callback) {
        super.load(callback);
        SQLite.select().from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .async()
                .queryListResultCallback(this).execute();
    }

    @Override
    protected boolean isRequired(User model) {
        return model.isFollow() && !model.getId().equals(Account.getUserId());
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<User> tResult) {
        Collections.reverse(tResult);
        super.onListQueryResult(transaction, tResult);
    }
}

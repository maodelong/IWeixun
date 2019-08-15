package com.delong.factory.data.message;

import android.support.annotation.NonNull;

import com.delong.factory.data.BaseRepository;
import com.delong.factory.model.db.Session;
import com.delong.factory.model.db.Session_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.Collections;
import java.util.List;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
public class SessionRepository extends BaseRepository<Session> implements SessionDataSource {
    @Override
    protected boolean isRequired(Session model) {
        return true;
    }

    @Override
    public void load(SuccessCallback<List<Session>> callback) {
        super.load(callback);
        SQLite.select().
                from(Session.class).
                limit(100).
                orderBy(Session_Table.modifyAt,false).
                async().
                queryListResultCallback(this).
                execute();
        ;
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Session> tResult) {
        Collections.reverse(tResult);
        super.onListQueryResult(transaction, tResult);
    }

    @Override
    public void insert(Session session) {
       models.add(0,session);
    }
}

package com.delong.factory.data.message;

import android.support.annotation.NonNull;

import com.delong.factory.data.BaseRepository;
import com.delong.factory.model.db.Message;
import com.delong.factory.model.db.Message_Table;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.Collections;
import java.util.List;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
public class MessageRepository extends BaseRepository<Message> implements MessageDataSource {
    private String receiverId;

    public MessageRepository(String receiverId) {
        super();
        this.receiverId = receiverId;
    }

    @Override
    protected boolean isRequired(Message model) {
        return (receiverId.equalsIgnoreCase(model.getSender().getId()) && model.getGroup() == null)
                || (model.getReceiver() != null
                && receiverId.equalsIgnoreCase(model.getReceiver().getId())
        );
    }

    @Override
    public void load(SuccessCallback<List<Message>> callback) {
        super.load(callback);
        SQLite.select().from(Message.class)
                .where(OperatorGroup.clause()
                        .and(Message_Table.sender_id.eq(receiverId)))
                .and(Message_Table.group_id.isNull())
                .or(Message_Table.receiver_id.eq(receiverId))
                .orderBy(Message_Table.createAt,false)
                .limit(30)
                .async()
                .queryListResultCallback(this).execute();
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
         Collections.reverse(tResult);
        super.onListQueryResult(transaction, tResult);
    }
}

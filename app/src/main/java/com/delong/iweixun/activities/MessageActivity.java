package com.delong.iweixun.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.delong.common.app.Activity;
import com.delong.common.app.Fragment;
import com.delong.factory.model.db.Group;
import com.delong.factory.model.db.Message;
import com.delong.factory.model.db.Session;
import com.delong.factory.model.db.User;
import com.delong.iweixun.R;
import com.delong.iweixun.flags.message.ChatContactFragment;
import com.delong.iweixun.flags.message.ChatGroupFragment;

public class MessageActivity extends Activity {
    public static final String KEY_RECEIVER_ID = "KEY_RECEIVER_ID";
    private static final String KEY_RECEIVER_IS_GROUP = "KEY_RECEIVER_IS_GROUP";
    private String mReceiverId;
    private boolean mIsGroup;

    public static  void show (Context context, Session session){
        if (session == null || TextUtils.isEmpty(session.getId()) || context == null)
            return;
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, session.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, session.getReceiverType()== Message.RECEIVER_TYPE_GROUP);
        context.startActivity(intent);
    }

    public static void show(Context context, User receiver) {
        if (receiver == null || TextUtils.isEmpty(receiver.getId()) || context == null)
            return;
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, receiver.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, false);
        context.startActivity(intent);
    }

    public static void show(Context context, Group group) {
        if (group == null || TextUtils.isEmpty(group.getId()) || context == null)
            return;
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, group.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, true);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mReceiverId = bundle.getString(KEY_RECEIVER_ID);
        mIsGroup = bundle.getBoolean(KEY_RECEIVER_IS_GROUP);
        return !TextUtils.isEmpty(mReceiverId);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
        Fragment fragment;
        if (mIsGroup)
            fragment = new ChatGroupFragment();
        else
            fragment = new ChatContactFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_RECEIVER_ID, mReceiverId);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().
                add(R.id.fl_chat_container, fragment).
                commit();
    }
}

package com.delong.iweixun.flags.message;

import com.delong.factory.model.db.Group;
import com.delong.factory.presenter.message.ChatContract;
import com.delong.iweixun.R;

public class ChatGroupFragment extends ChatFragment<Group> implements ChatContract.GroupView {
    public ChatGroupFragment() {
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_chat_group;
    }

    @Override
    protected ChatContract.Presenter initPresenter() {
        return null;
    }

    @Override
    public void onInit(Group group) {

    }
}

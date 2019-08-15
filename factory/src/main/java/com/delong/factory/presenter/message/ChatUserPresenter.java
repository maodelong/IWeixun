package com.delong.factory.presenter.message;

import com.delong.factory.data.helper.UserHelper;
import com.delong.factory.data.message.MessageRepository;
import com.delong.factory.model.db.Message;
import com.delong.factory.model.db.User;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
public class ChatUserPresenter extends ChatPresenter<ChatContract.UserView> implements ChatContract.Presenter {

    public ChatUserPresenter(ChatContract.UserView mView, String receiverId) {
        super(mView, new MessageRepository(receiverId), receiverId, Message.RECEIVER_TYPE_NONE);
    }

    @Override
    public void star() {
        super.star();
        // 从本地拿这个人的信息
        User receiver = UserHelper.findFromLocal(mReceiverId);
        getmView().onInit(receiver);
    }
}

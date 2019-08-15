package com.delong.factory.presenter.message;

import android.support.v7.util.DiffUtil;
import android.text.TextUtils;
import com.delong.factory.Factory;
import com.delong.factory.data.helper.MessageHelper;
import com.delong.factory.data.message.MessageDataSource;
import com.delong.factory.model.api.message.MsgCreateModel;
import com.delong.factory.model.db.Message;
import com.delong.factory.presenter.BaseSourcePresenter;
import com.delong.factory.utils.DiffUiDataCallback;
import java.util.List;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
@SuppressWarnings("WeakerAccess")
public class ChatPresenter<View extends ChatContract.View>
        extends BaseSourcePresenter<Message, Message, MessageDataSource, View>
        implements ChatContract.Presenter {
    protected String mReceiverId;
    private int mReceiverType;

    public ChatPresenter(View mView, MessageDataSource dbDataSource, String receiverId, int receiverType) {
        super(mView, dbDataSource);
        this.mReceiverId = receiverId;
        this.mReceiverType = receiverType;
    }

    @Override
    public void pushText(String content) {
        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver(mReceiverId, mReceiverType)
                .content(content, Message.TYPE_STR)
                .build();
        MessageHelper.push(model);
    }

    @Override
    public void pushAudio(String path,long time) {
     if (TextUtils.isEmpty(path))
         return;

     MsgCreateModel model = new MsgCreateModel.Builder()
             .attach(String.valueOf(time))
             .content(path,Message.TYPE_AUDIO)
             .receiver(mReceiverId,mReceiverType)
             .build();

     MessageHelper.push(model);

    }

    @Override
    public void pushImages(String[] paths) {
        for (String path : paths) {
            MsgCreateModel model = new MsgCreateModel.Builder()
                    .receiver(mReceiverId,mReceiverType)
                    .content(path,Message.TYPE_PIC)
                    .build();
            MessageHelper.push(model);
        }
    }

    @Override
    public boolean rePush(Message message) {
        return false;
    }


    @SuppressWarnings("unchecked")
    @Override
    public void onDataLoaded(final List<Message> messages) {
                ChatContract.View view = getmView();
                if (view == null)
                    return;
                List<Message> old = view.getAdapter().getItems();
                DiffUiDataCallback<Message> callback = new DiffUiDataCallback<>(old, messages);
                final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
                refreshData(result, messages);

            }
}

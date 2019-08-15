package com.delong.factory.data.helper;

import android.os.SystemClock;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.delong.common.Common;
import com.delong.common.app.MyApplication;
import com.delong.factory.Factory;
import com.delong.factory.model.api.RspModel;
import com.delong.factory.model.api.message.MsgCreateModel;
import com.delong.factory.model.card.MessageCard;
import com.delong.factory.model.db.Message;
import com.delong.factory.model.db.Message_Table;
import com.delong.factory.net.NetWork;
import com.delong.factory.net.UploaderHelper;
import com.delong.utils.PicturesCompressor;
import com.delong.utils.StreamUtil;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 作者：Maodelong4
 * 邮箱：mdl_android@163.com
 */
public class MessageHelper {
    public static Message findFromLocal(String id) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.id.eq(id))
                .querySingle();
    }

    public static void push(final MsgCreateModel model) {
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                Message message = findFromLocal(model.getId());
                if (message!=null&&message.getStatus()!=Message.STATUS_FAILED)
                    return;

              final MessageCard card = model.buildCard();
              Factory.getMessageCenter().dispatch(card);

                if (card.getType()!=Message.TYPE_STR){
                    if (!card.getContent().startsWith(UploaderHelper.ENDPOINT)){
                        String content ;
                        switch (card.getType()){
                            case Message.TYPE_PIC:
                                content = uploadImage(card.getContent());
                            break;

                            case Message.TYPE_AUDIO:
                                content = uploadAudio(card.getContent());
                                break;

                          default:
                                content = "";
                                break;
                        }
                        if (TextUtils.isEmpty(content)){
                            card.setStatus(Message.STATUS_FAILED);
                            Factory.getMessageCenter().dispatch(card);
                            return;
                        }
                        card.setContent(content);
                        Factory.getMessageCenter().dispatch(card);
                        model.refreshByCard(card);
                    }
                }
                Call<RspModel<MessageCard>> call = NetWork.remote().push(model);
                call.enqueue(new Callback<RspModel<MessageCard>>() {
                    @Override
                    public void onResponse(Call<RspModel<MessageCard>> call, Response<RspModel<MessageCard>> response) {
                        RspModel<MessageCard> rspModel = response.body();
                        if (rspModel.success()) {
                            MessageCard rspCard = rspModel.getResult();
                            if (rspCard == null)
                                return;
                            card.setStatus(Message.STATUS_DONE);
                            Factory.getMessageCenter().dispatch(card);
                        } else {
                            Factory.decodeRspCode(rspModel, null);

                        }
                    }

                    @Override
                    public void onFailure(Call<RspModel<MessageCard>> call, Throwable t) {
                        card.setStatus(Message.STATUS_FAILED);
                        Factory.getMessageCenter().dispatch(card);
                    }
                });
            }
        });
    }

    private static String uploadImage(String content) {
        File file = null;
        try {
            file = Glide.with(Factory.app()).
                    load(content).
                    downloadOnly(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL).
                    get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file!=null){
            String cacheDir = MyApplication.getCahceDirFile().getAbsolutePath();
            String tempFile = String.format("%s/image/Cache_%s.png",cacheDir, SystemClock.currentThreadTimeMillis());
            if (PicturesCompressor.compressImage(file.getAbsolutePath(),tempFile, Common.Constance.MAX_UPLOAD_IMAGE_LENGTH)) {
                String netPath = UploaderHelper.uploadImage(tempFile);
                StreamUtil.delete(tempFile);
                return netPath;
            }
        }
        return null;
    }

    private static String uploadAudio(String content) {
        File file = new File(content);
        if (!file.exists()||file.length()<=0){
           return null;
        }

        return  UploaderHelper.uplodAudio(content);
    }
    public static Message findLastWithGroup(String groupId) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.group_id.eq(groupId))
                .orderBy(Message_Table.createAt,false)
                .querySingle();
    }

    public static Message findLastWithUser(String receiverId) {
        List<Message> msg= SQLite.select().
                from(Message.class)
                .where(OperatorGroup.clause().and(Message_Table.receiver_id.eq(receiverId)).and(Message_Table.group_id.isNull()))
                .or(Message_Table.sender_id.eq(receiverId))
                .queryList();
        return msg.get(msg.size()-1);
    }
}

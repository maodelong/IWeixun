package com.delong.factory.model.api.message;

import com.delong.factory.model.card.MessageCard;
import com.delong.factory.model.db.Message;
import com.delong.factory.persistence.Account;

import java.util.Date;
import java.util.UUID;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
@SuppressWarnings("unused")
public class MsgCreateModel {
    private String id;
    // 内容不允许为空，类型为text
    private String content;

    // 附件
    private String attach;

    // 消息类型
    private int type = Message.TYPE_STR;
    // 接收者 可为空
    // 多个消息对应一个接收者
    private String receiverId;

    private int receiverType = Message.RECEIVER_TYPE_NONE;

    private MsgCreateModel() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public int getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(int receiverType) {
        this.receiverType = receiverType;
    }

    public void refreshByCard(MessageCard card) {
        if (card==null)
            return;
        this.content =card.getContent();
        this.attach =card.getAttach();

    }

    public static class Builder {
        private MsgCreateModel model;

        public Builder() {
            this.model = new MsgCreateModel();
        }

        public Builder receiver( String receiverId, int receiverType){
            model.receiverId = receiverId;
            model.receiverType = receiverType;
            return this;
        }

         public Builder content(String content ,int type){
            this.model.content = content;
            this.model.type =type;
            return this;
        }

        public Builder attach(String attach ){
            this.model.attach = attach;
            return this;
        }

        public MsgCreateModel build(){
            return this.model;
        }
    }

    private MessageCard card;
    public  MessageCard buildCard(){
        if (card==null){
            card = new MessageCard();
            card.setAttach(attach);
            card.setContent(content);
            card.setId(id);
            card.setType(type);
            card.setSenderId(Account.getUserId());
        }
        if (receiverType == Message.RECEIVER_TYPE_GROUP){
            card.setGroupId(receiverId);
        } else{
            card.setReceiverId(receiverId);
        }
        card.setStatus(Message.STATUS_CREATED);
        card.setCreateAt(new Date());
        return card;
    }
}

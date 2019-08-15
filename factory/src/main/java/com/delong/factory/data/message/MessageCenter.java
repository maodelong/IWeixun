package com.delong.factory.data.message;

import com.delong.factory.model.card.MessageCard;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
public interface MessageCenter {
    //分发处理一堆卡片的信息，并更新到数据库
    void dispatch(MessageCard...cards);
}

package com.delong.factory.data.user;

import com.delong.factory.model.card.UserCard;

/**
 * 用户中心的基本定义
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
public interface UserCenter {
    //分发处理一堆卡片的信息，并更新到数据库
    void dispatch(UserCard...cards);

}

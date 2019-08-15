package com.delong.factory.data.group;

import com.delong.factory.model.card.GroupCard;
import com.delong.factory.model.card.GroupMemberCard;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
public interface GroupCenter {
    void dispatch(GroupCard...cards);

    void dispatch(GroupMemberCard...cards);
}

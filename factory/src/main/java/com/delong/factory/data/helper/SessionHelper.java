package com.delong.factory.data.helper;

import com.delong.factory.model.db.Session;
import com.delong.factory.model.db.Session_Table;
import com.delong.factory.model.db.User_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
public class SessionHelper {

    public static Session findFromLocal(String id) {
        return SQLite.select()
                .from(Session.class)
                .where(Session_Table.id.eq(id))
                .querySingle();
    }
}

package com.delong.factory.data.helper;

import com.delong.factory.model.db.AppDataBase;
import com.delong.factory.model.db.BaseDbModel;
import com.delong.factory.model.db.Group;
import com.delong.factory.model.db.GroupMember;
import com.delong.factory.model.db.Group_Table;
import com.delong.factory.model.db.Message;
import com.delong.factory.model.db.Session;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据库的辅助工具类
 * 辅助完成：增、删、改
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
@SuppressWarnings({"unused", "JavaDoc", "unchecked", "FinalPrivateMethod", "RedundantSuppression"})
public class DBHelper {
    private static final DBHelper instance;

    static {
        instance = new DBHelper();
    }

    private DBHelper() {
    }

    public static <Model extends BaseDbModel> void save(final Class<Model> clz, final Model... models) {
        if (models == null || models.length == 0)
            return;
        //当前数据库的一个管理者
        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);
        //提交一个事物
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                ModelAdapter<Model> adapter = FlowManager.getModelAdapter(clz);
                adapter.saveAll(Arrays.asList(models));
                instance.notifySave(clz, models);
            }
        }).build().execute();
    }

    public static <Model extends BaseDbModel> void delete(final Class<Model> clz, final Model... models) {
        if (models == null || models.length == 0)
            return;
        //当前数据库的一个管理者
        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);
        //提交一个事物
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                ModelAdapter<Model> adapter = FlowManager.getModelAdapter(clz);
                adapter.deleteAll(Arrays.asList(models));
                instance.notifyDelete(clz, models);
            }
        }).build().execute();
    }


    /**
     * 进行通知
     *
     * @param clz     通知类型
     * @param models  通知的Model
     * @param <Model> 泛型 限定通知的类型必须是BaseModel
     */
    private final <Model extends BaseDbModel> void notifySave(final Class<Model> clz, final Model... models) {
        //找监听器
        final Set<ChangeListener> listeners = getListeners(clz);
        //告知监听者更新界面
        if (listeners != null && listeners.size() > 0) {
            for (ChangeListener<Model> listener : listeners) {
                listener.onDataSave(models);
            }
        }

        //列外情况
        //群成元变更，要通知对应群信息更新
        //消息变化，应该通知会话列表更新
        if (GroupMember.class.equals(clz)) {
            updateGroup((GroupMember[]) models);
        } else if (Message.class.equals(clz)) {
            updateSession((Message[]) models);
        }

    }


    /**
     * 进行通知
     *
     * @param clz     通知类型
     * @param models  通知的Model
     * @param <Model> 泛型 限定通知的类型必须是BaseModel
     */
    private final <Model extends BaseDbModel> void notifyDelete(final Class<Model> clz, final Model... models) {
        final Set<ChangeListener> listeners = getListeners(clz);
        if (listeners != null && listeners.size() > 0) {
            for (ChangeListener<Model> listener : listeners) {
                listener.onDataDelete(models);
            }
        }

        //列外情况
        //群成元变更，要通知对应群信息更新
        //消息变化，应该通知会话列表更新
        if (GroupMember.class.equals(clz)) {
            updateGroup((GroupMember[]) models);
        } else if (Message.class.equals(clz)) {
            updateSession((Message[]) models);
        }
    }


    /**
     * 从群成员中找出成员对应的群，并通知进行更新
     *
     * @param members
     */
    private void updateGroup(GroupMember... members) {
        final Set<String> groupIds = new HashSet<>();
        for (GroupMember member : members) {
            groupIds.add(member.getGroup().getId());
        }

        //异步的数据库查询
        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                List<Group> groups = SQLite.select()
                        .from(Group.class)
                        .where(Group_Table.id.in(groupIds))
                        .queryList();

                instance.notifyDelete(Group.class, groups.toArray(new Group[0]));
            }
        }).build().execute();
    }


    /**
     * 从消息列表中，筛选出对应的会话，并对会话进行更新
     *
     * @param messages Message列表
     */
    private void updateSession(Message... messages) {
        // 标示一个Session的唯一性
        final Set<Session.Identify> identifies = new HashSet<>();
        for (Message message : messages) {
            Session.Identify identify = Session.createSessionIdentify(message);
            identifies.add(identify);
        }

        // 异步的数据库查询，并异步的发起二次通知
        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                ModelAdapter<Session> adapter = FlowManager.getModelAdapter(Session.class);
                Session[] sessions = new Session[identifies.size()];

                int index = 0;
                for (Session.Identify identify : identifies) {
                    Session session = SessionHelper.findFromLocal(identify.id);

                    if (session == null) {
                        // 第一次聊天，创建一个你和对方的一个会话
                        session = new Session(identify);
                    }

                    // 把会话，刷新到当前Message的最新状态
                    session.refreshToNow();
                    // 数据存储
                    adapter.save(session);
                    // 添加到集合
                    sessions[index++] = session;
                }

                // 调用直接进行一次通知分发
                instance.notifySave(Session.class, sessions);

            }
        }).build().execute();
    }


//
//    private void updateSession(final Message... messages) {
//        final Set<Session.Identify> identifies = new HashSet<>();
//        for (Message message : messages) {
//            Session.Identify identify = Session.createSessionIdentify(message);
//            identifies.add(identify);
//        }
//        //异步的数据库查询,并异步发起第二次通知
//        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);
//        definition.beginTransactionAsync(new ITransaction() {
//            @Override
//            public void execute(DatabaseWrapper databaseWrapper) {
//                ModelAdapter<Session> adapter = FlowManager.getModelAdapter(Session.class);
//                Session[] sessions = new Session[identifies.size()];
//                int index = 0;
//                for (Session.Identify identify : identifies) {
//                    Session session = SessionHelper.findFromLocal(identify.id);
//                    if (session == null) {
//                        //第一次聊天，创建会话
//                        session = new Session(identify);
//                    }
////                    session.setMessage(messages[index]);
////                    session.setContent(messages[0].getContent());
//                    session.refreshToNow();
//                    adapter.save(session);
//                    sessions[index++] = session;
//                }
//                instance.notifySave(Session.class, sessions);
//            }
//        }).build().execute();
//
//    }

    /**
     * 观察者集合
     * Class<?>：观察者
     * Set<ChangeListener> ：每一个表对应的观察者很多
     */
    private final Map<Class<?>, Set<ChangeListener>> changeListeners = new HashMap<>();

    private <Model extends BaseModel> Set<ChangeListener> getListeners(Class<Model> modelClass) {
        if (changeListeners.containsKey(modelClass)) {
            return changeListeners.get(modelClass);
        }
        return null;
    }

    /**
     * 添加一个监听
     *
     * @param modelClass 对某个表关注
     * @param listener   监听者
     * @param <Model>    表的泛型
     */
    public static <Model extends BaseDbModel> void addChangeListener(final Class<Model> modelClass, ChangeListener<Model> listener) {
        Set<ChangeListener> changeListeners = instance.getListeners(modelClass);
        if (changeListeners == null) {
            changeListeners = new HashSet<>();
            instance.changeListeners.put(modelClass, changeListeners);
        }
        changeListeners.add(listener);
    }


    /**
     * 移除一个监听
     *
     * @param modelClass 对某个表关注
     * @param listener   监听者
     * @param <Model>    表的泛型
     */
    public static <Model extends BaseDbModel> void removeChangeListener(final Class<Model> modelClass, ChangeListener<Model> listener) {
        Set<ChangeListener> changeListeners = instance.getListeners(modelClass);
        if (changeListeners == null) {
            return;
        }
        changeListeners.remove(listener);
    }

    /**
     * 通知监听器
     */
    public interface ChangeListener<Data extends BaseDbModel> {
        void onDataSave(Data... list);

        void onDataDelete(Data... list);
    }
}

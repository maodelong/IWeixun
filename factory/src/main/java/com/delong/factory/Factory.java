package com.delong.factory;

import android.app.Application;
import android.support.annotation.StringRes;
import android.util.Log;

import com.delong.common.app.MyApplication;
import com.delong.factory.data.DataSource;
import com.delong.factory.data.group.GroupCenter;
import com.delong.factory.data.group.GroupDispatcher;
import com.delong.factory.data.message.MessageCenter;
import com.delong.factory.data.message.MessageDispatcher;
import com.delong.factory.data.user.UserCenter;
import com.delong.factory.data.user.UserDispatcher;
import com.delong.factory.model.api.PushModel;
import com.delong.factory.model.api.RspModel;
import com.delong.factory.model.card.GroupCard;
import com.delong.factory.model.card.GroupMemberCard;
import com.delong.factory.model.card.MessageCard;
import com.delong.factory.model.card.UserCard;
import com.delong.factory.persistence.Account;
import com.delong.factory.utils.DBFlowExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Factory {
    private static final Factory instance;
    private final Executor mExecutor;
    private static Gson mGson;
    private final static String TAG = Factory.class.getSimpleName();

    public static Gson getGson() {
        return mGson;
    }

    static {
        instance = new Factory();
    }

    private Factory() {
        mExecutor = Executors.newScheduledThreadPool(4);
        mGson = new GsonBuilder().
                setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").//"yyyy-MM-dd'T'HH:mm:ss.SSS"
                setExclusionStrategies(new DBFlowExclusionStrategy()).
                create();
    }

    public static Application app() {
        return MyApplication.getInstance();
    }

    public static void setup() {
        FlowManager.init(new FlowConfig.Builder(app()).openDatabasesOnInit(true).build());
        Account.load(app());
    }

    public static void runOnAsync(Runnable runnable) {
        instance.mExecutor.execute(runnable);
    }

    public static void decodeRspCode(RspModel model, DataSource.FailedCallback callback) {
        if (model == null || callback == null)
            return;
        switch (model.getCode()) {
            case RspModel.SUCCEED:
                return;
            case RspModel.ERROR_SERVICE:
                decodeRspCode(R.string.data_rsp_error_service, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_USER:
                decodeRspCode(R.string.data_rsp_error_not_found_user, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_GROUP:
                decodeRspCode(R.string.data_rsp_error_not_found_group, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_GROUP_MEMBER:
                decodeRspCode(R.string.data_rsp_error_not_found_group_member, callback);
                break;
            case RspModel.ERROR_CREATE_USER:
                decodeRspCode(R.string.data_rsp_error_create_user, callback);
                break;
            case RspModel.ERROR_CREATE_GROUP:
                decodeRspCode(R.string.data_rsp_error_create_group, callback);
                break;
            case RspModel.ERROR_CREATE_MESSAGE:
                decodeRspCode(R.string.data_rsp_error_create_message, callback);
                break;
            case RspModel.ERROR_PARAMETERS:
                decodeRspCode(R.string.data_rsp_error_parameters, callback);
                break;
            case RspModel.ERROR_PARAMETERS_EXIST_ACCOUNT:
                decodeRspCode(R.string.data_rsp_error_parameters_exist_account, callback);
                break;
            case RspModel.ERROR_PARAMETERS_EXIST_NAME:
                decodeRspCode(R.string.data_rsp_error_parameters_exist_name, callback);
                break;
            case RspModel.ERROR_ACCOUNT_TOKEN:
                MyApplication.showToast(R.string.data_rsp_error_account_token);
                instance.logout();
                break;
            case RspModel.ERROR_ACCOUNT_LOGIN:
                decodeRspCode(R.string.data_rsp_error_account_login, callback);
                break;
            case RspModel.ERROR_ACCOUNT_REGISTER:
                decodeRspCode(R.string.data_rsp_error_account_register, callback);
                break;
            case RspModel.ERROR_ACCOUNT_NO_PERMISSION:
                decodeRspCode(R.string.data_rsp_error_account_no_permission, callback);
                break;
            case RspModel.ERROR_UNKNOWN:
            default:
                decodeRspCode(R.string.data_rsp_error_unknown, callback);
                break;
        }
    }

    private void logout() {
        //TODO
    }

    private static void decodeRspCode(@StringRes final int strRes, final DataSource.FailedCallback callback) {
        callback.onDataNotAvailable(strRes);
    }

    public static void dispatchPush(String message) {

        if (!Account.isLogin())
            return;
        PushModel model = PushModel.decode(message);
        if (model == null)
            return;
        Log.e(TAG, model.toString());
        for (PushModel.Entity entity : model.getEntities()) {
            switch (entity.type) {
                case PushModel.ENTITY_TYPE_LOGOUT: {
                    instance.logout();
                    return;
                }
                case PushModel.ENTITY_TYPE_MESSAGE: {
                    MessageCard messageCard = getGson().fromJson(entity.content, MessageCard.class);
                    getMessageCenter().dispatch(messageCard);
                    break;
                }
                case PushModel.ENTITY_TYPE_ADD_FRIEND: {
                    UserCard userCard = getGson().fromJson(entity.content, UserCard.class);
                    getUserCenter().dispatch(userCard);
                    break;
                }
                case PushModel.ENTITY_TYPE_ADD_GROUP: {
                    GroupCard groupCard = getGson().fromJson(entity.content, GroupCard.class);
                    getGroupCenter().dispatch(groupCard);
                    break;
                }
                case PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS:
                case PushModel.ENTITY_TYPE_MODIFY_GROUP_MEMBERS: {
                    Type type = new TypeToken<List<GroupMemberCard>>(){}.getType();
                    List<GroupMemberCard> groupMemberCards = getGson().fromJson(entity.content,type);
                    getGroupCenter().dispatch(groupMemberCards.toArray(new GroupCard[0]));
                }
                case PushModel.ENTITY_TYPE_EXIT_GROUP_MEMBERS:{
                    //TODO
                }
            }
        }
    }

    public static UserCenter getUserCenter() {
        return UserDispatcher.instance();
    }

    public static MessageCenter getMessageCenter() {
        return MessageDispatcher.instance();
    }

    public static GroupCenter getGroupCenter() {
        return GroupDispatcher.instance();
    }
}

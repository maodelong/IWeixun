package com.delong.factory.presenter.message;

import com.delong.factory.model.db.Group;
import com.delong.factory.model.db.Message;
import com.delong.factory.model.db.User;
import com.delong.factory.presenter.BaseContract;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
public interface ChatContract {
    interface Presenter extends BaseContract.Presenter {
        void pushText(String content);
        void pushAudio(String path,long time);
        void pushImages(String[] paths);
        boolean rePush(Message message);
    }

    interface View<InitModel> extends BaseContract.RecyclerView<Presenter, Message>{
        void onInit(InitModel model);
    }

    interface UserView extends View<User>{

    }

    interface GroupView extends View<Group>{

    }

}

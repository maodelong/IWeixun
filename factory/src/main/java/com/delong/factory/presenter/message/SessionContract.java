package com.delong.factory.presenter.message;

import com.delong.factory.model.db.Session;
import com.delong.factory.presenter.BaseContract;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
public interface SessionContract {
    interface  Presenter extends BaseContract.Presenter {

    }

    interface View extends BaseContract.RecyclerView<Presenter,Session>{


    }


}

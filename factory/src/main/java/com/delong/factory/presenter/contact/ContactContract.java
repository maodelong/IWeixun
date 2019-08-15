package com.delong.factory.presenter.contact;
import com.delong.factory.model.db.User;
import com.delong.factory.presenter.BaseContract;

public interface ContactContract  {
    interface  Presenter extends BaseContract.Presenter {
    }

    interface  View extends  BaseContract.RecyclerView<Presenter,User>{
    }

}

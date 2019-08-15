package com.delong.factory.presenter.contact;

import android.support.v7.util.DiffUtil;
import com.delong.factory.data.DataSource;
import com.delong.factory.data.helper.UserHelper;
import com.delong.factory.data.user.ContactDataSource;
import com.delong.factory.data.user.ContactRepository;
import com.delong.factory.model.db.User;
import com.delong.factory.presenter.BaseSourcePresenter;
import com.delong.factory.utils.DiffUiDataCallback;
import com.delong.common.widget.recycler.RecyclerAdapter;

import java.util.List;

@SuppressWarnings("unused")
public class ContactPresenter extends
        BaseSourcePresenter<User, User,ContactDataSource, ContactContract.View>
        implements ContactContract.Presenter, DataSource.SuccessCallback<List<User>> {

    public ContactPresenter(ContactContract.View mView) {
        super(mView, new ContactRepository());
        UserHelper.refreshContact();
    }


    @Override
    public void onDataLoaded(List<User> users) {
        // 无论怎么操作，数据变更，最终都会通知到这里来
        final ContactContract.View view = getmView();
        int i = 2;
        if (view == null)
            return;

        RecyclerAdapter<User> adapter = view.getAdapter();
        List<User> old = adapter.getItems();

        // 进行数据对比
        DiffUtil.Callback callback = new DiffUiDataCallback<>(old, users);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        // 调用基类方法进行界面刷新
        refreshData(result, users);
    }

}

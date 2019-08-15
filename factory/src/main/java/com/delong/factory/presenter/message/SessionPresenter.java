package com.delong.factory.presenter.message;

import android.support.v7.util.DiffUtil;

import com.delong.factory.data.DataSource;
import com.delong.factory.data.message.SessionDataSource;
import com.delong.factory.data.message.SessionRepository;
import com.delong.factory.model.db.Session;
import com.delong.factory.presenter.BaseSourcePresenter;
import com.delong.factory.utils.DiffUiDataCallback;

import java.util.Collections;
import java.util.List;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
public class SessionPresenter extends
        BaseSourcePresenter<Session,Session, SessionDataSource,SessionContract.View>
        implements SessionContract.Presenter, DataSource.SuccessCallback<List<Session>>{

    public SessionPresenter(SessionContract.View mView) {
        super(mView, new SessionRepository());

    }

    @Override
    public void onDataLoaded(List<Session> sessions) {
        SessionContract.View view = getmView();
        if (view==null)
            return;
        Collections.reverse(sessions);
        List<Session> old = view.getAdapter().getItems();
        DiffUiDataCallback<Session> callback = new DiffUiDataCallback<>(old,sessions);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        refreshData(result,sessions);
    }
}

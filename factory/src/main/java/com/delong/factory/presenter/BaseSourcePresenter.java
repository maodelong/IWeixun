package com.delong.factory.presenter;

import com.delong.factory.data.DataSource;
import com.delong.factory.data.DbDataSource;

import java.util.List;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
public abstract class BaseSourcePresenter<Data,ViewModel,Source extends DbDataSource<Data>,View extends BaseContract.RecyclerView> extends
        BaseRecyclerPresenter<ViewModel,View> implements DataSource.SuccessCallback<List<Data>> {
    protected Source dbDataSource;

    public BaseSourcePresenter(View mView,Source dbDataSource) {
        super(mView);
        this.dbDataSource = dbDataSource;
    }

    @Override
    public void star() {
        super.star();
        dbDataSource.load(this);
    }

    @Override
    public void destroy() {
        super.destroy();
        dbDataSource.dispose();
    }
}

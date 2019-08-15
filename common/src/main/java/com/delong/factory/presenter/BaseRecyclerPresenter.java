package com.delong.factory.presenter;

import android.support.v7.util.DiffUtil;

import com.delong.common.widget.recycler.RecyclerAdapter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.Collections;
import java.util.List;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
public class BaseRecyclerPresenter<ViewModel,View extends BaseContract.RecyclerView> extends BasePresenter<View> {
    public BaseRecyclerPresenter(View mView) {
        super(mView);
    }

    protected void refreshData(final List<ViewModel> models){
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                View view = getmView();
                if (view==null)
                    return;
                RecyclerAdapter<ViewModel> adapter = view.getAdapter();
                adapter.replace(models);
                view.onAdapterDataChange();
            }
        });
    }


    protected void refreshData(final DiffUtil.DiffResult diffResult,final List<ViewModel> models){
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                refreshDataOnUiThread(diffResult,models);
            }
        });
    }

    private void refreshDataOnUiThread(final DiffUtil.DiffResult diffResult,final List<ViewModel> models){
        View view = getmView();
        if (view==null)
            return;
        RecyclerAdapter<ViewModel> adapter = view.getAdapter();
        adapter.getItems().clear();
        adapter.getItems().addAll(models);
        view.onAdapterDataChange();
        diffResult.dispatchUpdatesTo(adapter);
    }

}

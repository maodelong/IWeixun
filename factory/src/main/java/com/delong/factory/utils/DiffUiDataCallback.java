package com.delong.factory.utils;

import android.support.v7.util.DiffUtil;

import java.util.List;

public class DiffUiDataCallback<T extends DiffUiDataCallback.UiDataDiffer<T>> extends DiffUtil.Callback {
    private List<T> mOldLists ,mNewLists;


    public DiffUiDataCallback(List<T> mOldLists, List<T> mNewLists) {
        this.mOldLists = mOldLists;
        this.mNewLists = mNewLists;
    }

    @Override
    public int getOldListSize() {
        return mOldLists.size();
    }

    @Override
    public int getNewListSize() {
        return mNewLists.size();
    }

    //两个类是否是同一个东西，比如Id相等的User
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        T beanOld = mOldLists.get(oldItemPosition);
        T beanNew = mNewLists.get(newItemPosition);
        return beanNew.isSame(beanOld);
    }

    //经过相等判断后，进一步判断是否有数据更新
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        T beanOld = mOldLists.get(oldItemPosition);
        T beanNew = mNewLists.get(newItemPosition);
        return beanNew.isContentSame(beanOld);
    }


    public interface  UiDataDiffer<T>{
        boolean isSame(T old);
        boolean isContentSame(T old);
    }

}

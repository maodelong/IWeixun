package com.delong.iweixun.helper;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;

/**
 * 解决对Fragment的调度和重用问题
 * 达到最优的Fragment的切换
 */
public class NavHelper<T> {
    private SparseArray<Tab<T>> tabs = new SparseArray();
    private final Context context;
    private final FragmentManager mFragmentManager;
    private final int containerId;
    private final onTabChangeListner<T> listner;
    private Tab<T> currentTab;

    public NavHelper(Context context, FragmentManager mFragmentManager,
                     int containerId, onTabChangeListner<T> listner) {
        this.context = context;
        this.mFragmentManager = mFragmentManager;
        this.containerId = containerId;
        this.listner = listner;
    }

    public NavHelper<T> add(int menuId, Tab<T> tab) {
        tabs.put(menuId, tab);
        return this;
    }

    /**
     * 获取当前事件的实列
     *
     * @return currentTab
     */
    public Tab<T> getCurrentTab() {
        return currentTab;
    }

    /**
     * 执行从Active转接过来的Nav事件
     *
     * @param menuId 被点击的menu按钮的
     * @return true表示能处理该事件 false 表示不能处理
     */
    public boolean performClickMenu(int menuId) {
        Tab<T> tab = tabs.get(menuId);
        if (tab != null) {//表示该事件是能处理的事件
            doSelect(tab);
            return true;
        }
        return false;
    }

    /**
     * 具体处理tab事件，
     *
     * @param tab menu点击事件的封装
     */
    private void doSelect(Tab<T> tab) {
        if (currentTab != null) {
            if (currentTab == tab) {
                notifyReselect(tab);
                return;
            }
            doTabChange(tab);
            currentTab = tab;
        } else {
            currentTab = tab;
            doTabChange(tab);
        }
    }

    /**
     * Fragment的具体调度逻辑
     * @param newTab
     */
    private void doTabChange(Tab<T> newTab) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (currentTab.fragment!=null){
            transaction.detach(currentTab.fragment);
        }
        if (newTab.fragment==null){
            Fragment fragment = Fragment.instantiate(context,newTab.clx.getName());
            newTab.fragment =fragment;
            transaction.add(containerId,fragment);
        }else{
            transaction.attach(newTab.fragment);
        }
        transaction.commit();
        notifyselect(newTab,currentTab);
    }

    private void notifyselect(Tab<T> newTab, Tab<T> oldTab) {
        if (listner != null) {
            listner.onTabChanged(newTab, oldTab);
        }
    }

    private void notifyReselect(Tab<T> tab) {
        // TODo
    }

    public static class Tab<T> {
        public Class<?> clx;
        public T extra;
        Fragment fragment;

        public Tab(Class<?> clx, T extra) {
            this.clx = clx;
            this.extra = extra;
        }
    }

    /**
     * 定义事件处理完成后偶的回调接口
     *
     * @param <T>
     */
    public interface onTabChangeListner<T> {
        void onTabChanged(Tab<T> newTab, Tab<T> oldTab);
    }
}

package com.delong.iweixun.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.delong.common.app.Fragment;
import com.delong.common.app.ToolBarActivity;
import com.delong.iweixun.R;
import com.delong.iweixun.flags.search.SearchGroupFragment;
import com.delong.iweixun.flags.search.SearchUserFragment;

public class SearchActivity extends ToolBarActivity {
    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final int TYPE_USER = 1;
    public static final int TYPE_GROUP = 2;
    private int type;
    private SearchFragment mSearchFragment;

    public static void show(Context context, int type) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected boolean initArgs(Bundle Bundle) {
        type = Bundle.getInt(EXTRA_TYPE);
        return type == TYPE_USER || type == TYPE_GROUP;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        //显示对应的Fragment
        Fragment fragment;
        if (type==TYPE_USER){
            SearchUserFragment searchUserFragment = new SearchUserFragment();
            fragment = searchUserFragment;
            mSearchFragment = searchUserFragment;
        }else {
            SearchGroupFragment searchGroupFragment = new SearchGroupFragment();
            fragment = searchGroupFragment;
            mSearchFragment = searchGroupFragment;
        }
        getSupportFragmentManager().beginTransaction().add(R.id.lay_search_container,fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            //拿到搜索管理器
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            //添加监听
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //当提交了按钮的时候
                    search(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    //当文字改变的时候
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void search(String query) {
        if(mSearchFragment==null)
            return;
        mSearchFragment.search(query);
    }

    public  interface SearchFragment{
        void search(String content);
    }
}

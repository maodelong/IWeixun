package com.delong.common.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.delong.common.widget.convention.PlaceHolderView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class Activity extends AppCompatActivity {
    protected Unbinder mUnbinder;
    protected PlaceHolderView mPlaceHolderView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在界面初始化之前初始化窗口
        initWindows();

        if (initArgs(getIntent().getExtras())) {
            initBefore();
            //设置当前Activity的视图
            setContentView(getContentLayoutId());
            //初始化控件
            initWidget();
            //初始化数据
            initData();
        } else {
            finish();
        }

    }

    protected void initBefore() {

    }

    /**
     * 初始化窗口
     */
    protected void initWindows() {

    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    /**
     * 得到当前界面的资源ID
     *
     * @return 资源id
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget() {
        //绑定ButterKnife 注解框架
        mUnbinder = ButterKnife.bind(this);

    }

    /**
     * 初始化参数
     *
     * @param Bundle Bundle
     * @return boolean
     */
    @SuppressWarnings("unused")
    protected boolean initArgs(Bundle Bundle) {
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        //当界面导航返回时，finsh界面
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof com.delong.common.app.Fragment) {
                    if (((com.delong.common.app.Fragment) fragment).onBackPressed()) {
                        return;
                    }
                }
            }
        }
        super.onBackPressed();
        finish();
    }
    /**
     * 设置展占位布局
     * @param placeHolderView
     */
    protected void setPlaceHolderView(PlaceHolderView placeHolderView){
        this.mPlaceHolderView = placeHolderView;
    }

}

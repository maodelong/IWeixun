package com.delong.iweixun.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.delong.common.app.Activity;
import com.delong.factory.persistence.Account;
import com.delong.common.widget.PortraitView;
import com.delong.iweixun.R;
import com.delong.iweixun.flags.main.ActiveFragment;
import com.delong.iweixun.flags.main.ContactFragment;
import com.delong.iweixun.flags.main.GroupFragment;
import com.delong.iweixun.helper.NavHelper;
import net.qiujuer.genius.ui.Ui;
import java.util.Objects;
import butterknife.BindView;
import butterknife.OnClick;
import static com.delong.factory.persistence.Account.*;
import static com.delong.factory.persistence.Account.getUser;

public class MainActivity extends Activity implements BottomNavigationView
        .OnNavigationItemSelectedListener, NavHelper.onTabChangeListner<Integer> {
    @BindView(R.id.appbar)
    View mLayAppbar;

    @BindView(R.id.im_portrait)
    PortraitView mPortraitView;

    @BindView(R.id.txt_title)
    TextView mTitle;

    @BindView(R.id.lay_container)
    FrameLayout mFrameLayout;

    @BindView(R.id.btn_action)
    FloatingActionButton mAction;

    @BindView(R.id.navigation)
    BottomNavigationView mBottomNavigationView;

    private NavHelper<Integer> mNavHelper;


    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean initArgs(Bundle Bundle) {
        if (isComplete()){
            return super.initArgs(Bundle);
        }else{
            UserActivity.show(this);
            finish();
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initWidget() {
        super.initWidget();
        mNavHelper = new NavHelper<>(this,
                getSupportFragmentManager(), R.id.lay_container, this);
        mNavHelper.add(R.id.action_home, new NavHelper.Tab<>(ActiveFragment.class, R.string.title_home))
                .add(R.id.action_group, new NavHelper.Tab<>(GroupFragment.class, R.string.title_group))
                .add(R.id.action_contact, new NavHelper.Tab<>(ContactFragment.class, R.string.title_contact));
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        Glide.with(this)
                .load(R.drawable.bg_src_morning)
                .centerCrop()
                .into(new ViewTarget<View, GlideDrawable>(mLayAppbar) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setBackground(resource.getCurrent());
                    }
                });
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initData() {
        super.initData();
        Menu menu = mBottomNavigationView.getMenu();
        menu.performIdentifierAction(R.id.action_home, 0);
        Glide.with(this).load(getUser().getPortrait()).centerCrop().into(mPortraitView);
    }


    @OnClick(R.id.im_search)
    void onSearchMenuClick() {
        int type = Objects.equals(mNavHelper.getCurrentTab().extra,R.string.title_group)?SearchActivity.TYPE_USER
                      :SearchActivity.TYPE_USER;
        SearchActivity.show(this,type);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @OnClick(R.id.btn_action)
    void onActionMenuClick() {
        if (Objects.equals(mNavHelper.getCurrentTab().extra,R.string.title_group)){
            /* SearchActivity.show(this,SearchActivity.TYPE_GROUP); */
        }else {
            SearchActivity.show(this,SearchActivity.TYPE_USER);
        }

    }

    @OnClick(R.id.im_portrait)
    void onClickPortrait(){
         PersonalActivity.show(this, Account.getUserId());
    }


    /**
     * 当底部导航被点击时触发
     *
     * @param  menuItem menuItem
     * @return True 表示我们能处理点击事件
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return mNavHelper.performClickMenu(menuItem.getItemId());
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        mTitle.setText(newTab.extra);
        if (!mAction.isShown()) {
            mAction.setVisibility(View.VISIBLE);
        }

        float transY = 0;
        float rotation = 0;
        if (Objects.equals(newTab.extra, R.string.title_home)) {
            transY = Ui.dipToPx(getResources(), 100);
        } else {
            if (Objects.equals(newTab.extra, R.string.title_group)) {
                mAction.setImageResource(R.drawable.ic_group_add);
                rotation = -360;
            } else {
                mAction.setImageResource(R.drawable.ic_contact_add);
                rotation = 360;
            }
        }
        mAction.animate()
                .rotation(rotation)
                .translationY(transY)
                .setInterpolator(new AnticipateInterpolator(1))
                .setDuration(480)
                .start();
    }

}

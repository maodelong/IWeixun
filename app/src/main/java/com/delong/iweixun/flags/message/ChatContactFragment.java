package com.delong.iweixun.flags.message;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.delong.factory.model.db.User;
import com.delong.factory.presenter.message.ChatContract;
import com.delong.factory.presenter.message.ChatUserPresenter;
import com.delong.common.widget.PortraitView;
import com.delong.iweixun.R;
import com.delong.iweixun.activities.PersonalActivity;
import butterknife.BindView;
import butterknife.OnClick;

public class ChatContactFragment extends ChatFragment<User> implements ChatContract.UserView {
    @BindView(R.id.portrait)
    PortraitView mPortraitView;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    private MenuItem mUserInfoMenuItem;

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        Glide.with(this)
                .load(R.mipmap.default_banner_chat)
              .centerCrop().into(new ViewTarget<CollapsingToolbarLayout, GlideDrawable>(mCollapsingToolbarLayout) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                this.view.setContentScrim(resource.getCurrent());
            }
        });
        //TODO   打开界面不能在有数据时候默认收起CollapsingToolbarLayout
    }

    public ChatContactFragment() {
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_chat_contact;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        Toolbar toolbar = mToolbar;
        toolbar.inflateMenu(R.menu.chat_user);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_person) {
                    PersonalActivity.show(getContext(), mReceiverId);
                };
                return false;
            }
        });
        mUserInfoMenuItem = toolbar.getMenu().findItem(R.id.action_person);
    }

    @OnClick(R.id.portrait)
    void onPortraitClick() {
        PersonalActivity.show(getContext(), mReceiverId);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        super.onOffsetChanged(appBarLayout,verticalOffset);
        View view = mPortraitView;
        MenuItem menuItem = mUserInfoMenuItem;
        if (menuItem == null || view == null)
            return;

        if (verticalOffset == 0) {
            mPortraitView.setVisibility(View.VISIBLE);
            view.setScaleX(1);
            view.setScaleY(1);
            view.setAlpha(1);

            menuItem.setVisible(false);
            menuItem.getIcon().setAlpha(0);
        } else {
            verticalOffset = Math.abs(verticalOffset);
            final int totalScrollRange = appBarLayout.getTotalScrollRange();
            if (verticalOffset >= totalScrollRange) {
                mPortraitView.setVisibility(View.INVISIBLE);
                view.setScaleX(0);
                view.setScaleY(0);
                view.setAlpha(0);

                menuItem.setVisible(true);
                menuItem.getIcon().setAlpha(255);

            } else {
                float progress = 1 - verticalOffset / (float) totalScrollRange;
                mPortraitView.setVisibility(View.VISIBLE);
                view.setScaleX(progress);
                view.setScaleY(progress);
                view.setAlpha(progress);

                 int progress_menu = (int) ((verticalOffset / (float) totalScrollRange)*255);
                menuItem.getIcon().setAlpha(progress_menu);
            }
        }
    }

    @Override
    protected ChatContract.Presenter initPresenter() {
        return new ChatUserPresenter(this,mReceiverId);
    }

    @Override
    public void onInit(User user) {
        Glide.with(this).load(user.getPortrait()).centerCrop().into(mPortraitView);
        mCollapsingToolbarLayout.setTitle(user.getName());
    }

}

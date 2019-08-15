package com.delong.iweixun.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.delong.common.app.Activity;
import com.delong.common.app.Fragment;
import com.delong.iweixun.R;
import com.delong.iweixun.flags.account.AccountTrigger;
import com.delong.iweixun.flags.account.LoginFragment;
import com.delong.iweixun.flags.account.RegisterFragment;
import net.qiujuer.genius.ui.compat.UiCompat;
import butterknife.BindView;

public class AccountActivity extends Activity implements AccountTrigger {
    private Fragment mCurFragment;
    private LoginFragment mLoginFragment;
    private RegisterFragment mRegisterFragment;

    @BindView(R.id.im_bg)
    ImageView im_bg;


    public static void show(Context context) {
        context.startActivity(new Intent(context, AccountActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_account;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mLoginFragment = new LoginFragment();
        mCurFragment = mLoginFragment;
        getSupportFragmentManager().
                beginTransaction().
                add(R.id.lay_account_container, mCurFragment).
                commit();

        Glide.with(this)
                .load(R.drawable.bg_src_tianjin)
                .centerCrop()
                .into(new ViewTarget<ImageView, GlideDrawable>(im_bg) {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        Drawable drawable = resource.getCurrent();
                        drawable.setColorFilter(UiCompat.getColor(getResources(),R.color.colorAccent), PorterDuff.Mode.SCREEN);
                        this.view.setBackground(drawable);
                    }
                });
    }

    @Override
    public void triggerView() {
        Fragment fragment;
        if (mCurFragment == mLoginFragment) {
            if (mRegisterFragment == null) {
                mRegisterFragment = new RegisterFragment();
            }
            fragment = mRegisterFragment;
        } else {
            fragment = mLoginFragment;
        }
        mCurFragment = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.lay_account_container, mCurFragment)
                .commit();
    }
}

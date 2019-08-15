package com.delong.iweixun.activities;


import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.delong.common.app.Activity;
import com.delong.common.app.Fragment;
import com.delong.iweixun.R;
import com.delong.iweixun.flags.user.UpdateFragment;

import net.qiujuer.genius.ui.compat.UiCompat;
import butterknife.BindView;

public class UserActivity extends Activity {
    private Fragment curFragment;
    @BindView(R.id.im_bg)
    ImageView im_bg;

    public static void show(Context context){
        context.startActivity(new Intent(context , UserActivity.class));
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        curFragment = new UpdateFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.lay_user_container,curFragment).commit();

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
    protected int getContentLayoutId() {
        return R.layout.activity_user;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        curFragment.onActivityResult(requestCode, resultCode, data);
    }
}

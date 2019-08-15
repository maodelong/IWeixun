package com.delong.iweixun;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import com.delong.common.app.Activity;
import com.delong.common.app.MyApplication;
import com.delong.factory.persistence.Account;
import com.delong.iweixun.activities.AccountActivity;
import com.delong.iweixun.activities.MainActivity;
import com.delong.iweixun.issist.PermissionFragment;
import net.qiujuer.genius.res.Resource;
import net.qiujuer.genius.ui.compat.UiCompat;

public class LaunchActivity extends Activity implements PermissionFragment.OnSubmit {
    private ColorDrawable mBgDrawable;
    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }
    @SuppressWarnings("deprecation")
    @Override
    protected void initWidget() {
        super.initWidget();
         View root = findViewById(R.id.acticity_launch);
        int color = UiCompat.getColor(getResources(),R.color.colorPrimary);
        ColorDrawable drawable = new ColorDrawable(color);
        root.setBackground(drawable);
        mBgDrawable = drawable;
    }

    @Override
    protected void initData() {
        super.initData();
        startAnim(0.5f, new Runnable() {
            @Override
            public void run() {
                waitPushReceiverId();
            }
        });
    }

    /**
     * 等待个推框架设置好pushId
     */
    private void waitPushReceiverId(){
       // Log.e("waitPushReceiverId"," - -! ");
        if (Account.isLogin()){
            if (Account.isBind()){
                skip();
                return;
            }
        }else{
            if (!TextUtils.isEmpty(Account.getPushId())){
                skip();
                return;
            }
        }
        getWindow().getDecorView()
                   .postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           waitPushReceiverId();
                       }
                   },500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //skip();
    }

    /**
     * 在跳转之前需要把剩下的动画完成
     */
    private void skip() {
        startAnim(1f, new Runnable() {
            @Override
            public void run() {
                reallySkip();
            }
        });

    }

    private void reallySkip() {
        if (PermissionFragment.hasAll(this, getSupportFragmentManager(),this)){
            if (Account.isLogin()){
                MainActivity.show(this);
            }else{
                AccountActivity.show(this);
            }
            finish();
        }
    }

    @SuppressWarnings("unchecked")
    private void startAnim(float endProgress, final Runnable endCallback){
        int finalColor = Resource.Color.WHITE;
        //运算当前进度的颜色
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int endColor = (int)evaluator .evaluate(endProgress,mBgDrawable.getColor(),finalColor);
        ValueAnimator valueAnimator = ObjectAnimator.ofObject(this,property,evaluator,endColor);
        valueAnimator.setDuration(1500);
        valueAnimator.setIntValues(mBgDrawable.getColor(),endColor);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                endCallback.run();
            }
        });
        valueAnimator.start();
    }

    private Property<LaunchActivity,Object> property = new Property<LaunchActivity, Object>(Object.class,"color") {
        @Override
        public Object get(LaunchActivity object) {
            return object.mBgDrawable.getColor();
        }

        @Override
        public void set(LaunchActivity object, Object value) {
            object.mBgDrawable.setColor((Integer) value);
        }
    };

    @Override
    public void onClickSubmit(boolean requestPermIsSucceed) {
        if (requestPermIsSucceed){
            reallySkip();
        }else{
            MyApplication.showToast("请完成授权，不然无法使用哦！");
        }
    }
}

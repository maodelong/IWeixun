package com.delong.iweixun.flags.account;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import com.delong.common.app.Fragment;
import com.delong.common.app.PresenterFragment;
import com.delong.factory.presenter.account.RegisterContract;
import com.delong.factory.presenter.account.RegisterPresenter;
import com.delong.iweixun.R;
import com.delong.iweixun.activities.MainActivity;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends PresenterFragment<RegisterContract.Presenter> implements RegisterContract.View {
    private AccountTrigger mAccountTrigger;

    @BindView(R.id.et_name)
    EditText et_name;

    @BindView(R.id.et_number)
    EditText et_number;

    @BindView(R.id.et_password)
    EditText et_password;

    @BindView(R.id.btn_submit)
    Button btn_submit;

    @BindView(R.id.loading)
    Loading loading;

    @BindView(R.id.tex_go_login)
    TextView tex_go_login;

    public RegisterFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAccountTrigger = (AccountTrigger) context;
    }

    @Override
    protected RegisterContract.Presenter initPresenter() {
        return new RegisterPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_register;
    }


    @OnClick(R.id.tex_go_login)
    void onShowLoginClick(){
        mAccountTrigger.triggerView();
    }


    @OnClick(R.id.btn_submit)
    void onSubmitClick(){
        String phone = et_number.getText().toString().trim();
        String name = et_name.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        mPresenter.register(phone,name,password);
    }


    @Override
    public void showLoading() {
        super.showLoading();
        loading.start();
        btn_submit.setEnabled(false);
        et_name.setEnabled(false);
        et_number.setEnabled(false);
        et_password.setEnabled(false);
        tex_go_login.setEnabled(false);
    }

    @Override
    public void showError(int strId) {
        super.showError(strId);
        loading.stop();
        btn_submit.setEnabled(true);
        et_name.setEnabled(true);
        et_number.setEnabled(true);
        et_password.setEnabled(true);
        tex_go_login.setEnabled(true);
    }

    @Override
    public void registerSuccess() {
        mAccountTrigger.triggerView();
        //注册成功表示账户已经登陆
        //我们需要跳转到MainActivity
        MainActivity.show(getContext());
        getActivity().finish();
    }

}
